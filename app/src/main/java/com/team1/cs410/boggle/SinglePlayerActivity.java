package com.team1.cs410.boggle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePlayerActivity extends AppCompatActivity {

    // Tag for debug statements
    private static final String TAG = "SinglePlayerActivity";

    // Member fields
    Context context;
    GestureDetector gestureDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private Game game;
    private TextView selectedWordLabel;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        selectedWordLabel = (TextView) findViewById(R.id.input_word);

        // Create new game, and insert game board into wrapper
        context = this;
        game = new Game(this, this, gameHandler);
        LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
        gameBoardWrapper.addView(game.getBoard());

        // Get intent and start game timer
        Intent intent = getIntent();
        game.startTime();

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                //handleShakeEvent(count);
                //onClickShake(null);

                Toast.makeText(getBaseContext(), "Motion detected",
                        Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
                /*Context context = getApplicationContext();
                game = new Game(context,activity);
                LinearLayout gameBoardWrapper = (LinearLayout)findViewById(R.id.game_board_wrapper);
                gameBoardWrapper.addView(game.getBoard());

                // Get intent and start game timer
                Intent intent = getIntent();
                game.startTime();*/
            }

        });
    }

    // A word was submitted. Update selected word label based on result
    private void wordSubmitted (int result) {
        TextView scoreDisplay = (TextView)this.findViewById(R.id.score);
        scoreDisplay.setText(Integer.toString(game.getScore()));

        if (result == Constants.SUBMIT_VALID) {
            selectedWordLabel.setTextColor(Constants.COLOR_VALID_DICE);
        } else if (result == Constants.SUBMIT_INVALID) {
            selectedWordLabel.setTextColor(Constants.COLOR_INVALID_DICE);
        } else {
            selectedWordLabel.setTextColor(Constants.COLOR_ALREADY_FOUND_DICE);
        }
    }

    public void endbuttonclick (View view) {
        endGame();
    }

    private void endGame () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Time's up!");

        // Set up view for end game dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up name label
        TextView nameLabel = new TextView(this);
        nameLabel.setTextSize(24);
        nameLabel.setPadding(16, 16, 16, 16);
        nameLabel.setText("Enter your name");
        layout.addView(nameLabel);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(input);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                int score = game.getScore();
                Intent intent = new Intent(context, HighScoresActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putInt("score", score);
                bundle.putString("wordsFound", game.getWordsFound());
                bundle.putString("wordsNotFound", game.getWordsNotFound());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
                    wordSubmitted(result);
                    break;

                // Game timer went off
                case Constants.MESSAGE_TIME_UP:
                    Log.d(TAG, "Handler - MESSAGE_TIME_UP");
                    endGame();
                    break;
            }
        }
    };
}

