package com.team1.cs410.boggle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    // Tag for debug statements
    private static final String TAG = "MainActivity";

    // Constants that indicate the current menu
    private static final int MENU_MAIN = 0;
    private static final int MENU_MULTIPLAYER = 1;
    private static final int MENU_MULTIPLAYER_INFO = 2;
    private static final int MENU_CREDITS = 3;

    // Menu views
    private View menuMain;
    private View optSinglePlayer;
    private View optMultiplayer;
    private View optHighScores;
    private View optCredits;
    private View menuMultiplayer;
    private View optMultiplayerBasic;
    private View optMultiplayerCutthroat;
    private View optMultiplayerMultiRound;
    private View optMultiplayerInfo;
    private View optMultiplayerBack;
    private View menuMultiplayerInformation;
    private View optMultiplayerInformationBack;
    private View menuCredits;
    private View optCreditsBack;

    // Member fields
    Activity activity;
    private int animationDuration;
    private int currentMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Initialize variables
        animationDuration = 500; //getResources().getInteger(android.R.integer.config_shortAnimTime);
        currentMenu = MENU_MAIN;

        // Initialize menu views
        menuMain = findViewById(R.id.menu_main);
        optSinglePlayer = findViewById(R.id.menu_main_single_player);
        optMultiplayer = findViewById(R.id.menu_main_multiplayer);
        optHighScores = findViewById(R.id.menu_main_high_scores);
        optCredits = findViewById(R.id.menu_main_credits);
        menuMultiplayer = findViewById(R.id.menu_multiplayer);
        optMultiplayerBasic = findViewById(R.id.menu_multiplayer_basic);
        optMultiplayerCutthroat = findViewById(R.id.menu_multiplayer_cutthroat);
        optMultiplayerMultiRound = findViewById(R.id.menu_multiplayer_multi_round);
        optMultiplayerInfo = findViewById(R.id.menu_multiplayer_info);
        optMultiplayerBack = findViewById(R.id.menu_multiplayer_back);
        menuMultiplayerInformation = findViewById(R.id.menu_multiplayer_information);
        optMultiplayerInformationBack = findViewById(R.id.menu_multiplayer_information_back);
        menuCredits = findViewById(R.id.menu_credits);
        optCreditsBack = findViewById(R.id.menu_credits_back);

        // Main menu
        // Single player click event
        optSinglePlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(activity, SinglePlayerActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Multiplayer click event
        optMultiplayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                crossfade(menuMain, menuMultiplayer, MENU_MULTIPLAYER);
                return false;
            }
        });

        // High scores click event
        optHighScores.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Intent intent = new Intent(activity, HighScoresActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("name", "default");
//                bundle.putInt("score", -1);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                finish();
//                return false;

                startActivity(new Intent(activity, BoardTestActivity.class));
                finish();
                return false;
            }
        });

        // Credits click event
        optCredits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                crossfade(menuMain, menuCredits, MENU_CREDITS);
                return false;
            }
        });

        // Credits back button click event
        optCreditsBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                crossfade(menuCredits, menuMain, MENU_MAIN);
                return false;
            }
        });

        // Multiplayer menu
        // Basic mode click event
        optMultiplayerBasic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(activity, TwoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("gameMode", Constants.MODE_BASIC);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Cutthroat mode click event
        optMultiplayerCutthroat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(activity, TwoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("gameMode", Constants.MODE_CUTTHROAT);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Multi-round mode click event
        optMultiplayerMultiRound.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        // Multiplayer information touch event
        optMultiplayerInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                crossfade(menuMultiplayer, menuMultiplayerInformation, MENU_MULTIPLAYER_INFO);
                return false;
            }
        });

        // Multiplayer back button touch event
        optMultiplayerBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                crossfade(menuMultiplayer, menuMain, MENU_MAIN);
                return false;
            }
        });

        // Multiplayer information back button touch event
        optMultiplayerInformationBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                crossfade(menuMultiplayerInformation, menuMultiplayer, MENU_MULTIPLAYER);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (currentMenu) {
            case MENU_MAIN:
                finish();
                break;
            case MENU_MULTIPLAYER:
                crossfade(menuMultiplayer, menuMain, MENU_MAIN);
                break;
            case MENU_MULTIPLAYER_INFO:
                crossfade(menuMultiplayerInformation, menuMultiplayer, MENU_MULTIPLAYER);
                break;
            case MENU_CREDITS:
                crossfade(menuCredits, menuMain, MENU_MAIN);
                break;
        }
    }

    private void crossfade (View hideView, View showView, int menu) {

        currentMenu = menu;
        final View hide = hideView;
        final View show = showView;

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        show.setAlpha(0f);
        show.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.

        show.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        // Animate the "hide" view to 0% opacity. After the animation ends, set its visibility
        // to GONE as an optimization step (it won't participate in layout passes, etc.)
        hide.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        hide.setVisibility(View.GONE);
                    }
                });


//    public void onClickTwoPlayerCutthroat(View view){
//        Intent intent = new Intent(this, TwoPlayerCutThroat.class);
//        startActivity(intent);
//    }

    public void onClickTwoPlayerMultiRound(View view)
    {
        Intent intent = new Intent(this, TwoPlayerMultiRound.class);
        Bundle bundle = new Bundle();
        bundle.putInt("gameMode", Constants.MODE_BASIC);
        bundle.putInt("round",1);
        bundle.putInt("timer",180000);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void onClickTwoPlayerMultiRoundCutThroat(View view)
    {
        Intent intent = new Intent(this, TwoPlayerMultiRound.class);
        Bundle bundle = new Bundle();
        bundle.putInt("gameMode", Constants.MODE_CUTTHROAT);
        bundle.putInt("round",1);
        bundle.putInt("timer",180000);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        finish();
        //finish();
    }
}
