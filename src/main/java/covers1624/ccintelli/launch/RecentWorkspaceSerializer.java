package covers1624.ccintelli.launch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 18/02/2017.
 */
public class RecentWorkspaceSerializer {

    private final File FILE;

    public RecentWorkspaceSerializer(File file) {

        FILE = file;
    }

    public List<File> load() {
        List<File> files = new LinkedList<>();
        try {
            if (!FILE.exists()) {
                return files;
            }
            JsonParser parser = new JsonParser();
            JsonReader reader = new JsonReader(new FileReader(FILE));
            reader.setLenient(true);
            JsonElement element = parser.parse(reader);
            for (JsonElement entry : element.getAsJsonArray()) {
                files.add(new File(entry.getAsString()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Unable to parse recent workspaces.", e);
        }

        return files;
    }

    public void save(List<File> files) {
        files = cleanList(files);
        if (files.isEmpty()) {
            FILE.delete();
            return;
        }
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(FILE));
            writer.setLenient(true);
            writer.setIndent("    ");
            JsonArray array = new JsonArray();
            for (File file : files) {
                array.add(file.getAbsolutePath());
            }
            Streams.write(array, writer);
            writer.flush();

        } catch (Exception e) {
            throw new RuntimeException("Unable to save recent workspaces.", e);
        }
    }

    private static List<File> cleanList(List<File> files) {
        List<File> ret = new LinkedList<>();
        for (File file : files) {
            if (!ret.contains(file)) {
                ret.add(file);
            }
        }
        return ret;
    }

}
