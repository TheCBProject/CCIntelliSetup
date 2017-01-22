package covers1624.ccintelli.module;

import com.google.common.base.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by covers1624 on 21/01/2017.
 */
public class LibraryEntry extends OrderEntry {

	private final String CLASSES_ROOT;
	private final String JAVADOC_ROOT;
	private final String SOURCES_ROOT;

	protected LibraryEntry(boolean export, Scope scope, String classesRoot, String javadocRoot, String sourcesRoot) {
		super(Type.MODULE_LIBRARY, export, scope);
		CLASSES_ROOT = classesRoot;
		JAVADOC_ROOT = javadocRoot;
		SOURCES_ROOT = sourcesRoot;
	}

	@Override
	public Element createElement(Document document) {
		Element element = super.createElement(document);
		Element libElement = document.createElement("library");
		Element classesElement = document.createElement("CLASSES");
		Element javadocElement = document.createElement("JAVADOC");
		Element sourcesElement = document.createElement("SOURCES");

		if (!Strings.isNullOrEmpty(CLASSES_ROOT)) {
			Element root = document.createElement("root");
			root.setAttribute("url", CLASSES_ROOT);
			classesElement.appendChild(root);
		}
		if (!Strings.isNullOrEmpty(JAVADOC_ROOT)) {
			Element root = document.createElement("root");
			root.setAttribute("url", JAVADOC_ROOT);
			javadocElement.appendChild(root);
		}
		if (!Strings.isNullOrEmpty(SOURCES_ROOT)) {
			Element root = document.createElement("root");
			root.setAttribute("url", SOURCES_ROOT);
			sourcesElement.appendChild(root);
		}
		libElement.appendChild(classesElement);
		libElement.appendChild(javadocElement);
		libElement.appendChild(sourcesElement);
		element.appendChild(libElement);
		return element;
	}

	public static LibraryEntry fromElement(Element element, boolean export, Scope scope) {
		String classes = null;
		String javadoc = null;
		String sources = null;
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nodeElement = ((Element) node);
				switch (nodeElement.getTagName()) {
					case "CLASSES":
						if (nodeElement.hasChildNodes()) {
							classes = ((Element) nodeElement.getElementsByTagName("root").item(0)).getAttribute("url");
						}
						break;
					case "JAVADOC":
						if (nodeElement.hasChildNodes()) {
							javadoc = ((Element) nodeElement.getElementsByTagName("root").item(0)).getAttribute("url");
						}
						break;
					case "SOURCES":
						if (nodeElement.hasChildNodes()) {
							sources = ((Element) nodeElement.getElementsByTagName("root").item(0)).getAttribute("url");
						}
						break;
				}
			}
		}
		return new LibraryEntry(export, scope, classes, javadoc, sources);
	}
}
