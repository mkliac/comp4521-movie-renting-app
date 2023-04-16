package com.example.comp4521project.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.comp4521project.R;
import com.example.comp4521project.UserData.Users;
import com.example.comp4521project.movieUI.AllLibrary;

public class HomeFragment extends Fragment {
    Button buttonAll, buttonAction, buttonAdventure, buttonCartoon, buttonComedy;
    Button buttonDocumentary, buttonHorror, buttonMystery, buttonScifi;
    SearchView search;
    private HomeViewModel homeViewModel;
    FragmentManager fm;
    //FragmentTransaction fmTran;
    Fragment homePage;
    boolean active;
    AllLibrary allLibraryPage;
    Users user;
    boolean start = false;

    public HomeFragment(){}
    public HomeFragment(Users user){this.user = user; start = true;}


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        if(start){
            fm = getFragmentManager();
            //fmTran = fm.beginTransaction();
            homePage = this;
            active = true;
            allLibraryPage = (AllLibrary) fm.findFragmentByTag("library");
            //fm.beginTransaction().add(R.id.main_container, allLibraryPage, "movieDisplay").hide(allLibraryPage).commit();
            //fmTran.add(R.id.main_container, homePage, "home").commit();
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

            setButtons();
        }


        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // 不在最前端显示 相当于调用了onPause();
            if(active){
                //fmTran.hide(allLibraryPage).commit();
            }
        }else{  // 在最前端显示 相当于调用了onResume();
            //网络数据刷新
        }
    }

    private void setButtons(){
        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("********");
                //allLibraryPage.refreshByUserOrder(user.getUsername());
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("1*******");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("*1******");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("**1*****");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonComedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("***1****");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonDocumentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("****1***");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonHorror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("*****1**");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonMystery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("******1*");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
        });
        buttonScifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = false;
                allLibraryPage.refreshByGenre("*******1");
                allLibraryPage.setParent("home");
                fm.beginTransaction().hide(homePage).show(allLibraryPage).commit();
                active = true;
            }
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