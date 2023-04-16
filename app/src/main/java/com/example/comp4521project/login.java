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
    //establish connection to the database
    //private FirebaseDatabase users = FirebaseDatabase.getInstance();    //represents the database itself
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();  //has the path of the database

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

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signup = (TextView) findViewById(R.id.signup);
        loginButton = (Button) findViewById(R.id.loginButton);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);
        getSupportActionBar().hide();

        loginButton.setOnClickListener(new View.OnClickListener()      {
            @Override
            public void onClick(View v) {
                Toast toast;
                if(username.getText().toString().equals("")){   //if the user don't input name
                    toast = Toast.makeText(getApplicationContext(), "please input username", Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(password.getText().toString().equals("")){  //if the user don't input password (but has username input)
                    toast = Toast.makeText(getApplicationContext(), "please input password", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{   //both username and password has input, do below now
                    final String _username = username.getText().toString();
                    final String _password = password.getText().toString();
                    Log.d("debug", "name:" + _username + " password:" + _password);

                    //rootRef is the whole database
                    DatabaseReference userNameRef = rootRef.child("users").child(_username);    //establish single path of the database
                    Log.d("debug", String.valueOf(userNameRef.child("password")));

                    if(userNameRef == null) Log.d("debug", "null usernameref");
                    userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {   //snapshot = copy the whole structure of the target table
                            if(!dataSnapshot.exists()){ //if no this path = no this user (since the path I designed is: users/(username)/password.value
                                Toast toast = Toast.makeText(getApplicationContext(), "invalid username", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {  //else means this user exists
                                String serverPassword = dataSnapshot.child("password").getValue().toString();  //get password value from database
                                if (!serverPassword.equals(_password)) {    //if password not match
                                    Toast toast = Toast.makeText(getApplicationContext(), "invalid password", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                else {  //all authorization success
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
                            Log.d("DatabaseError", databaseError.getMessage()); //Don't ignore errors!
                            Toast toast = Toast.makeText(getApplicationContext(), "serverside error", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, register.class));
            }
        });

    }
}

