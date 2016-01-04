package com.gomtel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.PrintStream;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static String TAG = "DatabaseOpenHelper";

    public DatabaseOpenHelper(Context paramContext, String paramString,
                              CursorFactory paramCursorFactory, int paramInt) {
        super(paramContext, paramString, paramCursorFactory, paramInt);
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_DAY(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATE_LONG long not null, KEY_STEP int not null, KEY_BURN double not null, KEY_DISTANCE int not null, KEY_PROFILE_ID int not null); ");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_HOUR(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_DATETIME_LONG long not null, KEY_STEP int not null, KEY_BURN double not null, KEY_SLEEP_MOVE int not null, KEY_PROFILE_ID int not null); ");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_SLEEP(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_DATETIME_LONG long not null, KEY_SLEEP_START long not null, KEY_SLEEP_DEEP_MINUTES long not null, KEY_SLEEP_LIGHT_MINUTES long not null, KEY_SLEEP_TOTAL long not null,KEY_PROFILE_ID int not null); ");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS TABLE_HISTORY_HR(_id integer primary key autoincrement, KEY_DATE text not null, KEY_DATETIME text not null, KEY_HEARTRATE int not null, KEY_PROFILE_ID int not null); ");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS TABLE_AD(_id integer primary key autoincrement, AD_ID text not null, AD_NUM text not null, IMG_NAME text not null, AD_URL text not null, AD_TYPE text not null, AD_ENDTIME text not null); ");
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
                          int paramInt2) {
        Log.i(TAG, "update database      old version = " + paramInt1
                + "    new version = " + paramInt2);
        if (paramInt1 != 1)
            return;
//		paramSQLiteDatabase.beginTransaction();
//		paramSQLiteDatabase
//				.execSQL("ALTER TABLE TABLE_PROFILE RENAME TO TEMP_TABLE_PROFLIE");
//		paramSQLiteDatabase
//				.execSQL("CREATE TABLE IF NOT EXISTS TABLE_PROFILE(_id integer primary key autoincrement, KEY_NAME text not null, KEY_LANGUAGE text not null, KEY_AGE int not null, KEY_GENDER int not null, KEY_HEIGHT double not null, KEY_WEIGHT double not null, KEY_DATETIME_BEGIN int not null, KEY_DATETIME_END int not null, KEY_TIME_REMIND int not null); ");
//		System.out
//				.println("INSERT DATA = insert into TABLE_PROFILE select _id, KEY_NAME, KEY_LANGUAGE, '', KEY_GENDER, KEY_HEIGHT, KEY_WEIGHT, KEY_DATETIME_BEGIN, KEY_DATETIME_END, KEY_TIME_REMINDfrom TEMP_TABLE_PROFLIE");
//		paramSQLiteDatabase
//				.execSQL("insert into TABLE_PROFILE select _id, KEY_NAME, KEY_LANGUAGE, '', KEY_GENDER, KEY_HEIGHT, KEY_WEIGHT, KEY_DATETIME_BEGIN, KEY_DATETIME_END, KEY_TIME_REMINDfrom TEMP_TABLE_PROFLIE");
//		paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS TEMP_TABLE_PROFLIE");
//		paramSQLiteDatabase.setTransactionSuccessful();
//		paramSQLiteDatabase.endTransaction();
    }
}
