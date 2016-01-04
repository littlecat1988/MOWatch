package care.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.mtk.btnotification.R;

/** 
 *  
 * @author xiaanming 
 * 
 */  
public class WiperSwitch extends View implements OnTouchListener{  
    private Bitmap bg_on, bg_off, slipper_btn;  
    /** 
     * 锟斤拷锟斤拷时锟斤拷x锟酵碉拷前锟斤拷x 
     */  
    private float downX, nowX;  
      
    /** 
     * 锟斤拷录锟矫伙拷锟角凤拷锟节伙拷锟斤拷 
     */  
    private boolean onSlip = false;  
      
    /** 
     * 锟斤拷前锟斤拷状态 
     */  
    private boolean nowStatus = false;  
      
    /** 
     * 锟斤拷锟斤拷涌锟�
     */  
    private OnChangedListener listener;  
      
      
    public WiperSwitch(Context context) {  
        super(context);  
        init();  
    }  
  
    public WiperSwitch(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }  
      
    public void init(){  
        //锟斤拷锟斤拷图片锟斤拷源  
        bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.switch_on);  
        bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.switch_off);  
        slipper_btn = BitmapFactory.decodeResource(getResources(), R.drawable.button_kaiguan_dian);  
          
        setOnTouchListener(this);  
    }  
      
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        Matrix matrix = new Matrix();  
        Paint paint = new Paint();  
        float x = 0;  
          
        //锟斤拷锟絥owX锟斤拷锟矫憋拷锟斤拷锟斤拷锟斤拷锟斤拷锟竭癸拷状态  
        if (nowX < (bg_on.getWidth()/2)){  
            canvas.drawBitmap(bg_off, matrix, paint);//锟斤拷锟斤拷锟截憋拷时锟侥憋拷锟斤拷  
        }else{  
            canvas.drawBitmap(bg_on, matrix, paint);//锟斤拷锟斤拷锟斤拷时锟侥憋拷锟斤拷   
        }  
          
        if (onSlip) {//锟角凤拷锟斤拷锟节伙拷锟斤拷状态,    
            if(nowX >= bg_on.getWidth())//锟角否划筹拷指锟斤拷锟斤拷围,锟斤拷锟斤拷锟矫伙拷锟斤拷锟杰碉拷锟斤拷头,锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷卸锟� 
                x = bg_on.getWidth() - slipper_btn.getWidth()/2;//锟斤拷去锟斤拷锟斤拷1/2锟侥筹拷锟斤拷  
            else  
                x = nowX - slipper_btn.getWidth()/2;  
        }else {  
            if(nowStatus){//锟斤拷莸锟角帮拷锟阶刺拷锟斤拷没锟斤拷锟斤拷x值  
                x = bg_on.getWidth() - slipper_btn.getWidth();  
            }else{  
                x = 0;  
            }  
        }  
          
        //锟皆伙拷锟介滑锟斤拷锟斤拷锟斤拷锟届常锟斤拷锟�锟斤拷锟斤拷锟矫伙拷锟斤拷锟斤拷锟� 
        if (x < 0 ){  
            x = 0;  
        }  
        else if(x > bg_on.getWidth() - slipper_btn.getWidth()){  
            x = bg_on.getWidth() - slipper_btn.getWidth();  
        }  
          
        //锟斤拷锟斤拷锟斤拷锟斤拷  
        canvas.drawBitmap(slipper_btn, x , 0, paint);   
    }  
  
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        switch(event.getAction()){  
        case MotionEvent.ACTION_DOWN:{  
            if (event.getX() > bg_off.getWidth() || event.getY() > bg_off.getHeight()){  
                return false;  
            }else{  
                onSlip = true;  
                downX = event.getX();  
                nowX = downX;  
            }  
            break;  
        }  
        case MotionEvent.ACTION_MOVE:{  
            nowX = event.getX();  
            break;  
        }  
        case MotionEvent.ACTION_UP:{  
            onSlip = false;  
            if(event.getX() >= (bg_on.getWidth()/2)){  
                nowStatus = true;  
                nowX = bg_on.getWidth() - slipper_btn.getWidth();  
            }else{  
                nowStatus = false;  
                nowX = 0;  
            }  
              
            if(listener != null){  
                listener.OnChanged(WiperSwitch.this, nowStatus);
            }  
            break;  
        }  
        }  
        //刷锟铰斤拷锟斤拷  
        invalidate();  
        return true;  
    }  
      
      
      
    /** 
     * 为WiperSwitch锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷锟解部锟斤拷锟矫的凤拷锟斤拷 
     * @param listener 
     */  
    public void setOnChangedListener(OnChangedListener listener){  
        this.listener = listener;  
    }  
      
      
    /** 
     * @param checked 
     */  
    public void setChecked(boolean checked){  
        if(checked){  
            nowX = bg_off.getWidth();  
        }else{  
            nowX = 0;  
        }  
        nowStatus = checked;  
		invalidate();
    }  
  
    public interface OnChangedListener {  
        public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);
    }  
  
  
}  
