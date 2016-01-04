/**
 * 
 */

package com.mtk.app.notification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.notification.NotificationData;
import com.mtk.main.MainService;


/**
 * This class will receive and process all notifications.
 */
public class NotificationReceiver extends AccessibilityService {
    // Debugging
    private static final String TAG = "AppManager/Noti/Receiver";

    // Avoid propagating events to the client too frequently
    private static final long EVENT_NOTIFICATION_TIMEOUT_MILLIS = 0L;

    // Received event
    private AccessibilityEvent mAccessibilityEvent = null;
    private NotificationDataManager notificationDataManager = null;

    public NotificationReceiver() {
        notificationDataManager = new NotificationDataManager(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Only concern TYPE_NOTIFICATION_STATE_CHANGED
        Log.i(TAG, "onAccessibilityEvent(), eventType=" + event.getEventType());
        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return;
        }

        Log.d(TAG, "sdk version is " + android.os.Build.VERSION.SDK_INT);
        if(android.os.Build.VERSION.SDK_INT  >= 18){
            Log.i(TAG,"Android platform version is higher than 18.");
            return;
        }

        // If notification is null, will not forward it
        mAccessibilityEvent = event;
        Notification notification = (Notification) mAccessibilityEvent.getParcelableData();
        if (notification == null) {
            return;
        }

        NotificationData notificationData = notificationDataManager.getNotificationData(
                notification, event.getPackageName().toString(),"",NotificationController.genMessageId());
        notificationDataManager.sendNotificationData(notificationData);

    }

    @Override
    public void onServiceConnected() {
        Log.i(TAG, "onServiceConnected()");

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 14) {
            setAccessibilityServiceInfo();
        }

        MainService.setNotificationReceiver(this);
    }

    private void setAccessibilityServiceInfo() {
        Log.i(TAG, "setAccessibilityServiceInfo()");

        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = EVENT_NOTIFICATION_TIMEOUT_MILLIS;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind()");
        MainService.clearNotificationReceiver();

        return false;
    }

}
