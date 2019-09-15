package com.nguyen.moodblog;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationBackgroundService extends JobService {

    //Constants
    private final int DAILY_CHANNEL_ID = 1;
    private final int LIKE_COMMENT_CHANNEL_ID = 2;
    public final static String DAILY_NOTIFICATION_UPDATE_KEY = "UPDATE_DAILY";
    public final static String LIKE_UPDATE_KEY = "UPDATE_LIKES";
    public final static String COMMENT_UPDATE_KEY = "UPDATE_COMMENTS";
    public final static String LESS_NOTIFICATION_KEY = "LESS";
    public final static String NONE_NOTIFICATION_KEY = "NONE";

    //Member Variable
    private Boolean jobCancelled = false;
    private NotificationManagerCompat mNotificationManagerCompat;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int numberOfLikesIncrease = 0;
    private int numberOfCommentsIncrease = 0;


    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("MoodBlog", "Job started");
        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getBackGroundUpdate(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("MoodBlog", "Job is cancelled");
        jobCancelled = true;
        return true;
    }

    private void getBackGroundUpdate(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }
                mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                if(prefs.getString(LESS_NOTIFICATION_KEY, "false").equals("true")){
                    sendOnLikeAndCommentUpdatesChannel();
                } else if(prefs.getString(NONE_NOTIFICATION_KEY, "false").equals("true")){

                } else {
                    sendOnLikeAndCommentUpdatesChannel();
                    sendOnDailyNotificationChannel();
                }



                jobFinished(params, false);

            }
        }).start();
    }



    public void sendOnDailyNotificationChannel(){
        //Set up when the user clicks on the notification, it will jump into the app
        Intent activityIntent = new Intent(getApplicationContext(), ActivityMain.class);
        PendingIntent contentIntent =  PendingIntent.getActivity(this, 0, activityIntent, 0);

        //Set up action button for notification
        Intent broadcastIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent actionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), NotificationChannelCreator.DAILY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_compose)
                .setContentTitle("How's your day going?")
                .setContentText("Update your Mood Progress")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_compose, "Bad", actionIntent)
                .setColor(Color.rgb(255,189,89))
                .addAction(R.drawable.ic_compose, "Good",actionIntent)
                .setOnlyAlertOnce(true)
                .build();


        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(prefs.getString(DAILY_NOTIFICATION_UPDATE_KEY, "null").equals("null")){
            editor.putString(DAILY_NOTIFICATION_UPDATE_KEY, "false");
            editor.apply();
        }

        Log.d("MoodBlog", "daily update: " + prefs.getString(DAILY_NOTIFICATION_UPDATE_KEY, "null"));

        if(currentHour >= 18 && currentHour <= 23 && prefs.getString(DAILY_NOTIFICATION_UPDATE_KEY, "null").equals("false")){
            mNotificationManagerCompat.notify(DAILY_CHANNEL_ID, notification);
            editor.putString(DAILY_NOTIFICATION_UPDATE_KEY, "true");
            editor.apply();
        } else if(currentHour > 1 && currentHour < 18){
            editor.putString(DAILY_NOTIFICATION_UPDATE_KEY, "false");
            editor.apply();
        }

        Log.d("MoodBlog", "daily update2: " + prefs.getString(DAILY_NOTIFICATION_UPDATE_KEY, "null"));
    }

    public void sendOnLikeAndCommentUpdatesChannel(){


        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();

        if(prefs.getInt(LIKE_UPDATE_KEY, -1) == -1 ){
            editor.putInt(LIKE_UPDATE_KEY, 0);
            editor.putInt(COMMENT_UPDATE_KEY, 0);
            editor.apply();
        }

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userID = mAuth.getCurrentUser().getUid();
                if(dataSnapshot.child("users").child(userID).child("posts").getValue() != null){
                    long latestPostIndex = dataSnapshot.child("users").child(userID).child("posts").getChildrenCount() - 1;
                    if(dataSnapshot.child("users").child(userID).child("posts").child("" + latestPostIndex).getValue(UserPost.class).getDeletionTime().after(Calendar.getInstance().getTime())){
                        Log.d("MoodBlog", "si");
                        int numberOfLikes = dataSnapshot.child("users").child(userID).child("posts").child("" + latestPostIndex).getValue(UserPost.class).getNumberOfLikes();
                        int numberOfComments = (int) dataSnapshot.child("users").child(userID).child("posts").child("" + latestPostIndex).child("comments").getChildrenCount();

                        if(numberOfLikes > prefs.getInt(LIKE_UPDATE_KEY, 0) || numberOfComments > prefs.getInt(COMMENT_UPDATE_KEY,0)){
                            int numberOfLikesIncrease = numberOfLikes - prefs.getInt(LIKE_UPDATE_KEY, 0);
                            int numberOfCommentsIncrease = numberOfComments - prefs.getInt(COMMENT_UPDATE_KEY,0);
//                            editor.putInt(LIKE_UPDATE_KEY, numberOfLikes);
//                            editor.putInt(COMMENT_UPDATE_KEY, numberOfComments);
//                            editor.apply();

                            //Set up when the user clicks on the notification, it will jump into the app
                            Intent activityIntent = new Intent(getApplicationContext(), ActivityYourPosts.class);
                            PendingIntent contentIntent =  PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

                            String contentText = "";

                            if(numberOfLikesIncrease > 0 && numberOfCommentsIncrease > 0){
                                contentText = "Your post got " + numberOfLikesIncrease + " likes and " + numberOfComments + " new comments";
                            } else if(numberOfLikesIncrease > 0){
                                contentText = "Your post got " + numberOfLikesIncrease + " likes";
                            } else if(numberOfCommentsIncrease > 0){
                                contentText = "Your post got " + numberOfCommentsIncrease + " new comments";
                            }
                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), NotificationChannelCreator.DAILY_NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_compose)
                                    .setContentTitle("Your Post Update")
                                    .setContentText(contentText)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(true)
                                    .setColor(Color.rgb(255,189,89))
                                    .setOnlyAlertOnce(true)
                                    .build();
                            mNotificationManagerCompat.notify(LIKE_COMMENT_CHANNEL_ID, notification);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
