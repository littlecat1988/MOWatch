package care.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import care.bean.OffLineCountBean;

public class ContactListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
    private List<Map<String, String>> list;
    private Context context;
    
    private ArrayList<OffLineCountBean> mlist;
    
    public ContactListAdapter(Context context,List<Map<String, String>> list){
    	this.context=context;
    	this.list=list;
    	inflater=LayoutInflater.from(context);
    	List<String> list2 = new ArrayList<String>();
    	 for (int i = 0; i < list.size(); i++) {
				String str=list.get(i).get("chat_phone");
				Log.i("lk",str);
			}
    }
	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.chat_contactlist_item,null);
			holder=new ViewHolder();
			holder.chat_head_img=(ImageView) convertView.findViewById(R.id.chat_head_img);
			holder.chat_nickname=(TextView) convertView.findViewById(R.id.chat_nickname);
			holder.chat_phone=(TextView) convertView.findViewById(R.id.chat_phone);
			holder.isRead=(TextView) convertView.findViewById(R.id.isRead);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		    holder.chat_head_img.setImageResource(R.drawable.groups_icon);
		    holder.chat_nickname.setText(list.get(position).get("chat_nickName"));
		    holder.chat_phone.setText(list.get(position).get("chat_phone"));
		   
		   
//		    for (int i = 0; i < list.size(); i++) {
//				if(list.get(i).get("chat_phone").equals(list.get(position).get("chat_phone"))){
//					holder.chat_phone.setText("("+list.get(position).get("chat_phone")+")");
//				}
//			}
		    
		    
		    
//		    int count=0;
//		    if(mlist!=null){
//		    	for (int i = 0; i < mlist.size(); i++) {
//					if(mlist.get(i).getChatWinPhone().equals(list.get(position).get("chat_phone"))){
//						count++;
//					}
//				}
//		    	holder.isRead.setText(count+"");
//		    }
		return convertView;
	}
    class ViewHolder{
    	ImageView chat_head_img;
    	TextView chat_nickname;
    	TextView chat_phone;
    	TextView isRead;
    }
    
    /** 
     * 去除数据重复 
     * @param li 数据集合 
     * @return 
     */  
    public static List<String> getNewList(List<Map<String,String>> li){  
        List<String> list = new ArrayList<String>();  
        
        for(int i=0; i<li.size(); i++){  
            String str = li.get(i).get("chat_phone");    
            if(!list.get(i).contains(str)){     
                list.add(str);
            }  
        }  
        return list;  
    }  
}
