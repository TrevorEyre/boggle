package com.team1.cs410.boggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TwoPlayerGameBasic extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView text;
    private Activity activity;
    private Game game;
    private final long startTime = 180 * 1000;
    private final long interval = 1*1000;
    private static final int REQUEST_ENABLE_BT =1;
    private static final int REQUEST_DISCOVERABLE=2;
    private static final int REQUEST_ENABLE_BT2=3;
    ArrayList<String> DeviceNames = new ArrayList<String>();
    ArrayList<String> DeviceAddresses = new ArrayList<String>();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices;
    ArrayList<BluetoothDevice> listpairedDevices= new ArrayList<BluetoothDevice>();
    private ConnectionHelper mConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_game_basic);
        Bundle bundle = getIntent().getExtras();
        String gametype = bundle.getString("gametype");
        String tosend = "hello from host";


        if(mBluetoothAdapter==null)//check if bluetoothadapter is available
        {
            //If not, then there's no point in continuing with the activity
            Toast toast = Toast.makeText(getApplicationContext(), "Cannot find bluetooth", Toast.LENGTH_SHORT);
            toast.show();
            this.finish();
        }
        else//if found bluetooth adapter
        {
            mConnection = new ConnectionHelper(getApplicationContext(),mHandler);
            if(gametype.equals("join")) //if this device is hosting the game
            {
                if (!mBluetoothAdapter.isEnabled())//if bluetooth isn't enabled
                {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //starts an activity that returns result in onActivityResult
                }
                else//bluetooth is already on, just read paired devices and discover devices
                {
                    getpaireddevices();

                    //Show list of paired devices
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select a device to connect to:")
                            //.setItems((CharSequence[]) DeviceNames.toString(), new DialogInterface.OnClickListener() {
                            .setItems(DeviceNames.toArray(new CharSequence[DeviceNames.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("device selected", "" + which);
                                    //connect to 'which'
                                    Log.d("device selected", "" + which);
                                    Log.d("Clicked device: ", listpairedDevices.get(which).getName());
                                    mConnection.connectToDevice(listpairedDevices.get(which), mHandler);
                                    mConnection.waitForOtherDevice();
                                    mConnection.write("Ping".getBytes());
                                    mConnection.waitForOtherDevice();
                                    mConnection.write("Pong".getBytes());
                                }
                            });
                    builder.show();
                }

            }
            else if(gametype.equals("host"))
            {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT2); //starts an activity that returns result in onActivityResult
                }
                else
                {
                    mConnection.accept(mHandler);
//                    mConnection.waitForOtherDevice();
//                    mConnection.write("hello from host".getBytes());
                }

//                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//                //startActivity(discoverableIntent);
//                startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);

            }
        }

        activity = this;
    }

    private void hostGame () {
        game = new Game(this, this);
        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());

        // Send board to connected device
        String sendMessage = new String("0" + new String(game.getDice()));
        mConnection.waitForOtherDevice();
        mConnection.write(sendMessage.getBytes());
        game.startTime();
    }

    private void joinGame (String board) {
        game = new Game(this, this, board.toCharArray());
        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());
        game.startTime();
    }

    public void onBtnClick()
    {
        mConnection.waitForOtherDevice();
        mConnection.write("Clicked".getBytes());
    }

    //executed when startActivityforResult returns
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT://if the current request is to enable bluetooth
                if(resultCode == Activity.RESULT_CANCELED)//if the user doesn't grant permission
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error! Bluetooth is required for two player mode",Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                else if(resultCode == Activity.RESULT_OK)//if the user grants permission
                {
                    //If the user clicks ok, show a list of paired devices in the log.
                    getpaireddevices();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select a device to connect to:")
                            //.setItems((CharSequence[]) DeviceNames.toString(), new DialogInterface.OnClickListener() {
                            .setItems(DeviceNames.toArray(new CharSequence[DeviceNames.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("device selected", "" + which);
                                    Log.d("Clicked device: ", listpairedDevices.get(which).getName());
                                    mConnection.connectToDevice(listpairedDevices.get(which), mHandler);
//                                    mConnection.waitForOtherDevice();
//                                    mConnection.write("Ping".getBytes());
//                                    mConnection.waitForOtherDevice();
//                                    mConnection.write("Pong".getBytes());
                                }
                            });
                    builder.show();
                }
            break;
            case REQUEST_ENABLE_BT2: //if the current request is to make device discoverable
                //if(resultCode==Activity.RESULT_OK)
                if(resultCode==Activity.RESULT_OK)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Your device is now discoverable",Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d("Bluetooth ", "Device On");
                    mConnection.accept(mHandler);
