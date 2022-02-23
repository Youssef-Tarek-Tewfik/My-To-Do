package com.ycdm.myToDo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class Note extends TextView {

    public Note(Context context, final String text) {
        super(context);

        System.out.println();
        if (text.isEmpty()) {
            setText("+ Add New Note");
            setTextColor(Color.rgb(0, 0, 139));
        }
        else {
            setTextColor(Color.BLACK);
            setText(text);
        }

        setTextSize(22); // should scale
        setPadding(0,50,0,50);
        setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

    }

}
