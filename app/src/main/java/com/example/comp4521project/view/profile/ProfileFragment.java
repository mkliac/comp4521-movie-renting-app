package com.example.comp4521project.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.comp4521project.R;
import com.example.comp4521project.model.Users;
import com.example.comp4521project.movieUI.Library;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    Users user;
    boolean loadUser = false;
    String username = "";
    String nickname = "";
    String password = "";
    TextView usernameTV;
    TextView nicknameTV;
    TextView creditsTV;
    Float credits = 0F;
    View thisView;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference nicknameRef, creditRef;

    ProfileFragment profile;
    Library library;
    private ProfileViewModel profileViewModel;
    FragmentManager fm;

    public ProfileFragment(){
        loadUser = false;
    }

    public ProfileFragment(Users user){
        this.user = user;
        loadUser = true;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        thisView = root;

        if(loadUser){
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.password = user.getPassword();
            this.credits = user.getCredits();

            usernameTV = root.findViewById(R.id.usernameValue);
            usernameTV.setText(username);
            nicknameTV = root.findViewById(R.id.nicknameValue);
            nicknameTV.setText(nickname);
            creditsTV = root.findViewById(R.id.creditsValue);

            setNicknameRef(username);
            setCreditsRef(username);

            fm = getFragmentManager();

            library = (Library) fm.findFragmentByTag("library");
            profile = this;

            (root.findViewById(R.id.movieButton)).setOnClickListener(v -> {
                library.renderByByUser();
                library.setParent("profile");
                fm.beginTransaction().hide(profile).show(library).commit();
            });

            (root.findViewById(R.id.creditsButton)).setOnClickListener(v -> {
                Intent i = new Intent();
                i.setClass(getContext(), CreditsPurchase.class);
                i.putExtra("username", username);
                startActivity(i);
                ((Activity)getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            });
            (root.findViewById(R.id.settingButton)).setOnClickListener(v -> {
                Intent i = new Intent();
                i.setClass(getContext(), ProfileSetting.class);
                i.putExtra("username", username);
                startActivity(i);
                ((Activity)getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            });
        }

        return root;
    }

    public void setNicknameRef(String username){
        nicknameRef = rootRef.child("users").child(username).child("nickname");
        nicknameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nicknameTV.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    public void setCreditsRef(String username){
        creditRef = rootRef.child("users").child(username).child("credits");
        creditRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue(Float.class).toString()+" HKD";
                creditsTV.setText(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

}