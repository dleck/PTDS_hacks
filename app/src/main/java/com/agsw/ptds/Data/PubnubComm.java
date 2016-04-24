package com.agsw.ptds.Data;
import android.util.Log;

import com.pubnub.api.*;

/**
 * Created by antwan on 4/23/2016.
 */
public class PubnubComm implements DataCommunicator {

    private final static String CHANNEL_NAME = "PTDS_channel";

    private final static String ARM_DEVICE = "app:edwinArm";
    private final static String DISARM_DEVICE = "app:edwinDisarm";

    private final static String DEVICE_DISARMED = "edwin:disarmed";
    private final static String DEVICE_ARMED = "edwin:armed";
    private final static String DEVICE_STOLEN = "edwin:stolen";
    private final static String DEVICE_DELIVERED = "edwin:delivered";
    private final static String TRUNGPC_DELIVERED = "computer:delivered";

    DataReceiver receiver;
    Pubnub nub;
    Callback postCBack;

    public PubnubComm(DataReceiver rec) throws PubnubException {
        receiver = rec;
        log("Starting to connect...");
        nub = new Pubnub("pub-c-c4775583-2521-4e1f-b853-cdb74ae25cb3",
                         "sub-c-9dbe6968-0995-11e6-8c3e-0619f8945a4f");
        log("Client credentials setup, attempting to subscribe...");
        nub.subscribe(CHANNEL_NAME, new Callback() {

            @Override
            public void connectCallback(String channel, Object message) {
                Log.w("Pubnub", "SUBSCRIBE : CONNECT on channel:" + channel
                        + " : " + message.getClass() + " : "
                        + message.toString());
            }

            @Override
            public void disconnectCallback(String channel, Object message) {
                Log.w("Pubnub", "SUBSCRIBE : DISCONNECT on channel:" + channel
                        + " : " + message.getClass() + " : "
                        + message.toString());
                onError("Lost connection to the device!");
            }

            public void reconnectCallback(String channel, Object message) {
                Log.w("Pubnub", "SUBSCRIBE : RECONNECT on channel:" + channel
                        + " : " + message.getClass() + " : "
                        + message.toString());
            }

            @Override
            public void successCallback(String channel, Object message) {
                Log.w("Pubnub", "SUBSCRIBE : " + channel + " : "
                        + message.getClass() + " : " + message.toString());
                log("Recieved: " + new String(message.toString()));
                // handle messages
                switch ((String) message)
                {
                    case DEVICE_DISARMED: onDisarmed();
                        break;
                    case DEVICE_ARMED: onDeviceArmed();
                        break;
                    case DEVICE_STOLEN: onPackageStolen();
                        break;
                    case DEVICE_DELIVERED: onSensorDelivered();
                        break;
                    case TRUNGPC_DELIVERED: onSWDelivered();
                        break;
                    default: break;
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.w("Pubnub", "SUBSCRIBE : ERROR on channel " + channel
                        + " : " + error.toString());
                onError(error.getErrorString());
            }
        });
        log("Subscription request sent, waiting for callbacks...");

        postCBack = new Callback() {
            /**
             * This callback will be invoked when a message is received on the channel
             *
             * @param channel Channel Name
             * @param message
             */
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                log("Command sent - " + message.toString());
            }

            /**
             * This callback will be invoked when an error occurs
             *
             * @param channel Channel Name
             * @param error
             */
            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                log("Command not sent - " + error.getErrorString());
                onError(error.getErrorString());
            }
        };
    }

    @Override
    public void onSWDelivered() {

    }

    @Override
    public void onSensorDelivered() {

    }

    private void pushUpstream(String channel, String data)
    {
        log("Pushing to the channel - "+ channel + " -- " + data);
        nub.publish(channel, data, postCBack);
    }

    @Override
    public void disarmDevice() {
        pushUpstream(CHANNEL_NAME, DISARM_DEVICE);
    }

    @Override
    public void onDisarmed() {
        receiver.onDeviceDisarmed();
    }

    @Override
    public void armDevice() {
        pushUpstream(CHANNEL_NAME, ARM_DEVICE);
    }

    @Override
    public void onDeviceArmed() {
        receiver.onDeviceArmed();
    }

    @Override
    public void onPackageStolen() {
        receiver.onPackageStolen();
    }

    @Override
    public void onError(String data)
    {
        log("Error: " + data);
        receiver.onError(data);
    }

    private void log(String message)
    {
        Log.w("PubNub Comm", message);
        receiver.displayLog(message);
    }
}
