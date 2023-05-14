package com.example.comp4521project.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.model.MovieBrief;
import com.example.comp4521project.R;
import com.example.comp4521project.view.cart.CartFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context mContext;
    private CartFragment cartFragment;
    private String user;
    private Float price;
    private List<MovieBrief> mData;
    final long IN_MB = 1024 * 1024;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public CartAdapter(CartFragment cartFragment, List<MovieBrief> mData, String user) {
        this.cartFragment = cartFragment;
        this.mContext = cartFragment.getContext();
        this.mData = mData;
        this.user = user;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.cart_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        // get movie thumbnail
        final String id = mData.get(position).getId();
        String path = "movies/" + id + "/" + id + ".jpg";
        final StorageReference jpgRef = storageRef.child(path);
        jpgRef.getBytes(IN_MB).addOnSuccessListener(bytes -> {
            Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.movieImage.setImageResource(android.R.color.transparent);
            holder.movieImage.setImageBitmap(a);
            holder.movieImage.setBackground(new BitmapDrawable(mContext.getResources(), a));
            cartFragment.emptyHandling();
        });

        // get movie detail description
        path = "movies/" + id + "/content.txt";
        StorageReference contentRef = storageRef.child(path);
        if(getItemCount()!=0){
            contentRef.getBytes(IN_MB).addOnSuccessListener(bytes -> {
                String content = new String(bytes);
                String[] contents = content.split(System.getProperty("line.separator"));
                Log.d("debug", String.valueOf(contents));
                holder.title.setText(contents[0]);
                holder.year.setText(contents[1]);
                if(getItemCount()!=0){
                    Float price = mData.get(position).getPrice();
                    holder.price.setText(price.toString());
                }
            });
        }

        holder.remove.setOnClickListener(v -> removeCartItem(mData.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView price;
        TextView year;
        TextView title;
        ImageView movieImage;
        FloatingActionButton remove;

        public MyViewHolder(View itemView) {
            super(itemView);

            movieImage = itemView.findViewById(R.id.cart_view_movie_image);
            title = itemView.findViewById(R.id.cart_view_movie_name);
            year = itemView.findViewById(R.id.cart_view_year);
            price = itemView.findViewById(R.id.cart_view_movie_price);
            remove = itemView.findViewById(R.id.cart_view_remove);

        }
    }

    public void addItem(String movie_id){
        boolean idExist = false;
        for(int i = 0; i < getItemCount(); i++){
            if(!mData.get(i).getId().equals(movie_id)) continue;

            idExist = true;
            break;
        }
        // if no duplicate id found, save to add
        Log.e("debug", "addItem: "+movie_id);
        if(!idExist){
            DatabaseReference movieRef = rootRef.child("movies").child(movie_id);
            movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Float price = dataSnapshot.child("price").getValue(Float.class);
                    String id = dataSnapshot.child("id").getValue().toString();
                    mData.add(new MovieBrief(id, price));
                    notifyItemRangeChanged(0, getItemCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void removeItem(String movie_id){
        for(int i = 0; i < getItemCount(); i++){
            if(!mData.get(i).getId().equals(movie_id)) continue;

            mData.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, getItemCount());
            break;
        }
        cartFragment.emptyHandling();
    }

    public void removeCartItem(String movie_id)
    {
        Log.e("debug", "removeCartItem: "+movie_id);
        rootRef.child("purchaseStatus").child(user).child(movie_id).removeValue();
    }

}
