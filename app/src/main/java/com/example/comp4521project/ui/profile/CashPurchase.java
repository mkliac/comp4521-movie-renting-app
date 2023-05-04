package com.example.comp4521project.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class CashPurchase extends AppCompatActivity {
    String username;
    TextView mocking;
    EditText cash;
    ExtendedFloatingActionButton submit;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference creditsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_purchase);
        getSupportActionBar().hide();
        username = getIntent().getExtras().getString("username");
        creditsRef = rootRef.child("users").child(username).child("credits");

        mocking = (TextView) findViewById(R.id.mocking);
        cash = (EditText) findViewById(R.id.cash);
        submit = (ExtendedFloatingActionButton) findViewById(R.id.submit);
        mocking.setVisibility(View.INVISIBLE);



        cash.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(cash.getText().toString()==""){
                        mocking.setText("*Please enter value to the line");
                        mocking.setVisibility(View.VISIBLE);
                    }
                }
                else mocking.setVisibility(View.INVISIBLE);
            }
        });

        ((ImageButton)findViewById(R.id.exitButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, android.R.anim.slide_out_right);
            }
        });

        ((ExtendedFloatingActionButton)findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Float enteredCash = Float.parseFloat(cash.getText().toString());
                if(enteredCash==0F){
                    mocking.setText("*you are wasting your time by trying to add 0 dollars");
                    mocking.setVisibility(View.VISIBLE);
                }
                else if((cash.getText().toString())=="") { }
                else{
                    creditsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Float userCash = dataSnapshot.getValue(Float.class);

                            if((enteredCash < 0F) && (userCash + enteredCash < 0F)){
                                mocking.setText("*what you want to do will cause depts\nyou will own "+ (userCash+enteredCash) +" HKD in total");
                                mocking.setVisibility(View.VISIBLE);
                            }
                            else {
                                if(enteredCash < 0F && (enteredCash + userCash >= 0F)){
                                    Toast.makeText(getApplicationContext(), "You just lose "+ Float.toString((0F-enteredCash)) +"HKD at final", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), enteredCash.toString()+" HKD added to your credits", Toast.LENGTH_LONG).show();
                                }
                            }
                            final Float finalCash = userCash+enteredCash;
                            rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.child("users").child(username).child("credits").setValue(finalCash);
                            finish();
                            overridePendingTransition(0, android.R.anim.slide_out_right);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }

            }
        });
    }

}
