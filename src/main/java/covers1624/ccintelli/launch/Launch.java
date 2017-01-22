package covers1624.ccintelli.launch;

import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.util.Utils;
import covers1624.ccintelli.util.logger.LogHelper;
import covers1624.launchwrapper.LaunchHandler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by covers1624 on 19/01/2017.
 */
public class Launch {

	public static File BUILD_DIR;
	public static File LOG_DIR;
	public static File LIB_DIR;

	public static File WORKSPACE;
	public static File MODULES;

	public static File FORGE;

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		BUILD_DIR = new File(".", "build");
		LOG_DIR = new File(BUILD_DIR, "logs");
		LIB_DIR = new File(BUILD_DIR, "libs");
		WORKSPACE = new File(BUILD_DIR, "Workspace");
		MODULES = new File(WORKSPACE, "Modules");
		FORGE = new File("Forge");

		Utils.tryCreateDirectory(BUILD_DIR);
		Utils.tryCreateDirectory(LOG_DIR);
		Utils.tryCreateDirectory(LIB_DIR);
		Utils.tryCreateDirectory(WORKSPACE);
		Utils.tryCreateDirectory(MODULES);

		LaunchHandler.runPreLaunch("CCIntelliSetup", Launch.class.getResourceAsStream("/Dependencies.json"), LIB_DIR, null, null);

		LogHelper.setLogFile(new File(LOG_DIR, "builder.log"));


		String gradlew = new File(FORGE, "gradlew.bat").getAbsolutePath();
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(FORGE.getAbsoluteFile());
		builder.command(gradlew, "setupForge");
		runProcessAndLog(builder.start());

		File forgeXML = new File(FORGE, "projects/Forge/Forge.iml");
		File newXML = new File(MODULES, "Forge.iml");
		Module forgeModule = Module.buildForgeModule(forgeXML, FORGE);
		Utils.tryCreateFile(newXML);
		forgeModule.writeXML(newXML);


	}


	private static void runProcessAndLog(Process process) throws Exception {
		while (process.isAlive()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				LogHelper.info(line);
			}
		}
	}

}
