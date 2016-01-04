package com.gomtel.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.util.Log;

import com.gomtel.util.AdInfo;
import com.gomtel.util.CalendarHelper;
import com.gomtel.util.Global;
import com.gomtel.util.HistoryDay;
import com.gomtel.util.SleepDay;
import com.mtk.bluetoothle.HistoryHour;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DatabaseProvider
{
    private static final String TAG = "DATABASEPRROVIDER";

    public static void deleteADayHistoryHour(Context paramContext, int paramInt, Calendar paramCalendar)
  {
    if (paramCalendar == null)
      return;
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.delete_history_hour_aday(paramInt, paramCalendar);
    localDatabaseAdapter.closeDatabase();
  }

  public static void deleteAllTable(Context paramContext)
  {
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.deleteAllData();
    localDatabaseAdapter.closeDatabase();
  }

  public static void deleteHistoryAfterNow(Context paramContext, Calendar paramCalendar)
  {
    System.out.println("*******************************************delect");
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.delete_history_day_after_now(paramCalendar);
    localDatabaseAdapter.delete_history_hour_after_now(paramCalendar);
    localDatabaseAdapter.closeDatabase();
  }

  public static void deleteHistoryDate(Context paramContext, int paramInt, Calendar paramCalendar)
  {
    if (paramCalendar == null)
      return;
    Calendar localCalendar = CalendarHelper.setDayFormat(paramCalendar);
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.delete_history_day(paramInt, localCalendar);
    localDatabaseAdapter.closeDatabase();
  }

  public static void insertHistoryDate(Context paramContext, int paramInt, HistoryDay paramHistoryDay)
  {
    if (paramHistoryDay == null)
      return;
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.insert_history_day(paramInt, CalendarHelper.setDayFormat(paramHistoryDay.getDate()), paramHistoryDay.getStep(), paramHistoryDay.getBurn(), paramHistoryDay.getDistance());
    localDatabaseAdapter.closeDatabase();
  }

  public static void insertHistoryHour(Context paramContext, int paramInt, HistoryHour paramHistoryHour)
  {
    if (paramHistoryHour == null)
      return;
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.insert_history_hour(paramInt, CalendarHelper.setHourFormat(paramHistoryHour.getDate()), paramHistoryHour.getStep(), paramHistoryHour.getBurn(), paramHistoryHour.getSleepGrade());
    localDatabaseAdapter.closeDatabase();
  }

  public static HistoryDay queryHistoryDate(Context paramContext, int paramInt, Calendar paramCalendar) {
    if ((paramContext != null) && (paramCalendar != null))
    {
      Calendar localCalendar1 = CalendarHelper.setDayFormat(paramCalendar);
      DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
      localDatabaseAdapter.openDatabase();
      Cursor localCursor = localDatabaseAdapter.query_history_day(paramInt, localCalendar1);
//        Log.e(TAG,"lixiang---localCursor= "+localCursor);

      HistoryDay localHistoryDay = null;
      Object localObject = null;
        Date date = new Date();;
      if (localCursor.moveToFirst())
      {
          localHistoryDay = new HistoryDay();
//          date = Global.sdf_2.parse(localCursor.getString(0));
      }else{
          localDatabaseAdapter.closeDatabase();
          return null;
      }
      try
      {
        Date localDate = Global.sdf_2.parse(localCursor.getString(0));
        localObject = localDate;
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar2.setTime(date);
        localHistoryDay.setDate(localCalendar2);
        localHistoryDay.setStep(localCursor.getInt(1));
        localHistoryDay.setBurn(localCursor.getDouble(2));
        localHistoryDay.setDistance(localCursor.getDouble(3));
        localDatabaseAdapter.closeDatabase();
        return localHistoryDay;
      }
      catch (ParseException localParseException)
      {
        localParseException.printStackTrace();
//        break label86:
        localDatabaseAdapter.closeDatabase();
      }finally{
          localCursor.close();
      }
    }
    return (HistoryDay)null;
  }

  public static List<HistoryDay> queryHistoryDate(Context paramContext, int paramInt, Calendar localCalendar1, int days)
  {
    ArrayList localArrayList = new ArrayList();
    DatabaseAdapter localDatabaseAdapter = null;
    Cursor localCursor = null;
//    HistoryDay localHistoryDay = null;
    Object localObject = null;
    if ((paramContext != null) && (localCalendar1 != null))
    {
//      Calendar localCalendar1 = CalendarHelper.setDayFormat(paramCalendar1);
//      Calendar localCalendar2 = CalendarHelper.setDayFormat(paramCalendar2);
      localDatabaseAdapter = new DatabaseAdapter(paramContext);
      localDatabaseAdapter.openDatabase();
      localCursor = localDatabaseAdapter.query_history_day(paramInt, localCalendar1.getTimeInMillis()-days*Global.DAY_MILLIS, localCalendar1.getTimeInMillis());
      if (localCursor.moveToFirst())
      {

        localObject = new Date();
      }
    }
    try
    {
        do {
            HistoryDay localHistoryDay = new HistoryDay();
            Date localDate = Global.sdf_2.parse(localCursor.getString(0));
            localObject = localDate;
            Calendar localCalendar3 = Calendar.getInstance();
            localCalendar3.setTime((Date) localObject);
            localHistoryDay.setDate(localCalendar3);
            localHistoryDay.setStep(localCursor.getInt(1));
            localHistoryDay.setBurn(localCursor.getDouble(2));
            localHistoryDay.setDistance(localCursor.getDouble(3));
            localArrayList.add(localHistoryDay);
        }while(localCursor.moveToNext());
      localDatabaseAdapter.closeDatabase();

    }
    catch (ParseException localParseException)
    {
        localCursor.close();
      localParseException.printStackTrace();
    }
      localCursor.close();
      return localArrayList;
  }

  public static HistoryHour queryHistoryHour(Context paramContext, int paramInt, Calendar paramCalendar)
  {
    if ((paramContext != null) && (paramCalendar != null))
    {
      Calendar localCalendar1 = CalendarHelper.setHourFormat(paramCalendar);
      DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
      localDatabaseAdapter.openDatabase();
      Cursor localCursor = localDatabaseAdapter.query_history_hour(paramInt, localCalendar1);
      HistoryHour localHistoryHour = null;
      Object localObject2 = null;
      try
      {
        if (localCursor.moveToFirst())
          localObject2 = new Date();
      }
      finally
      {
        try
        {
          Date localDate = Global.sdf_3.parse(localCursor.getString(0));
          localObject2 = localDate;
          Calendar localCalendar2 = Calendar.getInstance();
          localCalendar2.setTime((Date)localObject2);
          localHistoryHour.setDate(localCalendar2);
          localHistoryHour.setStep(localCursor.getInt(1));
          localHistoryHour.setBurn(localCursor.getDouble(2));
          localHistoryHour.setSleepGrade(localCursor.getInt(3));
          localDatabaseAdapter.closeDatabase();
          if (localCursor != null)
            localCursor.close();
          return localHistoryHour;
        }
        catch (ParseException localParseException) {
            localParseException.printStackTrace();
        }
          finally{
          if (localCursor != null)
            localCursor.close();
        }
      }
      if (localCursor != null)
        localCursor.close();
      localDatabaseAdapter.closeDatabase();
    }
    return (HistoryHour)null;
  }

  public static List<HistoryHour> queryHistoryHour(Context paramContext, int paramInt, Calendar paramCalendar1, Calendar paramCalendar2)
  {
    ArrayList localArrayList = new ArrayList();
    DatabaseAdapter localDatabaseAdapter = null;
    Cursor localCursor = null;
    HistoryHour localHistoryHour = null;
    Object localObject;
    if ((paramContext != null) && (paramCalendar1 != null) && (paramCalendar2 != null))
    {
      Calendar localCalendar1 = CalendarHelper.setDayFormat(paramCalendar1);
      Calendar localCalendar2 = CalendarHelper.setDayFormat(paramCalendar2);
      localDatabaseAdapter = new DatabaseAdapter(paramContext);
      localDatabaseAdapter.openDatabase();
      localCursor = localDatabaseAdapter.query_history_hour(paramInt, localCalendar1, localCalendar2);
//        Log.e(TAG,"queryHistoryHour01= "+localCursor.moveToFirst());
      if (localCursor.moveToFirst())
      {
          try {
//              Log.e(TAG,"queryHistoryHour03= ");
              localHistoryHour = new HistoryHour();
              localObject = new Date();
              Date localDate = Global.sdf_3.parse(localCursor.getString(0));
              localObject = localDate;
              Calendar localCalendar3 = Calendar.getInstance();
              localCalendar3.setTime((Date) localObject);
              localHistoryHour.setDate(localCalendar3);
              localHistoryHour.setStep(localCursor.getInt(1));
//              Log.e(TAG, "queryHistoryHour---step= " + localCursor.getInt(1));
              localHistoryHour.setBurn(localCursor.getDouble(2));
              localHistoryHour.setSleepGrade(localCursor.getInt(3));
              localArrayList.add(localHistoryHour);
          } catch (ParseException localParseException) {
              localParseException.printStackTrace();
              localDatabaseAdapter.closeDatabase();
          }
while(localCursor.moveToNext()) {
    try {
//        Log.e(TAG,"queryHistoryHour03= ");
        localHistoryHour = new HistoryHour();
        localObject = new Date();
        Date localDate = Global.sdf_3.parse(localCursor.getString(0));
        localObject = localDate;
        Calendar localCalendar3 = Calendar.getInstance();
        localCalendar3.setTime((Date) localObject);
        localHistoryHour.setDate(localCalendar3);
        localHistoryHour.setStep(localCursor.getInt(1));
//        Log.e(TAG, "queryHistoryHour---step= " + localCursor.getInt(1));
        localHistoryHour.setBurn(localCursor.getDouble(2));
        localHistoryHour.setSleepGrade(localCursor.getInt(3));
        localArrayList.add(localHistoryHour);


    } catch (ParseException localParseException) {
        localParseException.printStackTrace();
        localDatabaseAdapter.closeDatabase();
    }
}
          localDatabaseAdapter.closeDatabase();
      }
    }
      return localArrayList;
  }

 
  public static void saveHistoryDay(Context paramContext,int step,double cal,double distance, Calendar date, int paramInt)
  {
    if(paramInt == -1)
        return ;
      DatabaseAdapter databaseadapter;
      SQLiteDatabase sqlitedatabase;
      databaseadapter = new DatabaseAdapter(paramContext);
      databaseadapter.openDatabase();
      sqlitedatabase = databaseadapter.getSQLiteDatabase();
      sqlitedatabase.beginTransaction();
      try{
              Cursor cursor = databaseadapter.query_history_day(paramInt, CalendarHelper.setDayFormat(date));
              if(cursor != null && cursor.moveToFirst()) {
//                  Log.e(TAG,"saveHistoryHour---step= "+historyhour.getStep());
//                  Log.e(TAG,"saveHistoryHour---historyhour= "+historyhour.getDate());
                  databaseadapter.update_history_day(paramInt, CalendarHelper.setDayFormat(date), step, cal, distance);
                  cursor.close();
              }
              if(cursor != null && !cursor.moveToFirst()){
                  databaseadapter.insert_history_day(paramInt, CalendarHelper.setDayFormat(date), step, cal, distance);
                  cursor.close();
              }


      sqlitedatabase.setTransactionSuccessful();
      }finally {
          sqlitedatabase.endTransaction();
      }

      databaseadapter.closeDatabase();


  }
  //add by lixiang for sleep 20150910
  public static void saveSleepHistory(Context paramContext,long startTime, long deepTime,
			long lightTime, long totalTime, int paramInt,Calendar date)
  {
    if(paramInt == -1)
        return ;
      DatabaseAdapter databaseadapter;
      SQLiteDatabase sqlitedatabase;
      databaseadapter = new DatabaseAdapter(paramContext);
      databaseadapter.openDatabase();
      sqlitedatabase = databaseadapter.getSQLiteDatabase();
      sqlitedatabase.beginTransaction();
      try{
              Cursor cursor = databaseadapter.query_history_sleep(paramInt, date);
              
              if(cursor != null && cursor.moveToFirst()){
            	  Log.e(TAG,"sleep update");
                  databaseadapter.update_sleep_history(startTime, deepTime,
                			lightTime, totalTime,date);
                  cursor.close();
              }
              if(cursor != null && !cursor.moveToFirst()){
            	  Log.e(TAG,"sleep insert");
                  databaseadapter.insert_sleep_history(startTime, deepTime,
              			lightTime, totalTime,date);
                  cursor.close();
              }
             


      sqlitedatabase.setTransactionSuccessful();
      }finally {
          sqlitedatabase.endTransaction();
      }

      databaseadapter.closeDatabase();
  }
      public static List<SleepDay> queryHistorySleep(Context context)
      {
        ArrayList localArrayList = new ArrayList();
        DatabaseAdapter localDatabaseAdapter = null;
        Cursor localCursor = null;
        SleepDay sleepDay = null;
        Object localObject;
        
          localDatabaseAdapter = new DatabaseAdapter(context);
          localDatabaseAdapter.openDatabase();
          localCursor = localDatabaseAdapter.query_history_sleep();
//            Log.e(TAG,"queryHistorysleep= "+localCursor.moveToFirst());
          if (localCursor.moveToFirst())
          {
              try {
                 
            	  sleepDay = new SleepDay();
                  localObject = new Date();
                  Date localDate = Global.sdf_2.parse(localCursor.getString(0));
                  localObject = localDate;
                  Calendar localCalendar3 = Calendar.getInstance();
                  localCalendar3.setTime((Date) localObject);
                  sleepDay.setDate(localCalendar3);
                  sleepDay.setStartSleepTime(localCursor.getLong(1));
                  sleepDay.setDeepSleepTime(localCursor.getLong(2));
                  sleepDay.setLightSleepTime(localCursor.getLong(3));
                  sleepDay.setSleepTotal(localCursor.getLong(4));
                  localArrayList.add(sleepDay);
//                  Log.e(TAG,"queryHistorysleep= "+localArrayList.size());
              } catch (ParseException localParseException) {
                  localParseException.printStackTrace();
                  localDatabaseAdapter.closeDatabase();
              }
    while(localCursor.moveToNext()) {
        try {
//            Log.e(TAG,"queryHistoryHour03= ");
        	sleepDay = new SleepDay();
            localObject = new Date();
            Date localDate = Global.sdf_3.parse(localCursor.getString(0));
            localObject = localDate;
            Calendar localCalendar3 = Calendar.getInstance();
            localCalendar3.setTime((Date) localObject);
            sleepDay.setDate(localCalendar3);
            sleepDay.setStartSleepTime(localCursor.getLong(1));
            sleepDay.setDeepSleepTime(localCursor.getLong(2));
            sleepDay.setLightSleepTime(localCursor.getLong(3));
            sleepDay.setSleepTotal(localCursor.getLong(4));
            localArrayList.add(sleepDay);

        } catch (ParseException localParseException) {
            localParseException.printStackTrace();
            localDatabaseAdapter.closeDatabase();
        }
    }
              localDatabaseAdapter.closeDatabase();
          }
        
          return localArrayList;
      
  }
//add by lixiang for sleep 20150910
    public static void saveHistoryHour(Context paramContext, List<HistoryHour> paramList, int paramInt)
    {
        if(paramInt == -1 || paramList == null)
            return ;
        DatabaseAdapter databaseadapter;
        SQLiteDatabase sqlitedatabase;
        databaseadapter = new DatabaseAdapter(paramContext);
        databaseadapter.openDatabase();
        sqlitedatabase = databaseadapter.getSQLiteDatabase();
        sqlitedatabase.beginTransaction();
        Iterator iterator = paramList.iterator();
        try{
            while(iterator.hasNext()){
                HistoryHour historyhour = (HistoryHour)iterator.next();
                if(historyhour != null){
                    Cursor cursor = databaseadapter.query_history_hour(paramInt, historyhour.getDate());
                    if(cursor != null && cursor.moveToFirst()){
//                  Log.e(TAG,"saveHistoryHour---step= "+historyhour.getStep());
//                  Log.e(TAG,"saveHistoryHour---historyhour= "+historyhour.getDate());
                        databaseadapter.update_history_hour(paramInt, CalendarHelper.setHourFormat(historyhour.getDate()), historyhour.getStep(), historyhour.getBurn(), historyhour.getSleepGrade());
                        cursor.close();
                    }
                    if(cursor != null && !cursor.moveToFirst()){
                        databaseadapter.insert_history_hour(paramInt, CalendarHelper.setHourFormat(historyhour.getDate()), historyhour.getStep(), historyhour.getBurn(), historyhour.getSleepGrade());
                        cursor.close();
                    }
                }
            }
            sqlitedatabase.setTransactionSuccessful();
        }finally {
            sqlitedatabase.endTransaction();
        }

        databaseadapter.closeDatabase();


    }
    //add by lixiang for sleep 20150910

  public static void updateHistoryDate(Context paramContext, int paramInt, HistoryDay paramHistoryDay)
  {
    if (paramHistoryDay == null)
      return;
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.update_history_day(paramInt, CalendarHelper.setDayFormat(paramHistoryDay.getDate()), paramHistoryDay.getStep(), paramHistoryDay.getBurn(), paramHistoryDay.getDistance());
    localDatabaseAdapter.closeDatabase();
  }

  public static void updateHistoryHour(Context paramContext, int paramInt, HistoryHour paramHistoryHour)
  {
    if (paramHistoryHour == null)
      return;
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(paramContext);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.update_history_hour(paramInt, CalendarHelper.setHourFormat(paramHistoryHour.getDate()), paramHistoryHour.getStep(), paramHistoryHour.getBurn(), paramHistoryHour.getSleepGrade());
    localDatabaseAdapter.closeDatabase();
  }
//add by lixiang for ad 20151120 begin
public static void insertAdInfo(Context context, AdInfo ad)
{
    if (ad == null)
        return;
    DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(context);
    localDatabaseAdapter.openDatabase();
    localDatabaseAdapter.insert_ad_info(ad);
    localDatabaseAdapter.closeDatabase();
}
    public static List<AdInfo> queryAdInfo(Context context)
    {
        ArrayList localArrayList = new ArrayList();
        DatabaseAdapter localDatabaseAdapter = null;
        Cursor localCursor = null;
        AdInfo adInfo = null;

        localDatabaseAdapter = new DatabaseAdapter(context);
        localDatabaseAdapter.openDatabase();
        localCursor = localDatabaseAdapter.query_ad_info();
        if (localCursor.moveToFirst())
        {
            adInfo = new AdInfo();
            adInfo.setImag_name(localCursor.getString(0));
            adInfo.setAd_url(localCursor.getString(1));
            adInfo.setAd_type(localCursor.getString(2));
            adInfo.setAd_endtime(localCursor.getString(3));
            localArrayList.add(adInfo);
//                  Log.e(TAG,"queryHistorysleep= "+localArrayList.size());
            while(localCursor.moveToNext()) {
                adInfo = new AdInfo();
                adInfo.setImag_name(localCursor.getString(0));
                adInfo.setAd_url(localCursor.getString(1));
                adInfo.setAd_type(localCursor.getString(2));
                adInfo.setAd_endtime(localCursor.getString(3));
                localArrayList.add(adInfo);
            }
            localCursor.close();
            localDatabaseAdapter.closeDatabase();
        }

        return localArrayList;

    }
    public static void clearAdInfo(Context context)
    {
        DatabaseAdapter localDatabaseAdapter = new DatabaseAdapter(context);
        localDatabaseAdapter.openDatabase();
        localDatabaseAdapter.clearAdInfo();
        localDatabaseAdapter.closeDatabase();
    }
    //add by lixiang for ad 20151120 end
}