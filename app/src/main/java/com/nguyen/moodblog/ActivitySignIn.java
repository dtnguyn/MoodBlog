package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivitySignIn extends AppCompatActivity {

    //static variable
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    //FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //UI References
    private AutoCompleteTextView mUserIDView;
    private EditText mPasswordView;
    private CheckBox mRememberMeCheckBox;

    //Member variables
    private Boolean checked;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Set Up the the UI References
        mUserIDView = findViewById(R.id.userIDTextAreaSignIn);
        mPasswordView = findViewById(R.id.passwordTextAreaSignIn);
        mRememberMeCheckBox = findViewById(R.id.remember_me);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    Log.d("MoodBlog", "I'm hereeee and who cares");
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mRememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor =  getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                if(buttonView.isChecked()){
                    editor.putString("rememberMe", "true");
                    if(!mUserIDView.getText().toString().isEmpty() && !mPasswordView.getText().toString().isEmpty()){
                        editor.putString("userID", mUserIDView.getText().toString());
                        editor.putString("password", mPasswordView.getText().toString());
                    }

                    editor.apply();
                } else {
                    editor.putString("rememberMe", "false");
                }
            }
        });

        //Set up database elements
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    //Call this function when the Sign In button is pressed
    public void signInExistingUser(View view) {
        attemptLogin();
    }

    public void attemptLogin(){
        //Getting the Text from the User input
        String userID = mUserIDView.getText().toString();
        String password = mPasswordView.getText().toString();

        //Check if the user ID and password are blank
        if(userID.isEmpty() && password.isEmpty()){
            new Toast(this).makeText(this, "User ID or Password is blank", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new Toast(this).makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();
            signInWithFirebaseUser(userID, password);
        }

    }


    public void signInWithFirebaseUser(String userID, String password){

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
                    Intent intent = new Intent(ActivitySignIn.this, ActivityBlog.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

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
