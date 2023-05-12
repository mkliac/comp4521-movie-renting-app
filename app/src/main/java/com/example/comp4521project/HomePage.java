package com.example.comp4521project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.comp4521project.model.Users;
import com.example.comp4521project.movieUI.Library;
import com.example.comp4521project.view.cart.CartFragment;
import com.example.comp4521project.view.home.HomeFragment;
import com.example.comp4521project.view.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class HomePage extends AppCompatActivity {
    HomeFragment home = new HomeFragment();
    CartFragment cart = new CartFragment();
    ProfileFragment profile = new ProfileFragment();
    Library library = new Library();
    Fragment active = home;
    FloatingActionButton setting;

    // screen, touch -> setting button rotate back & dimbox disappear
    ImageView dimBox;
    ExtendedFloatingActionButton logout;
    Users user;
    final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("user");
        profile = new ProfileFragment(user);
        home = new HomeFragment(user);
        cart = new CartFragment(user.getUsername());

        active = home;

        library = new Library(user.getUsername());
        library.setHome(home);
        library.setCart(cart);
        library.setProfile(profile);

        setting = findViewById(R.id.floating_action_button);
        dimBox = findViewById(R.id.dimBox);
        logout = findViewById(R.id.submit);

        dimBox.setVisibility(View.INVISIBLE);
        logout.setVisibility(View.INVISIBLE);
        logout.setAlpha(0.0F);

        getSupportActionBar().hide();

        Snackbar.make(this.findViewById(android.R.id.content),
                "Welcome! "+user.getNickname(),
                Snackbar.LENGTH_LONG)
                .show();

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm.beginTransaction().add(R.id.main_container, library, "library").hide(library).commit();
        fm.beginTransaction().add(R.id.main_container, cart, "cart").hide(cart).commit();
        fm.beginTransaction().add(R.id.main_container, profile, "profile").hide(profile).commit();
        fm.beginTransaction().add(R.id.main_container, home, "home").commit();

        rotateSettingBackward();

        setting.setOnClickListener(view -> rotateSettingForward());

        dimBox.setOnClickListener(view -> rotateSettingBackward());

        logout.setOnClickListener(view -> {
            SharedPreferences preferences = getSharedPreferences("groupProjectLoginPref", MODE_PRIVATE);
            preferences.edit().clear().apply();
            startActivity(new Intent(HomePage.this, login.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            finish();
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fm.beginTransaction().hide(active).hide(fm.findFragmentByTag("library")).show(home).commit();
                        active = home;
                        return true;

                    case R.id.navigation_cart:
                        fm.beginTransaction().hide(active).hide(fm.findFragmentByTag("library")).show(cart).commit();
                        active = cart;
                        return true;

                    case R.id.navigation_profile:
                        fm.beginTransaction().hide(active).hide(fm.findFragmentByTag("library")).show(profile).commit();
                        active = profile;
                        return true;
                }
                return false;
            };

    public void rotateSettingForward() {
        ViewCompat.animate(setting)
                .rotation(-180.0F)
                .withLayer()
                .translationY(-20L)
                .setDuration(300L)
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
                .start();
        ViewCompat.animate(logout)
                .alpha(0F)
                .translationY(80L)
                .setDuration(300L)
                .start();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            //logout button disappear after 0.3s
            dimBox.setVisibility(View.INVISIBLE);
            logout.setVisibility(View.INVISIBLE);
        }, 300);

    }



}
