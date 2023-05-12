package com.example.comp4521project;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InitialPage extends AppCompatActivity {
    Users user;
    String username, nickname, password;
    Float credits;
    TextView progressBarText;
    ProgressBar progressBar;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    private void prepareHome(Intent i){
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    private void prepareLogin(){
        startActivity(new Intent(InitialPage.this, login.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);
        progressBarText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.loadingBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(() -> {
            progressBar.setProgress(10);

            SharedPreferences pref = getSharedPreferences("groupProjectLoginPref",MODE_PRIVATE);
            username = pref.getString("username", null);
            password = pref.getString("password", null);

            progressBar.setProgress(30);
            if (username == null || password == null) {
                progressBar.setProgress(100);
                prepareLogin();
            }
            else{
                progressBar.setProgress(40);
                DatabaseReference userNameRef = rootRef.child("users").child(username);
                userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setProgress(70);
                        if(!dataSnapshot.exists()){
                            progressBar.setProgress(100);
                            prepareLogin();
                        }
                        else {
                            progressBar.setProgress(80);
                            String storedPw = dataSnapshot.child("password").getValue().toString();
                            if (!storedPw.equals(password)) {
                                progressBar.setProgress(100);
                                prepareLogin();
                            }
                            else {
                                progressBar.setProgress(100);
                                nickname = dataSnapshot.child("nickname").getValue().toString();
                                credits = Float.parseFloat(dataSnapshot.child("credits").getValue().toString());
                                user = new Users(username, password, nickname, credits);

                                Intent i = new Intent();
                                i.setClass(getApplicationContext(), HomePage.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user", user);
                                i.putExtras(bundle);
                                prepareHome(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setProgress(100);
                        prepareLogin();
                    }
                });

            }

        }, 2500);
    }
}
