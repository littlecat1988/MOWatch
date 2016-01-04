package care.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView
{
	/**
	* 屏幕宽度
	*/
	private int mScreenWidth;
	/**
	* dp
	*/
	private int mMenuRightPadding = 50;
	/**
	* 菜单的宽度
	*/
	private int mMenuWidth;
	private int mHalfMenuWidth;
	private int mThreeTwoMenuWidth;
	private int mThreeOneMenuWidth;

	private boolean once;
	
	private boolean isOpen = false;
	
	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	private ViewGroup menu;
	private ViewGroup content;
	private float oldX;
	public SlidingMenu(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setFadingEdgeLength(0);
		mScreenWidth = ScreenUtils.getScreenWidth(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		/**
		* 显示的设置一个宽度
		*/
		if (!once)
		{
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			menu = (ViewGroup) wrapper.getChildAt(0);
//			Button fenceButton=(Button)menu.findViewById(R.id.toFenceButton);
//			fenceButton.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					Toast.makeText(getContext(), "fence", Toast.LENGTH_SHORT).show();
//				}
//			});
			content = (ViewGroup) wrapper.getChildAt(1);
			// dp to px
			mMenuRightPadding = mScreenWidth/8;
//					(int) TypedValue.applyDimension(
//					TypedValue.COMPLEX_UNIT_DIP, mMenuRightPadding, content
//							.getResources().getDisplayMetrics());

			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mHalfMenuWidth = mMenuWidth / 2;
			mThreeTwoMenuWidth = mMenuWidth*2/ 3;
			mThreeOneMenuWidth = mMenuWidth/ 3;
			menu.getLayoutParams().width = mMenuWidth;
			content.getLayoutParams().width = mScreenWidth;

		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (changed)
		{
			// 将菜单隐藏
			this.scrollTo(mMenuWidth, 0);
			once = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			oldX=ev.getX();
			break;
		// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();//右侧主界面的显示宽度
			if(isOpen){
				if(ev.getX()>mMenuWidth&&ev.getX()==oldX){
					this.smoothScrollTo(mMenuWidth, 0);
					isOpen = false;
					return true;
				}
				if (scrollX > mScreenWidth/3-mMenuRightPadding){
					this.smoothScrollTo(mMenuWidth, 0);
					isOpen = false;
				}else{
					this.smoothScrollTo(0, 0);
					isOpen = true;
				}
			}else{
				if (scrollX > mScreenWidth*2/3){
					this.smoothScrollTo(mMenuWidth, 0);
					isOpen = false;
				}else{
					this.smoothScrollTo(0, 0);
					isOpen = true;
				}
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	@Override
	protected void onScrollChanged(int l,int t,int oldl,int oldt){
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l*1.0f/mMenuWidth;
		float rightScale = 0.8f+scale*0.2f;
		float leftScale = 1-0.3f*scale;
		ViewHelper.setScaleX(menu, leftScale);
		ViewHelper.setScaleY(menu, leftScale);
		ViewHelper.setAlpha(menu, 0.6f+0.4f*(1-scale));
		ViewHelper.setTranslationX(menu, mMenuWidth*scale*0.6f);
		ViewHelper.setPivotX(content, 0);
		ViewHelper.setPivotY(content, content.getHeight()/2);
		ViewHelper.setScaleX(content, rightScale);
		ViewHelper.setScaleY(content, rightScale);
	}
	

	public void openMenu(){
		if(!isOpen){
			this.smoothScrollTo(0, 0);
			isOpen = true;
		}
		
	}
	
	public void closeMenu(){
		if(isOpen){
			this.smoothScrollTo(mMenuWidth, 0);
			isOpen = false;
		}
	}
	
	public void toggle(){
		if(isOpen){
			closeMenu();
		}else{
			openMenu();
		}
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev){//重写事件拦截方法(该控件将子view的事件都拦截了，导致智能拖动侧边栏，地图拖动不了)
		if(ev.getX()<10){//手指在屏幕左边缘时，拦截事件，作用是只拖动侧边栏不拖动地图
			return true;
		}else if(isOpen){//侧边栏展开时，拦截事件，作用是只拖动侧边栏不拖动地图
			if(ev.getX()>mMenuWidth){
				return true;
			}
		}
		return false;//其他情况，放行事件，让地图层可以拖动
	}

}
