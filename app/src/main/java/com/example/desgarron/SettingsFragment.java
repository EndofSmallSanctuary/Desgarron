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
import android.widget.TextView;

import com.example.desgarron.Log.DesgarronLog;
import com.example.desgarron.Logic.SigurComponents.DeviceId;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingsFragment extends Fragment {



    //TODO Сделать так чтобы не записывать каждый раз данные в прфференсес
    SharedPreferences preferences;
    @BindView(R.id.settings_field_terminal)
    EditText terminalIP;
    @BindView(R.id.settings_field_waybills)
    EditText waybillsIP;
    @BindView(R.id.settings_devid)
    TextView devID;


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

        if(preferences.contains("dev_id")){
            devID.setText(preferences.getString("dev_id"," "));
        } else {
            devID.setText(DeviceId.get(getActivity()));
            preferences.edit().putString("dev_id",devID.getText().toString()).commit();
        }

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
        if(ipfieldvalidation(terminalIP)) {
            editor.putString("ip_terminal", terminalIP.getText().toString().trim())
                    .commit();
            DesgarronLog.append("ip terminal changed to : "+terminalIP.getText().toString().trim());
        }
        if(ipfieldvalidation(waybillsIP)) {
            editor.putString("ip_waybills", waybillsIP.getText().toString().trim())
                    .commit();
            DesgarronLog.append("ip waybills changed to : "+waybillsIP.getText().toString().trim());
        }
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