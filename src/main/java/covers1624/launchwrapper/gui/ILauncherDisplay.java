package covers1624.launchwrapper.gui;

import java.net.URL;

/**
 * Created by covers1624 on 2/15/2016.
 */
public interface ILauncherDisplay {

	void init();

	void dispose();

	void resetProgress(int sizeGuess);

	void updateProgress(int fullLength);

	void updateProgressString(String string, Object... data);

	boolean shouldClose();

	void setPokeThread(Thread thread);

	void onDownloadFail(String file, URL url, Exception e);

}
