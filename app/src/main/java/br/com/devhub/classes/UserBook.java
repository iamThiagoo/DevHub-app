package br.com.devhub.classes;

import java.math.BigInteger;

public class UserBook {

    private Book book;
    private String userId;
    private BigInteger created_at;

    public UserBook (Book book, String userId, BigInteger created_at) {
        this.book = book;
        this.userId = userId;
        this.created_at = created_at;
    }

    public Book getBook() {
        return book;
    }

    public String getUserId() {
        return userId;
    }

    public BigInteger getCreatedAt() {
        return created_at;
    }

}
