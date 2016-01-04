
package com.mtk.app.appstore;

import java.io.InputStream;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mediatek.wearable.DeviceInfo;
import com.mediatek.wearable.DeviceInfoListener;
import com.mediatek.wearable.WearableManager;

public class DownloadManager {

    private static final String TAG = "AppManager/DownloadManager";

    private static DownloadManager sInstance;

    private static final String URL = "https://iotlab.mediatek.com/smartwatch/general/1.0/apps";

    private DeviceInfo mDeviceInfo;

    private DownloadManager() {
    }

    public static synchronized DownloadManager getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadManager();
        }
        return sInstance;
    }

    // filter rule
    private String getAppStoreFilter() {
        DeviceInfoListener listener = new DeviceInfoListener() {
            @Override
            public void notifyDeviceInfo(final DeviceInfo deviceInfo) {
                mDeviceInfo = deviceInfo;
            }
        };
        WearableManager.getInstance().getDeviceInfo(listener);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO: handle exception
        }

        String queryString = "?";
        if (mDeviceInfo == null) {
            return queryString;
        }
        int lcdWidth = mDeviceInfo.getLcdWidth();
        int lcdLength = mDeviceInfo.getLcdHeight();
        String model = mDeviceInfo.getModel();
        int maxMemory = mDeviceInfo.getMaxMemory();

        boolean isFilter = false;
        if (lcdWidth > 0) {
            queryString += "lcdwidth=" + lcdWidth;
            isFilter = true;
        }
        if (lcdLength > 0) {
            queryString += (isFilter ? "&" : "");
            queryString += "lcdlength=" + lcdLength;
            isFilter = true;
        }
        if (!TextUtils.isEmpty(model)) {
            queryString += (isFilter ? "&" : "");
            queryString += "model=" + model;
            isFilter = true;
        }
        if (maxMemory > 0) {
            queryString += (isFilter ? "&" : "");
            queryString += "maxMemory=" + maxMemory;
            isFilter = true;
        }
        Log.d(TAG, "[getAppStoreFilter] queryString = " + queryString);
        return queryString;
    }

    private String getFilterURL() {
        return URL/* + getAppStoreFilter() */;
    }

    // refreshAppList
    public InputStream getAppListData() {
        String url = getFilterURL();
        InputStream inputStream = HttpHelper.getInputStreamFromURL(url);
        return inputStream;
    }
}
