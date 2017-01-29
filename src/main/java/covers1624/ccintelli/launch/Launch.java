package covers1624.ccintelli.launch;

import covers1624.ccintelli.gui.CCIntelliSetupConsole;
import covers1624.ccintelli.gui.CCIntelliSetupMainWindow;
import covers1624.ccintelli.gui.GuiFields;
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
	public static File PROJECT_RUN;
	public static File PROJECT_OUTPUT;

	public static File FORGE;

	public static CCIntelliSetupConsole console;
	public static CCIntelliSetupMainWindow window;

	public static String COMPILER_SELECT = "Eclipse";
	public static boolean NOT_NULL_ASSERTIONS = false;

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		console = new CCIntelliSetupConsole();
		BUILD_DIR = new File(".", "build");
		LOG_DIR = new File(BUILD_DIR, "logs");
		LIB_DIR = new File(BUILD_DIR, "libs");
		WORKSPACE = new File("Workspace");
		PROJECT_RUN = new File(WORKSPACE, "run");
		PROJECT_OUTPUT = new File(WORKSPACE, "out");
		MODULES = new File(WORKSPACE, "Modules");
		FORGE = new File("Forge");

		Utils.tryCreateDirectory(BUILD_DIR);
		Utils.tryCreateDirectory(LOG_DIR);
		Utils.tryCreateDirectory(LIB_DIR);

		LaunchHandler.runPreLaunch("CCIntelliSetup", Launch.class.getResourceAsStream("/Dependencies.json"), LIB_DIR, null, null);

		LogHelper.setLogFile(new File(LOG_DIR, "builder.log"));
		console.setVisible(true);
		window = new CCIntelliSetupMainWindow();
		window.setVisible(true);

//		run();


		//console.dispose();
	}

	public static void run() throws Exception {
		Utils.tryCreateDirectory(WORKSPACE);
		Utils.tryCreateDirectory(MODULES);
		Utils.tryCreateDirectory(PROJECT_RUN);
		Utils.tryCreateDirectory(PROJECT_OUTPUT);

		String gradlew = new File(FORGE, "gradlew.bat").getAbsolutePath();
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(FORGE.getAbsoluteFile());
		builder.command(gradlew, "clean", "setupForge");
		runProcessAndLog(builder.start());

		File forgeXML = new File(FORGE, "projects/Forge/Forge.iml");
		File newXML = new File(MODULES, "Forge.iml");
		Module forgeModule = Module.buildForgeModule(forgeXML, GuiFields.forgeModule);
		Utils.tryCreateFile(newXML);
		forgeModule.writeXML(newXML);
	}


	private static void runProcessAndLog(Process process) throws Exception {
		boolean firstLine = false;
		console.setStatus("Waiting for Gradle to start..");
		while (process.isAlive()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (!firstLine) {
					console.setStatus("Running Gradle task..");
					firstLine = true;
				}
				LogHelper.info(line);
			}
		}
	}

}
