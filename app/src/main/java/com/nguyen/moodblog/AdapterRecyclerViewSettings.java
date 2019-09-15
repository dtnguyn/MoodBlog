package com.nguyen.moodblog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRecyclerViewSettings extends RecyclerView.Adapter<AdapterRecyclerViewSettings.MySettingsViewHolder> {

    private List<String> mSettingItems;
    Context mContext;

    public AdapterRecyclerViewSettings(List<String> settingItems, Context context) {
        mSettingItems = settingItems;
        mContext = context;
    }

    @NonNull
    @Override
    public MySettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_setting, parent, false);
        MySettingsViewHolder viewHolder = new MySettingsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MySettingsViewHolder mySettingsViewHolder, int i) {
        mySettingsViewHolder.settingName.setText(mSettingItems.get(i));
        switch (i){
            case 0:
                mySettingsViewHolder.settingIcon.setImageResource(R.drawable.list);
                mySettingsViewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.oldPostsButton));
                mySettingsViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActivityYourPosts.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 1:
                mySettingsViewHolder.settingIcon.setImageResource(R.drawable.moodprogress);
                mySettingsViewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.moodProgressButton));
                break;
            case 2:
                mySettingsViewHolder.settingIcon.setImageResource(R.drawable.app);
                mySettingsViewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.oldPostsButton));
                mySettingsViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActivityAppSettings.class);
                        mContext.startActivity(intent);
                    }
                });
                mySettingsViewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.appSettingsButton));
                break;
            case 3:
                mySettingsViewHolder.settingIcon.setImageResource(R.drawable.settings);
                mySettingsViewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.userSettingsButton));
                mySettingsViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActivityUserSettings.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mSettingItems.size();
    }

    public static class MySettingsViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView settingName;
        private ImageView settingIcon;

        public MySettingsViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_setting);
            settingIcon = itemView.findViewById(R.id.setting_icon);
            settingName = itemView.findViewById(R.id.setting_name);
        }
    }
}
