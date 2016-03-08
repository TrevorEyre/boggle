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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int backButtonCount =0;
    // Tag for debug statements
    private static final String TAG = "MainActivity";

    // Constants that indicate the current menu
    private static final int MENU_MAIN = 0;
    private static final int MENU_MULTIPLAYER = 1;
    private static final int MENU_MULTIPLAYER_INFO = 2;
    private static final int MENU_CREDITS = 3;

    // Menu views
    private View menuMain;
    private View menuMultiplayer;
    private View menuMultiplayerInformation;
    private View menuCredits;

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
        View optSinglePlayer = findViewById(R.id.menu_main_single_player);
        View optMultiplayer = findViewById(R.id.menu_main_multiplayer);
        View optHighScores = findViewById(R.id.menu_main_high_scores);
        View optCredits = findViewById(R.id.menu_main_credits);
        menuMultiplayer = findViewById(R.id.menu_multiplayer);
        View optMultiplayerBasic = findViewById(R.id.menu_multiplayer_basic);
        View optMultiplayerCutthroat = findViewById(R.id.menu_multiplayer_cutthroat);
        View optMultiplayerMultiRound = findViewById(R.id.menu_multiplayer_multi_round);
        View optMultiplayerMultiRoundCutthroat = findViewById(R.id.menu_multiplayer_multi_round_cutthroat);
        View optMultiplayerInfo = findViewById(R.id.menu_multiplayer_info);
        View optMultiplayerBack = findViewById(R.id.menu_multiplayer_back);
        menuMultiplayerInformation = findViewById(R.id.menu_multiplayer_information);
        View optMultiplayerInformationBack = findViewById(R.id.menu_multiplayer_information_back);
        menuCredits = findViewById(R.id.menu_credits);
        View optCreditsBack = findViewById(R.id.menu_credits_back);

        // Main menu
        // Single player click event
        optSinglePlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
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
                backButtonCount =0;
                crossfade(menuMain, menuMultiplayer, MENU_MULTIPLAYER);
                return false;
            }
        });

        // High scores click event
        optHighScores.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                Intent intent = new Intent(activity, HighScoresActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "default");
                bundle.putInt("score", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Credits click event
        optCredits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                crossfade(menuMain, menuCredits, MENU_CREDITS);
                return false;
            }
        });

        // Credits back button click event
        optCreditsBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                crossfade(menuCredits, menuMain, MENU_MAIN);
                return false;
            }
        });

        // Multiplayer menu
        // Basic mode click event
        optMultiplayerBasic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
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
                backButtonCount =0;
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
                backButtonCount =0;
                Intent intent = new Intent(activity, TwoPlayerMultiRound.class);
                Bundle bundle = new Bundle();
                bundle.putInt("gameMode", Constants.MODE_BASIC);
                bundle.putInt("round", 1);
                bundle.putInt("timer", 120000);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Multi-round cutthroat mode click event
        optMultiplayerMultiRoundCutthroat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                Intent intent = new Intent(activity, TwoPlayerMultiRound.class);
                Bundle bundle = new Bundle();
                bundle.putInt("gameMode", Constants.MODE_CUTTHROAT);
                bundle.putInt("round", 1);
                bundle.putInt("timer",120000);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Multiplayer information touch event
        optMultiplayerInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                crossfade(menuMultiplayer, menuMultiplayerInformation, MENU_MULTIPLAYER_INFO);
                return false;
            }
        });

        // Multiplayer back button touch event
        optMultiplayerBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                crossfade(menuMultiplayer, menuMain, MENU_MAIN);
                return false;
            }
        });

        // Multiplayer information back button touch event
        optMultiplayerInformationBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                backButtonCount =0;
                crossfade(menuMultiplayerInformation, menuMultiplayer, MENU_MULTIPLAYER);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (currentMenu) {
            case MENU_MAIN:
//                finish();
                if(backButtonCount >= 1)
                {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                    backButtonCount++;
                }
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
    }

}
