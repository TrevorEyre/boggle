package com.team1.cs410.boggle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;

public class HighScoresActivity extends AppCompatActivity {
    //SharedPreferences scoredata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SharedPreferences.Editor editor = scoredata.edit();
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        int score = bundle.getInt("score");
        if(name.isEmpty())
        {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context,"You did not enter your name. Score will not be recorded!",Toast.LENGTH_LONG);
            toast.show();
        }
        else if(name.equals("default")==false && score !=-1)
        {
            updatescores(name, score);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

    }
    public void updatescores(String name, int score)
    {
        //To do: Implement persistent scores, check maximum and update
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        String text = name + Integer.toString(score);
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
