package care.db;

import android.content.ContentValues;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao {

	/**
	 * ��ݱ�,�������
	 * @param table
	 * @param values
	 * @throws java.sql.SQLException
	 */
	public int insert(Object object) throws SQLException;

	/**
	 * �������ɾ�����
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int deleteALLForCondition(String[] conditions, String[] conditionArgs) throws SQLException;

	/**
	 * �޸����
	 * @param values
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int update(ContentValues values, String[] conditions, String[] conditionArgs) throws SQLException;

	/**
	 * ��ѯ���
	 * @param conditions
	 * @param conditionArgs
	 * @param orderby
	 * @param aesc(trueΪ����)
	 * @return
	 * @throws java.sql.SQLException
	 */
	public List<?>queryALLForCondition(String[] conditions, String[] conditionArgs, String orderby, boolean aesc, long maxRaws) throws SQLException;

	/**
	 * ��ѯ���еĸ���
	 * @return
	 * @throws java.sql.SQLException
	 */
	public long queryDataCount(String[] conditions, String[] conditionArgs) throws SQLException;
}
