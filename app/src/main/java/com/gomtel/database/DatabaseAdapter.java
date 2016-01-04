package com.gomtel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.gomtel.util.AdInfo;
import com.gomtel.util.Global;
import com.gomtel.util.HttpUtils;

public class DatabaseAdapter {
	// public static final String ALTER_TABLE_PROFLIE =
	// "ALTER TABLE TABLE_PROFILE RENAME TO TEMP_TABLE_PROFLIE";
	// public static final String CREATE_TABLE_ALARM =
	// "CREATE TABLE IF NOT EXISTS TABLE_ALARM(_id integer primary key autoincrement, KEY_STATE int not null, KEY_HOUR int not null, KEY_MINUTE int not null, KEY_DURATION int not null, KEY_MONDAY int not null, KEY_TUESDAY int not null, KEY_WEDNESDAY int not null, KEY_THURSDAY int not null, KEY_FRIDAY int not null, KEY_SATURDAY int not null, KEY_SUNDAY int not null, KEY_PROFILE_ID int not null); ";
	// public static final String CREATE_TABLE_GOAL =
	// "CREATE TABLE IF NOT EXISTS TABLE_GOAL(_id integer primary key autoincrement, KEY_GOAL_STEP int not null, KEY_GOAL_BURN double not null, KEY_GOAL_SLEEP int not null, KEY_PROFILE_ID int not null); ";
	public static final String CREATE_TABLE_HISTORY_DAY = "CREATE TABLE IF NOT EXISTS TABLE_HISTORY_DAY(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATE_LONG long not null, KEY_STEP int not null, KEY_BURN double not null, KEY_DISTANCE int not null, KEY_PROFILE_ID int not null); ";
	public static final String CREATE_TABLE_HISTORY_HOUR = "CREATE TABLE IF NOT EXISTS TABLE_HISTORY_HOUR(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_DATETIME_LONG long not null, KEY_STEP int not null, KEY_BURN double not null, KEY_SLEEP_MOVE int not null, KEY_PROFILE_ID int not null); ";
	public static final String CREATE_TABLE_PROFILE = "CREATE TABLE IF NOT EXISTS TABLE_PROFILE(_id integer primary key autoincrement, KEY_NAME text not null, KEY_LANGUAGE text not null, KEY_AGE int not null, KEY_GENDER int not null, KEY_HEIGHT double not null, KEY_WEIGHT double not null, KEY_DATETIME_BEGIN int not null, KEY_DATETIME_END int not null, KEY_TIME_REMIND int not null); ";
	public static final String CREATE_TABLE_REMIND = "CREATE TABLE IF NOT EXISTS TABLE_REMIND(_id integer primary key autoincrement, KEY_REMINDER_CALL int not null, KEY_REMINDER_SMS int not null, KEY_REMINDER_MAIL int not null, KEY_PROFILE_ID int not null); ";
	public static final String CREATE_TABLE_REMINDER = "CREATE TABLE IF NOT EXISTS TABLE_REMINDER(_id integer primary key autoincrement, KEY_STATE int not null, KEY_HOUR_BEGIN text not null, KEY_HOUR_END text not null, INTERVAL text not null, KEY_MONDAY int not null, KEY_TUESDAY int not null, KEY_WEDNESDAY int not null, KEY_THURSDAY int not null, KEY_FRIDAY int not null, KEY_SATURDAY int not null, KEY_SUNDAY int not null, KEY_PROFILE_ID int not null); ";
	private static final String DATABASE_NAME = "watch";
	private static final int DATABASE_VERSION = 2;
	public static final String INSERT_DATA_PROFLIE = "insert into TABLE_PROFILE select _id, KEY_NAME, KEY_LANGUAGE, '', KEY_GENDER, KEY_HEIGHT, KEY_WEIGHT, KEY_DATETIME_BEGIN, KEY_DATETIME_END, KEY_TIME_REMINDfrom TEMP_TABLE_PROFLIE";
	public static final String KEY_ACTIVE_HOUR = "KEY_SLEEP_TOTAL";
	public static final String KEY_AGE = "KEY_AGE";
	public static final String KEY_BURN = "KEY_BURN";
	public static final String KEY_DATE = "KEY_DATE";
	public static final String KEY_DATETIME = "KEY_DATETIME";
	public static final String KEY_DATETIME_BEGIN = "KEY_DATETIME_BEGIN";
	public static final String KEY_DATETIME_END = "KEY_DATETIME_END";
	public static final String KEY_DATETIME_LONG = "KEY_DATETIME_LONG";
	public static final String KEY_DATE_LONG = "KEY_DATE_LONG";
	public static final String KEY_DURATION = "KEY_DURATION";
	public static final String KEY_FRIDAY = "KEY_FRIDAY";
	public static final String KEY_GENDER = "KEY_GENDER";
	public static final String KEY_GOAL_BURN = "KEY_GOAL_BURN";
	public static final String KEY_GOAL_SLEEP = "KEY_GOAL_SLEEP";
	public static final String KEY_GOAL_STEP = "KEY_GOAL_STEP";
	public static final String KEY_HEIGHT = "KEY_HEIGHT";
	public static final String KEY_HOUR = "KEY_HOUR";
	public static final String KEY_HOUR_BEGIN = "KEY_HOUR_BEGIN";
	public static final String KEY_HOUR_END = "KEY_HOUR_END";
	public static final String KEY_INTERVAL = "INTERVAL";
	public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
	public static final String KEY_MINUTE = "KEY_MINUTE";
	public static final String KEY_MONDAY = "KEY_MONDAY";
	public static final String KEY_NAME = "KEY_NAME";
	public static final String KEY_PROFILE_ID = "KEY_PROFILE_ID";
	public static final String KEY_REMINDER_CALL = "KEY_REMINDER_CALL";
	public static final String KEY_REMINDER_MAIL = "KEY_REMINDER_MAIL";
	public static final String KEY_REMINDER_SMS = "KEY_REMINDER_SMS";
	private static final String KEY_ROWID = "_id";
	public static final String KEY_SATURDAY = "KEY_SATURDAY";
	public static final String KEY_SLEEP_DEEP_HOUR = "KEY_SLEEP_DEEP_MINUTES";
	public static final String KEY_SLEEP_LIGHT_HOUR = "KEY_SLEEP_LIGHT_MINUTES";
	public static final String KEY_SLEEP_MOVE = "KEY_SLEEP_MOVE";
	public static final String KEY_SLEEP_QUALITY = "KEY_SLEEP_START";
	public static final String KEY_STATE = "KEY_STATE";
	public static final String KEY_STEP = "KEY_STEP";
	public static final String KEY_SUNDAY = "KEY_SUNDAY";
	public static final String KEY_THURSDAY = "KEY_THURSDAY";
	public static final String KEY_TUESDAY = "KEY_TUESDAY";
	public static final String KEY_WEDNESDAY = "KEY_WEDNESDAY";
	public static final String KEY_WEIGHT = "KEY_WEIGHT";
	public static final String TABLE_ALARM = "TABLE_ALARM";
	public static final String TABLE_GOAL = "TABLE_GOAL";
	public static final String TABLE_HISTORY_DAY = "TABLE_HISTORY_DAY";
	public static final String TABLE_HISTORY_HOUR = "TABLE_HISTORY_HOUR";
	public static final String TABLE_PROFLIE = "TABLE_PROFILE";
	public static final String TABLE_REMIND = "TABLE_REMIND";
	public static final String TABLE_REMINDER = "TABLE_REMINDER";
	private static String TAG = "DatabaseAdapter";
	public static final String TEMP_TABLE_ALARM = "TEMP_TABLE_ALARM";
	public static final String TEMP_TABLE_GOAL = "TEMP_TABLE_GOAL";
	public static final String TEMP_TABLE_HISTORY_DAY = "TEMP_TABLE_HISTORY_DAY";
	public static final String TEMP_TABLE_HISTORY_HOUR = "TEMP_TABLE_HISTORY_HOUR";
	public static final String TEMP_TABLE_PROFLIE = "TEMP_TABLE_PROFLIE";
	public static final String TEMP_TABLE_REMIND = "TEMP_TABLE_REMIND";
	public static final String TEMP_TABLE_REMINDER = "TEMP_TABLE_REMINDER";
	private final Context context;
	private DatabaseOpenHelper databaseOpenHelper;
	private SQLiteDatabase db;

