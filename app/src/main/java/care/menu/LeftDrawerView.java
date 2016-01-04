package care.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.mtk.btnotification.R;
import care.widget.WiperSwitch.OnChangedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import care.ActivityBabyControl;
import care.BaoBeiInfoActivity;
import care.BaoBeiManagerActivity;
import care.FeedBackActivity;
import care.FenceActivity;
import care.LoginActivity;
import care.Mess_Attention_Activity;
import care.PersonInfoActivity;
import care.QinQingActivity;
import care.RemainActivity;
import care.SettingActivity;
import care.SingleChatActivity;
import care.bean.BaoBeiBean;
import care.utils.BeanUtils;
import care.utils.Constants;
import care.utils.XcmTools;
import care.widget.CircularImage;
import care.widget.WiperSwitch;

public class LeftDrawerView implements OnClickListener, OnChangedListener {

    private final Activity activity;
    private SlidingMenu localSlidingMenu;

    private CircularImage user_head_id;
    private CircularImage device_head_id;

    private TextView user_name_id;
    private TextView device_name_id;
    private WiperSwitch switch_id;
    private ViewGroup bb_id, qingqing_id, yuancheng_id, version_id, fankui_id,
            about_id, message_id, exit_id, setting_id, fence_id, speak_id, remain_id;

    private BaoBeiBean currentBaby;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected DisplayImageOptions options;
    private XcmTools tools;

    public LeftDrawerView(Activity activity) {
        this.activity = activity;
    }

