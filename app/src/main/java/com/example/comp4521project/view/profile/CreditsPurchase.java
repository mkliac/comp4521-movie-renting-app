package com.example.comp4521project.view.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class CreditsPurchase extends AppCompatActivity {
    String username;
    TextView warningTV;
    EditText inputCreditsET;
    ExtendedFloatingActionButton submitButton;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference creditRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits_purchase);
        getSupportActionBar().hide();
        username = getIntent().getExtras().getString("username");
        creditRef = rootRef.child("users").child(username).child("credits");

        warningTV = findViewById(R.id.credit_purchase_warning_textView);
        inputCreditsET = findViewById(R.id.credit_purchase_credit_input);
        submitButton = findViewById(R.id.credit_purchase_submit);
        warningTV.setVisibility(View.INVISIBLE);

        inputCreditsET.setOnFocusChangeListener((view, b) -> {
            if(!b){
                if(inputCreditsET.getText().toString()==""){
                    warningTV.setText("Please enter the credits to add");
                    warningTV.setVisibility(View.VISIBLE);
                }
            }
            else warningTV.setVisibility(View.INVISIBLE);
        });

        findViewById(R.id.profile_setting_exit).setOnClickListener(view -> {
            finish();
            overridePendingTransition(0, android.R.anim.slide_out_right);
        });

        submitButton.setOnClickListener(view -> {
            String valString = inputCreditsET.getText().toString();
            if(valString.equals("")){
                warningTV.setText("Invalid operation, please input a positive value");
                warningTV.setVisibility(View.VISIBLE);
                return;
            }
            final Float addCredits = Float.parseFloat(valString);
            if(addCredits <= 0F){
                warningTV.setText("Invalid operation, please input a positive value");
                warningTV.setVisibility(View.VISIBLE);
            }else{
                creditRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Float oldCredits = dataSnapshot.getValue(Float.class);
                        final Float newCredits = oldCredits+addCredits;

                        Toast.makeText(getApplicationContext(), addCredits +" HKD added to your credits", Toast.LENGTH_LONG).show();
                        rootRef.child("users").child(username).child("credits").setValue(newCredits);

                        finish();
                        overridePendingTransition(0, android.R.anim.slide_out_right);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }

}
