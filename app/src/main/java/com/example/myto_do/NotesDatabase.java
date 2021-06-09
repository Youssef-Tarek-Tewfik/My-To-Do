package com.example.myto_do;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class NotesDatabase extends SQLiteOpenHelper {

    final static String dbName = "notesDB";
    SQLiteDatabase database;

    public NotesDatabase(Context context) {
        super(context, dbName, null, 1);
    }

    // notes table methods
    public int addNote(String name, String password) {
        ContentValues row = new ContentValues();
        row.put("name", name);
        row.put("password", password);
        database = getWritableDatabase();
        long id = database.insert("notes", null, row);
        database.close();
        return (int)id;
    }

    public void deleteNote(int id) {
        database = getWritableDatabase();
        database.delete("items", "noteID = '" + id + "'", null);
        database.delete("notes", "noteID = '" + id + "'", null);
        database.close();
    }

    public void updateNote(int id, String newName, String newPassword) {
        ContentValues row = new ContentValues();
        row.put("name", newName);
        row.put("password", newPassword);
        final String[] args = {String.valueOf(id)};
        database = getWritableDatabase();
        database.update("notes", row, "noteID like ?", args);
        database.close();
    }

    public Cursor getAllNotes() {
        database = getReadableDatabase();
        String[] rowDetails = {"noteID", "name", "password"};
        Cursor cursor = database.query("notes", rowDetails, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        database.close();
        return cursor;
    }

    public Cursor getNote(int noteID) {
        database = getReadableDatabase();
        String[] args = {String.valueOf(noteID)};
        Cursor cursor = database.rawQuery("select * from notes where noteID like ?", args);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        database.close();
        return cursor;
    }

    // items methods
    public int addItem(int noteID, String content, String status, String reminder) {
        ContentValues row = new ContentValues();
        row.put("noteID", noteID);
        row.put("content", content);
        row.put("status", status);
        row.put("reminder", reminder);
        database = getWritableDatabase();
        long id = database.insert("items", null, row);
        database.close();
        return (int)id;
    }

    public void deleteItem(int itemID) {
        database = getWritableDatabase();
        database.delete("items", "itemID = '" + itemID + "'", null);
        database.close();
    }

    public void updateItem(int itemID, String newContent, String newStatus, String newReminder) {
        ContentValues row = new ContentValues();
        row.put("content", newContent);
        row.put("status", newStatus);
        row.put("reminder", newReminder);
        final String[] args = {String.valueOf(itemID)};
        database = getWritableDatabase();
        database.update("items", row, "itemID like ?", args);
        database.close();
    }

    public Cursor getAllItems(int noteID) {
        database = getReadableDatabase();
        final String[] args = {String.valueOf(noteID)};
        Cursor cursor = database.rawQuery("select * from items where noteID like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        database.close();
        return cursor;
    }

    public Cursor getTodaysReminders(){

        database = getReadableDatabase();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String dayString = String.valueOf(day);
        String monthString = String.valueOf(++month);
        if (day < 10)
            dayString = "0" + dayString;
        if (month < 10)
            monthString = 0 + monthString;
        String date = dayString + "/" + monthString + "/" + year;

        final String[] args = {date};
        Cursor cursor = database.rawQuery("select content from items where reminder like ?", args);
        if (cursor != null)
            cursor.moveToFirst();
        database.close();
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table notes(noteID integer primary key autoincrement, name text not null, password text)");
        sqLiteDatabase.execSQL("create table items(" +
                "itemID integer primary key autoincrement," +
                "content text not null," +
                "status text not null," +
                "reminder text," +
                "noteID integer," +
                "foreign key(noteID) references notes (noteID))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists items");
        sqLiteDatabase.execSQL("drop table if exists notes");
        onCreate(sqLiteDatabase);
    }
}
