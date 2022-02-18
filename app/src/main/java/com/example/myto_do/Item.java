package com.example.myto_do;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class Item extends LinearLayout {

    private int itemID;
    private String currentContent;
    private String currentStatus;
    private String currentReminder;
    private NotesDatabase database;
    private EditText editText;
    private ImageView imageView;
    private TextView textView;
    private ImageButton imageButton;

    @SuppressLint("NewApi")
    public Item(final Context context, final int itemID, final String content, String status, String reminder,
                final NoteActivity.CallableDatePicker dateSetter, final LinearLayout parent) {
        super(context);

        setLayoutParams(new LinearLayout.LayoutParams(1100, 200));
        setGravity(Gravity.CENTER);

        this.itemID = itemID;
        this.currentContent = content;
        this.currentStatus = status;
        this.currentReminder = reminder;
        this.database = new NotesDatabase(context);

        imageButton = new ImageButton(context);
        imageButton.setImageResource(android.R.drawable.presence_busy);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageButton.setLayoutParams(new LayoutParams(90, 90));
        imageButton.setPadding(0, 0, 16, 0);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDestruct(parent);
            }
        });
        addView(imageButton);

        editText = new EditText(getContext());
        editText.setText(content);
        editText.setTextSize(20);
        editText.setGravity(Gravity.CENTER);
        editText.setMinWidth(400);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
        editText.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                currentContent = editText.getText().toString();
                updateDatabase();
            }
        });
        addView(editText);

        imageView = new ImageView(getContext());
        if (status.equals("unfinished"))
            imageView.setImageResource(android.R.drawable.checkbox_off_background);
        else
            imageView.setImageResource(android.R.drawable.checkbox_on_background);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LayoutParams(220, 180));
        imageView.setPadding(50, 0, 0, 0);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        addView(imageView);

        textView = new TextView(getContext());
        textView.setText(reminder);
        textView.setPadding(10, 0, 0, 0);
        textView.setTextSize(13);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dateSetter.setTextView(textView);
                    dateSetter.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                currentReminder = textView.getText().toString();
                updateDatabase();
            }
        });
        addView(textView);
    }

    private void toggle() {
        if (currentStatus.equals("finished")) {
            imageView.setImageResource(android.R.drawable.checkbox_off_background);
            currentStatus = "unfinished";
        }
        else {
            imageView.setImageResource(android.R.drawable.checkbox_on_background);
            currentStatus = "finished";
        }
        updateDatabase();
    }

    private void updateDatabase() {
        database.updateItem(itemID, currentContent, currentStatus, currentReminder);
    }

    private void selfDestruct(LinearLayout parent) {
        parent.removeView(this);
        database.deleteItem(itemID); // Order?
    }
}
