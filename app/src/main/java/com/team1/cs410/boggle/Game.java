package com.team1.cs410.boggle;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

public class Game {

    private Context context;
    private Activity activity;
    private Board gameBoard;
    private WordList dictionary;
    private WordList wordsFound;
    private int totalScore = 0;

    // Public constructor
    public Game (Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.gameBoard = new Board(context, activity);
        this.dictionary = gameBoard.getWordList();
        this.wordsFound = new WordList(dictionary);
    }

    // Try to submit a word from game board. Returns the score for that word.
    public int submitWord () {
        int s;
        String word = gameBoard.getSelectedWord();
        boolean isValid = wordsFound.add(word);
        gameBoard.submitWord(isValid);

        // Get score of submitted word
        if (isValid) {
            s = score(word);
        } else {
            s = 0;
        }

        return s;
    }

    // Clear the currently selected dice on the board
    public void clearSelected () {
        gameBoard.clearSelected();
    }

    //  Return total score for this game
    public int getScore () {
        return totalScore;
    }

    // Return score of submitted word
    private int score (String word) {
        int s = 1;

        totalScore += s;
        return s;
    }

    // Return the game board layout
    public LinearLayout getBoard () {
        return gameBoard.getBoard();
    }
}
