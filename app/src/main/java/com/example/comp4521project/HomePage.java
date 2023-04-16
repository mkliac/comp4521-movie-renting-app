package com.example.comp4521project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.comp4521project.UserData.Users;
import com.example.comp4521project.movieUI.AllLibrary;
import com.example.comp4521project.ui.cart.CartFragment;
import com.example.comp4521project.ui.home.HomeFragment;
import com.example.comp4521project.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomePage extends AppCompatActivity {

    FloatingActionButton setting;
    ImageView dimBox;
    ExtendedFloatingActionButton logout;
    Users user;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userMonitor;

    //fragment dealer:
    Button homePageButton;

    HomeFragment homePage = new HomeFragment();
    CartFragment cartPage = new CartFragment();
    ProfileFragment profilePage = new ProfileFragment();
    AllLibrary libraryPage = new AllLibrary();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //hid 2 other fragment


        setting = (FloatingActionButton) findViewById(R.id.floating_action_button);
        dimBox = (ImageView) findViewById(R.id.dimBox);
        logout = (ExtendedFloatingActionButton) findViewById(R.id.submit);

        dimBox.setVisibility(View.INVISIBLE);
        logout.setVisibility(View.INVISIBLE);
        logout.setAlpha(0.0F);



        //set users
        {
            Bundle bundle = getIntent().getExtras();
            user = bundle.getParcelable("user");
            libraryPage = new AllLibrary(user.getUsername());
            profilePage = new ProfileFragment(user);
            homePage = new HomeFragment(user);
            cartPage = new CartFragment(user.getUsername());
            active = homePage;
            libraryPage.setHome(homePage);
            libraryPage.setCart(cartPage);
            libraryPage.setProfile(profilePage);
        }
        getSupportActionBar().hide();

        Snackbar.make(this.findViewById(android.R.id.content),
                "Welcome! "+user.getNickname(),
                Snackbar.LENGTH_LONG)
                .show();
        //Toast toast = Toast.makeText(getApplicationContext(), "Welcome! "+user.getNickname(), Toast.LENGTH_LONG);
        //toast.show();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm.beginTransaction().add(R.id.main_container, libraryPage, "library").hide(libraryPage).commit();
        fm.beginTransaction().add(R.id.main_container, cartPage, "cart").hide(cartPage).commit();
        fm.beginTransaction().add(R.id.main_container, profilePage, "profile").hide(profilePage).commit();
        fm.beginTransaction().add(R.id.main_container,homePage, "home").commit();

        /*
        userMonitor = rootRef.child("users").child(user.getUsername());
        userMonitor.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                user.setNickname(dataSnapshot.child("nickname").getValue().toString());
                user.setPassword(dataSnapshot.child("password").getValue().toString());
                //user.setCredits(Float.parseFloat(dataSnapshot.child("credits").getValue().toString()));
                ProfileFragment pf =(ProfileFragment) fm.findFragmentByTag("profile");
                if (pf != null) {
                    pf.setNicknameValue(user.getNickname());
                    pf.setCreditsValue(user.getCredits());
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "null ptr occured", Toast.LENGTH_LONG);
                    toast.show();
                }
                //profilePage = new ProfileFragment(user);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        //setProfileActivity();
        rotateSettingBackward();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateSettingForward();
            }
        });

        dimBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateSettingBackward();
            }
        });

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("groupProjectLoginPref", MODE_PRIVATE);
                preferences.edit().clear().apply();
                startActivity(new Intent(HomePage.this, login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // close splash activity
                finish();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).hide(fm.findFragmentByTag("library")).show(homePage).commit();
                    active = homePage;
                    return true;

                case R.id.navigation_cart:
                    fm.beginTransaction().hide(active).hide(fm.findFragmentByTag("library")).show(cartPage).commit();
                    active = cartPage;
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).hide(fm.findFragmentByTag("library")).show(profilePage).commit();
                    active = profilePage;
                    return true;
            }
            return false;
        }
    };

    /*
    //set profile activity
    public void setProfileActivity(){
        profilePage.setUsernameValue(user.getUsername());
        profilePage.setNicknameValue(user.getNickname());
        profilePage.setCreditsValue(user.getCredits());
    }*/

    //animations
    public void rotateSettingForward() {
        ViewCompat.animate(setting)
                .rotation(-180.0F)
                .withLayer()
                .translationY(-20L)
                .setDuration(300L)
                //.setInterpolator(new OvershootInterpolator(10.0F))
                .start();
        dimBox.setVisibility(View.VISIBLE);
        ViewCompat.animate(dimBox)
                .alpha(0.65F)
                .setDuration(300L)
                .start();
        logout.setVisibility(View.VISIBLE);
        ViewCompat.animate(logout)
                .alpha(1F)
                .translationY(0L)
                .setDuration(300L)
                .start();
    }

    public void rotateSettingBackward() {
        ViewCompat.animate(dimBox)
                .alpha(0F)
                .setDuration(300L)
                .start();
        ViewCompat.animate(setting)
                .rotation(0.0F)
                .withLayer()
                .translationY(0L)
                .setDuration(300L)
                //.setInterpolator(new OvershootInterpolator(10.0F))
                .start();
        ViewCompat.animate(logout)
                .alpha(0F)
                .translationY(80L)
                .setDuration(300L)
                .start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 5s = 300ms
                dimBox.setVisibility(View.INVISIBLE);
                logout.setVisibility(View.INVISIBLE);
            }
        }, 300);

    }



}