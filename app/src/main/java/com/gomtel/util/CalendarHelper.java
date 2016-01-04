package com.gomtel.util;

import android.content.Context;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

public class CalendarHelper
{
  public static Calendar addADay(Calendar paramCalendar)
  {
    paramCalendar.set(Calendar.DAY_OF_MONTH, 1 + paramCalendar.get(Calendar.DAY_OF_MONTH));
    return paramCalendar;
  }

  public static Calendar addAMonth(Calendar paramCalendar)
  {
    paramCalendar.set(Calendar.MONTH, 1 + paramCalendar.get(Calendar.MONTH));
    return paramCalendar;
  }

  public static Calendar addAnHour(Calendar paramCalendar)
  {
    paramCalendar.set(Calendar.HOUR_OF_DAY, 1 + paramCalendar.get(Calendar.HOUR_OF_DAY));
    return paramCalendar;
  }

  public static Calendar get20010101Datetime()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(Calendar.YEAR, 2000);
    localCalendar.set(Calendar.MONTH, 0);
    localCalendar.set(Calendar.DAY_OF_MONTH, 1);
    localCalendar.set(Calendar.HOUR_OF_DAY, 0);
    localCalendar.set(Calendar.MINUTE, 0);
    localCalendar.set(Calendar.SECOND, 0);
    localCalendar.set(Calendar.MILLISECOND, 0);
    return localCalendar;
  }

  public static Calendar getDateBeforeYesterday(Calendar paramCalendar)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(paramCalendar.getTimeInMillis());
    return minADay(minADay(localCalendar));
  }

  public static Map<Calendar, HistoryDay> getDayMap(Calendar[] paramArrayOfCalendar)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Calendar localCalendar2 = null;
    Calendar localCalendar3 = null;
    if ((paramArrayOfCalendar != null) && (paramArrayOfCalendar.length == 2))
    {
      Calendar localCalendar1 = paramArrayOfCalendar[0];
      localCalendar2 = paramArrayOfCalendar[1];
      localCalendar3 = Calendar.getInstance();
      localCalendar3.setTimeInMillis(localCalendar1.getTimeInMillis());
    }
    while (true)
    {
      Calendar localCalendar4 = Calendar.getInstance();
      localCalendar4.setTimeInMillis(addADay(localCalendar3).getTimeInMillis());
      if (localCalendar4.equals(localCalendar2))
        return localLinkedHashMap;
      localLinkedHashMap.put(localCalendar4, null);
    }
  }

  public static String getDayOfWeekStr(Context paramContext, int paramInt)
  {
      if (paramInt == 2)
        return paramContext.getString(R.string.MON);
      if (paramInt == 3)
        return paramContext.getString(R.string.TUE);
      if (paramInt == 4)
        return paramContext.getString(R.string.WED);
      if (paramInt == 5)
        return paramContext.getString(R.string.THU);
      if (paramInt == 6)
        return paramContext.getString(R.string.FRI);
      if (paramInt == 7)
        return paramContext.getString(R.string.SAT);
      if (paramInt == 1)
         return paramContext.getString(R.string.SUN);
      return null;
  }

  public static Map<Calendar, HistoryHour> getHourMap(Calendar paramCalendar)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Calendar localCalendar1 = null;
    if (paramCalendar != null) {
        localLinkedHashMap.put(paramCalendar, null);
        localCalendar1 = Calendar.getInstance();
        localCalendar1.setTimeInMillis(paramCalendar.getTimeInMillis());
    }
while(true) {
    Calendar localCalendar2 = Calendar.getInstance();
    localCalendar2.setTimeInMillis(addAnHour(localCalendar1).getTimeInMillis());
    Calendar localCalendar3 = setHourFormat(localCalendar2);
    if (localCalendar3.get(Calendar.DAY_OF_MONTH) != paramCalendar.get(Calendar.DAY_OF_MONTH))
        return localLinkedHashMap;
        localLinkedHashMap.put(localCalendar3, null);
}