	public DatabaseAdapter(Context paramContext) {
		this.context = paramContext;
		this.databaseOpenHelper = new DatabaseOpenHelper(this.context,
				DATABASE_NAME, null, 2);
	}

	public void closeDatabase() {
		this.databaseOpenHelper.close();
	}

	public void deleteAllData() {
		// this.db.execSQL("DROP TABLE IF EXISTS TABLE_PROFILE");
		// this.db.execSQL("DROP TABLE IF EXISTS TABLE_ALARM");
		// this.db.execSQL("DROP TABLE IF EXISTS TABLE_REMINDER");
		this.db.execSQL("DROP TABLE IF EXISTS TABLE_HISTORY_DAY");
		this.db.execSQL("DROP TABLE IF EXISTS TABLE_HISTORY_HOUR");
		 this.db.execSQL("DROP TABLE IF EXISTS TABLE_HISTORY_SLEEP");
		 this.db.execSQL("DROP TABLE IF EXISTS TABLE_HISTORY_HR");
		// this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_PROFILE(_id integer primary key autoincrement, KEY_NAME text not null, KEY_LANGUAGE text not null, KEY_AGE int not null, KEY_GENDER int not null, KEY_HEIGHT double not null, KEY_WEIGHT double not null, KEY_DATETIME_BEGIN int not null, KEY_DATETIME_END int not null, KEY_TIME_REMIND int not null); ");
		// this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_ALARM(_id integer primary key autoincrement, KEY_STATE int not null, KEY_HOUR int not null, KEY_MINUTE int not null, KEY_DURATION int not null, KEY_MONDAY int not null, KEY_TUESDAY int not null, KEY_WEDNESDAY int not null, KEY_THURSDAY int not null, KEY_FRIDAY int not null, KEY_SATURDAY int not null, KEY_SUNDAY int not null, KEY_PROFILE_ID int not null); ");
		// this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_REMINDER(_id integer primary key autoincrement, KEY_STATE int not null, KEY_HOUR_BEGIN text not null, KEY_HOUR_END text not null, INTERVAL text not null, KEY_MONDAY int not null, KEY_TUESDAY int not null, KEY_WEDNESDAY int not null, KEY_THURSDAY int not null, KEY_FRIDAY int not null, KEY_SATURDAY int not null, KEY_SUNDAY int not null, KEY_PROFILE_ID int not null); ");
		this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_DAY(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATE_LONG long not null, KEY_STEP int not null, KEY_BURN double not null, KEY_DISTANCE int not null, KEY_PROFILE_ID int not null);  ");
		this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_HOUR(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_DATETIME_LONG long not null, KEY_STEP int not null, KEY_BURN double not null, KEY_SLEEP_MOVE int not null, KEY_PROFILE_ID int not null); ");
		 this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_SLEEP(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_DATETIME_LONG long not null, KEY_SLEEP_START long not null, KEY_SLEEP_DEEP_MINUTES long not null, KEY_SLEEP_LIGHT_MINUTES long not null, KEY_SLEEP_TOTAL long not null,KEY_PROFILE_ID int not null); ");
		 this.db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_HR(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_HEARTRATE int not null, KEY_PROFILE_ID int not null); ");
	}

