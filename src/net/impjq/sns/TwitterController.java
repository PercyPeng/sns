package net.impjq.sns;


public class TwitterController implements Controller {
	public static final String tag = TwitterController.class.getSimpleName();
	UserInfo mUserInfo;

	public boolean login() {
		// TODO Auto-generated method stub
		Log.e(tag, "login");
		return false;
	}

	public boolean postStatus() {
		// TODO Auto-generated method stub
		Log.e(tag, "postStatus");
		return false;
	}

	public void setUserInfo(UserInfo userInfo) {
		// TODO Auto-generated method stub
		mUserInfo = userInfo;
	}

	public void setControllerListener(ControllerListener listener) {
		// TODO Auto-generated method stub
		
	}
}