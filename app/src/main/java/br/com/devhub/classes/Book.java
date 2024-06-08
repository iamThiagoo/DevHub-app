package br.com.devhub.classes;

import java.math.BigInteger;
import java.sql.Timestamp;

public class Book {
    private String name;
    private String description;
    private String thumbnail;
    private String file;
    private String author;
    private String created_by;
    private BigInteger created_at;
    private BigInteger updated_at;

    public Book (String name, String description, String thumbnail, String file, String author, String created_by, BigInteger created_at, BigInteger updated_at) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.file = file;
        this.author = author;
        this.created_by = created_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
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

    public BigInteger getCreatedAt() {
        return created_at;
    }

    public BigInteger getUpdatedAt() {
        return updated_at;
    }
}
