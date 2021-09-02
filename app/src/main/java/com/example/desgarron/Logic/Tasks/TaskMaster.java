package com.example.desgarron.Logic.Tasks;

import com.example.desgarron.Logic.SigurComponents.Crypto;
import com.example.desgarron.Models.Direction;
import com.example.desgarron.Models.Protocol;
import com.example.desgarron.Models.Request;
import com.example.desgarron.Models.SigurTransaction;

import java.util.Date;
import java.util.LinkedList;


public class TaskMaster {

  static SigurLog mLog;

    public static SigurTransaction createEMPTransaction(byte[] personCard){
        byte[] bArr2 = personCard;
        Request request = new Request(Protocol.NFC_TERMINAL_REQUEST);
        request.putInt(6);
        request.putInt(bArr2.length);
        request.putBytes(bArr2);

        SigurTransaction transaction = new SigurTransaction(SigurTransaction.TRANSACTION_EMP,request.toBuffer());

        return transaction;
    };

    public static SigurTransaction logPersonDirection(int empID,int direction, Date date){
            mLog = new SigurLog(1, ((Integer)empID).intValue(),((Integer)direction).intValue(),date);
            LinkedList linkedList = new LinkedList();
            linkedList.add(mLog);
            byte[] encrypt = Crypto.encrypt(SigurLog.toJsonString(linkedList).getBytes());
            Request request = new Request(Protocol.NFC_TERMINAL_REQUEST);
            request.putInt(4);
            request.putInt(1);
            request.putInt(encrypt.length);
            request.putBytes(encrypt);
            if(direction == Direction.IN){
                return new SigurTransaction(SigurTransaction.TRANSACTION_ENTRY,request.toBuffer());
            }
            if(direction == Direction.OUT){
                return new SigurTransaction(SigurTransaction.TRANSACTION_EXIT,request.toBuffer());
            }
           return null;
        }
    }
