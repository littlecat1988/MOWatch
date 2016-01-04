package care.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.util.ArrayList;

import care.bean.Circle;

public class FenceListAdapter extends BaseAdapter {
	private Context cx;
	private ArrayList<Circle> circleList;
	
	public FenceListAdapter(Context context,ArrayList<Circle> circleList){
		this.cx=context;
		this.circleList=circleList;
	}

	@Override
	public int getCount() {
		return circleList.size();
	}

	@Override
	public Object getItem(int position) {
		return circleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(cx).inflate(R.layout.fence_item, null);
			viewHolder=new ViewHolder();
			viewHolder.fenceName=(TextView)convertView.findViewById(R.id.fence_name);
			viewHolder.fenceAddress=(TextView)convertView.findViewById(R.id.fence_address);
			viewHolder.whoSet=(TextView)convertView.findViewById(R.id.who_set);
			viewHolder.fenceRadius=(TextView)convertView.findViewById(R.id.fence_radius);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		Circle circle=circleList.get(position);
		viewHolder.fenceName.setText(circle.getName());
		viewHolder.fenceAddress.setText(circle.getAddr());
		viewHolder.fenceRadius.setText(cx.getString(R.string.circle_range)+circle.getRadius()+"m");
		return convertView;
	}
	public class ViewHolder{
		public TextView fenceName;
		public TextView fenceAddress;
		public TextView whoSet;
		public TextView fenceRadius;
	}
}
