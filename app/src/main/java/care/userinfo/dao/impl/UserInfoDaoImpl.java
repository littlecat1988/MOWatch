package care.userinfo.dao.impl;

import android.content.ContentValues;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import care.userinfo.dao.UserInfoDao;
import care.userinfo.model.UserInfo;

public class UserInfoDaoImpl implements UserInfoDao {

    private static UserInfoDaoImpl mUserInfoDaoImpl;
    private Dao<UserInfo, Integer> userInfoDataDao;  //��ݿ������

    public static UserInfoDaoImpl getInstance() {
        if (mUserInfoDaoImpl == null) {
            synchronized (UserInfoDaoImpl.class) {
                if (mUserInfoDaoImpl == null) {
                    mUserInfoDaoImpl = new UserInfoDaoImpl();
                }
            }
        }
        return mUserInfoDaoImpl;
    }

    public void setUserInfoDaoImpl(Dao<UserInfo, Integer> userInfoDataDao) {
        this.userInfoDataDao = userInfoDataDao;
    }

    @Override
    public int insert(Object object) throws SQLException {
        // TODO Auto-generated method stub
        UserInfo userInfo = (UserInfo) object;
        return userInfoDataDao.create(userInfo);
    }

    @Override
    public int deleteALLForCondition(String[] conditions, String[] conditionArgs)
            throws SQLException {
        // TODO Auto-generated method stub
        DeleteBuilder<UserInfo, Integer> dt = userInfoDataDao.deleteBuilder();
        if (conditions != null) {
            int count = 0;
            Where<UserInfo, Integer> whereCo = dt.where();
            for (String condition : conditions) {
                whereCo.eq(conditionArgs[count], condition);
                count++;
                if (count != conditions.length) {
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
        UpdateBuilder<UserInfo, Integer> up = userInfoDataDao.updateBuilder();
        int count = 0;
        Where<UserInfo, Integer> whereCo = up.where();
        for (String condition : conditions) {
            whereCo.eq(conditionArgs[count], condition);
            count++;
            if (count != conditions.length) {
                whereCo.and();
            }
        }
        for (String key : values.keySet()) {
            up.updateColumnValue(key, values.get(key));
        }
        return up.update();
    }

    @Override
    public List<UserInfo> queryALLForCondition(String[] conditions,
                                               String[] conditionArgs, String orderby, boolean aesc,long maxRaws) throws SQLException {
        // TODO Auto-generated method stub
        QueryBuilder<UserInfo, Integer> qb = userInfoDataDao.queryBuilder();
        if (conditions != null) {
            int count = 0;
            Where<UserInfo, Integer> whereCo = qb.where();

            for (String condition : conditions) {
                whereCo.eq(conditionArgs[count], condition);
                count++;
                if (count != conditions.length) {
                    whereCo.and();
                }
            }
        }
        if (orderby != null) {
            qb.orderBy(orderby, aesc);  //trueΪ����
        }
        return qb.query();
    }

    @Override
    public long queryDataCount(String[] conditions, String[] conditionArgs)
            throws SQLException {
        // TODO Auto-generated method stub
        QueryBuilder<UserInfo, Integer> qb = userInfoDataDao.queryBuilder();
        if (conditions != null) {
            int count = 0;
            Where<UserInfo, Integer> whereCo = qb.where();

            for (String condition : conditions) {
                whereCo.eq(conditionArgs[count], condition);
                count++;
                if (count != conditions.length) {
                    whereCo.and();
                }
            }
        }
        return qb.countOf();
    }

}
