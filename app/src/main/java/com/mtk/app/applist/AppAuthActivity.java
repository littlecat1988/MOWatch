package com.mtk.app.applist;

import java.util.ArrayList;
import java.util.HashMap;

import com.mtk.btnotification.R;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AppAuthActivity extends ListActivity {
    public static AppAuthActivity instance = null;
    private static final String TAG = "AppManager/AppAuth";
    private String mVxpName = "";
    private AuthListAdapter mAdapter;

    private int mCurrentPermValue = 0x00000000;

    private static int CALL_UP             = 0x00000001;
    private static int SEND_SMS            = 0x00000002;
    private static int SEND_MMS            = 0x00000004;
    private static int SWITCH_GPRS         = 0x00000008;
    private static int CONNECT_GPRS        = 0x00000010;
    private static int SWITCH_WLAN         = 0x00000020;
    private static int CONNECT_WLAN        = 0x00000040;
    private static int ENABLE_GPS          = 0x00000080;
    private static int CALL_RECORD         = 0x00000100;
    private static int RECORD              = 0x00000200;
    private static int CAMERA              = 0x00000400;
    private static int READ_PHONEBOOK      = 0x00000800;
    private static int READ_CALLLOG        = 0x00001000;
    private static int READ_SMS            = 0x00002000;
    private static int READ_MMS            = 0x00004000;
    private static int SWITCH_BLUETOOTH    = 0x00008000;
    private static int DELETE_SMS          = 0x00010000;
    private static int CANT_MODIFY         = 0x80000000;
    private static int NO_PERMISSION       = 0x00000000;

    private static ArrayList<Integer> sPermissionList;
    private HashMap<Integer, String> mPermNameMap;
    private HashMap<Integer, PermissionEntry> mPermEntryMap = new HashMap<Integer, PermissionEntry>();

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Switch switchView = (Switch) v.findViewById(R.id.widget_switch);
        switchView.setChecked(!switchView.isChecked());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (instance != null) {
            instance.finish();
        }
        instance = this;

        Intent intent = getIntent();
//        mVxpName = intent.getStringExtra("vxpname");
//        if (TextUtils.isEmpty(mVxpName)) {
//            instance.finish();
//        }
        mCurrentPermValue = intent.getIntExtra("permset", 0x00000000);
        initPermissionType();
        refreshPermissionEntry(mCurrentPermValue);
        mAdapter = new AuthListAdapter(this);
        this.setListAdapter(mAdapter);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestory");
        super.onDestroy();
    }

    public class AuthListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public AuthListAdapter(Context context) {
            mInflater = ((AppAuthActivity)context).getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mPermEntryMap.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView");
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.appauth_listview, null);
            }
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView summary = (TextView) convertView.findViewById(R.id.summary);
            Switch switchView = (Switch) convertView.findViewById(R.id.widget_switch);
            final PermissionEntry entry = mPermEntryMap.get(position);
            if (entry == null) {
                return null;
            }
            title.setText(entry.name);
            switchView.setChecked(entry.isOn);
            switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    entry.isOn = isChecked;
                    mCurrentPermValue = isChecked ? (entry.type | mCurrentPermValue) : ((~entry.type) & mCurrentPermValue);
                }
                
            });
            return convertView;
        }
        
    }

    public class PermissionEntry {
        public int type;
        public String name;
        public boolean isOn;
    }

    private void initPermissionType() {
        sPermissionList = new ArrayList<Integer>();
        sPermissionList.add(CALL_UP);
        sPermissionList.add(SEND_SMS);
        sPermissionList.add(SEND_MMS);
        sPermissionList.add(SWITCH_GPRS);
        sPermissionList.add(CONNECT_GPRS);
        sPermissionList.add(SWITCH_WLAN);
        sPermissionList.add(CONNECT_WLAN);
        sPermissionList.add(ENABLE_GPS);
        sPermissionList.add(CALL_RECORD);
        sPermissionList.add(RECORD);
        sPermissionList.add(CAMERA);
        sPermissionList.add(READ_PHONEBOOK);
        sPermissionList.add(READ_CALLLOG);
        sPermissionList.add(READ_SMS);
        sPermissionList.add(READ_MMS);
        sPermissionList.add(SWITCH_BLUETOOTH);
        sPermissionList.add(DELETE_SMS);

        mPermNameMap = new HashMap<Integer, String>();
        mPermNameMap.put(CALL_UP,getText(R.string.call_up).toString());
        mPermNameMap.put(SEND_SMS,getText(R.string.send_sms).toString());
        mPermNameMap.put(SEND_MMS,getText(R.string.send_mms).toString());
        mPermNameMap.put(SWITCH_GPRS,getText(R.string.switch_gprs).toString());
        mPermNameMap.put(CONNECT_GPRS,getText(R.string.connect_gprs).toString());
        mPermNameMap.put(SWITCH_WLAN,getText(R.string.switch_wlan).toString());
        mPermNameMap.put(CONNECT_WLAN,getText(R.string.connect_wlan).toString());
        mPermNameMap.put(ENABLE_GPS,getText(R.string.enable_gps).toString());
        mPermNameMap.put(CALL_RECORD,getText(R.string.call_record).toString());
        mPermNameMap.put(RECORD,getText(R.string.record).toString());
        mPermNameMap.put(CAMERA,getText(R.string.camera).toString());
        mPermNameMap.put(READ_PHONEBOOK,getText(R.string.read_phonebook).toString());
        mPermNameMap.put(READ_CALLLOG,getText(R.string.read_calllog).toString());
        mPermNameMap.put(READ_SMS,getText(R.string.read_sms).toString());
        mPermNameMap.put(READ_MMS,getText(R.string.read_mms).toString());
        mPermNameMap.put(SWITCH_BLUETOOTH,getText(R.string.switch_bluetooth).toString());
        mPermNameMap.put(DELETE_SMS,getText(R.string.delete_sms).toString());
    }

    private void refreshPermissionEntry(int permissionValue) {
        mPermEntryMap = new HashMap<Integer, PermissionEntry>();
        int j = 0;
        for (int i = 0; i < sPermissionList.size(); i++) {
            int type = sPermissionList.get(i);
            if ((type & permissionValue) == type) {
                PermissionEntry entry = new PermissionEntry();
                entry.type = type;
                entry.name = mPermNameMap.get(type);
                entry.isOn = true;
                mPermEntryMap.put(j++, entry);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
        
    }

    private static final int MENU_SAVE = 1001;
    private static final int MENU_CANCEL = 1002;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        
        menu.add(0, MENU_SAVE, 0, R.string.button_save).setTitle(
                R.string.button_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);;

        menu.add(0, MENU_CANCEL, 0, R.string.cancel).setTitle(
                R.string.cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SAVE:
                Intent intent = new Intent();
                intent.putExtra("permission", mCurrentPermValue);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case MENU_CANCEL:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return true;
    }

}
