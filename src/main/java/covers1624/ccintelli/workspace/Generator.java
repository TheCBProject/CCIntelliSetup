package covers1624.ccintelli.workspace;

import com.google.common.base.Strings;
import covers1624.ccintelli.gui.GuiFields;
import covers1624.ccintelli.launch.Launch;
import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.util.ATFileFilter;
import covers1624.ccintelli.util.ResourceWalker;
import covers1624.ccintelli.util.Utils;
import covers1624.ccintelli.util.logger.LogHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 10/02/2017.
 */
public class Generator {

    public static List<String> forgeATLines;
    public static Map<String, List<String>> moduleAtLines;

    public static void goGoGadgetMakeTheIdeaThings() {
        LogHelper.info("FJASDJKHASd");
        Utils.tryCreateDirectory(Launch.WORKSPACE);
        Utils.tryCreateDirectory(Launch.MODULES);
        Utils.tryCreateDirectory(Launch.PROJECT_RUN);
        Utils.tryCreateDirectory(Launch.PROJECT_OUTPUT);
        mergeATs();
        runForgeSetup();







        exportModules();

    }

    public static void runForgeSetup() {
        try {
            File forge = GuiFields.forgeModule.CONTENT_ROOT;
            String gradlew = new File(forge, "gradlew.bat").getAbsolutePath();
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(forge.getAbsoluteFile());
            builder.command(gradlew, "clean", "setupForge");
            runProcessAndLog(builder.start());
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong running forges gradle tasks!", e);
        }
    }

    public static void exportModules() {
        for (Module module : GuiFields.modules) {
            if (module.NAME.equals("Forge")) {
                module = Module.buildForgeModule(new File(module.CONTENT_ROOT, "projects/Forge/Forge.iml"), module);
            }
            File moduleXML = new File(Launch.MODULES, module.NAME + ".iml");
            Utils.tryCreateFile(moduleXML);
            module.writeXML(moduleXML);
        }
    }

    public static void mergeATs() {
        forgeATLines = new LinkedList<>();
        moduleAtLines = new HashMap<>();
        List<File> forgeAtFiles = findATFilesForModule(GuiFields.forgeModule);
        for (File atFile : forgeAtFiles) {
            forgeATLines.addAll(parseATFile(atFile));
        }
        for (Module module : GuiFields.modules) {
            if (module.NAME.equals("Forge")) {
                continue;
            }
            List<String> atLines = new LinkedList<>();
            for (File file : findATFilesForModule(module)) {
                atLines.addAll(parseATFile(file));
            }
            moduleAtLines.put(module.NAME, atLines);
        }

        List<String> finalModAtLines = new LinkedList<>();
        finalModAtLines.add("# Auto generated AT file by CCIntelliSetup, Contains at lines from all modules during setup.");
        for (Entry<String, List<String>> entry : moduleAtLines.entrySet()) {
            finalModAtLines.add("");
            finalModAtLines.add("# AT Lines for: " + entry.getKey());
            for (String atLine : entry.getValue()) {
                if (finalModAtLines.contains(atLine)) {
                    LogHelper.info("Skipping at line from module %s as already exists, %s", entry.getKey(), atLine);
                    continue;
                } else if (forgeATLines.contains(atLine)) {
                    LogHelper.warn("AT Line from module %s already exists in forges at file! \"%s\"", entry.getKey(), atLine);
                    continue;
                }
                finalModAtLines.add(atLine);
            }
        }
        try {
            File atFile = new File(GuiFields.forgeModule.CONTENT_ROOT, "src/main/resources/merged_at.cfg");
            Utils.tryCreateFile(atFile);
            PrintWriter writer = new PrintWriter(atFile);
            for (String atLine : finalModAtLines) {
                writer.println(atLine);
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<String> parseATFile(File file) {
        List<String> lines = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                if (Strings.isNullOrEmpty(line) || line.startsWith("#")) {
                    continue;
                }
                int tag = line.indexOf("#");
                if (tag != -1) {
                    line = line.substring(0, tag).trim();
                }
                lines.add(line);
            }
            IOUtils.closeQuietly(reader);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static List<File> findATFilesForModule(Module module) {
        List<File> files = new LinkedList<>();
        ResourceWalker walker = new ResourceWalker(new ATFileFilter());
        for (String srcFolder : module.sourceFolders) {
            File srcFile = new File(srcFolder);
            walker.setFolder(srcFile);
            try {
                files.addAll(walker.startWalk());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    private static void runProcessAndLog(Process process) throws Exception {
        boolean firstLine = false;
        Launch.console.setStatus("Waiting for Gradle to start..");
        while (process.isAlive()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    Launch.console.setStatus("Running Gradle task..");
                    firstLine = true;
                }
                LogHelper.info(line);
            }
        }
    }

}
