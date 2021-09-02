package com.example.desgarron.Models;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public abstract class OutPacket extends Packet {
    private static final int blockSize = 64;



    public void putByte(byte b) {
        if (this.mData == null) {
            allocateData(1);
        }
        if (this.mData.remaining() < 1) {
            reallocateData(1);
        }
        this.mData.put(b);
    }

    public void putBytes(byte[] bArr) {
        int length = bArr.length;
        if (this.mData == null) {
            allocateData(length);
        }
        if (this.mData.remaining() < length) {
            reallocateData(length);
        }
        this.mData.put(bArr);
    }

    public void putString(String str) {
        try {
            putBytes(str.getBytes("UTF-8"));
            putByte((byte) 0);
        } catch (UnsupportedEncodingException unused) {
            throw new IllegalArgumentException();
        }
    }

    public void putInt(int i) {
        if (this.mData == null) {
            allocateData(4);
        }
        if (this.mData.remaining() < 4) {
            reallocateData(4);
        }
        this.mData.putInt(i);
    }

    public void putLong(long j) {
        if (this.mData == null) {
            allocateData(8);
        }
        if (this.mData.remaining() < 8) {
            reallocateData(8);
        }
        this.mData.putLong(j);
    }

    /* access modifiers changed from: package-private */
    public void setTrid(int i) {
        this.mTrid = i;
    }

    /* access modifiers changed from: package-private */
    public ByteBuffer toBuffer() {
        int i = 16;
        if (this.mData != null) {
            this.mData.flip();
            i = 16 + this.mData.remaining();
        }
        ByteBuffer allocate = ByteBuffer.allocate(i);
        allocate.putInt(i);
        allocate.putInt(this.mType);
        allocate.putInt(this.mTrid);
        allocate.putInt(this.mCode);
        if (this.mData != null) {
            allocate.put(this.mData);
        }
        allocate.flip();
        return allocate;
    }

    protected OutPacket(int i, int i2) {
        super(i, i2);
    }

    private void allocateData(int i) {
        if (i < 1) {
            throw new IllegalArgumentException();
        } else if (this.mData == null) {
            this.mData = ByteBuffer.allocate(((i / 128) * 128) + 128);
        } else {
            throw new IllegalStateException();
        }
    }

    private void reallocateData(int i) {
        if (i < 1) {
            throw new IllegalArgumentException();
        } else if (this.mData != null) {
            this.mData.flip();
            ByteBuffer allocate = ByteBuffer.allocate(this.mData.remaining() + ((i / 128) * 128) + 128);
            allocate.put(this.mData);
            this.mData = allocate;
        } else {
            throw new IllegalStateException();
        }
    }
}
