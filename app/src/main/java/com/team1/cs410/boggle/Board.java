package com.team1.cs410.boggle;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Board {

    // Tag for debug statements
    private static final String TAG = "Board";

    // Constant properties
    private final int ROW_SIZE = 4;
    private final int COL_SIZE = 4;
    private final int BOARD_SIZE = ROW_SIZE * COL_SIZE;

    // Member fields
    private Handler handler;
    private Context context;
    private char[] dice;
    private ArrayList<ArrayList<Integer>> neighbors;
    private int[] visited;
    private GridView gameBoard;
    private ArrayList<View> boardDice;
    private ArrayList<View> selectedDice;
    private ArrayList<View> enabledDice;
    private StringBuilder selectedWord;
    private boolean firstBoardTouch;

    private WordList dictionary;
    private WordList wordList;


    // Constructor
    public Board (Handler handler, Context context) {
        init(handler, context);

        // Generate random board, ensuring it contains at least 5 valid words
        while (wordList.size() < 5) {
            wordList.clear();
            dice = initDice();
            gameBoard = initBoard();
            solveBoard();
        }
    }

    // Constructor. Initializes game board with a set of dice
    public Board (Handler handler, Context context, char[] dice) {
        init(handler, context);

        this.dice = dice;
        gameBoard = initBoard();
        solveBoard();
    }

    // Common initialization for all constructors
    private void init (Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
        boardDice = new ArrayList<>();
        selectedDice = new ArrayList<>();
        enabledDice = new ArrayList<>();
        dictionary = new WordList(context, R.raw.worddictionary);
        wordList = new WordList(dictionary);
        neighbors = initNeighbors();
        selectedWord = new StringBuilder();
        firstBoardTouch = true;
    }

    // Return the game board layout
    public View getBoard () {
        return gameBoard;
    }

    // Return raw board dice
    public char[] getDice () {
        return dice;
    }

    // Return full list of available words for this board
    public WordList getWordList () {
        return wordList;
    }

    // Signals that word was checked and submitted by Game. Sets dice color,
    // based on result, and resets board
    public void wordSubmitted (int result) {
        View view;
        SquareImageView background;
        TextView letter;
        int backgroundResource;
        int letterColor;

        // Set color of selected dice
        if (result == Constants.SUBMIT_VALID) {
            backgroundResource = R.drawable.dice_valid;
            letterColor = Constants.COLOR_VALID_DICE;
        } else if (result == Constants.SUBMIT_INVALID) {
            backgroundResource = R.drawable.dice_invalid;
            letterColor = Constants.COLOR_INVALID_DICE;
        } else {
            backgroundResource = R.drawable.dice_already_found;
            letterColor = Constants.COLOR_ALREADY_FOUND_DICE;
        }

        for (int i = 0; i < selectedDice.size(); ++i) {
            view = selectedDice.get(i);
            background = (SquareImageView) view.findViewById(R.id.background);
            letter = (TextView) view.findViewById(R.id.letter);

            // Set up transition for dice background
            TransitionDrawable transition = new TransitionDrawable(
                    new Drawable[] {
                            context.getResources().getDrawable(backgroundResource),
                            context.getResources().getDrawable(R.drawable.dice)
                    }
            );

            // Set up transition for letter color
            final ObjectAnimator letterTransition = ObjectAnimator.ofInt(letter, "textColor", letterColor, Constants.COLOR_DICE);
            letterTransition.setDuration(1000);
            letterTransition.setEvaluator(new ArgbEvaluator());
            letterTransition.setInterpolator(new DecelerateInterpolator(2));

            background.setImageDrawable(transition);
            letter.setTextColor(letterColor);
            transition.startTransition(750);
            letterTransition.start();
        }

        // Reset dice
        enabledDice.clear();
        selectedDice.clear();
        selectedWord.setLength(0);
        for (int i = 0; i < gameBoard.getChildCount(); ++i) {
            enabledDice.add(gameBoard.getChildAt(i));
        }
    }

    // Re-enables all dice and resets their color
    public void clearSelected () {
        SquareImageView background;
        TextView letter;

        // Clear selected dice
        enabledDice.clear();
        selectedDice.clear();
        selectedWord.setLength(0);

        // Re-enable all dice, and reset colors
        for (int i = 0; i < gameBoard.getChildCount(); ++i) {
            View v = gameBoard.getChildAt(i);
            enabledDice.add(v);
            background = (SquareImageView) v.findViewById(R.id.background);
            letter = (TextView) v.findViewById(R.id.letter);
            background.setImageResource(R.drawable.dice);
            letter.setTextColor(Constants.COLOR_DICE);
        }
    }

    // Kicks off board solving algorithm
    private void solveBoard () {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            StringBuilder word = new StringBuilder();
            visited = new int[BOARD_SIZE];
            findWords(i, word);
        }
    }

    // Searches words formed by current tile and any unvisited neighboring tiles
    private void findWords (int index, StringBuilder word) {
        word.append(dice[index]);
        // Check if word is equal to or a prefix of any word in the dictionary. Return if not.
        int isPrefix = wordList.checkPrefix(word.toString());
        if (isPrefix == 0) {
            word.setLength(Math.max(word.length() - 1, 0));
            return;
        } else if (isPrefix == 2) { // Word is equal to a word in dictionary. Add to list.
            wordList.add(word.toString());
        }
        // Word is not equal, but is a prefix of another word in dictionary. Continue.
        visited[index] = 1;

        // Check unvisited neighbors of current index
        ArrayList<Integer> n = neighbors.get(index);
        for (int i = 0; i < n.size(); ++i) {
            int neighborIndex = n.get(i);
            if (visited[neighborIndex] != 1) {
                findWords(neighborIndex, word);
            }
        }
        word.setLength(Math.max(word.length() - 1, 0));
        visited[index] = 0;
    }

    // Initialize game dice with random char values
    private char[] initDice () {
        char[] boardArray = new char[BOARD_SIZE];
        String[] d = new String[BOARD_SIZE];

        // Initialize possible dice values
        d[0] = "AAEEGN";
        d[1] = "ABBJOO";
        d[2] = "ACHOPS";
        d[3] = "AFFKPS";
        d[4] = "AOOTTW";
        d[5] = "CIMOTU";
        d[6] = "DEILRX";
        d[7] = "DELRVY";
        d[8] = "DISTTY";
        d[9] = "EEGHNW";
        d[10] = "EEINSU";
        d[11] = "EHRTVW";
        d[12] = "EIOSST";
        d[13] = "ELRTTY";
        d[14] = "HIMNUQ";
        d[15] = "HLNNRZ";

        // Shuffle the array to shuffle the 'dice location'
        for (int i = d.length - 1; i > 0; i--) {
            int index = (int)(Math.random() * d.length);
            String a = d[index];
            d[index] = d[i];
            d[i] = a;
        }

        // From each die randomly choose one letter
        for (int i = 0; i < d.length; i++) {
            char[] current = d[i].toCharArray();
            int rand = (int)(Math.random() * d[i].length());
            boardArray[i] = current[rand];
        }

        return boardArray;
    }

    // Initialize list of neighbors for each slot on the board
    private ArrayList<ArrayList<Integer>> initNeighbors () {
        ArrayList<ArrayList<Integer>> n = new ArrayList<>();

        for (int row = 0; row < ROW_SIZE; ++row) {
            for (int col = 0; col < COL_SIZE; ++col) {
                int dieIndex = row * ROW_SIZE + col;
                ArrayList<Integer> neighbor = new ArrayList<>();

                // Loop through adjacent dice and add to list of neighbors
                for (int i = Math.max(row - 1, 0); i <= Math.min(row + 1, ROW_SIZE - 1); ++i) {
                    for (int j = Math.max(col - 1, 0); j <= Math.min(col + 1, COL_SIZE - 1); ++j) {
                        int neighborIndex = i * ROW_SIZE + j;
                        if (neighborIndex != dieIndex) {
                            neighbor.add(neighborIndex);
                        }
                    }
                }
                n.add(neighbor);
            }
        }

        return n;
    }

    public void disableBoard() {
        enabledDice.clear();
    }

    // Set up game board
    private GridView initBoard () {
        // Create GridView to hold the board dice
        final GridView board = new GridView(context);
        board.setLayoutParams(
                new GridView.LayoutParams(
                        GridView.LayoutParams.MATCH_PARENT,
                        GridView.LayoutParams.WRAP_CONTENT
                )
        );
        board.setNumColumns(4);
        board.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        board.setSelector(android.R.color.transparent);
        board.setVerticalSpacing(4);
        board.setHorizontalSpacing(4);

        // Add dice to board and set touch listener
        board.setAdapter(new BoardAdapter(context, dice));
        board.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = board.pointToPosition(x, y);

                // Initialize dice lists if this is first touch
                if (firstBoardTouch) {
                    for (int i = 0; i < board.getChildCount(); ++i) {
                        boardDice.add(board.getChildAt(i));
                        enabledDice.add(board.getChildAt(i));
                    }
                    firstBoardTouch = false;
                }

                if (action == MotionEvent.ACTION_UP) {
                    submitDice();
                } else if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    // Position will be -1 if touch is not on any dice
                    if (position != -1) {
                        selectDice(x, y, board.getChildAt(position));
                    }
                }

                return false;
            }
        });

        return board;
    }


    private void selectDice(int x, int y, View touchedDice) {

        SquareImageView background;
        TextView letter;
        TextView diceIndex = (TextView) touchedDice.findViewById(R.id.index);
        int index = Integer.parseInt(diceIndex.getText().toString());
        ArrayList<Integer> diceNeighbors = neighbors.get(index);

        // Check that dice is enabled, hasn't already been selected,
        // and user is touching the letter in center of dice
        if (enabledDice.contains(touchedDice)) {
            if (!selectedDice.contains(touchedDice)) {
                letter = (TextView) touchedDice.findViewById(R.id.letter);
                if (x > touchedDice.getLeft() + letter.getLeft() && x < touchedDice.getRight() + letter.getRight()
                        && y > touchedDice.getTop() + letter.getTop() && y < touchedDice.getBottom() + letter.getBottom()) {

                    // Add neighboring dice to enabled list. Add touched dice to selected list
                    enabledDice.clear();
                    for (int i = 0; i < diceNeighbors.size(); ++i) {
                        int neighborIndex = diceNeighbors.get(i);
                        enabledDice.add(boardDice.get(neighborIndex));
                    }
                    selectedDice.add(touchedDice);

                    // Set background and text color
                    background = (SquareImageView) touchedDice.findViewById(R.id.background);
                    background.setImageResource(R.drawable.dice_selected);
                    letter.setTextColor(Constants.COLOR_SELECTED_DICE);
                    selectedWord.append(letter.getText());

                    // Send message to Game Handler
                    Message msg = handler.obtainMessage(Constants.MESSAGE_SELECT_WORD);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.SELECTED_WORD, selectedWord.toString());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        }
    }

    // Submit currently selected dice. Messages handler from Game to check word
    private void submitDice() {

        Message msg = handler.obtainMessage(Constants.MESSAGE_SUBMIT_WORD);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SUBMITTED_WORD, selectedWord.toString());
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private class BoardAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public BoardAdapter(Context context, char[] boardDice) {
            inflater = LayoutInflater.from(context);

            for (int i = 0; i < boardDice.length; ++i) {
                items.add(new Item(Integer.toString(i), Character.toString(boardDice[i]), R.drawable.dice));
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            TextView diceIndex;
            TextView letter;
            ImageView background;

            if (v == null) {
                v = inflater.inflate(R.layout.dice, viewGroup, false);
                v.setTag(R.id.index, v.findViewById(R.id.index));
                v.setTag(R.id.letter, v.findViewById(R.id.letter));
                v.setTag(R.id.background, v.findViewById(R.id.background));
            }

            diceIndex = (TextView) v.getTag(R.id.index);
            letter = (TextView) v.getTag(R.id.letter);
            background = (ImageView) v.getTag(R.id.background);
            Item item = (Item) getItem(i);
            diceIndex.setText(item.index);
            letter.setText(item.letter);
            background.setImageResource(item.drawableId);

            return v;
        }

        private class Item {
            final String index;
            final String letter;
            final int drawableId;

            Item(String index, String letter, int drawableId) {
                this.index = index;
                this.letter = letter;
                this.drawableId = drawableId;
            }
        }
    }


    // PRINT FUNCTIONS FOR DEBUGGING

    // Prints the Boggle board characters
    private void printBoard () {
        int n = 0;
        for (int i = 0; i < ROW_SIZE; ++i) {
            StringBuilder row = new StringBuilder();
            row.append(dice[n++]).append(" ");
            row.append(dice[n++]).append(" ");
            row.append(dice[n++]).append(" ");
            row.append(dice[n++]).append(" ");
            Log.d(TAG, row.toString());
        }
    }

    // Print neighbors
    private void printNeighbors () {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            ArrayList<Integer> n = neighbors.get(i);
            for (int j = 0; j < n.size(); ++j) {
                Log.d(TAG, Integer.toString(i) + ": " + n.get(j).toString());
            }
        }
    }
}
