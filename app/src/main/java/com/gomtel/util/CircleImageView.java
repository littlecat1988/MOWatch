package com.gomtel.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.io.PrintStream;

public abstract class CircleImageView extends ImageView
{
  private static final Xfermode MASK_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
  private Bitmap mask;
  private Paint paint;

  public CircleImageView(Context paramContext)
  {
    super(paramContext);
  }

  public CircleImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public CircleImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public abstract Bitmap createMask();

  protected void onDraw(Canvas paramCanvas)
  {
    Drawable localDrawable = getDrawable();
    if (localDrawable == null)
      return;
    try
    {
      if (this.paint == null)
      {
        this.paint = new Paint();
        this.paint.setFilterBitmap(false);
        this.paint.setXfermode(MASK_XFERMODE);
      }
      int i = paramCanvas.saveLayer(0.0F, 0.0F, getWidth(), getHeight(), null, 31);
      localDrawable.setBounds(0, 0, getWidth(), getHeight());
      localDrawable.draw(paramCanvas);
      if ((this.mask == null) || (this.mask.isRecycled()))
        this.mask = createMask();
      paramCanvas.drawBitmap(this.mask, 0.0F, 0.0F, this.paint);
      paramCanvas.restoreToCount(i);
      return;
    }
    catch (Exception localException)
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Attempting to draw with recycled bitmap. View ID = ");
      System.out.println("localStringBuilder==" + localStringBuilder);
    }
  }
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.gzgamut.max.view.CircleImageView
 * JD-Core Version:    0.5.4
 */