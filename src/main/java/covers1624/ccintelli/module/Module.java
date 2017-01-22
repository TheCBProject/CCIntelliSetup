package covers1624.ccintelli.module;

import covers1624.ccintelli.module.OrderEntry.Scope;
import covers1624.ccintelli.module.OrderEntry.Type;
import covers1624.ccintelli.util.Utils;
import covers1624.ccintelli.util.logger.LogHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 21/01/2017.
 */
public class Module {

	private final File CONTENT_ROOT;
	public List<String> soruceFolders = new LinkedList<>();
	public List<OrderEntry> orderEntries = new LinkedList<>();

	public Module(File content_root) {
		CONTENT_ROOT = content_root;
	}

	public void writeXML(File file) {
		try {
			Utils.tryCreateFile(file);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			document.setXmlVersion("1.0");
			document.setXmlStandalone(true);

			Element moduleElement = document.createElement("module");
			document.appendChild(moduleElement);
			moduleElement.setAttribute("relativePaths", "false");//TODO
			moduleElement.setAttribute("type", "JAVA_MODULE");
			moduleElement.setAttribute("version", "4");

			Element componentElement = document.createElement("component");
			moduleElement.appendChild(componentElement);
			componentElement.setAttribute("name", "NewModuleRootManager");
			componentElement.setAttribute("inherit-compiler-output", "true");

			componentElement.appendChild(document.createElement("exclude-output"));

			Element contentElement = document.createElement("content");
			componentElement.appendChild(contentElement);
			contentElement.setAttribute("url", "file://" + CONTENT_ROOT.getAbsolutePath());
			for (String srcFolder : soruceFolders) {
				Element element = document.createElement("sourceFolder");
				contentElement.appendChild(element);
				element.setAttribute("url", "file://" + srcFolder);
				element.setAttribute("isTestSource", "false");
			}
			for (OrderEntry entry : orderEntries) {
				componentElement.appendChild(entry.createElement(document));
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create module XML!", e);
		}
	}

	public static Module buildForgeModule(File currentXML, File forgeDir) {
		try {
			Module module = new Module(forgeDir);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(currentXML);
			document.getDocumentElement().normalize();
			Element moduleElement = document.getDocumentElement();
			Node componentElement = moduleElement.getElementsByTagName("component").item(0);
			NodeList list = componentElement.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element entryElement = ((Element) node);
					if (entryElement.hasAttribute("type")) {
						String type = entryElement.getAttribute("type");
						switch (type) {
							case "sourceFolder":
								OrderEntry entry = new SimpleEntry(Type.SOURCE_FOLDER);
								entry.extra.put("forTests", entryElement.getAttribute("forTests"));
								module.orderEntries.add(entry);
								break;
							case "inheritedJdk":
								module.orderEntries.add(new SimpleEntry(Type.INHERITED_JDK));
								break;
							case "module-library":
								boolean exported = entryElement.hasAttribute("exported");
								Scope scope = Scope.fromString(entryElement.getAttribute("scope"));
								NodeList nodes = entryElement.getChildNodes();
								for (int j = 0; j < nodes.getLength(); j++) {
									Node node1 = nodes.item(j);
									if (node1.getNodeType() == Node.ELEMENT_NODE) {
										module.orderEntries.add(LibraryEntry.fromElement(((Element) node1), exported, scope));
										break;
									}
								}

								break;
							default:
								LogHelper.warn("Unknown type for Forge's OrderEntries in module file! Type=%s File=%s", type, currentXML.getAbsoluteFile());
								break;
						}
					}
				}
			}
			module.soruceFolders.add(new File(forgeDir, "src/main/java").getAbsolutePath());
			module.soruceFolders.add(new File(forgeDir, "src/main/resources").getAbsolutePath());
			module.soruceFolders.add(new File(forgeDir, "projects/Forge/src/main/java").getAbsolutePath());
			module.soruceFolders.add(new File(forgeDir, "projects/Forge/src/main/resources").getAbsolutePath());
			module.soruceFolders.add(new File(forgeDir, "projects/Forge/src/main/start").getAbsolutePath());

			return module;
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse existing forge XML!", e);
		}
	}

}
