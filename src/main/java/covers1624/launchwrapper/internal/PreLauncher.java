package covers1624.launchwrapper.internal;

import covers1624.launchwrapper.digest.MD5Sum;
import covers1624.launchwrapper.digest.MD5SumCruncher;
import covers1624.launchwrapper.gui.LauncherGui;
import covers1624.launchwrapper.util.Downloader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 1/01/2017.
 */
public class PreLauncher {

    private static final Map<URL, MD5Sum> depMap = new HashMap<>();

    static {
        try {
            depMap.put(new URL("http://jcenter.bintray.com/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar"), new MD5Sum("A42F1F5BFA4E6F123DDCAB3DE7E0FF81"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void runInternal(File libsDir) {
    	System.out.println("[INFO]: Running Launcher setup..");
        File launcherLibsDir = new File(libsDir, "launcher");
        if (!launcherLibsDir.exists()) {
            launcherLibsDir.mkdirs();
        }
        LauncherGui gui = new LauncherGui("Covers1624 LauncherWrapper");
        gui.init();
        try {
            for (Entry<URL, MD5Sum> entry : depMap.entrySet()) {
                String fileName = entry.getKey().toString().substring(entry.getKey().toString().lastIndexOf("/") + 1);
                File libFile = new File(launcherLibsDir, fileName);
                MD5Sum local = MD5SumCruncher.calculateFileSum(libFile);
                //System.out.println(local);
                if (!local.equals(entry.getValue())) {
                    Downloader.download(fileName, entry.getKey(), launcherLibsDir, gui);
                }
                Downloader.injectLibToClassLoader(libFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to run pre launcher!", e);
        } finally {
            gui.dispose();
        }
	    System.out.println("[INFO]: Launcher setup done. Launching launcher!");
    }


}
