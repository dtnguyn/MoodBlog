package com.nguyen.moodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityCompose extends AppCompatActivity {
    //Constant Variables
    private final String DEFAULT_USER_NAME = "Anonymous";

    //Static Variable
    public static String mUserMood;


    //UI elements
    private CardView postBlockView;
    private TextView userNameView;
    private TextView userMoodView;
    private EditText userTagsView;
    private EditText userWritingView;
    private Button nextButton;
    private Button backButton;
    private ImageView userIcon;

    //Member variable
    private String mUserName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //Setters
    //Getters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        //Linking the UI elements
        userNameView = findViewById(R.id.id_user_name);
        userMoodView = findViewById(R.id.id_user_feeling);
        userTagsView = findViewById(R.id.id_user_tags);
        userWritingView = findViewById(R.id.id_user_writing);
        nextButton = findViewById(R.id.id_next_button_compose);
        backButton = findViewById(R.id.id_back_button_compose);
        userIcon = findViewById(R.id.img_user_avartar);

        //Set up the User Name and User Icon
        userIcon.setImageResource(ActivityBlog.userIconResourceId);
        ActivityEditPost.setUserIconResourceID(ActivityBlog.userIconResourceId);
        userNameView.setText(ActivityBlog.userName);

        //Set up the Firebase variable
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        setUpUserMood();
        setUpNextButton();

    }

    public void goToEdit(View view) {
        if(userWritingView.getText().toString().isEmpty()){
            new Toast(this).makeText(this, "You have to write something first!", Toast.LENGTH_SHORT).show();
        } else {
            ActivityEditPost.setUserName(userNameView.getText().toString());
            ActivityEditPost.setUserMood(userMoodView.getText().toString());
            ActivityEditPost.setUserTags(userTagsView.getText().toString());
            ActivityEditPost.setUserPostBody(userWritingView.getText().toString());

            Intent intent = new Intent(ActivityCompose.this, ActivityEditPost.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);
        }
    }



    public void setUpUserMood(){
        userMoodView.setText("is feeling " + mUserMood.toLowerCase());
    }

    public void setUpNextButton(){
        switch (mUserMood){
            case "Happy":
                nextButton.setBackgroundResource(R.drawable.happy_button);
                nextButton.setTextColor(getResources().getColor(R.color.happyText));
                ActivityEditPost.setPostButtonStyle(R.color.happyText, R.drawable.happy_button);
                break;
            case "Loved":
                nextButton.setBackgroundResource(R.drawable.loved_button);
                nextButton.setTextColor(getResources().getColor(R.color.loveText));
                ActivityEditPost.setPostButtonStyle(R.color.loveText, R.drawable.loved_button);
                break;
            case "Sad":
                nextButton.setBackgroundResource(R.drawable.sad_button);
                nextButton.setTextColor(getResources().getColor(R.color.sadText));
                ActivityEditPost.setPostButtonStyle(R.color.sadText, R.drawable.sad_button);
                break;
            case "Nervous":
                nextButton.setBackgroundResource(R.drawable.nervous_button);
                nextButton.setTextColor(getResources().getColor(R.color.nervousText));
                ActivityEditPost.setPostButtonStyle(R.color.nervousText, R.drawable.nervous_button);
                break;
            case "Tired":
                nextButton.setBackgroundResource(R.drawable.tired_button);
                nextButton.setTextColor(getResources().getColor(R.color.tiredText));
                ActivityEditPost.setPostButtonStyle(R.color.tiredText, R.drawable.tired_button);
                break;
            case "Excited":
                nextButton.setBackgroundResource(R.drawable.excited_button);
                nextButton.setTextColor(getResources().getColor(R.color.excitedText));
                ActivityEditPost.setPostButtonStyle(R.color.excitedText, R.drawable.excited_button);
                break;
            case "Angry":
                nextButton.setBackgroundResource(R.drawable.angry_button);
                nextButton.setTextColor(getResources().getColor(R.color.angryText));
                ActivityEditPost.setPostButtonStyle(R.color.angryText, R.drawable.angry_button);
                break;
            case "Confused":
                nextButton.setBackgroundResource(R.drawable.confused_button);
                nextButton.setTextColor(getResources().getColor(R.color.confusedText));
                ActivityEditPost.setPostButtonStyle(R.color.confusedText, R.drawable.confused_button);
                break;
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
}