	public void delete_history_day(int paramInt, Calendar paramCalendar) {
		Log.i(TAG, "delete history day_ date:" + paramCalendar.getTime());
		this.db.delete("TABLE_HISTORY_DAY",
				"KEY_DATE_LONG=" + paramCalendar.getTimeInMillis() + " and "
						+ "KEY_PROFILE_ID" + "=" + paramInt, null);
	}

	public void delete_history_day_after_now(Calendar paramCalendar) {
		this.db.delete("TABLE_HISTORY_DAY",
				"KEY_DATE_LONG>" + paramCalendar.getTimeInMillis(), null);
	}

	public void delete_history_hour_aday(int paramInt, Calendar paramCalendar) {
		if (paramCalendar == null)
			return;
		String str = Global.sdf_2.format(paramCalendar.getTime());
		Log.i(TAG, "delete history hour " + str);
		this.db.delete("TABLE_HISTORY_HOUR", "KEY_DATE='" + str + "'" + " and "
				+ "KEY_PROFILE_ID" + "=" + paramInt, null);
	}

	public void delete_history_hour_after_now(Calendar paramCalendar) {
		if (paramCalendar == null)
			return;
		this.db.delete("TABLE_HISTORY_HOUR", "KEY_DATETIME_LONG>"
				+ paramCalendar.getTimeInMillis(), null);
	}

