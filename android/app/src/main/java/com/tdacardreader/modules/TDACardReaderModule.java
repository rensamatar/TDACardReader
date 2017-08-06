package com.tdacardreader.modules;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

import rd.TDA.TDA;

/**
 * Created by Tar on 8/3/17.
 * company : Iwa Labs (Thailand)
 * email : tar@iwa.fi
 */

public class TDACardReaderModule extends ReactContextBaseJavaModule {

    private static final String TAG = TDACardReaderModule.class.getSimpleName();
    private static final String PROMISE_ERROR = "error";
    private static final String NO_INTERNET = "no-internet-connection";
    private static final String NO_TDA_SERVICE = "no-tda-service";
    private static final String NO_LICENSE = "no-license";
    private static final String NO_TDA_INIT = "no-tda-init";
    private static final String INVALID_LICENSE = "invalid-license";
    private static final String DOWNLOAD_LICENSE_SUCCESS = "download-license-success";
    private static final String DOWNLOAD_LICENSE_FAILED = "download-license-failed";
    private static final String NO_ID_CARD = "no-id-card";
    private static final String CARD_READER_NOT_FOUND = "card-reader-not-found";
    private static final String CARD_READER_NOT_RESPONSE = "card-reader-not-response";
    private static final String READING_CARD = "reading-card";
    private static final String EXCEPTION = "exception-error";

    private TDA tda;
    private boolean isOnReadingCard = false;

    public TDACardReaderModule(ReactApplicationContext reactContext) {
        super(reactContext);
        // create new TDA...
        tda = new TDA(reactContext);
        Log.d(TAG, "Init TDA library");
    }

    @Override
    public String getName() {
        return "TDACardReader";
    }