//                    mConnection.waitForOtherDevice();
//                    mConnection.write("Hello from host".getBytes());
                    hostGame();

                }
                else if(resultCode==Activity.RESULT_CANCELED)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error! You need to set device to be discoverable!",Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                break;
//            case REQUEST_DISCOVERABLE: //if the current request is to make device discoverable
//                //if(resultCode==Activity.RESULT_OK)
//                if(resultCode==Activity.RESULT_OK)
//                {
//                    Toast toast = Toast.makeText(getApplicationContext(),"Your device is now discoverable",Toast.LENGTH_LONG);
//                    toast.show();
//                    mConnection.accept(mHandler);
//                }
//                else if(resultCode==Activity.RESULT_CANCELED)
//                {
//                    Toast toast = Toast.makeText(getApplicationContext(),"Error! You need to set device to be discoverable!",Toast.LENGTH_LONG);
//                    toast.show();
//                    finish();
//                }
//                break;
        }
    }


    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(getClass().getSimpleName(), "setupChat()");

//        // Initialize the send button with a listener that for click events
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                View view = getView();
//                if (null != view) {
//                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
//                    String message = textView.getText().toString();
//                    sendMessage(message);
//                }
//            }
//        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mConnection = new ConnectionHelper(activity, mHandler);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("Handler","Inside handler"+msg.what);
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case ConnectionHelper.STATE_CONNECTED:
//                            Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
//                            break;
//                        case ConnectionHelper.STATE_CONNECTING:
//                            Toast.makeText(activity, "Connecting", Toast.LENGTH_SHORT).show();
//                            break;
//                        case ConnectionHelper.STATE_LISTEN:
//                        case ConnectionHelper.STATE_NONE:
//                            Toast.makeText(activity, "Unable to connect", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                    break;
                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);

                    String writeMessage = new String("Writing a message");
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    Log.d("Handler","Reading");
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String rawMessage = new String(readBuf, 0, msg.arg1);
                    int msgType = Integer.parseInt(rawMessage.substring(0, 1));
                    String readMessage = rawMessage.substring(1, rawMessage.length());
                    Log.d("MESSAGE_READ", "rawMessage: " + rawMessage);
                    Log.d("MESSAGE_READ", "msgType: " + msgType);
                    Log.d("MESSAGE_READ", "readMessage: " + readMessage);
//                    Toast.makeText(activity, readMessage, Toast.LENGTH_SHORT).show();

                    switch (msgType) {
                        case(0): // New game
                            joinGame(readMessage);
                            break;
                        case(1): // Send word
                            break;
                    }
                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
            }
        }
    };

    private void getpaireddevices()
    {
        //pairedDevices.clear();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                DeviceNames.add(device.getName());
                DeviceAddresses.add(device.getAddress());
                listpairedDevices.add(device);
                //Show the device list in the log
                Log.d("Bluetooth device found",device.getName() + "\t" + device.getAddress());
            }
        }
    }

    //@Override
    public void onClick(View v)
    {
        if(!timerHasStarted){
            countDownTimer.start();
            timerHasStarted = true;
        }
        else
        {
            countDownTimer.cancel();
            timerHasStarted = false;
        }
    }



    public class MyCountDownTimer extends CountDownTimer{
        public MyCountDownTimer(long startTime, long interval){
            super(startTime, interval);
        }

        public void onFinish(){
            text.setText("Times up!");
        }
        public void onTick(long millsUntilFinished){
            if(millsUntilFinished/1000 == 30) {
                text.setTextColor(Color.RED);
            }
            text.setText("" + String.format("%02d",((millsUntilFinished/1000)/60))+":"+String.format("%02d",((millsUntilFinished/1000)%60)));
        }

            /*long remainedSecs = millsUntilFinished/1000;
            text.setText("" + millsUntilFinished/1000);*/

    }

}