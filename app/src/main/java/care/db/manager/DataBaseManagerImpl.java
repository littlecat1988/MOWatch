package care.db.manager;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import care.clientmanager.ChatOffLineDaoImpl;
import care.clientmanager.ChatOffLineInfo;
import care.db.DataHelper;
import care.deviceinfo.dao.impl.DeviceInfoDaoImpl;
import care.deviceinfo.model.DeviceInfo;
import care.singlechatinfo.dao.impl.SingleChatInfoDaoImpl;
import care.singlechatinfo.model.SingleChatInfo;
import care.userinfo.dao.impl.UserInfoDaoImpl;
import care.userinfo.model.UserInfo;

public class DataBaseManagerImpl {

	public static DataBaseManagerImpl mDataBaseManagerImpl = null;
	public static DataHelper helper = null;
	
	/**
	 * dao操作层
	 */
	public static Dao<UserInfo,Integer> userInfoDataDao = null;
	public static Dao<DeviceInfo,Integer> deviceInfoDataDao = null;
	public static Dao<SingleChatInfo,Integer> singleChatInfoDataDao = null;
	public static Dao<ChatOffLineInfo,Integer> lineDao = null;
	/**
	 * dao层实现类
	 */
	public static UserInfoDaoImpl mUserInfoDaoImpl;
	public static DeviceInfoDaoImpl mDeviceInfoDaoImpl;
	public static SingleChatInfoDaoImpl mSingleChatInfoDaoImpl;
	public static ChatOffLineDaoImpl mChatOffLineDaoImpl;
	
	public static DataBaseManagerImpl getInstance(Context context){
		if(mDataBaseManagerImpl == null){
			synchronized (DataBaseManagerImpl.class) {
				if(mDataBaseManagerImpl == null){
					mDataBaseManagerImpl = new DataBaseManagerImpl();
				}
			}	
			mUserInfoDaoImpl = UserInfoDaoImpl.getInstance();
			mDeviceInfoDaoImpl = DeviceInfoDaoImpl.getInstance();
			mSingleChatInfoDaoImpl = SingleChatInfoDaoImpl.getInstance();
			mChatOffLineDaoImpl= ChatOffLineDaoImpl.getInstance();
		}
		//数据库连接层
		if(helper == null){
			helper = DataHelper.getInstance(context);
			try {
				getUserInfoDataDao();
				getDeviceInfoDataDao();
				getSingleChatInfoDataDao();
                getChatOffLineInfoDao();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return mDataBaseManagerImpl;
	}
	
	public DataHelper getDataHelperToData(){
		return helper;
	}

	/**
	 * �־û���ݿ������
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static Dao<UserInfo, Integer> getUserInfoDataDao() throws SQLException {
		if (userInfoDataDao == null) {
			userInfoDataDao = helper.getDao(UserInfo.class);
		}
		return userInfoDataDao;
	}
	public static Dao<DeviceInfo, Integer> getDeviceInfoDataDao() throws SQLException {
		if (deviceInfoDataDao == null) {
			deviceInfoDataDao = helper.getDao(DeviceInfo.class);
		}
		return deviceInfoDataDao;
	}
	public static Dao<SingleChatInfo, Integer> getSingleChatInfoDataDao() throws SQLException {
		if(singleChatInfoDataDao == null){
			singleChatInfoDataDao = helper.getDao(SingleChatInfo.class);
		}
		return singleChatInfoDataDao;
	}
	public static Dao<ChatOffLineInfo, Integer> getChatOffLineInfoDao() throws SQLException {
		if(lineDao == null){
			lineDao = helper.getDao(ChatOffLineInfo.class);
		}
		return lineDao;
	}
	/**
	 * 数据库实例化
	 * @return
	 */
	public UserInfoDaoImpl getUserInfoDaoImpl(){
		mUserInfoDaoImpl.setUserInfoDaoImpl(userInfoDataDao);  //����ݲ�����ת��ʵ����ӿڲ���	
		return mUserInfoDaoImpl;
	}
	public DeviceInfoDaoImpl getDeviceInfoDaoImpl(){
		mDeviceInfoDaoImpl.setDeviceInfoDaoImpl(deviceInfoDataDao);  //����ݲ�����ת��ʵ����ӿڲ���	
		return mDeviceInfoDaoImpl;
	}
	public SingleChatInfoDaoImpl getSingleChatInfoDaoImpl(){
		mSingleChatInfoDaoImpl.setSingleChatInfoDaoImpl(singleChatInfoDataDao);
		return mSingleChatInfoDaoImpl;
	}
	public ChatOffLineDaoImpl getChatOffLineDaoImpl(){
		mChatOffLineDaoImpl.setChatOffLineDaoImpl(lineDao);
		return mChatOffLineDaoImpl;
	}
}
