package com.nguyen.moodblog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterRecyclerViewComments extends RecyclerView.Adapter<AdapterRecyclerViewComments.MyCommentsViewHolder>{
    private List<Comment> mPostComments;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public AdapterRecyclerViewComments(List<Comment> postComments) {
        mPostComments = postComments;

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }


    @NonNull
    @Override
    public MyCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        MyCommentsViewHolder vHolder = new MyCommentsViewHolder(view);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCommentsViewHolder myCommentsViewHolder, int i) {

        myCommentsViewHolder.name.setText(mPostComments.get(i).getOwnerName());
        myCommentsViewHolder.avartar.setImageResource(mPostComments.get(i).getAvatarResourceID());
        myCommentsViewHolder.commentContent.setText(mPostComments.get(i).getCommentContent());
        myCommentsViewHolder.date.setText(mPostComments.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return mPostComments.size();
        //return 0;
    }

    public static class MyCommentsViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView date;
        private TextView commentContent;
        private ImageView avartar;

        public MyCommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            avartar = itemView.findViewById(R.id.img_comment_avartar);
            name = itemView.findViewById(R.id.id_user_name_comment);
            commentContent = itemView.findViewById(R.id.id_comment_content);
            date = itemView.findViewById(R.id.date_comment);
        }
    }
}
