package com.mtk.app.sos;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mediatek.ctrl.sos.SOSChangeListener;
import com.mediatek.ctrl.sos.SOSContact;
import com.mediatek.ctrl.sos.SOSController;
import com.mediatek.wearable.WearableManager;
import com.mtk.btnotification.R;
import com.mtk.main.MainActivity;

public class MultiKeySOSActivity extends ListActivity implements SOSChangeListener {

    private static final String TAG = "AppManager/SOSMultiKey";
    private SOSListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SOSListAdapter(this);
        this.setListAdapter(mAdapter);

        this.getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int key = position + 1;
                if (SOSController.getInstance().getContactForMultiKey(key) == null) {
                    Intent intent = new Intent(MultiKeySOSActivity.this, SOSEditActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("index", 1);
                    startActivity(intent);
                }
                return;
            }
        });

        SOSController.setListener(this);

        if (!SOSController.getInstance().isDataReady()) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.progress_dialog_title);
            dialog.setMessage(getString(R.string.load_sos_data));
            dialog.setCancelable(false);
            dialog.show();
            Timer timer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    if (SOSController.getInstance().isDataReady()) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            cancel();
                        }
                    }
                }
            };
            timer.schedule(task, 1000, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SOSController.setListener(null);
        super.onDestroy();
    }

    class SOSListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private LinearLayout mUnsetView;
        private LinearLayout mSetView;

        public SOSListAdapter(Context context) {
            mInflater = ((MultiKeySOSActivity)context).getLayoutInflater();
        }

        @Override
        public int getCount() {
            return SOSController.getInstance().getKeyCount();
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
            final int keyId = position + 1;
            Log.i(TAG, "getView, keyId = " + keyId);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.multikey_sos_listitem, null);
            }
            TextView keyView = (TextView) convertView.findViewById(R.id.key);
            keyView.setText(Integer.toString(keyId));
            mUnsetView = (LinearLayout) convertView.findViewById(R.id.unset_view);
            mSetView = (LinearLayout) convertView.findViewById(R.id.set_view);

            SOSContact contact = SOSController.getInstance().getContactForMultiKey(keyId);
            if (contact == null) {
                mSetView.setVisibility(View.GONE);
                mUnsetView.setVisibility(View.VISIBLE);
                mUnsetView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            } else {
                mUnsetView.setVisibility(View.GONE);
                mSetView.setVisibility(View.VISIBLE);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                TextView number = (TextView) convertView.findViewById(R.id.number);
                name.setText(contact.getName());
                number.setText(contact.getNumber());

                ImageView deleteButton = (ImageView) convertView.findViewById(R.id.delete);
                deleteButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        SOSController.getInstance().setContactForMultiKey(keyId, null);
                        notifyDataSetChanged();
                    }
                });
            }
            return convertView;
        }
        
    }

    @Override
    public void onNewDataArrived() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onErrArrived(int errCode, int cmdType) {
        Log.i(TAG, "error response received, errCode = " + errCode + ", cmdType = " + cmdType);
        if (cmdType == 0x04) {
            Toast.makeText(this, getString(R.string.sos_response_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onExtendReceive(byte[] dataBuffer) {
        Log.i(TAG, "extend command received");
    }

    @Override
    public void onConnectionChanged(int state) {
        if (state == WearableManager.STATE_CONNECT_LOST) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

}
