package com.team1.cs410.boggle;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class WordList {

    private TreeMap<String, String> wordList;
    private WordList dictionary;

    // Constructor - Empty WordList
    public WordList () {
        this.wordList = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        this.dictionary = null;
    }

    // Constructor - Initialize WordList with dictionary to check against
    public WordList (WordList dictionary) {
        this.wordList = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        this.dictionary = dictionary;
    }

    // Constructor - Initialize WordList from txt file
    public WordList (Context context, int txtResourceId) {
        this.wordList = this.readFromTxtFile(context, txtResourceId);
        this.dictionary = null;
    }

    // Adds a new word to the WordList. Returns true upon successful add, false otherwise. Checks
    // that word is valid length, has not already been added to the WordList, and exists in dictionary.
    public boolean add (String wordToAdd) {
        wordToAdd = wordToAdd.toLowerCase();
        if (wordToAdd.length() < 3 || wordToAdd.length() > 16) {return false;}
        if (check(wordToAdd)) {return false;}
        if (dictionary != null) {
            if (!dictionary.check(wordToAdd)) {return false;}
        }

        this.wordList.put(wordToAdd, wordToAdd);
        return true;
    }

    // Search tree for wordToCheck, returning true if found, false otherwise
    public boolean check (String wordToCheck) {
        wordToCheck = wordToCheck.toLowerCase();
        return this.wordList.containsKey(wordToCheck);
    }

    // Return length of wordList
    public int length () {
        return this.wordList.size();
    }

    // Check if argument is prefix of any key in dictionary
    public int checkPrefix (String prefix) {
        return this.dictionary.isPrefix(prefix);
    }

    // Check if argument is prefix of any key in wordList. Returns 0 for false, 1 if argument is a
    // prefix, or 2 if argument is equal to a key in wordList.
    private int isPrefix (String prefix) {
        prefix = prefix.toLowerCase();
        SortedMap tail = this.wordList.tailMap(prefix);
        if (tail.isEmpty()) {
            return 0;
        } else {
            String firstKey = tail.firstKey().toString().toLowerCase();
            if (firstKey.equals(prefix)) {
                return 2;
            } else if (firstKey.startsWith(prefix)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    // Iterate through tree and print all values to console
    public void print () {
        Iterator i = this.wordList.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry mapEntry = (Map.Entry)i.next();
            Log.d(getClass().getSimpleName(), (String)mapEntry.getValue());
        }
    }

    // Read raw txt file and add all entries to tree. Only words of 3 or more letters are added.
    private TreeMap readFromTxtFile (Context context, int txtResourceId) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        InputStream inputStream = context.getResources().openRawResource(txtResourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 3) {
                    treeMap.put(line, line);
                }
            }
        } catch (IOException e) {
            return null;
        }

        return treeMap;
    }
}
