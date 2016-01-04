package com.mtk.main;

//Person Information

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;
import com.gomtel.util.MyCircleImageView;
import com.gomtel.util.SportInfo;
import com.gomtel.util.UserInfo;
import com.gomtel.widget.OnWheelChangedListener;
import com.gomtel.widget.OnWheelScrollListener;
import com.gomtel.widget.WheelView;
import com.gomtel.widget.adapters.NumericWheelAdapter;
import com.mtk.bluetoothle.CustomizedBleClient;
import com.mtk.btnotification.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PIActivity extends Activity implements HttpUtils.HttpCallback {

    protected static final String TAG = "PIActivity";
    public static final int HTTP_REQUEST_SESULT_REGISTER = 0;
    public static final int HTTP_REQUEST_SESULT_LOGIN = 1;
    public static final int HTTP_REQUEST_SESULT_USERINFO = 6;
    public static final int HTTP_REQUEST_SESULT_RECORDER = 3;
    public static final int HTTP_REQUEST_SESULT_SPORTINFO = 7;
    private static final int REGISTER = 0;
    private static final int SYNC = 1;
    private static final int LOGIN = 2;
    private static final int LOGOUT = 3;
    private MyCircleImageView userimg;
    private TextView nickname;
    private EditText nick_name;
    private RadioGroup radioGroup;
    private RadioButton man;
    private RadioButton woman;
    private TextView birth;
    private TextView height;
    private TextView weight;
    private Button register;
    private Button login;
    private AlertDialog adi;
    private AlertDialog dialog_portrait;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "msg.obj= " + msg.obj);
                    updateAfterLogin(msg.obj);
                    break;
            }
        }

    };
    private OnClickListener myClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
                case R.id.birth:
                    showPopWindowBirth(birth);
                    break;
                case R.id.height:
                    showPopWindowHeight(height);
                    break;
                case R.id.weight:
                    showPopWindowWeight(weight);
                    break;
                case R.id.register:
                    if (status == REGISTER) {
                        showRegisterDialog();
                    } else {
                        syncData();
                    }
                    break;
                case R.id.login:
                    if (status == REGISTER) {
                        showLoginDialog();
                    } else {
                        status = REGISTER;
                        register.setText(R.string.register);
                        login.setText(R.string.login);
                        if (mPrefs == null)
                            mPrefs = getSharedPreferences("WATCH", 0);
                        SharedPreferences.Editor edit = mPrefs.edit();
                        edit.putInt("LOG_STATUS", status);
                        edit.commit();
                    }
                    break;
                case R.id.userimg:
                    actionClickPortrait();
                    break;
                case R.id.save:
                    saveInfo();
                    finish();
                    break;
                case R.id.text_gallrey:
                    Intent localIntent2 = new Intent("android.intent.action.PICK", null);
                    localIntent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(localIntent2, 1);
                    break;
                case R.id.text_camera:
                    Intent localIntent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                    localIntent1.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                    startActivityForResult(localIntent1, 2);
                    break;
                case R.id.text_cancle:
                    dialog_portrait.dismiss();
                    view.setVisibility(View.GONE);
                default:
                    break;
            }
        }

    };
    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
        }

        public void onScrollingFinished(WheelView wheel) {

        }
    };
    OnWheelChangedListener wheelListener_mon = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldMon, int newMon) {
            Log.e(TAG, "newValue= " + newMon);

        }
    };
    OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldYear, int newYear) {
            Log.e(TAG, "newYear= " + newYear);
        }
    };
    OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldDay, int newDay) {
            Log.e(TAG, "wheelListener_day= " + newDay);
            int newMon = month.getCurrentItem();
            if (newMon == 1) {
                if (isLeapYear(year.getCurrentItem() + 1900)) {
                    day.setViewAdapter(new NumericWheelAdapter(PIActivity.this,
                            1, 29));
                } else {
                    day.setViewAdapter(new NumericWheelAdapter(PIActivity.this,
                            1, 28));
                }
            } else if (newMon == 0 || newMon == 2 || newMon == 4 || newMon == 6
                    || newMon == 7 || newMon == 9 || newMon == 11) {
                day.setViewAdapter(new NumericWheelAdapter(PIActivity.this, 1,
                        31));
            } else {
                day.setViewAdapter(new NumericWheelAdapter(PIActivity.this, 1,
                        30));
            }
        }
    };
    private WheelView day;
    private WheelView month;
    private WheelView year;
    private Button btn_ok;
    private Button btn_cancel;
    private LayoutInflater inflater;
    private WheelView height_pop;
    private WheelView weight_pop;
    private HttpUtils httpUtils;
    private AlertDialog adi_login;
    private boolean isLoggined;
    private SharedPreferences mPrefs;
    private TextView save;
    private Bitmap head;
    private int status = 0;

    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        }
        return false;
    }

    protected void syncData() {
        // TODO Auto-generated method stub
        startActivity(new Intent(PIActivity.this, SyncDataActivity.class));
    }

    protected void actionClickPortrait() {
        // TODO Auto-generated method stub
        View localView = LayoutInflater.from(this).inflate(R.layout.view_set_portrait, null);
        ((TextView) localView.findViewById(R.id.text_gallrey)).setOnClickListener(myClickListener);
        ((TextView) localView.findViewById(R.id.text_camera)).setOnClickListener(myClickListener);
        ((TextView) localView.findViewById(R.id.text_cancle)).setOnClickListener(myClickListener);
        this.dialog_portrait = new AlertDialog.Builder(this).setView(localView).show();
        this.dialog_portrait.setCanceledOnTouchOutside(true);
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        Log.e(TAG, "requestCode = " + paramInt1);

        switch (paramInt1) {
            default:
                return;
            case 1:
                if (dialog_portrait != null)
                    dialog_portrait.dismiss();
                if (paramInt2 == -1) {
                    cropPhoto(paramIntent.getData());
                }
                break;

            case 2:
                if (dialog_portrait != null)
                    dialog_portrait.dismiss();
                if (paramInt2 == -1) {
                    cropPhoto(Uri.fromFile(new File((new StringBuilder()).append(Environment.getExternalStorageDirectory()).append("/head.jpg").toString())));
                    return;
                }
                break;
            case 3:
                if (paramIntent != null && paramIntent.getExtras() != null) {
                    head = (Bitmap) paramIntent.getExtras().getParcelable("data");
                    Log.e(TAG, (new StringBuilder("head = ")).append(head).toString());
                    if (head != null) {
                        userimg.setImageBitmap(head);
                    }
                }

        }

    }

    public void cropPhoto(Uri paramUri) {
        Intent localIntent = new Intent("com.android.camera.action.CROP");
        localIntent.setDataAndType(paramUri, "image/*");
        localIntent.putExtra("crop", "true");
        localIntent.putExtra("aspectX", 1);
        localIntent.putExtra("aspectY", 1);
        localIntent.putExtra("outputX", 300);
        localIntent.putExtra("outputY", 300);
        localIntent.putExtra("return-data", true);
        startActivityForResult(localIntent, 3);
    }

    protected void saveInfo() {
        // TODO Auto-generated method stub
        int age = getBirthInt(String.valueOf(birth.getText()));
        int gender = 0;
        if (radioGroup.getCheckedRadioButtonId() == R.id.man) {
            gender = 1;
        } else {
            gender = 0;
        }
        if (mPrefs == null)
            mPrefs = getSharedPreferences("WATCH", 0);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString("NICKNAME", String.valueOf(nick_name.getText()));
        edit.putString("BIRTH", String.valueOf(birth.getText()));
        edit.putString("HEIGHT", String.valueOf(height.getText()));
        edit.putString("WEIGHT", String.valueOf(weight.getText()));
        edit.putInt("GENDER", gender);
        Log.e(TAG, "gender= " + gender);
        edit.commit();
        int userid = mPrefs.getInt("LOG_USERID", 0);
        UserInfo info = new UserInfo();
        info.nickname = nick_name.getText().toString();
        info.userid = userid;
        info.sex = gender;
        info.age = String.valueOf(birth.getText());
        info.height = String.valueOf(height.getText());
        info.weight = String.valueOf(weight.getText());
        httpUtils.postUserInfo(this, info, this);
        if (this.head != null) {
            try {
                setPicToView(this.head);
                if (!this.head.isRecycled()) {
                    this.head.recycle();
                    this.head = null;
                }
            } catch (Exception e) {
                if (!this.head.isRecycled()) {
                    this.head.recycle();
                    this.head = null;
                }
            }
        }
        byte[] arrayOfByte = new byte[4];
        int heightOfSend = Integer.valueOf(info.height);
        int weightOfSend = Integer.valueOf(info.weight);
        arrayOfByte[0] = 0x07;
        arrayOfByte[1] = (byte) heightOfSend;
        arrayOfByte[2] = (byte) weightOfSend;
        arrayOfByte[3] = 0x0;
        Log.e(TAG, "arrayOfByte[1= " + arrayOfByte[1] + "arrayOfByte[2]= " + arrayOfByte[2]);
        MainService.getInstance().writeCharacteristic(
                CustomizedBleClient.getGatt(), MainService.UUID_SERVICE,
                MainService.UUID_CHARACTERISTIC_WRITE_AND_READ, arrayOfByte);
    }

    private void setPicToView(Bitmap bitmap) {
        FileOutputStream fileoutputstream;
        String s;
        if (!Environment.getExternalStorageState().equals("mounted"))
            return;
        fileoutputstream = null;
        (new File(Global.PATH)).mkdirs();
        s = (new StringBuilder(String.valueOf(Global.PATH))).append("head.jpg").toString();
        try {
            FileOutputStream fileoutputstream1 = new FileOutputStream(s);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fileoutputstream1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private int getBirthInt(String text) {
        // TODO Auto-generated method stub
        if (text != null) {
            String month = null;
            String day = null;
            if (text.split("-")[1].length() == 1) {
                month = "0" + text.split("-")[1];
            } else {
                month = text.split("-")[1];
            }
            if (text.split("-")[2].length() == 1) {
                day = "0" + text.split("-")[2];
            } else {
                day = text.split("-")[2];
            }
            String age = text.split("-")[0] + month + day;
            Log.e(TAG, "age" + Integer.parseInt(age));
            return Integer.parseInt(age);
        }
        return 0;
    }

    protected void updateAfterLogin(Object obj) {
        // TODO Auto-generated method stub
        UserInfo userInfo = (UserInfo) obj;
        register.setText(getResources().getString(R.string.sync));
        login.setText(getResources().getString(R.string.logout));
        status = SYNC;
        if (mPrefs == null)
            mPrefs = getSharedPreferences("WATCH", 0);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt("LOG_STATUS", status);
        edit.putInt("LOG_USERID", userInfo.userid);
        edit.commit();
        nick_name.setText(String.valueOf(userInfo.nickname));
        height.setText(String.valueOf(userInfo.height));
        weight.setText(String.valueOf(userInfo.weight));
        birth.setText(String.valueOf(userInfo.age));
        // if (mPrefs == null)
        // mPrefs = getSharedPreferences("WATCH", 0);
        // SharedPreferences.Editor edit = mPrefs.edit();
        // edit.putString("NICKNAME",userInfo.nickname);
    }

    protected void showPopWindowHeight(TextView view) {
        // TODO Auto-generated method stub
        View popView = inflater.inflate(R.layout.layout_height, null, false);
        final PopupWindow popWindow_height = new PopupWindow(popView, -1,
                (int) (210 * getResources().getDisplayMetrics().density), true);
        popWindow_height.setOutsideTouchable(true);
        popWindow_height.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        height_pop = (WheelView) popView.findViewById(R.id.height_pop);
        height_pop.setViewAdapter(new NumericWheelAdapter(this, 40, 300));
        height_pop.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldHeight, int newHeight) {
                Log.e(TAG, "newHeight= " + newHeight);

            }
        });
        btn_ok = (Button) popView.findViewById(R.id.btn_ok);
        btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                height.setText(String.valueOf(height_pop.getCurrentItem() + 40));
                popWindow_height.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                popWindow_height.dismiss();
            }
        });
    }

    protected void showPopWindowWeight(TextView view) {
        // TODO Auto-generated method stub
        View popView = inflater.inflate(R.layout.layout_weight, null, false);
        final PopupWindow popWindow_height = new PopupWindow(popView, -1,
                (int) (210 * getResources().getDisplayMetrics().density), true);
        popWindow_height.setOutsideTouchable(true);
        popWindow_height.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        weight_pop = (WheelView) popView.findViewById(R.id.weight_pop);
        weight_pop.setViewAdapter(new NumericWheelAdapter(this, 20, 200));
        weight_pop.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldWeight, int newWeight) {
                Log.e(TAG, "newWeight= " + newWeight);

            }
        });
        btn_ok = (Button) popView.findViewById(R.id.btn_ok);
        btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                weight.setText(String.valueOf(weight_pop.getCurrentItem() + 20));
                popWindow_height.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                popWindow_height.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPrefs = getSharedPreferences("WATCH", 0);
        initUI();

    }

    protected void showPopWindowBirth(View view) {
        // TODO Auto-generated method stub

        View popView = inflater.inflate(R.layout.layout_birth, null, false);
        final PopupWindow popWindow = new PopupWindow(popView, -1,
                (int) (250 * getResources().getDisplayMetrics().density), true);
        popWindow.setOutsideTouchable(true);
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        Calendar calendar = Calendar.getInstance();
        day = (WheelView) popView.findViewById(R.id.day);
        day.setViewAdapter(new NumericWheelAdapter(this, 1, 31));
        month = (WheelView) popView.findViewById(R.id.month);
        month.setViewAdapter(new NumericWheelAdapter(this, 1, 12));
        year = (WheelView) popView.findViewById(R.id.year);
        year.setViewAdapter(new NumericWheelAdapter(this, 1900, calendar
                .get(Calendar.YEAR)));
        day.addChangingListener(wheelListener_day);
        month.addChangingListener(wheelListener_mon);
        year.addChangingListener(wheelListener_year);
        day.setCurrentItem(calendar.get(Calendar.DATE) - 1);
        month.setCurrentItem(calendar.get(Calendar.MONTH));
        year.setCurrentItem(calendar.get(Calendar.YEAR) - 1900);
        btn_ok = (Button) popView.findViewById(R.id.btn_ok);
        btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                String date_birth = (year.getCurrentItem() + 1900) + "-"
                        + (month.getCurrentItem() + 1) + "-"
                        + (day.getCurrentItem() + 1);
                birth.setText(date_birth);
                popWindow.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                popWindow.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (httpUtils == null) {
            httpUtils = HttpUtils.getInstance();
        }
    }

    @SuppressWarnings("deprecation")
    private void showRegisterDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getResources().getString(R.string.register));
        ad.setView(LayoutInflater.from(this).inflate(R.layout.register, null));
        adi = ad.create();
        adi.setButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText account = (EditText) adi
                                .findViewById(R.id.account);
                        // account.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        EditText password = (EditText) adi
                                .findViewById(R.id.password);
                        EditText phone = (EditText) adi
                                .findViewById(R.id.phone);
                        // String account
                        if (!isEmail(account.getText().toString())) {
                            Toast.makeText(
                                    PIActivity.this,
                                    getResources().getString(R.string.is_email),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (password.getText().length() < 6) {
                            Toast.makeText(
                                    PIActivity.this,
                                    getResources().getString(
                                            R.string.tips_password),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        httpUtils.registerUser(PIActivity.this, account
                                        .getText().toString(), password.getText()
                                        .toString(), phone.getText().toString(),
                                PIActivity.this);
                        Log.e(TAG, "account= " + account.getText());
                    }
                });
        adi.setButton2(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adi.dismiss();
                    }
                });
        adi.show();

    }

    @SuppressWarnings("deprecation")
    private void showLoginDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getResources().getString(R.string.login));
        ad.setView(LayoutInflater.from(this).inflate(R.layout.login, null));
        adi_login = ad.create();
        adi_login.setButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText account = (EditText) adi_login
                                .findViewById(R.id.account);
                        account.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        EditText password = (EditText) adi_login
                                .findViewById(R.id.password);
                        EditText phone = (EditText) adi_login
                                .findViewById(R.id.phone);
                        httpUtils.loginUser(PIActivity.this, account.getText()
                                        .toString(), password.getText().toString(),
                                PIActivity.this);
                    }
                });
        adi_login.setButton2(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adi_login.dismiss();
                    }
                });
        adi_login.show();

    }

    private boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        userimg = (MyCircleImageView) findViewById(R.id.userimg);
        userimg.setImageResource(R.drawable.portrait);
        nick_name = (EditText) findViewById(R.id.nick_name);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        man = (RadioButton) findViewById(R.id.man);
        woman = (RadioButton) findViewById(R.id.woman);
        birth = (TextView) findViewById(R.id.birth);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        register = (Button) findViewById(R.id.register);
        save = (TextView) findViewById(R.id.save);
        login = (Button) findViewById(R.id.login);
        save.setOnClickListener(myClickListener);
        birth.setOnClickListener(myClickListener);
        height.setOnClickListener(myClickListener);
        weight.setOnClickListener(myClickListener);
        login.setOnClickListener(myClickListener);
        userimg.setOnClickListener(myClickListener);
        register.setOnClickListener(myClickListener);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);

            }
        });
        if (mPrefs == null)
            mPrefs = getSharedPreferences("WATCH", 0);
        if (mPrefs.getInt("GENDER", 0) == 0) {
            woman.setChecked(true);
        } else {
            man.setChecked(true);
        }
        status = mPrefs.getInt("LOG_STATUS", 0);
        if (status == SYNC) {
            register.setText(R.string.sync);
            login.setText(R.string.logout);
        } else {
            register.setText(R.string.register);
            login.setText(R.string.login);
        }
        birth.setText(mPrefs.getString("BIRTH", "1970-01-01"));
        nick_name.setText(mPrefs.getString("NICKNAME", "NAME"));
        weight.setText(mPrefs.getString("WEIGHT", "70"));
        height.setText(mPrefs.getString("HEIGHT", "170"));
        Bitmap localBitmap = BitmapFactory.decodeFile(Global.PATH + "head.jpg");
        if (localBitmap != null)
            userimg.setImageBitmap(localBitmap);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onHttpRequestComplete(int what, int result, Object object) {
        // TODO Auto-generated method stub
        switch (what) {
            case HTTP_REQUEST_SESULT_REGISTER:
                Log.e(TAG, "onHttpRequestComplete= " + result);
                if (result == 1) {
                    Toast.makeText(this,
                            getResources().getString(R.string.register_success),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            getResources().getString(R.string.register_failed),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case HTTP_REQUEST_SESULT_LOGIN:
                Log.e(TAG, "HTTP_REQUEST_SESULT_LOGIN= " + result);
                if (result == 1) {
                    isLoggined = true;
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = object;
                    Log.e(TAG, "object= " + object);
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(this,
                            getResources().getString(R.string.login_fail),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case HTTP_REQUEST_SESULT_USERINFO:
                Log.e(TAG, "HTTP_REQUEST_SESULT_USERINFO= " + result);
                break;

            case HTTP_REQUEST_SESULT_RECORDER:
                Log.e(TAG, "HTTP_REQUEST_SESULT_RECORDER= " + result);
                Log.e(TAG, "object= " + object);
                ArrayList<SportInfo> list = (ArrayList<SportInfo>) object;
                Log.e(TAG, "sp= " + list.get(0).step);
                break;
            case HTTP_REQUEST_SESULT_SPORTINFO:
                Log.e(TAG, "HTTP_REQUEST_SESULT_SPORTINFO= " + result);
                break;
        }
    }

    @Override
    public void onError(int what) {
        // TODO Auto-generated method stub

    }

}
