package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
                if(dataSnapshot.child("users").child(userID).child("posts").getValue() != null){
                    List <String> likePosts;
                    for(int i = (int) dataSnapshot.child("users").child(userID).child("posts").getChildrenCount() - 1; i >= 0 ; i--){
                        UserPost userPost = new UserPost();
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
                        userPost.setUserIconResourceId(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getUserIconResourceId());
                        userPost.setDeletionTime(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getDeletionTime());
                        userPost.setPostID(dataSnapshot.child("users").child(userID).child("posts").child("" + i).getValue(UserPost.class).getPostID());

                        if ((List<String>) dataSnapshot.child("users").child(userID).child("likedPosts").getValue() != null) {
                            likePosts = (ArrayList<String>) dataSnapshot.child("users").child(userID).child("likedPosts").getValue();
                            for (int j = 0; j < likePosts.size(); j++) {
                                if (userPost.getPostID().equals(likePosts.get(j))) {
                                    userPost.setIsLiked("true");
                                    break;
                                }
                            }
                        }

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

    @Override
    protected void onResume() {
        super.onResume();
        if(currentUserPostKey != null){
            mUserPosts.get(mUserPosts.size() - 1 - parseInt(currentUserPostKey)).getLikeButton().callOnClick();
            currentUserPostKey = null;
        }
    }
}
