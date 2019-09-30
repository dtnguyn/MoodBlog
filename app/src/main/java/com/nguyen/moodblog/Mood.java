package com.nguyen.moodblog;

import java.util.Date;

public class Mood {

    //Member variables
    private float mMoodValue;
    private String mDate;

    public Mood(float moodValue, String date) {
        mMoodValue = moodValue;
        mDate = date;
    }

    public Mood(){

    }

    //Setters
    public void setMoodValue(float moodValue) {
        mMoodValue = moodValue;
    }

    public void setDate(String date) {
        mDate = date;
    }


    //Getters
    public float getMoodValue() {
        return mMoodValue;
    }

    public String getDate() {
        return mDate;
    }
}
