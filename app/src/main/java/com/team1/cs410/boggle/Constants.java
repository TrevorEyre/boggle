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

    // Message types sent from Game Handler
    public static final int MESSAGE_TIME_UP = 1;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

}


