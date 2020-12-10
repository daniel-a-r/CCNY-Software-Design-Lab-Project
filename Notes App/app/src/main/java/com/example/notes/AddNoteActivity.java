package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";
    private boolean noteCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Note");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent returnIntent = new Intent();

        EditText editText = findViewById(R.id.editText);
        String noteText = editText.getText().toString();

        if (id == android.R.id.home) {
            if(noteText.isEmpty() && !noteCreated) {
                setResult(MainActivity.RESULT_CANCELED, returnIntent);
            }
            else {
                returnIntent.putExtra("note_operation", "added");
                setResult(MainActivity.RESULT_OK, returnIntent);
            }

            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        else if (id == R.id.save_note) {
            if (noteText.isEmpty()) {
                Toast.makeText(this, "An empty note cannot be saved", Toast.LENGTH_SHORT).show();
            }
            // if a note has note been created, add it to the database
            else if (!noteCreated) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                String currentDate = getCurrentDate();
                dbHelper.addNote(noteText, currentDate);
                noteCreated = true;
            }
            // if a note has been created and saved to the database, update it
            else {
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                String currentDate = getCurrentDate();
                String noteID = dbHelper.getLastID();
                dbHelper.updateNote(noteID, noteText, currentDate);
            }
            return true;
        }
        else if (id == R.id.delete_note) {
            if(noteText.isEmpty() && !noteCreated) {
                setResult(MainActivity.RESULT_CANCELED, returnIntent);
                finish();
            }
            else {
                confirmDeleteDialog();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    private void confirmDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note?");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (noteCreated) {
                    DatabaseHelper dbHelper = new DatabaseHelper(AddNoteActivity.this);
                    String noteID = dbHelper.getLastID();
                    dbHelper.deleteNote(noteID);
                }
                Intent resultIntent = new Intent();
                setResult(MainActivity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
