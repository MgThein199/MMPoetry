package com.kothein.mmpoetry.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Book implements Parcelable {
    private int id;
    private String title;
    private String author;
    private List<ContentElement> content;

    public Book(int id, String title, String author, List<ContentElement> content) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
    }
    // Parcelable constructor
    public Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        // Initialize other fields from the parcel
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<ContentElement> getContent() {
        return content;
    }

    public void setContent(List<ContentElement> content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(author);
        // Write other fields to the parcel
    }

    // Parcelable CREATOR
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

}
