package com.example.comp4521project.Tester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;

public class MovieDetail extends AppCompatActivity {
    ImageButton playTrailer, movieDetailExitButton;

    ImageView movieImage;
    Drawable image;
    String username;
    TextView movieName, movieNameShadow, movieYear, movieDescription, moviePopularity, priceValue;
    RatingBar ratingBar;
    String id, name, year, description, popularity, category;
    Float price;
    String path, url;
    ExtendedFloatingActionButton addToCart;
    final long MB = 1024 * 1024;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().hide();

        username = getIntent().getExtras().getString("username");
        movieImage = (ImageView) findViewById(R.id.movieImage);
        movieName = (TextView) findViewById(R.id.movieName);
        movieNameShadow = (TextView) findViewById(R.id.movieNameShadow);
        movieYear = (TextView) findViewById(R.id.movieYear);
        movieDescription = (TextView) findViewById(R.id.movieDescription);
        moviePopularity = (TextView) findViewById(R.id.moviePopularity);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        priceValue = (TextView) findViewById(R.id.priceValue);
        playTrailer = (ImageButton) findViewById(R.id.playTrailer);
        movieDetailExitButton = (ImageButton) findViewById(R.id.movieDetailExitButton);
        addToCart = (ExtendedFloatingActionButton) findViewById(R.id.checkout);

        id = getIntent().getStringExtra("movie_id");
        username = getIntent().getStringExtra("username");

        setListener();

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootRef.child("users").child(username).child("cart").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue().equals(true)) {
                                rootRef.child("users").child(username).child("cart").child(id).removeValue();
                        } else {
                            rootRef.child("users").child(username).child("cart").child(id).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference movieRef = rootRef.child("movies").child(id);
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.child("id").getValue().toString();
                    popularity = dataSnapshot.child("popularity").getValue().toString();
                    price = Float.parseFloat(dataSnapshot.child("price").getValue().toString());
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    path = "movies/"+id+"/content.txt";
                    StorageReference contentRef = storageRef.child(path);
                    contentRef.getBytes(MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            String content = new String(bytes);
                            String[] stringArray = content.split(System.getProperty("line.separator"));
                            name = stringArray[0];
                            year = stringArray[1];
                            category = stringArray[2];
                            url = stringArray[3];
                            description = stringArray[4];

                            movieName.setText(name);
                            movieNameShadow.setText(name);
                            movieYear.setText("( "+year+")");
                            movieDescription.setText(description);
                            moviePopularity.setText(popularity);
                            ratingBar.setRating(Float.parseFloat(popularity));
                            priceValue.setText(price.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pullImageFromDatabase(id);

        playTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlShort = url.substring(32, url.length()-1);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+urlShort));
                intent.putExtra("force_fullscreen",true);
                startActivity(intent);
            }
        });
        movieDetailExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    public void pullImageFromDatabase(final String id){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("movies/"+id+"/"+id+".jpg");
        final long MB = 1024 * 1024;
        imageRef.getBytes(MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                movieImage.setImageBitmap(a);
            }
        });

    }
    public void setListener(){
        DatabaseReference cartRef = rootRef.child("users").child(username).child("cart").child(id);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().equals(true)){
                        addToCart.setText("Cancel cart");
                        addToCart.setEnabled(true);
                    }
                }
                else {
                    DatabaseReference transactionRef = rootRef.child("users").child(username).child("transactions").orderByChild("movieID").equalTo(id).getRef();
                    transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot transactionDetail : dataSnapshot.getChildren())
                            {
                                Log.i("Wt","original "+id+" stuff "+transactionDetail.child("movieID").getValue().toString());
                                Log.i("Wt",transactionDetail.child("dueDate").getValue().toString());
                                if(!id.equals(transactionDetail.child("movieID").getValue().toString()))
                                    continue;
                                LocalDateTime dueDateTime=LocalDateTime.parse(transactionDetail.child("dueDate").getValue().toString());
                                LocalDateTime timeNow = LocalDateTime.now();
                                if(!timeNow.isAfter(dueDateTime))
                                {
                                    addToCart.setText("Already Rented");
                                    addToCart.setEnabled(false);
                                    return;
                                }
                            }
                            addToCart.setText("Add to cart");
                            addToCart.setEnabled(true);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}
