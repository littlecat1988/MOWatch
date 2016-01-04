package com.mtk.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.gomtel.database.DatabaseProvider;
import com.gomtel.util.Global;
import com.gomtel.util.HistoryDay;
import com.gomtel.util.LogUtil;
import com.mtk.btnotification.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class HistorySportActivity extends Activity {
    private static final String TAG = "HistorySportActivity";
    private LineChartView chart_step;
    private LineChartView chart_distance;
    private LineChartView chart_height;
    private LineChartView chart_burn;
    private TextView week;
    private TextView month;
    private TextView year;
    private Calendar mClendar = Calendar.getInstance();
    private LineChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private OnClickListener myListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.week:
                    week.setBackgroundResource(R.drawable.bg_sport_chart);
                    month.setBackground(null);
                    year.setBackground(null);
                    getWeekChart();
                    break;
                case R.id.month:
                    month.setBackgroundResource(R.drawable.bg_sport_chart);
                    week.setBackground(null);
                    year.setBackground(null);
                    getMonthChart();
                    break;
                case R.id.year:
                    year.setBackgroundResource(R.drawable.bg_sport_chart);
                    month.setBackground(null);
                    week.setBackground(null);
                    getYearChart();
                    break;

            }
        }
    };
    private List<HistoryDay> listOfMonth;
    private List<HistoryDay> listOfYear;
    private List<Long> listOfTotalYear = new ArrayList<Long>();
    private TextView distance_num;
    private TextView burn_num;

    private void getMonthChart() {
        if (listOfMonth == null || listOfMonth.size() == 0) {
            listOfMonth = DatabaseProvider.queryHistoryDate(this, 0, mClendar, 30);
            Collections.reverse(listOfMonth);
        }
        drawStep(listOfMonth,30);
        drawDistance(listOfMonth, 30);
        drawBurn(listOfMonth,30);
    }


    private void getYearChart() {
        if (listOfYear == null || listOfYear.size() == 0) {
            listOfYear = DatabaseProvider.queryHistoryDate(this, 0, mClendar, 365);
            Collections.reverse(listOfYear);
        }

        drawYearStep();
        drawYearDistance();
        drawYearBurn();
    }

    private void drawYearBurn() {
        for(int i = 0;i < listOfYear.size()/30+1;i++){
            long step = 0;
            if(listOfYear.size()-30*i < 30){
                for(int j = 30*i;j< listOfYear.size();j++){
                    step += listOfYear.get(j).getBurn();
                }
            }else{
                for(int j = 30*i;j< 30*(i+1);j++){
                    step += listOfYear.get(j).getBurn();
                }
            }
            listOfTotalYear.add(step);
        }
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        int listOfTotalYear_size = listOfTotalYear.size();
        burn_num.setText(Global.df_1_1.format(listOfYear.get(0).getBurn()));
        for (int i = 0; i < 12; i++) {
            PointValue pv = null;
            if (listOfTotalYear_size < 13 && i > 11 - listOfTotalYear_size) {
                pv = new PointValue(i, listOfTotalYear.get(11 - i));
            } else {
                pv = new PointValue(i, 0);
            }
            values.add(pv);
        }
        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> Axisvalues = new ArrayList<>();
            Axisvalues.add(new AxisValue(0, Global.sdf_4.format(mClendar.getTime()).toCharArray()));
            axisX.setValues(Axisvalues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart_burn.setLineChartData(data);
        listOfTotalYear.clear();
    }

    private void drawYearDistance() {
        for(int i = 0;i < listOfYear.size()/30+1;i++){
            long step = 0;
            if(listOfYear.size()-30*i < 30){
                for(int j = 30*i;j< listOfYear.size();j++){
                    step += listOfYear.get(j).getDistance();
                }
            }else{
                for(int j = 30*i;j< 30*(i+1);j++){
                    step += listOfYear.get(j).getDistance();
                }
            }
            listOfTotalYear.add(step);
        }
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        int listOfTotalYear_size = listOfTotalYear.size();
        distance_num.setText(Global.df_3.format(listOfYear.get(0).getDistance()));
        for (int i = 0; i < 12; i++) {
            PointValue pv = null;
            if (listOfTotalYear_size < 13 && i > 11 - listOfTotalYear_size) {
                pv = new PointValue(i, listOfTotalYear.get(11 - i));
            } else {
                pv = new PointValue(i, 0);
            }
            values.add(pv);
        }
        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> Axisvalues = new ArrayList<>();
            Axisvalues.add(new AxisValue(0, Global.sdf_4.format(mClendar.getTime()).toCharArray()));
            axisX.setValues(Axisvalues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart_distance.setLineChartData(data);
        listOfTotalYear.clear();
    }

    private void drawYearStep() {
        for(int i = 0;i < listOfYear.size()/30+1;i++){
            long step = 0;
            if(listOfYear.size()-30*i < 30){
                for(int j = 30*i;j< listOfYear.size();j++){
                    step += listOfYear.get(j).getStep();
                }
            }else{
                for(int j = 30*i;j< 30*(i+1);j++){
                    step += listOfYear.get(j).getStep();
                }
            }
            listOfTotalYear.add(step);
        }
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        int listOfTotalYear_size = listOfTotalYear.size();
        step_num.setText(String.valueOf(listOfYear.get(0).getStep()));
        for (int i = 0; i < 12; i++) {
            PointValue pv = null;
            if (listOfTotalYear_size < 13 && i > 11 - listOfTotalYear_size) {
                pv = new PointValue(i, listOfTotalYear.get(11 - i));
            } else {
                pv = new PointValue(i, 0);
            }
            values.add(pv);
        }
        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> Axisvalues = new ArrayList<>();
            Axisvalues.add(new AxisValue(0, Global.sdf_4.format(mClendar.getTime()).toCharArray()));
            axisX.setValues(Axisvalues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart_step.setLineChartData(data);
        listOfTotalYear.clear();
    }

    private List<HistoryDay> listOfWeek;
    private TextView step_num;

    private void getWeekChart() {
        if (listOfWeek == null || listOfWeek.size() == 0) {
            listOfWeek = DatabaseProvider.queryHistoryDate(this, 0, mClendar, 7);
            Collections.reverse(listOfWeek);
        }
        drawStep(listOfWeek,7);
        drawDistance(listOfWeek, 7);
        drawBurn(listOfWeek,7);
    }

    private void drawBurn(List<HistoryDay> list, int day) {
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        int list_size = list.size();
        burn_num.setText(Global.df_1_1.format(list.get(0).getBurn()));
        for (int i = 0; i < day; i++) {
            PointValue pv = null;
            if (list_size < day+1 && i > day - 1 - list_size) {
                pv = new PointValue(i, (float) list.get(day - 1 - i).getBurn());
            } else {
                pv = new PointValue(i, 0);
            }
            values.add(pv);
        }
        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> Axisvalues = new ArrayList<>();
            Axisvalues.add(new AxisValue(0, Global.sdf_4.format(mClendar.getTime()).toCharArray()));
            axisX.setValues(Axisvalues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart_burn.setLineChartData(data);
    }

    private void drawDistance(List<HistoryDay> list, int day) {
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        int list_size = list.size();
        distance_num.setText(Global.df_3.format(list.get(0).getDistance()));
        for (int i = 0; i < day; i++) {
            PointValue pv = null;
            if (list_size < day+1 && i > day - 1 - list_size) {
                pv = new PointValue(i, (float) list.get(day -1 - i).getDistance());
            } else {
                pv = new PointValue(i, 0);
            }
            values.add(pv);
        }
        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> Axisvalues = new ArrayList<>();
            Axisvalues.add(new AxisValue(0, Global.sdf_4.format(mClendar.getTime()).toCharArray()));
            axisX.setValues(Axisvalues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart_distance.setLineChartData(data);

    }

    private void drawStep(List<HistoryDay> list, int day) {
        List<PointValue> values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        int list_size = list.size();
        step_num.setText(String.valueOf(list.get(0).getStep()));
        for (int i = 0; i < day; i++) {
            PointValue pv = null;
            if (list_size < day+1 && i > day - 1 - list_size) {
                pv = new PointValue(i, list.get(day -1 - i).getStep());
            } else {
                pv = new PointValue(i, 0);
            }
            values.add(pv);
        }
        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> Axisvalues = new ArrayList<>();
            Axisvalues.add(new AxisValue(0, Global.sdf_4.format(mClendar.getTime()).toCharArray()));
            axisX.setValues(Axisvalues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart_step.setLineChartData(data);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_sport_chart);
        initView();
        week.setBackgroundResource(R.drawable.bg_sport_chart);
        month.setBackground(null);
        year.setBackground(null);
        getWeekChart();
    }

    private void initView() {
        chart_step = (LineChartView) findViewById(R.id.chart_step);
        chart_distance = (LineChartView) findViewById(R.id.chart_distance);
//        chart_height = (LineChartView) findViewById(R.id.chart_height);
        chart_burn = (LineChartView) findViewById(R.id.chart_burn);
        week = (TextView) findViewById(R.id.week);
        week.setOnClickListener(myListener);
        month = (TextView) findViewById(R.id.month);
        month.setOnClickListener(myListener);
        year = (TextView) findViewById(R.id.year);
        year.setOnClickListener(myListener);
        step_num = (TextView) findViewById(R.id.step_num);
        distance_num = (TextView) findViewById(R.id.distance_num);
        burn_num = (TextView) findViewById(R.id.burn_num);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
