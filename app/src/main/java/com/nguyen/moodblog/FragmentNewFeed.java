package com.nguyen.moodblog;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FragmentNewFeed extends Fragment {

    //Static variable
    public static String postKey;

    //private static List<UserPost> USER_POSTS;


    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    //Member variable
    private View mView;
    private RecyclerView mRecyclerView;
    private AdapterRecyclerViewNewFeeds recyClerViewAdapter;
    private List<UserPost>mUserPosts = new ArrayList<>();


    public FragmentNewFeed(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, getContext().MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            getContext().setTheme(R.style.DarkTheme);
        }else {
            getContext().setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_feed, container, false);
        mRecyclerView = mView.findViewById(R.id.feed_recyclerView);
        getActivity().getIntent().putExtra("postKey", "null");

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //showData(dataSnapshot);
                if(dataSnapshot.child("posts").getValue() != null){
                    String userID = mAuth.getCurrentUser().getUid();
                    List <String> likePosts;
                    for(int i = (int) dataSnapshot.child("posts").getChildrenCount() - 1; i >= 0 ; i--){
                        if(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getDeletionTime().after(Calendar.getInstance().getTime())){
                            if(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOnlyOwner()) {
                                if (dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOwnerID().equals(mAuth.getCurrentUser().getUid())) {
                                    UserPost userPost = new UserPost();
                                    userPost.setUserName(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserName());
                                    userPost.setUserMood(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserMood());
                                    userPost.setUserTags(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserTags());
                                    userPost.setUserPostBody(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserPostBody());
                                    userPost.setUserPostHeading(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserPostHeading());
                                    userPost.setDate(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getDate());
                                    userPost.setPostKey(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getPostKey());
                                    userPost.setCurrentUserPostKey(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getCurrentUserPostKey());
                                    userPost.setNumberOfLikes(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getNumberOfLikes());
                                    userPost.setOwnerID(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOwnerID());
                                    userPost.setUserIconResourceId(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserIconResourceId());
                                    userPost.setDeletionTime(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getDeletionTime());
                                    userPost.setPostID(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getPostID());

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

                                    mUserPosts.add(userPost);
                                }
                            } else {
                                UserPost userPost = new UserPost();
                                userPost.setUserName(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserName());
                                userPost.setUserMood(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserMood());
                                userPost.setUserTags(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserTags());
                                userPost.setUserPostBody(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserPostBody());
                                userPost.setUserPostHeading(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserPostHeading());
                                userPost.setDate(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getDate());
                                userPost.setPostKey(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getPostKey());
                                userPost.setCurrentUserPostKey(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getCurrentUserPostKey());
                                userPost.setNumberOfLikes(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getNumberOfLikes());
                                userPost.setOwnerID(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOwnerID());
                                userPost.setUserIconResourceId(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getUserIconResourceId());
                                userPost.setDeletionTime(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getDeletionTime());
                                userPost.setPostID(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getPostID());

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

                                mUserPosts.add(userPost);
                            }
                        } else {
                            List <UserPost> userPosts = (ArrayList<UserPost>) dataSnapshot.child("posts").getValue();
                            userPosts.remove(i);
                            myRef.child("posts").setValue(userPosts);
                            i--;
                        }


                    }
                    //getFragmentManager().beginTransaction().detach(FragmentNewFeed.this).attach(FragmentNewFeed.this).commit();
                    recyClerViewAdapter = new AdapterRecyclerViewNewFeeds(mUserPosts, getContext());
                    mRecyclerView.setAdapter(recyClerViewAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return mView;
    }


    @Override
    public void onResume() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, getContext().MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            getContext().setTheme(R.style.DarkTheme);
        }else {
            getContext().setTheme(R.style.AppTheme);
        }
        super.onResume();
        if(postKey != null){
            mUserPosts.get(mUserPosts.size() - 1 - parseInt(postKey)).getLikeButton().callOnClick();
            postKey = null;
        }
    }
}
