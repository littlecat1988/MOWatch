
package com.mtk.app.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.mtk.main.MainActivity;
import com.mtk.main.MainService;
import com.mtk.main.Utils;

import android.util.Log;

import com.mtk.btnotification.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

public class NotificationAppListActivity extends Activity {
    // Debugging
    private static final String TAG = "AppManager/NotificationAppList";

    // Tab tag enum
    private static final String TAB_TAG_PERSONAL_APP = "personal_app";

    private static final String TAB_TAG_SYSTEM_APP = "system_app";

    private LayoutInflater mInflater;

    private Context mContext;

    // View item filed
    private static final String VIEW_ITEM_INDEX = "item_index";

    private static final String VIEW_ITEM_ICON = "package_icon";

    private static final String VIEW_ITEM_TEXT = "package_text";

    private static final String VIEW_ITEM_CHECKBOX = "package_switch";

    private static final String VIEW_ITEM_NAME = "package_name"; // Only for
                                                                 // save to
                                                                 // ignore list

    private TabHost mTabHost = null;

    private ListView mPersonalAppListView;

    private ListView mSystemAppListView;

    private List<Map<String, Object>> mPersonalAppList = null;

    private List<Map<String, Object>> mBlockAppList = null;

    // For system app list
    private List<Map<String, Object>> mSystemAppList = null;

    private SystemAppListAdapter mSystemAppAdapter = null;

