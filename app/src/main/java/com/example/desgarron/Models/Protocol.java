package com.example.desgarron.Models;

public class Protocol {
    public static final int DATA_REQUEST = 3;
    public static final int EMP_REQUEST = 6;
    public static final int ERROR_ACCESS_POLICY = 9;
    public static final int ERROR_DB = 2;
    public static final int ERROR_LICENCE_VIOLATION = 11;
    public static final int ERROR_UNKNOWN_CARD = 8;
    public static final int ERROR_UNKNOWN_TERMINAL = 3;
    public static final int ERROR_UNKNOWN_USER = 4;
    public static final int ERROR_USER_ACCESS = 5;
    public static final int ERROR_USER_FLAGS = 6;
    public static final int ERROR_USER_PASSWORD = 7;
    public static final int ERROR_WRONG_REQUEST = 1;
    public static final int ERROR_WRONG_REQUEST_TYPE = 10;
    public static final int HANDSHAKE_REQUEST = 105;
    public static final int HANDSHAKE_RESPONSE = 106;
    public static final int HASH_REQUEST = 2;
    public static final int LOGIN_REQUEST = 107;
    public static final int LOGIN_RESPONSE = 108;
    public static final int LOG_REGUEST_OFFLINE = 2;
    public static final int LOG_REGUEST_ONLINE = 1;
    public static final int LOG_REQUEST = 4;
    public static final int NFC_TERMINAL_REQUEST = 195;
    public static final int NFC_TERMINAL_RESPONSE = 196;
    public static final int OK = 0;
    public static final int PAYDESK_EMP_REQUEST = 7;
    public static final int PAYDESK_SELL_REQUEST = 8;
    public static final int PHOTO_REQUEST = 5;
    public static final int PING_REPLY = 30;
    public static final int PING_REQUEST = 29;

    private Protocol() {
    }
}

