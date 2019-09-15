package com.nguyen.moodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityUserSettings extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Constant variable
    public final static String DEFAULT_DURATION_KEY = "DURATION_DEFAULT";
    public final static String DEFAULT_PRIVACY_KEY = "PRIVACY_DEFAULT";
    public final static String DEFAULT_APPEAR_KEY = "APPEAR_DEFAULT";


    //UI elements
    Spinner durationSpinner;
    private ImageView mYourName;
    private ImageView mAnonymous;
    private ImageView mPublic;
    private ImageView mOnlyYou;
    private ImageView mUserIcon;
    private CardView mUserNameCardView;
    private TextView mUserNameTextView;
    private Button mBackButton;
    private ImageView dialogIcon;

    //SharedPreferences variable
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


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
        setContentView(R.layout.activity_user_settings);

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        //Set up the UI elements
        durationSpinner = findViewById(R.id.id_duration_spinner_user_settings);
        mYourName = findViewById(R.id.appear_your_name);
        mAnonymous = findViewById(R.id.appear_anonymous);
        mPublic = findViewById(R.id.privacy_public);
        mOnlyYou = findViewById(R.id.privacy_only_you);
        mUserNameCardView = findViewById(R.id.userNameCardView_user_settings);
        mUserNameTextView = findViewById(R.id.userName_user_settings);
        mBackButton = findViewById(R.id.id_back_button_user_settings);
        mUserIcon = findViewById(R.id.userIcon_user_settings);

        //Set up the spinner
        setUpSpinner();

        //Initialize the user name and user mUserIcon
        mUserNameTextView.setText(ActivityBlog.userName);
        mUserIcon.setImageResource(ActivityBlog.userIconResourceId);

        //Initialize the settings
        if(prefs.getString(DEFAULT_APPEAR_KEY, "Your name").equals("Anonymous")){
            mYourName.setImageResource(R.drawable.light_unpicked);
            mAnonymous.setImageResource(R.drawable.light_picked);
        }

        if(prefs.getString(DEFAULT_PRIVACY_KEY, "Public").equals("Only you")){
            mPublic.setImageResource(R.drawable.light_unpicked);
            mOnlyYou.setImageResource(R.drawable.light_picked);
        }



        int defaultHour = prefs.getInt(DEFAULT_DURATION_KEY, 24);
        Log.d("MoodBlog", "default duration: " + defaultHour);
        switch (defaultHour){
            case 1:
                durationSpinner.setSelection(0);
                break;
            case 3:
                durationSpinner.setSelection(1);
                break;
            case 6:
                Log.d("MoodBlog", "default duration 6: " + defaultHour);
                durationSpinner.setSelection(2);
                break;
            case 12:
                durationSpinner.setSelection(3);
                break;
            case 18:
                durationSpinner.setSelection(4);
                break;
            case 24:
                durationSpinner.setSelection(5);
                break;
        }




        //Set up the change user name function
        mUserNameCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUserName dialogUserName = new DialogUserName();
                dialogUserName.show(getSupportFragmentManager(), "MoodBlog");
            }
        });

        //Set up the change user mUserIcon function
        mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUserIcon dialogUserIcon = new DialogUserIcon();
                dialogUserIcon.show(getSupportFragmentManager(), "MoodBlog");
            }
        });



        //Set up the check boxes
        mYourName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYourName.setImageResource(R.drawable.light_picked);
                mAnonymous.setImageResource(R.drawable.light_unpicked);
                editor.putString(DEFAULT_APPEAR_KEY, "Your name");
                editor.apply();
            }
        });

        mAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYourName.setImageResource(R.drawable.light_unpicked);
                mAnonymous.setImageResource(R.drawable.light_picked);
                editor.putString(DEFAULT_APPEAR_KEY, "Anonymous");
                editor.apply();
            }
        });

        mPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublic.setImageResource(R.drawable.light_picked);
                mOnlyYou.setImageResource(R.drawable.light_unpicked);
                editor.putString(DEFAULT_PRIVACY_KEY, "Public");
                editor.apply();
            }
        });

        mOnlyYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublic.setImageResource(R.drawable.light_unpicked);
                mOnlyYou.setImageResource(R.drawable.light_picked);
                editor.putString(DEFAULT_PRIVACY_KEY, "Only you");
                editor.apply();
            }
        });

    }




    private void setUpSpinner(){
        ArrayAdapter<CharSequence> durationSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.duration_user_settings, R.layout.spinner_selected_view);

        durationSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_menu);
        durationSpinner.setAdapter(durationSpinnerAdapter);
        durationSpinner.setOnItemSelectedListener(this);


    }


    @Override
    public void finish() {
        super.finish();
        //spinner selection
        String durationSelection = (String) durationSpinner.getSelectedItem();
        switch(durationSelection){
            case "1 hour" :
                editor.putInt(DEFAULT_DURATION_KEY, 1);
                editor.apply();
                break;
            case "3 hours" :
                editor.putInt(DEFAULT_DURATION_KEY, 3);
                editor.apply();
                break;
            case "6 hours" :
                editor.putInt(DEFAULT_DURATION_KEY, 6);
                editor.apply();
                int defaultHour = prefs.getInt(DEFAULT_DURATION_KEY, 24);
                Log.d("MoodBlog", "default duration: " + defaultHour);
                break;
            case "12 hours" :
                editor.putInt(DEFAULT_DURATION_KEY, 12);
                editor.apply();
                break;
            case "18 hours" :
                editor.putInt(DEFAULT_DURATION_KEY, 18);
                editor.apply();
                break;
            default:
                editor.putInt(DEFAULT_DURATION_KEY, 24);
                editor.apply();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void iconPicked(View v){
        String userID = mAuth.getCurrentUser().getUid();
        if(v.getId() == R.id.male_1_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_1_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m1");
            ActivityBlog.userIconResourceId = R.drawable.male_1;
            mUserIcon.setImageResource(R.drawable.male_1);
        }else if(v.getId() == R.id.male_2_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_2_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m2");
            ActivityBlog.userIconResourceId = R.drawable.male_2;
            mUserIcon.setImageResource(R.drawable.male_2);
        }else if(v.getId() == R.id.male_3_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_3_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m3");
            ActivityBlog.userIconResourceId = R.drawable.male_3;
            mUserIcon.setImageResource(R.drawable.male_3);
        }else if(v.getId() == R.id.male_4_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_4_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m4");
            ActivityBlog.userIconResourceId = R.drawable.male_4;
            mUserIcon.setImageResource(R.drawable.male_4);
        }else if(v.getId() == R.id.male_5_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_5_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m5");
            ActivityBlog.userIconResourceId = R.drawable.male_5;
            mUserIcon.setImageResource(R.drawable.male_5);
        }else if(v.getId() == R.id.male_6_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_6_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m6");
            ActivityBlog.userIconResourceId = R.drawable.male_6;
            mUserIcon.setImageResource(R.drawable.male_6);
        }else if(v.getId() == R.id.male_7_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_7_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m7");
            ActivityBlog.userIconResourceId = R.drawable.male_7;
            mUserIcon.setImageResource(R.drawable.male_7);
        }else if(v.getId() == R.id.male_8_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_8_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m8");
            ActivityBlog.userIconResourceId = R.drawable.male_8;
            mUserIcon.setImageResource(R.drawable.male_8);
        }else if(v.getId() == R.id.male_9_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.male_9_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("m9");
            ActivityBlog.userIconResourceId = R.drawable.male_9;
            mUserIcon.setImageResource(R.drawable.male_9);
        }else if(v.getId() == R.id.female_1_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_1_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm1");
            ActivityBlog.userIconResourceId = R.drawable.female_1;
            mUserIcon.setImageResource(R.drawable.female_1);
        }else if(v.getId() == R.id.female_2_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_2_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm2");
            ActivityBlog.userIconResourceId = R.drawable.female_2;
            mUserIcon.setImageResource(R.drawable.female_2);
        }else if(v.getId() == R.id.female_3_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_3_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm3");
            ActivityBlog.userIconResourceId = R.drawable.female_3;
            mUserIcon.setImageResource(R.drawable.female_3);
        }else if(v.getId() == R.id.female_4_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_4_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm4");
            ActivityBlog.userIconResourceId = R.drawable.female_4;
            mUserIcon.setImageResource(R.drawable.female_4);
        }else if(v.getId() == R.id.female_5_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_5_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm5");
            ActivityBlog.userIconResourceId = R.drawable.female_5;
            mUserIcon.setImageResource(R.drawable.female_5);
        }else if(v.getId() == R.id.female_6_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_6_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm6");
            ActivityBlog.userIconResourceId = R.drawable.female_6;
            mUserIcon.setImageResource(R.drawable.female_6);
        }else if(v.getId() == R.id.female_7_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_7_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm7");
            ActivityBlog.userIconResourceId = R.drawable.female_7;
            mUserIcon.setImageResource(R.drawable.female_7);
        }else if(v.getId() == R.id.female_8_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_8_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm8");
            ActivityBlog.userIconResourceId = R.drawable.female_8;
            mUserIcon.setImageResource(R.drawable.female_8);
        }else if(v.getId() == R.id.female_9_icon){
            if(dialogIcon != null){
                dialogIcon.setBackgroundResource(R.drawable.text_area);
            }
            dialogIcon = v.findViewById(R.id.female_9_icon);
            dialogIcon.setBackgroundResource(R.drawable.sign_in_buttton);
            myRef.child("users").child(userID).child("userIcon").setValue("fm9");
            ActivityBlog.userIconResourceId = R.drawable.female_9;
            mUserIcon.setImageResource(R.drawable.female_9);
        } else mUserIcon.setTag(R.drawable.unknown);

    }
}
