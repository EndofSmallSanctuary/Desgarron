package com.example.desgarron.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

public class Emp2_0 implements Parcelable {
    private int mId;
    private String mFIO;
    private String mNumber;
    private String mJob;
    private String mInfo;
    private int inaccess;
    private int outaccess;
    private byte[] photo;


    public Emp2_0(int mId, String mFIO, String mNumber, String mJob, String mInfo, int inaccess, int outaccess, byte[] photo) {
        this.mId = mId;
        this.mFIO = purifyString(mFIO);
        this.mNumber = purifyString(mNumber);
        this.mJob = purifyString(mJob);
        this.mInfo = purifyString(mInfo);
        this.inaccess = inaccess;
        this.outaccess = outaccess;
        this.photo = photo;
    }

    public Emp2_0(){};


    public void setmId(int mId) {
        this.mId = mId;
    }

    public void setmFIO(String mFIO) {
        this.mFIO = mFIO;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public void setmJob(String mJob) {
        this.mJob = mJob;
    }

    public void setmInfo(String mInfo) {
        this.mInfo = mInfo;
    }

    public void setInaccess(int inaccess) {
        this.inaccess = inaccess;
    }

    public void setOutaccess(int outaccess) {
        this.outaccess = outaccess;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getmId() {
        return mId;
    }

    public String getmFIO() {
        return mFIO;
    }

    public String getmNumber() {
        return mNumber;
    }

    public String getmJob() {
        return mJob;
    }

    public String getmInfo() {
        return mInfo;
    }

    public int getInaccess() {
        return inaccess;
    }

    public int getOutaccess() {
        return outaccess;
    }

    public byte[] getPhoto() {
        return photo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mFIO);
        dest.writeString(this.mNumber);
        dest.writeString(this.mJob);
        dest.writeString(this.mInfo);
        dest.writeInt(this.inaccess);
        dest.writeInt(this.outaccess);
        dest.writeByteArray(this.photo);
    }

    public void readFromParcel(Parcel source) {
        this.mId = source.readInt();
        this.mFIO = source.readString();
        this.mNumber = source.readString();
        this.mJob = source.readString();
        this.mInfo = source.readString();
        this.inaccess = source.readInt();
        this.outaccess = source.readInt();
        this.photo = source.createByteArray();
    }

    protected Emp2_0(Parcel in) {
        this.mId = in.readInt();
        this.mFIO = in.readString();
        this.mNumber = in.readString();
        this.mJob = in.readString();
        this.mInfo = in.readString();
        this.inaccess = in.readInt();
        this.outaccess = in.readInt();
        this.photo = in.createByteArray();
    }

    public static final Creator<Emp2_0> CREATOR = new Creator<Emp2_0>() {
        @Override
        public Emp2_0 createFromParcel(Parcel source) {
            return new Emp2_0(source);
        }

        @Override
        public Emp2_0[] newArray(int size) {
            return new Emp2_0[size];
        }
    };

    @Override
    public String toString() {
        return "Emp2_0{" +
                "mId=" + mId +
                ", mFIO='" + mFIO + '\'' +
                ", mNumber='" + mNumber + '\'' +
                ", mJob='" + mJob + '\'' +
                ", mInfo='" + mInfo + '\'' +
                ", inaccess=" + inaccess +
                ", outaccess=" + outaccess +
                ", photo=" + Arrays.toString(photo) +
                '}';
    }

    //Для corrupted карт
    public static Emp2_0 invalidEMP(){
       Emp2_0 unknown = new Emp2_0();
       unknown.setmInfo("Неизвестная карта");
       return unknown;
    }



    private String purifyString(String field){
        if(field!=null){
           String[] fieldsplit = field.split(" ");
           StringBuilder realField = new StringBuilder("");
           if(fieldsplit.length>0) {
               for (int i = 1; i < fieldsplit.length; i++) {
                   realField.append(fieldsplit[i]);
                   realField.append(" ");
               }
           }
           return realField.toString();
        }
        return field;
    }
}