    // Test
    @ReactMethod
    public void show(String message, int duration) {
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    // Expose method from TDA Library

    @ReactMethod
    public void initLicense(Promise promise) {

        isOnReadingCard = false;

        try {

            if (!Utils.isOnline(getReactApplicationContext())) {
                promise.reject(NO_INTERNET, "No internet connection");
            }

            if (!tda.isPackageInstalled(getReactApplicationContext())) {
                promise.reject(NO_TDA_SERVICE, "TDA service not install on device");
                return;
            }

            if (tda == null) {
                promise.reject(NO_TDA_INIT, "TDA Library not exist. Initial the library..");
                tda = new TDA(getReactApplicationContext());
            }

            // Close previous service if exist.
            tda.serviceTA("0");
            // Wait until service has closed.
            while (tda.serviceTA("9").compareTo("00") != 0) ;
            // Start service without fix the application name.
            tda.serviceTA("1");
            // Checking service status and wait for ready to work
            while (tda.serviceTA("9").compareTo("01") != 0) ;

            // Check license file
            String check = tda.infoTA("4");
            Log.d(TAG, "TDA service license path : " + check);

            // -2 = INVALID LICENSE
            // -12 = LICENSE FILE ERROR

            if (check.compareTo("-2") == 0) {
                promise.reject(INVALID_LICENSE, "Invalid license file");

                // Download or update license
                if (!canDownloadLicense()) {
                    promise.reject(DOWNLOAD_LICENSE_FAILED, "Failed to download license for this reader. Please contact R&D Computer System Co., Ltd");
                }
                return;
            }
            if (check.compareTo("-12") == 0) {
                promise.reject(NO_LICENSE, "License file not found or broken");

                // Download or update license
                if (!canDownloadLicense()) {
                    promise.reject(DOWNLOAD_LICENSE_FAILED, "Failed to download license for this reader. Please contact R&D Computer System Co., Ltd");
                }
                return;
            }

            // Hide all notification bar
            tda.notificationTA("0");

            // Searching bluetooth paring and finding card reader..
            String result = tda.readerTA("2");
            // 02 = Card present
            if (result.compareTo("02") == 0) {
                Toast.makeText(getReactApplicationContext(), "Prepare card reader", Toast.LENGTH_SHORT).show();
            }

            promise.resolve(DOWNLOAD_LICENSE_SUCCESS);

        } catch (Exception e) {
            promise.reject(EXCEPTION, e.getMessage());
        }
    }

    @ReactMethod
    public void readCard(Promise promise) {

        if (!tda.isPackageInstalled(getReactApplicationContext())) {
            promise.reject(NO_TDA_SERVICE, "TDA service not install on device");
            return;
        }

        if (tda == null) {
            promise.reject(PROMISE_ERROR, "TDA Library not exist. Initial the library..");
            tda = new TDA(getReactApplicationContext());
        }

        if (isOnReadingCard) {
            promise.reject(READING_CARD, "Reading card in progress...");
            return;
        }

        String result;
        isOnReadingCard = true;

        try {

            if (isHasLicense()) {
                // Reading text data from card
                result = tda.nidTextTA("0");

                // Check if un-registered card reader
                if (result.compareTo("-2") == 0) {
                    promise.reject(INVALID_LICENSE, "Invalid license file");
                    isOnReadingCard = false;
                    return;
                }
                if (result.compareTo("-3") == 0) {
                    promise.reject(CARD_READER_NOT_FOUND, "Card reader not found");
                    isOnReadingCard = false;
                    return;
                }
                if (result.compareTo("-11") == 0) {
                    promise.reject(CARD_READER_NOT_RESPONSE, "Card reader not response or not found");
                    isOnReadingCard = false;
                    return;
                }
                if (result.compareTo("-14") == 0) {
                    promise.reject(NO_TDA_SERVICE, "TDA service not start or not found");
                    isOnReadingCard = false;
                    return;
                }
                if (result.compareTo("-16") == 0) {
                    promise.reject(NO_ID_CARD, "No ID card in card reader");
                    isOnReadingCard = false;
                    return;
                }
            } else {
                if (!canDownloadLicense()) {
                    promise.reject(DOWNLOAD_LICENSE_FAILED, "Failed to download license for this reader. Please contact R&D Computer System Co., Ltd");
                    isOnReadingCard = false;
                    return;
                }
                // Read data again
                result = tda.serviceTA("0");
            }

            Log.d(TAG, "TDA result text : " + result);

            promise.resolve(result);
            isOnReadingCard = false;

        } catch (Exception e) {
            isOnReadingCard = false;
            promise.reject(EXCEPTION, e.getMessage());
        }
    }


    @ReactMethod
    public void readImage(Promise promise) {

        if (!tda.isPackageInstalled(getReactApplicationContext())) {
            promise.reject(NO_TDA_SERVICE, "TDA service not install on device");
            return;
        }

        if (tda == null) {
            promise.reject(PROMISE_ERROR, "TDA Library not exist. Initial the library..");
            tda = new TDA(getReactApplicationContext());
        }

        if (isOnReadingCard) {
            promise.reject(READING_CARD, "Reading card...");
            return;
        }

        byte[] result;
        Bitmap bitmap;
        String str;

        try {
            // Read image file
            result = tda.nidPhotoTA("0");
            //Decode Byte Array to Bitmap
            bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);

            // Set reading card
            isOnReadingCard = true;

            if (bitmap == null) {
                String error = "";
                str = Utils.bytesToHex(result);

                if (Arrays.equals(result, new byte[]{0x45, (byte) 0xFF})) {
                    error = str + "(Error-1)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xFE})) {
                    error = str + "(Error-2)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xFD})) {
                    error = str + "(Error-3)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xFC})) {
                    error = str + "(Error-4)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xFB})) {
                    error = str + "(Error-5)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xFA})) {
                    error = str + "(Error-6)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF9})) {
                    error = str + "(Error-7)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF8})) {
                    error = str + "(Error-8)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF7})) {
                    error = str + "(Error-9)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF6})) {
                    error = str + "(Error-10)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF5})) {
                    error = str + "(Error-11)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF4})) {
                    error = str + "(Error-12)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF3})) {
                    error = str + "(Error-13)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF2})) {
                    error = str + "(Error-14)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF1})) {
                    error = str + "(Error-15)";
                } else if (Arrays.equals(result, new byte[]{0x45, (byte) 0xF0})) {
                    error = str + "(Error-16)";
                }
                promise.reject(EXCEPTION, error);
                isOnReadingCard = false;
                return;
            }

            String base64Data = Base64.encodeToString(result, Base64.NO_WRAP);
            promise.resolve(base64Data);
            isOnReadingCard = false;

        } catch (Exception e) {
            isOnReadingCard = false;
            promise.reject(EXCEPTION, e.getMessage());
        }
    }

    private boolean isHasLicense() {

        if (tda == null) {
            tda = new TDA(getReactApplicationContext());
        }

        // Check license file
        String check = tda.infoTA("4");
        if (check.compareTo("-2") == 0 || check.compareTo("-12") == 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean canDownloadLicense() {

        if (!Utils.isOnline(getReactApplicationContext())) {
            Toast.makeText(getReactApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return false;
        }

        if (tda == null) {
            tda = new TDA(getReactApplicationContext());
        }

        String downloadResult = tda.serviceTA("2");
        if (downloadResult.compareTo("02") == 0) {
            return true;
        } else {
            return false;
        }
    }

    @ReactMethod
    public void exit(Promise promise) {

        isOnReadingCard = false;

        try {
            tda.serviceTA("0");
            promise.resolve("Exit TDA service");
        } catch (Exception e) {
            promise.reject(EXCEPTION, e.getMessage());
        }
    }

}