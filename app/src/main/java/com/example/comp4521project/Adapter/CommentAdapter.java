package com.example.comp4521project.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.model.Comment;
import com.example.comp4521project.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.comment_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Comment comment = mData.get(position);
        if(comment.getIsGood()) {
            holder.mood.setImageResource(R.drawable.baseline_good_24);
            holder.mood.setBackgroundColor(Color.parseColor("#FFA500"));
        } else{
            holder.mood.setImageResource(R.drawable.baseline_bad_24);
            holder.mood.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        holder.review.setText(comment.getUsername() + ": " + comment.getComment());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mood;
        public TextView review;
        public MyViewHolder(View itemView){
            super(itemView);
            mood = itemView.findViewById(R.id.comment_mood);
            review = itemView.findViewById(R.id.comment_review_item);
        }
    }

    public void add(Comment comment){
        mData.add(comment);
        this.notifyItemInserted(getItemCount() - 1);
    }
}
