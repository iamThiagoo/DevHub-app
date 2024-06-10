package br.com.devhub.classes;

import com.google.firebase.Timestamp;

import java.math.BigInteger;
import java.sql.Time;

public class Book {
    private String name;
    private String description;
    private String thumbnail;
    private String file;
    private String author;
    private String created_by;
    private Timestamp created_at;

    public Book (String name, String description, String author, String thumbnail, String file, String created_by, Timestamp created_at) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.file = file;
        this.author = author;
        this.created_by = created_by;
        this.created_at = created_at;
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

    public Timestamp getCreatedAt() {
        return created_at;
    }
}
