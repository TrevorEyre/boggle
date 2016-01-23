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

import java.util.ArrayList;

public class GameBoard extends AppCompatActivity {

    //Button disabledbuttons[];
    ArrayList<Button> disabledbuttons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        disabledbuttons = new ArrayList<>();
        Intent intent = getIntent();
    }

    //handle the click on board buttons
    public void boardbuttonclick (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn = (Button) findViewById(view.getId());
        textinputview.append(btn.getText());
        btn.setEnabled(false);
        disabledbuttons.add(btn);

    }

    //handle the click on 'clear'
    public void buttonclearclick (View view){
        TextView inputtextview = (TextView) findViewById(R.id.textviewinputword);
        inputtextview.setText("Your Word: ");
        Button btn;
        int i, arraylistsize;
        arraylistsize = disabledbuttons.size();
        for (i=0;i<arraylistsize;i++)
        {
            btn = disabledbuttons.get(i);
            btn.setEnabled(true);
        }
        disabledbuttons.clear();
    }


}
