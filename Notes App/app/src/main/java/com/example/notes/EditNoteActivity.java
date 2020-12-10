package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private static final String TAG = "EditNoteActivity";
    private int noteID;
    private int position;
    boolean noteEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Note");

        Note note = getIntent().getParcelableExtra("note");
        noteID = note.getId();
        position = getIntent().getIntExtra("list_position", -1);

        EditText editText = findViewById(R.id.editText);
        editText.setText(note.getText());
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
            if(noteEdited) {
                Log.d(TAG, "onOptionsItemSelected: list_position = " + position);

                returnIntent.putExtra("list_position", position);
                returnIntent.putExtra("note_operation", "updated");
                setResult(MainActivity.RESULT_OK, returnIntent);
            }
            else {
                setResult(MainActivity.RESULT_CANCELED, returnIntent);
            }
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        else if (id == R.id.save_note) {
            if (noteText.isEmpty()) {
                Toast.makeText(this, "An empty note cannot be saved", Toast.LENGTH_LONG).show();
            }
            else {
                DatabaseHelper dbHelper = new DatabaseHelper(this);

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = formatter.format(date);

                Note note = getIntent().getParcelableExtra("note");
                String noteID = Integer.toString(note.getId());

                dbHelper.updateNote(noteID, noteText, currentDate);
                noteEdited = true;
            }

            return true;
        }
        else if (id == R.id.delete_note) {
            confirmDeleteDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note?");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper dbHelper = new DatabaseHelper(EditNoteActivity.this);
                dbHelper.deleteNote(Integer.toString(noteID));

                Intent resultIntent = new Intent();
                resultIntent.putExtra("note_operation", "deleted");
                resultIntent.putExtra("list_position", position);
                setResult(MainActivity.RESULT_OK, resultIntent);

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
