package com.agsw.ptds.Controller;

/**
 * Created by antwan on 4/23/2016.
 */
public interface MainInterface {
    boolean getArmedStatus();
    void disarmSystem();
    void armSystem();
    void userRequestedAction();
    void displayLog(String msg);
}
