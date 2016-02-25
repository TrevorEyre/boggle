package com.team1.cs410.boggle;

//import android.animation.ArgbEvaluator;
//import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Board implements View.OnTouchListener {

    // Constant properties
    private final int rowSize = 4;
    private final int colSize = 4;
    private final int boardSize = rowSize * colSize;

    // Properties
    private Context context;
    private Activity activity;
    private char[] dice;
    private LinearLayout gameBoard;
    private ArrayList<Button> buttons;
    private ArrayList<ArrayList<Integer>> neighbors;
    private int[] visited;
    private ArrayList<Button> disabledButtons;
    private ArrayList<Button> pressedButtons;
    private WordList dictionary;
    private WordList wordList;

    // Constructor
    public Board (Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        buttons = new ArrayList<>();
        disabledButtons = new ArrayList<>();
        pressedButtons = new ArrayList<>();
        dictionary = new WordList(context, R.raw.worddictionary);
        wordList = new WordList(dictionary);
        neighbors = initNeighbors();

        // Generate random board, ensuring it contains at least 5 valid words
        while (wordList.size() < 5) {
            wordList.clear();
            dice = initDice();
            gameBoard = initBoard();
            solveBoard();
        }
        printBoard();
        wordList.print();
        Log.d(getClass().getSimpleName(), "Words found: " + Integer.toString(wordList.size()));
    }

    // Initialize game board with a set of dice
    public Board (Context context, Activity activity, char[] dice) {
        this.context = context;
        this.activity = activity;
        buttons = new ArrayList<>();
        disabledButtons = new ArrayList<>();
        pressedButtons = new ArrayList<>();
        dictionary = new WordList(context, R.raw.worddictionary);
        wordList = new WordList(dictionary);
        neighbors = initNeighbors();

        this.dice = dice;
        gameBoard = initBoard();
        solveBoard();
        printBoard();
        wordList.print();
        Log.d(getClass().getSimpleName(), "Words found: " + Integer.toString(wordList.size()));
    }

    // Return currently selected word
    public String getSelectedWord () {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < pressedButtons.size(); ++i)  {
            word.append(pressedButtons.get(i).getText());
        }
        return word.toString();
    }

    // Return the game board layout
    public LinearLayout getBoard () {
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

    public ArrayList<Button> getButtonList(){return this.buttons;}

    // Handler for onTouch event of the dice on the board. Disables dice that are not adjacent to
    // touched die, and sets the correct dice colors.
    @Override
    public boolean onTouch (View view, MotionEvent event) {
        Button button;
        ArrayList<Integer> buttonNeighbors = neighbors.get(view.getId());
        TextView selectedWord = (TextView)activity.findViewById(R.id.input_word);

        // Disable clicked button
        pressedButtons.add((Button)view);
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.dice_pressed);

        // Update selected word label
        selectedWord.setTextColor(Color.WHITE);
        selectedWord.setText(getSelectedWord());

        // Reset all buttons that haven't been pressed
        for (int i = 0; i < buttons.size(); ++i) {
            button = buttons.get(i);
            if (!pressedButtons.contains(button)) {
                button.setEnabled(true);
                button.setBackgroundResource(R.drawable.dice_enabled);
            }
        }
        disabledButtons.clear();

        // Add all non-neighboring dice to disabled list
        for (int i = 0; i < boardSize; ++i) {
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
                button.setBackgroundResource(R.drawable.dice_enabled);
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
            button.setBackgroundResource(R.drawable.dice_enabled);
        }

        disabledButtons.clear();
        pressedButtons.clear();
    }

    // Kicks off board solving algorithm
    private void solveBoard () {
        for (int i = 0; i < boardSize; ++i) {
            StringBuilder word = new StringBuilder();
            visited = new int[boardSize];
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
        char[] boardArray = new char[boardSize];
        String[] d = new String[boardSize];

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

        for (int row = 0; row < rowSize; ++row) {
            for (int col = 0; col < colSize; ++col) {
                int dieIndex = row * rowSize + col;
                ArrayList<Integer> neighbor = new ArrayList<>();

                // Loop through adjacent dice and add to list of neighbors
                for (int i = Math.max(row - 1, 0); i <= Math.min(row + 1, rowSize - 1); ++i) {
                    for (int j = Math.max(col - 1, 0); j <= Math.min(col + 1, colSize - 1); ++j) {
                        int neighborIndex = i * rowSize + j;
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

    // Set up game board
    private LinearLayout initBoard () {
        LinearLayout board = new LinearLayout(context);
        board.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );
        board.setOrientation(LinearLayout.VERTICAL);

        // Create new row of buttons
        for (int row = 0; row < rowSize; ++row) {
            LinearLayout boardRow = new LinearLayout(context);
            boardRow.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // Width
                            0,                                      // Height
                            1                                       // Weight
                    )
            );
            boardRow.setOrientation(LinearLayout.HORIZONTAL);

            // Add buttons to row
            for (int col = 0; col < colSize; ++col) {
                int buttonIndex = row * rowSize + col;
                Button button = new Button(new ContextThemeWrapper(context, R.style.dice), null, R.style.dice);
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                        0,                                      // Width
                        LinearLayout.LayoutParams.MATCH_PARENT, // Height
                        1                                       // Weight
                );
                buttonParams.setMargins(2, 2, 2, 2);
                button.setLayoutParams(buttonParams);
                button.setId(buttonIndex);
                button.setText(Character.toString(dice[buttonIndex]));
                button.setOnTouchListener(this);
                buttons.add(button);
                boardRow.addView(button);
            }

            board.addView(boardRow);
        }

        return board;
    }


    // PRINT FUNCTIONS FOR DEBUGGING

    // Prints the Boggle board characters
    private void printBoard () {
        int n = 0;
        for (int i = 0; i < rowSize; ++i) {
            StringBuilder row = new StringBuilder();
            row.append(dice[n++]).append(" ");
            row.append(dice[n++]).append(" ");
            row.append(dice[n++]).append(" ");
            row.append(dice[n++]).append(" ");
            Log.d(getClass().getSimpleName(), row.toString());
        }
    }

    // Print neighbors
    private void printNeighbors () {
        for (int i = 0; i < boardSize; ++i) {
            ArrayList<Integer> n = neighbors.get(i);
            for (int j = 0; j < n.size(); ++j) {
                Log.d(getClass().getSimpleName(), Integer.toString(i) + ": " + n.get(j).toString());
            }
        }
    }
}
