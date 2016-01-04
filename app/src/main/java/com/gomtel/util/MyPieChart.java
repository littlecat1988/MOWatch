package com.gomtel.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

/**
 * Created by lixiang on 15-4-24.
 */
public class MyPieChart {
	private static final String TAG = "MyPieChart";
	private long deep;
	private long light;
	private long wake;

	public View execute(Context context, long TotalSleepTime,
			long deepSleepTime, long wakeTime) {
		int[] colors = new int[] { Color.GREEN, Color.YELLOW, Color.BLUE };
		Log.e(TAG,"TotalSleepTime= "+TotalSleepTime+"  deepSleepTime= "+deepSleepTime+"  wakeTime= "+wakeTime);
		if(TotalSleepTime != 0){
		deep = 100 * deepSleepTime / TotalSleepTime;
		wake = 100 * wakeTime / TotalSleepTime;;
		}
//		wake = wakeTime;
		light = 100 - deep - wake;
		Log.e(TAG,"light= "+light);
		DefaultRenderer renderer = null;
		if (deep > 0 || wake > 0) {
			renderer = buildCategoryRenderer(context,colors, true);
		} else {
			renderer = buildCategoryRenderer(context,colors, false);
		}
		CategorySeries categorySeries = new CategorySeries("Vehicles Chart");
		categorySeries.add(context.getResources()
				.getString(R.string.deep_sleep), deep);
		categorySeries.add(
				context.getResources().getString(R.string.light_sleep), light);
		categorySeries.add(context.getResources().getString(R.string.wake),
				wake);
		return ChartFactory.getPieChartView(context, categorySeries, renderer);
	}

	protected DefaultRenderer buildCategoryRenderer(Context context, int[] colors,
			boolean isShowLabels) {
		DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		if (isShowLabels) {
			renderer.setShowLabels(true);
		} else {
			renderer.setShowLabels(false);
		}
		renderer.setShowLegend(false);
		renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.chart_sleep_label_size));
		renderer.setLabelsColor(Color.BLACK);
		renderer.setZoomEnabled(false);
		renderer.setPanEnabled(false);
		return renderer;
	}

}
