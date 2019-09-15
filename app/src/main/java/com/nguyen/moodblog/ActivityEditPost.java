package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ActivityEditPost extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    //UI Elements
    private Spinner nameSpinner;
    private Spinner privacySpinner;
    private Spinner durationSpinner;
    private Button backButton;
    private Button postButton;
    private EditText userPostHeading;
    private ImageView userIcon;

    //Memeber Variables
    private static int mTextButtonColor;
    private static int mButtonColor;
    private static String mUserName;
    private static String mUserMood;
    private static String mUserTags;
    private static String mUserPostHeading;
    private static String mUserPostBody;
    private static int mUserIconResourceID;
    private List<UserPost> mUserPosts;
    List<UserPost> mCurrentUserPosts;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    //SharedPreferences variable
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

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

    public String getUserPostBody() {
        return mUserPostBody;
    }

    public static int getUserIconResourceID() {
        return mUserIconResourceID;
    }


    //Setters

    public static void setUserName(String userName) {
        mUserName = userName;
    }

    public static void setUserMood(String userMood) {
        mUserMood = userMood;
    }

    public static void setUserTags(String userTags) {
        //userTags.replace(" ", "");
        if(userTags.equals("")){
            mUserTags = " ";
            return;
        }else if(userTags.charAt(0) != '#') {
            userTags = "#" + userTags;
        }
        for(int i = 1; i < userTags.length(); i++){
            if(userTags.charAt(i) == '#'){
                userTags = userTags.substring(0, i - 1) + " " + userTags.substring(i);
            }
        }
        Log.d("MoodBlog", "test: " + userTags);
        mUserTags = userTags;
    }


    public static void setUserPostBody(String userPostBody) {
        mUserPostBody = userPostBody;
    }

    public static void setUserIconResourceID(int mUserIconResourceID) {
        ActivityEditPost.mUserIconResourceID = mUserIconResourceID;
    }

    public static void setPostButtonStyle(int textButtonColor, int buttonColor){
        mButtonColor = buttonColor;
        mTextButtonColor = textButtonColor;
    }

    private void setUpPostButtonStyle(){
        postButton.setTextColor(getResources().getColor(mTextButtonColor));
        postButton.setBackgroundResource(mButtonColor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up the SharedPreferences variable
        editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);

        //Initialize the theme
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        mUserPosts = new ArrayList<>();
        mCurrentUserPosts = new ArrayList<>();

        //Link the UI elements
        nameSpinner = findViewById(R.id.id_name_spinner_edit);
        privacySpinner = findViewById(R.id.id_privacy_spinner_edit);
        durationSpinner = findViewById(R.id.id_duration_spinner_edit);
        backButton = findViewById(R.id.id_back_button_edit);
        postButton = findViewById(R.id.id_next_button_edit);
        userPostHeading = findViewById(R.id.id_heading_editText_edit);

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();




        setUpPostButtonStyle();
        setUpSpinner();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check if the total posts of MoodBlog is empty
                if((List<UserPost>) dataSnapshot.child("posts").getValue() != null){
                    mUserPosts = (ArrayList<UserPost>) dataSnapshot.child("posts").getValue();
                }

                //Check if the current user had posted anything before
                String userID = mAuth.getCurrentUser().getUid();
                if((List<UserPost>) dataSnapshot.child("users").child(userID).child("posts").getValue() != null){
                    mCurrentUserPosts = (ArrayList<UserPost>) dataSnapshot.child("users").child(userID).child("posts").getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userPostHeading.getText().toString().equals("")){
                    new Toast(v.getContext()).makeText(v.getContext(), "You have to enter the heading first", Toast.LENGTH_SHORT).show();
                } else {
                    mUserPostHeading = userPostHeading.getText().toString();

                    Calendar calendar = Calendar.getInstance();
                    String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

                    //Spinner 1 selection
                    String itemSelectedSpinnerOne = (String) nameSpinner.getSelectedItem();
                    if(itemSelectedSpinnerOne.equals("Anonymous") ||
                            (itemSelectedSpinnerOne.equals("Appear As...") && prefs.getString(ActivityUserSettings.DEFAULT_APPEAR_KEY, "Your name").equals("Anonymous"))){
                        mUserName = "Anonymous";
                        mUserIconResourceID = R.drawable.unknown;
                    }
                    UserPost userPost = new UserPost(v, mUserName, mUserMood, mUserTags, mUserPostHeading, mUserPostBody, currentDate, mUserIconResourceID);

                    //Spinner 2 selection
                    String itemSelectedSpinnerTwo = (String) privacySpinner.getSelectedItem();
                    if(itemSelectedSpinnerTwo.equals("Only you") || (
                            prefs.getString(ActivityUserSettings.DEFAULT_PRIVACY_KEY, "Public").equals("Only you") && itemSelectedSpinnerTwo.equals("Privacy..."))){
                        userPost.setOnlyOwner(true);
                    }

                    //Spinner 3 selection
                    String itemSelectedSpinnerThree = (String) durationSpinner.getSelectedItem();
                    Date deletionTime = Calendar.getInstance().getTime();
                    switch(itemSelectedSpinnerThree){
                        case "1 hour" :
                            userPost.setDeletionTime(addHoursToDate(deletionTime, 1));
                            break;
                        case "3 hours" :
                            userPost.setDeletionTime(addHoursToDate(deletionTime, 3));
                            break;
                        case "6 hours" :
                            userPost.setDeletionTime(addHoursToDate(deletionTime, 6));
                            break;
                        case "12 hours" :
                            userPost.setDeletionTime(addHoursToDate(deletionTime, 12));
                            break;
                        case "18 hours" :
                            userPost.setDeletionTime(addHoursToDate(deletionTime, 18));
                            break;
                        case "24 hours" :
                            userPost.setDeletionTime(addHoursToDate(deletionTime, 24));
                            break;
                        default:
                            int hours = prefs.getInt(ActivityUserSettings.DEFAULT_DURATION_KEY, 24);
                            userPost.setDeletionTime(addHoursToDate(deletionTime, hours));
                            break;
                    }

                    String userID = mAuth.getCurrentUser().getUid();

                    userPost.setPostKey("" + mUserPosts.size());
                    userPost.setCurrentUserPostKey("" + mCurrentUserPosts.size());
                    userPost.setOwnerID(userID);
                    userPost.setPostID(UUID.randomUUID().toString());


                    mUserPosts.add(userPost);
                    mCurrentUserPosts.add(userPost);

                    myRef.child("posts").setValue(mUserPosts);
                    myRef.child("users").child(userID).child("posts").setValue(mCurrentUserPosts);

                    //Update the latest post so that the like and comment notification can follow this post
                    SharedPreferences.Editor editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putInt(NotificationBackgroundService.LIKE_UPDATE_KEY, 0);
                    editor.putInt(NotificationBackgroundService.COMMENT_UPDATE_KEY, 0);
                    editor.apply();

                    Intent intent = new Intent(ActivityEditPost.this, ActivityBlog.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        });



    }

    private void setUpSpinner(){
        ArrayAdapter<CharSequence> nameSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.appear_as, R.layout.spinner_selected_view);
        ArrayAdapter<CharSequence> privacySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.privacy, R.layout.spinner_selected_view);
        ArrayAdapter<CharSequence> durationSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.duration, R.layout.spinner_selected_view);

        nameSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_menu);
        privacySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_menu);
        durationSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_menu);

        nameSpinner.setAdapter(nameSpinnerAdapter);
        privacySpinner.setAdapter(privacySpinnerAdapter);
        durationSpinner.setAdapter(durationSpinnerAdapter);

        nameSpinner.setOnItemSelectedListener(this);
        privacySpinner.setOnItemSelectedListener(this);
        durationSpinner.setOnItemSelectedListener(this);
    }

    public Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
