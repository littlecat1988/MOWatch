package care.deviceinfo.dao.impl;

import android.content.ContentValues;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import care.deviceinfo.dao.DeviceInfoDao;
import care.deviceinfo.model.DeviceInfo;

public class DeviceInfoDaoImpl implements DeviceInfoDao {

	private static DeviceInfoDaoImpl mDeviceInfoDaoImpl;
	private Dao<DeviceInfo,Integer> deviceInfoDataDao;
	
	public static DeviceInfoDaoImpl getInstance(){
		if(mDeviceInfoDaoImpl == null){
			synchronized (DeviceInfoDaoImpl.class) {
				if(mDeviceInfoDaoImpl == null){
					mDeviceInfoDaoImpl = new DeviceInfoDaoImpl();
				}
			}		
		}
		return mDeviceInfoDaoImpl;
	}
	public void setDeviceInfoDaoImpl(Dao<DeviceInfo,Integer> deviceInfoDataDao){
		this.deviceInfoDataDao = deviceInfoDataDao;
	}
	
	@Override
	public int insert(Object object) throws SQLException {
		// TODO Auto-generated method stub
		DeviceInfo deviceInfo = (DeviceInfo)object;
		return deviceInfoDataDao.create(deviceInfo);
	}

	@Override
	public int deleteALLForCondition(String[] conditions, String[] conditionArgs)
			throws SQLException {
		// TODO Auto-generated method stub
		DeleteBuilder<DeviceInfo,Integer> dt = deviceInfoDataDao.deleteBuilder();
		if(conditions != null && conditionArgs != null){
			int count = 0;
			Where<DeviceInfo,Integer> whereCo = dt.where();
			for(String condition : conditions){
				whereCo.eq(conditionArgs[count], condition);
				count++;
				if(count != conditions.length){
					whereCo.and();
				}		
			}
			
		}
		return dt.delete();
	}

	@Override
	public int update(ContentValues values, String[] conditions,
			String[] conditionArgs) throws SQLException {
		// TODO Auto-generated method stub
		UpdateBuilder<DeviceInfo, Integer> up = deviceInfoDataDao.updateBuilder();
		int count = 0;
		Where<DeviceInfo,Integer> whereCo = up.where();
		for(String condition : conditions){
			whereCo.eq(conditionArgs[count], condition);
			count++;
			if(count != conditions.length){
				whereCo.and();
			}		
		}
		for(String key : values.keySet()){
			up.updateColumnValue(key, values.get(key));
		}
		
		return up.update();
	}

	@Override
	public List<DeviceInfo> queryALLForCondition(String[] conditions,
			String[] conditionArgs, String orderby, boolean aesc,long maxRaws)
			throws SQLException {
		// TODO Auto-generated method stub
		QueryBuilder<DeviceInfo, Integer> qb = deviceInfoDataDao.queryBuilder();
		if(conditions != null){
			int count = 0;
			Where<DeviceInfo,Integer> whereCo = qb.where();
			
			for(String condition : conditions){
				whereCo.eq(conditionArgs[count], condition);
				count++;
				if(count != conditions.length){
					whereCo.and();
				}		
			}
		}	
		if(orderby != null){
			qb.orderBy(orderby, aesc);
		}
		return qb.query();
	}

	@Override
	public long queryDataCount(String[] conditions, String[] conditionArgs)
			throws SQLException {
		// TODO Auto-generated method stub
		QueryBuilder<DeviceInfo, Integer> qb = deviceInfoDataDao.queryBuilder();
		int count = 0;
		Where<DeviceInfo,Integer> whereCo = qb.where();
		
		for(String condition : conditions){
			whereCo.eq(conditionArgs[count], condition);
			count++;
			if(count != conditions.length){
				whereCo.and();
			}		
		}
		
		return qb.countOf();
	}

}
