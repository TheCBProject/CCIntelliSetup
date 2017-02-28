package covers1624.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * Created by covers1624 on 28/02/2017.
 */
public class NodeListIterator implements Iterator<Node>, Iterable<Node> {

    private final NodeList list;
    private int index;

    public NodeListIterator(NodeList list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return list != null && index < list.getLength();
    }

    @Override
    public Node next() {
        Node object = list.item(index);
        if (object != null) {
            index++;
        }
        return object;
    }

    @Override
    public Iterator<Node> iterator() {
        return this;
    }
}
