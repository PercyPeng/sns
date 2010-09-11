package net.impjq.sns;

/**
 * Executor 
 * @author Percy.Peng
 *
 */
public class ExecutorThread extends Thread {
	Controller mController;

	void setController(Controller controller, UserInfo userInfo) {
		mController = controller;
		mController.setUserInfo(userInfo);
	}

	void execute() {
		boolean result = mController.login();
		if (true == result) {
			mController.postStatus();
		}
	}

	@Override
	public void run() {
		execute();
	}
}
