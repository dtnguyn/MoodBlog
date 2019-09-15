package com.nguyen.moodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityAppSettings extends AppCompatActivity {

    //Static variable
    public final static String THEME_KEY = "THEME";

    //Member variable
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Boolean themeChanged = false;

    //UI elements
    private ImageView mNotificationNormal;
    private ImageView mNotificationLess;
    private ImageView mNotificationNone;
    private ImageView mLightMode;
    private ImageView mDarkMode;
    private CardView mUserName;
    private Button mBackButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            themeChanged = extra.getBoolean("themeChanged");
        }

        //Set up the sharedPreferences
        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();

        //Set up the UI elements
        mNotificationNormal = findViewById(R.id.notification_normal);
        mNotificationLess = findViewById(R.id.notification_less);
        mNotificationNone = findViewById(R.id.notification_none);
        mBackButton = findViewById(R.id.id_back_button_app_settings);
        mLightMode = findViewById(R.id.light_mode);
        mDarkMode = findViewById(R.id.dark_mode);

        if(prefs.getString(THEME_KEY, "light").equals("dark")){
            mDarkMode.setImageResource(R.drawable.dark_picked);
            mLightMode.setImageResource(R.drawable.light_unpicked);
        }else {
            mDarkMode.setImageResource(R.drawable.dark_unpicked);
            mLightMode.setImageResource(R.drawable.light_picked);
        }




        //Initialize the app settings
        if(prefs.getString(NotificationBackgroundService.LESS_NOTIFICATION_KEY,"false").equals("true")){
            mNotificationNormal.setImageResource(R.drawable.light_unpicked);
            mNotificationLess.setImageResource(R.drawable.light_picked);
            mNotificationNone.setImageResource(R.drawable.light_unpicked);
        } else if(prefs.getString(NotificationBackgroundService.NONE_NOTIFICATION_KEY,"false").equals("true")){
            mNotificationNormal.setImageResource(R.drawable.light_unpicked);
            mNotificationLess.setImageResource(R.drawable.light_unpicked);
            mNotificationNone.setImageResource(R.drawable.light_picked);
        } else {
            mNotificationNormal.setImageResource(R.drawable.light_picked);
            mNotificationLess.setImageResource(R.drawable.light_unpicked);
            mNotificationNone.setImageResource(R.drawable.light_unpicked);
        }

        //Set up the function of the check box
        mNotificationNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationNormal.setImageResource(R.drawable.light_picked);
                mNotificationLess.setImageResource(R.drawable.light_unpicked);
                mNotificationNone.setImageResource(R.drawable.light_unpicked);
                editor.putString(NotificationBackgroundService.LESS_NOTIFICATION_KEY, "false");
                editor.putString(NotificationBackgroundService.NONE_NOTIFICATION_KEY, "false");
                editor.apply();
            }
        });

        mNotificationLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationNormal.setImageResource(R.drawable.light_unpicked);
                mNotificationLess.setImageResource(R.drawable.light_picked);
                mNotificationNone.setImageResource(R.drawable.light_unpicked);
                editor.putString(NotificationBackgroundService.LESS_NOTIFICATION_KEY, "true");
                editor.apply();
            }
        });

        mNotificationNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationNormal.setImageResource(R.drawable.light_unpicked);
                mNotificationLess.setImageResource(R.drawable.light_unpicked);
                mNotificationNone.setImageResource(R.drawable.light_picked);
                editor.putString(NotificationBackgroundService.NONE_NOTIFICATION_KEY, "true");
                editor.apply();
            }
        });

        mLightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getString(THEME_KEY, "light").equals("light")){

                } else {
                    mLightMode.setImageResource(R.drawable.light_picked);
                    mDarkMode.setImageResource(R.drawable.dark_unpicked);
                    themeChanged = true;
                    editor.putString(THEME_KEY, "light");
                    editor.apply();
                    refreshActivity();
                }

            }
        });

        mDarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getString(THEME_KEY, "light").equals("dark")){

                } else {
                    mLightMode.setImageResource(R.drawable.light_unpicked);
                    mDarkMode.setImageResource(R.drawable.dark_picked);
                    themeChanged = true;
                    editor.putString(THEME_KEY, "dark");
                    editor.apply();
                    refreshActivity();
                }


            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }

    @Override
    public void finish() {

        Log.d("MoodBlog", "theme changed");
        if(themeChanged){
            Intent intent = new Intent(ActivityAppSettings.this, ActivityBlog.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        super.finish();
    }

    @Override
    protected void onResume() {
        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onResume();
    }

    private void refreshActivity(){
        Intent intent = new Intent(this, ActivityAppSettings.class);
        intent.putExtra("themeChanged",themeChanged);
        startActivity(intent);
        super.finish();

    }



}
