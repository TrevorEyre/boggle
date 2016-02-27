package com.team1.cs410.boggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class TwoPlayerGameBasic extends AppCompatActivity {

    // Tag for debug statements
    private static final String TAG = "TwoPlayerGameBasic";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Activity activity = null;
    private Game game;
    private boolean isHost;
    private TextView score;
    private TextView opponentScore;
    private Button buttonConnect;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService bluetoothService = null;
    private String connectedDeviceName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_game_basic);
        activity = this;

        // Bluetooth is not supported, exit activity
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        // If BT is not on, request that it be enabled. Otherwise, set up game
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (bluetoothService == null) {
            setupGame();
        }
    }

    // Performing this check in onResume() covers the case in which BT was
    // not enabled during onStart(), so we were paused to enable it...
    // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        if (bluetoothService != null) {
            // Start bluetooth services if we haven't already
            if (bluetoothService.getState() == bluetoothService.STATE_NONE) {
                Log.d(TAG, "onResume() - start bluetooth");
                bluetoothService.start();

//                ensureDiscoverable();
                Log.d(TAG, "onResume() - start BluetoothDevicesActivity");
                Intent serverIntent = new Intent(activity, BluetoothDevicesActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }

            // If we aren't connected to a device, start BluetoothDevicesActivity
//            int state = bluetoothService.getState();
//            if (state != bluetoothService.STATE_CONNECTED
//                    && state != bluetoothService.STATE_CONNECTING) {
//                ensureDiscoverable();
//                Log.d(TAG, "onResume() - start BluetoothDevicesActivity");
//                Intent serverIntent = new Intent(activity, BluetoothDevicesActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }

    // Set up game UI elements and start BluetoothService
    private void setupGame() {
        score = (TextView) findViewById(R.id.score);
        opponentScore = (TextView) findViewById(R.id.score_opp);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        bluetoothService = new BluetoothService(activity, handler);
    }

    // Host a game. Create new game object, and send board to connected device
    private void hostGame() {
        isHost = true;
        game = new Game(this, this);
        LinearLayout gameBoardWrapper = (LinearLayout) findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());

        // Send board to connected device
        String sendMessage = Constants.READ_NEW_GAME + new String(game.getDice());
        bluetoothService.write(sendMessage.getBytes());
        game.startTime();
    }

    // Join a game. Initialize game object with board received from host
    private void joinGame(String board) {
        isHost = false;
        game = new Game(this, this, board.toCharArray());
        LinearLayout gameBoardWrapper = (LinearLayout) findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());
        game.startTime();
    }

    // Receive a word found by opponent. Update opponent's score
    private void receiveOpponentWord(String word) {
        int score = Integer.parseInt(opponentScore.getText().toString());
        int newScore = game.score(word);
        score += newScore;
        opponentScore.setText(Integer.toString(score));
    }

    // Click event handler for button_submit
    public void buttonSubmitClick(View view) {
        TextView selectedWord = (TextView) this.findViewById(R.id.input_word);
        String word = game.submitWord();
        Log.d(TAG, "submitWord() " + word);

        if (word == null) {
            selectedWord.setTextColor(Color.rgb(244, 67, 54));
        } else {
            // Valid word. Send to opponent
            selectedWord.setTextColor(Color.rgb(0, 200, 83));
            String sendMessage = new String(Constants.READ_SEND_WORD + word);
            bluetoothService.write(sendMessage.getBytes());
        }
        score.setText(Integer.toString(game.getScore()));
    }

    // Click event handler for button_connect
    public void buttonConnectClick (View view) {
        Log.d(TAG, "buttonConnectClick()");
        Intent serverIntent = new Intent(activity, BluetoothDevicesActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void endbuttonclick(View view) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter your name!");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                m_Text = input.getText().toString();
//                okclick();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
    }

    // Return from bluetooth activities
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            // Return from BluetoothDevicesActivity
            case REQUEST_CONNECT_DEVICE:
                Log.d(TAG, "onActivityResult() - REQUEST_CONNECT_DEVICE");
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(intent);
                }
                break;

            // Return from bluetooth enable request
            case REQUEST_ENABLE_BT:
                Log.d(TAG, "onActivityResult() - REQUEST_ENABLE_BT");
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a new game
                    setupGame();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "Bluetooth not enabled");
                    Toast.makeText(activity, "Bluetooth must be enabled to play two player", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
                break;
        }
    }

    // Get device MAC address and attempt to establish connection with other device
    private void connectDevice(Intent data) {
        Log.d(TAG, "connectDevice()");
        String address = data.getExtras().getString(BluetoothDevicesActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        bluetoothService.connect(device);
    }

    // Change status when trying to connect Bluetooth
    private void setStatus(String status) {
        Log.d(TAG, "setStatus - " + status);
    }

    // The Handler that receives messages back from BluetoothService
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                // BluetoothService state change event
                case Constants.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "Handler - MESSAGE_STATE_CHANGE");
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus("Connected to " + connectedDeviceName);
                            buttonConnect.setVisibility(View.GONE);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus("Connecting...");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus("Not connected");
                            break;
                    }
                    break;

                // Successfully connected to remote device. Start hosted game
                case Constants.MESSAGE_HOST_GAME:
                    Log.d(TAG, "Handler - MESSAGE_HOST_GAME");
                    hostGame();
                    break;

                // Send message to connected device
                case Constants.MESSAGE_WRITE:
                    Log.d(TAG, "Handler - MESSAGE_WRITE");
                    break;

                // Receive message from connected device
                case Constants.MESSAGE_READ:
                    // Read buffer and get message type. First character in buffer is an int flag
                    // which tells what type of message this is.
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    int messageType = Integer.parseInt(readMessage.substring(0, 1));
                    readMessage = readMessage.substring(1);

                    switch (messageType) {
                        case (Constants.READ_NEW_GAME):
                            Log.d(TAG, "Handler - READ_NEW_GAME");
                            joinGame(readMessage);
                            break;
                        case (Constants.READ_SEND_WORD):
                            Log.d(TAG, "Handler - READ_SEND_WORD");
                            receiveOpponentWord(readMessage);
                            break;
                    }
                    break;

                // Receive name of connected device
                case Constants.MESSAGE_DEVICE_NAME:
                    // Save the connected device's name
                    Log.d(TAG, "Handler - MESSAGE_DEVICE_NAME");
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(activity, "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                // Receive toast from BluetoothService
                case Constants.MESSAGE_TOAST:
                    Log.d(TAG, "Handler - MESSAGE_TOAST");
                    Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}











































































































