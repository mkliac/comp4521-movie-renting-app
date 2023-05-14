package com.example.comp4521project;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    CheckBox checkBox;
    Button signUpButton;
    ProgressBar progressBar;
    EditText usernameET, pwET, rePwET;
    TextView tncMsg, tncErrMsg, usernameErrMsg, pwErrMsg, rePwErrMsg;
    private final int gray = Color.parseColor("#828282");
    private final int red = Color.parseColor("#F43325");
    private final int blue = Color.parseColor("#17b4fd");
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    protected boolean checkUsername(boolean hasFocus){
        if(hasFocus){
            usernameErrMsg.setVisibility(View.INVISIBLE);
            return false;
        }
        else{
            if(usernameET.getText().toString().equals("")){
                usernameErrMsg.setText("*Please enter username");
                usernameErrMsg.setTextColor(red);
                usernameErrMsg.setVisibility(View.VISIBLE);
                return false;
            }
            else return !usernameET.getText().toString().equals("");
        }
    }
    protected boolean checkPw(boolean hasFocus){
        if(hasFocus){
            pwErrMsg.setVisibility(View.INVISIBLE);
            rePwErrMsg.setVisibility(View.INVISIBLE);
        }
        else if(pwET.getText().toString().equals("")){
            pwErrMsg.setVisibility(View.VISIBLE);
            rePwErrMsg.setVisibility(View.INVISIBLE);
            return false;
        }
        else if (!pwET.getText().toString().equals(rePwET.getText().toString())) {
            pwErrMsg.setVisibility(View.INVISIBLE);
            rePwErrMsg.setVisibility(View.VISIBLE);
        }
        else if (pwET.getText().toString().equals(rePwET.getText().toString())) {
            pwErrMsg.setVisibility(View.INVISIBLE);
            rePwErrMsg.setVisibility(View.INVISIBLE);
        }
        return pwET.getText().toString().equals(rePwET.getText().toString());
    }
    protected boolean checkRePw(boolean hasFocus){
        if(hasFocus){
            rePwErrMsg.setVisibility(View.INVISIBLE);
            return false;
        }
        else if(!pwET.getText().toString().equals(rePwET.getText().toString())){
            rePwErrMsg.setVisibility(View.VISIBLE);
            return false;
        }
        else if(pwET.getText().toString().equals(rePwET.getText().toString())){
            rePwErrMsg.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkBox = findViewById(R.id.register_checkbox);
        signUpButton = findViewById(R.id.register_signUp);
        progressBar = findViewById(R.id.register_progressbar);

        usernameET = findViewById(R.id.register_username);
        pwET = findViewById(R.id.register_pw);
        rePwET = findViewById(R.id.register_re_pw);

        usernameErrMsg = findViewById(R.id.register_username_err);
        usernameErrMsg.setVisibility(View.INVISIBLE);
        pwErrMsg = findViewById(R.id.register_pw_err);
        pwErrMsg.setVisibility(View.INVISIBLE);
        rePwErrMsg = findViewById(R.id.register_re_pw_err);
        rePwErrMsg.setVisibility(View.INVISIBLE);
        tncMsg = findViewById(R.id.register_tnc);
        tncErrMsg = findViewById(R.id.register_tnc_err);
        tncErrMsg.setVisibility(View.INVISIBLE);

        getSupportActionBar().hide();


        progressBar.setVisibility(View.INVISIBLE);

        usernameET.setOnFocusChangeListener((view, hasFocus) -> {
            checkUsername(hasFocus);
            if(!hasFocus&& !usernameET.getText().toString().equals("")){
                usernameErrMsg.setText("checking validity");
                usernameErrMsg.setTextColor(gray);
                usernameErrMsg.setVisibility(View.VISIBLE);

                DatabaseReference userNameRef = rootRef.child("users").child(usernameET.getText().toString());
                userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            usernameErrMsg.setText("*This username has been taken");
                            usernameErrMsg.setTextColor(red);
                            usernameErrMsg.setVisibility(View.VISIBLE);
                        }
                        else{
                            usernameErrMsg.setText("This username is valid");
                            usernameErrMsg.setTextColor(blue);
                            usernameErrMsg.setVisibility(View.VISIBLE);
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

        pwET.setOnFocusChangeListener((view, hasFocus) -> checkPw(hasFocus));

        rePwET.setOnFocusChangeListener((view, hasFocus) -> {
            if(checkRePw(hasFocus)&&!hasFocus){
                rePwErrMsg.setVisibility(View.VISIBLE);
            }
        });

        checkBox.setOnCheckedChangeListener((compoundButton, hasChecked) -> {
            if(hasChecked){
                tncErrMsg.setVisibility(View.INVISIBLE);
            }
        });

        signUpButton.setOnClickListener(view -> {
            boolean hasProblems = false;
            if(!checkUsername(false)){hasProblems=true;}
            if(!checkPw(false)){hasProblems=true;}
            if(!checkRePw(false)){ hasProblems=true;}
            if(!checkBox.isChecked()){
                tncErrMsg.setVisibility(View.VISIBLE);
                hasProblems=true;}

            if(hasProblems)
                Toast.makeText(getApplicationContext(), "please match all condition", Toast.LENGTH_LONG).show();
            else{
                progressBar.setVisibility(View.VISIBLE);

                //rootRef is the whole database
                Log.d("debug", "enter db");
                final DatabaseReference userPathRef = rootRef.child("users");
                Log.d("debug", "end db");
                userPathRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("debug", "enter on data change");
                        if(!dataSnapshot.child(usernameET.getText().toString()).exists()){
                            Log.d("debug", "enter if statement");
                            DatabaseReference thisUser = userPathRef.child(usernameET.getText().toString());
                            thisUser.child("password").setValue(pwET.getText().toString());
                            thisUser.child("credits").setValue(0);
                            thisUser.child("nickname").setValue(usernameET.getText().toString());
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "account created", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Log.d("debug", "enter else statement");
                            usernameErrMsg.setText("*This username has been taken");
                            usernameErrMsg.setTextColor(red);
                            usernameErrMsg.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("debug", databaseError.getMessage());
                        Toast.makeText(getApplicationContext(), "firebase error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
