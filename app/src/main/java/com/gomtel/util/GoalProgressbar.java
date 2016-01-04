package com.gomtel.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class GoalProgressbar extends View
{
  private float barStrokeWidth = 10.0F;
  private int color_background = -1644826;
  private int color_progress = 0xffffb6e5;
  private float diameter = 0.0F;
  float instance = 0.0F;
  private Paint mPaintBackground = null;
  private Paint mPaintProgress = null;
  float mPointX = 0.0F;
  float mPointY = 0.0F;
  private int progress = 0;
  private RectF rectDraw = null;
  private boolean showProgressBackground = true;
  private int startAngle = 270;
  private int sweepAngle = 360;
    private int gender = 1;

    public GoalProgressbar(Context paramContext)
  {
    super(paramContext);
  }

  public GoalProgressbar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void init(Canvas paramCanvas)
  {
    this.rectDraw = new RectF(this.instance, this.instance, this.diameter - this.instance, this.diameter - this.instance);
    this.mPointX = (this.diameter / 2.0F);
    this.mPointY = (this.diameter / 2.0F);
    if (this.showProgressBackground)
    {
      this.mPaintBackground = new Paint();
      this.mPaintBackground.setAntiAlias(true);
      this.mPaintBackground.setStyle(Style.STROKE);
      this.mPaintBackground.setStrokeWidth(this.barStrokeWidth);
      this.mPaintBackground.setColor(this.color_background);
      paramCanvas.drawArc(this.rectDraw, this.startAngle, this.sweepAngle, false, this.mPaintBackground);
    }
    this.mPaintProgress = new Paint();
    this.mPaintProgress.setAntiAlias(true);
    this.mPaintProgress.setStyle(Style.STROKE);
    this.mPaintProgress.setStrokeWidth(this.barStrokeWidth);
    this.mPaintProgress.setColor(this.color_progress);
//      mPaintProgress.setColorFilter(new LightingColorFilter(Color.GREEN,Color.YELLOW));
      if(gender == 1) {
          LinearGradient shader = new LinearGradient(mPointX, mPointY, this.diameter, this.diameter , new int[]{0xffdde202, 0xfff9b53c, 0xffee98cd, 0xff9383ef}, null, Shader.TileMode.MIRROR);
          mPaintProgress.setShader(shader);
      }
    paramCanvas.drawArc(this.rectDraw, this.startAngle, this.progress * this.sweepAngle / 100, false, this.mPaintProgress);
    invalidate();
  }

  public int getProgress()
  {
    return this.progress;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    this.diameter = ((getWidth() + getHeight()) / 2);
    this.instance = (this.diameter / 40.0F);
    this.barStrokeWidth = (1.0F * this.instance);
    init(paramCanvas);
  }

  public void setBarColor(int paramInt)
  {
    this.color_progress = paramInt;
  }

  public void setProgress(int paramInt)
  {
    this.progress = paramInt;
    invalidate();
  }

  public void setShowSmallBg(boolean paramBoolean)
  {
    this.showProgressBackground = paramBoolean;
  }

  public void setSmallBgColor(int paramInt)
  {
    this.color_background = paramInt;
  }

    public void setGender(int i) {
        gender = i;
    }
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.gzgamut.max.view.GoalProgressbar
 * JD-Core Version:    0.5.4
 */