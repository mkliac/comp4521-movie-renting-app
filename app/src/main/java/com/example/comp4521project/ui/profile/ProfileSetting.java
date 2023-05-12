package com.example.comp4521project.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp4521project.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileSetting extends AppCompatActivity {
    ExtendedFloatingActionButton submit;
    Switch nameSwitch, passwordSwitch;
    TextView nameError, oldPwError, newPwError, newPw2Error;
    EditText name, oldPw, newPw, newPw2;
    String username, nickname, password;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference nicknameRef;
    private DatabaseReference passwordRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        getSupportActionBar().hide();
        username = getIntent().getExtras().getString("username");
        nicknameRef = rootRef.child("users").child(username).child("nickname");
        passwordRef = rootRef.child("users").child(username).child("password");

        //bound views
        {
            nameError = findViewById(R.id.nameErrorTag);
            oldPwError = findViewById(R.id.oldPwErrorTag);
            newPwError = findViewById(R.id.newPwErrorTag);
            newPw2Error = findViewById(R.id.newPw2ErrorTag);
            name = findViewById(R.id.name);
            oldPw = findViewById(R.id.oldPw);
            newPw = findViewById(R.id.newPw);
            newPw2 = findViewById(R.id.newPw2);
            nameSwitch = findViewById(R.id.nameSwitch);
            passwordSwitch = findViewById(R.id.passwordSwitch);
            submit = findViewById(R.id.submit);
        }

        nicknameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nickname = dataSnapshot.getValue().toString();
                name.setText(nickname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { password = dataSnapshot.getValue().toString(); }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        {
            name.setEnabled(nameSwitch.isChecked());
            oldPw.setEnabled(nameSwitch.isChecked());
            newPw.setEnabled(nameSwitch.isChecked());
            newPw2.setEnabled(nameSwitch.isChecked());
            nameError.setVisibility(View.INVISIBLE);
            oldPwError.setVisibility(View.INVISIBLE);
            newPwError.setVisibility(View.INVISIBLE);
            newPw2Error.setVisibility(View.INVISIBLE);
            name.setHint(nickname);
            submit.setEnabled(false);
        }

        name.setOnFocusChangeListener((view, b) -> nameError.setVisibility(View.INVISIBLE));
        oldPw.setOnFocusChangeListener((view, b) -> oldPwError.setVisibility(View.INVISIBLE));
        newPw.setOnFocusChangeListener((view, b) -> newPwError.setVisibility(View.INVISIBLE));
        newPw2.setOnFocusChangeListener((view, b) -> newPw2Error.setVisibility(View.INVISIBLE));

        ((ImageButton)findViewById(R.id.exitButton)).setOnClickListener(view -> {
            finish();
            overridePendingTransition(0, android.R.anim.slide_out_right);
        });
        ((Switch) findViewById(R.id.nameSwitch)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
                name.setEnabled(isChecked);
                if(!isChecked){
                    nameError.setVisibility(View.INVISIBLE);
                }
                if(isChecked){
                    submit.setEnabled(true);
                }
                else if(!passwordSwitch.isChecked()){
                    submit.setEnabled(false);
                }
        });

        ((Switch) findViewById(R.id.passwordSwitch)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            oldPw.setEnabled(isChecked);
            newPw.setEnabled(isChecked);
            newPw2.setEnabled(isChecked);
            if(!isChecked){
                oldPwError.setVisibility(View.INVISIBLE);
                newPwError.setVisibility(View.INVISIBLE);
                newPw2Error.setVisibility(View.INVISIBLE);
            }
            if(isChecked){
                submit.setEnabled(true);
            }
            else if(!nameSwitch.isChecked()){
                submit.setEnabled(false);
            }
        });

        ((ExtendedFloatingActionButton)findViewById(R.id.submit)).setOnClickListener(view -> {
            if(varify()){
                if(nameSwitch.isChecked()){
                    nicknameRef.setValue(name.getText().toString());
                }
                if(passwordSwitch.isChecked()){
                    passwordRef.setValue(newPw.getText().toString());
                }
                Toast toast = Toast.makeText(getApplicationContext(), "change applied", Toast.LENGTH_LONG);
                toast.show();
                finish();
                overridePendingTransition(0, android.R.anim.slide_out_right);
            }
        });

    }
    private boolean varify(){
        boolean hasError = false;
        if(nameSwitch.isChecked()){
            if(name.getText().toString().equals("")){
                nameError.setText("*Please Type in");
                nameError.setVisibility(View.VISIBLE);
                hasError = true;
            }
            if(nickname.equals(name.getText().toString())){
                nameError.setText("*Same nickname");
                nameError.setVisibility(View.VISIBLE);
                hasError = true;
            }
        }

        if(passwordSwitch.isChecked()){
            if(oldPw.getText().toString().equals("")){
                oldPwError.setText("*Please type in");
                oldPwError.setVisibility(View.VISIBLE);
                hasError = true;
            }
            else if(!password.equals(oldPw.getText().toString())){
                oldPwError.setText("*Incorrect password");
                oldPwError.setVisibility(View.VISIBLE);
                hasError = true;
            }
            else{
                if(newPw.getText().toString().equals("")){
                    newPwError.setText("*Please type in");
                    hasError = true;
                }
                if(!newPw.getText().toString().equals(newPw2.getText().toString())){
                    newPw2Error.setText("*password not match");
                    newPw2Error.setVisibility(View.VISIBLE);
                    hasError = true;
                }
                if(password.equals(newPw.getText().toString())){
                    newPwError.setText("*Same as old password");
                    newPwError.setVisibility(View.VISIBLE);
                    hasError = true;
                }
            }
        }

        if(hasError){
            return false;
        }
        else return true;
    }
}
