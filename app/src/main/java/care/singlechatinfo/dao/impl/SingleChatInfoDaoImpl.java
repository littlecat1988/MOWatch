package care.singlechatinfo.dao.impl;

import android.content.ContentValues;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import care.singlechatinfo.dao.SingleChatInfoDao;
import care.singlechatinfo.model.SingleChatInfo;

public class SingleChatInfoDaoImpl implements SingleChatInfoDao {

	private volatile static SingleChatInfoDaoImpl mSingleChatInfoDaoImpl;
	private Dao<SingleChatInfo, Integer> singleChatInfoDataDao; // ��ݿ������

	public static SingleChatInfoDaoImpl getInstance() {
		if (mSingleChatInfoDaoImpl == null) {
			synchronized (SingleChatInfoDaoImpl.class) {
				if (mSingleChatInfoDaoImpl == null) {
					mSingleChatInfoDaoImpl = new SingleChatInfoDaoImpl();
				}
			}
			
		}
		return mSingleChatInfoDaoImpl;
	}

	public void setSingleChatInfoDaoImpl(
			Dao<SingleChatInfo, Integer> singleChatInfoDataDao) {
		this.singleChatInfoDataDao = singleChatInfoDataDao;
	}

	@Override
	public int insert(Object object) throws SQLException {
		// TODO Auto-generated method stub
		SingleChatInfo singleChatInfo = (SingleChatInfo)object;
		return singleChatInfoDataDao.create(singleChatInfo);
	}

	@Override
	public int deleteALLForCondition(String[] conditions, String[] conditionArgs)
			throws SQLException {
		// TODO Auto-generated method stub
		DeleteBuilder<SingleChatInfo,Integer> dt = singleChatInfoDataDao.deleteBuilder();
		if(conditions != null){
			int count = 0;
			Where<SingleChatInfo,Integer> whereCo = dt.where();
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
		UpdateBuilder<SingleChatInfo,Integer> up = singleChatInfoDataDao.updateBuilder();
		int count = 0;
		Where<SingleChatInfo,Integer> whereCo = up.where();
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
	public List<SingleChatInfo> queryALLForCondition(String[] conditions,
											   String[] conditionArgs,String orderby,boolean aesc,long maxRaws) throws SQLException {
		// TODO Auto-generated method stub
		QueryBuilder<SingleChatInfo,Integer> qb = singleChatInfoDataDao.queryBuilder();
		if(conditions != null){
			int count = 0;
			Where<SingleChatInfo,Integer> whereCo = qb.where();

			for(String condition : conditions){
				whereCo.eq(conditionArgs[count], condition);
				count++;
				if(count != conditions.length){
					whereCo.and();
				}
			}
		}
		if(orderby != null){
			qb.orderBy(orderby, aesc);  //trueΪ����
		}
		return qb.query();
	}
	@Override
	public long queryDataCount(String[] conditions, String[] conditionArgs)
			throws SQLException {
		// TODO Auto-generated method stub
		QueryBuilder<SingleChatInfo,Integer> qb = singleChatInfoDataDao.queryBuilder();
		if(conditions != null){
			int count = 0;
			Where<SingleChatInfo,Integer> whereCo = qb.where();

			for(String condition : conditions){
				whereCo.eq(conditionArgs[count], condition);
				count++;
				if(count != conditions.length){
					whereCo.and();
				}
			}
		}

		return qb.countOf();
	}
}
