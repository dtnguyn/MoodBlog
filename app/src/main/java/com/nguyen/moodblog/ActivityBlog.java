package com.nguyen.moodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class ActivityBlog extends AppCompatActivity {

    private TabLayout mTabLayout;
    private AppBarLayout mAppBarLayout;
    private ViewPager mViewPager;
    private FragmentNewFeed mFragmentNewFeed;
    public static int userIconResourceId;
    public static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        mTabLayout = findViewById(R.id.tabLayout);
        mAppBarLayout = findViewById(R.id.appBar);
        mViewPager = findViewById(R.id.viewpaper);


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
    protected void onResumeFragments() {
        super.onResumeFragments();

    }
}

