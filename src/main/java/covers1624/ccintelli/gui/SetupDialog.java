package covers1624.ccintelli.gui;

import com.google.common.collect.ImmutableList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SetupDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField directoryField;
    private JButton choosButton;
    private JComboBox recentBox;

    public SetupDialog() {
        this(ImmutableList.of());
        choosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelect();
            }
        });
    }

    public SetupDialog(java.util.List<File> recents) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        recentBox.addItemListener(e -> directoryField.setText(e.getItem().toString()));

        setTitle("Select Workspace Directory");
        pack();

        for (File file : recents) {
            recentBox.addItem(file.getAbsoluteFile());
        }

        setSize(600, getHeight());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        if (recents.isEmpty()) {
            directoryField.setText(new File("").getAbsolutePath());
        }
        else {
            directoryField.setText(recents.get(0).getAbsolutePath());
        }
    }

    public File getDirectory() {
        setVisible(true);
        if (directoryField.getText().isEmpty()) {
            return null;
        }
        return new File(directoryField.getText());
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        directoryField.setText("");
        dispose();
    }

    private void onSelect() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(directoryField.getText()));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
