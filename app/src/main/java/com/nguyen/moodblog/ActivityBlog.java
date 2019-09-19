package com.nguyen.moodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;


public class ActivityBlog extends AppCompatActivity {
    //Constant variable
    private final int DAILY_NOTIFICATION_ID = 1;
    private final int LIKE_COMMENT_NOTIFICATION_ID = 2;
    public final static long NOTIFICATION_PERIOD = 900000;

    //Static variables
    public static int userIconResourceId;
    public static String userName;
    public static int tabIndex = 0;

    //Member variables
    private TabLayout mTabLayout;
    private AppBarLayout mAppBarLayout;
    private ViewPager mViewPager;
    private FragmentNewFeed mFragmentNewFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences.Editor editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        mTabLayout = findViewById(R.id.tabLayout);
        mAppBarLayout = findViewById(R.id.appBar);
        mViewPager = findViewById(R.id.viewpaper);


        String reverseValue = prefs.getString(NotificationBackgroundService.DAILY_NOTIFICATION_UPDATE_KEY, "null");

        editor.putString(NotificationBackgroundService.DAILY_NOTIFICATION_UPDATE_KEY, "true");
        editor.apply();

        scheduleNotification();

        editor.putString(NotificationBackgroundService.DAILY_NOTIFICATION_UPDATE_KEY, reverseValue);
        editor.apply();

        AdapterViewPaper adapter = new AdapterViewPaper(getSupportFragmentManager());
        adapter.AddFragment(new FragmentMenuPost(this), "");
        mFragmentNewFeed = new FragmentNewFeed();
        adapter.AddFragment( mFragmentNewFeed, "");
        adapter.AddFragment(new FragmentUserProfile(), "");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ff5959"));


        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_compose);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_new_feed);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_user_profile);

        mViewPager.setCurrentItem(tabIndex);

        //Changing the color when changing between tabs
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor;

                if(tab.getPosition() == 0){
                    tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.iconSelectedTabOne);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ff5959"));
                } else if(tab.getPosition() == 1){
                    tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.iconSelectedTabTwo);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#facf5a"));
                } else if(tab.getPosition() == 2){
                    tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.iconSelectedTabThree);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#49beb7"));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.iconUnselected);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        //Initiate the first tab color when the activity first created
        onTabSelectedListener.onTabSelected(mTabLayout.getTabAt(0));
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);

    }

    public void goToComposeActivity(View view) {
        Intent intent = new Intent(ActivityBlog.this, ActivityCompose.class);
        finish();
        startActivity(intent);
    }



    @Override
    protected void onResume() {
        Log.d("MoodBlog","calledddddd");
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void scheduleNotification(){

        ComponentName componentName = new ComponentName(this, NotificationBackgroundService.class);
        JobInfo jobInfo = new JobInfo.Builder(DAILY_NOTIFICATION_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(NOTIFICATION_PERIOD)
                .build();


        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);


    }

//    public void scheduleLikeAndCommentNotification(){
//        ComponentName componentName = new ComponentName(this, NotificationBackgroundService.class);
//
//
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(likeAndCommentsNotificationJobInfo);
//    }
}

