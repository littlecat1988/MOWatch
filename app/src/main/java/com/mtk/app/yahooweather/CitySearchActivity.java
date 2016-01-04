/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.mtk.app.yahooweather;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.mediatek.ctrl.yahooweather.City;
import com.mediatek.ctrl.yahooweather.YahooWeatherController;
import com.mtk.btnotification.R;
import com.mtk.main.BTNotificationApplication;
import com.mtk.main.Utils;


public class CitySearchActivity extends Activity
        implements OnItemClickListener,View.OnClickListener {
    public static final String SEARCH_CITY_RESULT = "search_city_result";
    public static final int DELAY_TIME = 1000;

    private CityAdapter mListviewAdapter;
    private EditText mEditCityName;
    private ListView mListView;
    private ImageView mButtonSearch;
    private View mSearching;
    private InputMethodManager mImm;
    private Toast mToast;
    private static Context sContext = BTNotificationApplication.getInstance().getApplicationContext();
    

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTitle(R.string.choose_city);
            setContentView(R.layout.choose_city);
            mListView = (ListView) findViewById(R.id.list_view);
            mEditCityName = (EditText) findViewById(R.id.edit);
            mListviewAdapter = new CityAdapter(this);
            mListView.setAdapter(mListviewAdapter);

            mEditCityName.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                    if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                        searchCity();
                        return true;
                    }
                    return false;
                }
            });
            mButtonSearch = (ImageView) findViewById(R.id.ok_btn);
            mSearching = findViewById(R.id.searching);
            mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            mButtonSearch.setVisibility(View.VISIBLE);
            mButtonSearch.setOnClickListener(this);
            // mImm.showSoftInput(mEditCityName, 0);
            // invoking showSoftInout() here is invalid, so using handler
            mHandler.sendEmptyMessageDelayed(SHOW_SI, DELAY_TIME);
            /**
             * If support theme manager, change the background color of the base
             * layout to theme background.
             */
            /*
             * if (FeatureOption.MTK_THEMEMANAGER_APP) { View baseLayout =
             * this.findViewById(R.id.choose_city_base_layout);
             * baseLayout.setThemeContentBgColor(Color.TRANSPARENT); }
             */

            mListView.setOnItemClickListener(this);
     
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            City city = (City) parent.getAdapter().getItem(position);

            // return the whole City information to Caller Activity
            Intent intent = new Intent();
            intent.putExtra(SEARCH_CITY_RESULT, (Parcelable)city);
            setResult(RESULT_OK, intent);

            finish();
        }

        @Override
        protected void onDestroy() {
            if (Utils.isTaskRunning(mCityTask)) {
                mCityTask.cancel(true);
            }

            super.onDestroy();
        }

        @Override
        public void onClick(View view) {
            searchCity();
        }

        private void searchCity() {
            final String city = mEditCityName.getText().toString().trim();
            if (!TextUtils.isEmpty(city) && !Utils.isTaskRunning(mCityTask)) {
               
                mButtonSearch.setClickable(false);
                mEditCityName.setEnabled(false);
                mSearching.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.INVISIBLE);
                mImm.hideSoftInputFromWindow(mEditCityName.getWindowToken(), 0);
                mCityTask = new CityTask();
                mCityTask.execute(city);
            }
        }

        private CityTask mCityTask;

        private class CityTask extends AsyncTask<String, Integer, List<City>> {
            private String mCityName;
            
            @Override
            protected List<City> doInBackground(String... cityName) {
                List<City> cities = null;
                mCityName = cityName[0];
                try {
                    cities = YahooWeatherController.getInstance(sContext).getCityByName(mCityName, null);
                }  catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Integer errorInteger = -1;
                    publishProgress(errorInteger);
                }
                return cities;
            }

            @Override
            protected void onPostExecute(List<City> result) {
                mButtonSearch.setClickable(true);
                mSearching.setVisibility(View.GONE);
                mEditCityName.setEnabled(true);
                
                if (result != null && !result.isEmpty()) {
                    mListView.setVisibility(View.VISIBLE);
                    mListviewAdapter.updateCites(result);
                } 
            }
            
            @Override
            protected void onProgressUpdate(Integer... values) {
            	if (values[0] == -1) {
            		mToast = Toast.makeText(CitySearchActivity.this, R.string.city_search_error, Toast.LENGTH_SHORT);
            		mToast.show();
            	}
            }
        }

        private final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case SHOW_SI:
                    mImm.showSoftInput(mEditCityName, 0);
                    break;

                default:
                    break;
                }
            }
        };

        private static final int SHOW_SI = 11;
}
