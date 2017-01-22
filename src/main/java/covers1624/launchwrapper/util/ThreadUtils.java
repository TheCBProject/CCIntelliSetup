package covers1624.launchwrapper.util;

/**
 * Created by covers1624 on 2/16/2016.
 */
public class ThreadUtils {

	public static void hangThread(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			System.err.println("Unable to hang thread...");
			e.printStackTrace();
		}
	}

}
