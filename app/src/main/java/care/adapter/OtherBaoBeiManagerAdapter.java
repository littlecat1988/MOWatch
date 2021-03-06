package care.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.mtk.btnotification.R;

import care.BaoBeiManagerActivity;
import care.bean.BaoBeiBean;
import care.utils.XcmTools;

public class OtherBaoBeiManagerAdapter extends BaseAdapter{

	private SparseArray<BaoBeiBean> mBaoBeiDataList;
	private LayoutInflater mInflater;
	private int positionCurrent = -1;
	private Context cx;
	private XcmTools tools;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String currentOperationId;
	private BaoBeiManagerActivity activity;

	public OtherBaoBeiManagerAdapter(Context mContext,XcmTools tools,ImageLoader imageLoader,DisplayImageOptions options, BaoBeiManagerActivity activity){
		this.cx = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mBaoBeiDataList = new SparseArray<BaoBeiBean>();
		this.tools = tools;
		this.imageLoader = imageLoader;
		this.options = options;
		this.activity = activity;
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
			convertView = mInflater.inflate(R.layout.baobei_manager_list_item2, parent, false);
			viewHolder.baobei_manager_icon_id = (ImageView)convertView.findViewById(R.id.baobei_manager_icon_id);
			viewHolder.baobei_manager_name_id = (TextView)convertView.findViewById(R.id.baobei_manager_name_id);
			viewHolder.baobei_manager_phone_id = (TextView)convertView.findViewById(R.id.baobei_manager_phone_id);
			viewHolder.is_current_device = (TextView)convertView.findViewById(R.id.is_current_device);
			viewHolder.switch_button = (TextView)convertView.findViewById(R.id.switch_button);
			viewHolder.switch_button.setOnClickListener(new View.OnClickListener() {

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
											activity.refreshCurrentDevice();
//											refreshCurrentDevice();

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
		String baobei_head=tmp.getPhoto();
		String baobei_manager_name_string = tmp.getName();
		String baobei_manager_phone_string = tmp.getPhone();
//		final String from_user = tmp.getFromUser();
		viewHolder.baobei_manager_icon_id.setImageResource(R.drawable.head_baby1);
		viewHolder.baobei_manager_name_id.setText(baobei_manager_name_string);
		viewHolder.baobei_manager_phone_id.setText(baobei_manager_phone_string);
		imageLoader.displayImage(tmp.getPhoto(), viewHolder.baobei_manager_icon_id,
				options);
		viewHolder.switch_button.setId(position);
		if(tmp.isCurrent()){
			viewHolder.is_current_device.setVisibility(View.VISIBLE);
			viewHolder.switch_button.setVisibility(View.GONE);
		}else{
			viewHolder.is_current_device.setVisibility(View.GONE);
			viewHolder.switch_button.setVisibility(View.VISIBLE);
		}
		/*viewHolder.baby_manager_share_agree.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				TextView text=new TextView(cx);
				String alarm = cx.getString(R.string.share_alarm1)+from_user+cx.getString(R.string.share_alarm2);
				text.setText(alarm);
				AlertDialog dialog=new AlertDialog.Builder(cx).setTitle(null).setView(text).setPositiveButton(R.string.share_agreed, new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface arg0, int arg1) {

    				}
    			}).setNegativeButton(R.string.share_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).show();
    	    	dialog.setCanceledOnTouchOutside(false);
			}});*/
		return convertView;
	}

	private static final class ViewHolder {
		TextView baobei_manager_phone_id;
		TextView baobei_manager_name_id;
		ImageView baobei_manager_icon_id;
		TextView is_current_device;
		TextView switch_button;
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
		notifyDataSetChanged();
	}
}
