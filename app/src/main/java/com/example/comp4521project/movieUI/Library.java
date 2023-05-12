package com.example.comp4521project.movieUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.Adapter.MovieAdapter;
import com.example.comp4521project.model.MovieBrief;
import com.example.comp4521project.R;
import com.example.comp4521project.view.cart.CartFragment;
import com.example.comp4521project.view.home.HomeFragment;
import com.example.comp4521project.view.profile.ProfileFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Library extends Fragment implements MovieAdapter.onCardListener{
    boolean start = false;
    boolean bySearch = false;
    boolean byCategory = false;
    boolean byUser = false;

    boolean isHomeParent = false;
    boolean isCartParent = false;
    boolean isProfileParent = false;

    List<MovieBrief> movieBriefList = new ArrayList<>();
    MovieAdapter myAdapter;

    String search;
    String user;
    View v;
    String categoryBCode = "********";
    HomeFragment home;
    CartFragment cart;
    ProfileFragment profile;
    final long IN_MB = 1024 * 1024;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference movieRef;

    public Library(){ }
    public Library(String user){
        this.user = user;
        start = true;
    }

    public void setHome(HomeFragment home) {
        this.home = home;
    }

    public void setCart(CartFragment cart) {
        this.cart = cart;
    }

    public void setProfile(ProfileFragment profile) {
        this.profile = profile;
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_library_fragment, container, false);
        v = rootView;
        final FragmentManager fm = getFragmentManager();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isHomeParent) {
                    fm.beginTransaction().hide(Library.this).show(home).commit();
                }
                else if(isCartParent){
                    fm.beginTransaction().hide(Library.this).show(cart).commit();
                }
                else if(isProfileParent){
                    fm.beginTransaction().hide(Library.this).show(profile).commit();
                }
                fm.beginTransaction().hide(Library.this).commit();
            }
        };
        if(start){
            rootView.findViewById(R.id.exitButton2).setOnClickListener(view -> fm.beginTransaction().hide(Library.this).commit());
            RecyclerView libraryRV = rootView.findViewById(R.id.all_library_view);
            myAdapter = new MovieAdapter(rootView.getContext(), movieBriefList, user, this);
            libraryRV.setAdapter(myAdapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
            libraryRV.setLayoutManager(layoutManager);

            addChildListener();

            // get movies
            movieRef = rootRef.child("movies");
            DatabaseReference userRef = rootRef.child("purchaseStatus").child(user);
            userRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    myAdapter.changeStatus(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    myAdapter.changeStatus(dataSnapshot.getKey());
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    myAdapter.changeStatus(dataSnapshot.getKey());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return rootView;

    }

    @Override
    public void onCardClick(int position) {
        List<MovieBrief> mData = myAdapter.returnList();
        Intent intent = new Intent(this.getContext(), MovieDetail.class);
        intent.putExtra("movie_id", mData.get(position).getId());
        intent.putExtra("username", user);
        startActivity(intent);
    }

    public boolean checkCategoryBCode(String category){
        boolean isMatch = true;
        // we have 8 categories
        for(int i = 0; i < 8 ; i++){
            //compare binary code
            if(this.categoryBCode.charAt(i)=='*') continue;
            if(this.categoryBCode.charAt(i)==category.charAt(i)){ }
            else {
                isMatch = false;
                break;
            }
        }
        return isMatch;
    }

    public void renderByCategory(String genre){
        changeFilter();
        setCategoryBCode(genre);
        changeListener();
    }
    public void renderBySearch(String search) {
        changeFilter();
        bySearch = true;
        this.search = search;
        changeListener();
    }
    public void renderByByUser(){
        changeFilter();
        setUser();
        changeListener();
    }

    public void setListener(){
        movieRef = rootRef.child("movies");
        movieRef.addChildEventListener(childEventListener);
    }
    public void changeListener(){
        myAdapter.removeAllItem();
        movieRef.removeEventListener(childEventListener);
        addChildListener();
        setListener();
    }
    public void changeFilter(){
        this.byCategory = false;
        this.byUser = false;
        this.bySearch = false;
    }
    public void setUser(){
        this.byUser = true;
    }

    public void setParent(String parent){
        if(parent.equals("home")) {
            isHomeParent = true;
            isCartParent = false;
            isProfileParent = false;
        }
        else if(parent.equals("cart")) {
            isHomeParent = false;
            isCartParent = true;
            isProfileParent = false;
        }
        else if(parent.equals("profile")){
                isHomeParent = false;
                isCartParent = false;
                isProfileParent = true;
        }
    }

    public boolean matchSearch(String movieName, String search){
        String movieWithNoSpecialLetter = movieName.replaceAll("[^a-zA-Z0-9\\s_-]", "");
        String searchInputWithNoSpecialLetter = search.replaceAll("[^a-zA-Z0-9\\s_-]", "");
        if(movieWithNoSpecialLetter.toLowerCase().contains(searchInputWithNoSpecialLetter.toLowerCase())){
            return true;
        }
        else if(movieName.toLowerCase().contains(search.toLowerCase())){
            return true;
        }
        else return false;
    }
    public void setCategoryBCode(String categoryBCode){
        this.categoryBCode = categoryBCode;
        this.byCategory = true;
    }

    public void addChildListener(){
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String id = dataSnapshot.getKey();

                //get movie properties & details
                final Float price = dataSnapshot.child("price").getValue(Float.class);
                final Integer popularity = dataSnapshot.child("popularity").getValue(Integer.class);

                String path = "movies/"+id+"/content.txt";
                StorageReference contentRef = FirebaseStorage.getInstance().getReference().child(path);
                contentRef.getBytes(IN_MB).addOnSuccessListener(bytes -> {
                    String content = new String(bytes);
                    final String[] contents = content.split(System.getProperty("line.separator"));
                    rootRef.child("purchaseStatus").child(user).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds2) {
                            boolean isMatch = true;
                            if(bySearch){
                                if(matchSearch(contents[0], search)){}
                                else isMatch = false;
                            }
                            if(byCategory){
                                if(checkCategoryBCode(contents[2])){ }
                                else isMatch = false;
                            }
                            if(byUser){
                                if(ds2.exists()){
                                    if(ds2.getValue(Integer.class)==2){}
                                    else isMatch = false;
                                }
                                else isMatch = false;
                            }
                            if(isMatch){
                                myAdapter.addItem(new MovieBrief(id , contents[0], contents[1], popularity, price));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
