package com.example.desgarron;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.desgarron.Logic.Tasks.TaskMaster;
import com.example.desgarron.Models.Direction;
import com.example.desgarron.Models.Emp2_0;
import com.example.desgarron.Models.SigurTransaction;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EMPFragment extends Fragment {

    Emp2_0 showingEMP;

    @BindView(R.id.emp_action_enter)
    ImageView enter_img;
    @BindView(R.id.emp_action_exit)
    ImageView exit_img;
    @BindView(R.id.emp_fio)
    TextView field_fio;
    @BindView(R.id.emp_num)
    TextView field_num;
    @BindView(R.id.emp_job)
    TextView field_job;
    @BindView(R.id.emp_info)
    TextView field_info;
    @BindView(R.id.emp_photo)
    ShapeableImageView field_photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           showingEMP = getArguments().getParcelable("emp");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_e_m_p, container, false);
        ButterKnife.bind(this,view);

            enter_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TerminalFragment fragment = (TerminalFragment)getParentFragment();
                    fragment.preparePacket(TaskMaster.logPersonDirection(showingEMP.getmId(), Direction.IN,new Date()));
                    getActivity().onBackPressed();

                }
            });
            exit_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TerminalFragment fragment = (TerminalFragment)getParentFragment();
                    fragment.preparePacket(TaskMaster.logPersonDirection(showingEMP.getmId(),Direction.OUT,new Date()));
                    getActivity().onBackPressed();
                }
            });

            if(showingEMP.getmFIO()!=null)
            field_fio.setText(showingEMP.getmFIO());
            if(showingEMP.getmNumber()!=null)
            field_num.setText(showingEMP.getmNumber());
            if(showingEMP.getmJob()!=null)
            field_job.setText(showingEMP.getmJob());
            if(showingEMP.getmInfo()!=null)
            field_info.setText(showingEMP.getmInfo());
            if(showingEMP.getPhoto()!=null)
            field_photo.setImageBitmap(BitmapFactory.decodeByteArray(showingEMP.getPhoto(),0,showingEMP.getPhoto().length));
            return view;
    }

    @Override
    public void onDestroy() {
        TerminalFragment parentfragment =  (TerminalFragment)getParentFragment();
        parentfragment.onChildFragmentGone();
        super.onDestroy();
    }



}