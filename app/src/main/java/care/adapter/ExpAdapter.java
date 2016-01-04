package care.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtk.btnotification.R;
import care.widget.IphoneTreeView.IphoneTreeHeaderAdapter;

import java.util.HashMap;

import care.bean.FriendBean;
import care.widget.IphoneTreeView;


public class ExpAdapter extends BaseExpandableListAdapter implements
		IphoneTreeHeaderAdapter{

	private SparseArray<SparseArray<FriendBean>> mChildDataList;  //孩子的数据
	private SparseArray<String> mGroupDataList;   //群的数据

	private HashMap<Integer, Integer> groupStatusMap;
	private Context context;

	private IphoneTreeView mIphoneTreeView;
	public ExpAdapter(Context context,
					  SparseArray<String> mGroupDataList,
					  SparseArray<SparseArray<FriendBean>> mChildDataList,
					  IphoneTreeView mIphoneTreeView){

		this.context = context;
		this.mIphoneTreeView = mIphoneTreeView;
		this.mGroupDataList = mGroupDataList;
		this.mChildDataList = mChildDataList;
		this.groupStatusMap = new HashMap<Integer, Integer>();
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mChildDataList.get(groupPosition).get(childPosition);  //获取孩子的对象
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildHolder mChildHolder = null;
		if(convertView == null){
			mChildHolder = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.qinqing_constact_child, null);
			mChildHolder = new ChildHolder();
			mChildHolder.contact_name = (TextView) convertView.findViewById(R.id.contact_name);
			mChildHolder.contact_phone = (TextView) convertView.findViewById(R.id.contact_phone);
			mChildHolder.contact_icon = (ImageView) convertView.findViewById(R.id.contact_icon);

			convertView.setTag(mChildHolder);
		}else{
			mChildHolder = (ChildHolder)convertView.getTag();
		}

		FriendBean friendBean = mChildDataList.get(groupPosition).get(childPosition);
		String friendUrl = friendBean.getFriendUrl();
		String friendName = friendBean.getFriendName();
		String friendPhone = friendBean.getFriendPhone();
		int id = friendBean.getId();

		mChildHolder.contact_name.setText(friendName);
		mChildHolder.contact_phone.setText(friendPhone);
		if(!"".equals(friendUrl) && !"0".equals(friendUrl)){

		}else{
			mChildHolder.contact_icon.setImageResource(R.drawable.device_head_normal);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mChildDataList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mGroupDataList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mGroupDataList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.qinqing_constact_group, null);
			holder = new GroupHolder();
			holder.group_name = (TextView) convertView.findViewById(R.id.group_name);
			holder.right_icon = (ImageView) convertView.findViewById(R.id.group_indicator);

			convertView.setTag(holder);
		}else{
			holder = (GroupHolder) convertView.getTag();
		}
		String groupName = mGroupDataList.get(groupPosition);
		holder.group_name.setText(groupName);

		if(isExpanded){  //若打开
			holder.right_icon.setImageResource(R.drawable.qb_down);
		}else{
			holder.right_icon.setImageResource(R.drawable.qb_right);
		}
		return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			//mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !mIphoneTreeView.isGroupExpanded(groupPosition)) {
			//mSearchView.setVisibility(View.VISIBLE);
			return PINNED_HEADER_GONE;
		} else {
			//mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureTreeHeader(View header, int groupPosition,
									int childPosition, int alpha) {
		// TODO Auto-generated method stub
		String groupName = mGroupDataList.get(groupPosition);
		TextView tmp = (TextView) header.findViewById(R.id.group_name);
		tmp.setText(groupName);
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		// TODO Auto-generated method stub
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		// TODO Auto-generated method stub
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	class ChildHolder {
		TextView contact_name;
		TextView contact_phone;
		ImageView contact_icon;
	}

	class GroupHolder {
		TextView group_name;
		ImageView right_icon;
	}
}
