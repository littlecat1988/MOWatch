package com.gomtel.app.uv;

import java.util.HashSet;

import android.util.Log;
import android.widget.Toast;

import com.mediatek.wearable.Controller;

public class UVController extends Controller {

	private static final String TAG = "UVController";
	private static UVController controller;
	@Override
	protected void onReceive(byte[] receive) {
		// TODO Auto-generated method stub
		super.onReceive(receive);
//		String rev = null;
		for(int i = 0; i < receive.length; i++){
			char rev = (char) receive[i];
			Log.e(TAG,"UVController= "+rev);
		}
		
	}

	@Override
	public void send(String arg0, byte[] arg1, boolean arg2, boolean arg3,
			int arg4) {
		// TODO Auto-generated method stub
		super.send(arg0, arg1, arg2, arg3, arg4);
	}

	protected UVController() {
		
		super("UVController",9);
		// TODO Auto-generated constructor stub
		HashSet localHashSet = new HashSet();
	    localHashSet.add("gt_uvdet");
	    localHashSet.add("gt_hrp");
	    localHashSet.add("gt_ped");
	    super.setReceiverTags(localHashSet);
	    Log.e(TAG,"UVController");
	}
	 public static UVController getInstance()
	  {
	    if (controller != null)
	      return controller;
	    controller = new UVController();
	    return controller;
	  }

}
