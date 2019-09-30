package com.nguyen.moodblog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DialogMoodProgress extends AppCompatDialogFragment {

    //UI elements
    SeekBar mMoodSeekBar;
    Button mSaveButton;
    TextView mDate;

    //Member variables
    List<Mood> mMoodProgress;
    ActivityMoodProgress mActivityMoodProgress;

    //SharedPreferences variables
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Calendar calendar = Calendar.getInstance();

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    public DialogMoodProgress(List<Mood> moodProgress, ActivityMoodProgress activityMoodProgress) {
        mMoodProgress = moodProgress;
        mActivityMoodProgress = activityMoodProgress;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_mood_progress, null);

        //Set up the SharedPreferences variable
        editor = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);

        //Set up the UI elements
        mMoodSeekBar = view.findViewById(R.id.seek_bar_mood_progress);
        mSaveButton = view.findViewById(R.id.save_button_mood_progress);
        mDate = view.findViewById(R.id.date_mood_progress);

        //Set up the Date
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        mDate.setText(currentDate);

        //Set up the initial value for the seek bar
        mMoodSeekBar.setProgress(5);


        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        builder.setView(view);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
                String userID = mAuth.getCurrentUser().getUid();

                Mood mood = new Mood(mMoodSeekBar.getProgress(),currentDate);
                if(mMoodProgress.size() == 6){
                    mMoodProgress.remove(0);
                }
                mMoodProgress.add(mood);
                myRef.child("users").child(userID).child("moodProgress").setValue(mMoodProgress);
//                editor.putString(ActivityMoodProgress.MOOD_PROGRESS_UPDATED, "true");
//                editor.apply();
                mActivityMoodProgress.updateBarGraph();

                DialogMoodProgress.super.dismiss();

            }
        });

        return builder.create();
    }


}
