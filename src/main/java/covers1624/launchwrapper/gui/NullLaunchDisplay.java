package covers1624.launchwrapper.gui;

import java.net.URL;

/**
 * Created by covers1624 on 1/01/2017.
 */
public class NullLaunchDisplay implements ILauncherDisplay {

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resetProgress(int sizeGuess) {
    }

    @Override
    public void updateProgress(int fullLength) {
    }

    @Override
    public void updateProgressString(String string, Object... data) {
    }

    @Override
    public boolean shouldClose() {
        return false;
    }

    @Override
    public void setPokeThread(Thread thread) {
    }

    @Override
    public void onDownloadFail(String file, URL url, Exception e) {
    }
}
