package net.impjq.sns;

public interface ControllerListener {
	void loginFinished(String tag,int result);

	void postFinished(String tag,int result);

}
