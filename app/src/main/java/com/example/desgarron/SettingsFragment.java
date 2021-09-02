package com.example.desgarron;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingsFragment extends Fragment {



    //TODO Сделать так чтобы не записывать каждый раз данные в прфференсес
    SharedPreferences preferences;
    @BindView(R.id.settings_field_terminal)
    EditText terminalIP;
    @BindView(R.id.settings_field_waybills)
    EditText waybillsIP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this,view);

        if(preferences.contains("ip_terminal")){
          terminalIP.setText(preferences.getString("ip_terminal",""));
        }
        if(preferences.contains("ip_waybills")){
            waybillsIP.setText(preferences.getString("ip_waybills",""));
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = preferences.edit();
        if(ipfieldvalidation(terminalIP))
            editor.putString("ip_terminal",terminalIP.getText().toString().trim())
            .commit();
        if(ipfieldvalidation(waybillsIP))
            editor.putString("ip_waybills",waybillsIP.getText().toString().trim())
            .commit();
    }



    private boolean ipfieldvalidation(EditText ipfield) {
        String initialString = ipfield.getText().toString().trim();

        int dots = 0;
        //Contains 4 dots
        for(int i=0;i<initialString.length();i++){
            if(initialString.charAt(i)=='.'){
                dots++;
            }
        }
        if(dots!=3) return false;

        return true;
    }
}