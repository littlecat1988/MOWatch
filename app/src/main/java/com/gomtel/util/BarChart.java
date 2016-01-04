package com.gomtel.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Build;
import android.util.Log;

import com.mtk.bluetoothle.HistoryHour;
import com.mtk.btnotification.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang on 15-4-24.
 */
public class BarChart {
    private static final String TAG = "HomeChart";
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

    public BarChart() {
        super();
        dataset = new XYMultipleSeriesDataset();
        renderer = new XYMultipleSeriesRenderer();
    }

    public GraphicalView getChartGraphicalView(Context context) {
//    	mContext = context;
//        return ChartFactory.getCubeLineChartView(context, dataset, renderer,0);
        return ChartFactory.getBarChartView(context, dataset, renderer, org.achartengine.chart.BarChart.Type.DEFAULT);
    }

    public XYMultipleSeriesDataset bulidBasicDataset() {
        return dataset;
    }

    public XYMultipleSeriesRenderer buildRenderer() {
        return renderer;
    }

    public void setRandererBasicProperty(Context context,
                                         String title, String xTitle, String yTitle, int axeColor, int labelColor) {


        renderer.setAxesColor(axeColor);
        renderer.setLabelsColor(labelColor);
        renderer.setXAxisColor(labelColor);
//        renderer.setLabelsColor(0xff000000);
        renderer.setMarginsColor(0x00ffffff);
        renderer.setXAxisMin(0);
        renderer.setYAxisMin(0);
        renderer.setXLabels(0);
//        renderer.setXLabels(12);
//        renderer.setYLabels(10);
//        renderer.setXLabels(20);
//        renderer.setXLabelsPadding(5.0F);
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

    public XYMultipleSeriesDataset getLastestDateset() {
        return dataset;
    }

    public XYMultipleSeriesRenderer getLastestRenderer() {
        return renderer;
    }

    public GraphicalView getBarChartGraphicalView(Context context, ArrayList<JSONObject> listOfSport) throws JSONException {


        xyRenderer = new XYSeriesRenderer();
        xyRenderer.setColor(context.getResources().getColor(R.color.light_blue));
        series = getSeries(listOfSport);
        setRandererBasicProperty(context, "", "", "", Color.BLACK, Color.BLACK);
        addNewSRPair(series, xyRenderer);

        return getChartGraphicalView(context);
    }

    private XYSeries getSeries(ArrayList<JSONObject> listOfSport) throws JSONException {
        sizeOfList = listOfSport.size();
        XYSeries series = new XYSeries("");
        int[] temp = new int[sizeOfList];
        int step = 0;
        int temp_time = 0;
        if (listOfSport.size() > 0) {
            start_time = (listOfSport.get(0).getString("startTime")).split(" ")[1].substring(0, 5);
            stop_time = (listOfSport.get(sizeOfList - 1).getString("startTime")).split(" ")[1].substring(0, 5);
            Log.e(TAG, "start_time= " + start_time);
        }

        series.add(0, 0.0);
        for(int i = 1;i<sizeOfList+1;i++ ){
            series.add(i, Double.parseDouble(listOfSport.get(i - 1).getString("qty")));
        }
        series.add(sizeOfList+1, 0.0);
//        for (int i = 0; i < sizeOfList; i++) {
//            if(i == 0){
//                step += Integer.parseInt((listOfSport.get(0).getString("qty")));
//                continue;
//            }
//            if (Integer.parseInt((listOfSport.get(i).getString("startTime")).split(" ")[1].substring(0, 2)) == Integer.parseInt((listOfSport.get(i - 1).getString("startTime")).split(" ")[1].substring(0, 2))) {
//                step += Integer.parseInt((listOfSport.get(i).getString("qty")));
//
//            }else{
//                series.add(time[Integer.parseInt((listOfSport.get(i-1).getString("startTime")).split(" ")[1].substring(0, 2))], step);
//                step = 0;
//                step += Integer.parseInt((listOfSport.get(i).getString("qty")));
//            }
//        }
//        series.add(sizeOfList + 1, 0);
        return series;
    }

    public void updateData(ArrayList<HistoryHour> list) {
        Log.e(TAG, "list= " + list);
        if (list != null && list.size() > 0) {
            dataset.removeSeries(series);
            renderer.removeAllRenderers();
            series.clear();
            maxStep = getMaxStep(list);
            renderer.setYAxisMax(10.0D + maxStep);
//        series.
//        XYSeriesRenderer xyRenderer=new XYSeriesRenderer();
            xyRenderer.setColor(Color.BLUE);
            int length = list.size();
            if (length > 24)
                length = 24;
            for (int i = 0; i < length; i++) {
                series.add(time[i], list.get(i).getStep());
            }
            addNewSRPair(series, xyRenderer);
        }
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
