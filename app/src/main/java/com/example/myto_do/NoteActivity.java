package com.example.myto_do;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.concurrent.Callable;

public class NoteActivity extends AppCompatActivity {

    public class CallableDatePicker implements Callable<Void> {
        private TextView textView;

        public CallableDatePicker() {
            this.textView = null;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        @Override
        public Void call() throws Exception {
            datePicker(textView);
            return null;
        }
    }

    private LinearLayout itemLayout;
    private String title;
    private String password;
    private TextView titleTextView;
    private int noteID;
    private CallableDatePicker picker;
    private NotesDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        picker = new CallableDatePicker();
        database = new NotesDatabase(this);
        noteID = getIntent().getExtras().getInt("noteID");
        itemLayout = findViewById(R.id.itemsLayout);

        if (noteID != -1) {
            Cursor cursor = database.getNote(noteID);
            title = cursor.getString(1);
            password = cursor.getString(2);
            initializeItems();
        }
        else {
            title = "New Note";
            password = "";
            noteID = database.addNote(title, password);
        }

        titleTextView = findViewById(R.id.noteTitle);
        titleTextView.setText(title);
        titleTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
            editTitle();
            return true;
            }
        });

        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                addNewItem();
            }
        });

        ImageButton lockButton = findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPassword();
            }
        });

        ImageButton deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });
    }

    private void addNewItem() {
        String content = "New Item";
        String status = "unfinished";
        String reminder = "set reminder";
        int itemID = database.addItem(noteID, content, status, reminder);
        Item item = new Item(getApplicationContext(), itemID, content, status, reminder, picker, itemLayout);
        itemLayout.addView(item);
    }

    private void editTitle() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("New Note Title:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (!value.isEmpty()) {
                    titleTextView.setText(value);
                    title = value;
                    database.updateNote(noteID, title, password);
                }
                return;
            }
        });
        alert.show();
    }

    private void editPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Set Password:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                password = value;
                database.updateNote(noteID, title, password);
                return;
            }
        });
        alert.show();
    }

    private void confirmDelete() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Are you sure you want to delete?");
        alert.setMessage(title + " will be deleted and there's no going back");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                database.deleteNote(noteID);
                finish();
                return;
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                alert.
            }
        });

        alert.show();
    }

    private void datePicker(final TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dayString = String.valueOf(dayOfMonth);
                        String monthString = String.valueOf(++monthOfYear);
                        if (dayOfMonth < 10)
                            dayString = "0" + dayString;
                        if (monthOfYear < 10)
                            monthString = 0 + monthString;
                        String date = dayString + "/" + monthString + "/" + year;
                        textView.setText(date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void initializeItems() {
        Cursor cursor = database.getAllItems(noteID);
        while (!cursor.isAfterLast()) {
            final int itemID = cursor.getInt(0);
            String content = cursor.getString(1);
            String status = cursor.getString(2);
            String reminder = cursor.getString(3);
            final Item item = new Item(this, itemID, content, status, reminder, picker, itemLayout);
            itemLayout.addView(item);
            cursor.moveToNext();
        }
    }
}
