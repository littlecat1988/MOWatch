
package com.mtk.app.remotecamera;

import com.mediatek.camera.service.MtkCameraAPService;
//import com.mediatek.camera.service.MtkCameraAPServiceListener;
import com.mediatek.camera.service.MtkCameraLocalBinder;
import com.mediatek.camera.service.RemoteCameraController;
import com.mediatek.camera.service.RemoteCameraEventListener;
import com.mtk.main.Utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class RemoteCameraService implements RemoteCameraEventListener {
    private Context mContext;

    public static final String BT_REMOTECAMERA_EXIT_ACTION = "com.mtk.RemoteCamera.EXIT";

    public static final String BT_REMOTECAMERA_CAPTURE_ACTION = "com.mtk.RemoteCamera.CAPTURE";

    private static final String TAG = "AppManager/Camera/Service";

    public static boolean inLaunchProgress = false;

    public static boolean needPreview = false;

    public static boolean isIntheProgressOfExit = false;

    public static boolean isLaunched = false;

    private MtkCameraAPService mMtkCameraAPService = null;

    private boolean mIsMtkCameraLaunched = false;

    private RemoteCameraController mController = RemoteCameraController.getInstance();

    public RemoteCameraService(Context context) {
        mContext = context;
    }

    @Override
    public void notifyRemoteCameraEvent(int eventType) {
        needPreview = false;
//        mIsCanLaunchMtkCameraAp = MtkCameraAPService.isCanLaunchMtkCameraAp(mContext);

        switch (eventType) {
            case ACTION_START_ACTIVITY:
                Log.i(TAG, "isIntheProgressOfExit: " + isIntheProgressOfExit + ", isLaunched: " + isLaunched + ", inLaunchProgress: " + inLaunchProgress);

                if (Utils.isScreenLocked(mContext)) {
                    mController.sendOnStart(false);
                } else {
                    if (!Utils.isScreenOn(mContext)) {
                        mController.sendOnStart(false);
                    } else {
                        if (isLaunched && (!isIntheProgressOfExit)) {
                            mController.sendOnStart(true);
                        } else if (isIntheProgressOfExit || inLaunchProgress) {
                            mController.sendOnStart(false);
                        } else {
                            inLaunchProgress = true;
                            Intent startServiceIntent = new Intent(mContext,
                                    MtkCameraAPService.class);
                            mContext.bindService(startServiceIntent, mCameraConnection,
                                    Context.BIND_AUTO_CREATE);
                        }
                    }
                }
                break;
            case ACTION_EXIT_ACTIVITY:
                if (isLaunched) {
                    isIntheProgressOfExit = true;
                }
                if (!mIsMtkCameraLaunched) {
                    if (mListener != null) {
                        mListener.onExitCamera();
                    }
                }
                try {
                    mContext.unbindService(mCameraConnection);
                } catch (Exception e) {
                    Log.i(TAG, "unbind service failed, e = " + e);
                }
                
                inLaunchProgress = false;
                break;
            case ACTION_TAKE_PICTURE:
                if (mIsMtkCameraLaunched) {
                    if (mMtkCameraAPService != null) {
                        mMtkCameraAPService.takePicture();
                    }
                } else {
                    if (mListener != null) {
                        mListener.onTakePicture();
                    }
                }
                break;
            case ACTION_PREVIEW:
                Log.i(TAG, "needPreview = true");
                needPreview = true;
                break;
            case ACTION_EXIT_FROM_SP:
                if (isLaunched) {
                    isIntheProgressOfExit = true;
                }
                try {
                    mContext.unbindService(mCameraConnection);
                } catch (Exception e) {
                    Log.i(TAG, "unbind service failed, e = " + e);
                }
                inLaunchProgress = false;
                break;
            default:
                break;
        }
    }

    private static CustomCameraListener mListener;

    public static void setListener(CustomCameraListener l) {
        mListener = l;
    }

    public interface CustomCameraListener {
        public void onExitCamera();

        public void onTakePicture();
    }

    private ServiceConnection mCameraConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "CameraConnection, onServiceDisconnected()");
//            mMtkCameraAPService.release();
            mMtkCameraAPService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "CameraConnection, onServiceConnected()");
            mMtkCameraAPService = ((MtkCameraLocalBinder) service).getService();
//            mMtkCameraAPService.start();
            
            mIsMtkCameraLaunched = mMtkCameraAPService.isMTKCameraLaunched();
            if (!mIsMtkCameraLaunched) {
                Intent launchIntent = new Intent();
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.setClass(mContext, RemoteCamera.class);
                mContext.startActivity(launchIntent);
            }
        }
    };
}
