package covers1624.ccintelli.module;

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
}
