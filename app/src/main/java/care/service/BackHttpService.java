package care.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.HashMap;

import care.db.ProtocolData;
import care.utils.Constants;
import care.utils.HttpServerUtil;
import care.utils.XcmTools;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackHttpService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "org.care.lower";
    private XcmTools tools;
    private ProtocolData mProtocolData;
    public BackHttpService() {
        super("BackHttpService");
    }

    public void onCreate(){
        super.onCreate();
        tools = new XcmTools(this);
        mProtocolData = ProtocolData.getInstance();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {

            }else if(Constants.GETDOWNLOADURL.equals(action)){
                getDownloadUrl();
            }
        }
    }

    public void getDownloadUrl() {
        int versionCode = Constants.getCurrentVersion(this);
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("version_code",versionCode);
        String mJr = mProtocolData.transFormToJson(map);
        try {
            String resulr = HttpServerUtil.invokeServer(Constants.UPDATEAPP, mJr);
            HashMap<String, Object> resultMap = mProtocolData.getBackResult(resulr);
            String name = ""+resultMap.get("name");
            String downloadUrl = ""+resultMap.get("download_url");
            tools.set_download_url(downloadUrl + "@" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
