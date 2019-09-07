package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ActivityMain extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    //FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //Set up database elements
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        if(prefs.getString("rememberMe", "false").equals("true")){
            String userID = prefs.getString("userID", "null");
            String password = prefs.getString("password", "null");
            mAuth.signInWithEmailAndPassword(userID + "@moodblog.com", password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.d("MoodBlog", "signInWithEmail() onComplete: " + task.isSuccessful()); //Check if the user signs in successfully

                    if(!task.isSuccessful()){
                        Log.d("MoodBlog", "Problem signing in: " + task.getException());
                        showErrorDialog("Problem signing in: " + task.getException().getMessage());

                    } else {
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userID = mAuth.getCurrentUser().getUid();
                                ActivityBlog.userIconResourceId = dataSnapshot.child("users").child(userID).child("userIcon").getValue(int.class);
                                ActivityBlog.userName = dataSnapshot.child("users").child(userID).child("userName").getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(ActivityMain.this, ActivityBlog.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }else setContentView(R.layout.activity_main);
    }

    public void registerNewUser(View view) {
        Intent intent = new Intent(this, ActivityRegister.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, ActivitySignIn.class);
        startActivity(intent);
    }

    public void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok , null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
