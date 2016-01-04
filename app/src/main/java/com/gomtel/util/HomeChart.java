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
import com.mtk.btnotification.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * Created by lixiang on 15-4-24.
 */
public class HomeChart {
    private static final String TAG = "HomeChart";
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private int[] time = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
    private int[] rate=new int[] { 100,300,250,12,300,0,0,0,0,70,0,0,520,302,60,1720,20,0,0,0,0,0,0,0};
    XYSeries series=new XYSeries("");
    private XYSeriesRenderer xyRenderer;
    private double maxStep;
//	private Context mContext;

    public HomeChart(){
        super();
        dataset=new XYMultipleSeriesDataset();
        renderer=new XYMultipleSeriesRenderer();
//        for(int i=-20;i<0;i++){
//            date[i+20]=new Date(value+i*TimeChart.DAY/86400);
//        }
    }

    public GraphicalView getChartGraphicalView(Context context){
//    	mContext = context;
        return ChartFactory.getCubeLineChartView(context, dataset, renderer,0);
//    	 return ChartFactory.getBarChartView(context, dataset, renderer,Type.DEFAULT);
    }

    public XYMultipleSeriesDataset bulidBasicDataset(){
        return dataset;
    }

    public XYMultipleSeriesRenderer buildRenderer(){
        return renderer;
    }

    public void setRandererBasicProperty(Context context,
            String title,String xTitle,String yTitle,int axeColor,int labelColor){

//        renderer.setChartTitle(title);
//        renderer.setXTitle(xTitle);
//        renderer.setYTitle(yTitle);
//        renderer.setRange(new double[]{10,10,10,10});

        renderer.setAxesColor(axeColor);
        renderer.setLabelsColor(0xff000000);
        renderer.setMarginsColor(0x00ffffff);
        renderer.setXAxisMin(0);
//        renderer.setXAxisMax(23);
        renderer.setYAxisMin(0);
//        renderer.setYAxisMax(500);

        renderer.setXLabels(0);
//        renderer.addXTextLabel(0, "0h");
        renderer.addXTextLabel(6, "6h");
        renderer.addXTextLabel(12, "12h");
        renderer.addXTextLabel(18, "18h");
        renderer.addXTextLabel(23, "24h");
        renderer.setYLabels(3);
//        renderer.setXLabelsAlign(Align.RIGHT);
//        renderer.setYLabelsAlign(Align.LEFT);

        renderer.setAxisTitleTextSize(16);
        renderer.setShowAxes(false);
        renderer.setPointSize(10);
//        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.text_chart_size));
        renderer.setLegendTextSize(30);
        renderer.setPointSize(5f);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
//        renderer.setYLabelsPadding(PADDING_SIZE);
//        renderer.setMargins(new int[]{20, 30, 15, 20});
        renderer.setPanEnabled(true, false);
        renderer.setShowGrid(true);
        renderer.setZoomEnabled(false, false);
        renderer.setInScroll(false);

        renderer.setPanLimits(new double[] { 0.0D, 0.0D, 0.0D, 0.0D });
        renderer.setXLabelsColor(-16777216);
        renderer.setYLabelsColor(0, -16777216);
        int[] arrayOfInt = new int[4];
        arrayOfInt[0] = 20;
        arrayOfInt[1] = 80;
        arrayOfInt[2] = 20;
        arrayOfInt[3] = 50;
        renderer.setMargins(arrayOfInt);
        renderer.setYLabelsVerticalPadding(-3.0F);
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
        setRandererBasicProperty(context,"今日运动数据", "时刻", "步数",Color.BLACK,Color.BLACK);

         xyRenderer=new XYSeriesRenderer();

        xyRenderer.setColor(Color.BLUE);
        xyRenderer.setPointStyle(PointStyle.CIRCLE);
        xyRenderer.setFillPoints(false);



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
        xyRenderer.setColor(Color.BLUE);
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
