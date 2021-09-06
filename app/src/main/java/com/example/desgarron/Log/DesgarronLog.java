package com.example.desgarron.Log;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DesgarronLog {
    private Context context;
    private static final String estimatedfolder = "desgarron_externals";
    private static final String estimatedfile = "desgarron_log.txt";
    private static File logfile;

    public static void append(String content){
        if(logfile!=null){
            try {
                FileWriter writer = new FileWriter(logfile,true);


                writer.append(new Timestamp(System.currentTimeMillis())+": ");
                writer.append(content);
                writer.append('\n');


                writer.flush();
                writer.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public static void prepareLogFile(Context context){
        try{
            File root;
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                root = context.getExternalFilesDir(null);
            } else {
                root = new File(Environment.getExternalStorageDirectory(),estimatedfolder);
            }
            if(!root.exists()) {
               if(!root.mkdirs()){
               }
            }
            logfile = new File(root,estimatedfile);
            if(!logfile.exists()) {
                createFile();
            }

        } catch (Exception e){
            Log.d("dogs",e.getMessage());
        }
    }


    private static void createFile(){
        try {
            logfile.createNewFile();
            FileWriter writer = new FileWriter(logfile,true);



            writer.append("Desgarron Mobile Terminal\n");
            writer.append("-------------------------------\n");


            writer.flush();
            writer.close();
        } catch (Exception e){
            Log.d("dogs",e.getMessage());
        }

    }
}
