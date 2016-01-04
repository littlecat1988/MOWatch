package com.mtk.app.sos;

import java.io.UnsupportedEncodingException;

import com.mediatek.ctrl.sos.SOSContact;
import com.mediatek.ctrl.sos.SOSController;
import com.mtk.app.applist.ApplistActivity;
import com.mtk.btnotification.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SOSEditActivity extends Activity {

    private EditText mNameText;
    private EditText mPhoneText;
    private Integer mKey;
    private Integer mIndex;
    private SOSController mSOSController;
    private Toast mToast;

    private static int MAX_NAME_LENGTH = 41;
    private static int MAX_NUMBER_LENGTH = 30;

    private InputFilter[] mNameInputFilter = new InputFilter[] {
            new InputFilter() {

                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                        Spanned dest, int dstart, int dend) {
                    int replaceLen = dend - dstart;
                    try {
                        byte[] dbytes = dest.toString().getBytes("UTF-8");
                        byte[] sbytes = source.toString().getBytes("UTF-8");

                        if (replaceLen == 0 ) {
                            if (dbytes.length + sbytes.length > MAX_NAME_LENGTH) {
                                source = cutStrByByte(source.toString(), MAX_NAME_LENGTH - dbytes.length);
                            }
                        } else {
                            CharSequence cut = dest.toString().substring(dstart, dend);
                            byte[] cbytes = cut.toString().getBytes("UTF-8");
                            if (dbytes.length - cbytes.length + sbytes.length > MAX_NAME_LENGTH) {
                                source = cutStrByByte(source.toString(), MAX_NAME_LENGTH - dbytes.length + cbytes.length);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return source;
                }
            }
    };

    private String cutStrByByte(String str, int length) throws UnsupportedEncodingException {
        if (str == null)
            return "";
        else {
            int strLength = str.length();
            String subStr = str;
            int byteLength = str.getBytes("UTF-8").length;
            while (byteLength > length) {
                subStr = str.substring(0, strLength--);
                byteLength = subStr.getBytes("UTF-8").length;
            }
            return subStr;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mKey = intent.getIntExtra("key", 0);
        mIndex = intent.getIntExtra("index", 0);
        mSOSController = SOSController.getInstance();
        setContentView(R.layout.sos_edit_number);
        mNameText = (EditText) findViewById(R.id.name);
        mNameText.setFilters(mNameInputFilter);
        mPhoneText = (EditText) findViewById(R.id.phone);

        ActionBar actionBar = getActionBar();
        ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(
            R.layout.sos_edit_actionbar, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
            | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        ImageButton mQuit = (ImageButton) v.findViewById(R.id.done);
        mQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameText.getText().toString();
                String number = mPhoneText.getText().toString();
                if (TextUtils.isEmpty(name) || name.trim().equals("")) {
                    mToast = Toast.makeText(SOSEditActivity.this, R.string.invalid_name, 1);
                    mToast.show();
                } else if (TextUtils.isEmpty(number) || number.trim().equals("")) {
                    mToast = Toast.makeText(SOSEditActivity.this, R.string.invalid_phone, 1);
                    mToast.show();
                } else {
                    SOSContact contact = mSOSController.generateContact(name, number);
                    mSOSController.setContact(mKey, mIndex, contact);
                    SOSEditActivity.this.finish();
                }
            }
        });

        actionBar.setCustomView(v);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
