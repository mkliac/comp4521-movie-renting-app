package com.example.comp4521project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity{
    EditText usernameET, passwordET;
    TextView signUpTV;
    Button loginButton;
    CheckBox rememberMe;
    Users user;
    String nickname;
    Float credits;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.login_username);
        passwordET = findViewById(R.id.login_pw);
        signUpTV = findViewById(R.id.login_signUp);
        loginButton = findViewById(R.id.login_login_button);
        rememberMe = findViewById(R.id.login_remember);
        getSupportActionBar().hide();

        loginButton.setOnClickListener(v -> {
            if(usernameET.getText().toString().equals(""))
                Toast.makeText(getApplicationContext(), "please input username", Toast.LENGTH_LONG).show();
            else if(passwordET.getText().toString().equals(""))
                Toast.makeText(getApplicationContext(), "please input password", Toast.LENGTH_LONG).show();
            else{
                final String _username = usernameET.getText().toString();
                final String _password = passwordET.getText().toString();

                DatabaseReference userNameRef = rootRef.child("users").child(_username);

                if(userNameRef == null) Log.d("debug", "null usernameref");
                userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                            Toast.makeText(getApplicationContext(), "invalid username", Toast.LENGTH_LONG).show();
                        else {
                            String serverPassword = dataSnapshot.child("password").getValue().toString();
                            if (!serverPassword.equals(_password))
                                Toast.makeText(getApplicationContext(), "invalid password", Toast.LENGTH_LONG).show();
                            else {
                                if(rememberMe.isChecked()){
                                    getSharedPreferences("login info",MODE_PRIVATE)
                                            .edit()
                                            .putString("username", _username)
                                            .putString("password", _password)
                                            .apply();
                            }
                                Intent intent = new Intent(login.this, HomePage.class);
                                Bundle bundle = new Bundle();
                                nickname = dataSnapshot.child("nickname").getValue().toString();
                                credits = Float.parseFloat(dataSnapshot.child("credits").getValue().toString());
                                user = new Users(_username, _password, nickname, credits);
                                bundle.putParcelable("user", user);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                login.this.finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("debug", databaseError.getMessage());
                        Toast.makeText(getApplicationContext(), "firebase error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        signUpTV.setOnClickListener(view -> startActivity(new Intent(login.this, register.class)));

    }
}

