package covers1624.launchwrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import covers1624.launchwrapper.digest.MD5Sum;
import covers1624.launchwrapper.digest.MD5SumCruncher;
import covers1624.launchwrapper.gui.ILauncherDisplay;
import covers1624.launchwrapper.gui.LauncherGui;
import covers1624.launchwrapper.os.OSManager;
import covers1624.launchwrapper.os.OSManager.EnumOS;
import covers1624.launchwrapper.util.Downloader;
import covers1624.launchwrapper.util.PairKV;
import covers1624.launchwrapper.util.ThreadUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Created by covers1624 on 2/15/2016.
 */
public class LauncherDownloader {

	private static ILauncherDisplay display;
	private static boolean DEBUG = Boolean.parseBoolean(System.getProperty("covers1624.launchwrapper.debug", "false"));

	private static Map<PairKV<URL, MD5Sum>, File> downloadSumMap = new HashMap<>();
	private static Map<File, List<PairKV<String, MD5Sum>>> nativeExtractMap = new HashMap<>();

	private static List<File> invalidFiles = new ArrayList<>();

	private static String name;
	private static InputStream dependenciesStream;
	private static File libsDirectory;
	private static File nativesDirectory;
	private static File cacheStore;

	public static void checkDependencies(String name, InputStream dependenciesFile, File libsDirectory, File nativesDirectory, File cacheStore) {
		info("[INFO]: LaunchWrapper is setting up!");
		LauncherDownloader.name = name;
		LauncherDownloader.dependenciesStream = dependenciesFile;
		LauncherDownloader.libsDirectory = libsDirectory;
		LauncherDownloader.nativesDirectory = nativesDirectory;
		LauncherDownloader.cacheStore = cacheStore;

		display = new LauncherGui(name);
		display.init();

		try {
			parseJson();
			deleteFiles();
			doDownloadAndExtract();

			finish();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (display != null) {
				display.dispose();
			}
			injectDependencies();
			hackNatives();
		}
		info("[INFO]: LaunchWrapper has finished!");

	}

	private static void parseJson() throws Exception {
		EnumOS currOS = OSManager.getCurrentOS();
		JsonReader reader = new JsonReader(new InputStreamReader(dependenciesStream));
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(reader).getAsJsonObject();

		JsonArray deps = object.getAsJsonArray("dependencies");
		if (nativesDirectory == null) {
			info("[INFO]: LaunchWrapper will not parse natives as the folder is null.");
		}

		for (JsonElement e : deps) {
			JsonObject dep = e.getAsJsonObject();
			URL url = new URL(dep.get("url").getAsString());
			MD5Sum md5 = new MD5Sum(dep.get("md5").getAsString());
			File downloadFile = parseURLToFile(url, libsDirectory);
			boolean markedForRemoval = false;
			if (dep.has("os")) {
				EnumOS os = OSManager.parseFromString(dep.get("os").getAsString());
				if (!os.equals(currOS)) {
					markedForRemoval = true;
				}
			}
			if (dep.has("natives") && nativesDirectory != null) {
				downloadFile = parseURLToFile(url, cacheStore);
				List<PairKV<String, MD5Sum>> natives = new ArrayList<>();
				for (JsonElement nE : dep.getAsJsonArray("natives")) {
					JsonObject nat = nE.getAsJsonObject();
					String file = nat.get("file").getAsString();
					MD5Sum nSum = new MD5Sum(nat.get("md5").getAsString());
					natives.add(new PairKV<>(file, nSum));
					if (markedForRemoval) {
						invalidFiles.add(new File(nativesDirectory, file));
					}
				}
				if (!markedForRemoval) {
					nativeExtractMap.put(downloadFile, natives);
				}
			}

			if (markedForRemoval) {
				invalidFiles.add(downloadFile);
			} else {
				downloadSumMap.put(new PairKV<>(url, md5), downloadFile);
			}
		}
		List<File> libFolderList = new ArrayList<>();
		addAllFilesList(libsDirectory, libFolderList);
		for (Entry<PairKV<URL, MD5Sum>, File> entry : downloadSumMap.entrySet()) {
			if (entry.getValue().exists()) {
				if (!entry.getKey().getValue().equals(MD5SumCruncher.calculateFileSum(entry.getValue()))) {
					invalidFiles.add(entry.getValue());
				}
			}
		}
		for (File file : libFolderList) {
			PairKV<URL, MD5Sum> pair = getValueFromKey(downloadSumMap, file);
			if (pair == null) {
				invalidFiles.add(file);
			}
		}

		if (nativesDirectory != null) {
			List<File> nativeFolderList = new ArrayList<>();
			addAllFilesList(nativesDirectory, nativeFolderList);
			for (Entry<File, List<PairKV<String, MD5Sum>>> entry : nativeExtractMap.entrySet()) {
				if (entry.getKey().exists()) {
					PairKV<URL, MD5Sum> sumPair = getValueFromKey(downloadSumMap, entry.getKey());
					if (sumPair == null) {
						invalidFiles.add(entry.getKey());
						continue;
					}
					if (!sumPair.getValue().equals(MD5SumCruncher.calculateFileSum(entry.getKey()))) {
						invalidFiles.add(entry.getKey());
					}
					for (PairKV<String, MD5Sum> pair : entry.getValue()) {
						File nFile = new File(nativesDirectory, pair.getKey());
						if (nFile.exists()) {
							if (!pair.getValue().equals(MD5SumCruncher.calculateFileSum(nFile))) {
								invalidFiles.add(nFile);
							}
						}
					}
				}
			}

			List<File> allNativesExtract = new ArrayList<>();
			for (List<PairKV<String, MD5Sum>> list : nativeExtractMap.values()) {
				for (PairKV<String, MD5Sum> pair : list) {
					File nFile = new File(nativesDirectory, pair.getKey());
					allNativesExtract.add(nFile);
				}
			}

			for (File file : nativeFolderList) {
				if (!allNativesExtract.contains(file)) {
					invalidFiles.add(file);
				}
			}
		}

		Collections.sort(invalidFiles);

		if (DEBUG) {
			info("[DEBUG]: Invalid Files:");
			for (File file : invalidFiles) {
				info("[DEBUG]: " + file);
				info("[DEBUG]: \t%s", MD5SumCruncher.calculateFileSum(file));
			}

			info("[DEBUG]: Files to download:");
			for (Entry<PairKV<URL, MD5Sum>, File> entry : downloadSumMap.entrySet()) {
				info("[DEBUG]: " + entry.getKey().getKey());
				info("[DEBUG]: \t%s", entry.getKey().getValue());
				info("[DEBUG]: \t%s", entry.getValue());
			}
			info("[DEBUG]: Natives to extract:");
			for (Entry<File, List<PairKV<String, MD5Sum>>> entry : nativeExtractMap.entrySet()) {
				info("[DEBUG]: " + entry.getKey());
				for (PairKV<String, MD5Sum> pair : entry.getValue()) {
					info("[DEBUG]: \t%s", pair.getKey());
					info("[DEBUG]: \t\t%s", pair.getValue());
				}
			}
		}
	}