	public SQLiteDatabase getSQLiteDatabase() {
		return this.db;
	}

	public long insert_history_day(int paramInt1, Calendar paramCalendar,
			int paramInt2, double paramDouble, double paramInt3) {
		Log.i(TAG, "insert history day_ date:" + paramCalendar.getTime()
				+ ", step:" + paramInt2 + ", burn:" + paramDouble + ", "
				+ paramInt3);
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("KEY_DATE",
				Global.sdf_2.format(paramCalendar.getTime()));
		localContentValues.put("KEY_DATE_LONG",
				Long.valueOf(paramCalendar.getTimeInMillis()));
		localContentValues.put("KEY_STEP", Integer.valueOf(paramInt2));
		localContentValues.put("KEY_BURN", Double.valueOf(paramDouble));
		localContentValues.put("KEY_DISTANCE", Double.valueOf(paramInt3));
		localContentValues.put("KEY_PROFILE_ID", Integer.valueOf(paramInt1));
		return this.db.insert("TABLE_HISTORY_DAY", null, localContentValues);
	}

	public long insert_history_hour(int paramInt1, Calendar paramCalendar,
			int paramInt2, double paramDouble, int paramInt3) {
		Log.i(TAG, "insert history hour_ datetime:" + paramCalendar.getTime()
				+ ", step:" + paramInt2 + ", burn:" + paramDouble + ", "
				+ paramInt3);
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("KEY_DATE",
				Global.sdf_2.format(paramCalendar.getTime()));
		localContentValues.put("KEY_DATETIME",
				Global.sdf_3.format(paramCalendar.getTime()));
		localContentValues.put("KEY_DATETIME_LONG",
				Long.valueOf(paramCalendar.getTimeInMillis()));
		localContentValues.put("KEY_STEP", Integer.valueOf(paramInt2));
		localContentValues.put("KEY_BURN", Double.valueOf(paramDouble));
		localContentValues.put("KEY_SLEEP_MOVE", Integer.valueOf(paramInt3));
		localContentValues.put("KEY_PROFILE_ID", Integer.valueOf(paramInt1));
		return this.db.insert("TABLE_HISTORY_HOUR", null, localContentValues);
	}

	public DatabaseAdapter openDatabase() {
		if (this.databaseOpenHelper != null)
			this.db = this.databaseOpenHelper.getWritableDatabase();
		return this;
	}

