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

    private final IBinder binder = new OperationBinder();
    Provider networkProvider = new Provider();
    SocketChannel sigurChannel;
    PacketReader reader = new PacketReader();
    PacketWriter writer = new PacketWriter();
    Boolean binded = false;
    boolean authB = false;
    Selector selector;
    BlockingQueue<SigurTransaction> transactions;

    Handler connection_handler = new Handler();
    Runnable connection_runnable;


    SigurTransaction awaitedTransaction = null;


    public void onChannelCreated(SocketChannel channel) {
        this.sigurChannel = channel;
        this.transactions = new LinkedBlockingQueue<>();
        String auth = "0000003100000001000000010000006B000000077465737400D41D8CD98F00B204E9800998ECF8427E738D275082B1709B";

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
                                if(newPacket.mData.array().length>16) {
                                    if (awaitedTransaction != null) {
                                        switch (awaitedTransaction.getType()) {
                                            case SigurTransaction.TRANSACTION_EMP: {
                                                try {
                                                    Emp2_0 empSigurBASE = PacketManager.onEMPAwaited(newPacket);
                                                    empResult.postValue(empSigurBASE);
                                                    Log.d("grimjow", empSigurBASE.toString());
                                                } catch (Exception e) {
                                                    Log.d("grimjow", e.getMessage());
                                                    e.printStackTrace();
                                                    transferEvent(new NetworkEvent(NetworkEvent.EVENT_EMP_ERROR, "Ошибка парсинга челика"));
                                                }
                                                finally {
                                                    awaitedTransaction = null;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (key.isWritable()) {
                                //Авторизацию в очередь
                                if(!authB) {
                                    channel.write(ByteBuffer.wrap(Utils.hexStringToBytes(auth)));
                                    authB=true;
                                } else {
                                    if(transactions.size()>0) {
                                        awaitedTransaction = transactions.poll();
                                        writer.writeTransaction(sigurChannel, awaitedTransaction);
                                    }
                                }
                            }
                            keyIterator.remove();
                        }

                        Thread.currentThread().sleep(40);
                    }

                    try {
                        networkProvider.validateANDfinishSocket(sigurChannel);
                    } catch (Exception e){
                       Log.d("grimjow",e.getMessage());
                    }
                } catch (IOException e) {
                    authB = false;
                    Log.d("grimjowIO", e.getMessage());
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
        Log.d("grimjow","service unbind");
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
        Log.d("grimjow","sending broadcast");
        sendBroadcast(intent);
    }



    public void onAddrSelected(String servername){
            Crypto.init(Utils.hexStringToBytes("6C6C738D275082B16C6C6C709B000000"));
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
                else Log.d("grimjow","socket open");

                connection_handler.postDelayed(connection_runnable,6000);
            }
        };

        connection_handler.post(connection_runnable);
    }
}
