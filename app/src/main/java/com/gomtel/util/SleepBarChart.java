package com.gomtel.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by lixiang on 15-4-24.
 */
public class SleepBarChart {
    private static final String TAG = "SleepBarChart";
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private int[] time = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    private int[] rate = new int[]{100, 300, 250, 12, 300, 0, 0, 0, 0, 70, 0, 0, 520, 302, 60, 1720, 20, 0, 0, 0, 0, 0, 0, 0};
    XYSeries series = new XYSeries("");
    private XYSeriesRenderer xyRenderer;
    private double maxStep;
    private int sizeOfList;
    private String start_time;
    private String stop_time;
//	private Context mContext;

    public SleepBarChart() {
        super();
        dataset = new XYMultipleSeriesDataset();
        renderer = new XYMultipleSeriesRenderer();
    }

    public GraphicalView getChartGraphicalView(Context context) {
        return ChartFactory.getBarChartView(context, dataset, renderer, org.achartengine.chart.BarChart.Type.DEFAULT);
    }


    public void setRandererBasicProperty(Context context,
                                         String title, String xTitle, String yTitle, int axeColor, int labelColor) {


        renderer.setAxesColor(axeColor);
        renderer.setLabelsColor(labelColor);
        renderer.setXAxisColor(labelColor);
        renderer.setMarginsColor(0x00ffffff);
        renderer.setXAxisMin(0);
        renderer.setYAxisMin(0);
        renderer.setXLabels(0);
        renderer.addXTextLabel(0, start_time);
        renderer.addXTextLabel(sizeOfList, stop_time);
        renderer.setLabelsTextSize(30);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.CENTER);
        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false, false);
        renderer.setChartTitleTextSize(0);
        renderer.setShowGrid(true);
//        renderer.setGridColor();
//        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        renderer.setYLabelsVerticalPadding(-3.0F);
    }

    public void addNewSRPair(XYSeries Series, XYSeriesRenderer xyRenderer) {
        if (dataset == null || renderer == null) {
            return;
        } else {
            dataset.addSeries(Series);
            renderer.addSeriesRenderer(xyRenderer);
        }
    }

    public GraphicalView getSleepBarChartGraphicalView(Context context, ArrayList<JSONObject> listOfSport) throws JSONException, ParseException {



        series = getSeries(listOfSport,context);
        setRandererBasicProperty(context, "", "", "", Color.BLACK, Color.BLACK);
//        addNewSRPair(series, xyRenderer);

        return getChartGraphicalView(context);
    }

    private XYSeries getSeries(ArrayList<JSONObject> listOfSleep, Context context) throws JSONException, ParseException {
        sizeOfList = listOfSleep.size();
        XYSeries series_nosleep = new XYSeries("");
        XYSeries series_deepsleep = new XYSeries("");
        int[] temp = new int[sizeOfList];
        int step = 0;
        long temp_time = 0;
        if (listOfSleep.size() > 0) {
            start_time = (listOfSleep.get(0).getString("starttime")).split(" ")[1].substring(0, 5);
            stop_time = (listOfSleep.get(sizeOfList - 1).getString("endtime")).split(" ")[1].substring(0, 5);
            Log.e(TAG, "start_time= " + start_time);
        }

        series_nosleep.add(0, 0.0);
        for(int i = 1;i<sizeOfList+1;i++ ){
            String type = listOfSleep.get(i-1).getString("type");
            temp_time = (Global.sdf_1.parse(listOfSleep.get(i-1).getString("endtime")).getTime() - Global.sdf_1.parse(listOfSleep.get(i-1).getString("starttime")).getTime())/Global.MIN;
            if("1".equals(type)){
                series_nosleep.add(i, temp_time);
            }
            if("2".equals(type)){
                series_deepsleep.add(i, temp_time);
            }

        }
//        series.add(sizeOfList+1, 0.0);
        XYSeriesRenderer xyRenderer_nosleep = new XYSeriesRenderer();
        xyRenderer_nosleep.setColor(context.getResources().getColor(R.color.light_blue));
        XYSeriesRenderer xyRenderer_deepsleep = new XYSeriesRenderer();
        xyRenderer_deepsleep.setColor(Color.GREEN);
        addNewSRPair(series_nosleep, xyRenderer_nosleep);
        addNewSRPair(series_deepsleep, xyRenderer_deepsleep);
        return series;
    }


    private double getMaxStep(ArrayList<HistoryHour> list) {
        int maxstep = 0;
        for (int m = 0; m < list.size(); m++) {
            if (list.get(m).getStep() > maxstep) {
                maxstep = list.get(m).getStep();
            }
        }
        return (double) 2 * maxstep;
    }
}
