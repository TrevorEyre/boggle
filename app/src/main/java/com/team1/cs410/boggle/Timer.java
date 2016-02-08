package com.team1.cs410.boggle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Timer {

    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView timerLabel;
    public TextView scoreLabel;
    public TextView score;
    private final long startTime = 180 * 1000;
    private final long interval = 1000;
    private ArrayList<Button> buttons;
    private Button submitbutton;
    private Button clearbutton;

    // Constructor
    public Timer (TextView timerLabel, TextView score, TextView scorelabel, Button submitbutton, Button clearbutton) {
        this.timerLabel = timerLabel;
        this.scoreLabel = scorelabel;
        this.score=score;
        this.clearbutton=clearbutton;
        this.submitbutton=submitbutton;
        countDownTimer = new MyCountDownTimer(startTime, interval);
        this.timerLabel.setText(this.timerLabel.getText() + String.valueOf(startTime / 1000));
    }

    // Start the timer
    public void startTimer (ArrayList<Button> btn) {
        countDownTimer.start();
        timerHasStarted = true;
        this.buttons = btn;
    }

    // Stop the timer
    public void stopTimer () {
        countDownTimer.cancel();
        timerHasStarted = false;
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer (long startTime, long interval) {
            super(startTime, interval);
        }

        public void onFinish () {
            String scorestr = (String)score.getText();
            timerLabel.setText("Times up!");
            scoreLabel.setText("Your score is: ");
            Button btn;
            for(int i=0;i< buttons.size();i++)
            {
                btn = buttons.get(i);
                btn.setEnabled(false);
            }
            clearbutton.setEnabled(false);
            submitbutton.setEnabled(false);
        }

        public void onTick (long millsUntilFinished) {
            if (millsUntilFinished/1000 == 30) {
                timerLabel.setTextColor(Color.RED);
            }
            timerLabel.setText("" + String.format("%02d", ((millsUntilFinished/1000)/60)) + ":" + String.format("%02d", ((millsUntilFinished/1000)%60)));
        }

    }
}
