package com.mtk.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gomtel.util.Global;
import com.gomtel.util.MyCircleImageView;
import com.mtk.btnotification.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import care.BaoBeiManagerActivity;
import care.LoginActivity;
import care.PersonInfoActivity;
import care.QinQingActivity;
import care.bean.BaoBeiBean;
import care.utils.BeanUtils;
import care.utils.XcmTools;

/**
 * Created by lixiang on 15-12-23.
 */
public class DeviceInfoActivity extends Activity{
    private RelativeLayout devide_manage;
    private RelativeLayout relative_num;
    private RelativeLayout exit;
    private RelativeLayout setting;
    private View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.devide_manage:
                    startActivity(new Intent(DeviceInfoActivity.this, BaoBeiManagerActivity.class));
                    break;
                case R.id.relative_num:
                    startActivity(new Intent(DeviceInfoActivity.this, QinQingActivity.class));
                    break;
                case R.id.exit:
                    TextView tv = new TextView(DeviceInfoActivity.this);
                    tv.setText(getString(R.string.exit_alarm));
                    new AlertDialog.Builder(DeviceInfoActivity.this)
                            .setTitle(getString(R.string.alert))
                            .setView(tv)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            XcmTools tools = new XcmTools(DeviceInfoActivity.this);
                                            tools.set_user_id("");
                                            tools.set_current_device_id("");
                                            finish();
                                            Intent i = new Intent();
                                            i.setClass(DeviceInfoActivity.this, LoginActivity.class);
                                            startActivity(i);
                                        }
                                    })
                            .setNegativeButton(getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {

                                        }

                                    }).show();
                    break;
                case R.id.setting:
                    startActivity(new Intent(DeviceInfoActivity.this, PersonInfoActivity.class));
                    break;
                default:
                    break;
            }
        }
    };
    private MyCircleImageView userimg;
    private TextView user_name;
    private XcmTools tools;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_info);
        initView();

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

    private void init_head() {
        String personDate[] = tools.get_person().split(",");
        imageLoader.displayImage(personDate[0], userimg, options);
        user_name.setText(personDate[1]);
    }

    private void initView() {
        tools = new XcmTools(this);
        devide_manage = (RelativeLayout)findViewById(R.id.devide_manage);
        relative_num = (RelativeLayout)findViewById(R.id.relative_num);
        exit = (RelativeLayout)findViewById(R.id.exit);
        setting = (RelativeLayout)findViewById(R.id.setting);
        devide_manage.setOnClickListener(myClickListener);
        relative_num.setOnClickListener(myClickListener);
        exit.setOnClickListener(myClickListener);
        setting.setOnClickListener(myClickListener);
        userimg = (MyCircleImageView) findViewById(R.id.userimg);
        user_name = (TextView)findViewById(R.id.user_name_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init_head();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
