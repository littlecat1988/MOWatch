package com.mtk.app.yahooweather;

import com.mediatek.ctrl.yahooweather.City;
import com.mediatek.ctrl.yahooweather.YahooWeatherController;
import com.mtk.btnotification.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class YWSettingActivity extends PreferenceActivity{
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private Preference mCitySearch;
	private Preference mTpTypeSet;
	private City mYahooCity;
	private String mCity = null;
	
	private AlertDialog mAlertDialog;
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.yahooweather_preference);
        mContext = this;
        mCity = YahooWeatherController.sCity.getName();
        
        mTpTypeSet = findPreference("temperature_units");
        Preference.OnPreferenceClickListener onTpTypeSetClickListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isFastDoubleClick()) {
                    return true;
                }
            	showTpTypeSetDialog();
            	return true;
            }
			
        };
        mTpTypeSet.setOnPreferenceClickListener(onTpTypeSetClickListener);
        if (YahooWeatherController.getTemperatureType() == YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_CEL) {
        	mTpTypeSet.setSummary(R.string.celsius);
        }
        else {
        	mTpTypeSet.setSummary(R.string.fahrenheit);
        }
        
        mCitySearch =  findPreference("city");    
        Preference.OnPreferenceClickListener onCitySearchClickListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isFastDoubleClick()) {
                    return true;
                }
                Intent intent = new Intent(YWSettingActivity.this, CitySearchActivity.class);
                startActivityForResult(intent, 100);
                return true;
            }
        };
        mCitySearch.setOnPreferenceClickListener(onCitySearchClickListener);
        if (mCity == null) {
        	mCitySearch.setSummary(R.string.no_city);
        }
        else {
        	mCitySearch.setSummary(getString(R.string.current_city, mCity));
        }
	}
	
	 @Override
	public void onResume() {
        super.onResume();
        if (YahooWeatherController.getTemperatureType() == YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_CEL) {
        	mTpTypeSet.setSummary(R.string.celsius);
        }
        else {
        	mTpTypeSet.setSummary(R.string.fahrenheit);
        }     
    }
	 
	private void showTpTypeSetDialog() {		
		
		LinearLayout linearLayoutMain = new LinearLayout(this);
		linearLayoutMain.setLayoutParams(new LayoutParams(  
		        LayoutParams.MATCH_PARENT, 300));  
		ListView listView = new ListView(this);
		  
		TemperatureAdapter adapter = new TemperatureAdapter(mContext);
		listView.setAdapter(adapter);  
		  
		linearLayoutMain.addView(listView);
		
		mAlertDialog = new AlertDialog.Builder(this)  
        .setTitle(R.string.temp_unit).setView(linearLayoutMain)
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  
  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.cancel();  
            }  
        }).create();
        
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.show(); 
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_CEL) {
				    YahooWeatherController.setTemperatureType(YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_CEL);
				}
				else {
				    YahooWeatherController.setTemperatureType(YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_FAH);
				}
				if (YahooWeatherController.getTemperatureType() == YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_CEL) {
		        	mTpTypeSet.setSummary(R.string.celsius);
		        }
		        else {
		        	mTpTypeSet.setSummary(R.string.fahrenheit);
		        }   
				mAlertDialog.cancel();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(100 == requestCode)
        {
            if (RESULT_OK == resultCode) {

                mYahooCity = (City)data.getExtras().getParcelable(CitySearchActivity.SEARCH_CITY_RESULT);
                YahooWeatherController.setCurrentCity(mContext, mYahooCity);
                mCity = mYahooCity.getName();
                YahooWeatherController.getInstance(mContext).sendYWConnected();
                
                if (mCity == null) {
                	mCitySearch.setSummary(R.string.no_city);
                }
                else {
                	mCitySearch.setSummary(getString(R.string.current_city, mCity));
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	public class TemperatureAdapter extends BaseAdapter {
		
        private YWSettingActivity activity; 
		
		
		public TemperatureAdapter(Context context) {
            this.activity = (YWSettingActivity)context;  
            mInflater = activity.getLayoutInflater(); 
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			view = mInflater.inflate(R.layout.temperature_set_layout, null);
			view.setPadding(0, 0, 20, 30);
			TextView textView = (TextView)view.findViewById(R.id.tptype_text);
			if (position == YahooWeatherController.YAHOO_WEATHER_TEMPERATURE_TYPE_CEL) {
				textView.setText(R.string.celsius);
			}
			else {
				textView.setText(R.string.fahrenheit);
			}
			return view;
		}
		
	}

    private long mLastClickTime = 0L;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;
    }
}
