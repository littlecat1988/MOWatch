package care;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mtk.btnotification.R;
import com.xcm.ui.HorizontalListView;

import care.fragment.PictureSelectFragment.PictureSelectInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import care.fragment.PictureSelectFragment;
import care.picturehead.CropHandler;
import care.picturehead.CropHelper;
import care.picturehead.CropParams;
import care.utils.CommonBaseActivity;
import care.utils.Constants;
import care.utils.Utils;
import care.widget.CircularImage;

public class PersonInfoActivity extends CommonBaseActivity implements
        OnClickListener, CropHandler, PictureSelectInterface {

    private CircularImage person_photo;
    private Calendar c;
    private LinearLayout person_name;
    private LinearLayout person_sex;
    private LinearLayout person_birthday;
    private LinearLayout person_height;
    private LinearLayout person_width;

    private TextView person_name_text;
    private TextView person_sex_text;
    private TextView person_birthday_text;
    private TextView person_height_text;
    private TextView person_width_text;
    private TextView title_string;

    private LinearLayout progressBar;
    private TextView progress_text;

    private boolean Sextype = true;

    private CropParams mCropParams;
    private PictureSelectFragment picture;
    private FragmentManager fm;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.person_info_main);
        fm = getFragmentManager();
        mCropParams = new CropParams("1");
    }

    @Override
    protected void initFindView() {
        // TODO Auto-generated method stub
        title_string = (TextView) findViewById(R.id.title_string);
        person_photo = (CircularImage) findViewById(R.id.person_photo);
        titleString.setText(R.string.baobei_info_string);
        right_txt.setVisibility(View.VISIBLE);
        right_txt.setText(R.string.save_string);
        person_name_text = (TextView) findViewById(R.id.person_name_text);
        person_sex_text = (TextView) findViewById(R.id.person_sex_text);
        person_birthday_text = (TextView) findViewById(R.id.person_birthday_text);
        person_height_text = (TextView) findViewById(R.id.person_height_text);
        person_width_text = (TextView) findViewById(R.id.person_width_text);

        person_name = (LinearLayout) findViewById(R.id.person_name);
        person_sex = (LinearLayout) findViewById(R.id.person_sex);
        person_birthday = (LinearLayout) findViewById(R.id.person_birthday);
        person_height = (LinearLayout) findViewById(R.id.person_height);
        person_width = (LinearLayout) findViewById(R.id.person_width);

        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        progress_text = (TextView) findViewById(R.id.progress_text);
        c = Calendar.getInstance();
        setOnClickListener();
        init();
    }

    String getUserSex(String sexString) {
        if (sexString.equals("0")) {
            return "男";
        } else {
            return "女";
        }

    }

    void initperson() {

        String personDate[] = tools.get_person().split(",");

        System.out.println("text03 = " + tools.get_person() + "leng "
                + personDate.length);

        if (personDate.length > 5) {
            // person_photo.setImageBitmap(Utils.stringtoBitmap(personDate[0]));
            imageLoader.displayImage(personDate[0], person_photo, options);
            person_name_text.setText(personDate[1]);
            person_sex_text.setText(getUserSex(personDate[2]));
            person_birthday_text.setText(personDate[3]);
            person_height_text.setText(personDate[4]);
            person_width_text.setText(personDate[5]);
        }

    }

    void init() {
        title_string.setText(getString(R.string.pserson_title));
        progress_text.setText(R.string.loading);
        initperson();

        // UserInfo userInfo = new UserInfo();
        // String user_sex = userInfo.getUserSex();
        // String user_nick = userInfo.getUserNickName();
        // String user_age = userInfo.getUserBirthday();
        // String user_height = userInfo.getUserHeight();
        // String user_weight = userInfo.getUserWeight();
        // System.out.println("ceshi01 " +
        // mUpdateDB.queryDataToBases(UserInfo.class, new
        // String[]{Constants.USERID}, new String[]{UserInfo.USER_ID}, null,
        // false));

        // System.out.println("ceshi 02 " + tmp);

        // person_photo.setImageBitmap(Utils.stringtoBitmap(userInfo.getUserHeadUrl()));
        // person_name_text.setText(userInfo.getUserNickName());
        // person_sex_text.setText(userInfo.getUserSex());
        // person_birthday_text.setText(userInfo.getUserBirthday());
        // person_height_text.setText(userInfo.getUserHeight());
        // person_width_text.setText(userInfo.getUserWeight());

        // System.out.println("user_sex" + user_sex + "\n"
        // + "user_nick " + user_nick + "\n"
        // + "user_age " + user_age + "\n"
        // + "user_height "+ user_height + "\n"
        // + "user_weight " + user_weight + "\n"
        // );

        // String babyList = tools.get_babyList();

        // System.out.println("cc = " + babyList);
        // try {
        // JSONArray babyArray=new JSONArray(babyList);
        // int length=babyArray.length();
        // String currentId=tools.get_current_device_id();
        // for(int i=0;i<length;i++){
        // JSONObject babyObject=(JSONObject)babyArray.get(i);
        // HashMap<String,String>
        // babyMap=BeanUtils.getJSONParserResult(babyObject.toString());
        // BaoBeiBean baobei=BeanUtils.getBaoBei(babyMap);
        // if(baobei.getImei().equals(currentId)){
        // currentBaby=baobei;
        // break;
        // }
        // }
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }
        // if(currentBaby!=null){
        // baby_name_text.setText(currentBaby.getName());
        // String sex="";
        //
        // if("0".equals(currentBaby.getSex())){
        // sex="男";
        // }else if("1".equals(currentBaby.getSex())){
        // sex="女";
        // }
        // baby_sex_text.setText(sex);
        // String birthDay=currentBaby.getBirthDay();
        // String birthDayFormat="";
        // if(birthDay!=null&&!"".equals(birthDay)){
        // if(birthDay.contains(" ")){
        // birthDayFormat=birthDay.split(" ")[0];
        // }
        // }
        // baby_birthday_text.setText(birthDayFormat);
        // baby_height_text.setText(currentBaby.getHeight());
        // baby_width_text.setText(currentBaby.getWeight());;
        // baby_phone_text.setText(currentBaby.getPhone());
        // baby_call_text.setText("");
        // }
    }

    public void onStart() {
        super.onStart();
    }

    private void setOnClickListener() {
        // TODO Auto-generated method stub
        person_name = (LinearLayout) findViewById(R.id.person_name);
        person_sex = (LinearLayout) findViewById(R.id.person_sex);
        person_birthday = (LinearLayout) findViewById(R.id.person_birthday);
        person_height = (LinearLayout) findViewById(R.id.person_height);
        person_width = (LinearLayout) findViewById(R.id.person_width);

        person_name.setOnClickListener(this);
        person_sex.setOnClickListener(this);
        person_birthday.setOnClickListener(this);
        person_height.setOnClickListener(this);
        person_width.setOnClickListener(this);
        person_photo.setOnClickListener(this);

        right_txt.setOnClickListener(this);
    }

    @Override
    protected void onDestoryActivity() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.person_photo:
                picture = PictureSelectFragment.getInstance(this, R.string.update_head_image_string, R.string.xiangji_string, R.string.xiangce_string);
                picture.show(fm, "picture_dialog");
                break;
            case R.id.person_name:
                showNameSettingDialog();
                break;
            case R.id.person_sex:
                showSexSettingDialog();
                break;
            case R.id.person_birthday:
                showBirthdayDialog();
                break;
            case R.id.person_height:
                showHeightDialog2();
