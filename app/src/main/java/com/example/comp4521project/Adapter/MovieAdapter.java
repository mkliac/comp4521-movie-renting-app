package com.example.comp4521project.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.model.MovieBrief;
import com.example.comp4521project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder>{

    private Context mContext;
    private String user;
    private List<MovieBrief> mData;
    private onCardListener onCardListener;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public MovieAdapter(Context mContext, List<MovieBrief> mData, String user, onCardListener onCardListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.user = user;
        this.onCardListener = onCardListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.movie_cart_item_view, parent, false);

        return new MyViewHolder(view, onCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        String id = mData.get(position).getId();
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.movieThumbnail.setImageResource(R.drawable.my_movie);
        holder.movieThumbnail.setBackground(null);

        //get movie status, 1 == on cart, 2 == purchased
        DatabaseReference purchasedStatusRef = rootRef.child("purchaseStatus").child(user).child(id);
        purchasedStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String val = dataSnapshot.getValue().toString();
                    if(val.equals("1")){
                        holder.icon.setImageResource(R.drawable.cart);
                        holder.icon.setBackgroundColor(Color.parseColor("#00CACA"));
                        holder.icon.setVisibility(View.VISIBLE);
                    }
                    else if(val.equals("2")){
                        holder.icon.setImageResource(R.drawable.tick);
                        holder.icon.setBackgroundColor(Color.parseColor("#0DCA00"));
                        holder.icon.setVisibility(View.VISIBLE);
                    }
                }else{
                    holder.icon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.icon.setImageResource(R.drawable.warning);
                holder.icon.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.icon.setVisibility(View.VISIBLE);
            }
        });

        // get movie thumbnail
        String path = "movies/"+id+"/"+id+".jpg";
        StorageReference imageRef = storageRef.child(path);
        holder.popularity.setText(mData.get(position).getPopularity().toString());
        final long IN_MB = 1024 * 1024;
        imageRef.getBytes(IN_MB).addOnSuccessListener(bytes -> {
            Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.movieThumbnail.setBackground(new BitmapDrawable(mContext.getResources(), a));
            holder.movieThumbnail.setImageResource(android.R.color.transparent);
            holder.movieThumbnail.setImageBitmap(a);
            holder.progressBar.setVisibility(View.INVISIBLE);

        });

        holder.popularity.setText(mData.get(position).getPopularity().toString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void changeStatus(String movieId){
        for(int i = 0; i < getItemCount(); i++){
            notifyItemChanged(i);
        }

    }

    public List<MovieBrief> returnList(){return mData;}

    public interface onCardListener{
        void onCardClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        ImageView movieThumbnail;
        ProgressBar progressBar;
        TextView popularity;
        onCardListener onCardListener;

        public MyViewHolder(View itemView, onCardListener onCardListener) {
            super(itemView);

            icon = itemView.findViewById(R.id.movie_cart_item_status_icon);
            movieThumbnail = itemView.findViewById(R.id.movie_cart_item_movie_image);
            progressBar = itemView.findViewById(R.id.movie_cart_item_progressBar);
            popularity = itemView.findViewById(R.id.movie_cart_item_popularity);
            this.onCardListener = onCardListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCardListener.onCardClick(getAdapterPosition());
        }
    }

    public void addItem(MovieBrief movieBrief){
        mData.add(movieBrief);
        this.notifyItemInserted(getItemCount()-1);
    }

    public void removeAllItem(){
        int size = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, size);
    }
}
