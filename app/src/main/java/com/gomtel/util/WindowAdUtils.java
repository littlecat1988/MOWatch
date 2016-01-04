package com.gomtel.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mtk.btnotification.R;


/**
 * Created by lixiang on 15-10-12.
 */
public class WindowAdUtils {

    private static final String LOG_TAG = "WindowAdUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;

    public static Boolean isShown = false;

    public static void showPopupWindow(final Context context, WindowManager windowManager, String pathOfAdBottom) {
        if (isShown) {
            LogUtil.e(LOG_TAG, "return cause already shown");
            return;
        }

        isShown = true;
        LogUtil.e(LOG_TAG, "showPopupWindow");
        mContext = context.getApplicationContext();
        mWindowManager = windowManager;
//        mWindowManager = (WindowManager) mContext
//                .getSystemService(Context.WINDOW_SERVICE);

        mView = setUpView(context,pathOfAdBottom);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 类型
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags = flags;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = 200;
        params.gravity = Gravity.BOTTOM;

        mWindowManager.addView(mView, params);


    }

    private static View setUpView(final Context context,  String pathOfAdBottom) {
        View view = LayoutInflater.from(context).inflate(R.layout.ad_window,
                null);
        ImageView adView = (ImageView) view.findViewById(R.id.adView);
        ImageView delete = (ImageView) view.findViewById(R.id.delete);
        adView.setImageURI(Uri.parse(Global.AD_PATH +pathOfAdBottom));
        adView.setScaleType(ImageView.ScaleType.FIT_XY);
        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
//                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                Uri content_url = Uri.parse("http://www.baidu.com");
                intent.setData(content_url);
                context.startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });
        return view;
    }

    public static void hidePopupWindow() {
        LogUtil.e(LOG_TAG, "hide " + isShown);
        if (isShown && null != mView) {
            LogUtil.e(LOG_TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
        }

    }
}
