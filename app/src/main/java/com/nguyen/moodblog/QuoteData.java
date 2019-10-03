package com.nguyen.moodblog;

public class QuoteData {

    private String mDate;
    private String mContent;
    private String mAuthor;

    public QuoteData(String date, String content, String author) {
        mDate = date;
        mContent = content;
        mAuthor = author;
    }

    public QuoteData() {
    }

    //Getters
    public String getDate() {
        return mDate;
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthor() {
        return mAuthor;
    }

    //Setters
    public void setDate(String date) {
        mDate = date;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }
}
