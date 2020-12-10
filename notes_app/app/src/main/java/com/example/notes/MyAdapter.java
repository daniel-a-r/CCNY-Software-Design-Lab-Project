package com.example.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final ArrayList<Note> noteList;
    private Context context;
    private Activity activity;

    public MyAdapter(Activity activity, Context context, ArrayList<Note> noteList) {
        this.context = context;
        this.activity = activity;
        this.noteList = noteList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView noteView;
        TextView dateView;
        CardView cardView;

        MyViewHolder(View cardItemView) {
            super(cardItemView);

            noteView = cardItemView.findViewById(R.id.note_view);
            dateView = cardItemView.findViewById(R.id.date_view);
            cardView = cardItemView.findViewById(R.id.card_view);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view/*, listener*/);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
        viewHolder.noteView.setText(noteList.get(position).getText());
        viewHolder.dateView.setText(noteList.get(position).getDateCreated());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // context is MainActivity
                Intent intent = new Intent(context, EditNoteActivity.class);
                intent.putExtra("note", noteList.get(position));
                intent.putExtra("list_position", position);
                activity.startActivityForResult(intent, MainActivity.REQUEST_CODE);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() { return noteList.size(); }
}
