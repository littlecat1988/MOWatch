package care.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.mtk.btnotification.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import care.bean.ChatInfoBean;
import care.db.manager.UpdateDB;
import care.singlechatinfo.model.SingleChatInfo;
import care.utils.Constants;

public class ChatInfoListAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<ChatInfoBean> chatInfoList;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected DisplayImageOptions options;
	
	private UpdateDB mUpdateDB = null;
	private MediaPlayer mMediaPlayer = null;
	
	private final long jiange_time = 1 * 60* 1000;
	public ChatInfoListAdapter(Context context, UpdateDB mUpdateDB){
		this.mContext = context;
		this.mUpdateDB = mUpdateDB;
		
		chatInfoList = new ArrayList<ChatInfoBean>();
		mMediaPlayer = new MediaPlayer();
		
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.default_head9)//设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_head9)//设置图片加载/解码过程中错误时候显示的图片
		.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
		.cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
		.cacheInMemory(true)//设置下载的图片是否缓存在内存中
		.imageScaleType(ImageScaleType.EXACTLY) //设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
		.displayer(new RoundedBitmapDisplayer(20)) //设置圆角图片
		.build();
	}
	
	public void setChatInfoList(ArrayList<ChatInfoBean> chatInfoList){
		this.chatInfoList.addAll(chatInfoList);
	}
	
	public void addChatInfo(ChatInfoBean chatInfoBean){
		if(!chatInfoList.contains(chatInfoBean)){
			chatInfoList.add(chatInfoBean);
		}	
	}
	
	/**
     * �޸设置数据源
     */
    public void setDataListPosition(int position,ChatInfoBean chatInfoBean){
    	if(!chatInfoList.isEmpty()){
    		chatInfoList.set(position, chatInfoBean);
    	}  	
    }
	public void refresh(){
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chatInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return chatInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chatting_item_msg_text, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.messageTime = (TextView)convertView.findViewById(R.id.messageTime);
			viewHolder.chart_container_from = (ViewGroup)convertView.findViewById(R.id.chart_container_from);
			viewHolder.user_head_from = (ImageView)convertView.findViewById(R.id.user_head_from);
			viewHolder.message_content_from = (TextView)convertView.findViewById(R.id.message_content_from);
			viewHolder.voice_timeLen_from = (TextView)convertView.findViewById(R.id.voice_timeLen_from);
			viewHolder.message_state_from = (ImageView)convertView.findViewById(R.id.message_state_from);
			viewHolder.user_name_from = (TextView)convertView.findViewById(R.id.user_name_from);
			viewHolder.chart_container_to = (ViewGroup)convertView.findViewById(R.id.chart_container_to);
			viewHolder.user_head_to = (ImageView)convertView.findViewById(R.id.user_head_to);
			viewHolder.message_content_to = (TextView)convertView.findViewById(R.id.message_content_to);
			viewHolder.voice_timeLen_to = (TextView)convertView.findViewById(R.id.voice_timeLen_to);
			viewHolder.message_state_to = (ImageView)convertView.findViewById(R.id.message_state_to);
			viewHolder.user_name_to = (TextView)convertView.findViewById(R.id.user_name_to);
			viewHolder.messageb_to = (ProgressBar)convertView.findViewById(R.id.messageb_to);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		ChatInfoBean chatInfoBean = chatInfoList.get(position);
		String content = chatInfoBean.getChatContent();
		String head_url = chatInfoBean.getChatHeadUrl();
		String is_read = chatInfoBean.getChatIsRead();
		String len = chatInfoBean.getChatLen();
		String date_time = chatInfoBean.getChatSendTime();
		String type = chatInfoBean.getChatType();
		String user_id = chatInfoBean.getChatUserId();
		String user_name = chatInfoBean.getChatUserName();
		int id = chatInfoBean.getId();   //id���ڸ���
		String chat_is_comefrom = chatInfoBean.getChatComeFrom();
		
		viewHolder.message_content_from.setOnClickListener(new ClickContentListener(type,content,position,user_id));
		viewHolder.message_content_to.setOnClickListener(new ClickContentListener(type,content,position,user_id));

		if(position != 0){
			final ChatInfoBean LastChat = chatInfoList.get(position - 1);
			String lastTime  = LastChat.getChatSendTime();
			long jiange = Constants.timeDiff(date_time, lastTime, "yyyy-MM-dd HH:mm");
			if(jiange <= jiange_time){
				viewHolder.messageTime.setVisibility(View.GONE);  
			}else{
				viewHolder.messageTime.setVisibility(View.VISIBLE);
				viewHolder.messageTime.setText(date_time);  //ʱ��
			}
		}else{			 
			viewHolder.messageTime.setText(date_time);  //ʱ��
		}
		if("0".equals(chat_is_comefrom)){  //0��ʾ�ֻ��?1��ʾ�ƶ�
			viewHolder.chart_container_from.setVisibility(View.GONE);
			viewHolder.chart_container_to.setVisibility(View.VISIBLE);
			
//			viewHolder.user_head_to.setTag(head_url);
			if(head_url != null && !"".equals(head_url)){
				imageLoader.displayImage(head_url, viewHolder.user_head_to, options);
			}else{
				viewHolder.user_head_to.setImageResource(R.drawable.default_head9);
			}
			
			viewHolder.user_name_to.setText(user_name);
			
			if("1".equals(type)){  //1��ʾ������Ϣ,3��ʾ������Ϣ
				viewHolder.message_content_to.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				viewHolder.message_content_to.setText(content);	  //������Ϣ
				viewHolder.voice_timeLen_to.setVisibility(View.GONE);  //������������
			}else if("3".equals(type)){
				viewHolder.message_content_to.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
				viewHolder.message_content_to.setText("");
				viewHolder.voice_timeLen_to.setVisibility(View.VISIBLE);
				viewHolder.voice_timeLen_to.setText(mContext.getString(R.string.chat_time,len));
			}		
			if("0".equals(is_read)){  //�����ֻ��?0��ʾ���ڷ���,1��ʾ���ͳɹ�,-1��ʾ����ʧ��
				viewHolder.messageb_to.setVisibility(View.VISIBLE);  //�ɼ�״̬
				viewHolder.message_state_to.setVisibility(View.GONE);
			}else if("-1".equals(is_read)){
				viewHolder.messageb_to.setVisibility(View.GONE);  //����״̬
				viewHolder.message_state_to.setVisibility(View.VISIBLE);
			}else if("1".equals(is_read)){
				viewHolder.messageb_to.setVisibility(View.GONE);
				viewHolder.message_state_to.setVisibility(View.GONE);
			}
//			viewHolder.message_state_to.setOnClickListener(new )
		}else{
			viewHolder.chart_container_from.setVisibility(View.VISIBLE);
			viewHolder.chart_container_to.setVisibility(View.GONE);
			
			if(head_url != null && !"".equals(head_url)){
				imageLoader.displayImage(head_url, viewHolder.user_head_from, options);
			}else{
				viewHolder.user_head_from.setImageResource(R.drawable.default_head9);
			}
			
			viewHolder.user_name_from.setText(user_name);
			if("1".equals(type)){
				viewHolder.message_content_from.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				viewHolder.message_content_from.setText(content);	  
				viewHolder.voice_timeLen_from.setVisibility(View.GONE);  
			}else if("3".equals(type)){
				viewHolder.message_content_from.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatfrom_voice_playing, 0);
				viewHolder.message_content_from.setText("");
				viewHolder.voice_timeLen_from.setVisibility(View.VISIBLE);
				viewHolder.voice_timeLen_from.setText(mContext.getString(R.string.chat_time,len));			
			}
			
			if("0".equals(is_read) && "3".equals(type)){  
				viewHolder.message_state_from.setVisibility(View.VISIBLE);
			}else{
				viewHolder.message_state_from.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	class ClickContentListener implements View.OnClickListener{

		private String chat_is_comefrom;
		private String content;
		private int position = 0;  //���λ��?
		private String userId = "0";
		
		public ClickContentListener(String chat_is_comefrom,String content,int position,String userId) {
			this.chat_is_comefrom = chat_is_comefrom;
			this.content = content;
			this.position = position;
			this.userId = userId;
		}
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.message_content_from:
				if(chat_is_comefrom.equals("3")){
					playMusic(content);
					updateData(position);			
				}
			break;
			case R.id.message_content_to:
                if(chat_is_comefrom.equals("3")){
                	playMusic(content);   
                	updateData(position);
				}
				break;
			}
			
		}		
	}
	private void updateData(int position){
		ChatInfoBean chatInfoBean = chatInfoList.get(position);
		
        int id = chatInfoBean.getId();
        String isRead = chatInfoBean.getChatIsRead();
        String type = chatInfoBean.getChatComeFrom();
        String belongType = chatInfoBean.getChatBelongType();  //0��ʾ����,1��ʾȺ��
        String userId = chatInfoBean.getChatUserId();
        
        if("0".equals(isRead) && "1".equals(type)){ //δ�������Ǻ�̨���ص�
        	chatInfoBean.setChatIsRead("1");
        	setDataListPosition(position,chatInfoBean);
        	refresh();
        	ContentValues values = new ContentValues();
        	
        	if("1".equals(belongType)){
        		values.put(SingleChatInfo.CHAT_IS_READ, "1");
            	mUpdateDB.updateDataToBases(SingleChatInfo.class, values, new String[]{String.valueOf(id)}, new String[]{SingleChatInfo.ID});
        	}
        	values.clear();
        }
	}
	private class ViewHolder{
		
		TextView messageTime;
		/**
		 * �ƶ˷����ֻ��?
		 */
		ViewGroup chart_container_from;  //����
		ImageView user_head_from;          //ͷ��
		TextView message_content_from;    //����
		TextView voice_timeLen_from;      //��������
		ImageView message_state_from;     //�Ƿ���δ��
		TextView user_name_from;          //�û���
		
		/**
		 * �ֻ�˷����ƶ�?
		 */
		ViewGroup chart_container_to;  //����
		ImageView user_head_to;          //ͷ��
		TextView message_content_to;    //����
		TextView voice_timeLen_to;      //��������
		ImageView message_state_to;     //�Ƿ��ͳɹ�
		TextView user_name_to;          //�û���
		ProgressBar messageb_to;       //���͵�תȦ
	}

	/**
	 * fun name: playMusic
	 * 
	 * @Description play the voice
	 * @param fileName
	 *            :the voice path
	 */
	private void playMusic(String fileName) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			mMediaPlayer.setDataSource(fis.getFD());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void clearData(){
		if(!chatInfoList.isEmpty()){
			chatInfoList.clear();
		}	
		refresh();
	}
	public void clear() {
		// TODO Auto-generated method stub
		if (mMediaPlayer != null) {
			if(mMediaPlayer.isPlaying()){
				mMediaPlayer.stop();
			}
		}
		chatInfoList.clear();
	}
}
