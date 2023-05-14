package com.example.comp4521project.view.cart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.Adapter.CartAdapter;
import com.example.comp4521project.model.MovieBrief;
import com.example.comp4521project.R;
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
    TextView priceTV;
    TextView creditsTV;
    TextView emptyNotification;

    private boolean start = false;
    private String user;
    private Float price = 0F;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cartRef;
    DatabaseReference userRef;

    ExtendedFloatingActionButton checkout;

    List<MovieBrief> cartList;
    CartAdapter myAdapter;

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
            {
                priceTV = root.findViewById(R.id.cartf_movie_price);
                creditsTV = root.findViewById(R.id.cartf_credit_remain);
                emptyNotification = root.findViewById(R.id.cartf_empty_notification);
                checkout = root.findViewById(R.id.cartf_checkout);
            }

            cartRef = rootRef.child("purchaseStatus").child(user);
            cartList = new ArrayList<>();
            RecyclerView cartRV = root.findViewById(R.id.cartf_cartView);
            myAdapter = new CartAdapter(this, cartList, user);
            cartRV.setAdapter(myAdapter);

            detectCartDBChange();
            boundUserCredits();

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
            cartRV.setLayoutManager(layoutManager);

            NotificationChannel channel = new NotificationChannel("check out", "check out", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager NotifManager = getActivity().getSystemService(NotificationManager.class);
            NotifManager.createNotificationChannel(channel);

            checkout.setOnClickListener(view -> {
                final Float movieTotal = Float.parseFloat(priceTV.getText().toString());
                DatabaseReference tempDB = rootRef.child("users").child(user).child("credits");
                tempDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot userCredits) {
                        if(userCredits.getValue(Float.class)<Float.parseFloat(priceTV.getText().toString())){
                            Toast.makeText(root.getContext(), "Not enough credits", Toast.LENGTH_LONG).show();
                        }
                        else{
                            final DatabaseReference movieValue = rootRef.child("purchaseStatus").child(user);
                            Float remainCredits = userCredits.getValue(Float.class) - movieTotal;
                            rootRef.child("users").child(user).child("credits").setValue(remainCredits);
                            Toast.makeText(root.getContext(), "Purchased", Toast.LENGTH_LONG);
                            //sent notification
                            notification(movieTotal);
                            movieValue.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                                        if(ds.getValue(Integer.class) != 1) continue;

                                        rootRef.child("purchaseStatus").child(user).child(ds.getKey()).setValue(2);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            });
        }


        return root;
    }

    public void detectCartDBChange(){
        price = 0F;
        cartRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                final String movieId = dataSnapshot.getKey();
                rootRef.child("movies").child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        if(ds.exists()){
                            if(dataSnapshot.getValue().toString().equals("1")){
                                String id = dataSnapshot.getKey();
                                myAdapter.addItem(id);
                                calculateAndRenderPrice(movieId, "ADD");
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
                String id = dataSnapshot.getKey();
                String val = dataSnapshot.getValue().toString();
                if(val.equals("1")){
                    myAdapter.addItem(id);
                    calculateAndRenderPrice(id, "ADD");
                }
                else if(val.equals("2")){
                    myAdapter.removeItem(id);
                    calculateAndRenderPrice(id, "MINUS");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                myAdapter.removeItem(id);
                calculateAndRenderPrice(id, "MINUS");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
    public void notification(Float amount){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), "check out");
        builder.setContentTitle("Transaction Accepted");
        builder.setContentText("Purchased successfully! $" + amount + " was deducted from your wallet.");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.getContext());
        managerCompat.notify(1, builder.build());
    }

    public void emptyHandling(){
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
        userRef = rootRef.child("users").child(user).child("credits");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                creditsTV.setText(dataSnapshot.getValue(Float.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void calculateAndRenderPrice(String movie_id, final String identity){
        DatabaseReference priceRef = rootRef.child("movies").child(movie_id).child("price");
        priceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(identity.equals("ADD"))
                        price += dataSnapshot.getValue(Float.class);
                    else if(identity.equals("MINUS"))
                        price -= dataSnapshot.getValue(Float.class);
                    else price = 0F;
                }
                priceTV.setText(price.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}