//			showHeightSettingDialog();
                break;
            case R.id.person_width:
                showWidthDialog2();
//			showWidthSettingDialog();
                break;

            case R.id.right_txt:
                if (Constants.IS_OPEN_NETWORK) {
                    SetpersonDataToBack(Utils.bitmaptoString(5, person_photo),
                            person_name_text.getText().toString().trim(),
                            person_sex_text.getText().toString().trim(),
                            person_birthday_text.getText().toString().trim(),
                            person_height_text.getText().toString().trim(),
                            person_width_text.getText().toString().trim());

                    progressBar.setVisibility(View.VISIBLE);


                } else {
                    showToast(R.string.network_error);
                }
                break;

        }
    }

    String get_sex(String sexString) {
        if (sexString.equals("男")) {
            return "0";
        } else {
            return "1";
        }
    }

    /**
     * 与后台通讯的方法
     */
    private void SetpersonDataToBack(String head, String name, String sex,
                                     String birthday, String height, String width) {
        // TODO Auto-generated method stub
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", tools.get_user_id());
        map.put("user_head", head);
        map.put("user_name", Constants.transUTF(name));
        map.put("user_sex", get_sex(sex));
        map.put("user_age", birthday);
        map.put("user_height", height);
        map.put("user_weight", width);
        String mJr = mProtocolData.transFormToJson(map);
        String uRl = Constants.PERSONETTING;

        new ConnectToLinkTask().execute(uRl, mJr);
    }

    protected void doConnectLinkCallback(String result) {
        // TODO Auto-generated method stub
        HashMap<String, Object> map = mProtocolData.getBackResult(result);
        int resultCode = (Integer) map.get("resultCode");
        System.out.println("用户结果= " + resultCode);
        progressBar.setVisibility(View.GONE);
        switch (resultCode) {
            case 1: // 成功

                showToast(R.string.other1);
                // String toPerson = Utils.bitmaptoString(5, person_photo)+","+
                String toPerson = map.get("user_head") + ","
                        + person_name_text.getText().toString().trim() + ","
                        + person_sex_text.getText().toString().trim() + ","
                        + person_birthday_text.getText().toString().trim() + ","
                        + person_height_text.getText().toString().trim() + ","
                        + person_width_text.getText().toString().trim();
                tools.set_person(toPerson);

                System.out.println("user head = " + map.get("user_head"));

                finish();
                break;
            case 0: // 失败
                showToast(R.string.other2);
                break;
            case -1: // 异常
                String exception = "" + map.get("exception");
                showToast(R.string.exception_code);

                break;
            case -2: // 绑定不合法
                showToast(R.string.other3);
                break;

            case -3: // 已经被绑定
                // String exception = "" + map.get("exception");
                showToast(R.string.other4);

                // Trace.i("exception++" + exception);
                break;
            case -6:
                showToast(R.string.link_chaoshi_code);
                break;
        }
    }

    public void showNameSettingDialog() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_baby_name, null);
        final Button yes = (Button) view.findViewById(R.id.yes);
        final Button no = (Button) view.findViewById(R.id.no);
        final EditText name = (EditText) view.findViewById(R.id.name);
        name.setText(person_name_text.getText().toString());

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .create();

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
                person_name_text.setText(name.getText().toString());
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // setDialogWidth(dialog, 0.8);
    }

    boolean getSex() {
        if (person_sex_text.getText().toString().equals("男")) {
            // System.out.println("cc = " +
            // person_sex_text.getText().toString());
            return true;
        } else {
            return false;
        }

    }

    void setSex(boolean type) {
        if (type) {
            person_sex_text.setText("男");
        } else {
            person_sex_text.setText("女");
        }

    }

    String guolvBirthdayDate(int i) {

        String old = person_birthday_text.getText().toString();
//		String old = "2015-05-07-08";

        String result = "";
        String[] date = {};
        if (old.equals("") || old.equals(null)) {
            date = "0-0-0".split("-");
        } else {
            date = old.split("-");
        }

        switch (i) {

            case 1:
                result = date[0];
                break;
            case 2:
                result = date[1];
                break;

            case 3:
                result = date[2];
                break;
        }
        return result;

    }

    public void showBirthdayDialog() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker arg0, int year, int monthOfYear,
                                  int dayOfMonth) {
                String date = "";
                String Month = "";
                String day = "";

                if (monthOfYear > 8) {
                    Month = String.valueOf((monthOfYear + 1));
                } else {
                    Month = "0" + String.valueOf((monthOfYear + 1));
                }

                if (dayOfMonth > 8) {
                    day = String.valueOf(dayOfMonth);
                } else {
                    day = "0" + String.valueOf(dayOfMonth);
                }

                String birthDay = year + "-" + Month + "-" + day;

                person_birthday_text.setText(birthDay);

                // if (!birthDay.equals(personSetValues[3])) {
                // personSetting.setVisibility(View.VISIBLE);
                // }
                // personSetValues[3] = birthDay;
                // personSetAdapter.notifyDataSetChanged();
            }
        }, Integer.valueOf(guolvBirthdayDate(1)).intValue(), Integer.valueOf(guolvBirthdayDate(2)).intValue() - 1, Integer.valueOf(guolvBirthdayDate(3)).intValue())
                .show();

    }

    public void showSexSettingDialog() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_baby_sex, null);
        final Button yes = (Button) view.findViewById(R.id.yes);
        final Button no = (Button) view.findViewById(R.id.no);
        final LinearLayout lin_b = (LinearLayout) view.findViewById(R.id.lin_b);
        final LinearLayout lin_g = (LinearLayout) view.findViewById(R.id.lin_g);
        final ImageView b_img = (ImageView) view.findViewById(R.id.b_img);
        final ImageView g_img = (ImageView) view.findViewById(R.id.g_img);

        if (getSex()) {
            lin_b.setBackgroundResource(R.drawable.sex_b);
            lin_g.setBackgroundResource(R.drawable.sex_no);
            b_img.setImageResource(R.drawable.sex_nan_p);
            g_img.setImageResource(R.drawable.sex_nv_n);
            Sextype = true;
        } else {
            lin_b.setBackgroundResource(R.drawable.sex_no);
            lin_g.setBackgroundResource(R.drawable.sex_g);
            b_img.setImageResource(R.drawable.sex_nan_n);
            g_img.setImageResource(R.drawable.sex_nv_p);
            Sextype = false;
        }

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .create();

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
                setSex(Sextype);
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
                dialog.dismiss();

            }
        });

        lin_b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                lin_b.setBackgroundResource(R.drawable.sex_b);
                lin_g.setBackgroundResource(R.drawable.sex_no);
                b_img.setImageResource(R.drawable.sex_nan_p);
                g_img.setImageResource(R.drawable.sex_nv_n);
                Sextype = true;
            }
        });

        lin_g.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                lin_b.setBackgroundResource(R.drawable.sex_no);
                lin_g.setBackgroundResource(R.drawable.sex_g);
                b_img.setImageResource(R.drawable.sex_nan_n);
                g_img.setImageResource(R.drawable.sex_nv_p);
                Sextype = false;
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // setDialogWidth(dialog, 0.8);
    }

    public void showHeightSettingDialog() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_baby_height, null);
        final Button yes = (Button) view.findViewById(R.id.yes);
        final Button no = (Button) view.findViewById(R.id.no);
        final TextView number = (TextView) view.findViewById(R.id.number);
        number.setText(person_height_text.getText().toString());
        final HorizontalListView horizontalListView = (HorizontalListView) view
                .findViewById(R.id.testlistview);
        List<Map<String, Object>> list = getData();
        horizontalListView.setAdapter(new FriendSystemInfoAdspter(this, list));

        // ;

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .create();

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Toast.makeText(BaoBeiInfoActivity.this, "也是", 500).show();
                person_height_text.setText(number.getText().toString());
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
                dialog.dismiss();

            }
        });

        horizontalListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
                // dialog.dismiss();
                number.setText(String.valueOf(position));
                System.out.println(horizontalListView.getLastVisiblePosition());

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // setDialogWidth(dialog, 0.8);
    }

    public void showWidthSettingDialog() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_baby_width, null);
        final Button yes = (Button) view.findViewById(R.id.yes);
        final Button no = (Button) view.findViewById(R.id.no);
        final TextView number = (TextView) view.findViewById(R.id.number);
        number.setText(person_width_text.getText().toString());
        final HorizontalListView horizontalListView = (HorizontalListView) view
                .findViewById(R.id.testlistview);
        List<Map<String, Object>> list = getData();
        horizontalListView.setAdapter(new FriendSystemInfoAdspter(this, list));

        // ;

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .create();

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Toast.makeText(BaoBeiInfoActivity.this, "也是", 500).show();
                person_width_text.setText(number.getText().toString());
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
                dialog.dismiss();

            }
        });

        horizontalListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
                // dialog.dismiss();
                number.setText(String.valueOf(position));
                System.out.println(horizontalListView.getLastVisiblePosition());

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // setDialogWidth(dialog, 0.8);
    }

    public List<Map<String, Object>> getData() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("number_list_text1", String.valueOf(i));
            list.add(map);

        }

        return list;
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

        /**
         * ������ϣ���Ӧlist.xml�еĿؼ�
         *
         * @author Administrator
         */
        public final class Zujian {
            public TextView number_list_text1;

        }

        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * ���ĳһλ�õ����
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        /**
         * ���Ψһ��ʶ
         */
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
                        .inflate(R.layout.number_list, null);

                zujian.number_list_text1 = (TextView) convertView
                        .findViewById(R.id.number_list_text1);

                convertView.setTag(zujian);
            } else {
                zujian = (Zujian) convertView.getTag();
            }

            zujian.number_list_text1.setText((String) data.get(position).get(
                    "number_list_text1"));

            return convertView;
        }
    }

    private void setDialogWidth(AlertDialog dialog, double e) {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialog.getWindow()
                .getAttributes();
        p.width = ((int) (d.getWidth() * e));
        dialog.getWindow().setAttributes(p);
    }

