package care.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import care.service.BackHttpService;
import care.utils.Constants;

/**
 * Created by wid3344 on 2015/8/27.
 */
public class DownApkEndReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if(Constants.GETDOWNLOADURL.equals(action)){
            Intent service = new Intent(context,BackHttpService.class);
            service.setAction(action);
            context.startService(service);
        }else{
            installApk(context);
        }
    }

    private void installApk(Context context){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File fils = new File(path,"xcm.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(fils),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