	private static void deleteFiles() {
		boolean shouldExit = false;
		for (File file : invalidFiles) {
			if (file.exists()) {
				if (!file.delete()) {
					shouldExit = true;
					info("[WARN]: Unable to delete file! Marked for delete on exit and the program will have to be launched again.");
					info("[WARN]: If this message appears again. You will have to manually delete the file.");
					info("[WARN]: File: %s", file.getAbsoluteFile());
					file.deleteOnExit();
				}
			}
		}
		if (shouldExit) {
			info("[WARN]: Exiting as Files were not properly deleted.");
			System.exit(-1);
			throw new RuntimeException("Files failed to delete.");//This shouldn't be called but meh.
		}
	}

	private static void doDownloadAndExtract() throws IOException {
		for (Entry<PairKV<URL, MD5Sum>, File> entry : downloadSumMap.entrySet()) {
			if (!entry.getKey().getValue().equals(MD5SumCruncher.calculateFileSum(entry.getValue()))) {
				Downloader.download(entry.getKey().getKey(), entry.getValue(), display);
			}
		}
		if (nativesDirectory != null) {
			for (Entry<File, List<PairKV<String, MD5Sum>>> entry : nativeExtractMap.entrySet()) {
				info("[DEBUG]: " + entry.getKey());
				JarFile jar = new JarFile(entry.getKey());
				for (PairKV<String, MD5Sum> pair : entry.getValue()) {
					ZipEntry zipEntry = jar.getEntry(pair.getKey());
					File nativeFile = new File(nativesDirectory, pair.getKey());
					if (!pair.getValue().equals(MD5SumCruncher.calculateFileSum(nativeFile))) {
						info("[DEBUG]: Extracting file: " + pair.getKey());
						if (!nativeFile.exists()) {
							nativeFile.createNewFile();
						}
						InputStream stream = jar.getInputStream(zipEntry);
						Downloader.copyISManaged(stream, new FileOutputStream(nativeFile), stream.available(), display);
					}
				}
			}
		}
	}

	private static void info(Object obj, Object... format) {
		System.out.println(String.format(obj.toString(), format));
	}

	private static void injectDependencies() {
		File[] files = libsDirectory.listFiles();
		try {
			if (files != null) {
				for (File file : files) {
					Downloader.injectLibToClassLoader(file);
				}
			} else {
				System.err.println("Libs folder empty... This is probably a bug.");
				throw new RuntimeException("Libs folder empty.");
			}
		} catch (Exception e) {
			info("[WARN]: Error occurred whilst adding libs.");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void hackNatives() {
		if (nativesDirectory != null) {
			System.out.println("Injecting natives dir to ClassLoader..");
			try {
				String paths = System.getProperty("java.library.path");
				String nativesDir = nativesDirectory.getAbsolutePath();

				paths = (paths == null || paths.isEmpty()) ? nativesDir : (paths + File.pathSeparator + nativesDir);
				System.setProperty("java.library.path", paths);

				// hack the classloader now.
				Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
				sysPathsField.setAccessible(true);
				sysPathsField.set(null, null);
			} catch (Throwable t) {
				System.err.println("Error occurred whilst hacking natives to the ClassLoader..");
				t.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private static <K, V> K getValueFromKey(Map<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private static void addAllFilesList(File folderToList, List<File> list) {
		File[] files = folderToList.listFiles();
		if (files != null) {
			for (File file : files) {
				if (!file.isDirectory()) {
					list.add(file);
				}
			}
		}
	}

	private static File parseURLToFile(URL url, File folder) {
		String str = url.toString();
		return new File(folder, str.substring(str.lastIndexOf("/") + 1));
	}

	private static void finish() {
		display.resetProgress(1);
		display.updateProgress(1);
		display.setPokeThread(null);
		display.updateProgressString("Dependencies downloaded! Launching..");
		ThreadUtils.hangThread(500);
	}
}
