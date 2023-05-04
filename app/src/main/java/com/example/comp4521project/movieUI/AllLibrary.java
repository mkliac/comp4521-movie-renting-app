package com.example.comp4521project.movieUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp4521project.Adapter.MovieAdapter;
import com.example.comp4521project.MovieData.MovieShort;
import com.example.comp4521project.R;
import com.example.comp4521project.Tester.MovieDetail;
import com.example.comp4521project.ui.cart.CartFragment;
import com.example.comp4521project.ui.home.HomeFragment;
import com.example.comp4521project.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AllLibrary extends Fragment implements MovieAdapter.onCardListener{

    List<MovieShort> movieShortList;
    MovieAdapter myAdapter;
    //MovieShort movie;
    boolean start = false;
    boolean byGenre = false;
    boolean byUserOrder = false;
    boolean bySearching = false;

    boolean parentHome = false;
    boolean parentCart = false;
    boolean parentProfile = false;

    String search;
    String user;
    ImageButton exitButton;
    View v;
    String genre = "********";
    String yearFilter = "any";
    String filter = "";
    HomeFragment home;
    CartFragment cart;
    ProfileFragment profile;
    final long ONE_MEGABYTE = 1024 * 1024;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference movieRef;
    ChildEventListener childVoyeur = new ChildEventListener() {
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

    public AllLibrary(){ }
    public AllLibrary(String user){
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_library_fragment, container, false);
        v = rootView;
        final FragmentManager fm = getFragmentManager();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(parentHome){
                    fm.beginTransaction().hide(AllLibrary.this).show(home).commit();
                }
                if(parentCart){
                    fm.beginTransaction().hide(AllLibrary.this).show(cart).commit();
                }
                if(parentProfile){
                    fm.beginTransaction().hide(AllLibrary.this).show(profile).commit();
                }
                fm.beginTransaction().hide(AllLibrary.this).commit();
            }
        };
        if(start){
            ((ImageButton) rootView.findViewById(R.id.exitButton2)).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   fm.beginTransaction().hide(AllLibrary.this).commit();
               }
           });
            RecyclerView myrv = (RecyclerView) rootView.findViewById(R.id.all_library_view);
            movieShortList = new ArrayList<>();
            myAdapter = new MovieAdapter(rootView.getContext(), movieShortList, user, this);
            myrv.setAdapter(myAdapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
            myrv.setLayoutManager(layoutManager);

            addChildListener();

            movieRef = rootRef.child("movies");
            //add blank event listener
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
        List<MovieShort> mData = myAdapter.returnList();
        Intent intent = new Intent(this.getContext(), MovieDetail.class);
        intent.putExtra("username", user);
        intent.putExtra("movie_id", mData.get(position).getId());
        startActivity(intent);
    }

    public boolean genreDecide(String genre){
        boolean match = true;
        for(int i = 0; i < 8 ; i++){
            if(this.genre.charAt(i)=='*'){ }
            else if(this.genre.charAt(i)==genre.charAt(i)){ }
            else {
                match = false;
                break;
            }
        }
        return match;
    }

    //pre-defined filtering method
    public void refreshByGenre(String genre){
        resetFilter();
        setGenre(genre);
        resetListener();
    }
    public void refreshBySearch(String search) {
        resetFilter();
        bySearching = true;
        this.search = search;
        resetListener();
    }
    public void refreshByUserOrder(String username){
        resetFilter();
        setUserOrder();
        resetListener();
    }
    public void customFilter(boolean byGenre, boolean byUserOrder, boolean bySearching, String username, String genreFilter, String search){}

    //set filter
    public void setListener(){
        movieRef = rootRef.child("movies");
        movieRef.addChildEventListener(childVoyeur);
    }
    public void resetListener(){
        myAdapter.removeAllItem();
        movieRef.removeEventListener(childVoyeur);
        addChildListener();
        setListener();
    }
    public void resetFilter(){
        this.byGenre = false;
        this.byUserOrder = false;
        this.bySearching = false;
    }
    public void setUserOrder(){
        this.byUserOrder = true;
    }

    //functional sub-method
    public void setParent(String parent){
        if(parent.equals("home")) {
            parentHome = true;
            parentCart = false;
            parentProfile = false;
        }
        else if(parent.equals("cart")) {
            parentHome = false;
            parentCart = true;
            parentProfile = false;
        }
        else if(parent.equals("profile")){
                parentHome = false;
                parentCart = false;
                parentProfile = true;
        }
    }


    public boolean matchSearch(String thisMovie, String search){
        String movieWithoutSPC = thisMovie.replaceAll("[^a-zA-Z0-9\\s_-]", "");
        String searchWithoutSPC = search.replaceAll("[^a-zA-Z0-9\\s_-]", "");
        if(movieWithoutSPC.toLowerCase().contains(searchWithoutSPC.toLowerCase())){
            return true;
        }
        else if(thisMovie.toLowerCase().contains(search.toLowerCase())){
            return true;
        }
        else return false;
    }
    public void setGenre(String genre){
        this.byGenre = true;
        this.genre = genre;
    }

    //main database access listener
    public void addChildListener(){
        childVoyeur = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String genre = dataSnapshot.child("genre").getValue().toString();
                final String id = dataSnapshot.getKey();
                final Integer popularity = dataSnapshot.child("popularity").getValue(Integer.class);
                final Float price = dataSnapshot.child("price").getValue(Float.class);

                String path = "movies/"+id+"/content.txt";
                StorageReference contentRef = FirebaseStorage.getInstance().getReference().child(path);
                contentRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        String content = new String(bytes);
                        final String[] stringArray = content.split(System.getProperty("line.separator"));
                        rootRef.child("purchaseStatus").child(user).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds2) {
                                boolean match = true;
                                if(byUserOrder){
                                    if(ds2.exists()){
                                        if(ds2.getValue(Integer.class)==2){}
                                        else match = false;
                                    }
                                    else match = false;
                                }
                                if(byGenre){
                                    if(genreDecide(stringArray[2])){ }
                                    else match = false;
                                }
                                if(bySearching){
                                    if(matchSearch(stringArray[0], search)){}
                                    else match = false;
                                }
                                if(match){
                                    myAdapter.addItem(new MovieShort(id , stringArray[0], stringArray[1], popularity, price, stringArray[2]));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
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
