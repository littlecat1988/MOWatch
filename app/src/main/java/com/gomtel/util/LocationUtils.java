package com.gomtel.util;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;

public class LocationUtils {
	private final String TAG = "LocationUtils";
	private static LocationUtils sInstance;
	private LocationManager mLocationManager;
	private Context mContext;
	private boolean gpsOpen = false;

	private LocationUtils(Context c) {
		mContext = c;
	}

	public static synchronized LocationUtils getInstance(Context c) {
		if (sInstance == null) {
			sInstance = new LocationUtils(c);
		}
		return sInstance;
	}

	private void openGps() {
//		gpsOpen = Settings.Secure.isLocationProviderEnabled(
//				mContext.getContentResolver(), LocationManager.GPS_PROVIDER);
//		if (!gpsOpen) {
//			Settings.Secure.setLocationProviderEnabled(
//					mContext.getContentResolver(),
//					LocationManager.GPS_PROVIDER, true);
//		}

		LocationManager locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			Intent gpsIntent = new Intent();
			gpsIntent.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
			gpsIntent.setData(Uri.parse("custom:3"));
			try {
				PendingIntent.getBroadcast(mContext, 0, gpsIntent, 0)
						.send();
			} catch (CanceledException e) {
				e.printStackTrace();
			}

		}
	}

	public void startLocation(){
		getLocationCity();
	}
	
	public void cancel(){
		if(mLocationManager!=null){
			mLocationManager.removeUpdates(mLocationListener);
		}
	}
	public void getLocationCity() {
		// openGps();
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		// Location location =
		// mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 10, mLocationListener);

	}

	private final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location != null) {

				double lat = location.getLatitude();

				double lng = location.getLongitude();
				Log.d(TAG, "zhjp , lat = " + lat + ", lng = " + lng);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};
}
