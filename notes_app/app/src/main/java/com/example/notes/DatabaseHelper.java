package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private Context context;

    private static final String DATABASE_NAME = "notes.db";
    private static int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "notes_data";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NOTE = "note_text";
    private static final String COLUMN_DATE_EDITED = "date_edited";
    private static final String COLUMN_DATE_CREATED = "date_created";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE + " TEXT NOT NULL, " +
                COLUMN_DATE_EDITED + " TIMESTAMP, " +
                COLUMN_DATE_CREATED + " TIMESTAMP" +
                ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllNotes() {
        // orders the rows by date and time
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE_EDITED + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        // checks if the database is empty or not
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

    public void addNote(String note, String date) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NOTE, note);
        cv.put(COLUMN_DATE_EDITED, date);
        cv.put(COLUMN_DATE_CREATED, date);

        // returned value determines if it was inserted correctly or not
        // -1 means it was not inserted correctly
        long result = db.insert(TABLE_NAME, null, cv);

        if(result != -1) {
            Log.i(TAG, "addNote: successfully inserted into database");
            Toast.makeText(context, "New note was successfully saved", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d(TAG, "addNote: failed to insert into database");
            Toast.makeText(context, "New note was not able to be saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateNote(String row_id, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NOTE, note);
        cv.put(COLUMN_DATE_EDITED, date);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if(result != -1) {
            Log.d(TAG, "updateNote: update successful");
            Toast.makeText(context, "Saved changes!", Toast.LENGTH_SHORT).show();

        }
        else {
            Log.d(TAG, "updateNote: update failed");
            Toast.makeText(context, "Changes we not able to be saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteNote(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if(result == -1){
            Log.d(TAG, "deleteRow: failed to delete row");
        }
        else {
            Log.d(TAG, "deleteRow: successfully deleted row");
            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor getRow(String row_id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null) {
            cursor = db.rawQuery(query, new String[]{row_id});
        }

        if(cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public String getLastID() {
        String query = "SELECT MAX(" + COLUMN_ID + ") AS " + COLUMN_ID + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        String id = "-1";

        if(db != null) {
            cursor = db.rawQuery(query, null);
        }

        if(cursor != null) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
        }

        return id;
    }
}
