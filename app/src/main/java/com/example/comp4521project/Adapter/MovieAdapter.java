package com.example.comp4521project.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.MovieData.MovieShort;
import com.example.comp4521project.R;
import com.example.comp4521project.MovieData.MovieShort;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private List<MovieShort> mData;
    private String currentCategory = "none";
    private onCardListener onCardListener;
    MovieAdapter mAdapt;
    /*
    all categories:
    action
    adventure
    cartoon
    comedy
    documentary
    horror
    mystery
    sci-fi

    search
    advanced search
     */

    public MovieAdapter(Context mContext, List<MovieShort> mData, String user, onCardListener onCardListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.user = user;
        this.onCardListener = onCardListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        String id;
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.movie_small_card_view, parent, false);

        return new MyViewHolder(view, onCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String id = mData.get(position).getId();
        holder.movieImage.setImageResource(R.drawable.custom_profile_movies_24dp);
        holder.movieImage.setBackground(null);
        holder.movieProgressBar.setVisibility(View.VISIBLE);

        DatabaseReference purchasedStatusRef = FirebaseDatabase.getInstance().getReference().child("purchaseStatus").child(user).child(id);
        purchasedStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){ Log.e("hello", "OH NO U GOT 0");holder.statusIcon.setVisibility(View.INVISIBLE); }
                else if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().toString().equals("1")){
                        Log.e("hello", "U GOT 1");
                        holder.statusIcon.setImageResource(R.drawable.custom_ic_shopping_cart_24dp);
                        holder.statusIcon.setBackgroundColor(Color.parseColor("#00CACA"));
                        holder.statusIcon.setVisibility(View.VISIBLE);
                    }
                    else if(dataSnapshot.getValue().toString().equals("2")){
                        Log.e("hello", "U GOT 2");
                        holder.statusIcon.setImageResource(R.drawable.custom_ic_tick_24dp);
                        holder.statusIcon.setBackgroundColor(Color.parseColor("#0DCA00"));
                        holder.statusIcon.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.statusIcon.setImageResource(R.drawable.custom_ic_warning_24dp);
                holder.statusIcon.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.statusIcon.setVisibility(View.VISIBLE);
            }
        });
        String path = "movies/"+id+"/"+id+".jpg";
        StorageReference imageRef = storageRef.child(path);
        holder.popularityValue.setText(mData.get(position).getPopularity().toString());
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.movieImage.setImageResource(android.R.color.transparent);
                holder.movieImage.setBackground(new BitmapDrawable(mContext.getResources(), a));
                holder.movieImage.setImageBitmap(a);
                holder.movieProgressBar.setVisibility(View.INVISIBLE);

            }
        });
        path = "movies/"+id+"/"+id+".png";
        imageRef = storageRef.child(path);
        holder.popularityValue.setText(mData.get(position).getPopularity().toString());
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.movieImage.setImageResource(android.R.color.transparent);
                holder.movieImage.setBackground(new BitmapDrawable(mContext.getResources(), a));
                holder.movieImage.setImageBitmap(a);
                holder.movieProgressBar.setVisibility(View.INVISIBLE);

            }
        });

        holder.popularityValue.setText(mData.get(position).getPopularity().toString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieImage;
        TextView popularityValue;
        ImageView statusIcon;
        ProgressBar movieProgressBar;
        onCardListener onCardListener;

        public MyViewHolder(View itemView, onCardListener onCardListener) {
            super(itemView);

            movieImage = (ImageView) itemView.findViewById(R.id.movieImage);
            popularityValue = (TextView) itemView.findViewById(R.id.popularityValue);
            statusIcon = (ImageView) itemView.findViewById(R.id.statusIcon);
            movieProgressBar = (ProgressBar) itemView.findViewById(R.id.movieProgressBar);
            this.onCardListener = onCardListener;
            //image = (ImageView) itemView.findViewById();
            //imageSelector =  (ImageView) itemView.findViewById();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCardListener.onCardClick(getAdapterPosition());
        }
    }

    public void updateUser(String user){
        this.user = user;
    }

    public void addItem(MovieShort mSingle){
        mData.add(mSingle);
        //Toast.makeText(mContext, "aaa", Toast.LENGTH_LONG).show();
        this.notifyItemInserted(getItemCount()-1);
    }

    public void removeAllItem(){
        int size = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void changeStatus(String movie_id/*, Integer status*/){
        for(int i = 0; i < getItemCount(); i++){
            notifyItemChanged(i);

            /*
            if(status == 0){
                holder.setImageResource(R.drawable.custom_ic_shopping_cart_24dp);
                holder.statusIcon.setBackgroundColor(Color.parseColor("#00CACA"));
                holder.statusIcon.setVisibility(View.VISIBLE);
            }
            else if(status == 1){
                    Log.e("hello", "U UPDATE 1");
                    holder.statusIcon.setImageResource(R.drawable.custom_ic_shopping_cart_24dp);
                    holder.statusIcon.setBackgroundColor(Color.parseColor("#00CACA"));
                    holder.statusIcon.setVisibility(View.VISIBLE);
            }
            else if(status == 2){
                    Log.e("hello", "U UPDATE 2");
                    holder.statusIcon.setImageResource(R.drawable.custom_ic_tick_24dp);
                    holder.statusIcon.setBackgroundColor(Color.parseColor("#0DCA00"));
                    holder.statusIcon.setVisibility(View.VISIBLE);
            }
            }*/
        }

    }

    public void setFragmentCategory(String category){
        this.currentCategory = category;
    }

    public String getFragmentCategory(){
        return this.currentCategory;
    }

    public List<MovieShort> returnList(){return mData;}




    //credits: https://www.youtube.com/watch?v=69C1ljfDvl0
    public interface onCardListener{
        void onCardClick(int position);
    }

}
