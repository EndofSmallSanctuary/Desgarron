package com.example.desgarron;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.desgarron.Log.DesgarronLog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        if(isStoragePermissionGranted())
            DesgarronLog.prepareLogFile(this);

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

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

}