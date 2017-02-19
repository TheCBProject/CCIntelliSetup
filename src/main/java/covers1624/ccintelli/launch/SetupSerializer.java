package covers1624.ccintelli.launch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    public static void readModules(File json) throws IOException {

        JsonReader reader = new JsonReader(new FileReader(json));
        reader.setLenient(true);
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(reader).getAsJsonArray();
        for (JsonElement element : array) {
            GuiFields.modules.add(Module.fromJson(element.getAsJsonObject()));
        }
    }

    public static void writeModules(File json) throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter(json));
        writer.setLenient(true);
        writer.setIndent("    ");

        JsonArray array = new JsonArray();
        for (Module module : GuiFields.modules) {
            if (!module.NAME.equals("Forge")) {
                array.add(module.toJson());
            }
        }

        Streams.write(array, writer);
        writer.flush();
    }
}
