package care.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.util.ArrayList;

import care.bean.User;
import care.utils.Trace;

public class ManagerAdapter extends BaseAdapter{

	private ArrayList<User> mManagerList;
	private LayoutInflater mInflater;
	private int positionCurrent = -1;
	private Context cx;
	
	public ManagerAdapter(Context mContext){
		this.mInflater = LayoutInflater.from(mContext);
		this.mManagerList = new ArrayList<User>();
		this.cx = mContext;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mManagerList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mManagerList.get(position);
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
			convertView = mInflater.inflate(R.layout.manager_list_item, parent, false);
			viewHolder.manager_icon_id = (ImageView)convertView.findViewById(R.id.manager_icon_id);
			viewHolder.manager_name_id = (TextView)convertView.findViewById(R.id.manager_name_id);
			viewHolder.manager_phone_id = (TextView)convertView.findViewById(R.id.manager_phone_id);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		User tmp = mManagerList.get(position);
//		String baobei_manager_icon_string = tmp.getBaoBeiUrl();
		String manager_name_string = tmp.getUserName();
		String manager_nick_name = tmp.getNickName();
		String manager_head = tmp.getUserHead();
		viewHolder.manager_icon_id.setImageResource(R.drawable.head_baby1);
		viewHolder.manager_name_id.setText(manager_name_string);
		viewHolder.manager_phone_id.setText(manager_nick_name);
		Trace.i("manager nick name==" + manager_nick_name);
		return convertView;
	}

	private static final class ViewHolder {
		TextView manager_phone_id;
		TextView manager_name_id;
		ImageView manager_icon_id;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		notifyDataSetChanged();
	}

	public void setDataList(ArrayList<User> mDataList) {
		// TODO Auto-generated method stub
		this.mManagerList = mDataList;
	}

	public void setBaoBeiInfoPosition(int position, User tmp) {
		// TODO Auto-generated method stub
		if(mManagerList.size() > 0){
			setCurrentPosition(position);
			mManagerList.set(position, tmp);
		}
	}
	
	public void setCurrentPosition(int positionCurrent){
		this.positionCurrent = positionCurrent;
	}
}
