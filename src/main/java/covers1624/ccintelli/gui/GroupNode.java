package covers1624.ccintelli.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by brandon3055 on 23/01/2017.
 */
public class GroupNode extends DefaultMutableTreeNode {
    public final String group;

    public GroupNode(String group) {
        super(group, true);
        this.group = group;
    }
}
