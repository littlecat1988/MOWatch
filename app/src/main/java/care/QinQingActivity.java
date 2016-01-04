package care;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import care.bean.BaoBeiBean;
import care.bean.FriendBean;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;
import care.widget.CircularImage;

public class QinQingActivity extends CommonBaseActivity implements
        OnClickListener {

    private CircularImage device_img;
    private TextView device_name;
    private TextView device_phone;

    private ListView relatives_list = null;
    private ListView friend_list = null;
    private ImageButton relatives_add, friend_add;

    private SparseArray<String> mGroupList = new SparseArray<String>();
    private SparseArray<SparseArray<FriendBean>> mChildList = new SparseArray<SparseArray<FriendBean>>();
    private Handler mHandler;
    private String type = "0";
    private BaoBeiBean currentBaby;
    private List<Map<String, Object>> relativesList = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> friendList = new ArrayList<Map<String, Object>>();
    private RelativesSystemInfoAdspter relativeAdapter;
    private FriendSystemInfoAdspter friendAdapter;
    private String phone_number = "";
    private String phone_number2 = "";

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.qinqing_main);

    }

    @Override
    protected void initFindView() {
        //
        mHandler = new Handler();
        titleString.setText(R.string.qingqing_string);
        device_img = (CircularImage) findViewById(R.id.device_img);
        device_name = (TextView) findViewById(R.id.device_name);
        device_phone = (TextView) findViewById(R.id.device_phone);
        relatives_list = (ListView) findViewById(R.id.relatives_list);
        friend_list = (ListView) findViewById(R.id.friend_list);
        relatives_add = (ImageButton) findViewById(R.id.relative_add);
        friend_add = (ImageButton) findViewById(R.id.friend_add);
        init();
        setOnClickLister();
        LoadRelativesList();
        LoadFriendList();
    }

    private void setOnClickLister() {
        // TODO Auto-generated method stub
        // add_qinqing_id.setOnClickListener(this);
        relatives_add.setOnClickListener(this);
        friend_add.setOnClickListener(this);
    }

    void updateAddButton() {
        if (relatives_list.getCount() >= 4) {
            relatives_add.setVisibility(View.GONE);

        } else {
            relatives_add.setVisibility(View.VISIBLE);
        }

        System.out.println("relatives = " + relatives_list.getCount());
        System.out.println("frinend  = " + friend_list.getCount());
        if (friend_list.getCount() >= 9) {
            friend_add.setVisibility(View.GONE);

        } else {
            friend_add.setVisibility(View.VISIBLE);
        }

    }

    protected void onResume() {
        super.onResume();
        updateAddButton();
    }

    private void init() {

        phone_number2 = "";
        phone_number = "";
        String babyList = tools.get_babyList();
        System.out.println("babylist = " + babyList);
        try {
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

            relativesList.clear();
            friendList.clear();
            ArrayList<Map<String, Object>> phoneList = currentBaby
                    .getPhoneBook();
            for (int i = 0; i < phoneList.size(); i++) {
                Map<String, Object> familyMember = phoneList.get(i);
                String relative = familyMember.get("family_relative")
                        .toString();

                String famil_phone = familyMember.get("family_phone")
                        .toString();

                phone_number += famil_phone + ",";


                if ("0".equals(relative)) {
                    relativesList.add(familyMember);
                } else if ("1".equals(relative)) {
                    friendList.add(familyMember);
                }
            }
        }

        try {
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
            device_name.setText(currentBaby.getName());
            device_phone.setText(currentBaby.getPhone());
            imageLoader.displayImage(currentBaby.getPhoto(), device_img,
                    options);
        }

        if (phone_number.equals("")) {
            phone_number2 = "";
        } else {
            phone_number2 = phone_number.substring(0, phone_number.length() - 1);
            System.out.println("number = " + phone_number2);
        }


    }

    @Override
    protected void onDestoryActivity() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {


            case R.id.relative_add:
                goto_Opertion("0", "add");
                break;


            case R.id.friend_add:
                goto_Opertion("1", "add");
                break;
        }
    }

    void LoadRelativesList() {
//		String aa = getString(R.string.test01);
//		List<Map<String, Object>> list = getRelativesData(aa);
        relativeAdapter = new RelativesSystemInfoAdspter(this, relativesList);
        relatives_list.setAdapter(relativeAdapter);
        getTotaHeightofListView(relatives_list);

    }

    void LoadFriendList() {
//		String bb = getString(R.string.test02);
//		List<Map<String, Object>> list = getFriendData(bb);
        friendAdapter = new FriendSystemInfoAdspter(this, friendList);
        friend_list.setAdapter(friendAdapter);
        getTotaHeightofListView(friend_list);

    }

    // public List<Map<String, Object>> getRelativesData(String relatives_date)
    // {
    //
    // List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    //
    // for (int i = 0; i < Utils.AnalyticalQinqingLength(relatives_date); i++) {
    //
    // Map<String, Object> map = new HashMap<String, Object>();
    // map.put("relatives_list_text1",
    // Utils.AnalyticalQinqingDate(0, i, relatives_date));
    // map.put("relatives_list_text2",
    // Utils.AnalyticalQinqingDate(1, i, relatives_date));
    //
    // list.add(map);
    //
    // }
    //
    // return list;
    // }

    // public List<Map<String, Object>> getFriendData(String friend_date) {
    //
    // List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    //
    // for (int i = 0; i < Utils.AnalyticalQinqingLength(friend_date); i++) {
    // Map<String, Object> map = new HashMap<String, Object>();
    // map.put("friend_list_text1",
    // Utils.AnalyticalQinqingDate(0, i, friend_date));
    // map.put("friend_list_text2",
    // Utils.AnalyticalQinqingDate(1, i, friend_date));
    // list.add(map);
    //
    // }
    //
    // return list;
    // }

    private class RelativesSystemInfoAdspter extends BaseAdapter {

        private List<Map<String, Object>> data;
        private LayoutInflater layoutInflater;
        private Context context;

        // private int phone_type;

        public RelativesSystemInfoAdspter(Context context,
                                          List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
            // this.phone_type = phone_type;
        }

        public final class Zujian {
            public Button relatives_list_button1;
            public TextView relatives_list_text1;
            public TextView relatives_list_text2;
            public TextView relatives_list_text3;

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            Zujian zujian = null;
            if (convertView == null) {
                zujian = new Zujian();
                convertView = layoutInflater.inflate(R.layout.relatives_list,
                        null);

                zujian.relatives_list_button1 = (Button) convertView
                        .findViewById(R.id.relatives_list_button1);

                zujian.relatives_list_text1 = (TextView) convertView
                        .findViewById(R.id.relatives_list_text1);

                zujian.relatives_list_text2 = (TextView) convertView
                        .findViewById(R.id.relatives_list_text2);

                zujian.relatives_list_text3 = (TextView) convertView
                        .findViewById(R.id.relatives_list_text3);

                convertView.setTag(zujian);
            } else {
                zujian = (Zujian) convertView.getTag();
            }

            zujian.relatives_list_button1
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            @SuppressWarnings("unchecked")
                            HashMap<String, String> map = (HashMap<String, String>) relatives_list
                                    .getItemAtPosition(position);

                            String name = map.get("family_nick");
                            String phone = map.get("family_phone");
                            String id = map.get("family_id");

                            goto_Opertion("0", "mof", name, phone, position, id);
                            //
                            // Toast.makeText(QinQingActivity.this,
                            // "name= " + name + "phone" + phone + "id = " + id,
                            // Toast.LENGTH_SHORT).show();
                        }
                    });

            zujian.relatives_list_text1.setText((String) data.get(position)
                    .get("family_nick"));
            zujian.relatives_list_text2.setText((String) data.get(position)
                    .get("family_phone"));

            zujian.relatives_list_text3.setText((String) data.get(position)
                    .get("family_id"));

            return convertView;
        }

    }

    private class FriendSystemInfoAdspter extends BaseAdapter {

        private List<Map<String, Object>> data;
        private LayoutInflater layoutInflater;
        private Context context;

        // private int phone_type;

        public FriendSystemInfoAdspter(Context context,
                                       List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
        }

        public final class Zujian {
            public Button friend_list_button1;
            public TextView friend_list_text1;
            public TextView friend_list_text2;

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            Zujian zujian = null;
            if (convertView == null) {
                zujian = new Zujian();
                convertView = layoutInflater
                        .inflate(R.layout.friend_list, null);

                zujian.friend_list_button1 = (Button) convertView
                        .findViewById(R.id.friend_list_button1);

                zujian.friend_list_text1 = (TextView) convertView
                        .findViewById(R.id.friend_list_text1);

                zujian.friend_list_text2 = (TextView) convertView
                        .findViewById(R.id.friend_list_text2);

                convertView.setTag(zujian);
            } else {
                zujian = (Zujian) convertView.getTag();
            }

            zujian.friend_list_button1
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            @SuppressWarnings("unchecked")
                            HashMap<String, String> map = (HashMap<String, String>) friend_list
                                    .getItemAtPosition(position);

                            String name = map.get("family_nick");
                            String phone = map.get("family_phone");
                            String id = map.get("family_id");

                            goto_Opertion("1", "mof", name, phone, position, id);
                            // Toast.makeText(QinQingActivity.this,
                            // "name= " + name + "phone" + phone,
                            // Toast.LENGTH_SHORT).show();
                        }
                    });

            zujian.friend_list_text1.setText((String) data.get(position).get(
                    "family_nick"));
            zujian.friend_list_text2.setText((String) data.get(position).get(
                    "family_phone"));

            return convertView;
        }

    }

    public static void getTotaHeightofListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            System.out.println("no======");
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            ViewGroup.LayoutParams params = listView.getLayoutParams();

            params.height = totalHeight
                    + (listView.getDividerHeight() * (mAdapter.getCount() - 1));

            listView.setLayoutParams(params);
            listView.requestLayout();
        }

    }

    void goto_Opertion(String type1, String type2) {

        Intent opertion = new Intent();
        opertion.setClass(QinQingActivity.this, OperationQinQingActivity.class);
        opertion.putExtra("type1", type1);
        opertion.putExtra("type2", type2);
        opertion.putExtra("phone_number", phone_number2);
        startActivityForResult(opertion, 0);

    }

    void goto_Opertion(String type1, String type2, String name, String phone,
                       int position, String id) {

        Intent opertion = new Intent();
        opertion.setClass(QinQingActivity.this, OperationQinQingActivity.class);
        opertion.putExtra("type1", type1);
        opertion.putExtra("type2", type2);
        opertion.putExtra("qinqing_name", name);
        opertion.putExtra("qinqing_id", id);
        opertion.putExtra("qinqing_phone", phone);
        opertion.putExtra("position", position);
        opertion.putExtra("phone_number", phone_number2);
        startActivityForResult(opertion, 1);
    }

    protected void doConnectLinkCallback(String result) {


    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode,
                                 final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        initData();


    }

    public void initData() {
        new Thread(mRunnable).start();
    }

    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            final String add_result = downloadData("");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    downloadResultCheck(add_result);
                }
            });

        }

    };

    String downloadData(String m) {
        String p_sid = tools.get_user_id();
        JSONObject json_download = new JSONObject();
        try {
            json_download.put("user_id", p_sid);
            String json_download_result = Utils.GetService(json_download,
                    Constants.DOWNLOAD_DATA);
            if (json_download_result.equals("0")) {
                return "0";
            } else if (json_download_result.equals("-1")) {
                return "-1";
            } else {
                return json_download_result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }

    private void downloadResultCheck(String result_check) {

        if (result_check.equals("0") || result_check.equals("-1")) {
        } else {
            HashMap<String, String> map;
            try {
                Trace.i("result_check====" + result_check);

                map = BeanUtils.getJSONParserResult(result_check);
                String result = map.get("resultCode");
                if ("1".equals(result)) {
                    String babyList = map.get("device_message");
                    tools.set_babyList(babyList);

//					JSONArray babyArray = new JSONArray(babyList);
//					int length = babyArray.length();
//					for (int i = 0; i < length; i++) {
//						JSONObject babyObject = (JSONObject) babyArray.get(i);
//						HashMap<String, String> babyMap = BeanUtils
//								.getJSONParserResult(babyObject.toString());
//						BaoBeiBean baobei = BeanUtils.getBaoBei(babyMap);
//						String currentId = tools.get_current_device_id();
//						Trace.i("main currentId====" + currentId);
//						if (currentId == null || "".equals(currentId)
//								|| "null".equals(currentId)
//								|| "0".equals(currentId)) {
//							Trace.i("baoby id====" + baobei.getImei());
////							tools.set_current_device_id(baobei.getImei());
//						}
//
//					}
                    init();
                    LoadRelativesList();
                    LoadFriendList();
                    updateAddButton();
//					if (length == 0) {
////						Toast.makeText(MainActivity.this,
////								getString(R.string.no_device),
////								Toast.LENGTH_SHORT).show();
////						Intent i = new Intent(MainActivity.this,
////								BondDeviceActivity.class);
////						startActivity(i);
//					}
                } else if ("0".equals(result)) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
