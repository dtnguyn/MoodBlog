package com.nguyen.moodblog;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserPost {
    private String mUserName;
    private String mUserMood;
    private String mUserTags;
    private String mUserPostHeading;
    private String mUserPostBody;
    private String mDate;
    private int numberOfLikes;
    private String postKey;
    private String postID;
    private String currentUserPostKey;
    private String isLiked = "false";
    private String ownerID;
    private int userIconResourceId;
    private Boolean onlyOwner = false;
    private Date deletionTime;
    List<Comment> mPostComments;

    //UI
    private ImageView likeButton;
    private TextView numberOfLikesIndicator;
    private ImageView userIcon;
    private TextView commentButton;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

//    private String postKey;

    public UserPost(View v, String userName, String userMood, String userTags, String userPostHeading, String userPostBody, String date, int id) {
        mUserName = userName;
        mUserMood = userMood;
        mUserTags = userTags;
        mUserPostHeading = userPostHeading;
        mUserPostBody = userPostBody;
        mDate = date;
        numberOfLikes = 0;
        userIconResourceId = id;
        //numberOfLikesIndicator = v.findViewById(R.id.id_number_of_likes);
    }


    public UserPost(View v) {
        numberOfLikes = 0;
        likeButton = v.findViewById(R.id.like_button);
    }

    public UserPost(){

    }
    //Getters
    public String getUserName() {
        return mUserName;
    }

    public String getUserMood() {
        return mUserMood;
    }

    public String getUserTags() {
        return mUserTags;
    }

    public String getUserPostHeading() {
        return mUserPostHeading;
    }

    public String getUserPostBody() {
        return mUserPostBody;
    }

    public String getDate() {
        return mDate;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public String getPostKey() {
        return postKey;
    }

    public String getCurrentUserPostKey() {
        return currentUserPostKey;
    }

    public ImageView getLikeButton() {
        return likeButton;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public TextView getNumberOfLikesIndicator() {
        return numberOfLikesIndicator;
    }

    public int getUserIconResourceId() {
        return userIconResourceId;
    }

    public TextView getCommentButton() {
        return commentButton;
    }

    public List<Comment> getPostComments() {
        return mPostComments;
    }

    public Boolean getOnlyOwner() {
        return onlyOwner;
    }

    public Date getDeletionTime() {
        return deletionTime;
    }

    public String getPostID() {
        return postID;
    }

    public int getNumberOfComments(){
        int numberOfComments = 0;
        if(mPostComments != null){
            numberOfComments = mPostComments.size();
        }
        return numberOfComments;
    }

    //Setter
    public void setUserName(String userName) {
        mUserName = userName;
    }

    public void setUserMood(String userMood) {
        mUserMood = userMood;
    }

    public void setUserTags(String userTags) {
        mUserTags = userTags;
    }

    public void setUserPostHeading(String userPostHeading) {
        mUserPostHeading = userPostHeading;
    }

    public void setUserPostBody(String userPostBody) {
        mUserPostBody = userPostBody;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public void setCurrentUserPostKey(String currentUserPostKey) {
        this.currentUserPostKey = currentUserPostKey;
    }

    public void setLikeButton(ImageView likeButton) {
        this.likeButton = likeButton;
    }

    public void setIsLiked(String liked) {
        isLiked = liked;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setNumberOfLikesIndicator(TextView numberOfLikesIndicator) {
        this.numberOfLikesIndicator = numberOfLikesIndicator;
    }

    public void setUserIconResourceId(int userIconResourceId) {
        this.userIconResourceId = userIconResourceId;
    }

    public void setCommentButton(TextView commentButton) {
        this.commentButton = commentButton;
    }

    public void setUserIcon(ImageView userIcon) {
        this.userIcon = userIcon;
    }

    public void setPostComments(List<Comment> postComments) {
        mPostComments = postComments;
    }

    public void setOnlyOwner(Boolean onlyOwner) {
        this.onlyOwner = onlyOwner;
    }

    public void setDeletionTime(Date deletionTime) {
        this.deletionTime = deletionTime;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    //This function perform the like activity
    public void like() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the button is clicked
                Log.d("MoodBlog", "Like Button is clicked");

                //Setup the database variables
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference();
                mAuth = FirebaseAuth.getInstance();

                //Increase the number of likes
                numberOfLikes++;

                //Change the isLiked status
                isLiked = "true";

                //Link and change the UI elements
                likeButton = v.findViewById(R.id.like_button);
                likeButton.setImageResource(R.drawable.like_red);
                //numberOfLikesIndicator = v.getRootView().findViewById(R.id.id_number_of_likes);
                numberOfLikesIndicator.setText("" + numberOfLikes);

                //Update the database
                String userID = mAuth.getCurrentUser().getUid();
                if(deletionTime.after(Calendar.getInstance().getTime())) {
                    myRef.child("posts").child(postKey).child("numberOfLikes").setValue(numberOfLikes);
                }
                myRef.child("users").child(ownerID).child("posts").child(currentUserPostKey).child("numberOfLikes")
                        .setValue(numberOfLikes);

                //Add the postID to the likedPosts of the current account
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userID = mAuth.getCurrentUser().getUid();
                        List<String> likePosts;

                        //Check if the likePosts has already existed on the database or not
                        if ((List<String>) dataSnapshot.child("users").child(userID).child("likedPosts").getValue() != null) {
                            likePosts = (ArrayList<String>) dataSnapshot.child("users").child(userID).child("likedPosts").getValue();
                        } else likePosts = new ArrayList<>();

                        likePosts.add(postID);
                        Log.d("MoodBlog", "" + likePosts.get(0));
                        myRef.child("users").child(userID).child("likedPosts").setValue(likePosts);
                        likeButton.setOnClickListener(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    public void comment(){
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityComment.class);
                intent.putExtra("name", mUserName);
                intent.putExtra("avatar", userIconResourceId);
                intent.putExtra("mood", mUserMood);
                intent.putExtra("date", mDate);
                intent.putExtra("tags", mUserTags);
                intent.putExtra("heading", mUserPostHeading);
                intent.putExtra("body", mUserPostBody);
                intent.putExtra("numberOfLikes", numberOfLikes);
                intent.putExtra("postKey", postKey);
                intent.putExtra("currentUserPostKey", currentUserPostKey);
                intent.putExtra("ownerID", ownerID);
                intent.putExtra("numberOfLikes", "" + numberOfLikes);
                intent.putExtra("isLiked", isLiked);


                v.getContext().startActivity(intent);
            }
        });
    }

}
