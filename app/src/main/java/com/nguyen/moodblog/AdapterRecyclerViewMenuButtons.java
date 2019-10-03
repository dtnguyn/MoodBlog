package com.nguyen.moodblog;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRecyclerViewMenuButtons extends RecyclerView.Adapter<AdapterRecyclerViewMenuButtons.MyMenuButtonsViewHolder> {

    Context mContext;
    List<MenuButton> mMenuButtons;

    public AdapterRecyclerViewMenuButtons(Context context, List<MenuButton> menuButtons) {
        mContext = context;
        mMenuButtons = menuButtons;
    }

    @NonNull
    @Override
    public MyMenuButtonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_menu_button,parent,false);
        MyMenuButtonsViewHolder vHolder = new MyMenuButtonsViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyMenuButtonsViewHolder myViewHolder, int i) {
        if(i == 0){
            myViewHolder.menuButton.setTextSize(20);
            myViewHolder.menuButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250));
            //myViewHolder.menuButton.setPadding(230,0,0,0);
            myViewHolder.menuButton.setGravity(Gravity.CENTER);
            myViewHolder.menuButton.setText(mMenuButtons.get(i).getText());
            myViewHolder.menuButton.setTypeface(mMenuButtons.get(i).getFont());
            myViewHolder.menuButton.setBackgroundResource(mMenuButtons.get(i).getBackgroundColor());
            myViewHolder.menuButton.setTextColor(mMenuButtons.get(i).getTextColor());
            myViewHolder.userMood = mMenuButtons.get(i).getText();
            myViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivityMoodProgress.class);
                    mContext.startActivity(intent);
                }
            });
        } else {
            myViewHolder.menuButton.setText(mMenuButtons.get(i).getText());
            myViewHolder.menuButton.setTypeface(mMenuButtons.get(i).getFont());
            myViewHolder.menuButton.setBackgroundResource(mMenuButtons.get(i).getBackgroundColor());
            myViewHolder.menuButton.setTextColor(mMenuButtons.get(i).getTextColor());
            myViewHolder.userMood = mMenuButtons.get(i).getText();
            myViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompose.mUserMood = myViewHolder.userMood;
                    Intent intent = new Intent(mContext, ActivityCompose.class);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mMenuButtons.size();
    }

    public static class MyMenuButtonsViewHolder extends RecyclerView.ViewHolder{

        private Button menuButton;
        private String userMood;


        public MyMenuButtonsViewHolder(@NonNull View itemView) {
            super(itemView);
            menuButton = itemView.findViewById(R.id.menu_button);

        }

    }



}
