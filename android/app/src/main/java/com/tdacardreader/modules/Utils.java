package com.tdacardreader.modules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Tar on 8/3/17.
 * company : Iwa Labs (Thailand)
 * email : tar@iwa.fi
 */
public class Utils {

    private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3 + 4];
        if (bytes.length < 10) {
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 3] = hexArray[v >>> 4];
                hexChars[j * 3 + 1] = hexArray[v & 0x0F];
                hexChars[j * 3 + 2] = ' ';
            }
        } else {
            for (int j = 0; j < 10; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 3] = hexArray[v >>> 4];
                hexChars[j * 3 + 1] = hexArray[v & 0x0F];
                hexChars[j * 3 + 2] = ' ';
            }
            if (bytes.length > 10) {
                hexChars[bytes.length * 3] = ' ';
                hexChars[bytes.length * 3 + 1] = '.';
                hexChars[bytes.length * 3 + 2] = '.';
                hexChars[bytes.length * 3 + 3] = '.';
            }
        }
        return new String(hexChars);
    }
}
