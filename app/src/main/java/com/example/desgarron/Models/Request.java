package com.example.desgarron.Models;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Request extends OutPacket {
    private final CountDownLatch mLatch = new CountDownLatch(1);
    private InPacket mResponse = null;

    public ByteBuffer getElderPacket(){
        return toBuffer();
    }

    public ByteBuffer getData(){
        return super.mData;
    }

    public Request(int i) {
        super(1, i);
    }

    public InPacket get(long j, TimeUnit timeUnit) throws InterruptedException {
        this.mLatch.await(j, timeUnit);
        return this.mResponse;
    }

    /* access modifiers changed from: package-private */
    public void notifyResponse(InPacket inPacket) {
        this.mResponse = inPacket;
        this.mLatch.countDown();
    }
}
