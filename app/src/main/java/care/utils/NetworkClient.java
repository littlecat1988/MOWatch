package care.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public abstract class NetworkClient implements Runnable {

	private String mUrl;

	public NetworkClient(String url) {
		mUrl = url;
	}

	@Override
	public void run() {
		HttpParams connParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(connParam, 10000);
		HttpClient client = new DefaultHttpClient(connParam);

		HttpPost post = new HttpPost(mUrl);
		try {
			post.setEntity(getParams());
			HttpResponse resp = client.execute(post);
			String jstr = EntityUtils.toString(resp.getEntity(), "utf8");
			Trace.i(jstr);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				onSuccessed(jstr);
				return;
			}
			onFailed();
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			onExecption(e);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	protected abstract UrlEncodedFormEntity getParams();

	protected abstract void onSuccessed(String jstr);

	protected abstract void onFailed();

	protected abstract void onExecption(IOException e);

}
