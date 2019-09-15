package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;

public class ActivityComment extends AppCompatActivity {
    //Member Variables
    List<Comment> mComments;
    List<Comment> mCommentsDatabase;
    AdapterRecyclerViewComments commentAdapter;
    RecyclerView mRecyclerView;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    //UI elements
    private TextView postName;
    private TextView userMood;
    private TextView userTag;
    private TextView userPostHeading;
    private TextView userPost;
    private TextView postDate;
    private TextView numberOfLikes;
    private ImageView postAvatar;
    private ImageView commentAvatar;
    private ImageView likeButton;
    private EditText commentArea;
    private Button addCommentButton;

    //Member Variables
    Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mComments = new ArrayList<>();
        mCommentsDatabase = new ArrayList<>();
        extras = getIntent().getExtras();

        mRecyclerView = findViewById(R.id.comment_recyclerView);

        //Linking the UI elements
        postName = findViewById(R.id.id_user_name_post_commentActivity);
        userMood = findViewById(R.id.id_user_feeling_commentActivity);
        userTag = findViewById(R.id.id_user_tags_commentActivty);
        userPostHeading = findViewById(R.id.id_user_heading_commentActivity);
        userPost = findViewById(R.id.id_user_writing_commentActivity);
        postDate = findViewById(R.id.date_commentActivity);
        numberOfLikes = findViewById(R.id.id_number_of_likes_commentActivity);
        postAvatar = findViewById(R.id.img_user_avartar_commentActivity);
        commentAvatar = findViewById(R.id.img_comment_avartar_commentActivity);
        likeButton = findViewById(R.id.like_button_commentActivity);
        commentArea = findViewById(R.id.comment_area);
        addCommentButton = findViewById(R.id.add_comment_button);

        //Set up the post information
        postName.setText(extras.getString("name"));
        userMood.setText(extras.getString("mood"));
        userTag.setText(extras.getString("tags"));
        postDate.setText(extras.getString("date"));
        userPostHeading.setText(extras.getString("heading"));
        userPost.setText(extras.getString("body"));
        numberOfLikes.setText(extras.getString("numberOfLikes"));
        commentAvatar.setImageResource(ActivityBlog.userIconResourceId);
        postAvatar.setImageResource(extras.getInt("avatar"));

        //Set up like numbers indicator
        numberOfLikes.setText(extras.getString("numberOfLikes"));

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        //Get existing comments from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((List<Comment>) dataSnapshot.child("posts").child(extras.getString("postKey"))
                        .child("comments").getValue() != null){

                    Log.d("MoodBlog", "It's in");

                    for(int i = 0; i <(int) dataSnapshot.child("posts").child(extras.getString("postKey"))
                            .child("comments").getChildrenCount(); i++){
                        Log.d("MoodBlog", "It's in");

                        Comment comment = new Comment();
                        Comment commentDatabase = new Comment();

                        //Get the existing comments
                        comment.setOwnerName(dataSnapshot.child("posts").child(extras.getString("postKey"))
                                .child("comments").child("" + i).getValue(Comment.class).getOwnerName());
                        comment.setOwnerID(dataSnapshot.child("posts").child(extras.getString("postKey"))
                                .child("comments").child("" + i).getValue(Comment.class).getOwnerID());
                        comment.setDate(dataSnapshot.child("posts").child(extras.getString("postKey"))
                                .child("comments").child("" + i).getValue(Comment.class).getDate());
                        comment.setCommentContent(dataSnapshot.child("posts").child(extras.getString("postKey"))
                                .child("comments").child("" + i).getValue(Comment.class).getCommentContent());
                        comment.setAvatarResourceID(dataSnapshot.child("posts").child(extras.getString("postKey"))
                                .child("comments").child("" + i).getValue(Comment.class).getAvatarResourceID());

                        mComments.add(comment);
                        mCommentsDatabase.add(commentDatabase);
                    }

                    commentAdapter = new AdapterRecyclerViewComments(mComments);
                    mRecyclerView.setAdapter(commentAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityComment.this));


                } else Log.d("MoodBlog", "comments null");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Set up the add comment button
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentArea.getText().toString().equals("")){
                    new Toast(v.getContext()).makeText(v.getContext(), "You have to type the comment first", Toast.LENGTH_SHORT).show();
                } else {

                    mRecyclerView.setAdapter(null);
                    //Date
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

                    //Setup Avatar
                    ImageView avatar = findViewById(R.id.img_comment_avartar);
                    if(avatar != null){
                        avatar.setImageResource(ActivityBlog.userIconResourceId);
                    } else Log.d("MoodBlog", "avatar null");


                    //Getting the extras from the intent
                    Bundle extras = getIntent().getExtras();


                    Comment comment = new Comment();

                    comment.setOwnerName(ActivityBlog.userName);
                    comment.setAvatarResourceID(ActivityBlog.userIconResourceId);
                    comment.setOwnerID(mAuth.getCurrentUser().getUid());
                    comment.setCommentContent(commentArea.getText().toString());
                    comment.setDate(currentDate);
                    comment.setAvatarView(avatar);

                    mComments.add(0, comment);

                    myRef.child("posts").child(extras.getString("postKey")).child("comments").setValue(mComments);
                    myRef.child("users").child(extras.getString("ownerID")).child("posts").child(extras.getString("currentUserPostKey")).child("comments").setValue(mComments);
                    Log.d("MoodBlog", "done");

                    if (commentAdapter == null){
                        commentAdapter = new AdapterRecyclerViewComments(mComments);
                        mRecyclerView.setAdapter(commentAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityComment.this));
                        new Toast(v.getContext()).makeText(v.getContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        mRecyclerView.setAdapter(commentAdapter);
                        new Toast(v.getContext()).makeText(v.getContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();
                    }
                    commentArea.setText("");
                }
            }
        });



        //Set up like button inside the comment Activity
        if(extras.getString("isLiked").equals("true")){
            likeButton.setImageResource(R.drawable.like_red);
            likeButton.setOnClickListener(null);
        } else {
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeButton.setImageResource(R.drawable.like_red);

                    int likes = parseInt(extras.getString("numberOfLikes")) + 1;
                    numberOfLikes.setText("" + likes);

                    FragmentNewFeed.postKey = extras.getString("postKey");
                    ActivityYourPosts.currentUserPostKey = extras.getString("currentUserPostKey");

                }
            });
        }





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
    }


    private void updateDatabase(ArrayList<Comment> comments){
        myRef.child("posts").child(extras.getString("postKey")).child("comments").setValue(comments);
        myRef.child("users").child(extras.getString("ownerID")).child("posts").child(extras.getString("currentUserPostKey")).child("comments").setValue(comments);
        finish();
    }
}
