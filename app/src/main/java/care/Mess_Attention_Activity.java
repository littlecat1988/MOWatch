package care;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mtk.btnotification.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import care.adapter.DateAdapter;
import care.adapter.DateComparator;
import care.adapter.DateText;
import care.bean.BaoBeiBean;
import care.utils.BeanUtils;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Trace;
import care.utils.Utils;
import care.utils.XcmTools;
import care.widget.CircularImage;

public class Mess_Attention_Activity extends CommonBaseActivity implements View.OnClickListener {

    private Handler messageHandler;
    private TextView message_ceshi;
    private XcmTools tools;
    private ImageButton menu_open,button_back;
    private ImageButton delete;
    private CircularImage cover_user_photo;
    private TextView family_bady_name, family_bady_phone, family_parent_phone;
    String f_bady__phone = "", f_bady_name = "", f_photo = "", book_phone = "",
            f_phonebook = "";
    private String badylist[] = {};
    private LinearLayout progressBar;
    private TextView progress_text;

    // 时间轴列表
    private ListView lvList;
    // 数据list
    private List<DateText> list;
    // 列表适配器
    private DateAdapter adapter;
    private String smgDate[] = {};
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.mess_attention);

    }

    @Override
    protected void initFindView() {
        // TODO Auto-generated method stub

        button_back  = (ImageButton)findViewById(R.id.back_button);
        button_back.setOnClickListener(this);
        cover_user_photo = (CircularImage) 	findViewById(R.id.message_bady_img);

        family_bady_name = (TextView) findViewById(R.id.message_bady_name);
        family_bady_phone = (TextView) findViewById(R.id.message_bady_phone);
        family_parent_phone = (TextView) findViewById(R.id.message_parent_phone);
        progressBar=(LinearLayout)findViewById(R.id.progress_bar);
        progress_text=(TextView)findViewById(R.id.progress_text);
        progress_text.setText(R.string.loading);
        messageHandler = new Handler();
        tools = new XcmTools(getApplicationContext());

        lvList = (ListView) findViewById(R.id.lv_list);
        badylist_jiexi();
//		badyInit(Integer.valueOf(tools.get_babylist_code()));

        progressBar.setVisibility(View.VISIBLE);
        GetMessageTask();
        updatelistDate();
    }
    private void GetMessageTask() {
        // TODO Auto-generated method stub
        HashMap<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("user_id", tools.get_user_id());
        String mJstr = mProtocolData.transFormToJson(tmp);
        new ConnectToLinkTask().execute(Constants.GETMESSAGE,mJstr);
    }
    private void updatelistDate() {
        list = new ArrayList<DateText>();
        // 添加测试数据
        addData();
        // 将数据按照时间排序
        DateComparator comparator = new DateComparator();
        Collections.sort(list, comparator);
        // listview绑定适配器
        adapter = new DateAdapter(getApplicationContext(), list);
        lvList.setAdapter(adapter);
        getTotaHeightofListView(lvList);

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
    private void addData() {
        String aa = "";

        for (int i = 0; i < smgDate.length; i++) {

            String myDate[] = {};

            myDate = smgDate[i].split("#");
//			aa += msgdate + "," + my_msg + "," + msgtype + ","
//					+ msgtime + "@zjw@";

            aa += "a1=" + myDate[0] + "  a2=" + myDate[1] + "  a3=" + myDate[2]
                    + "  a4=" + myDate[3];

            myDate[2]= "  ";
            DateText date1 = new DateText(myDate[0], myDate[1], myDate[2],
                    myDate[3]);
            list.add(date1);
        }

    }
    private void badylist_jiexi() {
        String babyList = tools.get_babyList();
        Log.i("badylist_jiexi", "babyList="+babyList);
        try {
            JSONArray babyArray=new JSONArray(babyList);
            String currentId=tools.get_current_device_id();
            Log.i("badylist_jiexi", "currentId="+currentId);
            for(int i=0;i<babyArray.length();i++){
                JSONObject babyObject=(JSONObject)babyArray.get(i);
                HashMap<String,String> babyMap= BeanUtils.getJSONParserResult(babyObject.toString());
                BaoBeiBean baobei= BeanUtils.getBaoBei(babyMap);
                if(baobei.getImei().equals(currentId)){
                    Log.i("badylist_jiexi", "baobei.getBaoBeiName()= " + baobei.getName());
                    family_bady_name.setText(baobei.getName());
                    family_bady_phone.setText(baobei.getPhone());
                    imageLoader.displayImage(baobei.getPhoto(), cover_user_photo,options);
//					family_parent_phone.setText(tools.get_login_phone());
                }else{
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void badyInit(int i) {

        JSONObject badyJson = new JSONObject();

        try {
            badyJson = Utils.getJSON(badylist[i]);

            f_bady__phone = badyJson.getString("phone");
            f_bady_name = badyJson.getString("name");
            f_photo = badyJson.getString("photo");

            f_phonebook = badyJson.getString("phonebook");

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

//		tools.set_book(f_phonebook);
        String book[] = {};
        book = f_phonebook.split(";");
        String book_1[] = {};

        book_1 = book[0].split(",");
        book_phone = book_1[1];

        family_bady_name.setText(f_bady_name);
        family_bady_phone.setText(f_bady__phone);
        family_parent_phone.setText(book_phone);
        cover_user_photo.setImageBitmap(Utils.stringtoBitmap(f_photo));
    }
    private String JiexiMsg(String msg) {
        String msgString = "";
        String msgOne = "";
        JSONObject msasageJson = new JSONObject();
        String msgtime1 = "";
        String msgtime = "";
        String msgdate = "";
        String my_msg1 = "";
        String my_msg = "";
        String msgtype = "";
        String aa = "";
        String serie = "";
        String area = "";
        try {
            if (msg.equals("") || msg.equals("[]") || msg.equals(null)) {
                return "-1";
            } else {
                JSONArray arr = new JSONArray(msg);
                for (int i = 0; i < arr.length(); i++) {
                    msasageJson = arr.getJSONObject(i);
                    msgtype = msasageJson.getString("msg_id");
                    my_msg1 = msasageJson.getString("msg_content");
                    Trace.i("my_msg1=" + my_msg1);
                    String date[] = {};
                    date = msasageJson.getString("msg_date").split(" ");
                    msgdate = date[0];
                    msgtime1 = date[1];
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date date2=new Date();
                    try {
                        date2=sdf.parse(msgtime1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    msgtime=sdf.format(date2);
                    if(my_msg1!=null && !"".equals(my_msg1)){
                        if(my_msg1.contains("@")){
                            serie=my_msg1.split("@")[1];
                            my_msg1 = my_msg1.split("@")[0];
                        }
                        Trace.i("my_msg=" + my_msg);
                        if(my_msg1.equals("0")){
                            my_msg = area +getString(R.string.messtext0)+serie;
                        }else if(my_msg1.equals("1")){
                            my_msg = area +getString(R.string.messtext1)+serie;
                        }else if(my_msg1.equals("2")){
                            my_msg = serie +getString(R.string.messtext2)+area;
                        }else if(my_msg1.equals("3")){
                            my_msg = serie +getString(R.string.messtext3)+area;
                        }else if(my_msg1.equals("4")){
                            my_msg = serie +getString(R.string.messtext4);
                        }else if(my_msg1.equals("5")){
                            my_msg = serie +getString(R.string.messtext5);
                        }else if(my_msg1.equals("6")){
                            my_msg = serie +getString(R.string.messtext6);
                        }else if (my_msg1.equals("7")){
                            my_msg = serie +getString(R.string.messtext7);
                        }
                    }else{
                        continue;
                    }
                    //0表示取消分享
                    //1表示分享
                    //2表示进入围栏
                    //3表示出围栏
                    //4 异常关机
                    //5 手动关机
                    //6 低电压关机
                    //7 开机登陆
                    Trace.i("msgdate=" + msgdate);
                    Trace.i("my_msg=" + my_msg);
                    Trace.i("msgtype=" + msgtype);
                    Trace.i("msgtime=" + msgtime);
                    aa += msgdate + "#" + my_msg + "#" + msgtype + "#"
                            + msgtime + "@zjw@";
                    Trace.i("aa=" + aa);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Trace.i("");
        }

        String cc = aa.substring(0, aa.length() - 5);

        smgDate = cc.split("@zjw@");
        Log.i("result", "smgDate = "+smgDate);

        // smgDate[i] = msgdate + "," + my_msg+"," + msgtype+"," +msgtime;

        return msgOne;

    }

    @Override
    protected void onDestoryActivity() {
        // TODO Auto-generated method stub

    }
    @Override
    protected void doConnectLinkCallback(String result) {
        // TODO Auto-generated method stub
        Log.i("result", "doConnectLinkCallback= "+result);
        Trace.i("doConnectLinkCallback=" + result);
        HashMap<String, Object> map = mProtocolData.getBackResult(result);
        int resultCode = (Integer)map.get("resultCode");
        switch (resultCode) {
            case 1:
                String msg_count = "" + map.get("msg_count");
                String msg_array = (String) map.get("msg_array");
                progressBar.setVisibility(View.GONE);
                Log.i("result", "msg_array   = "+msg_array+"         "+msg_count);
                if (JiexiMsg(msg_array).equals("-1")) {
//				tetxmess.setVisibility(ViewGroup.VISIBLE);
                } else {
//				tetxmess.setVisibility(View.GONE);
                    updatelistDate();
                }

                break;
            case 0: // 请求失败
                progressBar.setVisibility(View.GONE);
                break;
            case -1:   //后台异常
                String exception = "" + map.get("exception");
                progressBar.setVisibility(View.GONE);
                showToast(R.string.exception_code);
                Trace.i("exception++" + exception);
                break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_button:
                finish();
                break;
        }
    }
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getExtras().getString("name");
            initFindView();
        }
    };

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jf504");
        registerReceiver(this.broadcastReceiver, filter);
    }

    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
    }

}
