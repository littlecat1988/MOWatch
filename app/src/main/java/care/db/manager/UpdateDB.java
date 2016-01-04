package care.db.manager;

import android.content.ContentValues;
import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import care.application.XcmApplication;
import care.clientmanager.ChatOffLineDaoImpl;
import care.clientmanager.ChatOffLineInfo;
import care.deviceinfo.dao.impl.DeviceInfoDaoImpl;
import care.deviceinfo.model.DeviceInfo;
import care.singlechatinfo.dao.impl.SingleChatInfoDaoImpl;
import care.singlechatinfo.model.SingleChatInfo;
import care.userinfo.dao.impl.UserInfoDaoImpl;
import care.userinfo.model.UserInfo;

public class UpdateDB {

    private volatile static UpdateDB mUpdateDB;
    private XcmApplication mIntance;
    private DataBaseManagerImpl mDataBaseManagerImpl;  //������

    private UserInfoDaoImpl mUserInfoDaoImpl;
    private DeviceInfoDaoImpl mDeviceInfoDaoImpl;
    private SingleChatInfoDaoImpl mSingleChatInfoDaoImpl;
    private ChatOffLineDaoImpl mChatOffLineDaoImpl;
    /**
     * �û���,�豸��
     */
    private static ArrayList<Class<?>> tablelist = new ArrayList<Class<?>>();

    public UpdateDB(Context context) {
        // TODO Auto-generated constructor stub
        tablelist.add(UserInfo.class);
        tablelist.add(DeviceInfo.class);
        tablelist.add(SingleChatInfo.class);
        tablelist.add(ChatOffLineInfo.class);
        
        mIntance = XcmApplication.getInstance();
        mDataBaseManagerImpl = mIntance.getDataBaseManagerImpl(context);
        mUserInfoDaoImpl = mDataBaseManagerImpl.getUserInfoDaoImpl();
        mDeviceInfoDaoImpl = mDataBaseManagerImpl.getDeviceInfoDaoImpl();
        mSingleChatInfoDaoImpl = mDataBaseManagerImpl.getSingleChatInfoDaoImpl();
        mChatOffLineDaoImpl=mDataBaseManagerImpl.getChatOffLineDaoImpl();
    }

    public static UpdateDB getInstance(Context context) {
        if (mUpdateDB == null) {
            synchronized (UpdateDB.class) {
                if (mUpdateDB == null) {
                    mUpdateDB = new UpdateDB(context);
                }
            }
        }
        return mUpdateDB;
    }

    /**
     * �������
     *
     * @param ObjectClass
     * @param object
     * @return
     */
    public int insertToDataBases(Class<?> ObjectClass, Object object) {
        int returnValue = 0;  //返回值
        int position = tablelist.indexOf(ObjectClass);   //要操作表的位置
        try {
            if (position != -1) {
                switch (position) {
                    case 0:
                        UserInfo userInfo = (UserInfo) object;
                        returnValue = mUserInfoDaoImpl.insert(userInfo);
                        break;
                    case 1:
                        DeviceInfo deviceInfo = (DeviceInfo) object;
                        returnValue = mDeviceInfoDaoImpl.insert(deviceInfo);
                        break;
                    case 2:
                        SingleChatInfo singleChatInfo = (SingleChatInfo)object;
                        returnValue = mSingleChatInfoDaoImpl.insert(singleChatInfo);
                        break;
                    case 3:
                        ChatOffLineInfo chatOffLineInfo = (ChatOffLineInfo)object;
                        returnValue = mChatOffLineDaoImpl.insert(chatOffLineInfo);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * �޸����
     *
     * @param ObjectClass
     * @param values
     * @param conditions
     * @param conditionArgs(�޸ĵ��ֶ�)
     */
    public int updateDataToBases(Class<?> ObjectClass, ContentValues values, String[] conditions, String[] conditionArgs) {
        int returnValue = 0;  //Ĭ�ϲ��벻�ɹ�
        int position = tablelist.indexOf(ObjectClass);   //��ȡλ��
        try {
            if (position != -1) {
                switch (position) {
                    case 0:
                        returnValue = mUserInfoDaoImpl.update(values, conditions, conditionArgs);
                        break;
                    case 1:
                        returnValue = mDeviceInfoDaoImpl.update(values, conditions, conditionArgs);
                        break;
                    case 2:
                        returnValue = mSingleChatInfoDaoImpl.update(values, conditions, conditionArgs);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * @param ObjectClass
     * @param values
     * @param conditions
     * @param conditionArgs
     * @return
     */
    public List<?> queryDataToBases(Class<?> ObjectClass, String[] conditions, String[] conditionArgs, String orderby, boolean asc,long maxRaws) {
        List<?> tmp = null;
        int position = tablelist.indexOf(ObjectClass);   //��ȡλ��
        try {
            if (position != -1) { 
                switch (position) {
                    case 0:
                        tmp = mUserInfoDaoImpl.queryALLForCondition(conditions, conditionArgs, orderby, asc,maxRaws);
                        break;
                    case 1:
                        tmp = mDeviceInfoDaoImpl.queryALLForCondition(conditions, conditionArgs, orderby, asc,maxRaws);
                        break;
                    case 2:
                        tmp = mSingleChatInfoDaoImpl.queryALLForCondition(conditions, conditionArgs, orderby, asc,maxRaws);
                        break;
                    case 3:
                        tmp = mChatOffLineDaoImpl.queryALLForCondition(conditions, conditionArgs, orderby, asc,maxRaws);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     * ɾ��
     *
     * @param ObjectClass
     * @param values
     * @param conditions
     * @param conditionArgs
     * @return
     */
    public int deleteDataToBases(Class<?> ObjectClass, ContentValues values, String[] conditions, String[] conditionArgs) {
        int returnValue = 0;  //Ĭ�ϲ��벻�ɹ�
        int position = tablelist.indexOf(ObjectClass);   //��ȡλ��
        try {
            if (position != -1) {
                switch (position) {
                    case 0:
                        returnValue = mUserInfoDaoImpl.deleteALLForCondition(conditions, conditionArgs);
                        break;
                    case 1:
                        returnValue = mDeviceInfoDaoImpl.deleteALLForCondition(conditions, conditionArgs);
                        break;
                    case 2:
                        returnValue = mSingleChatInfoDaoImpl.deleteALLForCondition(conditions, conditionArgs);
                        break;
                    case 3:
                        returnValue = mChatOffLineDaoImpl.deleteALLForCondition(conditions, conditionArgs);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * ��ѯ����
     *
     * @param ObjectClass
     * @param values
     * @param conditions
     * @param conditionArgs
     * @return
     */
    public long queryDataCountToBases(Class<?> ObjectClass, String[] conditions, String[] conditionArgs) {
        long count = 0;
        int position = tablelist.indexOf(ObjectClass);   //��ȡλ��
        try {
            if (position != -1) {
                switch (position) {
                    case 0:
                        count = mUserInfoDaoImpl.queryDataCount(conditions, conditionArgs);
                        break;
                    case 1:
                        count = mDeviceInfoDaoImpl.queryDataCount(conditions, conditionArgs);
                        break;
                    case 2:
                        count = mSingleChatInfoDaoImpl.queryDataCount(conditions, conditionArgs);
                        break;
                    case 3:
                    	count= mChatOffLineDaoImpl.queryDataCount(conditions, conditionArgs);
                    	break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
