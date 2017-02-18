package covers1624.launchwrapper.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

/**
 * Created by covers1624 on 2/15/2016.
 */
public class LauncherGui extends JOptionPane implements ILauncherDisplay {

    private JDialog dialog;
    private JLabel activity;
    private JProgressBar progressBar;
    boolean stop;
    private Thread pokeThread;
    private String name;

    public LauncherGui(String name) {
        this.name = name;
    }

    @Override
    public void init() {
        setMessageType(JOptionPane.INFORMATION_MESSAGE);
        setMessage(makeProgress());
        setOptions(new Object[] { "Stop" });
        addPropertyChangeListener(evt -> {
            if (evt.getSource() == LauncherGui.this && evt.getPropertyName().equals(VALUE_PROPERTY)) {
                requestClose("This will stop " + name + " from launching.\nAre you sure you want to do this?");
            }
        });
        dialog = new JDialog(null, "Hello", Dialog.ModalityType.MODELESS);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.add(this);
        updateUI();
        dialog.pack();
        dialog.setMinimumSize(new Dimension(640, 197));
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                requestClose("Closing this will stop " + name + " from launching\nAre you sure you want to do this?");
            }
        });
    }

    @Override
    public void dispose() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    private Box makeProgress() {
        Box box = Box.createVerticalBox();
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel welcomeLabel = new JLabel("<html><b><font size='+1'>Setting up " + name + "!</font></b></html>");
        box.add(welcomeLabel);
        activity = new JLabel("Working on...");
        box.add(activity);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        box.add(progressBar);
        box.add(Box.createRigidArea(new Dimension(0, 30)));

        return box;
    }

    protected void requestClose(String message) {
        int close = JOptionPane.showConfirmDialog(dialog, message, "Are you sure you want to stop?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (close == YES_OPTION) {
            dialog.dispose();
        }
        stop = true;
        if (pokeThread != null) {
            pokeThread.interrupt();
        }
    }

    @Override
    public void resetProgress(int sizeGuess) {
        if (progressBar != null) {
            progressBar.getModel().setRangeProperties(0, 0, 0, sizeGuess, false);
        }
    }

    @Override
    public void updateProgress(int fullLength) {
        if (progressBar != null) {
            progressBar.getModel().setValue(fullLength);
        }
    }

    @Override
    public void updateProgressString(String string, Object... data) {
        if (activity != null) {
            activity.setText(String.format(string, data));
        }
    }

    @Override
    public boolean shouldClose() {
        return stop;
    }

    @Override
    public void setPokeThread(Thread thread) {
        pokeThread = thread;
    }

    @Override
    public void onDownloadFail(String file, URL url, Exception e) {
    }
}
