package com.example.desgarron.Models;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class InPacket extends Packet {
    public byte extractByte() {
        if (this.mData != null) {
            return this.mData.get();
        }
        throw new BufferUnderflowException();
    }

    public void extractBytes(byte[] bArr) {
        if (this.mData != null) {
            this.mData.get(bArr);
            return;
        }
        throw new BufferUnderflowException();
    }

    public int extractInt() {
        if (this.mData != null) {
            return this.mData.getInt();
        }
        throw new BufferUnderflowException();
    }

    public String extractString() {
        if (this.mData != null) {
            byte[] bArr = new byte[this.mData.remaining()];
            int i = 0;
            while (true) {
                byte b = this.mData.get();
                if (b == 0) {
                    try {
                        return new String(bArr, 0, i, "UTF-8");
                    } catch (UnsupportedEncodingException unused) {
                        throw new RuntimeException();
                    }
                } else {
                    bArr[i] = b;
                    i++;
                }
            }
        } else {
            throw new BufferUnderflowException();
        }
    }

    public static InPacket fromBuffer(ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() < 16) {
            throw new IllegalArgumentException();
        } else if (byteBuffer.remaining() <= 67108864) {
            byteBuffer.getInt();
            int i = byteBuffer.getInt();
            int i2 = byteBuffer.getInt();
            int i3 = byteBuffer.getInt();
            ByteBuffer byteBuffer2 = null;
            if (byteBuffer.hasRemaining()) {
                byteBuffer2 = ByteBuffer.allocate(byteBuffer.remaining());
                byteBuffer2.put(byteBuffer);
                byteBuffer2.flip();
            }
            return new InPacket(i, i2, i3, byteBuffer2);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private InPacket(int i, int i2, int i3, ByteBuffer byteBuffer) {
        super(i, i3);
        this.mTrid = i2;
        this.mData = byteBuffer;
    }
}
