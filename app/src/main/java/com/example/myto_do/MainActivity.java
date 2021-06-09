package com.example.myto_do;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getTodaysReminders();
        refreshNotesList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNotesList();
    }

    private void navigateToNote(int noteID, final String password) {
        if (!password.isEmpty()) {
            authenticate(noteID, password);
            return;
        }
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("noteID", noteID);
        startActivity(intent);
    }

    private void authenticate(final int noteID, final String password) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Note is password locked");
        alert.setMessage("Please enter password");

        final EditText input = new EditText(this);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alert.setView(input);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.equals(password)) {
                    navigateToNote(noteID, "");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_LONG).show();
                }
                return;
            }
        });

        alert.show();
    }

    private void refreshNotesList() {
        LinearLayout layout = findViewById(R.id.scrollViewLayout);
        NotesDatabase database = new NotesDatabase(getApplicationContext());
        layout.removeAllViews();

        try {
            Cursor cursor = database.getAllNotes();
            while (!cursor.isAfterLast()) {
                final int id = cursor.getInt(0);
                String name = cursor.getString(1);
                final String password = cursor.getString(2);
                Note note = new Note(this, name);
                note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navigateToNote(id, password);
                    }
                });
                layout.addView(note);
                cursor.moveToNext();
            }
        }
        catch (Exception e) {
            System.out.println("No Notes Found");
        }

        Note newNote = new Note(this, "");
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToNote(-1, "");
            }
        });
        layout.addView(newNote);
    }

    private void getTodaysReminders() {
        NotesDatabase database = new NotesDatabase(getApplicationContext());
        Cursor c = database.getTodaysReminders();
        String message = "";

        while(!c.isAfterLast()) {
            message += c.getString(0) + "\n";
            c.moveToNext();
        }
        if(!message.equals("")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Today's reminders");
            alert.setMessage(message);
            alert.show();
        }
    }
}
