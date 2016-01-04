package com.gomtel.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Global
{
	public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/Watch/myPortrait/";
    public static final String AD_PATH = Environment.getExternalStorageDirectory().getPath() + "/Watch/myAd/";
  public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + "/Watch/download/";
  public static final DecimalFormat df_1 = new DecimalFormat("00");
  public static final DecimalFormat df_1_1 = new DecimalFormat("0");
  public static final DecimalFormat df_2 = new DecimalFormat("0.0");
  public static final DecimalFormat df_3 = new DecimalFormat("0.00");

  @SuppressLint({"SimpleDateFormat"})
  public static final SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  @SuppressLint({"SimpleDateFormat"})
  public static final SimpleDateFormat sdf_2 = new SimpleDateFormat("yyyy-MM-dd");

  @SuppressLint({"SimpleDateFormat"})
  public static final SimpleDateFormat sdf_3 = new SimpleDateFormat("yyyy-MM-dd HH");
    public static final SimpleDateFormat sdf_4 = new SimpleDateFormat("MM-dd");
    public static final SimpleDateFormat sdf_5 = new SimpleDateFormat("HH:mm");
  public static final String STARTTIME = " 00:00:00";
  public static final String ENDTIME = " 23:59:59";
  public static final String MOB_AD = "mob_ad";
  public static final String SPORT_TYPE = "sportType";
  public static final String SPORT_STEP = "qty";
  public static final String SPORT_STARTTIME = "startTime";
  public static final String SPORT_ENDTIME = "endTime";
  public static final String SPORT_NUMBER = "sportNum";
  public static final String HEIGHT = "HEIGHT";
  public static final long HOUR = 60*1000;
  public static final String URL_GETSLEEPINFO = "http://220.231.193.48:8080/GTSmartDevice/sleepInfo.do";
  public static final long MIN = 60 * 1000;
  public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;
  public static final String URL_GETTOTALSTEP = "http://220.231.193.48:8080/GTSmartDevice/downloadTotalTakeSportInfo.do";
  public static final String URL_DOWNLOAD_SOFTWARE = "http://220.231.193.48:8080/GTSmartDevice/downloadAppInfo.do";


  public static float density = -1;
  public static final String URL_HEARTBEAT = "http://220.231.193.48:8080/GTSmartDevice/appHeartbeat.do";
  public static final String URL_ADINFO = "http://220.231.193.48:8080/GTSmartDevice/doAdvertInfo.do";;
  public static final String URL_DOWNLOADIMG = "http://220.231.193.48:8080/GTSmartDevice/doAdvertImg.do";
  public static final String URL_GETTOTALSPORTS = "http://220.231.193.48:8080/GTSmartDevice/downloadTotalCompositeSportInfo.do";
  public static final String URL_GETDETAILSPORT = "http://220.231.193.48:8080/GTSmartDevice/downloadDetailCompositeSportInfo.do";
  public static final String AD_IDS = "AD_IDS";
  public static final String AD_TYPE_NOTI = "1";
  public static final String AD_TYPE_WELCOM = "2";
  public static final String AD_TYPE_BOTTOM = "3";
  public static final String PUBLISHER_ID = "56OJ2h1IuNw/XYiFF2";
  public static final String SPLASH_PPID = "16TLP-boApSJANUUNEAC5mYs";
  public static final String BANNER_ID = "16TLP-boApSJANUUNykPDiis";
  public static final double HEIGHT_PARAM = 0.37;
  public static final double CAL_PARAM = 0.069;
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.gzgamut.max.global.Global
 * JD-Core Version:    0.5.4
 */