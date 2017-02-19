package covers1624.ccintelli.launch;

import covers1624.ccintelli.gui.CCIntelliSetupConsole;
import covers1624.ccintelli.gui.CCIntelliSetupMainWindow;
import covers1624.ccintelli.gui.SetupDialog;
import covers1624.ccintelli.util.Utils;
import covers1624.ccintelli.util.logging.LogHelper;
import covers1624.launchwrapper.LaunchHandler;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 19/01/2017.
 */
public class Launch {

    private static boolean SHOULD_EXIT = false;

    public static File WORKING_DIR;
    public static File LIB_DIR;

    public static File SETUP_DIR;
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
        console = new CCIntelliSetupConsole();

        RecentWorkspaceSerializer workspaceSerializer = new RecentWorkspaceSerializer(new File(WORKING_DIR, "recents.json"));
        List<File> recents = new LinkedList<>(workspaceSerializer.load());
        SetupDialog setup = new SetupDialog(recents);
        SETUP_DIR = setup.getDirectory();

        if (SETUP_DIR == null) {
            LogHelper.info("No workspace selected. Canceling launch!");
            return;
        }
        recents.add(0, SETUP_DIR);//Inject last to top of the list.
        workspaceSerializer.save(recents);

        WORKSPACE = new File(SETUP_DIR, "Workspace");
        PROJECT_RUN = new File(WORKSPACE, "run");
        PROJECT_OUTPUT = new File(WORKSPACE, "out");
        MODULES = new File(WORKSPACE, "Modules");
        FORGE = new File(SETUP_DIR, "Forge");

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
}
