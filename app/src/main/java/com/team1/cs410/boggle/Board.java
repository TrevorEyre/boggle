package com.team1.cs410.boggle;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Board implements View.OnTouchListener {

    // Tag for debug statements
    private static final String TAG = "Board";

    // Constant properties
    private final int ROW_SIZE = 4;
    private final int COL_SIZE = 4;
    private final int BOARD_SIZE = ROW_SIZE * COL_SIZE;
    private final int DICE_COLOR = Color.rgb(102, 102, 102);
    private final int SELECTED_DICE_COLOR = Color.rgb(3, 169, 244);
    private final int VALID_DICE_COLOR = Color.rgb(76, 175, 80);
    private final int INVALID_DICE_COLOR = Color.rgb(244, 67, 54);


    // Member fields
    private Context context;
    private Activity activity;
    private char[] dice;
    private View gameBoard;
    private ArrayList<Button> buttons;
    private ArrayList<ArrayList<Integer>> neighbors;
    private int[] visited;
    private ArrayList<Button> disabledButtons;
    private ArrayList<Button> pressedButtons;

    private ArrayList<View> boardDice;
    private ArrayList<View> selectedDice;
    private ArrayList<View> enabledDice;
    private StringBuilder selectedWord;
    private boolean firstBoardTouch;

    private WordList dictionary;
    private WordList wordList;

    // Constructor
    public Board (Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        buttons = new ArrayList<>();
        disabledButtons = new ArrayList<>();
        pressedButtons = new ArrayList<>();

        boardDice = new ArrayList<>();
        selectedDice = new ArrayList<>();
        enabledDice = new ArrayList<>();

        dictionary = new WordList(context, R.raw.worddictionary);
        wordList = new WordList(dictionary);
        neighbors = initNeighbors();
        selectedWord = new StringBuilder();
        firstBoardTouch = true;

        // Generate random board, ensuring it contains at least 5 valid words
        while (wordList.size() < 5) {
            wordList.clear();
            dice = initDice();
            gameBoard = initBoard();
            solveBoard();
        }
    }

    // Initialize game board with a set of dice
    public Board (Context context, Activity activity, char[] dice) {
        this.context = context;
        this.activity = activity;
        buttons = new ArrayList<>();
        disabledButtons = new ArrayList<>();
        pressedButtons = new ArrayList<>();

        boardDice = new ArrayList<>();
        selectedDice = new ArrayList<>();
        enabledDice = new ArrayList<>();

        dictionary = new WordList(context, R.raw.worddictionary);
        wordList = new WordList(dictionary);
        neighbors = initNeighbors();
        selectedWord = new StringBuilder();
        firstBoardTouch = true;

        this.dice = dice;
        gameBoard = initBoard();
        solveBoard();
        printBoard();
        wordList.print();
        Log.d(TAG, "Words found: " + Integer.toString(wordList.size()));
    }

    // Return currently selected word
    public String getSelectedWord () {
//        StringBuilder word = new StringBuilder();
//        for (int i = 0; i < pressedButtons.size(); ++i)  {
//            word.append(pressedButtons.get(i).getText());
//        }
//        return word.toString();
        return selectedWord.toString();
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

    public ArrayList<Button> getButtonList() {
        return this.buttons;
    }

    // Submit a word on the board. Resets all dice back to enabled, and sets the color of dice that
    // were submitted, based on whether they formed a valid word or not.
    public void submitWord (boolean isValid) {
        Button button;

        // Update selected word label
        TextView selectedWord = (TextView)activity.findViewById(R.id.input_word);
        selectedWord.setText(getSelectedWord());

        // Re-enable all dice
        for (int i = 0; i < buttons.size(); ++i) {
            button = buttons.get(i);
            button.setEnabled(true);
            if (!pressedButtons.contains(button)) {
//                button.setBackgroundResource(R.drawable.dice_enabled);
            }
        }

        // Set background color of submitted dice
        int backgroundResource = (isValid) ? R.drawable.dice_valid : R.drawable.dice_invalid;
        for (int i = 0; i < pressedButtons.size(); ++i) {
            button = pressedButtons.get(i);
            button.setBackgroundResource(backgroundResource);
//            ObjectAnimator colorFade = ObjectAnimator.ofInt(button, "backgroundResource", backgroundResource, R.drawable.dice_enabled);
//            colorFade.setDuration(1000);
//            colorFade.start();
        }

        disabledButtons.clear();
        pressedButtons.clear();
    }

    // Re-enables all dice and resets their background color
    public void clearSelected () {
        Button button;
        for (int i = 0; i < buttons.size(); ++i) {
            button = buttons.get(i);
            button.setEnabled(true);
//            button.setBackgroundResource(R.drawable.dice_enabled);
        }

        disabledButtons.clear();
        pressedButtons.clear();
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

    public void disableboard()
    {
        Button button;
        for(int i=0;i<buttons.size();i++)
        {
            button = buttons.get(i);
            button.setEnabled(false);
        }
    }

    // Set up game board
//    private LinearLayout initBoard () {
//        LinearLayout board = new LinearLayout(context);
//        board.setLayoutParams(
//                new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT
//                )
//        );
//        board.setOrientation(LinearLayout.VERTICAL);
//
//        // Create new row of buttons
//        for (int row = 0; row < ROW_SIZE; ++row) {
//            LinearLayout boardRow = new LinearLayout(context);
//            boardRow.setLayoutParams(
//                    new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, // Width
//                            0,                                      // Height
//                            1                                       // Weight
//                    )
//            );
//            boardRow.setOrientation(LinearLayout.HORIZONTAL);
//
//            // Add buttons to row
//            for (int col = 0; col < COL_SIZE; ++col) {
//                int buttonIndex = row * ROW_SIZE + col;
//                Button button = new Button(new ContextThemeWrapper(context, R.style.dice), null, R.style.dice);
//                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
//                        0,                                      // Width
//                        LinearLayout.LayoutParams.MATCH_PARENT, // Height
//                        1                                       // Weight
//                );
//                buttonParams.setMargins(2, 2, 2, 2);
//                button.setLayoutParams(buttonParams);
//                button.setId(buttonIndex);
//                button.setText(Character.toString(dice[buttonIndex]));
////                button.setOnTouchListener(this);
//                buttons.add(button);
//                boardRow.addView(button);
//            }
//
//            board.addView(boardRow);
//        }
//
//        return board;
//    }

    // Set up game board
    private View initBoard () {
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

        // Add dice to board
        board.setAdapter(new BoardAdapter(context, dice));
//        for (int i = 0; i < board.getCount(); ++i) {
//            boardDice.add(board.getChildAt(i));
//            enabledDice.add(board.getChildAt(i));
//        }
//        Log.d(TAG, "getChildCount - " + board.getCount());
//        Log.d(TAG, "boardDiceCount - " + boardDice.size());
//        Log.d(TAG, "enabledDiceCount - " + enabledDice.size());
//        for (int i = 0; i < boardDice.size(); ++i) {
//            Log.d(TAG, boardDice.get(i).toString());
//        }


        // Set board touch listener
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
        Log.d(TAG, "selectDice() - " + touchedDice.toString());

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
                    letter.setTextColor(SELECTED_DICE_COLOR);
                    selectedWord.append(letter.getText());
                }
            }
        }
    }

    private void submitDice() {
        Log.d(TAG, "submitDice()");

        SquareImageView background;
        TextView letter;

        GridView board = (GridView) gameBoard;
        enabledDice.clear();
        selectedDice.clear();
        for (int i = 0; i < board.getChildCount(); ++i) {
            View v = board.getChildAt(i);
            enabledDice.add(v);
            background = (SquareImageView) v.findViewById(R.id.background);
            letter = (TextView) v.findViewById(R.id.letter);
            background.setImageResource(R.drawable.dice);
            letter.setTextColor(DICE_COLOR);

        }
    }


    // Handler for onTouch event of the dice on the board. Disables dice that are not adjacent to
    // touched die, and sets the correct dice colors.
    @Override
    public boolean onTouch (View view, MotionEvent event) {
        Button button;
        ArrayList<Integer> buttonNeighbors = neighbors.get(view.getId());
        TextView selectedWord = (TextView)activity.findViewById(R.id.input_word);

        // Disable clicked button
        pressedButtons.add((Button) view);
        view.setEnabled(false);
//        view.setBackgroundResource(R.drawable.dice_pressed);

        // Update selected word label
        selectedWord.setTextColor(Color.WHITE);
        selectedWord.setText(getSelectedWord());

        // Reset all buttons that haven't been pressed
        for (int i = 0; i < buttons.size(); ++i) {
            button = buttons.get(i);
            if (!pressedButtons.contains(button)) {
                button.setEnabled(true);
//                button.setBackgroundResource(R.drawable.dice_enabled);
            }
        }
        disabledButtons.clear();

        // Add all non-neighboring dice to disabled list
        for (int i = 0; i < BOARD_SIZE; ++i) {
            if (!buttonNeighbors.contains(i)) {
                button = buttons.get(i);
                //button.setBackgroundResource(R.drawable.dice_disabled);
                button.setEnabled(false);
                //button.setBackgroundResource(R.drawable.dice_disabled);
                disabledButtons.add(button);
            }
        }

//        return activity.onTouchEvent(event);
        return true;
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
