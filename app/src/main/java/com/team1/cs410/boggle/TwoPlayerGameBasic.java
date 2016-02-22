package com.team1.cs410.boggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    ArrayList<String> DeviceNames = new ArrayList<String>();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ConnectionHelper connectionHelper = new ConnectionHelper();
    Set<BluetoothDevice> pairedDevices;

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
        else//if found bluetooth device
        {
            if (!mBluetoothAdapter.isEnabled())//if bluetooth isn't enabled
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //starts an activity that returns result in onActivityResult
            }
            else{
                if(gametype.equals("host"))
                {

                }
            }
        }


        if(gametype.equals("host"))
        {
                //before doing this, select a device
                //after device is selected, invoke appropriate methods from ConnectionHelper for 'server'

        }
        else if(gametype.equals("join"))
        {
            //after device is selected, invoke appropriate methods from ConnectionHelper for 'client'
        }

        activity = this;
        game = new Game(this, this);
        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());
        text = (TextView)this.findViewById(R.id.timer);
        countDownTimer = new TwoPlayerGameBasic.MyCountDownTimer(startTime, interval);
        text.setText(text.getText() + String.valueOf(startTime/1000));
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

    public void onActivityResult(int requestCode, int resultCode, Intent data)//executed when startActivityforResult returns
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
                                    Log.d("device selected","" + which);
                                }
                            });
                    builder.show();

                }
            break;
        }
    }

    private void getpaireddevices()
    {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                DeviceNames.add(device.getName());
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