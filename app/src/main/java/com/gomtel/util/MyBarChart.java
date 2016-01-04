package com.gomtel.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.mtk.bluetoothle.HistoryHour;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * Created by lixiang on 15-4-24.
 */
public class MyBarChart {
    private static final String TAG = "HomeChart";
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private int[] time = new int[]{1,2,3,4,5};
    private int[] rate=new int[] {0,10,50,20,0};
    XYSeries series=new XYSeries("");
    private XYSeriesRenderer xyRenderer;
    private double maxStep;

    public MyBarChart(){
        super();
        dataset=new XYMultipleSeriesDataset();
        renderer=new XYMultipleSeriesRenderer();
//        for(int i=-20;i<0;i++){
//            date[i+20]=new Date(value+i*TimeChart.DAY/86400);
//        }
    }

    public GraphicalView getChartGraphicalView(Context context){
//        return ChartFactory.getCubeLineChartView(context, dataset, renderer,0.3f);
    	 return ChartFactory.getBarChartView(context, dataset, renderer,Type.DEFAULT);
    }

    public XYMultipleSeriesDataset bulidBasicDataset(){
        return dataset;
    }

    public XYMultipleSeriesRenderer buildRenderer(){
        return renderer;
    }

    public void setRandererBasicProperty(
            String title,String xTitle,String yTitle,int axeColor,int labelColor){

//        renderer.setChartTitle(title);
//        renderer.setXTitle(xTitle);
//        renderer.setYTitle(yTitle);
////        renderer.setRange(new double[]{10,10,10,10});
//
        renderer.setAxesColor(axeColor);
        renderer.setLabelsColor(0xff000000);
        renderer.setMarginsColor(0x00ffffff);
//        renderer.setXAxisMin(0);
////        renderer.setXAxisMax(23);
//        renderer.setYAxisMin(0);
////        renderer.setYAxisMax(500);
//
//        renderer.setXLabels(0);
////        renderer.addXTextLabel(0, "0h");
//        renderer.addXTextLabel(6, "6h");
//        renderer.addXTextLabel(12, "12h");
//        renderer.addXTextLabel(18, "18h");
//        renderer.addXTextLabel(23, "24h");
//        renderer.setYLabels(3);
////        renderer.setXLabelsAlign(Align.RIGHT);
////        renderer.setYLabelsAlign(Align.LEFT);
//
//        renderer.setAxisTitleTextSize(16);
////        renderer.setChartTitleTextSize(20);
//        renderer.setLabelsTextSize(30);
//        renderer.setLegendTextSize(30);
//        renderer.setPointSize(5f);
//        renderer.setYLabelsAlign(Paint.Align.RIGHT);
////        renderer.setYLabelsPadding(PADDING_SIZE);
////        renderer.setMargins(new int[]{20, 30, 15, 20});
//        renderer.setPanEnabled(true, false);
//        renderer.setShowGrid(true);
//        renderer.setZoomEnabled(false, false);
//        renderer.setInScroll(false);
//
//        renderer.setPanLimits(new double[] { 0.0D, 0.0D, 0.0D, 0.0D });
//        renderer.setXLabelsColor(-16777216);
//        renderer.setYLabelsColor(0, -16777216);
//        int[] arrayOfInt = new int[4];
//        arrayOfInt[0] = 20;
//        arrayOfInt[1] = 80;
//        arrayOfInt[2] = 20;
//        arrayOfInt[3] = 50;
//        renderer.setMargins(arrayOfInt);
//        renderer.setYLabelsVerticalPadding(-3.0F);
    	 renderer.setZoomEnabled(false, false);
    	         renderer.setXLabels(5);
    	         renderer.setYLabels(10);
    	         renderer.setXLabelsAlign(Align.RIGHT);
    	         renderer.setYLabelsAlign(Align.LEFT);
    	         renderer.setPanEnabled(false, false);

    	         renderer.setBarSpacing(1.0f);
    	         //renderer.setLabelsTextSize(30);
    	         renderer.setXLabelsPadding(10);
    	         renderer.setFitLegend(true);
    }

    public void addNewSRPair(XYSeries Series,XYSeriesRenderer xyRenderer){
        if(dataset==null||renderer==null){
            return ;
        }else{
            dataset.addSeries(Series);
            renderer.addSeriesRenderer(xyRenderer);
        }
    }

    public XYMultipleSeriesDataset getLastestDateset(){
        return dataset;
    }

    public XYMultipleSeriesRenderer getLastestRenderer(){
        return renderer;
    }
    public GraphicalView getHomeChartGraphicalView(Context context){
        setRandererBasicProperty("�����˶�����", "ʱ��", "����",Color.BLACK,Color.BLACK);
//        buildRenderer();
//        bulidBasicDataset();
//

         xyRenderer=new XYSeriesRenderer();

        xyRenderer.setColor(Color.GREEN);



        int length=time.length;
        for(int i=0;i<length;i++){
            series.add(time[i],rate[i]);
        }

        addNewSRPair(series, xyRenderer);

        return getChartGraphicalView(context);
    }
    public void updateData(ArrayList<HistoryHour> list){
    	Log.e(TAG,"list= "+list);
    	if(list!= null && list.size()>0){
        dataset.removeSeries(series);
        renderer.removeAllRenderers();
        series.clear();
        maxStep = getMaxStep(list);
        renderer.setYAxisMax(10.0D+maxStep);
//        series.
//        XYSeriesRenderer xyRenderer=new XYSeriesRenderer();
        xyRenderer.setColor(Color.GREEN);
        int length=list.size();
        if(length > 24)
        	length = 24 ;
        for(int i=0;i<length;i++){
            series.add(time[i],list.get(i).getStep());
        }
        addNewSRPair(series, xyRenderer);
    	}
    }

    private double getMaxStep(ArrayList<HistoryHour> list) {
        int maxstep = 0;
        for(int m = 0;m<list.size();m++){
            if(list.get(m).getStep() > maxstep){
                maxstep = list.get(m).getStep();
            }
        }
        return (double)2*maxstep;
    }
}
