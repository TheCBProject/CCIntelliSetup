package covers1624.launchwrapper;

import covers1624.launchwrapper.internal.PreLauncher;

import java.io.File;
import java.io.InputStream;

/**
 * Created by covers1624 on 1/01/2017.
 */
public class LaunchHandler {

    public static void runPreLaunch(String name, InputStream json, File libsDirectory, File nativesDirectory, File cacheStore) {
        PreLauncher.runInternal(libsDirectory);
        LauncherDownloader.checkDependencies(name, json, libsDirectory, nativesDirectory, cacheStore);
    }

}
