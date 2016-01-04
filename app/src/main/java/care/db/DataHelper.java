package care.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import care.application.XcmApplication;
import care.clientmanager.ChatOffLineInfo;
import care.deviceinfo.model.DeviceInfo;
import care.singlechatinfo.model.SingleChatInfo;
import care.userinfo.model.UserInfo;

public class DataHelper extends OrmLiteSqliteOpenHelper{

	public static final String DARABASE_NAME = "xcm.db";
	public static int DATABASE_VERSION = 1;
	public static String DATABASE_PATH= XcmApplication.DB_FILE+"/"+DARABASE_NAME;
	private static DataHelper helper;
	@SuppressWarnings("rawtypes")
	private Map<String,Dao> daos=new HashMap<String, Dao>();
	
	public DataHelper(Context context){
		super(context, DARABASE_NAME, null, DATABASE_VERSION);
		try { 
		File f = new File(DATABASE_PATH);  
	        if (!f.exists()) {  
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH, null);  
        onCreate(db);  
        db.close();  
          }
		}catch (Exception e) {  
	    } 
	}
	
	@Override  
	public synchronized SQLiteDatabase getWritableDatabase() {  
	    return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);  
	}  
	  
	public synchronized SQLiteDatabase getReadableDatabase() {  
	    return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);  
	}  
	
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource source) {
		// TODO Auto-generated method stub
		try{
			TableUtils.createTable(source, UserInfo.class);
			TableUtils.createTable(source, DeviceInfo.class);
			TableUtils.createTable(source, SingleChatInfo.class);
			TableUtils.createTable(source, ChatOffLineInfo.class);
			
		}catch(SQLException e){
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource source, int oldVersion,
			int newVersion) {
		try  
        {  
            TableUtils.dropTable(source, UserInfo.class, true);
            TableUtils.dropTable(source, DeviceInfo.class, true);
            TableUtils.dropTable(source, SingleChatInfo.class, true);
            TableUtils.dropTable(source, ChatOffLineInfo.class, true);
            onCreate(database, source);  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
		
	}

	public static synchronized DataHelper getInstance(Context context){
		context=context.getApplicationContext();
		if(helper == null){
			synchronized (DataHelper.class) {
				if(helper == null){
					new DataHelper(context);
					helper = OpenHelperManager.getHelper(context,DataHelper.class);
				}
			}
		}
		return helper;
	}
	
	public synchronized Dao getDao(Class clazz) throws SQLException  
	    {  
	        Dao dao = null;  
	        String className = clazz.getSimpleName();  
	  
	        if (daos.containsKey(className))  
	        {  
	            dao = daos.get(className);  
	        }  
	        if (dao == null)  
	        {  
	            dao = super.getDao(clazz);  
	            daos.put(className, dao);  
	        }  
	        return dao;  
	    }  
	 /**
	   * 释放资源 
	   */  
	    @Override  
	    public void close()  
	    {  
	        super.close();  
	  
	        for (String key : daos.keySet())  
	        {  
	            Dao dao = daos.get(key);  
	            dao = null;  
	        }  
	    }  
}
