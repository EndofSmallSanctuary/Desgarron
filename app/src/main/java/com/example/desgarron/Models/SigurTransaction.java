package com.example.desgarron.Models;

import java.nio.ByteBuffer;

public class SigurTransaction {

    //TRANSACTION-TYPES
    public static final int TRANSACTION_EXIT = 1;
    public static final int TRANSACTION_ENTRY = 2;
    public static final int TRANSACTION_EMP = 4;
    public static final int TRANSACTION_PING = 1;

    private int type;
    private ByteBuffer content;

    public SigurTransaction(int type, ByteBuffer content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ByteBuffer getContent() {
        return content;
    }

    public void setContent(ByteBuffer content) {
        this.content = content;
    }
}
