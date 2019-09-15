package com.nguyen.moodblog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogUserName extends AppCompatDialogFragment {

    //UI elements
    EditText mNewUserNameEditText;
    Button mSaveButton;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private TextView mUserName;

    public void DialogUserName(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_user_name, null);

        mNewUserNameEditText = view.findViewById(R.id.new_user_name);
        mSaveButton = view.findViewById(R.id.save_button);
        mUserName = getActivity().findViewById(R.id.userName_user_settings);

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        builder.setView(view);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNewUserNameEditText.getText().toString().isEmpty()){
                    new Toast(getContext()).makeText(getContext(), "You have to enter the user name first", Toast.LENGTH_SHORT).show();
                } else {
                    String userID = mAuth.getCurrentUser().getUid();
                    myRef.child("users").child(userID).child("userName").setValue(mNewUserNameEditText.getText().toString());
                    mUserName.setText(mNewUserNameEditText.getText().toString());
                    ActivityBlog.userName = mNewUserNameEditText.getText().toString();
                    DialogUserName.super.dismiss();

                }
            }
        });

        return builder.create();
    }


}
