package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegister extends AppCompatActivity {
    //Constant Variable
    public static final String USER_NAME_PREFS = "userNamePrefs";
    public static final String USER_NAME_KEY = "username";

    //Member Variables
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private int iconResource;

    //UI References
    private AutoCompleteTextView mUserNameView;
    private AutoCompleteTextView mUserIDView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmationView;
    private ImageView mUserIcon;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserNameView = findViewById(R.id.userNameTextAreaRegistration);
        mUserIDView = findViewById(R.id.userIDTextAreaRegistration);
        mPasswordView = findViewById(R.id.passwordTextAreaRegistration);
        mPasswordConfirmationView = findViewById(R.id.passwordConfirmationTextAreaRegistration);
        mUserIcon = findViewById(R.id.userIcon_register);

        mPasswordConfirmationView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.integer.register_form_finished || actionId == EditorInfo.IME_NULL) {
                    Log.d("MoodBlog", "Helloooooo");
                    attemptRegistration();
                    return true;
                } else Log.d("MoodBlog", "not working");
                return false;
            }
        });

        mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUserIcon dialogUserIcon = new DialogUserIcon();
                dialogUserIcon.show(getSupportFragmentManager(), "MoodBlog");
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

    }

    public void register(View view) {
        attemptRegistration();
    }

    private boolean isUserIDValid(String userID) {

        return userID.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = mPasswordConfirmationView.getText().toString();
        return confirmPassword.equals(password) && password.length() > 4;
    }

    private void attemptRegistration() {

        mUserIDView.setError(null);
        mPasswordView.setError(null);

        String userID = mUserIDView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(userID)) {
            mUserIDView.setError(getString(R.string.error_field_required));
            focusView = mUserIDView;
            cancel = true;
        } else if (!isUserIDValid(userID)) {
            mUserIDView.setError(getString(R.string.error_invalid_email));
            focusView = mUserIDView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Log.d("MoodBlog", "Hi");
            createFirebaseUser();
        }
    }

    private void createFirebaseUser() {
        String userID = mUserIDView.getText().toString() + "@moodblog.com";
        String password = mPasswordView.getText().toString();
        Log.d("MoodBlog", userID);
        mAuth.createUserWithEmailAndPassword(userID, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("MoodBlog", "createUser onComplete: " + task.isSuccessful());
                if(!task.isSuccessful()){
                    Log.d("MoodBlog", "user creation failed");
                    showErrorDialog("User creation failed: " + task.getException().getMessage() );
                } else {
                    createUserData();
                    Intent intent = new Intent(ActivityRegister.this, ActivitySignIn.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    //Save user name
    private void createUserData(){
        String userName = mUserNameView.getText().toString();
        String userID = mAuth.getCurrentUser().getUid();
        if(mUserIcon.getTag() == null){
            mUserIcon.setTag(R.drawable.unknown);
        }
        iconResource = (int) mUserIcon.getTag();
//        SharedPreferences prefs = getSharedPreferences(USER_NAME_PREFS, 0);
//        prefs.edit().putString(USER_NAME_KEY, userName).apply();
        myRef.child("users").child(userID).child("userName").setValue(userName);
        myRef.child("users").child(userID).child("userIcon").setValue(iconResource);
    }


    public void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok , null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void iconPicked(View v){
        if(v.getId() == R.id.male_1_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_1_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_1);
            mUserIcon.setTag(R.drawable.male_1);
        }else if(v.getId() == R.id.male_2_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_2_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_2);
            mUserIcon.setTag(R.drawable.male_2);
        }else if(v.getId() == R.id.male_3_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_3_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_3);
            mUserIcon.setTag(R.drawable.male_3);
        }else if(v.getId() == R.id.male_4_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_4_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_4);
            mUserIcon.setTag(R.drawable.male_4);
        }else if(v.getId() == R.id.male_5_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_5_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_5);
            mUserIcon.setTag(R.drawable.male_5);
        }else if(v.getId() == R.id.male_6_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_6_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_6);
            mUserIcon.setTag(R.drawable.male_6);
        }else if(v.getId() == R.id.male_7_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_7_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_7);
            mUserIcon.setTag(R.drawable.male_7);
        }else if(v.getId() == R.id.male_8_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_8_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_8);
            mUserIcon.setTag(R.drawable.male_8);
        }else if(v.getId() == R.id.male_9_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.male_9_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.male_9);
            mUserIcon.setTag(R.drawable.male_9);
        }else if(v.getId() == R.id.female_1_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_1_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_1);
            mUserIcon.setTag(R.drawable.female_1);
        }else if(v.getId() == R.id.female_2_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_2_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_2);
            mUserIcon.setTag(R.drawable.female_2);
        }else if(v.getId() == R.id.female_3_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_3_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_3);
            mUserIcon.setTag(R.drawable.female_3);
        }else if(v.getId() == R.id.female_4_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_4_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_4);
            mUserIcon.setTag(R.drawable.female_4);
        }else if(v.getId() == R.id.female_5_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_5_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_5);
            mUserIcon.setTag(R.drawable.female_5);
        }else if(v.getId() == R.id.female_6_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_6_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_6);
            mUserIcon.setTag(R.drawable.female_6);
        }else if(v.getId() == R.id.female_7_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_7_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_7);
            mUserIcon.setTag(R.drawable.female_7);
        }else if(v.getId() == R.id.female_8_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_8_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_8);
            mUserIcon.setTag(R.drawable.female_8);
        }else if(v.getId() == R.id.female_9_icon){
            if(icon != null){
                icon.setBackgroundResource(R.drawable.text_area);
            }
            icon = v.findViewById(R.id.female_9_icon);
            icon.setBackgroundResource(R.drawable.sign_in_buttton);
            mUserIcon.setImageResource(R.drawable.female_9);
            mUserIcon.setTag(R.drawable.female_9);
        } else mUserIcon.setTag(R.drawable.unknown);
    }


}
