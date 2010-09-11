package net.impjq.sns;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class XiaoneiController implements Controller {
	public static final String tag = XiaoneiController.class.getSimpleName();

	static String loginUri = "http://www.renren.com/PLogin.do";
	static String originUrl = "http://www.renren.com/Home.do";
	static String domain = "renren.com";

	static String userAgent = "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2";

	static String postStatusUrl = "http://status.renren.com/doing/update.do?";

	UserInfo mUserInfo;
	ControllerListener mListener;

	String ticket = null;
	DefaultHttpClient httpClient = null;

	public boolean login() {
		// TODO Auto-generated method stub
		Log.e(tag, "login");
		String username;
		String password;

		if (null != mUserInfo) {
			username = mUserInfo.mUserName;
			password = mUserInfo.mPassword;
		} else {
			if (null != mListener) {
				mListener.loginFinished(tag, ResultCode.NO_USER_INFO);
			}
			return false;
		}

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setUserAgent(params, userAgent);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);

		httpClient = new DefaultHttpClient(cm, params);
		// HttpHost proxyHost = new HttpHost("127.0.0.1", 5865, "http");
		// httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxyHost);
		httpClient.getParams().setParameter(HTTP.USER_AGENT, userAgent);

		HttpPost httpPost = new HttpPost(loginUri);
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		nvp.add(new BasicNameValuePair("email", username));
		nvp.add(new BasicNameValuePair("password", password));
		nvp.add(new BasicNameValuePair("originUrl", originUrl));
		nvp.add(new BasicNameValuePair("domain", domain));

		String uri = null;

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(httpPost);

			System.out.println("Post logon cookies:");
			List<Cookie> cookies = httpClient.getCookieStore().getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					// System.out.println("- " + cookies.get(i).toString());
				}
			}

			Log.e(tag, "" + response.getStatusLine());
			
			if (HttpStatus.SC_MOVED_TEMPORARILY==response.getStatusLine().getStatusCode()) {
				Header[] headers= response.getAllHeaders();
				
				for (Header header:headers){
					Log.e(tag, ""+header);
				}
				
				uri=response.getFirstHeader("Location").getValue();
				Log.e(tag,"Location="+uri);
				
				
			}		
			Log.e(tag, "getUri=" + uri);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mListener) {
				mListener.loginFinished(tag, ResultCode.LOGIN_FAILED);
			}
			return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mListener) {
				mListener.loginFinished(tag, ResultCode.LOGIN_FAILED);
			}
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mListener) {
				mListener.loginFinished(tag, ResultCode.LOGIN_FAILED);
			}
			return false;
		}

		if (null != uri) {
			HttpGet httpGet = new HttpGet(uri);

			try {
				HttpResponse response = httpClient.execute(httpGet);
				Log.e(tag, "" + response.getStatusLine());
				HttpEntity httpEntity = response.getEntity();

				if (null != httpEntity) {
					String entity = EntityUtils.toString(httpEntity);

					ticket = getTicket(entity);
					Log.e(tag, "ticket=" + ticket);
					// Log.e(tag, entity);
					httpEntity.consumeContent();
				}

				System.out.println("Post logon cookies:");
				List<Cookie> cookies = httpClient.getCookieStore().getCookies();
				if (cookies.isEmpty()) {
					System.out.println("None");
				} else {
					for (int i = 0; i < cookies.size(); i++) {
						// System.out.println("- " + cookies.get(i).toString());
					}
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (null == ticket) {
			if (null != mListener) {
				mListener.loginFinished(tag, ResultCode.LOGIN_FAILED);
			}

			return false;
		}

		if (null != mListener) {
			mListener.loginFinished(tag, ResultCode.LOGIN_SUCCESS);
		}

		return true;
	}

	public boolean postStatus() {
		// TODO Auto-generated method stub
		Log.e(tag, "postStatus");
		String status = null;

		if (null != mUserInfo) {
			status = mUserInfo.mStatus;
		} else {
			if (null != mListener) {
				mListener.postFinished(tag, ResultCode.NO_USER_INFO);
			}
			return false;
		}

		HttpPost httpPost = new HttpPost(postStatusUrl);

		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		nvp.add(new BasicNameValuePair("c", status));
		nvp.add(new BasicNameValuePair("raw", status));
		String requestToken = ticket;
		nvp.add(new BasicNameValuePair("publisher_form_ticket", ticket));
		nvp.add(new BasicNameValuePair("requestToken", requestToken));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e(tag, "post status finished:" + httpResponse.getStatusLine());

			HttpEntity httpEntity = httpResponse.getEntity();

			if (null != httpEntity) {
				Log.e(tag, EntityUtils.toString(httpEntity));
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mListener) {
				mListener.postFinished(tag, ResultCode.POST_FAILED);
			}
			return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mListener) {
				mListener.postFinished(tag, ResultCode.POST_FAILED);
			}
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mListener) {
				mListener.postFinished(tag, ResultCode.POST_FAILED);
			}
			return false;
		}

		if (null != mListener) {
			mListener.postFinished(tag, ResultCode.POST_SUCCESS);
		}

		httpClient.getConnectionManager().shutdown();
		return true;
	}

	public void setUserInfo(UserInfo userInfo) {
		// TODO Auto-generated method stub
		mUserInfo = userInfo;
	}

	public void setControllerListener(ControllerListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
	}

	public static String getTicket(String str) {
		str = str.split("publisher_form_ticket\" value=\"")[1];
		return str.split("\"")[0];
	}
}
