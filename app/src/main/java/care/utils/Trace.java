package care.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Trace
{
	private static final String LOG_TAG = "xcm";
	private static File dir = null;  
	private static boolean isLogMode = false;
	private static boolean isInit = false;
	
	private static String s = null;
	public static void init(boolean bCreated){
	  boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	  
	  isInit = true;
	  try  
      {  				
	        if (sdCardExist)  
	        {  
	            dir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "xcm.log");  
	            if (!dir.exists())  
	            {  
	            	if(bCreated){
	            		dir.createNewFile();
	            		isLogMode = true;
	            	}
	            	else{
	            		isLogMode = false;
	            	}
	            }  		
	            else{
	            	isLogMode = true; 
	            }
	        }
	        else if(bCreated){
	  		  isLogMode = true; 
	  	    }	        
      } catch (Exception e)  
      {  
          e.printStackTrace();  
      }  		
	}
	
	public static void writeFile(String msg){		
		  try  
	      { 
			  if(dir != null){				
				  FileOutputStream fos = new FileOutputStream(dir, true); 	
				  msg = msg + "\r\n";
				  byte [] bytes = msg.getBytes();   
				  fos.write(bytes);   
				  fos.close();			  
			  }
			  
	      } catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	}
	
	public static void e(String msg)
	{
		if(!isInit){
			init(true); 
		}
		if(isLogMode){
			writeFile(s+",,,,"+msg);
			Log.e(LOG_TAG, msg);	
		}					
	}
	
	public static void w(String msg)
	{
		if(!isInit){
			init(true);
		}
		
		if(isLogMode){
			writeFile(s+",,,,"+msg);		
			Log.w(LOG_TAG, msg);
		}		
	}
	
	public static void i(String msg)
	{
		if(!isInit){
			init(true);
		}
		
		if(isLogMode){
			s = InputTime();
			writeFile(s+",,,,"+msg);
			Log.i(LOG_TAG, msg);	
		}				
	}
	private static String InputTime() {
		// TODO Auto-generated method stub
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSSS"); 
		  s = dateFormat.format(date);
		  return s;
	}

}