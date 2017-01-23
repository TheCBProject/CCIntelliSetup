package covers1624.ccintelli.gui;

import covers1624.ccintelli.module.Module;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by brandon3055 on 23/01/2017.
 */
public class ModuleNode extends DefaultMutableTreeNode {
    public final Module module;

    public ModuleNode(Module module) {
        super(module.NAME, false);
        this.module = module;
    }
}
