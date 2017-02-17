package covers1624.ccintelli.gui;

import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.module.ModuleEntry;
import covers1624.ccintelli.module.OrderEntry.Scope;
import covers1624.ccintelli.util.EnumLanguageLevel;
import covers1624.ccintelli.util.LogHelper;

import java.io.File;
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
        LogHelper.info("Import From: " + fileToImport);
    }

    public static void exportSetup(File targetFile) {
        LogHelper.info("Export As: " + targetFile);
    }
}
