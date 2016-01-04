package com.mtk.bluetoothle;

import java.io.Serializable;
import java.util.Calendar;

public class HistoryHour implements Serializable 
{
    private static final String TAG = "HistoryHour";
    private double burn;
  private Calendar date;
  private int sleepGrade;
  private int step;

  public double getBurn()
  {
    return this.burn;
  }

  public Calendar getDate()
  {
    return this.date;
  }

  public int getSleepGrade()
  {
    return this.sleepGrade;
  }

  public int getStep()
  {
//      Log.e(TAG, "getStep = "+step);
      return this.step;
  }

  public void setBurn(double paramDouble)
  {
    this.burn = paramDouble;
  }

  public void setDate(Calendar paramCalendar)
  {
    this.date = paramCalendar;
  }

  public void setSleepGrade(int paramInt)
  {
    this.sleepGrade = paramInt;
  }

  public void setStep(int paramInt)
  {

    this.step = paramInt;
//      Log.e(TAG, "setStep = "+step);
  }
}