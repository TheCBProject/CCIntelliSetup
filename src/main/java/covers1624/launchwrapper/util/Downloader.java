package covers1624.launchwrapper.util;

import covers1624.launchwrapper.LauncherDownloader;
import covers1624.launchwrapper.gui.ILauncherDisplay;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.ByteBuffer;

/**
 * Created by covers1624 on 1/01/2017.
 */
public class Downloader {

    public static void download(String fileName, URL url, File downloadFolder, ILauncherDisplay display) {
        download(url, new File(downloadFolder, fileName), display);
    }

    public static void download(URL url, File lib, ILauncherDisplay display) {
        try {
            display.updateProgressString("Downloading file %s", url.toString());
            System.out.println("[INFO]: Downloading file " + url.toString());
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("User-Agent", "Covers1624 LaunchWrapper downloader.");
            int expectedSize = urlConnection.getContentLength();
            try {
                if (!lib.exists()) {
                    lib.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(lib);
                copyISManaged(urlConnection.getInputStream(), fos, expectedSize, display);
            } catch (Exception e) {
                lib.deleteOnExit();
                throw e;
            }
        } catch (Exception e) {
            display.onDownloadFail(lib.toString().substring(lib.toString().lastIndexOf("/") + 1), url, e);
        }
    }

    public static void copyISManaged(InputStream input, FileOutputStream out, int avail, ILauncherDisplay display) throws IOException {
        ByteBuffer downloadBuffer = ByteBuffer.allocateDirect(avail);
        downloadBuffer.clear();

        int bytesRead;
        int fullLength = 0;

        display.resetProgress(avail);
        try {
            display.setPokeThread(Thread.currentThread());

            byte[] smallBuffer = new byte[1024];
            while ((bytesRead = input.read(smallBuffer)) > 0) {
                downloadBuffer.put(smallBuffer, 0, bytesRead);
                fullLength += bytesRead;
                display.updateProgress(fullLength);
            }
            input.close();
            display.setPokeThread(null);
            downloadBuffer.limit(fullLength);
            downloadBuffer.position(0);
        } catch (InterruptedIOException e) {
            Thread.interrupted();
            throw new RuntimeException("Stop");
        }
        downloadBuffer.position(0);
        out.getChannel().write(downloadBuffer);
        out.flush();
        out.close();
    }

	public static void injectLibToClassLoader(File file) throws Exception {
		if (file.isDirectory()) {
			return;
		}
		System.out.println("[INFO]: Adding lib: " + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(File.separatorChar) + 1));
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
	}
}
