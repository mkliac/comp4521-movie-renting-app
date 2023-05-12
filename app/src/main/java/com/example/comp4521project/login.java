package com.example.comp4521project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.UserData.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity{

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    EditText username, password;
    TextView signup;
    Button loginButton;
    CheckBox rememberMe;
    Users user;
    String _nickname;
    Float _credits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        loginButton = findViewById(R.id.loginButton);
        rememberMe = findViewById(R.id.rememberMe);
        getSupportActionBar().hide();

        loginButton.setOnClickListener(v -> {
            Toast toast;
            if(username.getText().toString().equals("")){
                toast = Toast.makeText(getApplicationContext(), "please input username", Toast.LENGTH_LONG);
                toast.show();
            }
            else if(password.getText().toString().equals("")){
                toast = Toast.makeText(getApplicationContext(), "please input password", Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                final String _username = username.getText().toString();
                final String _password = password.getText().toString();

                DatabaseReference userNameRef = rootRef.child("users").child(_username);

                if(userNameRef == null) Log.d("debug", "null usernameref");
                userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Toast toast = Toast.makeText(getApplicationContext(), "invalid username", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            String serverPassword = dataSnapshot.child("password").getValue().toString();
                            if (!serverPassword.equals(_password)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "invalid password", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {
                                if(rememberMe.isChecked()){
                                    getSharedPreferences("groupProjectLoginPref",MODE_PRIVATE)
                                            .edit()
                                            .putString("username", _username)
                                            .putString("password", _password)
                                            .apply();
                            }
                                Intent i = new Intent();
                                i.setClass(login.this, HomePage.class);
                                Bundle bundle = new Bundle();
                                _nickname = dataSnapshot.child("nickname").getValue().toString();
                                _credits = Float.parseFloat(dataSnapshot.child("credits").getValue().toString());
                                user = new Users(_username, _password, _nickname,  _credits);
                                bundle.putParcelable("user", user);
                                i.putExtras(bundle);
                                startActivity(i);
                                login.this.finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("DatabaseError", databaseError.getMessage());
                        Toast toast = Toast.makeText(getApplicationContext(), "serverside error", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        });

        signup.setOnClickListener(view -> startActivity(new Intent(login.this, register.class)));

    }
}

