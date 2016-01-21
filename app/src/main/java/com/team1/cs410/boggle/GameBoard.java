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

    public void button11click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn11 = (Button) findViewById(R.id.button11);
        textinputview.append(btn11.getText());

    }
    public void button12click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn12 = (Button) findViewById(R.id.button12);
        textinputview.append(btn12.getText());

    }
    public void button13click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn13 = (Button) findViewById(R.id.button13);
        textinputview.append(btn13.getText());

    }
    public void button14click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn14 = (Button) findViewById(R.id.button14);
        textinputview.append(btn14.getText());

    }

    public void buttonclearclick (View view){
        TextView inputtextview = (TextView) findViewById(R.id.textviewinputword);
        inputtextview.setText("Your Word: ");
    }

    public void button21click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn21 = (Button) findViewById(R.id.button21);
        textinputview.append(btn21.getText());

    }

    public void button22click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn22 = (Button) findViewById(R.id.button22);
        textinputview.append(btn22.getText());

    }
    public void button23click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn23 = (Button) findViewById(R.id.button23);
        textinputview.append(btn23.getText());

    }
    public void button24click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn24 = (Button) findViewById(R.id.button24);
        textinputview.append(btn24.getText());

    }
    public void button31click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn31 = (Button) findViewById(R.id.button31);
        textinputview.append(btn31.getText());

    }
    public void button32click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn32 = (Button) findViewById(R.id.button32);
        textinputview.append(btn32.getText());

    }
    public void button33click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn33 = (Button) findViewById(R.id.button33);
        textinputview.append(btn33.getText());

    }
    public void button34click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn34 = (Button) findViewById(R.id.button34);
        textinputview.append(btn34.getText());

    }
    public void button41click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn41 = (Button) findViewById(R.id.button41);
        textinputview.append(btn41.getText());

    }
    public void button42click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn42 = (Button) findViewById(R.id.button42);
        textinputview.append(btn42.getText());

    }
    public void button43click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn43 = (Button) findViewById(R.id.button43);
        textinputview.append(btn43.getText());

    }
    public void button44click (View view) {
        TextView textinputview = (TextView) findViewById(R.id.textviewinputword);
        Button btn44 = (Button) findViewById(R.id.button44);
        textinputview.append(btn44.getText());

    }


}
