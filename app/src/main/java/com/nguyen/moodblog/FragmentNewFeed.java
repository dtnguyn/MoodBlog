package com.nguyen.moodblog;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nguyen.moodblog.Interface.LoadMore;

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


    //Member variables
    private View mView;
    private RecyclerView mRecyclerView;
    private AdapterRecyclerViewNewFeeds recyClerViewAdapter;
    private List<UserPost>mUserPosts = new ArrayList<>();
    private int firstIndex;
    private int lastIndex;

    //UI elements
    SwipeRefreshLayout mSwipeRefreshLayout;


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

        //Set up the first and last index
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstIndex = (int) dataSnapshot.child("posts").getChildrenCount() - 1;
                lastIndex = firstIndex - 9;
                if(lastIndex < 0){
                    lastIndex = 0;
                }
                Log.d("MoodBlog", "lastIndex ini: " + lastIndex);
                Log.d("MoodBlog", "fistIndex ini: " + firstIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_refresh_layout_new_feed);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("posts").getValue() != null){
                            String userID = mAuth.getCurrentUser().getUid();
                            List <String> likePosts = new ArrayList<>();
                            mUserPosts.clear();
                            firstIndex = (int) dataSnapshot.child("posts").getChildrenCount() - 1;
                            lastIndex = firstIndex - 9;
                            if(lastIndex < 0){
                                lastIndex = 0;
                            }

                            getFeedsFromDatabase(dataSnapshot, likePosts,userID);

                            //Set up the recyclerView adapter
                            recyClerViewAdapter = new AdapterRecyclerViewNewFeeds(mUserPosts, getContext(), getActivity(), mRecyclerView);
                            mRecyclerView.setAdapter(recyClerViewAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            //Set Load more event
                            recyClerViewAdapter.setLoadMore(new LoadMore() {
                                @Override
                                public void onLoadMore() {
                                    if(mUserPosts.size() <= (int) dataSnapshot.child("posts").getChildrenCount() - 1){
                                        mUserPosts.add(null);
                                        recyClerViewAdapter.notifyItemInserted(mUserPosts.size() - 1);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mUserPosts.remove(mUserPosts.size() - 1);
                                                recyClerViewAdapter.notifyItemRemoved(mUserPosts.size());

                                                String userID = mAuth.getCurrentUser().getUid();
                                                List <String> likePosts = new ArrayList<>();
                                                getFeedsFromDatabase(dataSnapshot, likePosts, userID);

                                                recyClerViewAdapter.notifyDataSetChanged();
                                                recyClerViewAdapter.setLoaded();
                                            }
                                        }, 2500);
                                    }
                                }
                            });


                        } else {
                            mUserPosts.clear();recyClerViewAdapter = new AdapterRecyclerViewNewFeeds(mUserPosts, getContext(), getActivity(), mRecyclerView);
                            mRecyclerView.setAdapter(recyClerViewAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("posts").getValue() != null){
                    String userID = mAuth.getCurrentUser().getUid();
                    List <String> likePosts = new ArrayList<>();
                    getFeedsFromDatabase(dataSnapshot, likePosts,userID);

                    //Set up the recyclerView adapter
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyClerViewAdapter = new AdapterRecyclerViewNewFeeds(mUserPosts, getContext(), getActivity(), mRecyclerView);
                    mRecyclerView.setAdapter(recyClerViewAdapter);

                    //Set Load more event
                    recyClerViewAdapter.setLoadMore(new LoadMore() {
                        @Override
                        public void onLoadMore() {
                            if(mUserPosts.size() <= (int) dataSnapshot.child("posts").getChildrenCount() - 1){
                                mUserPosts.add(null);
                                recyClerViewAdapter.notifyItemInserted(mUserPosts.size() - 1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUserPosts.remove(mUserPosts.size() - 1);
                                        recyClerViewAdapter.notifyItemRemoved(mUserPosts.size());

                                        String userID = mAuth.getCurrentUser().getUid();
                                        List <String> likePosts = new ArrayList<>();
                                        getFeedsFromDatabase(dataSnapshot, likePosts, userID);

                                        recyClerViewAdapter.notifyDataSetChanged();
                                        recyClerViewAdapter.setLoaded();
                                    }
                                }, 2500);
                            }
                        }
                    });


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

    private void setUpUserPost(UserPost userPost, DataSnapshot dataSnapshot,List<String> likePosts, String userID, int i){
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
        userPost.setUserIconResourceId(getUserIconFromDatabase(dataSnapshot,i));
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
    }

    //Get new feed data from database
    private void getFeedsFromDatabase(DataSnapshot dataSnapshot, List<String> likePosts, String userID){
        List <UserPost> updatedUserPosts = (ArrayList<UserPost>) dataSnapshot.child("posts").getValue();
        int preUpdatedSize = updatedUserPosts.size();
        for(int i = firstIndex; i >= lastIndex ; i--){

            if(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class) == null){
                Log.d("MoodBlog", "Index: " + i + " lastIndex: " + lastIndex);
            }
            if(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getDeletionTime().after(Calendar.getInstance().getTime())){
                if(dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOnlyOwner()) {
                    if (dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOwnerID().equals(mAuth.getCurrentUser().getUid())) {
                        UserPost userPost = new UserPost();
                        setUpUserPost(userPost,dataSnapshot, likePosts, userID, i);
                        mUserPosts.add(userPost);
                    }
                } else {
                    UserPost userPost = new UserPost();
                    setUpUserPost(userPost,dataSnapshot, likePosts, userID, i);
                    mUserPosts.add(userPost);
                }
            } else {

                //Update the userPost list on database
                updatedUserPosts.remove(i);
                myRef.child("posts").setValue(updatedUserPosts);

                lastIndex--;
                if(lastIndex < 0){
                    lastIndex = 0;
                }
            }
        }
        if(updatedUserPosts.size() != preUpdatedSize){
            myRef.child("posts").setValue(updatedUserPosts);
        }

        firstIndex = lastIndex - 1;
        lastIndex = firstIndex - 9;
        if(firstIndex < 0){
            firstIndex = (int) dataSnapshot.child("posts").getChildrenCount() - 1;
            lastIndex = 0;
        } else if(lastIndex < 0){
            lastIndex = 0;
        }
    }

    //Set up userIcon
    private int getUserIconFromDatabase(DataSnapshot dataSnapshot, int i){
        String userID = dataSnapshot.child("posts").child("" + i).getValue(UserPost.class).getOwnerID();
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

}
