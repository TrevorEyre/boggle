package com.team1.cs410.boggle;

/**
 * Created by nikhilagrawal on 2/17/16.
 */

/**
 * Created by nikhilagrawal on 2/16/16.
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Color;

/**
 * Defines several constants used between BluetoothChatService and the UI.
 */
public interface Constants {

    // Multiplayer game modes
    public static final int MODE_BASIC = 1;
    public static final int MODE_CUTTHROAT = 2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_HOST_GAME = 2;
    public static final int MESSAGE_READ = 3;
    public static final int MESSAGE_WRITE = 4;
    public static final int MESSAGE_DEVICE_NAME = 5;
    public static final int MESSAGE_TOAST = 6;

    // MESSAGE_READ flags in BluetoothChatService Handler
    public static final int READ_NEW_GAME = 1;
    public static final int READ_SEND_WORD = 2;
    public static final int READ_END_GAME = 3;
    public static final int READ_END_TIMER = 4;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Message types sent from Game Handler
    public static final int MESSAGE_SUBMIT_WORD = 1;
    public static final int MESSAGE_SELECT_WORD = 2;
    public static final int MESSAGE_TIME_UP = 3;

    // Message types sent from Board Handler
//    public static final int MESSAGE_SUBMIT_WORD = 1; (Used in Game Handler and Board Handler)
//    public static final int MESSAGE_SELECT_WORD = 2;

    // Key names received from Board Handler
    public static final String SELECTED_WORD = "selected_word";
    public static final String SUBMITTED_WORD = "submitted_word";
    public static final String SUBMIT_RESULT = "submit_result";

    // Results of submitting a word in game
    public static final int SUBMIT_VALID = 1;
    public static final int SUBMIT_INVALID = 2;
    public static final int SUBMIT_ALREADY_FOUND = 3;

    // Colors
    public static final int COLOR_DICE = Color.rgb(102, 102, 102);
    public static final int COLOR_SELECTED_DICE = Color.rgb(3, 169, 244);
    public static final int COLOR_VALID_DICE = Color.rgb(76, 175, 80);
    public static final int COLOR_INVALID_DICE = Color.rgb(244, 67, 54);
    public static final int COLOR_ALREADY_FOUND_DICE = Color.rgb(255, 152, 0);

}


