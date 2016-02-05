package com.team1.cs410.boggle;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SinglePlayerGame extends AppCompatActivity {

    GestureDetector gestureDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    //private ShakeEventManager mShakeDetector;

    private Game game;
    private Timer timer;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_game);

        // Start new game, and insert game board into wrapper
        game = new Game(this, this);
        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());

        // Get intent and start game timer
        Intent intent = getIntent();
        timer = new Timer((TextView)this.findViewById(R.id.timer));
    }

    // Click event handler for button_clear
    public void buttonClearClick (View view) {
        TextView selectedWord = (TextView)this.findViewById(R.id.input_word);
        selectedWord.setText("");

        game.clearSelected();
    }

    // Click event handler for button_submit
    public void buttonSubmitClick (View view) {
        TextView scoreDisplay = (TextView)this.findViewById(R.id.score);
        TextView selectedWord = (TextView)this.findViewById(R.id.input_word);

        int score = game.submitWord();
        if (score == 0) {
            selectedWord.setTextColor(Color.rgb(244, 67, 54));
        } else {
            selectedWord.setTextColor(Color.rgb(0, 200, 83));
        }
        scoreDisplay.setText(Integer.toString(game.getScore()));
    }
}
