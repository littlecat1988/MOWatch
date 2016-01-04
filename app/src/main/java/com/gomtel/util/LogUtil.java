package com.gomtel.util;

import android.util.Log;

/**
 * Created by lixiang on 15-10-12.
 */
public class LogUtil {

    private static final String TAG = "WATCH";
    private final static boolean debug = true;

    public static void e(String Tag,String content){
        if(debug) {
            Log.e(Tag, content);
        }
    }
}
