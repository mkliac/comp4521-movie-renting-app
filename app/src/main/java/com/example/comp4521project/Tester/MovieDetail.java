package com.example.comp4521project.Tester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.CommentActivity;
import com.example.comp4521project.R;
import com.example.comp4521project.VideoActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class MovieDetail extends AppCompatActivity {
    ImageButton playTrailer, movieDetailExitButton;

    ImageView movieImage;
    Drawable image;
    String username , id, name, year, description, category,popularity;
    TextView movieName, movieNameShadow, movieYear, movieDescription, moviePopularity, priceValue;
    Float price;
    String path, url;
    ExtendedFloatingActionButton addToCart, playMovie, comment;
    RatingBar movieRating;
    String movieMp4Url = "";
    final long ONE_MEGABYTE = 1024 * 1024;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference statusRef;

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
        priceValue = (TextView) findViewById(R.id.priceValue);
        playTrailer = (ImageButton) findViewById(R.id.playTrailer);
        movieDetailExitButton = (ImageButton) findViewById(R.id.movieDetailExitButton);
        addToCart = (ExtendedFloatingActionButton) findViewById(R.id.checkout);
        comment = (ExtendedFloatingActionButton) findViewById(R.id.comment);
        movieRating = (RatingBar) findViewById(R.id.ratingBar);
        id = getIntent().getStringExtra("movie_id");
        username = getIntent().getStringExtra("username");

        setListener();

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootRef.child("purchaseStatus").child(username).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.getValue().toString().equals("1")){
                                rootRef.child("purchaseStatus").child(username).child(id).removeValue();
                            }
                            else if(dataSnapshot.getValue().toString().equals("2"))
                            {
                                if(movieMp4Url != "") {
                                    Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                                    intent.putExtra("videoUri", movieMp4Url);
                                    startActivity(intent);
                                }
                            }
                                else rootRef.child("purchaseStatus").child(username).child(id).setValue(1);
                        }
                        else rootRef.child("purchaseStatus").child(username).child(id).setValue(1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference movieRef = rootRef.child("movies").child(id);
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) { //if no this path = no this user (since the path I designed is: users/(username)/password.value
                    id = dataSnapshot.child("id").getValue().toString();
                    popularity = dataSnapshot.child("popularity").getValue().toString();
                    price = Float.parseFloat(dataSnapshot.child("price").getValue().toString());
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    path = "movies/"+id+"/content.txt";
                    StorageReference contentRef = storageRef.child(path);
                    path = "movies/"+id+"/"+id+".mp4";
                    StorageReference movieUrlRef = storageRef.child(path);
                    movieUrlRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            movieMp4Url = uri.toString();
                        }
                    });
                    contentRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                            movieRating.setRating(Float.parseFloat(popularity));
                            moviePopularity.setText(popularity);
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

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("movieId", id);
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
        String path = "movies/"+id+"/"+id+".jpg";
        StorageReference imageRef = storageRef.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                movieImage.setImageBitmap(a);
            }
        });

    }
    public void setListener(){
        statusRef = rootRef.child("purchaseStatus").child(username).child(id);
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().toString().equals("1")){
                        addToCart.setText("Cancel cart");
                        addToCart.setIcon(getResources().getDrawable(R.drawable.custom_ic_shopping_cart_24dp,null));
                        addToCart.setEnabled(true);
                    }
                    else {
                        addToCart.setText("Play Movie");
                        addToCart.setIcon(getResources().getDrawable(R.drawable.custom_ic_movie_play_24dp,null));
                    }
                }
                else {
                    addToCart.setText("Add to cart");
                    addToCart.setEnabled(true);
                    addToCart.setIcon(getResources().getDrawable(R.drawable.custom_ic_shopping_cart_24dp,null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}
