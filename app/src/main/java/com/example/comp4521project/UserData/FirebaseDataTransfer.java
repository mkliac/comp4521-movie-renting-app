package com.example.comp4521project.UserData;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataTransfer {
    private Users user;
    String password;
    String nickname;
    Float credits;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    public FirebaseDataTransfer(){ }

    public Users getUserData(final String username){
        user = new Users();
        final DatabaseReference userNameRef = rootRef.child("users").child(username);    //establish single path of the database
        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    password = (dataSnapshot.child("password").getValue().toString());
                    credits = (Float.parseFloat(dataSnapshot.child("credits").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        final DatabaseReference userNicknameRef = rootRef.child("userNickname").child(username);    //establish single path of the database
        userNicknameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nickname = (dataSnapshot.child("nickname").getValue().toString());
                } else nickname = username;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                nickname = username;
            }
        });

        user = new Users(username, password, nickname, credits);
        return user;
    }
}
