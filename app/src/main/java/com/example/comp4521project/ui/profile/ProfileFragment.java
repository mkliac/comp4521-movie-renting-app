package com.example.comp4521project.ui.profile;

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
import com.example.comp4521project.UserData.Users;
import com.example.comp4521project.movieUI.AllLibrary;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    TextView usernameValue;
    TextView nicknameValue;
    TextView creditsValue;
    Users user;
    boolean loadUser = false;
    String username = "";
    String nickname = "";
    String password = "";
    Float credits = 0F;
    View thisView;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference nicknameMonitor, creditsMonitor;

    private ProfileViewModel profileViewModel;
    FragmentManager fm;
    ProfileFragment profilePage;
    AllLibrary allLibraryPage;

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
            usernameValue = (TextView) root.findViewById(R.id.usernameValue);
            nicknameValue = (TextView) root.findViewById(R.id.nicknameValue);
            creditsValue = (TextView) root.findViewById(R.id.creditsValue);
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.password = user.getPassword();
            this.credits = user.getCredits();
            usernameValue.setText(username);
            nicknameValue.setText(nickname);
            //creditsValue.setText(credits.toString()+" HKD");
            setNicknameMonitor(username);
            setCreditsMonitor(username);
            fm = getFragmentManager();
            //fmTran = fm.beginTransaction();
            profilePage = this;
            allLibraryPage = (AllLibrary) fm.findFragmentByTag("library");

            ((ExtendedFloatingActionButton)(root.findViewById(R.id.movieButton))).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    allLibraryPage.refreshByUserOrder(username);
                    allLibraryPage.setParent("profile");
                    fm.beginTransaction().hide(profilePage).show(allLibraryPage).commit();
                }
            });

            ((ExtendedFloatingActionButton)(root.findViewById(R.id.creditsButton))).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(getContext(), CashPurchase.class);
                    i.putExtra("username", username);
                    startActivity(i);
                    ((Activity)getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
                }
            });
            ((ExtendedFloatingActionButton)(root.findViewById(R.id.settingButton))).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(getContext(), ProfileSetting.class);
                    i.putExtra("username", username);
                    startActivity(i);
                    ((Activity)getContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
                }
            });
        }

        return root;
    }

    public void setListeners(){

    }

    public void setNicknameMonitor(String username){
        nicknameMonitor = rootRef.child("users").child(username).child("nickname");
        nicknameMonitor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nicknameValue.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    public void setCreditsMonitor(String username){
        creditsMonitor = rootRef.child("users").child(username).child("credits");
        creditsMonitor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue(Float.class).toString()+" HKD";
                creditsValue.setText(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

}