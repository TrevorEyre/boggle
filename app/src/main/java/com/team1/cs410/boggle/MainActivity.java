package com.team1.cs410.boggle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Player selects New Game from menu
    public void onClickSinglePlayerMultipleLevel (View view) {
        Intent intent = new Intent(this, SinglePlayerGame.class);
        startActivity(intent);
        finish();
    }
    public void onClickTwoPlayerBasic (View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Host or join?")
                .setPositiveButton("Host", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),TwoPlayerGameBasic.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("gametype","host");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),TwoPlayerGameBasic.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("gametype","join");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
        //builder.create();
        builder.show();

        /*Intent intent = new Intent(this, TwoPlayerGameBasic.class);
        startActivity(intent);*/
    }

    public void onClickHighScores (View view){
        Intent intent = new Intent(this, HighScoresActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name","default");
        bundle.putInt("score", -1);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        //Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_HOME);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        finish();
        //finish();
    }
}
