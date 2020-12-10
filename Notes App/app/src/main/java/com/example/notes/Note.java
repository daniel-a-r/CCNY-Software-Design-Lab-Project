package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Parcelable {

    private static final String TAG = "Note";

    private int id;
    private String text;
    private String dateEdited;
    private final String dateCreated;

    /*public Note() {
        this.text = "";
        Date created = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        dateCreated = formatter.format(created);
    }*/

    /*public Note(int id, String text) {
        this.id = id;
        this.text = text;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateEdited = formatter.format(date);
        dateCreated = formatter.format(date);
    }*/

    public Note(int id, String text, String dateEdited, String dateCreated) {
        this.id = id;
        this.text = text;
        this.dateEdited = dateEdited;
        this.dateCreated = dateCreated;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        text = in.readString();
        dateCreated = in.readString();
        dateEdited = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) { return new Note(in); }

        @Override
        public Note[] newArray(int size) { return new Note[size]; }
    };

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getDateCreated() { return dateCreated; }

    public String getDateEdited() { return dateEdited; }

    public void setDateEdited(Date edited) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        dateEdited = formatter.format(edited);
    }

    public void setDateEdited(String dateEdited) { this.dateEdited = dateEdited; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeString(dateCreated);
        dest.writeString(dateEdited);
    }

    @Override
    public String toString() {
        return String.format("%d %s %s", getId(), getDateEdited(), getDateCreated());
    }
}
