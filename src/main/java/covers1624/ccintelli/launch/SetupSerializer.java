package covers1624.ccintelli.launch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import covers1624.ccintelli.gui.GuiFields;
import covers1624.ccintelli.module.Module;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by covers1624 on 19/02/2017.
 */
public class SetupSerializer {

    public static void readSetup(File json) throws IOException {

        JsonReader reader = new JsonReader(new FileReader(json));
        reader.setLenient(true);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(reader).getAsJsonObject();
        JsonArray array = object.getAsJsonArray("modules");
        for (JsonElement element : array) {
            Module module = Module.fromJson(element.getAsJsonObject());
            if (!containsModule(module)) {
                GuiFields.modules.add(module);
            }
        }
        for (JsonElement element : object.getAsJsonArray("core_plugins")) {
            GuiFields.fmlCorePlugins.add(element.getAsString());
        }
        GuiFields.vmArgs.clear();
        for (JsonElement element : object.getAsJsonArray("vm_args")) {
            GuiFields.vmArgs.add(element.getAsString());
        }
    }

    public static void writeSetup(File json) throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter(json));
        writer.setLenient(true);
        writer.setIndent("    ");

        JsonObject object = new JsonObject();
        JsonArray moduleArray = new JsonArray();
        for (Module module : GuiFields.modules) {
            //if (!module.NAME.equals("Forge")) {
            moduleArray.add(module.toJson());
            //}
        }
        object.add("modules", moduleArray);
        JsonArray corePlugins = new JsonArray();
        for (String corePlugin : GuiFields.fmlCorePlugins) {
            corePlugins.add(corePlugin);
        }
        object.add("core_plugins", corePlugins);

        JsonArray vmArgs = new JsonArray();
        for (String arg : GuiFields.vmArgs) {
            vmArgs.add(arg);
        }
        object.add("vm_args", vmArgs);

        Streams.write(object, writer);
        writer.flush();
    }

    private static boolean containsModule(Module test) {
        for (Module suspect : GuiFields.modules) {
            if (suspect.NAME.equals(test.NAME)) {
                return true;
            }
        }
        return false;
    }
}
