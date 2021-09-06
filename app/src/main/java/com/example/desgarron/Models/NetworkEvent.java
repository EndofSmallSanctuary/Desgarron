package com.example.desgarron.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class NetworkEvent implements Parcelable {

    //EVENT-TYPE CONSTANTS PRE-DEFINED
    public static final int EVENT_NETWORK_DISCONNECTED = 1;
    public static final int EVENT_WARNING = 2;
    public static final int EVENT_NETWORK_CONNECTED = 3;
    public static final int EVENT_EMP_ERROR = 4;
    public static final int EVENT_LOGIN_SUCCESS = 5;
    public static final int EVENT_LOGIN_FAILED = 6;

    private int type;
    private String info;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.info);
    }

    public void readFromParcel(Parcel source) {
        this.type = source.readInt();
        this.info = source.readString();
    }

    public NetworkEvent() {
    }

    public NetworkEvent(int type, String info) {
        this.type = type;
        this.info = info;
    }


    protected NetworkEvent(Parcel in) {
        this.type = in.readInt();
        this.info = in.readString();
    }

    public static final Parcelable.Creator<NetworkEvent> CREATOR = new Parcelable.Creator<NetworkEvent>() {
        @Override
        public NetworkEvent createFromParcel(Parcel source) {
            return new NetworkEvent(source);
        }

        @Override
        public NetworkEvent[] newArray(int size) {
            return new NetworkEvent[size];
        }
    };
}
