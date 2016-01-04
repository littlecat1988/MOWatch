package com.gomtel.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class MyCircleImageView extends CircleImageView
{
  public MyCircleImageView(Context paramContext)
  {
    super(paramContext);
  }

  public MyCircleImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public MyCircleImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public Bitmap createMask()
  {
    Bitmap localBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint(1);
    localPaint.setColor(-16777216);
    localCanvas.drawOval(new RectF(0.0F, 0.0F, getWidth(), getHeight()), localPaint);
    return localBitmap;
  }
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.gzgamut.max.view.MyCircleImageView
 * JD-Core Version:    0.5.4
 */