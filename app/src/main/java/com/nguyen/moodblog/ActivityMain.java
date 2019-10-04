package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
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
                        setContentView(R.layout.activity_main);
                    } else {
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userID = mAuth.getCurrentUser().getUid();
                                ActivityBlog.userName = dataSnapshot.child("users").child(userID).child("userName").getValue(String.class);
                                String icon = dataSnapshot.child("users").child(userID).child("userIcon").getValue(String.class);
                                switch(icon){
                                    case "m1":
                                        ActivityBlog.userIconResourceId = R.drawable.male_1;
                                        break;
                                    case "m2":
                                        ActivityBlog.userIconResourceId = R.drawable.male_2;
                                        break;
                                    case "m3":
                                        ActivityBlog.userIconResourceId = R.drawable.male_3;
                                        break;
                                    case "m4":
                                        ActivityBlog.userIconResourceId = R.drawable.male_4;
                                        break;
                                    case "m5":
                                        ActivityBlog.userIconResourceId = R.drawable.male_5;
                                        break;
                                    case "m6":
                                        ActivityBlog.userIconResourceId = R.drawable.male_6;
                                        break;
                                    case "m7":
                                        ActivityBlog.userIconResourceId = R.drawable.male_7;
                                        break;
                                    case "m8":
                                        ActivityBlog.userIconResourceId = R.drawable.male_8;
                                        break;
                                    case "m9":
                                        ActivityBlog.userIconResourceId = R.drawable.male_9;
                                        break;
                                    case "fm1":
                                        ActivityBlog.userIconResourceId = R.drawable.female_1;
                                        break;
                                    case "fm2":
                                        ActivityBlog.userIconResourceId = R.drawable.female_2;
                                        break;
                                    case "fm3":
                                        ActivityBlog.userIconResourceId = R.drawable.female_3;
                                        break;
                                    case "fm4":
                                        ActivityBlog.userIconResourceId = R.drawable.female_4;
                                        break;
                                    case "fm5":
                                        ActivityBlog.userIconResourceId = R.drawable.female_5;
                                        break;
                                    case "fm6":
                                        ActivityBlog.userIconResourceId = R.drawable.female_6;
                                        break;
                                    case "fm7":
                                        ActivityBlog.userIconResourceId = R.drawable.female_7;
                                        break;
                                    case "fm8":
                                        ActivityBlog.userIconResourceId = R.drawable.female_8;
                                        break;
                                    case "fm9":
                                        ActivityBlog.userIconResourceId = R.drawable.female_9;
                                        break;
                                    default:
                                        ActivityBlog.userIconResourceId = R.drawable.unknown;
                                        ;                            }
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

    @Override
    protected void onResume() {
        SharedPreferences prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onResume();
    }
}
