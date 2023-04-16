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

import com.example.comp4521project.MovieData.MovieShort;
import com.example.comp4521project.R;
import com.example.comp4521project.ui.cart.CartFragment;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context mContext;
    private CartFragment cartFragment;
    private String user;
    private Float price;
    private List<MovieShort> mData;
    private String currentCategory = "none";
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cartRef;

    //private MovieAdapter.onCardListener onCardListener;

    public CartAdapter(CartFragment cartFragment, List<MovieShort> mData, String user /* , onCardListener onCardListener*/) {
        this.cartFragment = cartFragment;
        this.mContext = cartFragment.getContext();
        this.mData = mData;
        this.user = user;
        //this.onCardListener = onCardListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //String id;
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.cart_view, parent, false);

        return new MyViewHolder(view  /*, onCardListener*/);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final String id = mData.get(position).getId();
        String path = "movies/" + id + "/" + id + ".jpg";
        final StorageReference imageRef = storageRef.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.movieImage.setImageResource(android.R.color.transparent);
                holder.movieImage.setBackground(new BitmapDrawable(mContext.getResources(), a));
                holder.movieImage.setImageBitmap(a);
                cartFragment.checkEmpty();
            }
        });

        path = "movies/" + id + "/content.txt";
        StorageReference contentRef = FirebaseStorage.getInstance().getReference().child(path);
        if(getItemCount()!=0){
            contentRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCartItem(mData.get(position).getId());
                //  removie item from the recycler view
                //mData.remove(position);
                //parentRecyclerView.removeViewAt(position);
                // notifyItemRemoved(position);
                //notifyItemRemoved(position);
                //this line below gives you the animation and also updates the
                //list items after the deleted item
                //notifyItemRangeChanged(position, getItemCount());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImage;
        TextView year, title, price;
        //ImageView statusIcon;
        FloatingActionButton remove;
        //onCardListener onCardListener;

        public MyViewHolder(View itemView   /*, onCardListener onCardListener*/) {
            super(itemView);

            movieImage = (ImageView) itemView.findViewById(R.id.movieImage);
            title = (TextView) itemView.findViewById(R.id.Title);
            year = (TextView) itemView.findViewById(R.id.yearValue);
            price = (TextView) itemView.findViewById(R.id.priceValue);
            remove = (FloatingActionButton) itemView.findViewById(R.id.remove);

            //this.onCardListener = onCardListener;
            //image = (ImageView) itemView.findViewById();
            //imageSelector =  (ImageView) itemView.findViewById();
            //itemView.setOnClickListener(this);
        }
    }

    public void addItem(MovieShort mSingle) {

        mData.add(mSingle);
        //Toast.makeText(mContext, "aaa", Toast.LENGTH_LONG).show();
        this.notifyItemInserted(getItemCount() - 1);
    }

    public void removeCartItem(String movie_id)
    {
        Log.e("hello", "removeCartItem: "+movie_id);
        FirebaseDatabase.getInstance().getReference().child("purchaseStatus").child(user).child(movie_id).removeValue();
        //cartFragment.recalculateTotal();
    }

    public void removeAll(){
            int size = mData.size();
            mData.clear();
            notifyItemRangeRemoved(0, size);
    }

    public void addItem(String movie_id){
        boolean saveToAdd = true;
        for(int i = 0; i < getItemCount(); i++){
            if(mData.get(i).getId().equals(movie_id)){
                saveToAdd = false;
                break;
            }
        }
        if(saveToAdd){
            DatabaseReference movieRef = rootRef.child("movies").child(movie_id);
            movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.child("id").getValue().toString();
                    Float price = dataSnapshot.child("price").getValue(Float.class);
                    mData.add(new MovieShort(id, price));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void removeItem(String movie_id){
        //Log.e("hello", Integer.toString(getItemCount()));
        for(int i = 0; i < getItemCount(); i++){
            //Log.e("hello", mData.get(i).getId());
            if(mData.get(i).getId().equals(movie_id)){
                mData.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, getItemCount());
                break;
            }
        }
        cartFragment.checkEmpty();
    }

    public List<MovieShort> returnList(){return mData;}
}