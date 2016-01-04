package care.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.util.List;

public class DateAdapter extends BaseAdapter {
	private Context context;
	private List<DateText> list;

	public DateAdapter(Context context, List<DateText> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (list == null) {
			return null;
		}
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_time_line, parent, false);
			holder.date = (TextView) convertView
					.findViewById(R.id.txt_date_time);
			holder.content = (TextView) convertView
					.findViewById(R.id.txt_date_content);
			holder.line = (View) convertView.findViewById(R.id.v_line);
			
			holder.title = (RelativeLayout) convertView
					.findViewById(R.id.rl_title);
			
			holder.text_title = (TextView) convertView
					.findViewById(R.id.text_title);
			
			
			holder.text_time = (TextView) convertView
					.findViewById(R.id.text_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//鏃堕棿杞寸珫绾跨殑layout
		LayoutParams params = (LayoutParams) holder.line.getLayoutParams();
		//绗竴鏉℃暟鎹紝鑲畾鏄剧ず鏃堕棿鏍囬
		if (position == 0) {
			holder.title.setVisibility(View.VISIBLE);
			holder.date.setText(list.get(position).getDate());
			params.addRule(RelativeLayout.ALIGN_TOP, R.id.rl_title);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.txt_date_content);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.text_title);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.text_time);
		} else { // 涓嶆槸绗竴鏉℃暟鎹�
			// 鏈潯鏁版嵁鍜屼笂涓�潯鏁版嵁鐨勬椂闂存埑鐩稿悓锛屾椂闂存爣棰樹笉鏄剧ず
			if (list.get(position).getDate()
					.equals(list.get(position - 1).getDate())) {
				holder.title.setVisibility(View.GONE);
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.txt_date_content);
				params.addRule(RelativeLayout.ALIGN_BOTTOM,
						R.id.txt_date_content);
				params.addRule(RelativeLayout.ALIGN_BOTTOM,
						R.id.text_title);
				params.addRule(RelativeLayout.ALIGN_BOTTOM,
						R.id.text_time);
			} else {
				//鏈潯鏁版嵁鍜屼笂涓�潯鐨勬暟鎹殑鏃堕棿鎴充笉鍚岀殑鏃跺�锛屾樉绀烘暟鎹�
				holder.title.setVisibility(View.VISIBLE);
				holder.date.setText(list.get(position).getDate());
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.rl_title);
				params.addRule(RelativeLayout.ALIGN_BOTTOM,
						R.id.txt_date_content);
				params.addRule(RelativeLayout.ALIGN_BOTTOM,
						R.id.text_title);
				params.addRule(RelativeLayout.ALIGN_BOTTOM,
						R.id.text_time);
			}
		}
		holder.line.setLayoutParams(params);
		holder.content.setText(list.get(position).getText());
		holder.text_title.setText(list.get(position).getTextTitle());
		holder.text_time.setText(list.get(position).getTime());
		return convertView;
	}

	public static class ViewHolder {
		RelativeLayout title;
		View line;
		TextView date;
		TextView content;
		TextView text_title;
		TextView text_time;
	}
}
