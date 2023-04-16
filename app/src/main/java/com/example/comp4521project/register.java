package com.example.comp4521project;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class register extends AppCompatActivity {
    //establish connection to the database
    private boolean noServerError = true;
    private final int red = Color.parseColor("#F44336");
    private final int blue = Color.parseColor("#03A9F4");
    private final int gray = Color.parseColor("#888888");
    private FirebaseDatabase users = FirebaseDatabase.getInstance();    //represents the database itself
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();  //has the path of the database
    EditText username, password, password2;
    TextView usernameError, passwordError, password2Error, tnc, tncError;
    CheckBox checkBox;
    Button signUp;
    ProgressBar progressBar;

    protected boolean usernameChecker(EditText username, TextView usernameError, boolean hasFocus){
        username = this.username;
        usernameError = this.usernameError;
        if(hasFocus){
            usernameError.setVisibility(View.INVISIBLE);
            return false;
        }
        else{
            if(username.getText().toString().equals("")){
                usernameError.setText("*Please enter username");
                usernameError.setTextColor(red);
                usernameError.setVisibility(View.VISIBLE);
                return false;
            }
            else return !username.getText().toString().equals("");
        }
    }
    protected boolean passwordChecker(EditText password, EditText password2, TextView passwordError, TextView password2Error, boolean hasFocus){
        password = this.password;
        password2 = this.password2;
        passwordError = this.passwordError;
        password2Error = this.password2Error;
        if(hasFocus){
            passwordError.setVisibility(View.INVISIBLE);
            password2Error.setVisibility(View.INVISIBLE);
            //return password.getText().toString().equals(password2.getText().toString());
        }
        else if(password.getText().toString().equals("")){
            passwordError.setVisibility(View.VISIBLE);
            password2Error.setVisibility(View.INVISIBLE);
            return false;
        }
        else if (!password.getText().toString().equals(password2.getText().toString())) {
            passwordError.setVisibility(View.INVISIBLE);
            password2Error.setVisibility(View.VISIBLE);
            //return password.getText().toString().equals(password2.getText().toString());
        }
        else if (password.getText().toString().equals(password2.getText().toString())) {
            passwordError.setVisibility(View.INVISIBLE);
            password2Error.setVisibility(View.INVISIBLE);
            //return password.getText().toString().equals(password2.getText().toString());
        }
        return password.getText().toString().equals(password2.getText().toString());
    }
    protected boolean password2Checker(EditText password, EditText password2, TextView password2Error, boolean hasFocus){
        password = this.password;
        password2 = this.password2;
        password2Error = this.password2Error;
        if(hasFocus){
            password2Error.setVisibility(View.INVISIBLE);
            return false;
        }
        else if(!password.getText().toString().equals(password2.getText().toString())){
            password2Error.setVisibility(View.VISIBLE);
            return false;
        }
        else if(password.getText().toString().equals(password2.getText().toString())){
            password2Error.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();
        {
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);
            password2 = (EditText) findViewById(R.id.password2);
            usernameError = findViewById(R.id.usernameError);
            usernameError.setVisibility(View.INVISIBLE);
            passwordError = (TextView) findViewById(R.id.passwordError);
            passwordError.setVisibility(View.INVISIBLE);
            password2Error = (TextView) findViewById(R.id.password2Error);
            password2Error.setVisibility(View.INVISIBLE);
            tnc = (TextView) findViewById(R.id.tnc);
            tncError = (TextView) findViewById(R.id.tncError);
            tncError.setVisibility(View.INVISIBLE);
            checkBox = (CheckBox) findViewById(R.id.checkBox);
            signUp = (Button) findViewById(R.id.signUp);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
        } //initialize view objects
        progressBar.setVisibility(View.INVISIBLE);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                usernameChecker(username, usernameError, hasFocus);
                if(!hasFocus&& !username.getText().toString().equals("")){
                    usernameError.setText("checking validity");
                    usernameError.setTextColor(gray);
                    usernameError.setVisibility(View.VISIBLE);
                    //rootRef is the whole database
                    DatabaseReference userNameRef = rootRef.child("users").child(username.getText().toString());    //establish single path of the database
                    userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {   //snapshot = copy the whole structure of the target table
                            //if this path exists = has this user = username conflict
                            if(dataSnapshot.exists()) {
                                noServerError = false;
                                usernameError.setText("*This username has been taken");
                                usernameError.setTextColor(red);
                                usernameError.setVisibility(View.VISIBLE);
                            }
                            else{
                                noServerError = true;
                                usernameError.setText("This username is valid");
                                usernameError.setTextColor(blue);
                                usernameError.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("DatabaseError", databaseError.getMessage()); //Don't ignore errors!
                            Toast toast = Toast.makeText(getApplicationContext(), "serverside error", Toast.LENGTH_LONG);
                            toast.show();
                            noServerError = false;
                        }
                    });
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                passwordChecker(password, password2, passwordError, password2Error, hasFocus);
            }
        });

        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(password2Checker(password, password2, password2Error, hasFocus)&&!hasFocus){
                    password2Error.setVisibility(View.VISIBLE);
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean hasChecked) {
                if(hasChecked){
                    tncError.setVisibility(View.INVISIBLE);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasProblems = false;
                if(!usernameChecker(username, usernameError, false)){hasProblems=true;}
                if(!passwordChecker(password, password2, passwordError, password2Error, false)){hasProblems=true;}
                if(!password2Checker(password, password2, password2Error, false)){ hasProblems=true;}
                if(!checkBox.isChecked()){
                    tncError.setVisibility(View.VISIBLE);
                    hasProblems=true;}

                if(hasProblems){
                    Toast toast = Toast.makeText(getApplicationContext(), "please match all condition", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    //rootRef is the whole database
                    Log.d("debug", "enter db");
                    final DatabaseReference userPathRef = rootRef.child("users");    //establish single path of the database
                    Log.d("debug", "end db");
                    userPathRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {   //snapshot = copy the whole structure of the target table
                            Log.d("debug", "enter on data change");
                            if(!dataSnapshot.child(username.getText().toString()).exists()){ //if this path exists = has this user = username conflict
                                Log.d("debug", "enter if statement");
                                DatabaseReference thisUser = userPathRef.child(username.getText().toString());
                                thisUser.child("password").setValue(password.getText().toString());
                                thisUser.child("credits").setValue(0);
                                thisUser.child("nickname").setValue(username.getText().toString());
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(getApplicationContext(), "account created", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            }
                            else {
                                Log.d("debug", "enter else statement");
                                usernameError.setText("*This username has been taken");
                                usernameError.setTextColor(red);
                                usernameError.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("DatabaseError", databaseError.getMessage()); //Don't ignore errors!
                            Toast toast = Toast.makeText(getApplicationContext(), "serverside error", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
            }
        });
    }
}
