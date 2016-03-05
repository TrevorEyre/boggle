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

    // Constructor - Initialize empty WordList
    public WordList () {
        this.wordList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.dictionary = null;
    }

    // Constructor - Initialize WordList with dictionary to check against
    public WordList (WordList dictionary) {
        this.wordList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.dictionary = dictionary;
    }

    // Constructor - Initialize WordList from txt file
    public WordList (Context context, int txtResourceId) {
        this.wordList = this.readFromTxtFile(context, txtResourceId);
        this.dictionary = null;
    }

    // Adds a new word to the WordList. Returns submit result as int. Checks that word
    // is valid length, has not already been added to the WordList, and exists in dictionary.
    public int add (String word) {
        word = word.toLowerCase();
        if (word.length() < 3 || word.length() > 16) {return Constants.SUBMIT_INVALID;}
        if (check(word)) {return Constants.SUBMIT_ALREADY_FOUND;}
        if (dictionary != null) {
            if (!dictionary.check(word)) {return Constants.SUBMIT_INVALID;}
        }

        this.wordList.put(word, word);
        return Constants.SUBMIT_VALID;
    }

    // Returns WordList minus all words in listToRemove. Original WordList is unaltered
    public WordList remove (WordList listToRemove) {
        WordList returnList = new WordList();
        returnList.wordList = (TreeMap)this.wordList.clone();
        Iterator i = listToRemove.wordList.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry mapEntry = (Map.Entry)i.next();
            returnList.wordList.remove(mapEntry.getKey());
        }
        return returnList;
    }

    public WordList remove (String[] arryToRemove) {
        WordList returnList = new WordList();
        returnList.wordList = (TreeMap)this.wordList.clone();
        for (int i = 0; i < arryToRemove.length; ++i) {
            returnList.wordList.remove(arryToRemove[i]);
        }
        return returnList;
    }

    // Search tree for wordToCheck, returning true if found, false otherwise
    private boolean check (String word) {
        word = word.toLowerCase();
        return this.wordList.containsKey(word);
    }

    // Clear contents of word list
    public void clear () {
        this.wordList.clear();
    }

    // Return length of wordList
    public int size () {
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

    // Return wordList as string
    public String toString () {
        StringBuilder returnString = new StringBuilder();
        Iterator i = this.wordList.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry mapEntry = (Map.Entry)i.next();
            returnString.append((String)mapEntry.getValue()).append("\n");
        }
        if (returnString.length() > 0) {
            returnString.setLength(returnString.length() - 1);
        }
        return returnString.toString();
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
        TreeMap<String, String> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
