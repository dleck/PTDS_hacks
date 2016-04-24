package com.agsw.ptds.Data;

/**
 * Created by antwan on 4/23/2016.
 */
public interface DataReceiver {
    void onDeviceDisarmed();
    void onDeviceArmed();
    void onPackageStolen();
    void onSensorDelivered();
    void onSWDelivered();
    void onError(String data);
    void displayLog(String message);
}
