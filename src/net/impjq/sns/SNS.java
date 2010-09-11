package net.impjq.sns;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * The main test function.
 * @author Percy.Peng
 *
 */
public class SNS {
	public static final String tag = SNS.class.getSimpleName();
	static String username = "your email";
	static String password = "yourpassword";
	public static String status = "to be posted status";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// getContentFromUri("http://twitter.com/login");
		// loginToTwitter();
		// Xiaonei.loginAndPostStatus();
		Controller controller = new XiaoneiController();
		controller.setControllerListener(new ControllerListener() {

			public void loginFinished(String tag, int result) {
				// TODO Auto-generated method stub
				Log.e(tag, "loginFinished " + result);

			}

			public void postFinished(String tag, int result) {
				// TODO Auto-generated method stub
				Log.e(tag, "postFinished " + result);
			}
		});
		ExecutorThread executor = new ExecutorThread();
		executor.setController(controller, new UserInfo(username, password,
				status));
		executor.start();

		controller = new TwitterController();
		executor = new ExecutorThread();
		executor.setController(controller, new UserInfo(username, password,
				status));
		executor.start();
	}
	
	public static void setProxy() {
		String host = "127.0.0.1";
		String port = "5865";

		System.getProperties().setProperty("http.proxyHost", host);
		System.getProperties().setProperty("http.proxyPort", port);
	}

	public static void getContentFromUri(String uri) {
		Log.e(tag, "getContentFromUri,uri=" + uri);
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);

		try {
			HttpResponse response = client.execute(httpGet);
			Log.e(tag, EntityUtils.toString(response.getEntity()));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client.getConnectionManager().shutdown();
	}

}