//      return localLinkedHashMap;
  }

  public static Calendar[] getLastMonthToTomorrow(Calendar paramCalendar)
  {
    Calendar localCalendar1 = Calendar.getInstance();
    localCalendar1.setTimeInMillis(paramCalendar.getTimeInMillis());
    localCalendar1.set(Calendar.MONTH, -1 + localCalendar1.get(Calendar.MONTH));
    Calendar localCalendar2 = setDayFormat(minADay(localCalendar1));
    Calendar localCalendar3 = Calendar.getInstance();
    localCalendar3.setTimeInMillis(paramCalendar.getTimeInMillis());
    return new Calendar[] { localCalendar2, setDayFormat(addADay(localCalendar3)) };
  }

  public static Calendar[] getLastWeekToTomorrow(Calendar paramCalendar)
  {
    Calendar localCalendar1 = Calendar.getInstance();
    localCalendar1.setTimeInMillis(paramCalendar.getTimeInMillis());
    localCalendar1.set(Calendar.DAY_OF_MONTH, -7 + localCalendar1.get(Calendar.DAY_OF_MONTH));
    Calendar localCalendar2 = setDayFormat(localCalendar1);
    Calendar localCalendar3 = Calendar.getInstance();
    localCalendar3.setTimeInMillis(paramCalendar.getTimeInMillis());
    return new Calendar[] { localCalendar2, setDayFormat(addADay(localCalendar3)) };
  }

  public static Calendar[] getLastYearToTomorrow(Calendar paramCalendar)
  {
    Calendar localCalendar1 = Calendar.getInstance();
    localCalendar1.setTimeInMillis(paramCalendar.getTimeInMillis());
    localCalendar1.set(Calendar.YEAR, -1 + localCalendar1.get(Calendar.YEAR));
    Calendar localCalendar2 = setDayFormat(localCalendar1);
    Calendar localCalendar3 = Calendar.getInstance();
    localCalendar3.setTimeInMillis(paramCalendar.getTimeInMillis());
    return new Calendar[] { localCalendar2, setDayFormat(addADay(localCalendar3)) };
  }

  public static String getMonthStr(Context paramContext, int paramInt)
  {
    String str;
      if (paramInt == 0)
        return paramContext.getString(R.string.JAN);
      if (paramInt == 1)
        return paramContext.getString(R.string.FEB);
      if (paramInt == 2)
        return paramContext.getString(R.string.MAR);
      if (paramInt == 3)
        return paramContext.getString(R.string.APR);
      if (paramInt == 4)
        return paramContext.getString(R.string.MAY);
      if (paramInt == 5)
        return paramContext.getString(R.string.JUN);
      if (paramInt == 6)
        return paramContext.getString(R.string.JUL);
      if (paramInt == 7)
        return paramContext.getString(R.string.AUG);
      if (paramInt == 8)
        return paramContext.getString(R.string.SEP);
      if (paramInt == 9)
        return paramContext.getString(R.string.OCT);
      if (paramInt == 10)
        return paramContext.getString(R.string.NOV);
      if (paramInt == 11)
        return paramContext.getString(R.string.DEC);
    return null;
  }

  public static Calendar getToday()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(Calendar.HOUR_OF_DAY, 0);
    localCalendar.set(Calendar.MINUTE, 0);
    localCalendar.set(Calendar.SECOND, 0);
    localCalendar.set(Calendar.MILLISECOND, 0);
    return localCalendar;
  }

  public static Calendar[] getTodayToTomorrow(Calendar paramCalendar)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(paramCalendar.getTimeInMillis());
    return new Calendar[] { paramCalendar, setDayFormat(addADay(localCalendar)) };
  }

  public static Calendar getTomorrow(Calendar paramCalendar)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(paramCalendar.getTimeInMillis());
    return setDayFormat(addADay(localCalendar));
  }


  public static Calendar getYesterday(Calendar paramCalendar)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(paramCalendar.getTimeInMillis());
    return minADay(localCalendar);
  }

  public static Calendar[] getYesterdayToTomorrow(Calendar paramCalendar)
  {
    Calendar localCalendar1 = Calendar.getInstance();
    localCalendar1.setTimeInMillis(paramCalendar.getTimeInMillis());
    Calendar localCalendar2 = setDayFormat(minADay(localCalendar1));
    Calendar localCalendar3 = Calendar.getInstance();
    localCalendar3.setTimeInMillis(paramCalendar.getTimeInMillis());
    return new Calendar[] { localCalendar2, setDayFormat(addADay(localCalendar3)) };
  }

  public static Calendar minADay(Calendar paramCalendar)
  {
    paramCalendar.set(Calendar.DAY_OF_MONTH, -1 + paramCalendar.get(Calendar.DAY_OF_MONTH));
    return paramCalendar;
  }

  public static void setDateInformation(Context paramContext, Calendar paramCalendar, TextView paramTextView)
  {
    int i = paramCalendar.get(Calendar.DAY_OF_MONTH);
    int j = paramCalendar.get(Calendar.MONTH);
    String str1 = getDayOfWeekStr(paramContext, paramCalendar.get(Calendar.DAY_OF_WEEK));
    String str2 = getMonthStr(paramContext, j);
    if ((str1 == null) || (str2 == null))
      return;
    paramTextView.setText(str1 + " " + Global.df_1.format(i) + " " + str2);
  }

  public static Calendar setDayFormat(Calendar paramCalendar)
  {
      paramCalendar.set(Calendar.HOUR_OF_DAY, 0);
      paramCalendar.set(Calendar.MINUTE, 0);
      paramCalendar.set(Calendar.SECOND, 0);
      paramCalendar.set(Calendar.MILLISECOND, 0);
    return paramCalendar;
  }

  public static Calendar setHourFormat(Calendar paramCalendar)
  {
      paramCalendar.set(Calendar.MINUTE, 0);
      paramCalendar.set(Calendar.SECOND, 0);
      paramCalendar.set(Calendar.MILLISECOND, 0);
    return paramCalendar;
  }

  public static Calendar setMonthFormat(Calendar paramCalendar)
  {
      paramCalendar.set(Calendar.DAY_OF_MONTH, 1);
      paramCalendar.set(Calendar.HOUR_OF_DAY, 0);
      paramCalendar.set(Calendar.MINUTE, 0);
      paramCalendar.set(Calendar.SECOND, 0);
      paramCalendar.set(Calendar.MILLISECOND, 0);
    return paramCalendar;
  }
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.watch.paick.helper.CalendarHelper
 * JD-Core Version:    0.5.4
 */