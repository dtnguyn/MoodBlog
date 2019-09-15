package com.nguyen.moodblog;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannelCreator extends Application {
    public static final String DAILY_NOTIFICATION_CHANNEL_ID = "daily";
    public static final String LIKE_COMMENT_NOTIFICATION_CHANNEL_ID = "likeComment";



    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //This notification channel will ask user every day.
            NotificationChannel dailyChannel = new NotificationChannel(
                    DAILY_NOTIFICATION_CHANNEL_ID,
                    "Daily Updates",
                    NotificationManager.IMPORTANCE_DEFAULT);

            dailyChannel.enableLights(true);
            dailyChannel.enableVibration(true);
            dailyChannel.setDescription("Mood Blog want to get updates from your feeling");

            //This notification channel will update the number and likes
            NotificationChannel likesAndCommentsUpdateChannel = new NotificationChannel(
                    LIKE_COMMENT_NOTIFICATION_CHANNEL_ID,
                    "Update likes and comments",
                    NotificationManager.IMPORTANCE_DEFAULT);

            dailyChannel.enableLights(true);
            dailyChannel.enableVibration(true);
            dailyChannel.setDescription("Mood Blog want to send you updates of your post");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(dailyChannel);
            manager.createNotificationChannel(likesAndCommentsUpdateChannel);





        }
    }
}
