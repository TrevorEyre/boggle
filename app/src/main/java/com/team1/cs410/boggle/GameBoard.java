package com.team1.cs410.boggle;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GameBoard extends AppCompatActivity {


    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView text;
    private final long startTime = 180 * 1000;
    private final long interval = 1000;
    ArrayList<Button> disabledbuttons;
    ArrayList<Button> pressedbuttons;
    Button [][] buttons;
    char boardarray[][];
    RandomBoardGenerator generator;

    GestureDetector gestureDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    //private ShakeEventManager mShakeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        disabledbuttons = new ArrayList<>();
        pressedbuttons = new ArrayList<>();
        generator = new RandomBoardGenerator();
        buttons = new Button[4][4];
        boardarray= new char[4][4];
        //initialize the 2d array of buttons
        Button btn = (Button) findViewById(R.id.button11);
        buttons[0][0] = btn;
        btn = (Button) findViewById(R.id.button12);
        buttons[0][1] = btn;
        btn = (Button) findViewById(R.id.button13);
        buttons[0][2] = btn;
        btn = (Button) findViewById(R.id.button14);
        buttons[0][3] = btn;

        btn = (Button) findViewById(R.id.button21);
        buttons[1][0] = btn;
        btn = (Button) findViewById(R.id.button22);
        buttons[1][1] = btn;
        btn = (Button) findViewById(R.id.button23);
        buttons[1][2] = btn;
        btn = (Button) findViewById(R.id.button24);
        buttons[1][3] = btn;

        btn = (Button) findViewById(R.id.button31);
        buttons[2][0] = btn;
        btn = (Button) findViewById(R.id.button32);
        buttons[2][1] = btn;
        btn = (Button) findViewById(R.id.button33);
        buttons[2][2] = btn;
        btn = (Button) findViewById(R.id.button34);
        buttons[2][3] = btn;

        btn = (Button) findViewById(R.id.button41);
        buttons[3][0] = btn;
        btn = (Button) findViewById(R.id.button42);
        buttons[3][1] = btn;
        btn = (Button) findViewById(R.id.button43);
        buttons[3][2] = btn;
        btn = (Button) findViewById(R.id.button44);
        buttons[3][3] = btn;

        //get randomized text array from generator class
        boardarray = generator.generateboard();
        //set corresponding button text according to random array
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                /*Button btn1;
                btn1 = buttons[i][j];
                btn1.setText(boardarray[i][j]);*/
                //char current;
                //current = boardarray[i][j];
                //String str1;
                //str1 = new String();
                //str1 = Character.toString(boardarray[i][j]);
                //buttons[i][j].setText(boardarray[i][j]);
                buttons[i][j].setText(Character.toString(boardarray[i][j]));
            }
        }

        Intent intent = getIntent();
        text = (TextView)this.findViewById(R.id.timer);
        countDownTimer = new MyCountDownTimer(startTime, interval);
        text.setText(text.getText() + String.valueOf(startTime / 1000));
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

    //handle the click on board buttons
    public void boardbuttonclick (View view) {

        //disabledbuttons.clear();
        enableandclear(disabledbuttons);
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn = (Button) findViewById(view.getId());
        textinputview.append(btn.getText());
        btn.setEnabled(false);
        btn.setBackgroundColor(0xFFF34040);
        pressedbuttons.add(btn);

        switch (view.getId())
        {
            case R.id.button11:
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button12:
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button13:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button14:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button21:
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button22:
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button23:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button24:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button31:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button32:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button33:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                break;
            case R.id.button34:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                break;
            case R.id.button41:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button33);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button43);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button42:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button34);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button44);
                disabledbuttons.add(btn);
                break;
            case R.id.button43:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                break;
            case R.id.button44:
                btn = (Button) findViewById(R.id.button11);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button12);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button13);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button14);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button21);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button22);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button23);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button24);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button31);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button32);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button41);
                disabledbuttons.add(btn);
                btn = (Button) findViewById(R.id.button42);
                disabledbuttons.add(btn);
                break;
        }
        for(int i=0;i<disabledbuttons.size();i++)
        {
            btn = disabledbuttons.get(i);
            btn.setEnabled(false);
        }

    }

    //handle the click on 'clear'
    public void buttonclearclick (View view){
        TextView inputtextview = (TextView) findViewById(R.id.textviewinputword);
        inputtextview.setText("Your Word: ");
        Button btn;
        for(int i=0;i<disabledbuttons.size();i++)
        {
            btn = disabledbuttons.get(i);
                btn.setEnabled(true);
        }
        disabledbuttons.clear();
        for(int i=0;i<pressedbuttons.size();i++)
        {
            btn = pressedbuttons.get(i);
            btn.setEnabled(true);
            btn.setBackgroundResource(android.R.drawable.btn_default);
        }
        pressedbuttons.clear();
    }

    //a function to enable a list of disabled buttons and clear it
    public void enableandclear(ArrayList<Button> list)
    {
        Button btn;
        int i, arraylistsize;
        arraylistsize = list.size();
        for(i=0;i<arraylistsize;i++)
        {
            btn = list.get(i);
            if(!pressedbuttons.contains(btn)){
                btn.setEnabled(true);
            }
        }
        list.clear();
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

    }

}
