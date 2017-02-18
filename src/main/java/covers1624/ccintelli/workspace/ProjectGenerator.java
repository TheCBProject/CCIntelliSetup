package covers1624.ccintelli.workspace;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import covers1624.ccintelli.gui.GuiFields;
import covers1624.ccintelli.launch.Launch;
import covers1624.ccintelli.module.Module;
import covers1624.ccintelli.util.LogHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 11/02/2017.
 */
public class ProjectGenerator {

    private static List<Map<String, String>> resourceExtensionAttributes;
    private static List<Map<String, String>> wildcardResoaurceAttibutes;
    private static List<Map<String, String>> javadocGenerationAttributes;

    static {
        List<Map<String, String>> tempMap = new LinkedList<>();

        tempMap.add(ImmutableMap.of("name", ".+\\.(properties|xml|html|dtd|tld)"));
        tempMap.add(ImmutableMap.of("name", ".+\\.(gif|png|jpeg|jpg)"));

        resourceExtensionAttributes = ImmutableList.copyOf(tempMap);
        tempMap.clear();

        tempMap.add(ImmutableMap.of("name", "!?*.class"));
        tempMap.add(ImmutableMap.of("name", "!?*.scala"));
        tempMap.add(ImmutableMap.of("name", "!?*.groovy"));
        tempMap.add(ImmutableMap.of("name", "!?*.java"));
        wildcardResoaurceAttibutes = ImmutableList.copyOf(tempMap);
        tempMap.clear();

        tempMap.add(ImmutableMap.of("name", "OUTPUT_DIRECTORY"));
        tempMap.add(ImmutableMap.of("name", "OPTION_SCOPE", "value", "protected"));
        tempMap.add(ImmutableMap.of("name", "OPTION_HIERARCHY", "value", "true"));
        tempMap.add(ImmutableMap.of("name", "OPTION_NAVIGATOR", "value", "true"));
        tempMap.add(ImmutableMap.of("name", "OPTION_INDEX", "value", "true"));
        tempMap.add(ImmutableMap.of("name", "OPTION_SEPARATE_INDEX", "value", "true"));
        tempMap.add(ImmutableMap.of("name", "OPTION_DOCUMENT_TAG_USE", "value", "false"));
        tempMap.add(ImmutableMap.of("name", "OPTION_DOCUMENT_TAG_AUTHOR", "value", "false"));
        tempMap.add(ImmutableMap.of("name", "OPTION_DOCUMENT_TAG_VERSION", "value", "false"));
        tempMap.add(ImmutableMap.of("name", "OPTION_DOCUMENT_TAG_DEPRECATED", "value", "false"));
        tempMap.add(ImmutableMap.of("name", "OPTION_DEPRECATED_LIST", "value", "false"));
        tempMap.add(ImmutableMap.of("name", "OTHER_OPTIONS", "value", ""));
        tempMap.add(ImmutableMap.of("name", "HEAP_SIZE"));
        tempMap.add(ImmutableMap.of("name", "LOCALE"));
        tempMap.add(ImmutableMap.of("name", "OPEN_IN_BROWSER", "value", "true"));
        javadocGenerationAttributes = ImmutableList.copyOf(tempMap);
    }


