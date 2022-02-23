package com.ycdm.myToDo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        int width = parent.getWidth();

        setLayoutParams(new LinearLayout.LayoutParams(width, (int)(width * 0.15)));
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
        imageButton.setLayoutParams(new LayoutParams((int)(width * 0.06), (int)(width * 0.05)));
        imageButton.setPadding(0, 0, (int)(width * 0.01), 0);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDestruct(parent);
            }
        });
        addView(imageButton);

        int editTextFontSize = Math.max((int)(width * 0.017), 18);
        editText = new EditText(getContext());
        editText.setText(content);
        editText.setTextSize(editTextFontSize);
        editText.setGravity(Gravity.CENTER);
        editText.setMinWidth((int)(width * 0.5));
        editText.setMaxWidth((int)(width * 0.5));
        //editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter((int)(editTextFontSize * 0.5)) }); // max chars
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
        imageView.setLayoutParams(new LayoutParams((int)(width * 0.13), (int)(width * 0.12)));
        imageView.setPadding((int)(width * 0.01), 0, 0, 0);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        addView(imageView);

        textView = new TextView(getContext());
        textView.setText(reminder);
        textView.setPadding((int)(width * 0.01), 0, 0, 0);
        textView.setTextSize((int)(editTextFontSize * 0.7));
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
        database.deleteItem(itemID);
    }
}
