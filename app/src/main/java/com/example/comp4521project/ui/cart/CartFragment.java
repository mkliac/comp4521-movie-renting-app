package com.example.comp4521project.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.Adapter.CartAdapter;
import com.example.comp4521project.MovieData.MovieShort;
import com.example.comp4521project.R;
import com.example.comp4521project.ui.profile.ProfileFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private boolean start = false;
    private String user;
    private Float price = 0F;
    //PriceCalculator priceCal;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userRef;
    DatabaseReference cartRef;

    TextView priceValue, creditsValue, emptyNotification;
    ExtendedFloatingActionButton checkout;
    Button remove, cancel;
    List<MovieShort> cartList;
    CartAdapter myAdapter;
    ProfileFragment profilePage;

    public CartFragment(){}
    public CartFragment(String user){
        this.user = user;
        start = true;
    }


    private CartViewModel cartViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_cart, container, false);
        if(start){
            //bound views
            {
                priceValue = (TextView) root.findViewById(R.id.priceValue);
                creditsValue = (TextView) root.findViewById(R.id.creditsValue);
                emptyNotification = (TextView) root.findViewById(R.id.emptyNotification);
                checkout = (ExtendedFloatingActionButton) root.findViewById(R.id.checkout);
            }

            cartRef = rootRef.child("purchaseStatus").child(user);
            cartList = new ArrayList<>();
            RecyclerView myrv = (RecyclerView) root.findViewById(R.id.cartView);
            myAdapter = new CartAdapter(this, cartList, user);
            myrv.setAdapter(myAdapter);

            //priceCal = new PriceCalculator(priceValue);
            //priceCal.setInitializing(true);

            listenToCartDatabase();
            boundUserCredits();

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
            myrv.setLayoutManager(layoutManager);

            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Float movieTotal = Float.parseFloat(priceValue.getText().toString());
                    DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("credits");
                    temp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot userCredits) {
                            if(userCredits.getValue(Float.class)<Float.parseFloat(priceValue.getText().toString())){
                                Toast.makeText(root.getContext(), "Not enough credits", Toast.LENGTH_LONG).show();
                            }
                            else{
                                final DatabaseReference movieValue = FirebaseDatabase.getInstance().getReference().child("purchaseStatus").child(user);
                                FirebaseDatabase.getInstance().getReference().child("users").child(user).child("credits").setValue(userCredits.getValue(Float.class)-movieTotal);
                                //Log.e("hello", "onDataChange: "+(userCredits.getValue(Float.class)-userCredits.getValue(Float.class)));
                                //Log.e("hello", user);
                                Toast.makeText(root.getContext(), "Purchased", Toast.LENGTH_LONG);
                                movieValue.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            if(ds.getValue(Integer.class)==1){
                                                FirebaseDatabase.getInstance().getReference().child("purchaseStatus").child(user).child(ds.getKey()).setValue(2);
                                            }
                                        }
                                        /*
                                        FirebaseDatabase.getInstance().getReference().child("users").child(user).child("credits").setValue(
                                                userCredits.getValue(Float.class)-Float.parseFloat(priceValue.getText().toString())
                                        );
                                        myAdapter.removeAll();*/
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                //recalculateTotal();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            });
        }


        return root;
    }
    public void notifyUserDataChanged(){
    }
    public void notifyMovieAdded(){
    }
    //public void notifyMovieRemoved(){}
    public void checkEmpty(){
        if(myAdapter.getItemCount()==0){
            emptyNotification.setVisibility(View.VISIBLE);
            checkout.setEnabled(false);
        }
        else {
            emptyNotification.setVisibility(View.INVISIBLE);
            checkout.setEnabled(true);
        }

    }
    private void boundUserCredits(){
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("credits");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                creditsValue.setText(dataSnapshot.getValue(Float.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void recalculateTotal(String movie_id, final String identity){
        //PriceCalculator setPrice = new PriceCalculator();
        //priceValue.setText(setPrice.getTotal());
        //priceCal.getTotal(user);
        Log.e("hello", "movies/"+movie_id+"/price");
        DatabaseReference priceRef = rootRef.child("movies").child(movie_id).child("price");
        priceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(identity.equals("ADD")){
                        price+=dataSnapshot.getValue(Float.class);
                    }
                    else if(identity.equals("MINUS")){
                        price-=dataSnapshot.getValue(Float.class);
                    }
                    else price=0F;
                }
                priceValue.setText(price.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void listenToCartDatabase(){
        //recalculateTotal();
        price = 0F;
        cartRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                final String movie_id = dataSnapshot.getKey();
                rootRef.child("movies").child(movie_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        if(ds.exists()){
                            if(dataSnapshot.getValue().toString().equals("1")){
                                //priceCal.setCurrentNodes(priceCal.getCurrentNodes()+1);
                                String status = dataSnapshot.getValue().toString();
                                String id = dataSnapshot.getKey();
                                myAdapter.addItem(id);
                                recalculateTotal(movie_id, "ADD");
                            }
                        }
                        else rootRef.child("purchaseStatus").child(user).child(dataSnapshot.getKey()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String status = dataSnapshot.getValue().toString();
                String id = dataSnapshot.getKey();
                Log.e("hello", "Child change detected: "+id+" "+dataSnapshot.getValue().toString());
                if(dataSnapshot.getValue().toString().equals("1")){
                    Log.e("hello", "method 1 entered");
                    //priceCal.setCurrentNodes(priceCal.getCurrentNodes()+1);
                    myAdapter.addItem(id);
                    recalculateTotal(id, "ADD");
                }
                else if(dataSnapshot.getValue().toString().equals("2")){
                    Log.e("hello", "method 2 entered");
                    myAdapter.removeItem(id);
                    recalculateTotal(id, "MINUS");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue().toString();
                String id = dataSnapshot.getKey();
                myAdapter.removeItem(id);
                recalculateTotal(id, "MINUS");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
}