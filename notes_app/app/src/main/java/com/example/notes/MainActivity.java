package com.example.notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE = 1;
    private final ArrayList<Note> noteList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNoteList();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new MyAdapter(MainActivity.this, this, noteList);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("list_size", noteList.size());
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // converts format from yyyy-MM-dd HH:mm:ss to MM/dd/yyyy
    private String formatDate(String date) {
        String[] dateArr = date.split(" ");
        dateArr = dateArr[0].split("-");
        date = String.format("%s/%s/%s", dateArr[1], dateArr[2], dateArr[0]);
        return date;
    }

    // retrieves data from the database if it's note empty and stores it in the array list
    private void initNoteList() {
        dbHelper = new DatabaseHelper(this);

        Cursor cursor = dbHelper.getAllNotes();

        if(cursor.getCount() == 0) {
            Log.d(TAG, "onCreate: database is empty");
        }
        else {
            while(cursor.moveToNext()) {
                int id = Integer.parseInt(cursor.getString(0));
                String noteText = cursor.getString(1);
                String dateEdited = formatDate(cursor.getString(2));
                String dateCreated = formatDate(cursor.getString(3));

                noteList.add(new Note(id, noteText, dateEdited, dateCreated));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        dbHelper = new DatabaseHelper(this);

        if(resultCode == MainActivity.RESULT_OK) {
            String noteOperation = data.getStringExtra("note_operation");

            switch (noteOperation) {
                case "added": {
                    Cursor cursor = dbHelper.getRow(dbHelper.getLastID());

                    // gets new note attributes from database
                    int id = Integer.parseInt(cursor.getString(0));
                    String noteText = cursor.getString(1);
                    String dateEdited = formatDate(cursor.getString(2));
                    String dateCreated = formatDate(cursor.getString(3));

                    Note note = new Note(id, noteText, dateEdited, dateCreated);

                    noteList.add(0, note);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
                case "updated": {
                    int position = data.getIntExtra("list_position", -1);
                    int row = noteList.get(position).getId();

                    Cursor cursor = dbHelper.getRow(Integer.toString(row));

                    // gets updated values from database
                    String noteText = cursor.getString(1);
                    String dateEdited = formatDate(cursor.getString(2));

                    // updates the value of the note as given index
                    noteList.get(position).setText(noteText);
                    noteList.get(position).setDateEdited(dateEdited);

                    Note note = noteList.get(position);

                    // removes the updated note object and re-inserts it to the beginning
                    noteList.remove(position);
                    noteList.add(0, note);

                    mAdapter.notifyDataSetChanged();
                    break;
                }
                case "deleted": {
                    int position = data.getIntExtra("list_position", -1);
                    noteList.remove(position);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    // Features that I was not able to implement in time that I might want to come back to later
    // and finish

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_by) {
            Toast.makeText(this, "Sort By option selected", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.delete) {
            Toast.makeText(this, "Delete option selected", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
