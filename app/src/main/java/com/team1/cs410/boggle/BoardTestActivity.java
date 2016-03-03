package com.team1.cs410.boggle;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BoardTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_test);

        final GridView gridView = (GridView)findViewById(R.id.gridboard);
        gridView.setAdapter(new MyAdapter(this));

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View child = null;
                SquareImageView background = null;
                TextView tv = null;
                int posX = (int) event.getX();
                int posY = (int) event.getY();

                int position = gridView.pointToPosition(posX, posY);
                Log.d("BoardTest", "position: " + position);
                if (position != -1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            child = gridView.getChildAt(position);
                            tv = (TextView) child.findViewById(R.id.letter);
//                            String letter = tv.getText().toString();
                            if (posX > child.getLeft() + tv.getLeft() && posX < child.getRight() + tv.getRight()
                                    && posY > child.getTop() + tv.getTop() && posY < child.getBottom() + tv.getBottom()) {
                                background = (SquareImageView) child.findViewById(R.id.background);
                                background.setImageResource(R.drawable.dice_highlight);
                                tv.setTextColor(Color.rgb(3, 169, 244));
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            for (int i = 0; i < gridView.getChildCount(); ++i) {
                                child = gridView.getChildAt(i);
                                tv = (TextView) child.findViewById(R.id.letter);
                                background = (SquareImageView) child.findViewById(R.id.background);
                                background.setImageResource(R.drawable.dice_background);
                                tv.setTextColor(Color.rgb(153, 153, 153));
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            items.add(new Item("A", R.drawable.dice_background));
            items.add(new Item("B", R.drawable.dice_background));
            items.add(new Item("C", R.drawable.dice_background));
            items.add(new Item("A", R.drawable.dice_background));
            items.add(new Item("B", R.drawable.dice_background));
            items.add(new Item("C", R.drawable.dice_background));
            items.add(new Item("A", R.drawable.dice_background));
            items.add(new Item("B", R.drawable.dice_background));
            items.add(new Item("C", R.drawable.dice_background));
            items.add(new Item("A", R.drawable.dice_background));
            items.add(new Item("B", R.drawable.dice_background));
            items.add(new Item("C", R.drawable.dice_background));
            items.add(new Item("C", R.drawable.dice_background));
            items.add(new Item("A", R.drawable.dice_background));
            items.add(new Item("B", R.drawable.dice_background));
            items.add(new Item("C", R.drawable.dice_background));
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;

            if (v == null) {
                v = inflater.inflate(R.layout.dice, viewGroup, false);
                v.setTag(R.id.background, v.findViewById(R.id.background));
                v.setTag(R.id.letter, v.findViewById(R.id.letter));
            }

            picture = (ImageView)v.getTag(R.id.background);
            name = (TextView)v.getTag(R.id.letter);

            Item item = (Item)getItem(i);

            picture.setImageResource(item.drawableId);
            name.setText(item.name);

            return v;
        }

        private class Item
        {
            final String name;
            final int drawableId;

            Item(String name, int drawableId)
            {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }

}
