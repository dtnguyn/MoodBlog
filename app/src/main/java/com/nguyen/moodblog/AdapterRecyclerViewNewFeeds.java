package com.nguyen.moodblog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyen.moodblog.Interface.LoadMore;

import java.util.Calendar;
import java.util.List;

public class AdapterRecyclerViewNewFeeds extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Constant variables
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    //Member variables
    private List<UserPost> mUserPosts;
    private Context mContext;
    private LoadMore mLoadMore;
    private boolean isLoading;
    private Activity mActivity;
    private int visibleThreshold = 10;
    private int lastVisibleItem;
    private int totalItemCount;


    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public AdapterRecyclerViewNewFeeds(List<UserPost> userPosts, Context context,Activity activity, RecyclerView recyclerView) {
        mUserPosts = userPosts;
        mContext = context;
        mActivity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                Log.d("MoodBlog", "total item: " + totalItemCount);
//                Log.d("MoodBlog", "last item: " + lastVisibleItem);
                if(!isLoading && lastVisibleItem < totalItemCount){
                    if(mLoadMore != null){
                        mLoadMore.onLoadMore();
                        isLoading = true;
                    }
                }

            }
        });

        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    public void setLoadMore(LoadMore loadMore){
        mLoadMore = loadMore;
    }

    @Override
    public int getItemViewType(int position) {
        return mUserPosts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        if(i == VIEW_TYPE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_user_post, parent, false);
            MyNewFeedsViewHolder vHolder = new MyNewFeedsViewHolder(view);
            return vHolder;
        } else if(i == VIEW_TYPE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            LoadingFeedsViewHolder vHolder = new LoadingFeedsViewHolder(view);
            return vHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if(holder instanceof  MyNewFeedsViewHolder){
            MyNewFeedsViewHolder myViewHolder = (MyNewFeedsViewHolder) holder;
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
            if(mUserPosts.get(i).getIsLiked().equals("true")) {

                myViewHolder.likeButton.setImageResource(R.drawable.like_red);
                myViewHolder.likeButton.setOnClickListener(null);
            } else myViewHolder.likeButton.setImageResource(R.drawable.like_white);
        } else if(holder instanceof LoadingFeedsViewHolder){
            LoadingFeedsViewHolder myLoadingViewHolder = (LoadingFeedsViewHolder) holder;
            myLoadingViewHolder.loadingIcon.setIndeterminate(true);
        }
    }



    @Override
    public int getItemCount() {
        return mUserPosts.size();
    }

    public void setLoaded(){
        isLoading = false;
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

    public static class LoadingFeedsViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar loadingIcon;

        public LoadingFeedsViewHolder(@NonNull View itemView) {
            super(itemView);
            loadingIcon = itemView.findViewById(R.id.id_progressBar_loading_item);

        }
    }

}
