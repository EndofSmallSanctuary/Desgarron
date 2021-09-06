package com.example.desgarron.Logic.SigurComponents;

import android.content.Context;
import android.util.Log;

import com.example.desgarron.SettingsFragment;
import com.example.desgarron.Utils.Utils;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String algorithm = "AES";
    private static final int blockSize = 16;
    private static byte[] key = null;
    private static final String transformation = "AES/CBC/NoPadding";

    private Crypto() {
    }

    public static byte[] decrypt(byte[] bArr) {
//        Log.d("dogs","indecrypt");
//        Log.d("dogs",Common.bytesToHexString(bArr));

        if (key == null) {
            throw new IllegalStateException();
        } else if (bArr == null) {
            throw new IllegalArgumentException();
        } else if (bArr.length % 16 != 0) {
            throw new IllegalArgumentException();
        } else {
            try {
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
                Cipher instance = Cipher.getInstance(transformation);
                instance.init(2, secretKeySpec, new IvParameterSpec(new byte[16]));
                byte[] doFinal = instance.doFinal(bArr);
                int length = doFinal.length;
                do {
                    length--;
                } while (doFinal[length] == 0);
                return Arrays.copyOfRange(doFinal, 0, length + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] encrypt(byte[] bArr) {
        if (key == null) {
            throw new IllegalStateException();
        } else if (bArr == null) {
            throw new IllegalArgumentException();
        } else {
            try {
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
                Cipher instance = Cipher.getInstance(transformation);
                instance.init(1, secretKeySpec, new IvParameterSpec(new byte[16]));
                int length = bArr.length;
                if (length % 16 == 0) {
                    return instance.doFinal(bArr);
                }
                int i = (length / 16) * 16;
                int i2 = i + 16;
                byte[] bArr2 = new byte[i2];
                instance.update(bArr, 0, i, bArr2, 0);
                instance.doFinal(Arrays.copyOfRange(bArr, i, i2), 0, 16, bArr2, i);
                return bArr2;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static byte[] getKey(Context context) {
        int i;
        int i2;
        String str = DeviceId.get(context);
        Log.d(Crypto.class.getName(), "device id = [" + str + "]");
        byte[] hexStringToBytes = hexStringToBytes(str);
        byte[] bArr = new byte[16];
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if (i4 == 0 || i4 == 1 || i4 == 8 || i4 == 9 || i4 == 10) {
                bArr[i4] = 108;
                i = i4 + 1;
                i2 = i3;
            } else if (i3 < 8) {
                i2 = i3 + 1;
                bArr[i4] = hexStringToBytes[i3];
                i = i4 + 1;
            } else {
                bArr[i4] = 0;
                i = i4 + 1;
                i2 = i3;
            }
            if (i >= 16) {
                return bArr;
            }
            i4 = i;
            i3 = i2;
        }
    }



    private static byte[] hexStringToBytes(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    static public void init(Context context) {
        if (key != null) {
            //  throw new IllegalStateException();
        }
        key = getKey(context);
        Log.d("crypto", Utils.bytesToHexString(key));
    }
}