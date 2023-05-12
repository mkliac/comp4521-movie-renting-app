package com.example.comp4521project.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.comp4521project.R;
import com.example.comp4521project.UserData.Users;
import com.example.comp4521project.movieUI.AllLibrary;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeFragment extends Fragment {
    Button buttonAll, buttonAction, buttonAdventure, buttonCartoon, buttonComedy;
    Button buttonDocumentary, buttonHorror, buttonMystery, buttonScifi;
    SearchView search;
    private HomeViewModel homeViewModel;
    FragmentManager fm;
    Fragment homePage;
    boolean active;
    AllLibrary allLibraryPage;
    Users user;
    boolean start = false;

    ImageView rotateThumbnail;
    int[] images = {R.drawable.icon_action, R.drawable.icon_adventure, R.drawable.icon_cartoon,
                    R.drawable.icon_comedy, R.drawable.icon_documentary, R.drawable.icon_horror,
                    R.drawable.icon_mystery, R.drawable.icon_scifi};

    public HomeFragment(){}
    public HomeFragment(Users user){this.user = user; start = true;}


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        if(start){
            fm = getFragmentManager();
            homePage = this;
            active = true;
            allLibraryPage = (AllLibrary) fm.findFragmentByTag("library");
            buttonAll = (Button) root.findViewById(R.id.buttonAll);
            buttonAction = (Button) root.findViewById(R.id.buttonAction);
            buttonAdventure = (Button) root.findViewById(R.id.buttonAdventure);
            buttonCartoon = (Button) root.findViewById(R.id.buttonCartoon);
            buttonComedy = (Button) root.findViewById(R.id.buttonComedy);
            buttonDocumentary = (Button) root.findViewById(R.id.buttonDocumentary);
            buttonHorror = (Button) root.findViewById(R.id.buttonHorror);
            buttonMystery = (Button) root.findViewById(R.id.buttonMystery);
            buttonScifi = (Button) root.findViewById(R.id.buttonScifi);
            search = (SearchView) root.findViewById(R.id.searchView) ;
            rotateThumbnail = (ImageView) root.findViewById(R.id.temp);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            rotateThumbnail.setImageResource(images[0]);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                int i = 0;
                @Override
                public void run() {
                    rotateThumbnail.setImageResource(images[(++i) % images.length]);
                    handler.postDelayed(this, 4000);
                }
            };
            handler.postDelayed(runnable, 4000);

            setButtons();
        }


        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if(active){
            }
        }else{

        }
    }

    private void setButtons(){
        buttonAll.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("********");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonAction.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("1*******");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonAdventure.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("*1******");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonCartoon.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("**1*****");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonComedy.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("***1****");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonDocumentary.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("****1***");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonHorror.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("*****1**");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonMystery.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("******1*");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        buttonScifi.setOnClickListener(view -> {
            active = false;
            allLibraryPage.refreshByGenre("*******1");
            allLibraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
            active = true;
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.equals("")){return false;}
                else{
                    allLibraryPage.refreshBySearch(s);
                    allLibraryPage.setParent("home");
                    fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}