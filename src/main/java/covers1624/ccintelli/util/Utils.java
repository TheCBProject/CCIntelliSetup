package covers1624.ccintelli.util;

import covers1624.ccintelli.module.Module;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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

}
