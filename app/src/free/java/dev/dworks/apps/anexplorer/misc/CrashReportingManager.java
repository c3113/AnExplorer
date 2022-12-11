package dev.dworks.apps.anexplorer.misc;

import android.content.Context;
import android.util.Log;

import dev.dworks.apps.anexplorer.BuildConfig;

/**
 * Created by HaKr on 23/05/16.
 */

public class CrashReportingManager {

    public static void enable(Context context, boolean enable){
        if(!enable){
            return;
        }
//        final Fabric fabric = new Fabric.Builder(context)
//                .kits(new Crashlytics())
//                .debuggable(BuildConfig.DEBUG)           // Enables Crashlytics debugger
//                .build();
//        Fabric.with(fabric);
    }

    public static void logException(Exception e) {
        logException(e, false);
    }

    public static void logException(Exception e, boolean log) {
        if(BuildConfig.DEBUG){
            e.printStackTrace();
        }
//        else if(log) {
//            Crashlytics.logException(e);
//        }
    }

    public static void log(String s) {
        Log.i("crash", s);
//        Crashlytics.log(s);
    }

    public static void log(String tag, String s) {
        Log.i(tag, s);
//        Crashlytics.log(tag+":"+s);
    }
}