	public Cursor query_history_day(int paramInt, Calendar paramCalendar) {
		Log.i(TAG, "query history day_ date:" + paramCalendar.getTime());
		Cursor localCursor = this.db.query(true, "TABLE_HISTORY_DAY",
				new String[] { "KEY_DATE", "KEY_STEP", "KEY_BURN",
						"KEY_DISTANCE" },
				"KEY_DATE_LONG=" + paramCalendar.getTimeInMillis() + " and "
						+ "KEY_PROFILE_ID" + "=" + paramInt, null, null, null,
				null, null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}
	
	//add by lixiang for sleep 20150908 begin
	public Cursor query_history_sleep(int paramInt, Calendar paramCalendar) {
		Log.e(TAG, "sleep history:" + Global.sdf_2.format(paramCalendar.getTime()));
		Cursor localCursor = this.db.query(true, "TABLE_HISTORY_SLEEP",
				new String[] { "KEY_DATE", "KEY_SLEEP_START", "KEY_SLEEP_DEEP_MINUTES",
						"KEY_SLEEP_LIGHT_MINUTES","KEY_SLEEP_TOTAL" },
				"KEY_DATE=?" , new String[]{Global.sdf_2.format(paramCalendar.getTime())}, null, null,
				null, null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}
	//add by lixiang for sleep 20150908 begin

	public Cursor query_history_day(int paramInt, long startTime,
			long stopTime) {
//		Log.i(TAG, "query history day_ begin:" + paramCalendar1.getTime()
//				+ ", end:" + paramCalendar2.getTime());
		Cursor localCursor = this.db.query(
				true,
				"TABLE_HISTORY_DAY",
				new String[] { "KEY_DATE", "KEY_STEP", "KEY_BURN",
						"KEY_DISTANCE" },
				"KEY_DATE_LONG>=" + startTime + " and "
						+ "KEY_DATE_LONG" + "<"
						+ stopTime + " and "
						+ "KEY_PROFILE_ID" + "=" + paramInt, null, null, null,
				null, null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor query_history_hour(int paramInt, Calendar paramCalendar) {
		Log.i(TAG, "query history hour_ date:" + paramCalendar.getTime());
		Cursor localCursor = this.db.query(true, "TABLE_HISTORY_HOUR",
				new String[] { "KEY_DATETIME", "KEY_STEP", "KEY_BURN",
						"KEY_SLEEP_MOVE" }, "KEY_DATETIME_LONG="
						+ paramCalendar.getTimeInMillis() + " and "
						+ "KEY_PROFILE_ID" + "=" + paramInt, null, null, null,
				null, null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public Cursor query_history_hour(int paramInt, Calendar paramCalendar1,
			Calendar paramCalendar2) {
		Log.i(TAG, "query history hour_ begin:" + paramCalendar1.getTime()
				+ ", end:" + paramCalendar2.getTime());
		Cursor localCursor = this.db.query(
				true,
				"TABLE_HISTORY_HOUR",
				new String[] { "KEY_DATETIME", "KEY_STEP", "KEY_BURN",
						"KEY_SLEEP_MOVE" },
				"KEY_DATETIME_LONG>=" + paramCalendar1.getTimeInMillis()
						+ " and " + "KEY_DATETIME_LONG" + "<"
						+ paramCalendar2.getTimeInMillis() + " and "
						+ "KEY_PROFILE_ID" + "=" + paramInt, null, null, null,
				"KEY_DATETIME_LONG asc", null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public int update_history_day(int paramInt1, Calendar paramCalendar,
			int paramInt2, double paramDouble, double paramInt3) {
		Log.i(TAG, "update history day_ date:" + paramCalendar.getTime()
				+ ", step:" + paramInt2 + ", burn:" + paramDouble + ", "
				+ paramInt3);
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("KEY_DATE",
				Global.sdf_2.format(paramCalendar.getTime()));
		localContentValues.put("KEY_DATE_LONG",
				Long.valueOf(paramCalendar.getTimeInMillis()));
		localContentValues.put("KEY_STEP", Integer.valueOf(paramInt2));
		localContentValues.put("KEY_BURN", Double.valueOf(paramDouble));
		localContentValues.put("KEY_DISTANCE", Double.valueOf(paramInt3));
		return this.db.update("TABLE_HISTORY_DAY", localContentValues,
				"KEY_DATE_LONG=" + paramCalendar.getTimeInMillis() + " and "
						+ "KEY_PROFILE_ID" + "=" + paramInt1, null);
	}
	//add by lixiang for sleep 20150901 begin 
	public long insert_sleep_history(long startTime, long deepTime,
			long lightTime, long totalTime, Calendar date) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("KEY_DATE",
				Global.sdf_2.format(date.getTime()));
		localContentValues.put("KEY_DATETIME",
				Global.sdf_1.format(date.getTime()));
		localContentValues.put("KEY_DATETIME_LONG",
				Long.valueOf(date.getTimeInMillis()));
		localContentValues.put("KEY_SLEEP_START",
				startTime);
		localContentValues.put("KEY_SLEEP_DEEP_MINUTES", deepTime);
		localContentValues.put("KEY_SLEEP_LIGHT_MINUTES", lightTime);
		localContentValues.put("KEY_SLEEP_TOTAL",totalTime);
		localContentValues.put("KEY_PROFILE_ID", 0);
		return this.db.insert("TABLE_HISTORY_SLEEP", null, localContentValues);
	}
	
	public long update_sleep_history(long startTime, long deepTime,
			long lightTime, long totalTime, Calendar date) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("KEY_DATE",
				Global.sdf_2.format(date.getTime()));
		localContentValues.put("KEY_DATETIME",
				Global.sdf_1.format(date.getTime()));
		localContentValues.put("KEY_DATETIME_LONG",
				Long.valueOf(date.getTimeInMillis()));
		localContentValues.put("KEY_SLEEP_START",
				startTime);
		localContentValues.put("KEY_SLEEP_DEEP_MINUTES", deepTime);
		localContentValues.put("KEY_SLEEP_LIGHT_MINUTES", lightTime);
		localContentValues.put("KEY_SLEEP_TOTAL",totalTime);
		return this.db.update("TABLE_HISTORY_SLEEP", localContentValues, "KEY_DATE=?", new String[]{Global.sdf_2.format(date.getTime())});
	}
	
	public Cursor query_history_sleep() {
		
		Cursor localCursor = this.db.query(
				true,
				"TABLE_HISTORY_SLEEP",
				new String[] { "KEY_DATE","KEY_SLEEP_START", "KEY_SLEEP_DEEP_MINUTES", "KEY_SLEEP_LIGHT_MINUTES",
						"KEY_SLEEP_TOTAL" },
				null, null, null, null,
				null, null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}
	//add by lixiang for sleep 20150901 end
	public int update_history_hour(int paramInt1, Calendar paramCalendar,
			int paramInt2, double paramDouble1, double paramDouble2) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("KEY_DATE",
				Global.sdf_2.format(paramCalendar.getTime()));
		localContentValues.put("KEY_DATETIME",
				Global.sdf_1.format(paramCalendar.getTime()));
		localContentValues.put("KEY_DATETIME_LONG",
				Long.valueOf(paramCalendar.getTimeInMillis()));
		localContentValues.put("KEY_STEP", Integer.valueOf(paramInt2));
		localContentValues.put("KEY_BURN", Double.valueOf(paramDouble1));
		localContentValues.put("KEY_SLEEP_MOVE", Double.valueOf(paramDouble2));
		return this.db.update("TABLE_HISTORY_HOUR", localContentValues,
				"KEY_DATETIME_LONG=" + paramCalendar.getTimeInMillis()
						+ " and " + "KEY_PROFILE_ID" + "=" + paramInt1, null);
	}

	public long insert_ad_info(AdInfo ad) {
		String image = HttpUtils.getImageName(Global.density);
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("AD_ID",ad.getAd_id());
		localContentValues.put("AD_NUM",ad.getAd_num());
		if("imgSName".equals(image))
			localContentValues.put("IMG_NAME",ad.getImag_name());
		if("imgMName".equals(image))
			localContentValues.put("IMG_NAME", ad.getImag_name());
		if("imgBName".equals(image))
			localContentValues.put("IMG_NAME", ad.getImag_name());
		localContentValues.put("AD_URL",ad.getAd_url());
		localContentValues.put("AD_TYPE",ad.getAd_type());
		localContentValues.put("AD_ENDTIME",ad.getAd_endtime());
		return db.insert("TABLE_AD", null, localContentValues);
	}

	public Cursor query_ad_info() {
		Cursor localCursor = db.query(true, "TABLE_AD",
				new String[]{"IMG_NAME", "AD_URL", "AD_TYPE",
						"AD_ENDTIME"},
				null, null, null, null,
				null, null, null);
		if (localCursor != null)
			localCursor.moveToFirst();
		return localCursor;
	}

	public void clearAdInfo(){
		this.db.execSQL("DELETE FROM TABLE_AD;");
		this.db.execSQL("update sqlite_sequence set seq=0 where name='\"+TABLE_AD+\"'");
	}
}