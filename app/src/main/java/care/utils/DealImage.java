package care.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

public class DealImage {
	
	public static Bitmap readBitMap(String path){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565; 
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//获取资源图片
		
		return BitmapFactory.decodeFile(path,opt);
	}
	public static Bitmap readBitMaps(Context context, int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565; 
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is,null,opt);
	}
	public static BitmapDrawable readBitmapDrawable(Context context, int resId){
		Bitmap bitmap = readBitMaps(context,resId);
		BitmapDrawable drawable = new BitmapDrawable(context.getResources(),bitmap);
		
		return drawable;
	}
	public static Bitmap readBitMap2(Context context, int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888; 
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is,null,opt);
	}
	public static BitmapDrawable readBitmapDrawable2(Context context, int resId){
		Bitmap bitmap = readBitMap2(context,resId);
		BitmapDrawable drawable = new BitmapDrawable(context.getResources(),bitmap);
		
		return drawable;
	}
}
