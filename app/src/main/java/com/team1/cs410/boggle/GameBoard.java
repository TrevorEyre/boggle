package com.team1.cs410.boggle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private final long interval = 1*1000;
    //Button disabledbuttons[];
    ArrayList<Button> disabledbuttons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        disabledbuttons = new ArrayList<>();
        Intent intent = getIntent();
        text = (TextView)this.findViewById(R.id.timer);
        countDownTimer = new MyCountDownTimer(startTime, interval);
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

    //handle the click on board buttons
    public void boardbuttonclick (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn = (Button) findViewById(view.getId());
        textinputview.append(btn.getText());
        btn.setEnabled(false);
        disabledbuttons.add(btn);

    }

    //handle the click on 'clear'
    public void buttonclearclick (View view){
        TextView inputtextview = (TextView) findViewById(R.id.textviewinputword);
        inputtextview.setText("Your Word: ");
        Button btn;
        int i, arraylistsize;
        arraylistsize = disabledbuttons.size();
        for (i=0;i<arraylistsize;i++)
        {
            btn = disabledbuttons.get(i);
            btn.setEnabled(true);
        }
        disabledbuttons.clear();
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
