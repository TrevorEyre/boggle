package com.team1.cs410.boggle;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Game {

    private Context context;
    private Activity activity;
    private Board gameBoard;
    private Timer timer;
    private WordList dictionary;
    private WordList wordsFound;
    private int totalScore = 0;

    // Public constructor
    public Game (Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.gameBoard = new Board(context, activity);
        this.timer = new Timer((TextView)activity.findViewById(R.id.timer), (TextView) activity.findViewById(R.id.score), (TextView)activity.findViewById(R.id.score_lbl), (Button)activity.findViewById(R.id.button_submit), (Button)activity.findViewById(R.id.button_clear));
        this.dictionary = gameBoard.getWordList();
        this.wordsFound = new WordList(dictionary);
    }

    // Initialize game with a preset board
    public Game (Context context, Activity activity, char[] dice) {
        this.context = context;
        this.activity = activity;
        this.gameBoard = new Board(context, activity, dice);
        this.timer = new Timer((TextView)activity.findViewById(R.id.timer), (TextView) activity.findViewById(R.id.score), (TextView)activity.findViewById(R.id.score_lbl), (Button)activity.findViewById(R.id.button_submit), (Button)activity.findViewById(R.id.button_clear));
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

    // Start the game timer
    public void startTime () {
        timer.startTimer(gameBoard.getButtonList());
    }

    // Stop the game timer
    public void stopTime () {
        timer.stopTimer();
    }

    // Return score of submitted word
    public int score (String word) {
        int wordlength = word.length();
        int s=0;
        if(wordlength == 3 || wordlength == 4)
            s = 1;
        else if(wordlength==5)
            s = 2;
        else if(wordlength==6)
            s=3;
        else if(wordlength==7)
            s=5;
        else if(wordlength>=8)
            s=11;
        totalScore += s;
        return s;
    }

    // Return the game board layout
    public LinearLayout getBoard () {
        return gameBoard.getBoard();
    }

    // Return dice from game board
    public char[] getDice () {
        return gameBoard.getDice();
    }

    // Return selected word
    public String getSelectedWord () {
        return gameBoard.getSelectedWord();
    }
}
