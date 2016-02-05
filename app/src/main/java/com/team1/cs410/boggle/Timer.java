package com.team1.cs410.boggle;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

public class Timer {

    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView timerLabel;
    private final long startTime = 180 * 1000;
    private final long interval = 1000;

    // Constructor
    public Timer (TextView timerLabel) {
        this.timerLabel = timerLabel;
        countDownTimer = new MyCountDownTimer(startTime, interval);
        this.timerLabel.setText(this.timerLabel.getText() + String.valueOf(startTime / 1000));
    }

    // Start the timer
    public void startTimer () {
        countDownTimer.start();
        timerHasStarted = true;
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
            timerLabel.setText("Times up!");
        }

        public void onTick (long millsUntilFinished) {
            if (millsUntilFinished/1000 == 30) {
                timerLabel.setTextColor(Color.RED);
            }
            timerLabel.setText("" + String.format("%02d", ((millsUntilFinished/1000)/60)) + ":" + String.format("%02d", ((millsUntilFinished/1000)%60)));
        }

    }
}