//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//		switch (requestCode) {
//		case 0:
//			super.onActivityResult(requestCode, resultCode, data);
//			Bundle bundle = data.getExtras();
//			String gameviString = bundle.getString("bitmapUrl");
//			// System.out.println("cc = " + gameviString);
//			if (gameviString.equals("") || gameviString.equals(null)
//					|| gameviString.equals("no")) {
//
//			} else {
//				person_photo.setImageBitmap(Utils.stringtoBitmap(gameviString));
//			}
//			break;
//
//		default:
//			break;
//		}
//
//	}

    public void showWidthDialog2() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_baby_phone2, null);
        final Button yes = (Button) view.findViewById(R.id.yes);
        final Button no = (Button) view.findViewById(R.id.no);
        final EditText name = (EditText) view.findViewById(R.id.name);
        name.setText(person_width_text.getText().toString());

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .create();

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                person_width_text.setText(name.getText().toString());
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // setDialogWidth(dialog, 0.8);
    }


    public void showHeightDialog2() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_baby_phone1, null);
        final Button yes = (Button) view.findViewById(R.id.yes);
        final Button no = (Button) view.findViewById(R.id.no);
        final EditText name = (EditText) view.findViewById(R.id.name);
        name.setText(person_height_text.getText().toString());

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .create();

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Toast.makeText(BaoBeiInfoActivity.this, "是也", 500).show();
                person_height_text.setText(name.getText().toString());
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(BaoBeiInfoActivity.this, "不是", 500).show();
                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // setDialogWidth(dialog, 0.8);
    }

    @Override
    public void onPictureSelectXiangji() {
        // TODO Auto-generated method stub
        onDeleteCache();
        Intent intent = CropHelper.buildCaptureIntent(mCropParams.uri);
        startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
    }

    @Override
    public void onPictureSelectXiangce() {
        // TODO Auto-generated method stub
        onDeleteCache();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //4.4版本以上
            startActivityForResult(CropHelper.buildCropFromGalleryIntent(mCropParams), CropHelper.SELECET_A_PICTURE_AFTER_KIKAT);
        } else {
            startActivityForResult(CropHelper.buildCropFromGalleryIntent(mCropParams), CropHelper.REQUEST_CROP);
        }
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        // TODO Auto-generated method stub
        picture.dismiss();
//		onHeadUrlUpload();   //ͷ���ϴ��ӿ���ʱ��д
        onSuccess(uri);
    }

    @Override
    public void onCropCancel() {
        // TODO Auto-generated method stub
        picture.dismiss();
        showToast(R.string.photo_huoqu_cancle);
    }

    @Override
    public void onCropFailed(String message) {
        // TODO Auto-generated method stub
        picture.dismiss();
        showToast(R.string.photo_huoqu_fail);
    }

    @Override
    public CropParams getCropParams() {
        // TODO Auto-generated method stub
        return mCropParams;
    }

    @Override
    public Activity getContext() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropHelper.REQUEST_CAMERA:
            case CropHelper.REQUEST_CROP:
            case CropHelper.SELECET_A_PICTURE_AFTER_KIKAT:
                CropHelper.handleResult(this, requestCode, resultCode, data);
                break;
        }
    }
    private void onSuccess(Uri uri) {
        // TODO Auto-generated method stub
        person_photo.setImageURI(uri);
    }
    private void onDeleteCache() {
        if (CropHelper.clearCachedCropFile(mCropParams.uri)) {
            mCropParams = new CropParams("1" + "_" + Constants.getCurrentTimeLong() + ".jpg");
        }
    }
}
