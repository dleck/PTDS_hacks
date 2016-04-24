package com.agsw.ptds.Data;

/**
 * Created by antwan on 4/23/2016.
 */
public interface DataCommunicator {
    void onSWDelivered();

    void onSensorDelivered();

    void disarmDevice();
    void onDisarmed();
    void armDevice();
    void onDeviceArmed();
    void onPackageStolen();
    void onError(String data);

}
