package care.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.mtk.btnotification.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import care.ActivityInvitePeople;
import care.BaoBeiManagerActivity;
import care.bean.BaoBeiBean;
import care.utils.BeanUtils;
import care.utils.Constants;
import care.utils.Utils;
import care.utils.XcmTools;

public class BaoBeiManagerAdapter extends BaseAdapter{

	private SparseArray<BaoBeiBean> mBaoBeiDataList;
	private LayoutInflater mInflater;
	private int positionCurrent = -1;
	private Context cx;
	private Handler mHandler;
	private XcmTools tools;
	private String currentOperationId;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private BaoBeiManagerActivity activity;

	public BaoBeiManagerAdapter(Context mContext,XcmTools tools,ImageLoader imageLoader,DisplayImageOptions options, BaoBeiManagerActivity activity){
		this.mInflater = LayoutInflater.from(mContext);
		this.mBaoBeiDataList = new SparseArray<BaoBeiBean>();
		this.cx = mContext;
		this.tools = tools;
		this.imageLoader = imageLoader;
		this.options = options;
		this.activity = activity;
		mHandler=new Handler();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBaoBeiDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBaoBeiDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.baobei_manager_list_item, parent, false);
			viewHolder.baobei_manager_icon_id = (ImageView)convertView.findViewById(R.id.baobei_manager_icon_id);
			viewHolder.baobei_manager_name_id = (TextView)convertView.findViewById(R.id.baobei_manager_name_id);
			viewHolder.baobei_manager_phone_id = (TextView)convertView.findViewById(R.id.baobei_manager_phone_id);
			viewHolder.is_current_device = (TextView)convertView.findViewById(R.id.is_current_device);
			viewHolder.device_share_button = (TextView)convertView.findViewById(R.id.share_button);
			viewHolder.device_share_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					BaoBeiBean baobei=mBaoBeiDataList.get(v.getId());
					Intent i = new Intent(cx, ActivityInvitePeople.class);
					Bundle bundle=new Bundle();
					bundle.putString("babyImei", baobei.getImei());
					bundle.putString("babyName", baobei.getName());
					bundle.putString("babyPhone", baobei.getPhone());
					bundle.putString("babyPhoto", baobei.getPhoto());
					i.putExtras(bundle);
					cx.startActivity(i);
				}
			});
			viewHolder.device_unbond_button = (TextView)convertView.findViewById(R.id.switch_button);
			viewHolder.device_unbond_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					BaoBeiBean baobei=mBaoBeiDataList.get(v.getId());
					currentOperationId=baobei.getImei();
					TextView view = new TextView(cx);
					view.setText(cx.getString(R.string.set_baby_now));
					new AlertDialog.Builder(cx)
							.setTitle(cx.getString(R.string.alert))
							.setView(view)
							.setPositiveButton(cx.getString(R.string.ok),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0,
															int arg1) {

//											new Thread(mRunnable).start();
//											BaoBeiBean baobei=mDataList.get(position);
											tools.set_current_device_id(currentOperationId);
//											refreshCurrentDevice();
											activity.refreshCurrentDevice();
										}
									})
							.setNegativeButton(cx.getString(R.string.cancel),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0,
															int arg1) {

										}

									}).show();
				}
			});
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}

		BaoBeiBean tmp = mBaoBeiDataList.get(position);
//		String baobei_manager_icon_string = tmp.getBaoBeiUrl();
		String baobei_manager_name_string = tmp.getName();
		String baobei_manager_phone_string = tmp.getPhone();
		String head_url = tmp.getPhoto();
		String baobei_manager_select_string = tmp.getBaoBeiSelect();
		imageLoader.displayImage(tmp.getPhoto(), viewHolder.baobei_manager_icon_id,
				options);
//		viewHolder.baobei_manager_icon_id.setImageResource(R.drawable.head_baby1);
		viewHolder.baobei_manager_name_id.setText(baobei_manager_name_string);
		viewHolder.baobei_manager_phone_id.setText(baobei_manager_phone_string);
		viewHolder.device_share_button.setId(position);
		viewHolder.device_unbond_button.setId(position);
		if(tmp.isCurrent()){
			viewHolder.is_current_device.setVisibility(View.VISIBLE);
			viewHolder.device_unbond_button.setVisibility(View.GONE);
		}else{
			viewHolder.is_current_device.setVisibility(View.GONE);
			viewHolder.device_unbond_button.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private static final class ViewHolder {
		TextView baobei_manager_phone_id;
		TextView baobei_manager_name_id;
		ImageView baobei_manager_icon_id;
		TextView is_current_device;
		TextView device_share_button;
		TextView device_unbond_button;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		notifyDataSetChanged();
	}

	public void setDataList(SparseArray<BaoBeiBean> mDataList) {
		// TODO Auto-generated method stub
		this.mBaoBeiDataList = mDataList;
	}

	public void setBaoBeiInfoPosition(int position, BaoBeiBean tmp) {
		// TODO Auto-generated method stub
		if(mBaoBeiDataList.size() > 0){
			setCurrentPosition(position);
			mBaoBeiDataList.setValueAt(position, tmp);
		}
	}

	public void setCurrentPosition(int positionCurrent){
		this.positionCurrent = positionCurrent;
	}

	String unbind(String m) {
		String p_sid = tools.get_user_id();
		String p_imei = currentOperationId;
		JSONObject json_unbind = new JSONObject();
		try {
			json_unbind.put("user_id", p_sid);
			json_unbind.put("device_imei", p_imei);
			String json_download_result = Utils.GetService(json_unbind, Constants.BABYDELETE);
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

	Runnable mRunnable=new Runnable(){

		@Override
		public void run() {
			final String add_result = unbind("");
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					unbindResultCheck(add_result);
				}
			});

		}

	};

	private void unbindResultCheck(String result_check) {

		if (result_check.equals("0") || result_check.equals("-1")) {

		} else {
			HashMap<String, String> map;
			try {
				map = BeanUtils.getJSONParserResult(result_check);
				String result=map.get("resultCode");
				if("1".equals(result)){
					for(int i=0;i<mBaoBeiDataList.size();i++){
						BaoBeiBean baobei=mBaoBeiDataList.get(i);
						String imei=baobei.getImei();
						if(imei.equals(currentOperationId)){
							mBaoBeiDataList.remove(i);
							if(imei.equals(tools.get_current_device_id())){
								tools.set_current_device_id("");
							}
							break;
						}
					}
					tools.set_babyList("");
					notifyDataSetChanged();
					Toast.makeText(cx, cx.getString(R.string.unbind_success), Toast.LENGTH_SHORT).show();
				}else if("0".equals(result)){
					Toast.makeText(cx, cx.getString(R.string.unbind_failed), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void refreshCurrentDevice(){
		for(int i=0;i<mBaoBeiDataList.size();i++){
			BaoBeiBean baobei=mBaoBeiDataList.get(i);
			String imei=baobei.getImei();
			if(imei.equals(tools.get_current_device_id())){
				baobei.setCurrent(true);
			}else{
				baobei.setCurrent(false);
			}
		}
/*		for(int i=0;i<mSharedDataList.size();i++){
			BaoBeiBean baobei=mSharedDataList.get(i);
			String imei=baobei.getImei();
			if(imei.equals(tools.get_current_device_id())){
				baobei.setCurrent(true);
			}else{
				baobei.setCurrent(false);
			}
		}*/
		notifyDataSetChanged();
	}
}
