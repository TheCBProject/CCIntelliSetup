package covers1624.ccintelli.launch;

import covers1624.ccintelli.gui.*;
import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.util.logging.LogHelper;
import covers1624.ccintelli.util.Utils;
import covers1624.launchwrapper.LaunchHandler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 19/01/2017.
 */
public class Launch {

    private static boolean SHOULD_EXIT = false;

    public static File WORKING_DIR;
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

	private static final List<Runnable> RUNNABLES = new LinkedList<>();

	public static void main(String[] args) throws Exception {
        WORKING_DIR = Utils.getWorkingDirectory("ccintelli");

		LIB_DIR = new File(WORKING_DIR, "libs");
		Utils.tryCreateDirectory(LIB_DIR);

		LaunchHandler.runPreLaunch("CCIntelliSetup", Launch.class.getResourceAsStream("/Dependencies.json"), LIB_DIR, null, null);
        UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");

        List<File> recents = new ArrayList<>();

        //recents.add(new File("C:\\RecentDir1"));
        //recents.add(new File("C:\\RecentDir2"));
        //recents.add(new File("C:\\RecentDir3"));
        //recents.add(new File("C:\\RecentDir4"));

        SetupDialog setup = new SetupDialog(recents);
        File workspaceDir = setup.getDirectory();

        if (workspaceDir == null) {
            LogHelper.info("No workspace selected. Canceling launch!");
            return;
        }

        WORKSPACE = new File("Workspace");
        PROJECT_RUN = new File(WORKSPACE, "run");
        PROJECT_OUTPUT = new File(WORKSPACE, "out");
        MODULES = new File(WORKSPACE, "Modules");
        FORGE = new File("Forge");

        console = new CCIntelliSetupConsole();
        console.setVisible(true);
        window = new CCIntelliSetupMainWindow();
        window.setVisible(true);

        LogHelper.info("CCIntelliSetup \\o/");

        while (!shouldExit()) {
            List<Runnable> runnableCopy;
            synchronized (RUNNABLES) {
                runnableCopy = new LinkedList<>(RUNNABLES);
            }

            for (Runnable runnable : runnableCopy) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    LogHelper.warnError("Exception thrown whilst running a scheduled task.", e);
                }
            }

            synchronized (RUNNABLES) {
                RUNNABLES.removeAll(runnableCopy);
            }
        }


        window.dispose();
		console.dispose();
	}

	public static synchronized void scheduleTask(Runnable runnable) {
        synchronized (RUNNABLES) {
            RUNNABLES.add(runnable);
        }
    }
    
    public static boolean shouldExit() {
	    return SHOULD_EXIT;
    }

    public static void exit() {
	    SHOULD_EXIT = true;
    }

	public static void run() throws Exception {


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
