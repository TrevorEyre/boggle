package com.team1.cs410.boggle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Timer {

    private Handler gameHandler;
    private CountDownTimer countDownTimer;
    public TextView timerLabel;
    private final long startTime = 180 * 1000;
    private final long interval = 1000;

    // Constructor
    public Timer (Handler gameHandler, TextView timerLabel) {
        this.gameHandler = gameHandler;
        this.timerLabel = timerLabel;
        countDownTimer = new MyCountDownTimer(startTime, interval);
        this.timerLabel.setText(this.timerLabel.getText() + String.valueOf(startTime / 1000));
    }

    // Start the timer
    public void startTimer (ArrayList<Button> btn) {
        countDownTimer.start();
    }

    // Stop the timer
    public void stopTimer () {
        countDownTimer.cancel();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer (long startTime, long interval) {
            super(startTime, interval);
        }

        public void onFinish () {
            timerLabel.setText("0");
            gameHandler.obtainMessage(Constants.MESSAGE_TIME_UP).sendToTarget();
        }

        public void onTick (long millsUntilFinished) {
            if (millsUntilFinished/1000 == 30) {
                timerLabel.setTextColor(Color.rgb(244, 67, 54));
            }
            timerLabel.setText("" + String.format("%02d", ((millsUntilFinished/1000)/60)) + ":" + String.format("%02d", ((millsUntilFinished/1000)%60)));
        }

    }
}
