package covers1624.ccintelli.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import covers1624.ccintelli.launch.Launch;
import covers1624.ccintelli.module.OrderEntry.Scope;
import covers1624.ccintelli.module.OrderEntry.Type;
import covers1624.ccintelli.util.EnumLanguageLevel;
import covers1624.ccintelli.util.Utils;
import covers1624.ccintelli.util.logging.LogHelper;
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

	public String NAME;
	public String GROUP = "";
	public File CONTENT_ROOT;
	public List<String> sourceFolders = new LinkedList<>();
	public List<OrderEntry> orderEntries = new LinkedList<>();
	public EnumLanguageLevel langLevel = EnumLanguageLevel.JDK_1_8;
	public EnumLanguageLevel bytecodeLevel = EnumLanguageLevel.JDK_1_8;

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
			moduleElement.setAttribute("relativePaths", "false");
			moduleElement.setAttribute("type", "JAVA_MODULE");
			moduleElement.setAttribute("version", "4");

			Element componentElement = document.createElement("component");
			moduleElement.appendChild(componentElement);
			componentElement.setAttribute("name", "NewModuleRootManager");
			componentElement.setAttribute("inherit-compiler-output", "true");
			componentElement.setAttribute("LANGUAGE_LEVEL", langLevel.getXMLName());

			componentElement.appendChild(document.createElement("exclude-output"));

			Element contentElement = document.createElement("content");
			componentElement.appendChild(contentElement);
			contentElement.setAttribute("url", "file://" + CONTENT_ROOT.getAbsolutePath());
			for (String srcFolder : sourceFolders) {
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

	public static Module buildForgeModule(File currentXML, Module forgeModule) {
		try {

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
								forgeModule.orderEntries.add(entry);
								break;
							case "inheritedJdk":
								forgeModule.orderEntries.add(new SimpleEntry(Type.INHERITED_JDK));
								break;
							case "module-library":
								boolean exported = entryElement.hasAttribute("exported");
								Scope scope = Scope.fromString(entryElement.getAttribute("scope"));
								NodeList nodes = entryElement.getChildNodes();
								for (int j = 0; j < nodes.getLength(); j++) {
									Node node1 = nodes.item(j);
									if (node1.getNodeType() == Node.ELEMENT_NODE) {
										forgeModule.orderEntries.add(LibraryEntry.fromElement(((Element) node1), exported, scope));
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

			return forgeModule;
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse existing forge XML!", e);
		}
	}

	public JsonElement toJson() {

        JsonObject object = new JsonObject();
        object.addProperty("name", NAME);
        object.addProperty("group", GROUP);

        object.addProperty("content_root", CONTENT_ROOT.getAbsolutePath().replace(Launch.SETUP_DIR.getAbsolutePath(), "").substring(1));

        object.addProperty("language_level", langLevel.name());
        object.addProperty("bytecode_level", bytecodeLevel.name());
        JsonArray array = new JsonArray();
        for (OrderEntry entry : orderEntries) {
            if (entry instanceof ModuleEntry) {
                array.add(((ModuleEntry) entry).toJson());
            }
        }
        object.add("module_entries", array);

        JsonArray srcDirs = new JsonArray();
        for (String file : sourceFolders) {
            srcDirs.add(file.replace(Launch.SETUP_DIR.getAbsolutePath(), "").substring(1));
        }

        object.add("src_dirs", srcDirs);

        return object;
    }

    public static Module fromJson(JsonObject object) {

        Module module = new Module();
        module.NAME = object.get("name").getAsString();
        module.GROUP = object.get("group").getAsString();
        String contentRoot = object.get("content_root").getAsString();
        module.CONTENT_ROOT = new File(Launch.SETUP_DIR, contentRoot);
        module.langLevel = EnumLanguageLevel.fromName(object.get("language_level").getAsString());
        module.bytecodeLevel = EnumLanguageLevel.fromName(object.get("bytecode_level").getAsString());

        for (JsonElement moduleElement : object.getAsJsonArray("module_entries")) {
            module.orderEntries.add(ModuleEntry.fromJson(moduleElement.getAsJsonObject()));
        }

        for (JsonElement element : object.getAsJsonArray("src_dirs")) {
            module.sourceFolders.add(Launch.SETUP_DIR.getAbsolutePath() + "/" + element.getAsString());
        }

        return module;
    }

}
