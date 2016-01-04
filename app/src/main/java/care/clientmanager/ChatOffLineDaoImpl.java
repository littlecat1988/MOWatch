package care.clientmanager;

import android.content.ContentValues;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

public class ChatOffLineDaoImpl implements ChatOffLineDao {
	private volatile static ChatOffLineDaoImpl mChatOffLineDaoImpl;
	private Dao<ChatOffLineInfo, Integer> chatOffLineInfoDao;
	
	public static ChatOffLineDaoImpl getInstance() {
		if (mChatOffLineDaoImpl == null) {
			synchronized (ChatOffLineDaoImpl.class) {
				if (mChatOffLineDaoImpl == null) {
					mChatOffLineDaoImpl = new ChatOffLineDaoImpl();
				}
			}
		}
		return mChatOffLineDaoImpl;
	}
	public void setChatOffLineDaoImpl(
			Dao<ChatOffLineInfo, Integer> chatOffLineInfo) {
		this.chatOffLineInfoDao = chatOffLineInfo;
	}
	@Override
	public int insert(Object object) throws SQLException {
		ChatOffLineInfo LineInfo=(ChatOffLineInfo) object;
		return chatOffLineInfoDao.create(LineInfo); 
	}
	@Override
	public int deleteALLForCondition(String[] conditions, String[] conditionArgs)
			throws SQLException {
		DeleteBuilder<ChatOffLineInfo,Integer> dt = chatOffLineInfoDao.deleteBuilder();
		if(conditions != null){
			int count = 0;
			Where<ChatOffLineInfo,Integer> whereCo = dt.where();
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
		UpdateBuilder<ChatOffLineInfo,Integer> up = chatOffLineInfoDao.updateBuilder();
		int count = 0;
		Where<ChatOffLineInfo,Integer> whereCo = up.where();
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
	public List<?> queryALLForCondition(String[] conditions,
			String[] conditionArgs, String orderby, boolean aesc,long maxRaws)
			throws SQLException {
		QueryBuilder<ChatOffLineInfo,Integer> qb = chatOffLineInfoDao.queryBuilder();
		if(conditions != null){
			int count = 0;
			Where<ChatOffLineInfo,Integer> whereCo = qb.where();

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
		return qb.limit(maxRaws).query();
	}

	@Override
	public long queryDataCount(String[] conditions, String[] conditionArgs)
			throws SQLException {
		QueryBuilder<ChatOffLineInfo,Integer> qb = chatOffLineInfoDao.queryBuilder();
		if(conditions != null){
			int count = 0;
			Where<ChatOffLineInfo,Integer> whereCo = qb.where();

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

