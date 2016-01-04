package care.utils;

import com.gomtel.util.LogUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.List;

import care.application.XcmApplication;

public class HttpServerUtil {

	public static String invokeServer(String serverURL,String params)
			throws Exception {
		try{
			JSONObject jsonParams = new JSONObject(params);
			String belongProject = XcmApplication.getInstance().gettMetaData();
			jsonParams.put("belong_project",belongProject);
			LogUtil.e("gomtel","belong_project= "+belongProject);

			params = jsonParams.toString();
			StringEntity se = new StringEntity(params);
			HttpParams paramsw = createHttpParams();
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			HttpPost post = new HttpPost(serverURL);
			post.setEntity(se);
			HttpResponse response = new DefaultHttpClient(paramsw).execute(post);
			int sCode = response.getStatusLine().getStatusCode();
			if (sCode ==HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity());
			} else {
				JSONObject json = new JSONObject();
				json.put("resultCode", -6);
				return json.toString(); // 链接不上后台
			}
		}catch(Exception e){
			JSONObject json = new JSONObject();
			json.put("resultCode", -6);
			return json.toString(); // 链接不上后台
		}	
	}

	public static String invokeServer(String serverURL,List<NameValuePair> params) throws Exception {
		HttpPost post = new HttpPost(serverURL);
		HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(entity);
		HttpResponse response = new DefaultHttpClient().execute(post);
		int sCode = response.getStatusLine().getStatusCode();
		if (sCode == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity());
		} else
			return "-6";
	}

	private final static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 60 * 1000); // 设置参数连接时间
		HttpConnectionParams.setSoTimeout(params, 60 * 1000); // 设置参数连接超时时间
		HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);
		return params;
	}
}
