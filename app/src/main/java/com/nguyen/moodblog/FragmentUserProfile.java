package com.nguyen.moodblog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserProfile extends Fragment {
    //UI elements
    View mView;
    ImageView avatarView;
    TextView userNameView;
    TextView logOutButton;

    //Member Variables
    List<String> mSettingItems;
    RecyclerView mRecyclerView;
    AdapterRecyclerViewSettings mAdapterRecyclerViewSettings;

    public FragmentUserProfile() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        avatarView = mView.findViewById(R.id.img_user_avatar_profile);
        userNameView = mView.findViewById(R.id.name_profile);
        logOutButton = mView.findViewById(R.id.id_log_out_button);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityMain.class);
                startActivity(intent);
                SharedPreferences.Editor editor =  getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME,
                                                                                        getActivity().MODE_PRIVATE).edit();
                editor.putString("rememberMe", "false");
                editor.apply();
                getActivity().finish();
            }
        });

        avatarView.setImageResource(ActivityBlog.userIconResourceId);
        userNameView.setText(ActivityBlog.userName);

        //Setting up the recyclerView
        mRecyclerView = mView.findViewById(R.id.setting_recyclerView);
        mAdapterRecyclerViewSettings = new AdapterRecyclerViewSettings(mSettingItems, getContext());
        mRecyclerView.setAdapter(mAdapterRecyclerViewSettings);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mView;

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

        mSettingItems = new ArrayList<>();
        mSettingItems.add("Your Posts");
        mSettingItems.add("Mood Progress");
        mSettingItems.add("App Settings");
        mSettingItems.add("User Settings");
    }



    @Override
    public void onResume() {
        super.onResume();
        userNameView.setText(ActivityBlog.userName);
        avatarView.setImageResource(ActivityBlog.userIconResourceId);
    }
}
