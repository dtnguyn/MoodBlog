package com.nguyen.moodblog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class AdapterRecyclerViewYourPosts extends RecyclerView.Adapter<AdapterRecyclerViewYourPosts.MyYourPostViewHolder>{

    private List<UserPost> mUserPosts;
    private View mView;

    public AdapterRecyclerViewYourPosts(List<UserPost> userPosts) {
        mUserPosts = userPosts;
    }

    @NonNull
    @Override
    public MyYourPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mView = inflater.inflate(R.layout.item_user_post, parent, false);
        MyYourPostViewHolder viewHolder = new MyYourPostViewHolder(mView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyYourPostViewHolder myViewHolder, int i) {
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

        if(mUserPosts.get(i).getDeletionTime().after(Calendar.getInstance().getTime())){
            mUserPosts.get(i).like(); //Call the like() function to allow the onclickListener inside to work
        }

        //Set up the comment button
        mUserPosts.get(i).setCommentButton(myViewHolder.comment);
        mUserPosts.get(i).comment();

        //Set up the like numbers indicator
        mUserPosts.get(i).setNumberOfLikesIndicator(myViewHolder.numberOfLikes);

        //If the post is already liked, then the like button is turned red and disabled
        if(mUserPosts.get(i).getIsLiked().equals("true")){
            myViewHolder.likeButton.setImageResource(R.drawable.like_red);
            myViewHolder.likeButton.setOnClickListener(null);
        } else myViewHolder.likeButton.setImageResource(R.drawable.like_white);
    }

    @Override
    public int getItemCount() {
        return mUserPosts.size();
    }

    public static class MyYourPostViewHolder extends RecyclerView.ViewHolder {

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

        public MyYourPostViewHolder(@NonNull View itemView) {
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
