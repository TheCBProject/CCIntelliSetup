package covers1624.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Objects;

/**
 * Created by covers1624 on 28/02/2017.
 */
public class XMLUtils {

    /**
     * Walks the provided elements child nodes for the first Element node.
     *
     * @param element The element in which to walk the child nodes
     * @return The first element, Null if no Element nodes exist.
     */
    public static Element getFirstElementNode(Element element) {
        for (Node node : new NodeListIterator(element.getChildNodes())) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return ((Element) node);
            }
        }
        return null;
    }

    /**
     * Walks the provided elements child nodes for the first Element node with the given name.
     *
     * @param element The element in which to walk the child nodes
     * @param name    The name for the child element to find.
     * @return The first element, Null if no Element nodes exist with the given name.
     */
    public static Element getFirstElementNode(Element element, String name) {
        for (Node node : new NodeListIterator(element.getChildNodes())) {
            if (node.getNodeType() == Node.ELEMENT_NODE && Objects.equals(node.getNodeName(), name)) {
                return ((Element) node);
            }
        }
        return null;
    }

    public static Element createAndAdd(Document document, Element parent, String name) {
        Element element = document.createElement(name);
        parent.appendChild(element);
        return element;
    }

}
