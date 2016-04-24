package com.agsw.ptds.Data;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;

/**
 * Created by antwan on 4/23/2016.
 */
public class RandomTestComm implements DataCommunicator {

    DataReceiver reciever;
    Random rand;
    private final static String TAG = "TestComm";
    private int frequentStolen = 1000;
    private int frequentError = 200;
    private int frequentSWDeliveredFirst = 2;

    public RandomTestComm(DataReceiver reciever)
    {
        this.reciever = reciever;
        rand = new Random(SystemClock.currentThreadTimeMillis());
    }

    @Override
    public void onSWDelivered() {
        log("Got a message that ShipHawk recieved the message first");
        reciever.onSWDelivered();
    }

    @Override
    public void onSensorDelivered() {
        log("Got a message that the sensors found the device first");
        reciever.onSensorDelivered();
    }

    @Override
    public void disarmDevice() {
        log("Got a message to disarm the device");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (didPackageGetStolen()) onPackageStolen();
                else if (didAnErrorOccur()) onError("Mock error");
                else if (didSWArriveFirst()) onSWDelivered();
                else onSensorDelivered();
                onDisarmed();
            }
        }, rand.nextInt(8000));
    }

    private boolean didSWArriveFirst() {
        if (rand.nextInt(frequentSWDeliveredFirst) == 1)
            return true;
        return false;
    }

    @Override
    public void onDisarmed() {
        log("Sending a message - device has been disarmed");
        reciever.onDeviceDisarmed();
    }

    @Override
    public void armDevice() {
        log("Got a message to arm the device");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if (didAnErrorOccur()) onError("Mock error");
                else if (didPackageGetStolen()) onPackageStolen();
                onDeviceArmed();
            }
        }, rand.nextInt(8000));

    }

    @Override
    public void onDeviceArmed() {
        log("Sending a message - device has been armed");
        reciever.onDeviceArmed();

    }

    @Override
    public void onPackageStolen() {
        log("Sending a message - package has been stolen");
        reciever.onPackageStolen();
    }

    @Override
    public void onError(String data) {
        log("Sending a message - an error occured - " + data);
        reciever.onError(data);

    }

    private void log(String message)
    {
        Log.d(TAG, message);
    }

    private boolean didPackageGetStolen()
    {
        return (rand.nextInt(frequentStolen)%frequentStolen == 0);
    }

    private boolean didAnErrorOccur()
    {
        return (rand.nextInt(frequentError) == 0);
    }
}
