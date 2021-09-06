package com.example.desgarron;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.desgarron.Log.DesgarronLog;
import com.example.desgarron.Logic.SigurComponents.Crypto;
import com.example.desgarron.Logic.SigurComponents.DeviceId;
import com.example.desgarron.Logic.SocketManagement.OperationService;
import com.example.desgarron.Logic.Tasks.TaskMaster;
import com.example.desgarron.Models.Emp2_0;
import com.example.desgarron.Models.NetworkEvent;
import com.example.desgarron.Models.SigurTransaction;
import com.example.desgarron.Utils.Utils;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TerminalFragment extends Fragment {

     Emp2_0 toSaveEMP;

    @BindView(R.id.auth_connected_holder)
    ConstraintLayout auth_form_holder;
    @BindView(R.id.auth_connectedto_host)
    TextView auth_form_host;
    @BindView(R.id.auth_login)
    EditText auth_form_login;
    @BindView(R.id.auth_password)
    EditText auth_form_password;
    @BindView(R.id.auth_enter)
    Button auth_form_enter;


    @BindView(R.id.terminal_emp_container)
     ConstraintLayout container;
     @BindView(R.id.terminal_loading)
     SpinKitView loadingView;
     @BindView(R.id.terminal_noSettings)
     ImageView foreground;
     SharedPreferences preferences;

     BroadcastReceiver eventReciever = new EventReciever();
     OperationService mService;
     String terminal_host;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if (getArguments() != null) {
        }
        Crypto.init(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        ButterKnife.bind(this,view);


        foreground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparePacket(TaskMaster.createEMPTransaction(Utils.hexStringToBytes("049346924F5F80")));
            }
        });
        auth_form_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth_form_login.getText().toString().length()>0) {
                    SigurTransaction transaction = TaskMaster.createLoginRequest(
                            auth_form_login.getText().toString().trim(),
                            auth_form_password.getText().toString().trim(),
                            DeviceId.get(getActivity()));
                    preparePacket(transaction);
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
       getActivity().registerReceiver(eventReciever,new IntentFilter("NETWORK_EVENT"));
        if(!preferences.contains("ip_terminal")){
            foreground.setImageDrawable(getResources().getDrawable(R.drawable.kissmyass,null));
            loadingView.setVisibility(View.GONE);
            foreground.setVisibility(View.VISIBLE);
        } else {
            terminal_host = preferences.getString("ip_terminal","");
            getActivity().bindService(new Intent(getActivity(),OperationService.class),connection,Context.BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(eventReciever);
        if(mService!=null)
            getActivity().unbindService(connection);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OperationService.OperationBinder binder = (OperationService.OperationBinder) service;
            mService= binder.getService();
            mService.getEmpResult().observe(TerminalFragment.this, new Observer<Emp2_0>() {
                @Override
                public void onChanged(Emp2_0 emp2_0) {
                    toSaveEMP = emp2_0;
                    fillEmpFragment(emp2_0);
                }
            });
            mService.onAddrSelected(terminal_host);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            DesgarronLog.append("Terminal service disconnected");
        }
    };

    public class EventReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("NETWORK_EVENT")){
                try{
                    Bundle b = intent.getParcelableExtra("event");
                    NetworkEvent event = (NetworkEvent) b.getParcelable("event_content");
                    switch (event.getType()){
                        case NetworkEvent.EVENT_NETWORK_DISCONNECTED:{

                            loadingView.setVisibility(View.VISIBLE);

                            foreground.setVisibility(View.GONE);

                            auth_form_holder.setVisibility(View.GONE);

                            DesgarronLog.append(event.getInfo());
                            Toasty.error(context,event.getInfo(),Toasty.LENGTH_SHORT).show();
                            break;
                        }
                        case NetworkEvent.EVENT_WARNING:{
                            DesgarronLog.append(event.getInfo());
                            Toasty.warning(context,event.getInfo(),Toasty.LENGTH_SHORT).show();
                            break;
                        }
                        case NetworkEvent.EVENT_NETWORK_CONNECTED:{
                            onConnectionEstablished(event);
                            Toasty.success(context,event.getInfo(),Toasty.LENGTH_LONG).show();
                            break;
                        }
                        case NetworkEvent.EVENT_EMP_ERROR:
                        case NetworkEvent.EVENT_LOGIN_FAILED: {
                            DesgarronLog.append(event.getInfo());
                            Toasty.error(context,event.getInfo(),Toasty.LENGTH_SHORT).show();
                            break;
                        }
                        case NetworkEvent.EVENT_LOGIN_SUCCESS:{
                            DesgarronLog.append("Login successfull");
                            Toasty.success(context,event.getInfo(),Toasty.LENGTH_LONG).show();
                            auth_form_holder.setVisibility(View.GONE);
                            foreground.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                } catch (Exception e){
                    DesgarronLog.append(e.getMessage());
                }
            }
        }
    }

    private void onConnectionEstablished(NetworkEvent event) {
        loadingView.setVisibility(View.GONE);
        DesgarronLog.append(event.getInfo());
        auth_form_host.setText(preferences.getString("ip_terminal",""));
        auth_form_holder.setVisibility(View.VISIBLE);
    }

    private void fillEmpFragment(Emp2_0 emp2_0){

        EMPFragment empFragment = new EMPFragment();
        Bundle b = new Bundle();
        b.putParcelable("emp",emp2_0);
        empFragment.setArguments(b);
        foreground.setVisibility(View.GONE);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.terminal_emp_container,empFragment,"emp_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void onChildFragmentGone(){
        foreground.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void preparePacket(SigurTransaction transaction){
        mService.enqueuePacket(transaction);
    }
}