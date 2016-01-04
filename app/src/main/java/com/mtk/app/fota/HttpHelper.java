package com.mtk.app.fota;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

public class HttpHelper {

    private static final String TAG = "[FOTA_UPDATE][HttpHelper]";
    
    public static final String JSON_URL_STRING = "url";
    public static final String JSON_VERSION_STRING = "targetVersion";
    public static final String JSON_RELEASE_DATE_STRING = "releaseDate";
    
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 60 * 1000;
    
    public static final int URL_CONNECT_TIMEOUT = 1;
    
    public interface IExceptionHandler {
        void onNetworkError();
        void onDataError();
    }

    public static InputStream getInputStreamFromURL(String urlStr, IExceptionHandler callback, Handler handler) {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(READ_TIMEOUT);
            // conn.setRequestProperty("User-agent",
            Log.d(TAG, "[getInputStreamFromURL] conn.connect begin -- " + urlStr);
            sendConnectTimeout(handler, conn);
            conn.connect();
            removeTimerMessage(handler);
            Log.d(TAG, "[getInputStreamFromURL] conn.connect end");
            // keep-alive: default on

            Log.d(TAG, "[getInputStreamFromURL] conn.getInputStream begin");
            try {
                inputStream = conn.getInputStream();
            } catch (IOException e) {
                Log.d(TAG, "[getInputStreamFromURL] getInputStream IOException : " + e.getMessage());
                e.printStackTrace();
                callback.onNetworkError();
                return null;
            }
            inputStream = new BufferedInputStream(inputStream);
            Log.d(TAG, "[getInputStreamFromURL] conn.getInputStream end");
        } catch (MalformedURLException e) {
            Log.d(TAG, "[getInputStreamFromURL] MalformedURLException : " + e.getMessage());
            callback.onDataError();
            return null;
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "[getInputStreamFromURL] SocketTimeoutException : " + e.getMessage());
            callback.onNetworkError();
            return null;
        } catch (ProtocolException e) {
            Log.d(TAG, "[getInputStreamFromURL] ProtocolException : " + e.getMessage());
            callback.onNetworkError();
            return null;
        } catch (IOException e) {
            Log.d(TAG, "[getInputStreamFromURL] IOException : " + e.getMessage());
            removeTimerMessage(handler);
            callback.onNetworkError();
            return null;
        }
        return inputStream;
    }
    
    private static void sendConnectTimeout(Handler handler, HttpURLConnection conn) {
        if (conn == null) {
            return;
        }
        Message msg = handler.obtainMessage();
        msg.what = URL_CONNECT_TIMEOUT;
        msg.obj = conn;
        handler.sendMessageDelayed(msg, READ_TIMEOUT);
    }
    
    private static void removeTimerMessage(Handler handler) {
        Log.d(TAG, "[removeTimerMessage] remove timer message");
        handler.removeMessages(URL_CONNECT_TIMEOUT);
    }
    
    public static HashMap<String, String> getJSonFromInputStream(InputStream in) {
        if (in == null) {
            Log.e(TAG, "[getJSonFromUrl] in is null");
            return null;
        }

        JsonReader reader = new JsonReader(new InputStreamReader(in));
        HashMap<String, String> returnMap = new HashMap<String, String>();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                String str = reader.nextString();
                Log.d(TAG, "[getJSonFromInputStream] name : " + name);
                Log.d(TAG, "[getJSonFromInputStream] str : " + str);
                if (name.equals(JSON_URL_STRING)) {
                    returnMap.put(JSON_URL_STRING, str);
                } else if (name.equals(JSON_VERSION_STRING)) {
                    returnMap.put(JSON_VERSION_STRING, str);
                } else if (name.equals(JSON_RELEASE_DATE_STRING)) {
                    returnMap.put(JSON_RELEASE_DATE_STRING, str);
                } else {
                    Log.e(TAG, "[getJSonFromInputStream] unrecoginized name");
                    return null;
                }
            }
            reader.endObject();
            return returnMap;
        } catch (IOException e) {
            Log.e(TAG, "[getJSonFromInputStream] ioexception 1");
            e.printStackTrace();
            return returnMap;
        } finally {
            try {
                in.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "[getJSonFromInputStream] ioexception 2");
                return returnMap;
            }
        }
    }
}
