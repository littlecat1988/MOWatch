package com.mtk.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.gomtel.util.DialogHelper;
import com.mtk.btnotification.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ReadHttpGet extends AsyncTask<Object, Object, Object>{

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	private ProgressDialog dialog_sync;
	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mHandler != null){
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
	}
	@Override
	protected void onProgressUpdate(Object... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
	private HttpResponse ressponse;
	private Handler mHandler;
	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		mHandler = (Handler)params[2];
		if(mHandler != null){
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendMessage(msg);
		}
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(params[0].toString());
		try {
			ressponse = client.execute(request);
			if(ressponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				InputStream is = ressponse.getEntity().getContent();
				
				File file = new File(params[1].toString());
			    if(file.exists())
			    {
			       file.delete();
			    }
					           byte[] bs = new byte[1024];
					           int len;
					           OutputStream os = new FileOutputStream(file);
//					           OutputStream os = openFileOutput("latest.xim",Context.MODE_APPEND);           
					           while ((len = is.read(bs)) != -1) {
					               os.write(bs, 0, len);
					           }
					           os.flush();
					           os.close();
					           is.close();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Log.e(TAG,"response"+ressponse.getStatusLine().getStatusCode());
		
		return null;
	}

}
