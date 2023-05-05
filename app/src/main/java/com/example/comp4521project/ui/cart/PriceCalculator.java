package com.example.comp4521project.ui.cart;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PriceCalculator {
    boolean initializing = false;
    boolean checkout = false;
    TextView textView;
    Float price;
    String username;
    int totalNodes = 0;
    int currentNodes = 0;

    public PriceCalculator(TextView t){
        textView = t;
    }

    public void setInitializing(boolean initializing) {
        this.initializing = initializing;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
    }

    public void getTotal(String username) {
        if(initializing){}
        else if(checkout){}
        else {
            price = 0F;
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("cart");
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.getValue(Boolean.class)==true){

                            DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("movies").child(ds.getKey());
                            temp.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    price += dataSnapshot.child("price").getValue(Float.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    textView.setText(price.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setCurrentNodes(int currentNodes) {
        this.currentNodes = currentNodes;
    }

    public int getCurrentNodes() {
        return currentNodes;
    }
}
