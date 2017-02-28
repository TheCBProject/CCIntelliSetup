package covers1624.ccintelli.workspace;

import covers1624.ccintelli.gui.GuiFields;
import covers1624.ccintelli.launch.Launch;
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
import java.util.ArrayList;
import java.util.List;

import static covers1624.util.XMLUtils.createAndAdd;
import static covers1624.util.XMLUtils.getFirstElementNode;

/**
 * Created by covers1624 on 20/02/2017.
 */
public class RunConfigGenerator {

    public static void generateRunConfigs(String projectName) {
        File workspaceIWS = new File(Launch.WORKSPACE, projectName + ".iws");
        try {
            List<String> vmArgs = new ArrayList<>();

            vmArgs.addAll(GuiFields.vmArgs);

            if (!GuiFields.fmlCorePlugins.isEmpty()) {
                StringBuilder builder = new StringBuilder("-Dfml.coreMods.load=");
                boolean hasFirst = false;
                for (String corePlugin : GuiFields.fmlCorePlugins) {
                    if (hasFirst) {
                        builder.append(",");
                    }
                    hasFirst = true;
                    builder.append(corePlugin);
                }
                vmArgs.add(builder.toString());
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(RunConfigGenerator.class.getResourceAsStream("/iwsTemplate.xml"));
            document.getDocumentElement().normalize();

            Element documentElement = document.getDocumentElement();
            Element componentElement = getFirstElementNode(documentElement, "component");
            {
                Element clientRun = createAndAdd(document, componentElement, "configuration");
                clientRun.setAttribute("factoryName", "Application");
                clientRun.setAttribute("type", "Application");
                clientRun.setAttribute("name", "Minecraft Client");
                clientRun.setAttribute("default", "false");
                Element extension = createAndAdd(document, clientRun, "extension");
                extension.setAttribute("runner", "idea");
                extension.setAttribute("merge", "false");
                extension.setAttribute("enabled", "false");
                extension.setAttribute("name", "coverage");
                Element mainClassOpt = createAndAdd(document, clientRun, "option");
                mainClassOpt.setAttribute("value", "GradleStart");
                mainClassOpt.setAttribute("name", "MAIN_CLASS_NAME");
                StringBuilder argBuilder = new StringBuilder();
                boolean hasFirst = false;
                for (String arg : vmArgs) {
                    if (hasFirst) {
                        argBuilder.append(" ");
                    }
                    hasFirst = true;
                    argBuilder.append(arg);
                }
                Element vmArgsOpt = createAndAdd(document, clientRun, "option");
                vmArgsOpt.setAttribute("value", argBuilder.toString());
                vmArgsOpt.setAttribute("name", "VM_PARAMETERS");
                Element progArgsOpt = createAndAdd(document, clientRun, "option");
                progArgsOpt.setAttribute("value", "");
                progArgsOpt.setAttribute("name", "PROGRAM_PARAMETERS");
                Element workingDirOpt = createAndAdd(document, clientRun, "option");
                workingDirOpt.setAttribute("value", "file://" + Launch.PROJECT_RUN.getAbsoluteFile().getPath());
                workingDirOpt.setAttribute("name", "WORKING_DIRECTORY");
                Element altJERpathEnableOpt = createAndAdd(document, clientRun, "option");
                altJERpathEnableOpt.setAttribute("value", "false");
                altJERpathEnableOpt.setAttribute("name", "ALTERNATIVE_JRE_PATH_ENABLED");
                Element aldJrePathOpt = createAndAdd(document, clientRun, "option");
                aldJrePathOpt.setAttribute("name", "ALTERNATIVE_JRE_PATH");
                Element swingInspecOpt = createAndAdd(document, clientRun, "option");
                swingInspecOpt.setAttribute("value", "false");
                swingInspecOpt.setAttribute("name", "ENABLE_SWING_INSPECTOR");
                Element envVarsOpt = createAndAdd(document, clientRun, "option");
                envVarsOpt.setAttribute("name", "ENV_VARIABLES");
                Element passParentEnvsOpt = createAndAdd(document, clientRun, "option");
                passParentEnvsOpt.setAttribute("value", "true");
                passParentEnvsOpt.setAttribute("name", "PASS_PARENT_ENVS");

                Element moduleClassPathElement = createAndAdd(document, clientRun, "module");
                moduleClassPathElement.setAttribute("name", GuiFields.forgeModule.NAME);
                createAndAdd(document, clientRun, "envs");
                createAndAdd(document, clientRun, "method");
            }
            {
                Element serverRun = createAndAdd(document, componentElement, "configuration");
                serverRun.setAttribute("factoryName", "Application");
                serverRun.setAttribute("type", "Application");
                serverRun.setAttribute("name", "Minecraft Server");
                serverRun.setAttribute("default", "false");
                Element extension = createAndAdd(document, serverRun, "extension");
                extension.setAttribute("runner", "idea");
                extension.setAttribute("merge", "false");
                extension.setAttribute("enabled", "false");
                extension.setAttribute("name", "coverage");
                Element mainClassOpt = createAndAdd(document, serverRun, "option");
                mainClassOpt.setAttribute("value", "GradleStartServer");
                mainClassOpt.setAttribute("name", "MAIN_CLASS_NAME");
                StringBuilder argBuilder = new StringBuilder();
                boolean hasFirst = false;
                for (String arg : vmArgs) {
                    if (hasFirst) {
                        argBuilder.append(" ");
                    }
                    hasFirst = true;
                    argBuilder.append(arg);
                }
                Element vmArgsOpt = createAndAdd(document, serverRun, "option");
                vmArgsOpt.setAttribute("value", argBuilder.toString());
                vmArgsOpt.setAttribute("name", "VM_PARAMETERS");
                Element progArgsOpt = createAndAdd(document, serverRun, "option");
                progArgsOpt.setAttribute("value", "");
                progArgsOpt.setAttribute("name", "PROGRAM_PARAMETERS");
                Element workingDirOpt = createAndAdd(document, serverRun, "option");
                workingDirOpt.setAttribute("value", "file://" + Launch.PROJECT_RUN.getAbsoluteFile().getPath());
                workingDirOpt.setAttribute("name", "WORKING_DIRECTORY");
                Element altJERpathEnableOpt = createAndAdd(document, serverRun, "option");
                altJERpathEnableOpt.setAttribute("value", "false");
                altJERpathEnableOpt.setAttribute("name", "ALTERNATIVE_JRE_PATH_ENABLED");
                Element aldJrePathOpt = createAndAdd(document, serverRun, "option");
                aldJrePathOpt.setAttribute("name", "ALTERNATIVE_JRE_PATH");
                Element swingInspecOpt = createAndAdd(document, serverRun, "option");
                swingInspecOpt.setAttribute("value", "false");
                swingInspecOpt.setAttribute("name", "ENABLE_SWING_INSPECTOR");
                Element envVarsOpt = createAndAdd(document, serverRun, "option");
                envVarsOpt.setAttribute("name", "ENV_VARIABLES");
                Element passParentEnvsOpt = createAndAdd(document, serverRun, "option");
                passParentEnvsOpt.setAttribute("value", "true");
                passParentEnvsOpt.setAttribute("name", "PASS_PARENT_ENVS");

                Element moduleClassPathElement = createAndAdd(document, serverRun, "module");
                moduleClassPathElement.setAttribute("name", GuiFields.forgeModule.NAME);
                createAndAdd(document, serverRun, "envs");
                createAndAdd(document, serverRun, "method");
            }

            Element listElement = getFirstElementNode(componentElement, "list");
            listElement.setAttribute("size", "2");
            Element clientChild = createAndAdd(document, listElement, "item");
            clientChild.setAttribute("itemvalue", "Application.Minecraft Client");
            clientChild.setAttribute("class", "java.lang.String");
            clientChild.setAttribute("index", "0");

            Element serverChild = createAndAdd(document, listElement, "item");
            serverChild.setAttribute("itemvalue", "Application.Minecraft Server");
            serverChild.setAttribute("class", "java.lang.String");
            serverChild.setAttribute("index", "1");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(workspaceIWS);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException("Exception thrown whilst generating run configs.", e);
        }
    }
}
