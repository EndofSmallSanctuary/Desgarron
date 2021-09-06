package com.example.desgarron.Logic.SocketManagement;

import com.example.desgarron.Log.DesgarronLog;
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
                        onPostexecute(service, new NetworkEvent(NetworkEvent.EVENT_NETWORK_DISCONNECTED, "Не удалось установить соеднинение"));
                        return;
                    } catch (InterruptedException e) {
                        return;
                    } catch (UnresolvedAddressException e) {
                        onPostexecute(service, new NetworkEvent(NetworkEvent.EVENT_NETWORK_DISCONNECTED, "Адрес параша"));
                        return;
                    }

                }
            });
        } else {
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
                    service.transferEvent(new NetworkEvent(NetworkEvent.EVENT_NETWORK_DISCONNECTED, "Сервах сдох нах"));
                } else {
                    service.transferEvent(new NetworkEvent(NetworkEvent.EVENT_NETWORK_CONNECTED, "Установлено соединение"));
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
            DesgarronLog.append(e.getMessage());
        }
    }

//    public boolean validateConnection(){
//
//    }
}