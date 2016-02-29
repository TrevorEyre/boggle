package com.team1.cs410.boggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Single player click event
        View singlePlayer = findViewById(R.id.menu_single_player);
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SinglePlayerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Multiplayer touch event
        View multiPlayer = findViewById(R.id.menu_multi_player);
        multiPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TwoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("gameMode", Constants.MODE_BASIC);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        // High scores touch event
        View highScores = findViewById(R.id.menu_high_scores);
        highScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, HighScoresActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name","default");
                bundle.putInt("score", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

//    // Player selects New Game from menu
//    public void onClickSinglePlayerMultipleLevel (View view) {
//        Intent intent = new Intent(this, SinglePlayerActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    public void onClickTwoPlayerBasic (View view){
//        Intent intent = new Intent(this, TwoPlayerActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt("gameMode", Constants.MODE_BASIC);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//
//    public void onClickTwoPlayerCutthroat (View view) {
//        Intent intent = new Intent(this, TwoPlayerActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt("gameMode", Constants.MODE_CUTTHROAT);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//
//    public void onClickHighScores (View view){
//        Intent intent = new Intent(this, HighScoresActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("name","default");
//        bundle.putInt("score", -1);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onBackPressed () {
        finish();
    }
}
