package com.nguyen.moodblog;

import android.widget.ImageView;

public class Comment {
    //UI Elements
    ImageView avatarView;

    //Member variables
    String commentContent;
    String date;
    String ownerName;
    String ownerID;
    int avatarResourceID;

    //Constructors
    public Comment(){
    }

    public Comment(String commentContent,  String ownerName, String date, ImageView avatarImage,
                   int avatarResourceID, String ownerID) {
        this.commentContent = commentContent;
        this.date = date;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.avatarResourceID = avatarResourceID;
        this.avatarView = avatarImage;
        avatarImage.setImageResource(avatarResourceID);
    }

    //Getters
    public ImageView getAvatarView() {
        return avatarView;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public String getDate() {
        return date;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public int getAvatarResourceID() {
        return avatarResourceID;
    }

    //Setters
    public void setAvatarView(ImageView avatarView) {
        this.avatarView = avatarView;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setAvatarResourceID(int avatarResourceID) {
        this.avatarResourceID = avatarResourceID;
    }
}
