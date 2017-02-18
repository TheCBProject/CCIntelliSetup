package covers1624.ccintelli.gui;

import com.google.common.collect.ImmutableList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by brandon3055 on 17/02/2017.
 */
public class SetupDialog extends JDialog {
    private JPanel jPanel1;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField directoryField;
    private JButton chooseButton;
    private JComboBox recentBox;

    public SetupDialog() {
        this(ImmutableList.of());
    }

    public SetupDialog(java.util.List<File> recents) {
        initComponents();
        setModal(true);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new JPanel();
        JLabel jLabel1 = new JLabel();
        directoryField = new JTextField();
        chooseButton = new JButton();
        JLabel jLabel2 = new JLabel();
        recentBox = new JComboBox<>();
        buttonCancel = new JButton();
        buttonOK = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Select Workspace Directory");

        chooseButton.setText("...");
        chooseButton.addActionListener(evt -> onSelect());

        jLabel2.setText("Recent:");

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(evt -> onCancel());

        buttonOK.setText("OK");
        buttonOK.addActionListener(evt -> onOK());

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(directoryField)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chooseButton))
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(recentBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(buttonOK, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(buttonCancel)))
                        .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(directoryField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(chooseButton))
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(recentBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(63, Short.MAX_VALUE))
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(buttonCancel)
                                                .addComponent(buttonOK))
                                        .addContainerGap())))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

    }// </editor-fold>

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
