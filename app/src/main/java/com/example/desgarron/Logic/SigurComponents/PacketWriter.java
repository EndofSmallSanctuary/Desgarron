package com.example.desgarron.Logic.SigurComponents;

import com.example.desgarron.Models.SigurTransaction;


import java.io.IOException;
import java.nio.channels.SocketChannel;

public class PacketWriter {

    public void writeTransaction(SocketChannel socketChannel, SigurTransaction t) throws IOException {
           socketChannel.write(t.getContent());
    }
}
