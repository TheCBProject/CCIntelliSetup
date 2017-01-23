package covers1624.ccintelli.gui;

import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.module.ModuleEntry;
import covers1624.ccintelli.module.OrderEntry;
import covers1624.ccintelli.util.logger.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 23/01/2017.
 */
public class GuiFields {
    //The oreder of the entries in this list will be the order they show.
    //I can apply a comparator to the list before display to sort it.
    public static List<Module> modules = new LinkedList<>();

    static {
        Module module = new Module();
        module.NAME = "Forge";
        module.CONTENT_ROOT = new File("Forge");
        module.sourceFolders = findModuleSrc(module);
        module.orderEntries.add(new ModuleEntry("testDep1", true, OrderEntry.Scope.COMPILE));
        module.orderEntries.add(new ModuleEntry("testDep2", false, OrderEntry.Scope.RUNTIME));
        module.orderEntries.add(new ModuleEntry("testDep3", true, OrderEntry.Scope.TEST));
        module.orderEntries.add(new ModuleEntry("testDep4", true, OrderEntry.Scope.PROVIDED));
        modules.add(module);
    }

    //TODO... Im a little confused as to what you were trying to tell me to do. I was asking for defaults when a module is created. Is that what this is meant to return?
    public static List<String> findModuleSrc(Module module) {
        return new ArrayList<String>(){{add("src/main/resources");}};
    }

    public static void importSetup(File fileToImport) {
        LogHelper.info("Import From: " + fileToImport);
    }

    public static void exportSetup(File targetFile) {
        LogHelper.info("Export As: " + targetFile);
    }
}
