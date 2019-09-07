package com.nguyen.moodblog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

public class MenuButton {
    private String Text;
    private int backgroundColor;
    private Typeface font;
    private int textColor;

    public MenuButton() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public MenuButton(Context context, String text, int backgroundColor, int font, int textColor) {
        Text = text;
        this.backgroundColor = backgroundColor;
        this.font = ResourcesCompat.getFont(context, font);
        this.textColor = context.getColor(textColor);
    }

    public MenuButton(String text, int backgroundColor) {
        Text = text;
        this.backgroundColor = backgroundColor;

    }

    //setters

    public void setText(String text) {
        Text = text;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFont(Typeface font) {
        this.font = font;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    //getters

    public String getText() {
        return Text;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public Typeface getFont() {
        return font;
    }

    public int getTextColor() {
        return textColor;
    }

}


