package com.example.comp4521project.Tester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class MovieDetail extends AppCompatActivity {
    ImageButton playTrailer, movieDetailExitButton;
    //Movie movie;
    //FilePuller fp = new FilePuller();

    ImageView movieImage;
    Drawable image;
    String username;
    TextView movieName, movieNameShadow, movieYear, movieDescription, moviePopularity, priceValue;
    String id, name, year, description, popularity, category;
    Float price;
    String path, url;
    ExtendedFloatingActionButton addToCart;
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



        //movieImage.setImageDrawable(new BitmapDrawable(getResources(), (fp.pullImageFromDatabase(movie.getId(), getResources()))));
        pullImageFromDatabase(id);
        //url = movie.getTrailerURL();

        playTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlShort = url.substring(32, url.length()-1);
                //Toast.makeText(getApplicationContext(), urlShort+"\n"+Integer.toString(urlShort.length()), Toast.LENGTH_LONG).show();
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
        String path = "movies/"+id+"/"+id+".jpg";
        StorageReference imageRef = storageRef.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //Drawable i = new BitmapDrawable(getResources(), a);
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
                        addToCart.setEnabled(true);
                    }
                    else {
                        addToCart.setText("Purchased");
                        addToCart.setEnabled(false);
                    }
                }
                else {
                    addToCart.setText("Add to cart");
                    addToCart.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}
