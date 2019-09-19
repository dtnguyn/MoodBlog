package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;

public class ActivityYourPosts extends AppCompatActivity {
    //Static variable
    public static String currentUserPostKey;

    //Member Variables
    List<UserPost> mUserPosts;


    //UI elements
    RecyclerView mRecyclerView;
    AdapterRecyclerViewYourPosts mAdapterRecyclerViewYourPosts;
    Button backButton;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_posts);







        //Set up the post list
        mUserPosts = new ArrayList<>();

        //Set up the back button
        backButton = findViewById(R.id.id_back_button_yourPost);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userID = mAuth.getCurrentUser().getUid();
                //When the user get on this activity, the number of likes and comments will be updated
                if(dataSnapshot.child("users").child(userID).child("posts").getValue() != null) {
                    long latestPostIndex = dataSnapshot.child("users").child(userID).child("posts").getChildrenCount() - 1;
                    if (dataSnapshot.child("users").child(userID).child("posts").child("" + latestPostIndex).getValue(UserPost.class).getDeletionTime().after(Calendar.getInstance().getTime())) {
                        int numberOfLikes = dataSnapshot.child("users").child(userID).child("posts").child("" + latestPostIndex).getValue(UserPost.class).getNumberOfLikes();
                        int numberOfComments = (int) dataSnapshot.child("users").child(userID).child("posts").child("" + latestPostIndex).child("comments").getChildrenCount();
                        //Save the update
                        SharedPreferences.Editor editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putInt(NotificationBackgroundService.LIKE_UPDATE_KEY, numberOfLikes);
                        editor.putInt(NotificationBackgroundService.COMMENT_UPDATE_KEY, numberOfComments);
                        editor.apply();
                    }
                }
                if(dataSnapshot.child("users").child(userID).child("posts").getValue() != null){
                    List <String> likePosts = new ArrayList<>();
                    for(int i = (int) dataSnapshot.child("users").child(userID).child("posts").getChildrenCount() - 1; i >= 0 ; i--){
                        UserPost userPost = new UserPost();
                        setUpUserPost(userPost,dataSnapshot, likePosts,userID,i);
                        mUserPosts.add(userPost);

                    }

                    mAdapterRecyclerViewYourPosts = new AdapterRecyclerViewYourPosts(mUserPosts);
                    mRecyclerView = findViewById(R.id.yourPost_recyclerView);
                    if(mAdapterRecyclerViewYourPosts !=null){
                        mRecyclerView.setAdapter(mAdapterRecyclerViewYourPosts);
                    }
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setUpUserPost(UserPost userPost, DataSnapshot dataSnapshot,List<String> likePosts, String userID, int i){
        userPost.setUserName(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getUserName());
        userPost.setUserMood(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getUserMood());
        userPost.setUserTags(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getUserTags());
        userPost.setUserPostBody(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getUserPostBody());
        userPost.setUserPostHeading(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getUserPostHeading());
        userPost.setDate(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getDate());
        userPost.setPostKey(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getPostKey());
        userPost.setCurrentUserPostKey(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getCurrentUserPostKey());
        userPost.setNumberOfLikes(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getNumberOfLikes());
        userPost.setOwnerID(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getOwnerID());
        userPost.setUserIconResourceId(getUserIconFromDatabase(dataSnapshot,i));
        userPost.setDeletionTime(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getDeletionTime());
        userPost.setPostID(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getPostID());

        //Check if the likePosts has already existed on the database or not
        if ((List<String>) dataSnapshot.child("users").child(userID).child("likedPosts").getValue() != null) {
            likePosts = (ArrayList<String>) dataSnapshot.child("users").child(userID).child("likedPosts").getValue();
            for (int j = 0; j < likePosts.size(); j++) {
                if (userPost.getPostID().equals(likePosts.get(j))) {
                    userPost.setIsLiked("true");
                    break;
                }

            }
        }
    }

    //Set up userIcon
    private int getUserIconFromDatabase(DataSnapshot dataSnapshot, int i){
        String userID = mAuth.getCurrentUser().getUid();
        String icon = dataSnapshot.child("users").child(userID).child("userIcon").getValue(String.class);
        int userIconResourceID;
        switch(icon){
            case "m1":
                userIconResourceID = R.drawable.male_1;
                break;
            case "m2":
                userIconResourceID = R.drawable.male_2;
                break;
            case "m3":
                userIconResourceID = R.drawable.male_3;
                break;
            case "m4":
                userIconResourceID = R.drawable.male_4;
                break;
            case "m5":
                userIconResourceID = R.drawable.male_5;
                break;
            case "m6":
                userIconResourceID = R.drawable.male_6;
                break;
            case "m7":
                userIconResourceID = R.drawable.male_7;
                break;
            case "m8":
                userIconResourceID = R.drawable.male_8;
                break;
            case "m9":
                userIconResourceID = R.drawable.male_9;
                break;
            case "fm1":
                userIconResourceID = R.drawable.female_1;
                break;
            case "fm2":
                userIconResourceID = R.drawable.female_2;
                break;
            case "fm3":
                userIconResourceID = R.drawable.female_3;
                break;
            case "fm4":
                userIconResourceID = R.drawable.female_4;
                break;
            case "fm5":
                userIconResourceID = R.drawable.female_5;
                break;
            case "fm6":
                userIconResourceID = R.drawable.female_6;
                break;
            case "fm7":
                userIconResourceID = R.drawable.female_7;
                break;
            case "fm8":
                userIconResourceID = R.drawable.female_8;
                break;
            case "fm9":
                userIconResourceID = R.drawable.female_9;
                break;
            default:
                userIconResourceID = R.drawable.unknown;
                break;
        }
        return  userIconResourceID;

    }


    @Override
    protected void onResume() {
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onResume();
        super.onResume();
        if(currentUserPostKey != null){
            mUserPosts.get(mUserPosts.size() - 1 - parseInt(currentUserPostKey)).getLikeButton().callOnClick();
            currentUserPostKey = null;
        }
    }


}
