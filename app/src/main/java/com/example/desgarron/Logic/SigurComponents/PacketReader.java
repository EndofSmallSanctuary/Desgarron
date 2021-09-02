package com.example.desgarron.Logic.SigurComponents;



import com.example.desgarron.Models.InPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PacketReader {
    private static final int bytesInLengthBuffer = 4;
    private static final int bytesInSmallBuffer = 65536;
    private ByteBuffer mLargeBuffer;
    private final ByteBuffer mLengthBuffer = ByteBuffer.allocate(4);
    private final ByteBuffer mSmallBuffer = ByteBuffer.allocate(65536);

    public InPacket read(SocketChannel socketChannel) throws IOException {
        if (this.mLengthBuffer.hasRemaining()) {
            if (socketChannel.read(this.mLengthBuffer) < 0) {
                throw new IOException("connection closed by remote side");
            } else if (this.mLengthBuffer.hasRemaining()) {
                return null;
            } else {
                int i = this.mLengthBuffer.getInt(0);
                if (i < 16) {
                    throw new IOException("got incorrect packet");
                } else if (i > 67108864) {
                    throw new IOException("got incorrect packet");
                } else if (i > 65536) {
                    ByteBuffer allocate = ByteBuffer.allocate(i);
                    this.mLargeBuffer = allocate;
                    allocate.putInt(i);
                } else {
                    this.mSmallBuffer.limit(i);
                    this.mSmallBuffer.putInt(i);
                }
            }
        }
        ByteBuffer byteBuffer = this.mLargeBuffer;
        if (byteBuffer == null) {
            byteBuffer = this.mSmallBuffer;
        }
        if (socketChannel.read(byteBuffer) < 0) {
            throw new IOException("connection closed by remote side");
        } else if (byteBuffer.hasRemaining()) {
            return null;
        } else {

            byteBuffer.flip();
            InPacket fromBuffer = InPacket.fromBuffer(byteBuffer);
//            Log.d("grimjowReader", Utils.bytesToHexString(fromBuffer.mData.array()));
//            Log.d("grimjowReader",fromBuffer.mData.array().length+"");

            ByteBuffer byteBuffer2 = this.mLargeBuffer;
            if (byteBuffer2 != null) {
                byteBuffer2.clear();
                this.mLargeBuffer = null;
            }
            this.mSmallBuffer.clear();
            this.mLengthBuffer.clear();
            return fromBuffer;
        }
    }

}