//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_two_player_game_basic);
//        Bundle bundle = getIntent().getExtras();
//        String gametype = bundle.getString("gametype");
//        isHost = gametype == "host";
//
//
//        if(bluetoothAdapter==null)//check if bluetoothadapter is available
//        {
//            //If not, then there's no point in continuing with the activity
////            Toast toast = Toast.makeText(getApplicationContext(), "Cannot find bluetooth", Toast.LENGTH_SHORT);
////            toast.show();
//            this.finish();
//        }
//        else//if found bluetooth adapter
//        {
//            connection = new BluetoothService(getApplicationContext(),mHandler);
//            if(gametype.equals("join")) //if this device is hosting the game
//            {
//                if (!bluetoothAdapter.isEnabled())//if bluetooth isn't enabled
//                {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //starts an activity that returns result in onActivityResult
//                }
//                else//bluetooth is already on, just read paired devices and discover devices
//                {
//                    getpaireddevices();
//
//                    //Show list of paired devices
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Select a device to connect to:")
//                            //.setItems((CharSequence[]) DeviceNames.toString(), new DialogInterface.OnClickListener() {
//                            .setItems(DeviceNames.toArray(new CharSequence[DeviceNames.size()]), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Log.d("device selected", "" + which);
//                                    //connect to 'which'
//                                    Log.d("device selected", "" + which);
//                                    Log.d("Clicked device: ", listpairedDevices.get(which).getName());
//                                    connection.connectToDevice(listpairedDevices.get(which), mHandler);
////                                    connection.waitForOtherDevice();
////                                    connection.write("Ping".getBytes());
////                                    connection.waitForOtherDevice();
////                                    connection.write("Pong".getBytes());
//                                }
//                            });
//                    builder.show();
//                }
//
//            }
//            else if(gametype.equals("host"))
//            {
//                if (!bluetoothAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT2); //starts an activity that returns result in onActivityResult
//                }
//                else
//                {
//                    connection.accept(mHandler);
////                    connection.waitForOtherDevice();
////                    connection.write("hello from host".getBytes());
//                }
//
////                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
////                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
////                //startActivity(discoverableIntent);
////                startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
//
//            }
//        }
//
//        activity = this;
//    }
//
//    private void hostGame () {
//        game = new Game(this, this);
//        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
//        gameBoardWrapper.addView(game.getBoard());
//
//        // Send board to connected device
//        String sendMessage = new String("0" + new String(game.getDice()));
//        connection.waitForOtherDevice();
//        connection.write(sendMessage.getBytes());
//        game.startTime();
//    }
//
//    private void joinGame (String board) {
//        game = new Game(this, this, board.toCharArray());
//        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
//        gameBoardWrapper.addView(game.getBoard());
//        game.startTime();
//    }
//
//    private void receiveOpponentWord (String word) {
//        TextView oppScoreTextView = (TextView)this.findViewById(R.id.score_opp);
//        int score = Integer.parseInt(oppScoreTextView.getText().toString());
//        int newScore = game.score(word);
//        score += newScore;
//
//        oppScoreTextView.setText(Integer.toString(score));
//    }
//
//    public void onBtnClick()
//    {
//        connection.waitForOtherDevice();
//        connection.write("Clicked".getBytes());
//    }
//
//    //executed when startActivityforResult returns
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        switch (requestCode)
//        {
//            case REQUEST_ENABLE_BT://if the current request is to enable bluetooth
//                if(resultCode == Activity.RESULT_CANCELED)//if the user doesn't grant permission
//                {
////                    Toast toast = Toast.makeText(getApplicationContext(),"Error! Bluetooth is required for two player mode",Toast.LENGTH_LONG);
////                    toast.show();
//                    finish();
//                }
//                else if(resultCode == Activity.RESULT_OK)//if the user grants permission
//                {
//                    //If the user clicks ok, show a list of paired devices in the log.
//                    getpaireddevices();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Select a device to connect to:")
//                            //.setItems((CharSequence[]) DeviceNames.toString(), new DialogInterface.OnClickListener() {
//                            .setItems(DeviceNames.toArray(new CharSequence[DeviceNames.size()]), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Log.d("device selected", "" + which);
//                                    Log.d("Clicked device: ", listpairedDevices.get(which).getName());
//                                    connection.connectToDevice(listpairedDevices.get(which), mHandler);
////                                    connection.waitForOtherDevice();
////                                    connection.write("Ping".getBytes());
////                                    connection.waitForOtherDevice();
////                                    connection.write("Pong".getBytes());
//                                }
//                            });
//                    builder.show();
//                }
//            break;
//            case REQUEST_ENABLE_BT2: //if the current request is to make device discoverable
//                //if(resultCode==Activity.RESULT_OK)
//                if(resultCode==Activity.RESULT_OK)
//                {
////                    Toast toast = Toast.makeText(getApplicationContext(),"Your device is now discoverable",Toast.LENGTH_SHORT);
////                    toast.show();
//                    Log.d("Bluetooth ", "Device On");
//                    connection.accept(mHandler);
////                    connection.waitForOtherDevice();
////                    connection.write("Hello from host".getBytes());
//                    hostGame();
//
//                }
//                else if(resultCode==Activity.RESULT_CANCELED)
//                {
////                    Toast toast = Toast.makeText(getApplicationContext(),"Error! You need to set device to be discoverable!",Toast.LENGTH_LONG);
////                    toast.show();
//                    finish();
//                }
//                break;
////            case REQUEST_DISCOVERABLE: //if the current request is to make device discoverable
////                //if(resultCode==Activity.RESULT_OK)
////                if(resultCode==Activity.RESULT_OK)
////                {
////                    Toast toast = Toast.makeText(getApplicationContext(),"Your device is now discoverable",Toast.LENGTH_LONG);
////                    toast.show();
////                    connection.accept(mHandler);
////                }
////                else if(resultCode==Activity.RESULT_CANCELED)
////                {
////                    Toast toast = Toast.makeText(getApplicationContext(),"Error! You need to set device to be discoverable!",Toast.LENGTH_LONG);
////                    toast.show();
////                    finish();
////                }
////                break;
//        }
//    }
//
//
//    /**
//     * Set up the UI and background operations for chat.
//     */
//    private void setupChat() {
//        Log.d(getClass().getSimpleName(), "setupChat()");
//
////        // Initialize the send button with a listener that for click events
////        mSendButton.setOnClickListener(new View.OnClickListener() {
////            public void onClick(View v) {
////                // Send a message using content of the edit text widget
////                View view = getView();
////                if (null != view) {
////                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
////                    String message = textView.getText().toString();
////                    sendMessage(message);
////                }
////            }
////        });
//
//        // Initialize the BluetoothChatService to perform bluetooth connections
//        connection = new BluetoothService(activity, mHandler);
//    }
//
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Log.d("Handler","Inside handler"+msg.what);
////            FragmentActivity activity = getActivity();
//            switch (msg.what) {
////                case Constants.MESSAGE_STATE_CHANGE:
////                    switch (msg.arg1) {
////                        case BluetoothService.STATE_CONNECTED:
////                            Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
////                            break;
////                        case BluetoothService.STATE_CONNECTING:
////                            Toast.makeText(activity, "Connecting", Toast.LENGTH_SHORT).show();
////                            break;
////                        case BluetoothService.STATE_LISTEN:
////                        case BluetoothService.STATE_NONE:
////                            Toast.makeText(activity, "Unable to connect", Toast.LENGTH_SHORT).show();
////                            break;
////                    }
////                    break;
//                case Constants.MESSAGE_WRITE:
////                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
////                    String writeMessage = new String(writeBuf);
//
//                    String writeMessage = new String("Writing a message");
////                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
//                case Constants.MESSAGE_READ:
//                    Log.d("Handler","Reading");
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String rawMessage = new String(readBuf, 0, msg.arg1);
//                    int msgType = Integer.parseInt(rawMessage.substring(0, 1));
//                    String readMessage = rawMessage.substring(1, rawMessage.length());
//                    Log.d("MESSAGE_READ", "rawMessage: " + rawMessage);
//                    Log.d("MESSAGE_READ", "msgType: " + msgType);
//                    Log.d("MESSAGE_READ", "readMessage: " + readMessage);
////                    Toast.makeText(activity, readMessage, Toast.LENGTH_SHORT).show();
//
//                    switch (msgType) {
//                        case(0): // New game
//                            joinGame(readMessage);
//                            break;
//                        case(1): // Send word
//                            receiveOpponentWord(readMessage);
//                            break;
//                    }
//                    break;
////                case Constants.MESSAGE_DEVICE_NAME:
////                    // save the connected device's name
////                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
////                    if (null != activity) {
////                        Toast.makeText(activity, "Connected to "
////                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
////                    }
////                    break;
////                case Constants.MESSAGE_TOAST:
////                    if (null != activity) {
////                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
////                                Toast.LENGTH_SHORT).show();
////                    }
////                    break;
//            }
//        }
//    };
//
//    private void getpaireddevices()
//    {
//        //pairedDevices.clear();
//        pairedDevices = bluetoothAdapter.getBondedDevices();
//        // If there are paired devices
//        if (pairedDevices.size() > 0) {
//            // Loop through paired devices
//            for (BluetoothDevice device : pairedDevices) {
//                // Add the name and address to an array adapter to show in a ListView
//                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                DeviceNames.add(device.getName());
//                DeviceAddresses.add(device.getAddress());
//                listpairedDevices.add(device);
//                //Show the device list in the log
//                Log.d("Bluetooth device found",device.getName() + "\t" + device.getAddress());
//            }
//        }
//    }
//
//    // Click event handler for button_submit
//    public void buttonSubmitClick (View view) {
//        TextView scoreDisplay = (TextView)this.findViewById(R.id.score);
//        TextView selectedWord = (TextView)this.findViewById(R.id.input_word);
//
//        String word = game.submitWord();
//        Log.d("buttonSubmitClick", "submit: " + word);
//        if (word == null) {
//            selectedWord.setTextColor(Color.rgb(244, 67, 54));
//        } else {
//            // Valid word. Send to opponent
//            selectedWord.setTextColor(Color.rgb(0, 200, 83));
//            String sendMessage = new String("1" + word);
//            connection.waitForOtherDevice();
//            connection.write(sendMessage.getBytes());
//        }
//        scoreDisplay.setText(Integer.toString(game.getScore()));
//    }
//}