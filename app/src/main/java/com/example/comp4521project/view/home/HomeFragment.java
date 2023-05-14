package com.example.comp4521project.view.home;

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
import com.example.comp4521project.model.Users;
import com.example.comp4521project.movieUI.Library;

public class HomeFragment extends Fragment {
    boolean start = false;
    Button allButton, actionButton, adventureButton, cartoonButton, comedyButton, documentaryButton, horrorButton, mysteryButton, scifiButton;
    SearchView search;
    Fragment homePage;
    boolean active;
    Library libraryPage;
    private HomeViewModel homeViewModel;
    FragmentManager fm;
    Users user;

    ImageView rotateThumbnail;
    int[] images = {R.drawable.action, R.drawable.adventure, R.drawable.cartoon,
                    R.drawable.comedy, R.drawable.documentary, R.drawable.horror,
                    R.drawable.mystery, R.drawable.scifi};

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
            libraryPage = (Library) fm.findFragmentByTag("library");
            allButton = root.findViewById(R.id.homef_buttonAll);
            actionButton = root.findViewById(R.id.homef_buttonAction);
            adventureButton = root.findViewById(R.id.homef_buttonAdventure);
            cartoonButton = root.findViewById(R.id.homef_buttonCartoon);
            comedyButton = root.findViewById(R.id.homef_buttonComedy);
            documentaryButton = root.findViewById(R.id.homef_buttonDocumentary);
            horrorButton = root.findViewById(R.id.homef_buttonHorror);
            mysteryButton = root.findViewById(R.id.homef_buttonMystery);
            scifiButton = root.findViewById(R.id.homef_buttonScifi);
            search = root.findViewById(R.id.homef_searchView) ;
            rotateThumbnail = root.findViewById(R.id.homef_rotate_thumbnail);

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

            initAllButtons();
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

    private void initAllButtons(){
        allButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("********");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        actionButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("1*******");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        adventureButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("*1******");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        cartoonButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("**1*****");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        comedyButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("***1****");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        documentaryButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("****1***");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        horrorButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("*****1**");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        mysteryButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("******1*");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        scifiButton.setOnClickListener(view -> {
            active = false;
            libraryPage.renderByCategory("*******1");
            libraryPage.setParent("home");
            fm.beginTransaction().hide(homePage).show(libraryPage).commit();
            active = true;
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.equals(""))
                    return false;
                else{
                    libraryPage.renderBySearch(s);
                    libraryPage.setParent("home");
                    fm.beginTransaction().hide(homePage).show(libraryPage).commit();
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