    private void setCacheImage() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.head_baby1)
                .showImageOnFail(R.drawable.head_baby1)
                .showStubImage(R.drawable.head_baby1)
                .resetViewBeforeLoading(true).cacheOnDisc(true)
                .cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20)).build();
    }
    //初始化侧边栏
    public SlidingMenu initSlidingMenu() {
        localSlidingMenu = new SlidingMenu(activity);
        localSlidingMenu.setSlidingEnabled(false);
        localSlidingMenu.setMode(SlidingMenu.LEFT);//左边划出
        localSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//设置触摸的屏幕区域
        localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        localSlidingMenu.setShadowDrawable(R.drawable.shadow);
        localSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        localSlidingMenu.setFadeDegree(0.35F);
        localSlidingMenu.attachToActivity(activity, SlidingMenu.RIGHT);
        localSlidingMenu.setMenu(R.layout.left_main_menu);

        initView();
        intitClickListener();
        return localSlidingMenu;
    }

    private void intitClickListener() {
        // TODO Auto-generated method stub
        user_head_id.setOnClickListener(this);
        device_head_id.setOnClickListener(this);
        bb_id.setOnClickListener(this);
        qingqing_id.setOnClickListener(this);
        yuancheng_id.setOnClickListener(this);
        version_id.setOnClickListener(this);
        fankui_id.setOnClickListener(this);
        about_id.setOnClickListener(this);
        setting_id.setOnClickListener(this);
        message_id.setOnClickListener(this);
        switch_id.setOnChangedListener(this);
        exit_id.setOnClickListener(this);
        fence_id.setOnClickListener(this);
        speak_id.setOnClickListener(this);
        remain_id.setOnClickListener(this);
    }

    private void initView() {
        tools = new XcmTools(activity);
        // TODO Auto-generated method stub
        user_name_id = (TextView) localSlidingMenu
                .findViewById(R.id.user_name_id);
        device_name_id = (TextView) localSlidingMenu
                .findViewById(R.id.device_name_id);
        user_head_id = (CircularImage) localSlidingMenu
                .findViewById(R.id.user_head_id);
        device_head_id = (CircularImage) localSlidingMenu
                .findViewById(R.id.device_head_id);
        bb_id = (ViewGroup) localSlidingMenu.findViewById(R.id.bb_id);
        qingqing_id = (ViewGroup) localSlidingMenu
                .findViewById(R.id.qingqing_id);
        yuancheng_id = (ViewGroup) localSlidingMenu
                .findViewById(R.id.yuancheng_id);
        version_id = (ViewGroup) localSlidingMenu.findViewById(R.id.version_id);
        fankui_id = (ViewGroup) localSlidingMenu.findViewById(R.id.fankui_id);
        about_id = (ViewGroup) localSlidingMenu.findViewById(R.id.about_id);
        setting_id = (ViewGroup) localSlidingMenu.findViewById(R.id.setting_id);
        fence_id = (ViewGroup) localSlidingMenu.findViewById(R.id.fence_id);
        speak_id = (ViewGroup) localSlidingMenu.findViewById(R.id.speak_id);
        message_id = (ViewGroup) localSlidingMenu.findViewById(R.id.message_id);
        switch_id = (WiperSwitch) localSlidingMenu.findViewById(R.id.switch_id);
        exit_id = (ViewGroup) localSlidingMenu.findViewById(R.id.exit_id);
        remain_id = (ViewGroup) localSlidingMenu.findViewById(R.id.remain_id);
        init_head();
    }

    public void init_head() {
        String personDate[] = tools.get_person().split(",");
        imageLoader.displayImage(personDate[0], user_head_id, options);
        user_name_id.setText(personDate[1]);
        try {
            String babyList = tools.get_babyList();
            JSONArray babyArray = new JSONArray(babyList);
            int length = babyArray.length();
            String currentId = tools.get_current_device_id();
            for (int i = 0; i < length; i++) {
                JSONObject babyObject = (JSONObject) babyArray.get(i);
                HashMap<String, String> babyMap = BeanUtils
                        .getJSONParserResult(babyObject.toString());
                BaoBeiBean baobei = BeanUtils.getBaoBei(babyMap);
                if (baobei.getImei().equals(currentId)) {
                    currentBaby = baobei;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (currentBaby != null) {
            device_name_id.setText(currentBaby.getName());

            System.out.println("url + " + currentBaby.getPhoto());
            
            imageLoader.displayImage(currentBaby.getPhoto(), device_head_id,
                    options);
        }
    }
    @Override
    public void onClick(View v) {
        Intent dataIntent = new Intent();
        switch (v.getId()) {
            case R.id.bb_id:
                dataIntent.setClass(activity, BaoBeiManagerActivity.class);
                activity.startActivity(dataIntent);
                break;
            case R.id.qingqing_id:
                dataIntent.setClass(activity, QinQingActivity.class);
                activity.startActivity(dataIntent);
                break;
            case R.id.yuancheng_id: // Զ�̼���
                // showToast(R.string.no_function);
                Intent i = new Intent(activity, ActivityBabyControl.class);
                activity.startActivity(i);
                break;
            case R.id.version_id: // �汾��
                showToast(R.string.no_function);
                break;
            case R.id.fankui_id: // �����
                dataIntent.setClass(activity, FeedBackActivity.class);
                activity.startActivity(dataIntent);
                break;

            case R.id.setting_id: //
                // showToast(R.string.no_function);
                Intent settingId = new Intent();
                settingId.setClass(activity, SettingActivity.class);
                activity.startActivity(settingId);
                break;
            case R.id.fence_id:
                Intent fenceId = new Intent();
                fenceId.setClass(activity, FenceActivity.class);
                activity.startActivity(fenceId);
                break;
            case R.id.speak_id:
                Intent speakId = new Intent();
                speakId.setClass(activity, SingleChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "1");
                bundle.putString("chatWinName", Constants.USERNICKNAME);
                speakId.putExtras(bundle);
                activity.startActivity(speakId);
                break;
            case R.id.message_id:
//			showToast(R.string.no_function);
                Intent i_msg = new Intent();
                i_msg.setClass(activity, Mess_Attention_Activity.class);
                activity.startActivity(i_msg);
                break;

            case R.id.user_head_id:
                dataIntent.setClass(activity, PersonInfoActivity.class);
                activity.startActivity(dataIntent);
                break;
            case R.id.device_head_id:
                dataIntent.setClass(activity, BaoBeiInfoActivity.class);
                activity.startActivity(dataIntent);
                break;
            case R.id.remain_id:
                dataIntent.setClass(activity, RemainActivity.class);
                activity.startActivity(dataIntent);
                break;
            case R.id.exit_id:
                TextView view = new TextView(activity);
                view.setText(activity.getString(R.string.exit_alarm));
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.alert))
                        .setView(view)
                        .setPositiveButton(activity.getString(R.string.ok),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        XcmTools tools = new XcmTools(activity);
                                        tools.set_user_id("");
                                        tools.set_current_device_id("");
                                        activity.finish();
                                        Intent i = new Intent();
                                        i.setClass(activity, LoginActivity.class);
                                        activity.startActivity(i);
                                    }
                                })
                        .setNegativeButton(activity.getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {

                                    }

                                }).show();
                break;
        }

    }

    private void showToast(int resId) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
        // TODO Auto-generated method stub
        switch (wiperSwitch.getId()) {
            case R.id.switch_id:
                switch_id.setChecked(checkState);
                break;
        }
    }

    public void setBlutSwitch(boolean switch_blue) {
        //TODO Auto-generated method stub
        switch_id.setChecked(switch_blue);
    }

    public void init_person_head() {
        String personDate[] = tools.get_person().split(",");
        imageLoader.displayImage(personDate[0], user_head_id, options);
        user_name_id.setText(personDate[1]);
    }
}
