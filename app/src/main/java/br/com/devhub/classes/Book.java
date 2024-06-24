package br.com.devhub.classes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Book implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String thumbnail;
    private String file;
    private String author;
    private String created_by;

    public Book (String id, String name, String description, String author, String thumbnail, String file, String created_by) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.file = file;
        this.author = author;
        this.created_by = created_by;
    }

    protected Book(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        thumbnail = in.readString();
        file = in.readString();
        author = in.readString();
        created_by = in.readString();
    }

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getFile() {
        return file;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreatedBy() {
        return created_by;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeString(file);
        dest.writeString(author);
        dest.writeString(created_by);
    }
}
