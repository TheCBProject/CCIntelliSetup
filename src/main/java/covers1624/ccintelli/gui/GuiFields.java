package covers1624.ccintelli.gui;

import com.google.common.collect.ImmutableList;
import covers1624.ccintelli.launch.SetupSerializer;
import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.module.ModuleEntry;
import covers1624.ccintelli.module.OrderEntry.Scope;
import covers1624.ccintelli.util.EnumLanguageLevel;
import covers1624.ccintelli.util.logging.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 23/01/2017.
 */
public class GuiFields {

    //The order of the entries in this list will be the order they show.
    //I can apply a comparator to the list before display to sort it.
    public static List<Module> modules = new LinkedList<>();
    public static Module forgeModule;
    public static List<String> fmlCorePlugins = new LinkedList<>();
    public static List<String> vmArgs = new LinkedList<>(ImmutableList.of("-Xmx4G", "-Xms1G"));

    public static EnumLanguageLevel projectLangLevel = EnumLanguageLevel.JDK_1_8;
    public static EnumLanguageLevel projectBytecodeLevel = EnumLanguageLevel.JDK_1_8;

    static {
        forgeModule = new Module();
        forgeModule.NAME = "Forge";
        forgeModule.CONTENT_ROOT = new File("Forge");
        forgeModule.sourceFolders.addAll(findModuleSrc(forgeModule));
        forgeModule.langLevel = forgeModule.bytecodeLevel = EnumLanguageLevel.JDK_1_6;
        modules.add(forgeModule);
    }

    public static List<String> findModuleSrc(Module module) {
        List<String> list = new ArrayList<>();

        if (module.NAME.equals("Forge")) {
            list.add(new File(module.CONTENT_ROOT, "src/main/java").getAbsolutePath());
            list.add(new File(module.CONTENT_ROOT, "src/main/resources").getAbsolutePath());
            list.add(new File(module.CONTENT_ROOT, "projects/Forge/src/main/java").getAbsolutePath());
            list.add(new File(module.CONTENT_ROOT, "projects/Forge/src/main/resources").getAbsolutePath());
            list.add(new File(module.CONTENT_ROOT, "projects/Forge/src/main/start").getAbsolutePath());
        } else {
            list.add(new File(module.CONTENT_ROOT, "src/main/java").getAbsolutePath());
            list.add(new File(module.CONTENT_ROOT, "src/main/resources").getAbsolutePath());
        }

        return list;
    }

    public static void onModuleAdded(Module module) {
        modules.add(module);
        module.orderEntries.add(new ModuleEntry("Forge", false, Scope.PROVIDED));
        forgeModule.orderEntries.add(new ModuleEntry(module.NAME, false, Scope.RUNTIME));
    }

    public static void importSetup(File fileToImport) {
        try {
            int before = modules.size();
            SetupSerializer.readSetup(fileToImport);
            LogHelper.info("Successfully imported %s modules from: %s", modules.size() - before, fileToImport.getAbsolutePath());
        } catch (IOException e) {
            LogHelper.errorError("Exception was thrown whilst importing modules from: %s", e, fileToImport.getAbsolutePath());
        }
    }

    public static void exportSetup(File targetFile) {
        try {
            SetupSerializer.writeSetup(targetFile);
            LogHelper.info("Successfully exported %s modules to: %s", modules.size() - 1, targetFile.getAbsolutePath());
        } catch (IOException e) {
            LogHelper.errorError("Exception was thrown whilst exporting modules to: %s", e, targetFile.getAbsolutePath());
        }
    }
}
