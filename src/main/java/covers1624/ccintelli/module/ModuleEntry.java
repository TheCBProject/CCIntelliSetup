package covers1624.ccintelli.module;

import com.google.gson.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by covers1624 on 21/01/2017.
 */
public class ModuleEntry extends OrderEntry {

	public String NAME;

	public ModuleEntry(String name, boolean export, Scope scope) {
		super(Type.MODULE, export, scope);
		this.NAME = name;
	}

	@Override
	public Element createElement(Document document) {
		Element element = super.createElement(document);
		element.setAttribute("module-name", NAME);
		return element;
	}

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("export", export);
        object.addProperty("scope", scope.name);
        object.addProperty("module", NAME);
        return object;
    }

    public static ModuleEntry fromJson(JsonObject element) {
	    boolean export = element.get("export").getAsBoolean();
	    Scope scope = Scope.fromString(element.get("scope").getAsString());
	    String name = element.get("module").getAsString();
        return new ModuleEntry(name, export, scope);
    }
}
