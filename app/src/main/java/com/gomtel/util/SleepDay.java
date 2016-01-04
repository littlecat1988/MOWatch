package com.gomtel.util;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

public class SleepDay implements Serializable {
	private static final String TAG = "SleepDay";
	private long startSleepTime;
	private long deepSleepTime;
	private long lightSleepTime;
	private long wakeTime;
	private long totalTime;
	private Calendar date;

	public Calendar getDate() {
		return this.date;
	}

	public long getDeepSleepTime() {
		return this.deepSleepTime;
	}
	
	public long getStartSleepTime() {
		return this.startSleepTime;
	}

	public long getLightSleepTime() {
		return this.lightSleepTime;
	}

	public long getWakeTime() {
		return this.wakeTime;
	}

	public long getSleepTotal() {
		return this.totalTime;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public void setStartSleepTime(long paramInt) {
		this.startSleepTime = paramInt;
	}

	public void setDeepSleepTime(long paramInt) {
		this.deepSleepTime = paramInt;
	}

	public void setLightSleepTime(long paramInt) {
		this.lightSleepTime = paramInt;
	}

	public void setWakeTime(long paramInt) {
		this.wakeTime = paramInt;
	}

	public void setSleepTotal(long paramInt) {
		this.totalTime = paramInt;
	}

}

/*
 * Location:
 * D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name: com.gzgamut.max.been.HistoryDay JD-Core Version: 0.5.4
 */