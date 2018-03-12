package com.aau.thesis.lockscreenapplication.helper;

/**
 * Created by mette on 12/03/2018.
 */

public abstract class Utilities {


    //Method for shutting down application.
    public static void shutDownApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
