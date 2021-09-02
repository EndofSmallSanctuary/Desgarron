package com.example.desgarron.Logic.SocketManagement;

import android.util.Log;

import com.example.desgarron.Models.NetworkEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.TimeUnit;

public class Provider extends AsynkTaskisBack{
    SocketChannel channel;
    //String servername = "192.168.31.43";


    public void doInBackGround(OperationService service, String servername) {
        if (channel == null || channel.socket().isClosed()||!channel.isConnected()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if(channel != null&&!channel.socket().isClosed()) {
                        try {
                            channel.socket().close();
                        } catch (Exception e){
                        }
                    }
                    try {
                        InetSocketAddress address = new InetSocketAddress(servername, 3308);
                        channel = SocketChannel.open();
                        channel.configureBlocking(false);
                        channel.connect(address);
                        long await = 1000;
                        long nowInMilis = System.currentTimeMillis();
                        while (!channel.finishConnect()) {
                            if (System.currentTimeMillis() > nowInMilis + await) {
                                break;
                            }
                            TimeUnit.MILLISECONDS.sleep(50);
                        }
                        onPostexecute(service, null);
                        return;
                    } catch (IOException e) {
                        Log.d("grimjow", e.getMessage());
                        onPostexecute(service, new NetworkEvent(NetworkEvent.EVENT_NETWORK_ERROR, "Не удалось установить соеднинение"));
                        return;
                    } catch (InterruptedException e) {
                        Log.d("grimjow", e.getMessage());
                        return;
                    } catch (UnresolvedAddressException e) {
                        Log.d("grimjow", "Адрес параша");
                        onPostexecute(service, new NetworkEvent(NetworkEvent.EVENT_NETWORK_ERROR, "Адрес параша"));
                        return;
                    }

                }
            });
        } else {
            Log.d("grimjow","socket already connected");
//            onPostexecute(service,null);
        }
    }

    public void onPostexecute(OperationService service, NetworkEvent event) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (event != null) {
                    service.transferEvent(event);
                    return;
                }

                if (!channel.isConnected()) {
                    service.transferEvent(new NetworkEvent(NetworkEvent.EVENT_NETWORK_ERROR, "Сервах сдох нах"));
                } else {
                    service.transferEvent(new NetworkEvent(NetworkEvent.EVENT_NETWORK_SUCCESS, ""));
                    service.onChannelCreated(channel);
                }
            }
        });

    }

    public void validateANDfinishSocket(SocketChannel channel){
        if(!channel.socket().isClosed())
        try {
            channel.socket().close();
        } catch (IOException e) {
            Log.d("grimjowExeption",e.getMessage());
        }
    }

//    public boolean validateConnection(){
//
//    }
}