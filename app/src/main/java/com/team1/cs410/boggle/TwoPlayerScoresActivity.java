package com.team1.cs410.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class TwoPlayerScoresActivity extends AppCompatActivity {

    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_scores);

        activity = this;

        //Get the intent from previous activity
        Bundle bundle = getIntent().getExtras();
        int score = bundle.getInt("score");
        int oppScore = bundle.getInt("oppScore");
        String wordsFound = bundle.getString("wordsFound");
        String oppWordsFound = bundle.getString("oppWordsFound");
        String wordsNotFound = bundle.getString("wordsNotFound");

        // Get layout elements
        TextView winnerTV = (TextView) findViewById(R.id.winner);
        TextView scoreTV = (TextView) findViewById(R.id.you_score);
        TextView oppScoreTV = (TextView) findViewById(R.id.opp_score);
        TextView wordsFoundTV = (TextView) findViewById(R.id.you_words_found);
        TextView oppWordsFoundTV = (TextView) findViewById(R.id.opp_words_found);
        TextView wordsNotFoundTV = (TextView) findViewById(R.id.words_not_found);

        // Set winner label
        if (score > oppScore) {
            winnerTV.setText("You Win!");
        } else if (score < oppScore) {
            winnerTV.setText("You Lose");
        } else {
            winnerTV.setText("Tie Game");
        }

        // Set other game stats
        scoreTV.setText(Integer.toString(score));
        oppScoreTV.setText(Integer.toString(oppScore));
        wordsFoundTV.setText(wordsFound);
        oppWordsFoundTV.setText(oppWordsFound);
        wordsNotFoundTV.setText(wordsNotFound);

        // Back button click event
        TextView backButton = (TextView) findViewById(R.id.menu_back);
        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(activity, MainActivity.class));
                finish();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
