package com.example.comp4521project;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.Adapter.CommentAdapter;
import com.example.comp4521project.MovieData.Comment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CommentActivity extends AppCompatActivity {

    ExtendedFloatingActionButton addComment;
    EditText commentET;
    RadioGroup ratingGroup;

    String username, movieId;
    CommentAdapter myAdapter;
    DatabaseReference commentRef;
    RadioButton ratingGood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        addComment = findViewById(R.id.addComment);
        ratingGroup = findViewById(R.id.ratingRadioGroup);
        commentET = findViewById(R.id.commentText);
        ratingGood= findViewById(R.id.ratingGood);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        movieId = extras.getString("movieId");

        commentRef = FirebaseDatabase.getInstance().getReference().child("comment").child(movieId);

        RecyclerView myrv = findViewById(R.id.commentView);
        myAdapter = new CommentAdapter(this);
        myrv.setAdapter(myAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        myrv.setLayoutManager(layoutManager);

        listenToCommentDatabase();

        addComment.setOnClickListener(v -> addComment());

        getSupportActionBar().setTitle("Comments");
    }

    private void addComment() {
        if(commentET.getText().toString().equals("")){
            Toast.makeText(this,"Please leave your comment", Toast.LENGTH_LONG).show();
            return;
        }
        String commentText = commentET.getText().toString();
        commentET.setText("");
        Boolean isGood = ratingGood.isChecked();
        String uuid = UUID.randomUUID().toString();
        commentRef.child(uuid).child("comment").setValue(commentText);
        commentRef.child(uuid).child("good").setValue(isGood ? 1 : 0);
        commentRef.child(uuid).child("username").setValue(username);
    }

    public void listenToCommentDatabase(){
        commentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                DataSnapshot nameRef = dataSnapshot.child("username");
                DataSnapshot commentRef = dataSnapshot.child("comment");
                DataSnapshot isGoodRef = dataSnapshot.child("good");
                if(nameRef.exists() && commentRef.exists() && isGoodRef.exists()){
                    String name = nameRef.getValue().toString();
                    String commentText = commentRef.getValue().toString();
                    Boolean isGood = isGoodRef.getValue(Integer.class).equals(1);
                    Comment comment = new Comment(name, isGood, commentText);
                    myAdapter.add(comment);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataSnapshot nameRef = dataSnapshot.child("username");
                DataSnapshot commentRef = dataSnapshot.child("comment");
                DataSnapshot isGoodRef = dataSnapshot.child("good");
                if(nameRef.exists() && commentRef.exists() && isGoodRef.exists()){
                    String name = nameRef.getValue().toString();
                    String commentText = commentRef.getValue().toString();
                    Boolean isGood = isGoodRef.getValue(Integer.class).equals(1);
                    Comment comment = new Comment(name, isGood, commentText);
                    myAdapter.add(comment);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
}