package com.nguyen.moodblog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentMenuPost extends Fragment {
    View mView;
    private Context mContext;
    private RecyclerView myRecyclerView;
    private List<MenuButton> mMenuButtons;
    String userMood;

    public FragmentMenuPost(){

    }

    @SuppressLint("ValidFragment")
    public FragmentMenuPost(Context context) {
        mContext = context;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_menu_post, container, false);
        myRecyclerView = mView.findViewById(R.id.menu_recyclerView);
        Log.d("MoodBlog", "menu: " + mMenuButtons.size());
        AdapterRecyclerViewMenuButtons recyclerViewAdapter = new AdapterRecyclerViewMenuButtons(getContext(), mMenuButtons);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(recyclerViewAdapter);

        return mView;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        Log.d("MoodBlog", currentDate);

        mMenuButtons = new ArrayList<>();
        mMenuButtons.add(new MenuButton(mContext,"How do you feel today?\n" + currentDate, R.drawable.transparent_button, R.font.josefin_sans_regular, R.color.registerAndSignInText));
        mMenuButtons.add(new MenuButton(mContext,"Happy", R.drawable.happy_button, R.font.brusher, R.color.happyText));
        mMenuButtons.add(new MenuButton(mContext,"Loved", R.drawable.loved_button, R.font.fredoka_one, R.color.loveText));
        mMenuButtons.add(new MenuButton(mContext,"Nervous", R.drawable.nervous_button, R.font.abril_fatface, R.color.nervousText));
        mMenuButtons.add(new MenuButton(mContext,"Sad", R.drawable.sad_button, R.font.essays, R.color.sadText));
        mMenuButtons.add(new MenuButton(mContext,"Angry", R.drawable.angry_button, R.font.baloo, R.color.angryText));
        mMenuButtons.add(new MenuButton(mContext,"Excited", R.drawable.excited_button, R.font.diarygirl, R.color.excitedText));
        mMenuButtons.add(new MenuButton(mContext,"Tired", R.drawable.tired_button, R.font.firasansbold, R.color.tiredText));
        mMenuButtons.add(new MenuButton(mContext,"Confused", R.drawable.confused_button, R.font.sedgwickave, R.color.confusedText));
    }
}
