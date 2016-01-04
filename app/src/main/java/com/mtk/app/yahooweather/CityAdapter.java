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

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.mediatek.ctrl.yahooweather.City;

// code copy from framework for following reason
//  0) can not just extends ArrayAdapter to do the job
//  1) to improve performance for city filter as city list is too big(2.5k)
//  2) to support pinyin filter

/**
 * A ListAdapter that manages a ListView backed by an array of arbitrary
 * objects. By default this class expects that the provided resource id
 * references a single TextView. If you want to use a more complex layout, use
 * the constructors that also takes a field id. That field id should reference a
 * TextView in the larger layout resource.
 *
 * However the TextView is referenced, it will be filled with the toString() of
 * each object in the array. You can add lists or arrays of custom objects.
 * Override the toString() method of your objects to determine what text will be
 * displayed for the item in the list.
 *
 * To use something other than TextViews for the array display, for instance,
 * ImageViews, or to have some of data besides toString() results fill the
 * views, override {@link #getView(int, View, ViewGroup)} to return the type of
 * view you want.
 */
public class CityAdapter extends BaseAdapter {
    /**
     * Contains the list of objects that represent the data of this
     * ArrayAdapter. The content of this list is referred to as "the array" in
     * the documentation.
     */
    private final List<City> mCites = new ArrayList<City>();
    private final LayoutInflater mInflater;

    public CityAdapter(Activity activity) {
        mInflater = activity.getLayoutInflater();
    }

    public void updateCites(List<City> cities) {
        mCites.clear();
        mCites.addAll(cities);
        notifyDataSetChanged();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        TextView textView;

        if (convertView == null) {
            textView = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        City item = mCites.get(position);
        textView.setSingleLine(false);
        textView.setText(item.getName());
        if (!TextUtils.isEmpty(item.getCountry())) {
            textView.append(" " + item.getCountry());
        }
        if (!TextUtils.isEmpty(item.getState())) {
            textView.append(" " + item.getState());
        }
        textView.setTextColor(Color.BLACK);

        return textView;
    }

    @Override
    public int getCount() {
        return mCites.size();
    }

    @Override
    public Object getItem(int position) {
        return mCites.get(position);
    }

}