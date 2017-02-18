package covers1624.ccintelli.util;

import covers1624.launchwrapper.os.OSManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by covers1624 on 20/01/2017.
 */
public class Utils {

    public static void tryCreateFile(File file) {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to create file: " + file.getAbsolutePath(), e);
            }
        }
    }

    public static void tryCreateDirectory(File dir) {
        if (!dir.exists()) {
            try {
                if (!dir.mkdirs()) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to create directory: " + dir.getAbsolutePath(), e);
            }
        }
    }

    public static void archiveFile(File current, File archive) throws IOException {
        if (current.exists()) {
            if (!archive.exists()) {
                Utils.tryCreateFile(archive);
            }
            FileUtils.copyFile(current, archive);
        }
    }

    public static File getWorkingDirectory(String folder) {
        return AccessController.doPrivileged((PrivilegedAction<File>) () -> getWorkingDirectory_do(folder));
    }

    private static File getWorkingDirectory_do(String folder) {

        String homeDir = System.getProperty("user.home");
        File file;

        switch (OSManager.getCurrentOS()) {
            case OSX:
                file = new File(homeDir + "Library/Application Support/" + folder);
                break;
            case WINDOWS:
            case LINUX:
            case SOLARIS:
            case UNKNOWN:
            default:
                file = new File(homeDir, "." + folder + "/");
                break;
        }
        tryCreateDirectory(file);
        if (!file.exists()) {
            throw new RuntimeException("Unable to create the working directory: " + file.getAbsolutePath());
        }
        return file;
    }

}