    // private int mPersonalAppSelectedCount = 0;
    private PersonalAppListAdapter mPersonalAppAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.notification_app_list);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.tittle_btn_layout);

        mContext = this;

        initTabHost();
        initTabWidget();

        LoadPackageTask loadPackageTask = new LoadPackageTask(this);
        try {
            loadPackageTask.execute("");
        } catch (Exception e) {
            Toast toast = Toast.makeText(NotificationAppListActivity.this,
                    R.string.launchfail, 1);
            toast.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.acsetting, menu);
        if (true) {
            menu.findItem(R.id.menu_acsetting).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_acsetting:
                if (android.os.Build.VERSION.SDK_INT < 18) {
                    startActivity(MainActivity.ACCESSIBILITY_INTENT);
                } else {
                    startActivity(MainActivity.NOTIFICATION_LISTENER_INTENT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_PERSONAL_APP).setContent(R.id.notilinear001)
                .setIndicator(getString(R.string.personal_apps_title)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_SYSTEM_APP).setContent(R.id.notilinear002)
                .setIndicator(getString(R.string.system_apps_title)));
    }

    private void initTabWidget() {
        TabWidget tabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View child = tabWidget.getChildAt(i);

            // Set text to center
            final TextView tv = (TextView) child.findViewById(android.R.id.title);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv.getLayoutParams();
            // params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            // params.addRule(RelativeLayout.CENTER_IN_PARENT,
            // RelativeLayout.TRUE);

            // Adjust TabWidget height
            // final float ratio = 1.5F;
            // child.getLayoutParams().height /= ratio;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        saveIgnoreList();
        saveBlockList();
    }

    private void saveBlockList() {

        // Save personal app

        BlockList.getInstance().saveBlockList();

        // Load package in background
    }

    private void saveIgnoreList() {
        IgnoreList.getInstance().saveIgnoreList();

        // Prompt user that have saved successfully .
        // Toast.makeText(this, R.string.save_successfully,
        // Toast.LENGTH_SHORT).show();
    }

    private void initUiComponents() {
        mPersonalAppListView = (ListView) findViewById(R.id.list_notify_personal_app);
        mPersonalAppAdapter = new PersonalAppListAdapter(this);
        mPersonalAppListView.setAdapter(mPersonalAppAdapter);

        mSystemAppListView = (ListView) findViewById(R.id.list_notify_system_app);
        mSystemAppAdapter = new SystemAppListAdapter(this);
        mSystemAppListView.setAdapter(mSystemAppAdapter);
    }

    private class PackageItemComparator implements Comparator<Map<String, Object>> {

        private final String mKey;

        public PackageItemComparator() {
            mKey = NotificationAppListActivity.VIEW_ITEM_TEXT;
        }

        /**
         * Compare package in alphabetical order.
         * 
         * @see java.util.Comparator#compare(Object, Object)
         */
        @Override
        public int compare(Map<String, Object> packageItem1, Map<String, Object> packageItem2) {

            String packageName1 = (String) packageItem1.get(mKey);
            String packageName2 = (String) packageItem2.get(mKey);
            return packageName1.compareToIgnoreCase(packageName2);
        }
    }

    private class PersonalAppListAdapter extends BaseAdapter {
        private Activity activity;

        public class ViewHolder {
            public TextView tvAppName;

            public ImageView ivIcon;

            public Switch swPush;
        }

        public PersonalAppListAdapter(Context context) {
            this.activity = (NotificationAppListActivity) context;
            mInflater = activity.getLayoutInflater();

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return (mPersonalAppList.size() + 2);
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            ViewHolder viewHolder = null;
            /*
             * TextView tvAppName; ImageView ivIcon; Switch swPush;
             */

            if (view == null) {
                viewHolder = new ViewHolder();

                view = mInflater.inflate(R.layout.package_list_layout, null);
                view.setPadding(0, 30, 0, 30);
                viewHolder.tvAppName = (TextView) view.findViewById(R.id.package_text);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.package_icon);
                viewHolder.swPush = (Switch) view.findViewById(R.id.package_switch);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();

                    viewHolder.tvAppName = (TextView) view.findViewById(R.id.package_text);
                    viewHolder.ivIcon = (ImageView) view.findViewById(R.id.package_icon);
                    viewHolder.swPush = (Switch) view.findViewById(R.id.package_switch);
                    view.setTag(viewHolder);
                }
            }

            /*
             * view = mInflater.inflate(R.layout.package_list_layout, null);
             * view.setPadding(0, 20, 0, 20); tvAppName = (TextView)
             * view.findViewById(R.id.package_text); ivIcon = (ImageView)
             * view.findViewById(R.id.package_icon); swPush = (Switch)
             * view.findViewById(R.id.package_switch);
             */
            Map<String, Object> packageItem = null;

            viewHolder.swPush
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int index = position;
                            if (index == 0) {
                                if (isChecked) {
                                    MainService.getInstance().startCallService();
                                } else {
                                    MainService.getInstance().stopCallService();
                                }

                                return;
                            } else if (index == 1) {
                                if (isChecked) {
                                    MainService.getInstance().startSmsService();
                                } else {
                                    MainService.getInstance().stopSmsService();
                                }
                                return;
                            }
                            Map<String, Object> item = mPersonalAppList.get(index - 2);
                            if (item == null) {
                                return;
                            }

                            // Toggle item selection
                            item.remove(VIEW_ITEM_CHECKBOX);
                            item.put(VIEW_ITEM_CHECKBOX, isChecked);

                            // update list data

                            String appName = (String) item.get(VIEW_ITEM_NAME);
                            if (!isChecked) {

                                IgnoreList.getInstance().addIgnoreItem(appName);
                            } else {
                                IgnoreList.getInstance().removeIgnoreItem(appName);
                                BlockList.getInstance().removeBlockItem(appName);
                            }

                        }
                    });

            if (position >= 2) {
                packageItem = mPersonalAppList.get(position - 2);

                Drawable data = (Drawable) packageItem.get(VIEW_ITEM_ICON);
                viewHolder.ivIcon.setImageDrawable(data);

                String text = (String) packageItem.get(VIEW_ITEM_TEXT);
                viewHolder.tvAppName.setText(text);

                Boolean checked = (Boolean) packageItem.get(VIEW_ITEM_CHECKBOX);
                viewHolder.swPush.setChecked(checked);
            } else {
                if (position == 0) {
                    viewHolder.ivIcon.setImageResource(R.drawable.call_service);
                    viewHolder.tvAppName.setText(R.string.call_preference_title);
                    Boolean checked = (MainService.getInstance().getCallServiceStatus());
                    viewHolder.swPush.setChecked(checked);
                } else if (position == 1) {
                    viewHolder.ivIcon.setImageResource(R.drawable.message_service);
                    viewHolder.tvAppName.setText(R.string.sms_preference_title);
                    Boolean checked = (MainService.getInstance().getSmsServiceStatus());
                    viewHolder.swPush.setChecked(checked);
                }
            }
            // ivIcon.setImageDrawable(data);

            // tvAppName.setText(text);

            // swPush.setChecked(checked);

            //
            return view;
        }

    }

    private class SystemAppListAdapter extends BaseAdapter {
        private Activity activity;

        public class ViewHolder {
            public TextView tvAppName;

            public ImageView ivIcon;

            public Switch swPush;
        }

        public SystemAppListAdapter(Context context) {
            this.activity = (NotificationAppListActivity) context;
            mInflater = activity.getLayoutInflater();

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mSystemAppList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            ViewHolder viewHolder = null;
            /*
             * TextView tvAppName; ImageView ivIcon; Switch swPush;
             */

            if (view == null) {
                viewHolder = new ViewHolder();

                view = mInflater.inflate(R.layout.package_list_layout, null);
                view.setPadding(0, 30, 0, 30);
                viewHolder.tvAppName = (TextView) view.findViewById(R.id.package_text);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.package_icon);
                viewHolder.swPush = (Switch) view.findViewById(R.id.package_switch);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            /*
             * view = mInflater.inflate(R.layout.package_list_layout, null);
             * view.setPadding(0, 20, 0, 20); tvAppName = (TextView)
             * view.findViewById(R.id.package_text); ivIcon = (ImageView)
             * view.findViewById(R.id.package_icon); swPush = (Switch)
             * view.findViewById(R.id.package_switch);
             */
            Map<String, Object> packageItem = null;

            viewHolder.swPush
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int index = position;

                            Map<String, Object> item = mSystemAppList.get(index);
                            if (item == null) {
                                return;
                            }

                            // Toggle item selection
                            item.remove(VIEW_ITEM_CHECKBOX);
                            item.put(VIEW_ITEM_CHECKBOX, isChecked);

                            // update list data

                            String appName = (String) item.get(VIEW_ITEM_NAME);
                            if (!isChecked) {

                                IgnoreList.getInstance().addIgnoreItem(appName);
                            } else {
                                IgnoreList.getInstance().removeIgnoreItem(appName);
                                BlockList.getInstance().removeBlockItem(appName);
                            }

                        }
                    });

            packageItem = mSystemAppList.get(position);

            Drawable data = (Drawable) packageItem.get(VIEW_ITEM_ICON);
            viewHolder.ivIcon.setImageDrawable(data);

            String text = (String) packageItem.get(VIEW_ITEM_TEXT);
            viewHolder.tvAppName.setText(text);

            Boolean checked = (Boolean) packageItem.get(VIEW_ITEM_CHECKBOX);
            viewHolder.swPush.setChecked(checked);
            return view;
        }

    }

    private class LoadPackageTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog mProgressDialog;

        private final Context mContext;

        public LoadPackageTask(Context context) {
            Log.i(TAG, "LoadPackageTask(), Create LoadPackageTask!");

            mContext = context;
            createProgressDialog();
        }

        /*
         * Show a ProgressDialog to prompt user to wait
         */
        private void createProgressDialog() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle(R.string.progress_dialog_title);
            mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_message));
            mProgressDialog.show();

            Log.i(TAG, "createProgressDialog(), ProgressDialog shows");
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Log.i(TAG, "doInBackground(), Begin load and sort package list!");

            // Load and sort package list
            loadPackageList();
            sortPackageList();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "onPostExecute(), Load and sort package list complete!");

            // Do the operation after load and sort package list completed
            initUiComponents();

            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mProgressDialog = null;
            }
        }

        private synchronized void loadPackageList() {
            mPersonalAppList = new ArrayList<Map<String, Object>>();
            mBlockAppList = new ArrayList<Map<String, Object>>();
            mSystemAppList = new ArrayList<Map<String, Object>>();
            HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
            HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
            HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
            List<PackageInfo> packagelist = getPackageManager().getInstalledPackages(0);

            for (PackageInfo packageInfo : packagelist) {
                if (packageInfo != null) {
                    // Whether this package should be exclude;
                    if (exclusionList.contains(packageInfo.packageName)) {
                        continue;
                    }

                    /*
                     * Add this package to package list
                     */
                    Map<String, Object> packageItem = new HashMap<String, Object>();

                    // Add app icon
                    Drawable icon = mContext.getPackageManager().getApplicationIcon(
                            packageInfo.applicationInfo);
                    packageItem.put(VIEW_ITEM_ICON, icon);

                    // Add app name
                    String appName = mContext.getPackageManager()
                            .getApplicationLabel(packageInfo.applicationInfo).toString();
                    packageItem.put(VIEW_ITEM_TEXT, appName);
                    packageItem.put(VIEW_ITEM_NAME, packageInfo.packageName);

                    // Add if app is selected
                    boolean isChecked = ((!ignoreList.contains(packageInfo.packageName)) && (!blockList
                            .contains(packageInfo.packageName)));
                    packageItem.put(VIEW_ITEM_CHECKBOX, isChecked);

                    // Add to package list
                    if (!Utils.isSystemApp(packageInfo.applicationInfo)) {
                        mPersonalAppList.add(packageItem);
                    } else {
                        mSystemAppList.add(packageItem);
                    }
                }
            }

            Log.i(TAG, "loadPackageList(), PersonalAppList=" + mPersonalAppList);
        }

        private synchronized void sortPackageList() {
            // Sort package list in alphabetical order.
            PackageItemComparator comparator = new PackageItemComparator();

            // Sort personal app list
            if (mPersonalAppList != null) {
                Collections.sort(mPersonalAppList, comparator);
            }
            if (mSystemAppList != null) {
                Collections.sort(mSystemAppList, comparator);
            }

            Log.i(TAG, "sortPackageList(), PersonalAppList=" + mPersonalAppList);
        }
    }

}
