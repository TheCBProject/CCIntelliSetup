package covers1624.ccintelli.module;

import com.google.common.base.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 21/01/2017.
 */
public abstract class OrderEntry {

	public final Type type;
	public boolean export;
	public Scope scope;
	public Map<String, String> extra = new LinkedHashMap<>();

	protected OrderEntry(Type type, boolean export, Scope scope) {
		this.type = type;
		this.export = export;
		this.scope = scope;
	}

	public Element createElement(Document document) {
		Element element = document.createElement("orderEntry");
		element.setAttribute("type", type.name);
		if (export) {
			element.setAttribute("exported", "");
		}
		if (scope != Scope.NONE) {
			element.setAttribute("scope", scope.name);
		}
		if (!extra.isEmpty()) {
			for (Entry<String, String> entry : extra.entrySet()) {
				element.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		return element;
	}

	public enum Type {
		SOURCE_FOLDER("sourceFolder"),
		INHERITED_JDK("inheritedJdk"),
		MODULE_LIBRARY("module-library"),
		MODULE("module");

		String name;

		Type(String name) {
			this.name = name;
		}
	}

	public enum Scope {
		NONE(""),
		//Wont gen in the xml.
		COMPILE("COMPILE"),
		TEST("TEST"),
		RUNTIME("RUNTIME"),
		PROVIDED("PROVIDED");

		String name;

		Scope(String name) {
			this.name = name;
		}

		public static Scope fromString(String name) {
			if (Strings.isNullOrEmpty(name)) {
				return NONE;
			}
			for (Scope scope : Scope.values()) {
				if (scope.name.equals(name)) {
					return scope;
				}
			}
			return NONE;
		}
	}

}
