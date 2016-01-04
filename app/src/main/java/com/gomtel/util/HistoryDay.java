package com.gomtel.util;

import android.util.Log;

import java.util.Calendar;

public class HistoryDay
{
    private static final String TAG = "HistoryDay";
    private int activeHour;
  private double burn;
  private Calendar date;
 private double distance;
  private int step;



  public double getBurn()
  {
    return this.burn;
  }

  public double getDistance()
  {
    return this.distance;
  }
  
  public Calendar getDate()
  {
    return this.date;
  }

 

  public int getStep()
  {
    return this.step;
  }



  public void setBurn(double paramDouble)
  {
    this.burn = paramDouble;
  }
  
  public void setDistance(double paramDouble)
  {
    this.distance = paramDouble;
  }


  public void setDate(Calendar paramCalendar)
  {
    this.date = paramCalendar;
  }



  public void setStep(int paramInt)
{
    this.step = paramInt;
    Log.e(TAG, "setStep = " + step);
  }
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.gzgamut.max.been.HistoryDay
 * JD-Core Version:    0.5.4
 */