package com.team1.cs410.boggle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class HighScoresActivity extends AppCompatActivity {

    private int[] scores= new int[5];
    private String[] names= new String[5];
    TextView tview1,tview2,tview3,tview4,tview5,tview6,tview7,tview8,tview9,tview10;
    SharedPreferences prefs = null;
    String filename = "myfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        String readdata = "";
        prefs=getSharedPreferences("firstcheck",Context.MODE_PRIVATE);

        //Get the intent from previous activity
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        int score = bundle.getInt("score");
        String wordsFound = bundle.getString("wordsFound");
        String wordsNotFound = bundle.getString("wordsNotFound");

        if(prefs.getBoolean("firstrun",true)) //Check if the app is on it's first run ever on this device
        {//If it is, create the file and write default values to it.
            Log.d("SharedPrefs","This is the first run");
            FileOutputStream outputStream1;
            try {
                String defaultnames = "Player1 Player2 Player3 Player4 Player5";
                String defaultscores = "0 0 0 0 0";
                outputStream1 = openFileOutput(filename,MODE_PRIVATE);
                outputStream1.write(defaultnames.getBytes());
                outputStream1.write("&".getBytes());
                outputStream1.write(defaultscores.getBytes());
                outputStream1.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            prefs.edit().putBoolean("firstrun",false).commit();
        }

        else if(name.equals("default") && score == -1) //If this activity is called from main activity then do these things
        {
            readdata=readfile(); //Read data from file
            Log.d("Read from file",readdata);

            //Load data into corresponding arrays from file
            String readsplit[] = readdata.split("&");
            String allnames[] = readsplit[0].split(" ");
            String allscores[] = readsplit[1].split(" ");
           for(int i=0;i<allnames.length;i++)
            {
                names[i]=allnames[i];
            }
            for(int i=0;i<allscores.length;i++)
            {
                scores[i]=Integer.parseInt(allscores[i]);
            }
            for(int i=0;i<5;i++)
            {
                Log.d("Everythingloaded",names[i] + scores[i]);
            }

            // Hide words found/not found labels
            findViewById(R.id.yourScore).setVisibility(View.GONE);
            findViewById(R.id.wordsFoundLabel).setVisibility(View.GONE);
            findViewById(R.id.wordsFound).setVisibility(View.GONE);
            findViewById(R.id.wordsNotFoundLabel).setVisibility(View.GONE);
            findViewById(R.id.wordsNotFound).setVisibility(View.GONE);

            //Set the textviews
            TextView tview1,tview2,tview3,tview4,tview5,tview6,tview7,tview8,tview9,tview10;
            tview1=(TextView)this.findViewById(R.id.textname1);
            tview1.setText(names[0]);
            tview2=(TextView)findViewById(R.id.textvalue1);
            tview2.setText(String.valueOf(scores[0]));

            tview3=(TextView)findViewById(R.id.textname2);
            tview3.setText(names[1]);
            tview4=(TextView)findViewById(R.id.textvalue2);
            tview4.setText(String.valueOf(scores[1]));

            tview5=(TextView)findViewById(R.id.textname3);
            tview5.setText(names[2]);
            tview6=(TextView)findViewById(R.id.textvalue3);
            tview6.setText(String.valueOf(scores[2]));

            tview7=(TextView)findViewById(R.id.textname4);
            tview7.setText(names[3]);
            tview8=(TextView)findViewById(R.id.textvalue4);
            tview8.setText(String.valueOf(scores[3]));

            tview9=(TextView)findViewById(R.id.textname5);
            tview9.setText(names[4]);
            tview10=(TextView)findViewById(R.id.textvalue5);
            tview10.setText(String.valueOf(scores[4]));
        }
        else if(!name.equals("default") && score !=-1)
        {
            //loadarrays();
            readdata=readfile(); //Read data from file


            //Load data into corresponding arrays from file
            String readsplit[] = readdata.split("&");
            String allnames[] = readsplit[0].split(" ");
            String allscores[] = readsplit[1].split(" ");
            for(int i=0;i<allnames.length;i++)
            {
                names[i]=allnames[i];
            }
            for(int i=0;i<allscores.length;i++)
            {
                scores[i]=Integer.parseInt(allscores[i]);
            }
            updatescores(name, score);

            // Update your score label
            TextView yourScore = (TextView)findViewById(R.id.yourScore);
            yourScore.setText("Score: " + score);

            // Print found words and unfound words
            TextView wordsFoundTextView = (TextView)findViewById(R.id.wordsFound);
            TextView wordsNotFoundTextView = (TextView)findViewById(R.id.wordsNotFound);
            wordsFoundTextView.setText(wordsFound);
            wordsNotFoundTextView.setText(wordsNotFound);
        }

    }
    public void updatescores(String name, int score)
    {
        //Context context = getApplicationContext();
        //int duration = Toast.LENGTH_SHORT;
        //String text = name + Integer.toString(score);
        //Toast toast = Toast.makeText(context, text, duration);
        //toast.show();

        //Copy everything into a new array with new score appended
        int temparray[] = new int[6];
        String tempnames[] = new String[6];
        for(int i=0;i<4;i++)
        {
            temparray[i]=this.scores[i];
            tempnames[i]=this.names[i];
        }
        temparray[5]=score;
        tempnames[5]=name;

        //Sort the new array of 6 names and scores
        for(int i=0;i<5;i++)
        {
            for(int j=0;j<5;j++)
            {
                if(temparray[j]<temparray[j+1])
                {
                    int temp = temparray[j+1];
                    temparray[j+1]=temparray[j];
                    temparray[j]=temp;

                    String tempstr = tempnames[j+1];
                    tempnames[j+1]=tempnames[j];
                    tempnames[j]=tempstr;
                }
            }

        }

        //Copy everything back
        for(int i=0;i<4;i++)
        {
            this.names[i]=tempnames[i];
            this.scores[i]=temparray[i];
        }

        //Set the views accordingly
        TextView tview1,tview2,tview3,tview4,tview5,tview6,tview7,tview8,tview9,tview10;
        tview1=(TextView)this.findViewById(R.id.textname1);
        tview1.setText(names[0]);
        tview2=(TextView)findViewById(R.id.textvalue1);
        tview2.setText(String.valueOf(scores[0]));

        tview3=(TextView)findViewById(R.id.textname2);
        tview3.setText(names[1]);
        tview4=(TextView)findViewById(R.id.textvalue2);
        tview4.setText(String.valueOf(scores[1]));

        tview5=(TextView)findViewById(R.id.textname3);
        tview5.setText(names[2]);
        tview6=(TextView)findViewById(R.id.textvalue3);
        tview6.setText(String.valueOf(scores[2]));

        tview7=(TextView)findViewById(R.id.textname4);
        tview7.setText(names[3]);
        tview8=(TextView)findViewById(R.id.textvalue4);
        tview8.setText(String.valueOf(scores[3]));

        tview9=(TextView)findViewById(R.id.textname5);
        tview9.setText(names[4]);
        tview10=(TextView)findViewById(R.id.textvalue5);
        tview10.setText(String.valueOf(scores[4]));

        //Write the new scores to file
        FileOutputStream outputStream2;
        try{
            outputStream2 = openFileOutput(filename,MODE_PRIVATE);
            for(int i=0;i<5;i++)
            {
                outputStream2.write(names[i].getBytes());
                if(i<4)
                    outputStream2.write(" ".getBytes());
            }
            outputStream2.write("&".getBytes());
            for(int i=0;i<5;i++)
            {
                outputStream2.write(Integer.toString(scores[i]).getBytes());
                if(i<4)
                    outputStream2.write(" ".getBytes());
            }
            outputStream2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readfile()//Reads a text file and returns the string read from it
    {
        FileInputStream inputStream;
        String toreturn = "";
        byte[] readdata = new byte[3000];
        int nread;
        int total=0;
        try{
            inputStream = openFileInput(filename);
            while((nread = inputStream.read(readdata))!=-1)
            {

                total += nread;
            }
            if(total!=0)
            {
                byte[] correctlengthbuf = new byte[total];
                for(int i=0;i<total;i++)
                {
                    correctlengthbuf[i]=readdata[i];
                }
                toreturn = new String(correctlengthbuf);
                Log.d("from file",toreturn);
            }

            inputStream.close();
            Log.d("Read Bytes: ",total + "");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("File handling","Could not find file so creating new one");

        } catch (IOException e) {
            e.printStackTrace();
            try {
                FileOutputStream outputStream;
                outputStream = openFileOutput(filename,getApplicationContext().MODE_PRIVATE);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

        }
        return toreturn;
    }


    @Override
    public void onBackPressed()//Need this to capture the 'back' button on the phone
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
