package com.team1.cs410.boggle;

/**
 * Created by Deven on 1/30/2016.
 */
public class RandomBoardGenerator
{
    private String dice[];
    private char boardarray[][];
    private char randomized[];

    public RandomBoardGenerator()
    {
        dice = new String[16];
        //diceold = new String[16];
        randomized = new char[16];
        boardarray = new char[4][4];
        dice[0] = "AAEEGN";
        dice[1] = "ABBJOO";
        dice[2] = "ACHOPS";
        dice[3] = "AFFKPS";
        dice[4] = "AOOTTW";
        dice[5] = "CIMOTU";
        dice[6] = "DEILRX";
        dice[7] = "DELRVY";
        dice[8] = "DISTTY";
        dice[9] = "EEGHNW";
        dice[10] = "EEINSU";
        dice[11] = "EHRTVW";
        dice[12] = "EIOSST";
        dice[13] = "ELRTTY";
        //dice[14] = "HIMNUQu";
        dice[14] = "HIMNUQ";
        dice[15] = "HLNNRZ";
    }

    public char[][] generateboard()
    {
        //Shuffle the array to shuffle the 'dice location'
        //Random rnd = ThreadLocalRandom.current();

        for(int i= dice.length-1;i>0;i--)
        {
            //int index = rnd.nextInt(i+1);
            int index = (int)(Math.random() * 16);
            String a= dice[index];
            dice[index] = dice[i];
            dice[i] = a;
        }


        //System.out.println("Printing randomized:");

        //from each die randomly choose one letter
        for(int i=0;i<dice.length;i++)
        {
            char[] current = dice[i].toCharArray();
            int rand = (int)(Math.random() * dice[i].length());
            //System.out.println(current[rand]);
            randomized[i]=current[rand];
        }

        //convert the array of random letters into 4x4
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                boardarray[i][j]=  randomized[4*i+j];
            }
        }
        return boardarray;
    }
}
