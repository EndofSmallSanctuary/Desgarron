package com.example.desgarron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.desgarron.Models.NetworkEvent;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity{


    @BindView(R.id.content_placeholder)
    FrameLayout fragmentPlaceholder;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);

        if(savedInstanceState==null){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.content_placeholder,new TerminalFragment())
                   // .addToBackStack(null)
                    .commit();
        }
        //TODO убрать перезапуск фрагментов
        navigationView.setOnItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.menu_main:{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_placeholder,new TerminalFragment())
                         //   .addToBackStack(null)
                            .commit();
                    break;
                }
                case R.id.menu_settings:{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_placeholder,new SettingsFragment())
                         //   .addToBackStack(null)
                            .commit();
                    break;
                }
            }
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        // if there is a fragment and the back stack of this fragment is not empty,
        // then emulate 'onBackPressed' behaviour, because in default, it is not working
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
        super.onBackPressed();
    }
}