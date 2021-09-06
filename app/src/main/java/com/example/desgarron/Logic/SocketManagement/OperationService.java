package com.example.desgarron.Logic.SocketManagement;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.desgarron.Log.DesgarronLog;
import com.example.desgarron.Logic.SigurComponents.Crypto;
import com.example.desgarron.Logic.SigurComponents.PacketManager;
import com.example.desgarron.Logic.SigurComponents.PacketReader;
import com.example.desgarron.Logic.SigurComponents.PacketWriter;
import com.example.desgarron.Models.Emp2_0;
import com.example.desgarron.Models.InPacket;
import com.example.desgarron.Models.NetworkEvent;
import com.example.desgarron.Models.SigurTransaction;
import com.example.desgarron.Utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class OperationService extends Service {

    MutableLiveData<Emp2_0> empResult = new MutableLiveData<>();



    public MutableLiveData<Emp2_0> getEmpResult() {
        return empResult;
    }

    private final int connectionCheckInterval = 3000;
    private final int pingMSGLength = 8;
    private final IBinder binder = new OperationBinder();
    Provider networkProvider = new Provider();
    SocketChannel sigurChannel;
    PacketReader reader = new PacketReader();
    PacketWriter writer = new PacketWriter();
    Boolean binded = false;
    Selector selector;
    BlockingQueue<SigurTransaction> transactions;

    Handler connection_handler = new Handler();
    Runnable connection_runnable;


    SigurTransaction awaitedTransaction = null;


    public void onChannelCreated(SocketChannel channel) {
        this.sigurChannel = channel;
        this.transactions = new LinkedBlockingQueue<>();

        //ПОМЕНЯТЬ СТРУКТУРУ
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    selector = Selector.open();
                    if(!sigurChannel.isRegistered()){
                        sigurChannel.register(selector,  SelectionKey.OP_READ | SelectionKey.OP_WRITE );
                    }
                    while (binded) {
                        int readychannels  = selector.selectNow();
                        if(readychannels==0) continue;
                        Set selectedKeys = selector.selectedKeys();
                        Iterator keyIterator = selectedKeys.iterator();
                        while (keyIterator.hasNext()) {

                            SelectionKey key = (SelectionKey) keyIterator.next();
                            if (key.isAcceptable()) {
                            } else if (key.isConnectable()) {
                                // a connection was established with a remote server.
                            } else if (key.isReadable()) {
                                InPacket newPacket = reader.read(sigurChannel);
                                //Кто-то поменял нас в сигуре
                                Log.d("desdogs",Utils.bytesToHexString(newPacket.mData.array()));
                                if(newPacket.mData.array().length!=pingMSGLength) {
                                    if (awaitedTransaction != null) {
                                        switch (awaitedTransaction.getType()) {
                                            case SigurTransaction.TRANSACTION_EMP: {
                                                try {
                                                    Emp2_0 empSigurBASE = PacketManager.onEMPAwaited(newPacket);
                                                    empResult.postValue(empSigurBASE);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    DesgarronLog.append(e.getMessage());
                                                    transferEvent(new NetworkEvent(NetworkEvent.EVENT_EMP_ERROR, "Ошибка парсинга челика"));
                                                } finally {
                                                    awaitedTransaction = null;
                                                }
                                                break;
                                            }
                                            case SigurTransaction.TRANSACTION_LOGIN: {
                                                try {
                                                    if (!PacketManager.onLoginAwaited(newPacket))
                                                        transferEvent(new NetworkEvent(NetworkEvent.EVENT_LOGIN_FAILED, "Ошибка логина"));
                                                    else transferEvent(new NetworkEvent(NetworkEvent.EVENT_LOGIN_SUCCESS,""));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    DesgarronLog.append(e.getMessage());
                                                } finally {
                                                    awaitedTransaction = null;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else if (key.isWritable()) {
                                //Авторизацию в очередь
                                    if(transactions.size()>0) {
                                        awaitedTransaction = transactions.poll();
                                        writer.writeTransaction(sigurChannel, awaitedTransaction);
                                    }

                            }
                            keyIterator.remove();
                        }

                        Thread.currentThread().sleep(40);
                    }

                    try {
                        networkProvider.validateANDfinishSocket(sigurChannel);
                    } catch (Exception e){
                       DesgarronLog.append(e.getMessage());
                    }
                } catch (IOException e) {
                    DesgarronLog.append(e.getMessage());
                    networkProvider.validateANDfinishSocket(sigurChannel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {
        binded=false;
        connection_handler.removeCallbacks(connection_runnable);
        DesgarronLog.append("Terminal service unbinded");
        return super.onUnbind(intent);
    }

    public class OperationBinder extends Binder {
        public OperationService getService(){
            return OperationService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        binded=true; return binder;
    }


    public void transferEvent(NetworkEvent event){
        Intent intent = new Intent("NETWORK_EVENT");
        Bundle bundle = new Bundle();
        bundle.putParcelable("event_content",event);
        intent.putExtra("event",bundle);
        sendBroadcast(intent);
    }



    public void onAddrSelected(String servername){
           // transactions = new LinkedBlockingQueue<>();
            postConnection(servername);

    }

    public void enqueuePacket(SigurTransaction transaction){
        if(sigurChannel!=null&&sigurChannel.isConnected())
            transactions.offer(transaction);
    }


    private void postConnection(String servername){

        connection_runnable = new Runnable() {
            @Override
            public void run() {
                if(sigurChannel==null||sigurChannel.socket().isClosed()){
                networkProvider.doInBackGround(OperationService.this,servername);
                }

                connection_handler.postDelayed(connection_runnable,connectionCheckInterval);
            }
        };

        connection_handler.post(connection_runnable);
    }
}
