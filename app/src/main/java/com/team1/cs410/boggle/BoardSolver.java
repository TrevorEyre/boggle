package com.team1.cs410.boggle;

import android.content.Context;
import android.util.Log;

public class BoardSolver {

    private int boardSize = 16;
    private int rowSize = 4;
    private Integer[] buttons;
    private char[][] board2dArray;
    private char[] boardArray;
    private RandomBoardGenerator generator;
    private Integer[][] neighbors;

    private WordList dictionary;
    private WordList wordList;
    private int[] visited;

    public BoardSolver (Context context) {
        buttons = new Integer[boardSize];
        generator = new RandomBoardGenerator();
        neighbors = new Integer[boardSize][9];
        board2dArray = generator.generateboard();
        dictionary = new WordList(context, R.raw.worddictionary);
        wordList = new WordList(dictionary);

        // Convert board2dArray to boardArray
        boardArray = new char[boardSize];
        int z = 0;
        for (int i = 0; i < rowSize; ++i) {
            for (int j = 0; j < rowSize; ++j) {
                boardArray[z++] = Character.toLowerCase(board2dArray[i][j]);
            }
        }

        // Initialize buttons array
        for (int i = 0; i < boardSize; ++i) {
            buttons[i] = i;
        }

        // Initialize neighbors
        for (int i = 0; i < boardSize; ++i) {
            int n = 0;
            for (int j = 0; j < boardSize; ++j) {
                if (i != j
                        && Math.abs(i / rowSize - j / rowSize) <= 1
                        && Math.abs(i % rowSize - j % rowSize) <= 1) {
                    neighbors[i][n++] = j;
                }
            }
            neighbors[i][n] = -1;
        }

        solveBoard();
        printBoard();
        wordList.print();
        Log.d(getClass().getSimpleName(), "Words found: " + Integer.toString(wordList.length()));
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
        word.append(boardArray[index]);
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
        for (int i = 0; neighbors[index][i] >= 0; ++i) {
            int n = neighbors[index][i];
            if (visited[n] != 1) {
                findWords(n, word);
            }
        }
        word.setLength(Math.max(word.length() - 1, 0));
        visited[index] = 0;
    }

    // Prints the Boggle board characters
    private void printBoard () {
        int n = 0;
        for (int i = 0; i < rowSize; ++i) {
            StringBuilder row = new StringBuilder();
            row.append(boardArray[n++]).append(" ");
            row.append(boardArray[n++]).append(" ");
            row.append(boardArray[n++]).append(" ");
            row.append(boardArray[n++]).append(" ");
            Log.d(getClass().getSimpleName(), row.toString());
        }
    }

    // Prints the Boggle board, masking visited tiles
    private void printBoardVisited () {
        int n = 0;
        for (int i = 0; i < rowSize; ++i) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < 4; ++j) {
                if (visited[n] == 1) {
                    row.append("X ");
                    ++n;
                } else {
                    row.append(boardArray[n++]).append(" ");
                }
            }
            Log.d(getClass().getSimpleName(), row.toString());
        }
    }

    // Print neighbors
    private void printNeighbors () {
        for (int i = 0; i < boardSize; ++i) {
            for (int n = 0; neighbors[i][n] >= 0; ++n) {
                Log.d(getClass().getSimpleName(), Integer.toString(i) + ": " + Integer.toString(neighbors[i][n]));
            }
        }
    }
}
