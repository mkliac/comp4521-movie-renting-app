package com.example.comp4521project;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.UserData.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InitialPage extends AppCompatActivity {
    TextView loadingText;
    ProgressBar loadingBar;
    String username, nickname, password;
    Float credits;
    Users user;
    DatabaseReference rootRef;

    private void fakeLoading(){
        loadingText.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
    }
    private void loadHomePage(Intent i){
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    private void loadLoginPage(){
        startActivity(new Intent(InitialPage.this, login.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // close splash activity
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);
        loadingText = (TextView) findViewById(R.id.loadingText);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setMax(100);
        loadingBar.setProgress(0);

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                loadingBar.setProgress(14);
                SharedPreferences pref = getSharedPreferences("groupProjectLoginPref",MODE_PRIVATE);
                username = pref.getString("username", null);
                password = pref.getString("password", null);
                loadingBar.setProgress(33);
                if (username == null || password == null) {
                    loadingBar.setProgress(100);
                    loadLoginPage();
                }
                else{
                    loadingBar.setProgress(38);
                    rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userNameRef = rootRef.child("users").child(username);    //establish single path of the database
                    userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {   //snapshot = copy the whole structure of the target table
                            loadingBar.setProgress(71);
                            if(!dataSnapshot.exists()){ //if no this path = no this user (since the path I designed is: users/(username)/password.value
                                loadingBar.setProgress(100);
                                loadLoginPage();
                            }
                            else {  //else means this user exists
                                loadingBar.setProgress(78);
                                String serverPassword = dataSnapshot.child("password").getValue().toString();  //get password value from database
                                if (!serverPassword.equals(password)) {    //if password not match
                                    loadingBar.setProgress(100);
                                    loadLoginPage();
                                }
                                else {  //all authorization success
                                    loadingBar.setProgress(100);
                                    nickname = dataSnapshot.child("nickname").getValue().toString();
                                    credits = Float.parseFloat(dataSnapshot.child("credits").getValue().toString());
                                    user = new Users(username, password, nickname, credits);

                                    Intent i = new Intent();
                                    i.setClass(getApplicationContext(), HomePage.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("user", user);
                                    i.putExtras(bundle);
                                    loadHomePage(i);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            loadingBar.setProgress(100);
                            loadLoginPage();
                        }
                    });

                }

            }
        }, 3000);
    }
}
