package com.team1.cs410.boggle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.util.Log;
import android.widget.TextView;


public class Game {

    // Tag for debug statements
    private static final String TAG = "Game";

    private Handler activityHandler;
    private Board gameBoard;
    private Timer timer;
    private WordList dictionary;
    private WordList wordsFound;    // Words found by you and opponent
    private WordList youWordsFound; // Words found by you
    private int totalScore = 0;
    private int roundcount = 1;


    // Public constructor
    public Game (Context context, Activity activity, Handler handler) {
        this.activityHandler = handler;
        this.gameBoard = new Board(boardHandler, context);
        this.timer = new Timer(handler, (TextView)activity.findViewById(R.id.timer));
        this.dictionary = gameBoard.getWordList();
        this.wordsFound = new WordList(dictionary);
        this.youWordsFound = new WordList(dictionary);
        this.roundcount = 1;
    }

    // Initialize game with a preset board
    public Game (Context context, Activity activity, Handler handler, char[] dice) {
        this.activityHandler = handler;
        this.gameBoard = new Board(boardHandler, context, dice);
        this.timer = new Timer(handler, (TextView)activity.findViewById(R.id.timer));
        this.dictionary = gameBoard.getWordList();
        this.wordsFound = new WordList(dictionary);
        this.youWordsFound = new WordList(dictionary);
        this.roundcount = 1;
    }

    // Constructor for multi round two player
    public Game (Context context, Activity activity, Handler handler, long timervalue, int roundcount) {
        this.activityHandler = handler;
        this.gameBoard = new Board(boardHandler, context);
        //this.timer = new Timer(handler, (TextView)activity.findViewById(R.id.timer));
        Log.d("Sending to timer ","Timer: " + timervalue + " Count: " + roundcount);
        this.timer = new Timer(handler, (TextView)activity.findViewById(R.id.timer),(TextView)activity.findViewById(R.id.absolutetimervalue),timervalue,true);
        this.dictionary = gameBoard.getWordList();
        this.wordsFound = new WordList(dictionary);
        this.youWordsFound = new WordList(dictionary);
        this.roundcount = roundcount;
        TextView roundview = (TextView)activity.findViewById(R.id.round);
        roundview.setText(String.valueOf(roundcount));
    }

    // Constructor for multi round two player with preset board
    public Game (Context context, Activity activity, Handler handler, char[] dice, long timervalue, int roundcount) {
        this.activityHandler = handler;
        this.gameBoard = new Board(boardHandler, context, dice);
        Log.d("Sending to timer ","Timer: " + timervalue + " Count: " + roundcount);
        this.timer = new Timer(handler, (TextView)activity.findViewById(R.id.timer),(TextView)activity.findViewById(R.id.absolutetimervalue),timervalue,true);
        this.dictionary = gameBoard.getWordList();
        this.wordsFound = new WordList(dictionary);
        this.youWordsFound = new WordList(dictionary);
        this.roundcount = roundcount;
        TextView roundview = (TextView)activity.findViewById(R.id.round);
        roundview.setText(String.valueOf(roundcount));
    }

    // Try to submit a word from game board. Returns the score for that word.
    public int submitWord (String word) {
        int result = wordsFound.add(word);
        if (result == Constants.SUBMIT_VALID) {
            youWordsFound.add(word);
        }
        gameBoard.wordSubmitted(result);

        // Get score of submitted word
        if (result == Constants.SUBMIT_VALID) {
            totalScore += score(word);
        }

        return result;
    }

    // Add a word to list of found words, without incrementing score
    public boolean addWord (String word) {
        int result = wordsFound.add(word);
        wordsFound.print();
        return (result == Constants.SUBMIT_VALID);
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
        timer.startTimer();
    }

    // Stop the game timer
    public void stopTime () {
        timer.stopTimer();
    }

    // Return score of submitted word
    public int score (String word) {
        int wordlength = word.length();
        int s = 0;
        if (wordlength == 3 || wordlength == 4)
            s = 1;
        else if (wordlength == 5)
            s = 2;
        else if (wordlength == 6)
            s = 3;
        else if (wordlength == 7)
            s = 5;
        else if (wordlength >= 8)
            s = 11;
        return s;
    }

    // Return score of an array of words
    public int score (String[] words) {
        int s = 0;
        for (int i = 0; i < words.length; ++i) {
            s += score(words[i]);
        }
        return s;
    }

    // Return all words found in this game
    public String getWordsFound () {
        return youWordsFound.toString();
    }

    // Return all words that weren't found in this game
    public String getWordsNotFound () {
        WordList wordsNotFound = dictionary.remove(wordsFound);
        return wordsNotFound.toString();
    }

    // Return all words that weren't found in this game, also removing opponent's words
    public String getWordsNotFound (String[] words) {
        WordList tmp = dictionary.remove(wordsFound);
        WordList wordsNotFound = tmp.remove(words);
        return wordsNotFound.toString();
    }

    public int getWordCount(){
        return youWordsFound.size();
    }

    // Return the game board layout
    public View getBoard () {
        return gameBoard.getBoard();
    }

    public void disablebuttons(){
        gameBoard.disableBoard();
    }

    // Return dice from game board
    public char[] getDice () {
        return gameBoard.getDice();
    }


    // The Handler that receives messages back from the board
    private final Handler boardHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            Message message;

            switch (msg.what) {

                // User selected new dice. Signal activityHandler
                case Constants.MESSAGE_SELECT_WORD:
                    Log.d(TAG, "boardHandler - MESSAGE_SELECTED_WORD");
                    String selectedWord = msg.getData().getString(Constants.SELECTED_WORD);
                    message = activityHandler.obtainMessage(Constants.MESSAGE_SELECT_WORD);
                    bundle = new Bundle();
                    bundle.putString(Constants.SELECTED_WORD, selectedWord);
                    message.setData(bundle);
                    activityHandler.sendMessage(message);
                    break;

                // User submitted a word. Check word, and send result to activityHandler
                case Constants.MESSAGE_SUBMIT_WORD:
                    Log.d(TAG, "boardHandler - MESSAGE_SUBMIT_WORD");
                    String submittedWord = msg.getData().getString(Constants.SUBMITTED_WORD);
                    int result = submitWord(submittedWord);

                    message = activityHandler.obtainMessage(Constants.MESSAGE_SUBMIT_WORD);
                    bundle = new Bundle();
                    bundle.putString(Constants.SUBMITTED_WORD, submittedWord);
                    bundle.putInt(Constants.SUBMIT_RESULT, result);
                    message.setData(bundle);
                    activityHandler.sendMessage(message);
                    break;
            }
        }
    };

}
