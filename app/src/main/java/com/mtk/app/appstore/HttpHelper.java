
package com.mtk.app.appstore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import com.mtk.app.appstore.RemoteAppInfo.AppInfoListener;
import com.mtk.main.BTNotificationApplication;

import android.util.Log;
import android.widget.Toast;

public class HttpHelper {

    private static final String TAG = "AppManager/HttpHelper";

    public static InputStream getInputStreamFromURL(String urlStr) {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("User-agent",
            // "Mozilla/5.0 (Linux; Android 4.2.1; Nexus 7 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
            Log.d(TAG, "[getInputStreamFromURL] conn.connect begin -- " + urlStr);
            conn.connect();
            Log.d(TAG, "[getInputStreamFromURL] conn.connect end");
            // keep-alive: default on

            // conkies session
            // String session_value = conn.getHeaderField("Set-Cookie");
            // session_value = conn.getHeaderField("Cookie");
            // session_value = conn.getHeaderField("Content-Type");
            // session_value = conn.getHeaderField("Date");
            // if (session_value != null) {
            // String[] sessionId = session_value.split(";");
            // Log.d(TAG, "sessionId " + sessionId);
            // conn.setRequestProperty("Cookie", sessionId[0]);
            // }

            // String location = urlConn.getHeaderField("Location");
            Log.d(TAG, "[getInputStreamFromURL] conn.getInputStream begin");
            inputStream = conn.getInputStream();
            inputStream = new BufferedInputStream(inputStream);
            Log.d(TAG, "[getInputStreamFromURL] conn.getInputStream end");
        } catch (MalformedURLException e) {
            Log.d(TAG, "[getInputStreamFromURL] MalformedURLException : " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "[getInputStreamFromURL] IOException : " + e.getMessage());
            if (e.getMessage().contains("ExtCertPathValidatorException")) {
                HashSet<AppInfoListener> listeners = RemoteAppInfo.getAppInfoListener();
                for (AppInfoListener listener : listeners) {
                    listener.onDownloadError("Could not validate certificate");
                }
                
            }
        }
        return inputStream;
    }
}
