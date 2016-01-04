package com.gomtel.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by lixiang on 15-12-29.
 */
public class SleepChart extends ImageView {
    private Paint paint_green = new Paint();
    private Paint paint_gray = new Paint();
    private boolean isInvalidate;
    private ArrayList<JSONObject> list;
    private double time;
    private double temp_time;
    private int startPosition = 0;
    private float density;

    public SleepChart(Context context) {
        super(context);
    }
    public SleepChart(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint_green.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paint_green.setColor(Color.GREEN);
        paint_gray.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paint_gray.setColor(Color.GRAY);
        if(isInvalidate) {
            for(JSONObject json: list) {
                try {
                     temp_time = (Global.sdf_1.parse(json.getString("endtime")).getTime() - Global.sdf_1.parse(json.getString("starttime")).getTime())/Global.MIN;
                    LogUtil.e("lixiang","density= "+density);
                    switch(json.getString("type")){
                        case "1":
                            int width_gray = (int) (Float.valueOf(Global.df_3.format(temp_time/time))*getWidth());
                            canvas.drawRect(startPosition, 100*density, startPosition + width_gray, 300*density, paint_gray);
                            startPosition += width_gray;
                                    LogUtil.e("lixiang", "width_gray= " +width_gray);
                            break;
                        case "2":
                            int width_green = (int) (Float.valueOf(Global.df_3.format(temp_time/time))*getWidth());
                            canvas.drawRect(startPosition, 60*density, startPosition+width_green,300*density, paint_green);
                            startPosition += width_green;
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (ParseException e) {
                    e.printStackTrace();
                }
//                canvas.drawRect(10, 10, 30, 30, paint);
            }
        }
    }

    public void setupChart(ArrayList<JSONObject> listOfSleep, long total_time,float density) {

        this.density = density;
        list = listOfSleep;
        time = total_time/Global.MIN;
        isInvalidate = true;
        invalidate();
    }
}
