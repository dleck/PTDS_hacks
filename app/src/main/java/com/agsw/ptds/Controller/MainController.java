package com.agsw.ptds.Controller;

import android.graphics.Color;
import android.os.SystemClock;

import com.agsw.ptds.Data.DataCommunicator;
import com.agsw.ptds.Data.DataReceiver;
import com.agsw.ptds.Data.PubnubComm;
import com.agsw.ptds.Data.RandomTestComm;
import com.agsw.ptds.R;
import com.agsw.ptds.View.MainActivity;

import java.util.Random;

/**
 * Created by antwan on 4/23/2016.
 */
public class MainController implements MainInterface, DataReceiver
{
    boolean inComm = false;
    boolean isArmed = true;
    public static boolean debugMode = false;
    MainActivity view;
    DataCommunicator dataComm;
    Random rand;

    // Strings
    private final static String STATUS_SYSDIS = "PackageGuard is Off!";
    private final static String STATUS_SYSEN= "PackageGuard is On!";
    private final static String ACTION_SYSDIS= "Turn on";
    private final static String ACTION_SYSEN= "Turn off";

    // colors
    private final static int COLOR_SYSDIS = Color.parseColor("#c62828");
    private final static int COLOR_WAITING = Color.parseColor("#0277BD");
    private final static int COLOR_SYSEN = Color.parseColor("#2e7d32");

    // loadingMessages
    String[] loadingMessages = {"The pigeons are carrying the message. Please wait.",
                                "A few bits escaped but we got them, One moment please.",
                                "The server is powered by a lemon, Please wait.",
                                "Doing some math, please wait",
                                "We're testing your patience. One moment please.",
                                "The bits are flowing slowly today, Please wait.",
                                "You don't have anything better to do, just wait.",
                                "Locating the required gigapixels to render...",
                                "Spinning up the hamster...",
                                "Warming up Large Hadron Collider...",
                                "Monkeys encrypting your data, please wait.",
                                "Shovelling coal into the server...",
                                "HAN SOLO SHOT FIRST!",
                                "Time is an illusion. Loading time more so.",
                                "Hang on a sec, I know your data is here somewhere",
                                "Calling Al Gore for Internet Help, be patient.",
                                "Are your shoelaces untied?",
                                "Magic Elves are processing your request. Please wait."};
    private boolean physicallyDelivered = false;

    public MainController(MainActivity activity)
    {
        view = activity;
        view.makeToast("Debug Mode");
        // init a data comm object
        try {
            dataComm = new PubnubComm(this);
        } catch (Exception e) {
            // print error message if we get any
            e.printStackTrace();
            onError(e.getMessage());
        }
        rand = new Random(SystemClock.currentThreadTimeMillis());
    }

    @Override
    public boolean getArmedStatus() {
        return isArmed;
    }

    @Override
    public void disarmSystem() {
        dataComm.disarmDevice();
    }

    @Override
    public void armSystem() {
        dataComm.armDevice();
    }

    private void updateCommunicatingUI()
    {
        // Update the UI
        view.setStatusText(STATUS_SYSEN);
        view.setStatusColor(COLOR_SYSEN);
        view.setActionText(ACTION_SYSEN);
        view.updateStatusIcon(R.mipmap.ic_unarmed);
    }



    @Override
    public void userRequestedAction()
    {
        // indicate its going to start a comm mission/disable
        inComm = true;
        view.disableAction();

        // switch to loading ui
        view.setActionText("Loading...");
        view.setStatusText(loadingMessages[rand.nextInt(loadingMessages.length)]);
        view.setStatusColor(COLOR_WAITING);
        view.loadingAnimation();

        // switch the status of the system
        if (isArmed) disarmSystem();
        else armSystem();
        isArmed = !isArmed;

        // notify PubNub
    }

    @Override
    public void displayLog(String message) {
        if (debugMode)
            view.makeToast(message);
    }

    @Override
    public void onDeviceDisarmed() {
        inComm = false;
        view.enableAction();

        // Update the UI
        view.stopLoadingAnimation();
        view.setStatusText(STATUS_SYSDIS);
        view.setStatusColor(COLOR_SYSDIS);
        view.setActionText(ACTION_SYSDIS);
        view.updateStatusIcon(R.mipmap.ic_unarmed);
      //  view.showNotification("PackageGuard change!", "PackageGuard is not protecting your package " +
        //        "anymore. Open the app and turn it on as soon as possible.", R.mipmap.ic_launcher);
        isArmed = false;
    }

    @Override
    public void onDeviceArmed() {
        inComm = false;

        // Update the UI
        view.stopLoadingAnimation();
        view.enableAction();
        view.setStatusText(STATUS_SYSEN);
        view.setStatusColor(COLOR_SYSEN);
        view.setActionText(ACTION_SYSEN);
        view.updateStatusIcon(R.mipmap.ic_checkmark);
    //    view.showNotification("PackageGuard change!", "PackageGuard has been armed! Your packages are protected.", R.mipmap.ic_launcher);
        isArmed = true;
    }

    @Override
    public void onPackageStolen() {
        view.stopLoadingAnimation();
        view.showNotification("Package Stolen!",
                "PackageGuard has detected that one of your packages may have gotten stolen",
                R.mipmap.ic_launcher, true);

    }

    @Override
    public void onSensorDelivered() {
        view.showNotification("PackageGuard", "Your package has been detected by our system.", R.mipmap.ic_launcher, false);
        physicallyDelivered = true;
    }

    @Override
    public void onSWDelivered() {
        if (physicallyDelivered == false)
        view.showNotification("PackageGuard Warning", "Your package has been marked as delivered " +
                "but our system can't detect it. It may have been delivered to the wrong address," +
                " or your mailman is bad at his job.", R.mipmap.ic_launcher, false);

        physicallyDelivered = false;
    }

    @Override
    public void onError(String data) {
        view.stopLoadingAnimation();
        view.showNotification("An Error has occurred!",
                data,
                R.mipmap.ic_launcher, false);
    }
}
