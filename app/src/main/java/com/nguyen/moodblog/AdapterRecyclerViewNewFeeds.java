package com.nguyen.moodblog;

import android.content.Context;
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

public class AdapterRecyclerViewNewFeeds extends RecyclerView.Adapter<AdapterRecyclerViewNewFeeds.MyNewFeedsViewHolder>{
    private List<UserPost> mUserPosts;
    private Context mContext;

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public AdapterRecyclerViewNewFeeds(List<UserPost> userPosts, Context context) {
        mUserPosts = userPosts;
        mContext = context;
        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyNewFeedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_post, parent, false);
        MyNewFeedsViewHolder vHolder = new MyNewFeedsViewHolder(view);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewFeedsViewHolder myViewHolder, int i) {

        myViewHolder.name.setText(mUserPosts.get(i).getUserName());
        myViewHolder.userMood.setText(mUserPosts.get(i).getUserMood());
        myViewHolder.userTag.setText(mUserPosts.get(i).getUserTags());
        myViewHolder.userPostHeading.setText(mUserPosts.get(i).getUserPostHeading());
        myViewHolder.userPost.setText(mUserPosts.get(i).getUserPostBody());
        myViewHolder.postDate.setText(mUserPosts.get(i).getDate());
        myViewHolder.numberOfLikes.setText("" + mUserPosts.get(i).getNumberOfLikes());
        myViewHolder.avatar.setImageResource(mUserPosts.get(i).getUserIconResourceId());

        //Set up the like button
        mUserPosts.get(i).setLikeButton(myViewHolder.likeButton);
        mUserPosts.get(i).like(); //Call the like() function to allow the onclickListener inside to work

        //Set up the comment button
        mUserPosts.get(i).setCommentButton(myViewHolder.comment);
        mUserPosts.get(i).comment();

        //Set up the like numbers indicator
        mUserPosts.get(i).setNumberOfLikesIndicator(myViewHolder.numberOfLikes);

        //If the post is already liked, then the like button is turned red and disabled
        if(mUserPosts.get(i).getIsLiked().equals("true")){
            myViewHolder.likeButton.setImageResource(R.drawable.like_red);
            myViewHolder.likeButton.setOnClickListener(null);
        }
    }

    @Override
    public void onViewRecycled(@NonNull MyNewFeedsViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mUserPosts.size();
    }

    public static class MyNewFeedsViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView userMood;
        private TextView userTag;
        private TextView userPostHeading;
        private TextView userPost;
        private TextView postDate;
        private TextView numberOfLikes;
        private ImageView avatar;
        private ImageView likeButton;
        private TextView comment;


        public MyNewFeedsViewHolder(@NonNull View itemView) {
            super(itemView);
            postDate = itemView.findViewById(R.id.date_post);
            name = itemView.findViewById(R.id.id_user_name_post);
            userMood = itemView.findViewById(R.id.id_user_feeling_post);
            userTag = itemView.findViewById(R.id.id_user_tags_post);
            userPostHeading = itemView.findViewById(R.id.id_user_heading_post);
            userPost = itemView.findViewById(R.id.id_user_writing_post);
            likeButton = itemView.findViewById(R.id.like_button);
            numberOfLikes = itemView.findViewById(R.id.id_number_of_likes);
            avatar = itemView.findViewById(R.id.img_user_avartar);
            comment = itemView.findViewById(R.id.comment_button);
        }

    }

}