    public static void generateWorkspace() {
        LogHelper.info("Generating Project configuration..");
        File workspaceIPR = new File(Launch.WORKSPACE, "Workspace.ipr");
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlVersion("1.0");
            document.setXmlStandalone(true);

            Element projectElement = document.createElement("project");
            document.appendChild(projectElement);

            projectElement.setAttribute("version", "4");
            {// Compiler
                Element compilerComponent = document.createElement("component");
                projectElement.appendChild(compilerComponent);
                compilerComponent.setAttribute("name", "CompilerConfiguration");

                Element defaultCompiler = document.createElement("option");
                compilerComponent.appendChild(defaultCompiler);
                defaultCompiler.setAttribute("value", Launch.COMPILER_SELECT);
                defaultCompiler.setAttribute("name", "DEFAULT_COMPILER");

                Element notNullAssertions = document.createElement("addNotNullAssertions");
                compilerComponent.appendChild(notNullAssertions);
                notNullAssertions.setAttribute("enabled", String.valueOf(Launch.NOT_NULL_ASSERTIONS).toLowerCase(Locale.US));

                Element resourceExtensions = document.createElement("resourceExtensions");
                compilerComponent.appendChild(resourceExtensions);
                appendBatchedAttributes(document, resourceExtensions, "entry", resourceExtensionAttributes);

                Element wildResourcePatterns = document.createElement("wildcardResourcePatterns");
                compilerComponent.appendChild(wildResourcePatterns);
                appendBatchedAttributes(document, wildResourcePatterns, "entry", wildcardResoaurceAttibutes);

                Element annotationProcessing = document.createElement("annotationProcessing");
                compilerComponent.appendChild(annotationProcessing);
                annotationProcessing.setAttribute("enabled", "false");
                annotationProcessing.setAttribute("useClasspath", "true");

                Element bytecodeLevel = document.createElement("bytecodeTargetLevel");
                compilerComponent.appendChild(bytecodeLevel);
                bytecodeLevel.setAttribute("target", GuiFields.projectBytecodeLevel.getBytecodeTarget());

                List<Map<String, String>> moduleByteCodeLevels = new LinkedList<>();
                for (Module module : GuiFields.modules) {
                    moduleByteCodeLevels.add(ImmutableMap.of("name", module.NAME, "target", module.bytecodeLevel.getBytecodeTarget()));
                }
                appendBatchedAttributes(document, bytecodeLevel, "module", moduleByteCodeLevels);
            }

            {// Copyright
                Element copyrightComponent = document.createElement("component");
                projectElement.appendChild(copyrightComponent);
                copyrightComponent.setAttribute("name", "CopyrightManager");
                copyrightComponent.setAttribute("default", "");
                copyrightComponent.appendChild(document.createElement("module2copyright"));
            }

            {// DependencyValidation
                Element dependencyCoponent = document.createElement("component");
                projectElement.appendChild(dependencyCoponent);
                dependencyCoponent.setAttribute("name", "DependencyValidationManager");
                Element option1 = document.createElement("option");
                dependencyCoponent.appendChild(option1);
                option1.setAttribute("name", "SKIP_IMPORT_STATEMENTS");
                option1.setAttribute("value", "false");
            }

            {// Encoding
                Element encodingComponent = document.createElement("component");
                projectElement.appendChild(encodingComponent);
                encodingComponent.setAttribute("name", "Encoding");
                encodingComponent.setAttribute("useUTFGuessing", "true");
                encodingComponent.setAttribute("native2AsciiForPropertiesFiles", "false");
            }

            {// GradleUiSettings
                Element gradleSettingElement = document.createElement("component");
                projectElement.appendChild(gradleSettingElement);
                gradleSettingElement.setAttribute("name", "GradleUISettings");
                Element setting1 = document.createElement("setting");
                gradleSettingElement.appendChild(setting1);
                setting1.setAttribute("name", "root");
            }

            {// GradleUiSettings2
                Element gradleSettingElement = document.createElement("component");
                projectElement.appendChild(gradleSettingElement);
                gradleSettingElement.setAttribute("name", "GradleUISettings2");
                Element setting1 = document.createElement("setting");
                gradleSettingElement.appendChild(setting1);
                setting1.setAttribute("name", "root");
            }

            {// IdProvider
                Element idProviderComponent = document.createElement("component");
                projectElement.appendChild(idProviderComponent);
                idProviderComponent.setAttribute("name", "IdProvider");
                idProviderComponent.setAttribute("IDEtalkID", "11DA1DB66DD62DDA1ED602B7079FE97C");
            }

            {// JavadocGeneration
                Element javadocGenerationComponent = document.createElement("component");
                projectElement.appendChild(javadocGenerationComponent);
                javadocGenerationComponent.setAttribute("name", "javadocGenerationManager");
                appendBatchedAttributes(document, javadocGenerationComponent, "option", javadocGenerationAttributes);
            }

            {// Modules!
                Element moduleComponent = document.createElement("component");
                projectElement.appendChild(moduleComponent);
                moduleComponent.setAttribute("name", "ProjectModuleManager");
                Element modulesElement = document.createElement("modules");
                moduleComponent.appendChild(modulesElement);

                for (Module module : GuiFields.modules) {
                    Element moduleElement = document.createElement("module");
                    modulesElement.appendChild(moduleElement);
                    File file = new File(Launch.MODULES, module.NAME + ".iml");
                    moduleElement.setAttribute("filepath", file.getAbsolutePath());
                    moduleElement.setAttribute("fileurl", "file://" + file.getAbsolutePath());
                    if (!Strings.isNullOrEmpty(module.GROUP)) {
                        moduleElement.setAttribute("group", module.GROUP);
                    }
                }
            }

            { //ProjectRoot
                Element projectRootComponent = document.createElement("component");
                projectElement.appendChild(projectRootComponent);
                projectRootComponent.setAttribute("name", "ProjectRootManager");
                projectRootComponent.setAttribute("version", "2");
                projectRootComponent.setAttribute("languageLevel", GuiFields.projectLangLevel.getXMLName());//TODO See other TODO above.
                projectRootComponent.setAttribute("assert-keyword", "true");
                projectRootComponent.setAttribute("jdk-15", "true");
                projectRootComponent.setAttribute("project-jdk-type", "JavaSDK");
                projectRootComponent.setAttribute("assert-jdk-15", "true");
                projectRootComponent.setAttribute("project-jdk-name", "1.8");

                Element outputElement = document.createElement("output");
                projectRootComponent.appendChild(outputElement);
                outputElement.setAttribute("url", "file://" + Launch.PROJECT_OUTPUT.getAbsolutePath());
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(workspaceIPR);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong generating the Workspace.ipr", e);
        }
    }

    public static void appendBatchedAttributes(Document document, Element parent, String elementName, List<Map<String, String>> valueMap) {
        for (Map<String, String> values : valueMap) {
            Element element = document.createElement(elementName);
            parent.appendChild(element);
            for (Entry<String, String> entry : values.entrySet()) {
                element.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

}
