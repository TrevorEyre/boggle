package com.team1.cs410.boggle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        Intent intent = getIntent();
    }

    public void boardbuttonclick (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        //Button btn11 = (Button) findViewById(R.id.button11);
        //textinputview.append(view.getId());
        Button btn = (Button) findViewById(view.getId());
        textinputview.append(btn.getText());

    }


    public void buttonclearclick (View view){
        TextView inputtextview = (TextView) findViewById(R.id.textviewinputword);
        inputtextview.setText("Your Word: ");
    }


}
