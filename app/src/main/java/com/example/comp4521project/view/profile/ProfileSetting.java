package com.example.comp4521project.view.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
    EditText name, oldPw, newPw, newRePw;
    TextView nameErrMsg, oldPwErrMsg, newPwErrMsg, newRePwErrMsg;
    String username, nickname, password;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference nicknameRef;
    private DatabaseReference passwordRef;
    ExtendedFloatingActionButton submit;
    Switch nicknameSwitch, passwordSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        getSupportActionBar().hide();
        username = getIntent().getExtras().getString("username");
        nicknameRef = rootRef.child("users").child(username).child("nickname");
        passwordRef = rootRef.child("users").child(username).child("password");

        {
            nameErrMsg = findViewById(R.id.profile_setting_nickname_err);
            oldPwErrMsg = findViewById(R.id.profile_setting_old_pw_err);
            newPwErrMsg = findViewById(R.id.profile_setting_new_pw_err);
            newRePwErrMsg = findViewById(R.id.profile_setting_re_pw_err);

            name = findViewById(R.id.profile_setting_nickname);
            oldPw = findViewById(R.id.profile_setting_old_pw);
            newPw = findViewById(R.id.profile_setting_new_pw);
            newRePw = findViewById(R.id.profile_setting_re_pw);

            submit = findViewById(R.id.profile_setting_apply);
            nicknameSwitch = findViewById(R.id.profile_setting_nickname_switch);
            passwordSwitch = findViewById(R.id.profile_setting_pw_switch);
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
            nameErrMsg.setVisibility(View.INVISIBLE);
            oldPwErrMsg.setVisibility(View.INVISIBLE);
            newPwErrMsg.setVisibility(View.INVISIBLE);
            newRePwErrMsg.setVisibility(View.INVISIBLE);

            name.setEnabled(nicknameSwitch.isChecked());
            oldPw.setEnabled(nicknameSwitch.isChecked());
            newPw.setEnabled(nicknameSwitch.isChecked());
            newRePw.setEnabled(nicknameSwitch.isChecked());

            name.setHint(nickname);
            submit.setEnabled(false);
        }

        name.setOnFocusChangeListener((view, b) -> nameErrMsg.setVisibility(View.INVISIBLE));
        oldPw.setOnFocusChangeListener((view, b) -> oldPwErrMsg.setVisibility(View.INVISIBLE));
        newPw.setOnFocusChangeListener((view, b) -> newPwErrMsg.setVisibility(View.INVISIBLE));
        newRePw.setOnFocusChangeListener((view, b) -> newRePwErrMsg.setVisibility(View.INVISIBLE));

        findViewById(R.id.profile_setting_exit).setOnClickListener(view -> {
            finish();
            overridePendingTransition(0, android.R.anim.slide_out_right);
        });
        ((Switch)findViewById(R.id.profile_setting_nickname_switch)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
                name.setEnabled(isChecked);

                if(!isChecked) nameErrMsg.setVisibility(View.INVISIBLE);

                if(!isChecked) submit.setEnabled(true);
                else if(!passwordSwitch.isChecked()) submit.setEnabled(false);
        });

        ((Switch)findViewById(R.id.profile_setting_pw_switch)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            oldPw.setEnabled(isChecked);
            newPw.setEnabled(isChecked);
            newRePw.setEnabled(isChecked);

            if(!isChecked){
                oldPwErrMsg.setVisibility(View.INVISIBLE);
                newPwErrMsg.setVisibility(View.INVISIBLE);
                newRePwErrMsg.setVisibility(View.INVISIBLE);
            }

            if(isChecked) submit.setEnabled(true);
            else if(!nicknameSwitch.isChecked()) submit.setEnabled(false);
        });

        findViewById(R.id.profile_setting_apply).setOnClickListener(view -> {
            if(check()){
                if(passwordSwitch.isChecked())
                    passwordRef.setValue(newPw.getText().toString());

                if(nicknameSwitch.isChecked())
                    nicknameRef.setValue(name.getText().toString());

                Toast.makeText(getApplicationContext(), "change applied", Toast.LENGTH_LONG).show();
                finish();
                overridePendingTransition(0, android.R.anim.slide_out_right);
            }
        });

    }
    private boolean check(){
        boolean containErr = false;
        if(nicknameSwitch.isChecked()){
            if(name.getText().toString().equals("")){
                nameErrMsg.setText("*Please Type in");
                nameErrMsg.setVisibility(View.VISIBLE);
                containErr = true;
            }
            if(nickname.equals(name.getText().toString())){
                nameErrMsg.setText("*Same nickname");
                nameErrMsg.setVisibility(View.VISIBLE);
                containErr = true;
            }
        }

        if(passwordSwitch.isChecked()){
            if(oldPw.getText().toString().equals("")){
                oldPwErrMsg.setText("*Please type in");
                oldPwErrMsg.setVisibility(View.VISIBLE);
                containErr = true;
            }
            else if(!password.equals(oldPw.getText().toString())){
                oldPwErrMsg.setText("*Incorrect password");
                oldPwErrMsg.setVisibility(View.VISIBLE);
                containErr = true;
            }
            else{
                if(newPw.getText().toString().equals("")){
                    newPwErrMsg.setText("*Please type in");
                    containErr = true;
                }
                if(!newPw.getText().toString().equals(newRePw.getText().toString())){
                    newRePwErrMsg.setText("*password not match");
                    newRePwErrMsg.setVisibility(View.VISIBLE);
                    containErr = true;
                }
                if(password.equals(newPw.getText().toString())){
                    newPwErrMsg.setText("*Same as old password");
                    newPwErrMsg.setVisibility(View.VISIBLE);
                    containErr = true;
                }
            }
        }

        if(containErr) return false;
        else return true;
    }
}
