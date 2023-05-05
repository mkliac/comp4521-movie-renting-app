package com.example.comp4521project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.MovieData.MovieShort;
import com.example.comp4521project.R;
import com.example.comp4521project.ui.cart.CartFragment;
import com.example.comp4521project.ui.profile.MyMovieFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MovieViewAdapter extends RecyclerView.Adapter<MovieViewAdapter.MyViewHolder> {

    private Context mContext;
    private MyMovieFragment cartFragment;
    private String user;
    private List<MovieShort> mData;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    public MovieViewAdapter(MyMovieFragment cartFragment, List<MovieShort> mData, String user) {
        this.cartFragment = cartFragment;
        this.mContext = cartFragment.getContext();
        this.mData = mData;
        this.user = user;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.my_movies_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final String id = mData.get(position).getId();
        String path = "movies/" + id + "/" + id + ".jpg";
        final StorageReference imageRef = storageRef.child(path);
        final long MB = 1024 * 1024;
        imageRef.getBytes(MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.movieImage.setImageResource(android.R.color.transparent);
                holder.movieImage.setBackground(new BitmapDrawable(mContext.getResources(), a));
                holder.movieImage.setImageBitmap(a);

            }
        });

        path = "movies/" + id + "/content.txt";
        StorageReference contentRef = FirebaseStorage.getInstance().getReference().child(path);

        if(getItemCount()!=0){
            contentRef.getBytes(MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    String content = new String(bytes);
                    String[] stringArray = content.split(System.getProperty("line.separator"));
                    holder.title.setText(stringArray[0]);
                    holder.year.setText(stringArray[1]);
                    if(getItemCount()!=0){
                        Float tempPrice = mData.get(position).getPrice();
                        holder.price.setText(tempPrice.toString());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImage;
        TextView year, title, price;
        FloatingActionButton playMovie;

        public MyViewHolder(View itemView) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.movieImage);
            title = itemView.findViewById(R.id.Title);
            year = itemView.findViewById(R.id.yearValue);
            price = itemView.findViewById(R.id.dueDateValue);
            playMovie = itemView.findViewById(R.id.playMovie);
        }
    }

    public List<MovieShort> returnList(){return mData;}
}
