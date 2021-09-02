package com.example.desgarron.Models;

import java.nio.ByteBuffer;

public abstract class Packet {
    public static final int MAX_LENGTH = 67108864;
    public static final int MIN_LENGTH = 16;
    public static final int TYPE_ASYNC = 0;
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_RESPONSE = 2;
    protected final int mCode;
    public ByteBuffer mData;
    protected int mTrid;
    protected final int mType;

    public Packet(int i, int i2) {
        this.mType = i;
        this.mCode = i2;
    }

    public int getType() {
        return this.mType;
    }

    public int getCode() {
        return this.mCode;
    }

    public String toString() {
        return "type=" + this.mType + ". code=" + this.mCode;
    }

    /* access modifiers changed from: package-private */
    public int getTrid() {
        return this.mTrid;
    }



}
