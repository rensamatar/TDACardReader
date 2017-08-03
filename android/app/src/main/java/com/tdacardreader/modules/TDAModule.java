package com.tdacardreader.modules;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import rd.TDA.TDA;

public class TDAModule extends ReactContextBaseJavaModule {

    private static final String TAG = TDAModule.class.getSimpleName();

    private TDA tda;
    private Callback successCallback;
    private Callback errorCallback;

    public TDAModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "TDA";
    }

    // Test
    @ReactMethod
    public void show(String message, int duration) {
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    // Expose method from TDA Library
    @ReactMethod
    public void initLicense(ReadableMap config, Callback successCallback, Callback errorCallback) {

        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            this.errorCallback.invoke("Activity doesn't exist");
            return;
        }

        try {
            Log.d(TAG, "init license");
            tda = new TDA(currentActivity);
            this.successCallback.invoke("Init license success");
        } catch (Exception e) {
            this.errorCallback.invoke(e);
        }
    }

    @ReactMethod
    public void exit() {
        Log.d(TAG, "exit TDA service");
        tda.serviceTA("0");
    }

    @ReactMethod
    public void startProcess(ReadableMap config, Callback successCallback, Callback errorCallback) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        try {
            tda.serviceTA("0");                                             //Close previous service if exist
            while (tda.serviceTA("9").compareTo("00") != 0);                //Wait until service closed
            tda.serviceTA("1, TDA");                                        //Start TDAService with "TDA"
            while (tda.serviceTA("9").compareTo("01") != 0);                //Wait until service started

            // Check license file
            String check = tda.infoTA("4");                                 //Test Command
            Log.i("Check", "check = " + check);                             //Print Log

            this.successCallback.invoke(check);

            // -2 = INVALID LICENSE
            // -12 = LICENSE FILE ERROR
            if (check.compareTo("-2") == 0 || check.compareTo("-12") == 0) {
                if (Utils.isOnline(getReactApplicationContext())) {         //Method Check Internet
                    tda.serviceTA("2");                                     //Update license file
                }
            }
        } catch (Exception e) {
            this.errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void searchBluetooth() {
        String result = tda.readerTA("2");                              //Auto scan Bluetooth reader
        if (result.compareTo("02") == 0) {                              //Check Result //02 = Card Present
            Toast.makeText(getReactApplicationContext(), "Search Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @ReactMethod
    public void readText(ReadableMap config, Callback successCallback, Callback errorCallback) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        String result = null;
        try {
            result = tda.nidTextTA("0");                    //ReadText
            if (result.compareTo("-2") == 0) {              //Check if un-registered reader
                tda.serviceTA("2");                         //Update license file
                result = tda.nidTextTA("0");                //Read Text Again
                this.successCallback.invoke(result);
            }
        } catch (Exception e) {
            this.errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void readImage(ReadableMap config, Callback successCallback, Callback errorCallback) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        byte[] photo;
        Bitmap bitmap;

        try {
            photo = tda.nidPhotoTA("0");                                        //Read Photo
            bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);     // Decode Byte Array to Bitmap
            this.successCallback.invoke(bitmap);
        } catch (Exception e) {
            this.errorCallback.invoke(e.getMessage());
        }
    }


}