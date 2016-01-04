package com.mtk.bluetoothle;

import com.mediatek.leprofiles.LeServer;
import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.wearableProfiles.WearableClientProfileManager;

import java.util.ArrayList;

public class CustomizedBleFeaturesIniter {

    public static final void initBleServers() {
        ArrayList<LeServer> demoList = new ArrayList<LeServer>();
//        demoList.add(new CustomizedBleServer());
        /*
         * demoList.add(...); ...
         */
        LocalBluetoothLEManager.getInstance().addCustomizedLeServers(demoList);
    }

    public static final void initBleClients() {
        // The looper is null now. But If you need run the callback event in
        // your special thread, you can set it to your looper.
        WearableClientProfileManager.getWearableClientProfileManager()
                .registerWearableClientProfile(new CustomizedBleClient(), null);
        WearableClientProfileManager.getWearableClientProfileManager()
        .registerWearableClientProfile(new UvBleClient(), null);
        /*
         * WearableClientProfileManager.getWearableClientProfileManager()
         * .registerWearableClientProfile(..., null);
         */
    }
}
