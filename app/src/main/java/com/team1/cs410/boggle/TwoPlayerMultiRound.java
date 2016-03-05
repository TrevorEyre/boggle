package com.team1.cs410.boggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TwoPlayerMultiRound extends AppCompatActivity {

    private static final String TAG = "TwoPlayerActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Member fields
    private Activity activity = null;
    private Game game;
    private boolean isHost;
    private int gameMode;
    private TextView score;
    private TextView oppScore;
    private View menuConnect;
    private View menuEndRound;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService bluetoothService = null;
    private String connectedDeviceName = null;
    private boolean gameOver;
    private boolean oppGameOver;
    private String oppWordsFound;
    private int oppTotalScore;
    private TextView selectedWordLabel;
    private int round;
    private int timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_multi_round);
        activity = this;

        // Get extras from previous activity
        Bundle bundle = getIntent().getExtras();
        round = bundle.getInt("round");
        timer = bundle.getInt("timer");
        gameMode = bundle.getInt("gameMode");

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
            // Start bluetooth services if we haven't already, and
            // start BluetoothDevicesActivity
            if (bluetoothService.getState() == bluetoothService.STATE_NONE) {
                Log.d(TAG, "onResume() - start bluetooth");
                bluetoothService.start();
                Log.d(TAG, "onResume() - start BluetoothDevicesActivity");
                Intent serverIntent = new Intent(activity, BluetoothDevicesActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Set up game UI elements and start BluetoothService
    private void setupGame() {
        gameOver = false;
        oppGameOver = false;
        score = (TextView) findViewById(R.id.score);
        oppScore = (TextView) findViewById(R.id.score_opp);
        bluetoothService = new BluetoothService(activity, handler);
        selectedWordLabel = (TextView) findViewById(R.id.input_word);

        menuConnect = findViewById(R.id.menu_connect);
        menuConnect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "menuConnectClick()");
                Intent serverIntent = new Intent(activity, BluetoothDevicesActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return false;
            }
        });

        menuEndRound = findViewById(R.id.menu_end_round);
        menuEndRound.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (game.getWordCount() >= 5) {
                    game.stopTime();
                    youEndGame();
                } else {
                    Toast.makeText(activity, "You need to find at least five words before ending the round!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    // Host a game. Create new game object, and send board to connected device
    private void hostGame() {
        isHost = true;
//        game = new Game(this, this, gameHandler);
        Log.d(TAG,"Timer: " + timer + " Round: " + round);
        game = new Game(this,this,gameHandler,timer,round);
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
//        game = new Game(this, this, gameHandler, board.toCharArray());
        Log.d(TAG,"Timer: " + timer + " Round: " + round);
        game = new Game(this,this,gameHandler,board.toCharArray(),timer,round);
        LinearLayout gameBoardWrapper = (LinearLayout) findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());
        game.startTime();
    }

    // Receive a word found by opponent. Update opponent's score
    private void receiveOpponentWord(String word) {
        int score = Integer.parseInt(oppScore.getText().toString());
        int newScore = game.score(word);
        score += newScore;
        oppScore.setText(Integer.toString(score));

        // Update your word list if in cutthroat mode
        if (gameMode == Constants.MODE_CUTTHROAT) {
            Log.d(TAG, "receiveOpponentWord() - MODE_CUTTHROAT");
            game.addWord(word);
        }
    }

    // Your game ended. Send message to opponent and try to end game
    private void youEndGame () {
        game.disablebuttons();
        String sendMessage = new String(Constants.READ_END_GAME + game.getWordsFound());
        bluetoothService.write(sendMessage.getBytes());
        gameOver = true;
        endGame();
    }

    // Opponent game ended
    private void opponentEndGame(String oppWords) {
        oppWordsFound = oppWords;
        String arryOppWordsFound[] = oppWordsFound.split("\\n");
        oppTotalScore = game.score(arryOppWordsFound);
        oppGameOver = true;
        endGame();
    }

    private void youEndTimer(){
        //Start intent for two player scores
        Log.d("GameEnd","Inside youEndTimer");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String sendMessage = new String(Constants.READ_END_TIMER + "");
        bluetoothService.write(sendMessage.getBytes());
        Log.d("GameEnd","Sent message to timeout");
        builder.setMessage("You lose!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    // End game. Gets total score for you and opponent, and starts score activity
    private void endGame() {
        // Exit if you or opponent still playing
        if(!gameOver || !oppGameOver) {
            return;
        }

        // Bundle game stats and start TwoPlayerScoresActivity

        Intent intent = new Intent(this, TwoPlayerMultiRound.class);
        Bundle bundle = new Bundle();
        bundle.putInt("round", round + 1);
        //Log.d("tosendtimer")
        TextView absolutetimer = (TextView)activity.findViewById(R.id.absolutetimervalue);
        int timertosend = Integer.parseInt(absolutetimer.getText().toString());
        bundle.putInt("timer",(game.getScore()*1000)+timertosend);
        intent.putExtras(bundle);
//        bundle.putInt("score", score);
//        bundle.putInt("oppScore", oppTotalScore);
//        bundle.putString("wordsFound", game.getWordsFound());
//        bundle.putString("oppWordsFound", oppWordsFound);
//        bundle.putString("wordsNotFound", game.getWordsNotFound(oppWordsFound.split("\\n")));
//        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    // Event handler for submitting a word
    private void wordSubmitted(int result, String submittedWord) {
        Log.d(TAG, "submitWord() " + submittedWord);

        if (result == Constants.SUBMIT_VALID) {
            selectedWordLabel.setTextColor(Constants.COLOR_VALID_DICE);
            String sendMessage = new String(Constants.READ_SEND_WORD + submittedWord);
            bluetoothService.write(sendMessage.getBytes());
        } else if (result == Constants.SUBMIT_INVALID) {
            selectedWordLabel.setTextColor(Constants.COLOR_INVALID_DICE);
        } else {
            selectedWordLabel.setTextColor(Constants.COLOR_ALREADY_FOUND_DICE);
        }

        score.setText(Integer.toString(game.getScore()));
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

    // The Handler that receives messages back from game
    private final Handler gameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                // User selected dice
                case Constants.MESSAGE_SELECT_WORD:
                    String selectedWord = msg.getData().getString(Constants.SELECTED_WORD);
                    selectedWordLabel.setTextColor(Constants.COLOR_DICE);
                    selectedWordLabel.setText(selectedWord);
                    break;

                // User submitted a word
                case Constants.MESSAGE_SUBMIT_WORD:
                    int result = msg.getData().getInt(Constants.SUBMIT_RESULT);
                    String submittedWord = msg.getData().getString(Constants.SUBMITTED_WORD);
                    wordSubmitted(result, submittedWord);
                    break;

                // Game timer went off
                case Constants.MESSAGE_TIME_UP:
                    Log.d(TAG, "gameHandler - MESSAGE_TIME_UP");
                    //youEndGame();
                    youEndTimer();
                    break;
            }
        }
    };

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
                            menuConnect.setVisibility(View.GONE);
                            menuEndRound.setVisibility(View.VISIBLE);
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
                        case (Constants.READ_END_GAME):
                            Log.d(TAG, "Handler - READ_END_GAME");
                            opponentEndGame(readMessage);
                            break;
                        case (Constants.READ_END_TIMER):
                            //Start intent for two player scores
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            //String sendMessage = new String(Constants.READ_END_TIMER + "");
                            //bluetoothService.write(sendMessage.getBytes());
                            Log.d("GameEnd","Readed *cough* end timer");
                            builder.setMessage("You win!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            builder.show();
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
