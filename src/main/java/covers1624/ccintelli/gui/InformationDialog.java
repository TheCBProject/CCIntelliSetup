package covers1624.ccintelli.gui;

import com.google.common.collect.ImmutableList;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.util.List;

/**
 * Created by covers1624 on 19/02/2017.
 */
public class InformationDialog {

    private final String title;
    private final Component parent;
    private final List<String> data;

    public InformationDialog(String title, Component parent, List<String> data) {
        this.title = title;
        this.parent = parent;
        this.data = ImmutableList.copyOf(data);
    }

    public void dispaly() {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (String line : data) {
            builder.append("<br><center>");
            builder.append(line);
        }
        builder.append("</html>");
        JEditorPane pane = new JEditorPane("text/html", builder.toString());
        pane.setAutoscrolls(true);
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.addHyperlinkListener(e -> {
            try {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                }
            } catch (Exception ignored) {
            }
        });
        JOptionPane.showMessageDialog(parent, pane, title, -1);
    }

}
