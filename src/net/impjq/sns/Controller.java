package net.impjq.sns;

public interface Controller {
	public boolean login();

	public boolean postStatus();

	public void setUserInfo(UserInfo userInfo);
	
	public void setControllerListener(ControllerListener listener);
}
