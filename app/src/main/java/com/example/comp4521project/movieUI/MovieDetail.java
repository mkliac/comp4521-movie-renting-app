package com.example.comp4521project.movieUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.CommentActivity;
import com.example.comp4521project.R;
import com.example.comp4521project.VideoActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MovieDetail extends AppCompatActivity {
    String id;
    String name;
    String username;
    String year;
    String category;
    String description;
    ImageButton movieDetailExitButton;
    ImageButton playTrailer;
    ImageView movieImage;
    TextView popularityTV;
    TextView priceTV;
    TextView nameTV;
    TextView nameShadowTV;
    TextView yearTV;
    TextView descriptionTV;
    Float price;
    String popularity;
    String path;
    String url;
    ExtendedFloatingActionButton addToCart;
    ExtendedFloatingActionButton comment;
    RatingBar movieRating;
    String movieMp4Url = "";
    final long IN_MB = 1024 * 1024;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().hide();

        username = getIntent().getExtras().getString("username");
        movieImage = findViewById(R.id.movie_detail_movie_image);
        nameTV = findViewById(R.id.movie_detail_movie_name);
        nameShadowTV = findViewById(R.id.movie_detail_movie_name_shadow);
        yearTV = findViewById(R.id.movie_detail_movie_year);
        descriptionTV = findViewById(R.id.movie_detail_movie_desc);
        popularityTV = findViewById(R.id.movie_detail_movie_popularity);
        priceTV = findViewById(R.id.movie_detail_movie_price);
        playTrailer = findViewById(R.id.movie_detail_play_trailer);
        movieDetailExitButton = findViewById(R.id.movie_detail_exit);
        addToCart = findViewById(R.id.movie_detail_checkout);
        comment = findViewById(R.id.movie_detail_comment);
        movieRating = findViewById(R.id.ratingBar);
        id = getIntent().getStringExtra("movie_id");
        username = getIntent().getStringExtra("username");

        setListener();

        addToCart.setOnClickListener(view -> rootRef.child("purchaseStatus").child(username).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    rootRef.child("purchaseStatus").child(username).child(id).setValue(1);
                else{
                    String val = dataSnapshot.getValue().toString();
                    if(val.equals("1")){
                        rootRef.child("purchaseStatus").child(username).child(id).removeValue();
                    }
                    else if(val.equals("2"))
                    {
                        if(movieMp4Url != "") {
                            Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                            intent.putExtra("videoUri", movieMp4Url);
                            startActivity(intent);
                        }
                    }
                    else rootRef.child("purchaseStatus").child(username).child(id).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        }));

        //get movies
        DatabaseReference movieRef = rootRef.child("movies").child(id);
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.child("id").getValue().toString();
                    price = Float.parseFloat(dataSnapshot.child("price").getValue().toString());
                    popularity = dataSnapshot.child("popularity").getValue().toString();

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    path = "movies/"+id+"/content.txt";
                    StorageReference contentRef = storageRef.child(path);
                    path = "movies/"+id+"/"+id+".mp4";
                    StorageReference movieUrlRef = storageRef.child(path);

                    movieUrlRef.getDownloadUrl().addOnSuccessListener(uri -> movieMp4Url = uri.toString());
                    contentRef.getBytes(IN_MB).addOnSuccessListener(bytes -> {
                        String content = new String(bytes);
                        String[] contents = content.split(System.getProperty("line.separator"));
                        name = contents[0];
                        year = contents[1];
                        category = contents[2];
                        url = contents[3];
                        description = contents[4];

                        popularityTV.setText(popularity);
                        priceTV.setText(price.toString());
                        nameTV.setText(name);
                        nameShadowTV.setText(name);
                        yearTV.setText("( "+year+")");
                        descriptionTV.setText(description);
                        movieRating.setRating(Float.parseFloat(popularity));
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getThumbnailFromFirebase(id);

        playTrailer.setOnClickListener(view -> {
            String urlShort = url.substring(32, url.length()-1);
            String uriString = "vnd.youtube:"+urlShort;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            intent.putExtra("force_fullscreen",true);
            startActivity(intent);
        });

        comment.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("movieId", id);
            startActivity(intent);
        });
        movieDetailExitButton.setOnClickListener(view -> finish());

    }

    public void setListener(){
        //get movie purchaseStatus
        DatabaseReference statusRef = rootRef.child("purchaseStatus").child(username).child(id);
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    addToCart.setText("Add to cart");
                    addToCart.setEnabled(true);
                    addToCart.setIcon(getResources().getDrawable(R.drawable.cart,null));
                }
                else{
                    String val = dataSnapshot.getValue().toString();
                    if(val.equals("1")){
                        addToCart.setText("Cancel cart");
                        addToCart.setIcon(getResources().getDrawable(R.drawable.cart,null));
                        addToCart.setEnabled(true);
                    }
                    else if(val.equals("2")){
                        addToCart.setText("Play Movie");
                        addToCart.setIcon(getResources().getDrawable(R.drawable.play_movie,null));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void getThumbnailFromFirebase(final String id){
        String path = "movies/"+id+"/"+id+".jpg";
        StorageReference imageRef = storageRef.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            movieImage.setImageBitmap(a);
        });

    }
}
