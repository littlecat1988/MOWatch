package com.mtk.app.sos;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.mediatek.ctrl.sos.SOSChangeListener;
import com.mediatek.ctrl.sos.SOSContact;
import com.mediatek.ctrl.sos.SOSController;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.applist.ApplistActivity;
import com.mtk.btnotification.R;
import com.mtk.main.MainActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OneKeySOSActivity extends ListActivity implements SOSChangeListener {
    private static final String TAG = "AppManager/SOSOneKey";
    private SOSListAdapter mAdapter;

    private static final int MENU_CALL_MODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SOSListAdapter(this);
        this.setListAdapter(mAdapter);
        this.getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int index = position + 1;
                if (SOSController.getInstance().getContactForOneKey(index) == null) {
                    Intent intent = new Intent(OneKeySOSActivity.this, SOSEditActivity.class);
                    intent.putExtra("key", 1);
                    intent.putExtra("index", index);
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

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        menu.add(0, MENU_CALL_MODE, 0, R.string.call_mode_title).setIcon(R.drawable.ic_settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CALL_MODE:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        OneKeySOSActivity.this);
                builder.setTitle(R.string.call_mode_title);
                final ListView v = new ListView(this);
                v.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, getData()));
                v.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                int mode = SOSController.getInstance().getMode(1);
                v.setItemChecked(mode, true);

                builder.setView(v);
                builder.setPositiveButton(R.string.set,
                        new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // sync call mode to watch
                        SOSController.getInstance().setMode(1, v.getCheckedItemPosition());
                        arg0.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        data.add(getString(R.string.call_mode_loop));
        data.add(getString(R.string.call_mode_manual));
        return data;
    }

    class SOSListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private LinearLayout mUnsetView;
        private LinearLayout mSetView;

        public SOSListAdapter(Context context) {
            mInflater = ((OneKeySOSActivity)context).getLayoutInflater();
        }

        @Override
        public int getCount() {
            return SOSController.getInstance().getMaxSize();
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
            final int index = position + 1;
            Log.i(TAG, "getView, index = " + index);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.onekey_sos_listitem, null);
            }
            TextView indexView = (TextView) convertView.findViewById(R.id.index);
            indexView.setText(Integer.toString(index));
            mUnsetView = (LinearLayout) convertView.findViewById(R.id.unset_view);
            mSetView = (LinearLayout) convertView.findViewById(R.id.set_view);

            SOSContact contact = SOSController.getInstance().getContactForOneKey(index);
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
                        SOSController.getInstance().setContactForOneKey(index, null);
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
