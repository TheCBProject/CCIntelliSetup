/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covers1624.ccintelli.gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author brandon3055
 */
public class CCIntelliSetupConsole extends javax.swing.JFrame {

    public CCIntelliSetupConsole() {
        initComponents();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tryClose();
            }
        });
        closeButton.addActionListener(e -> tryClose());
    }

    private void tryClose() {
        if (JOptionPane.showConfirmDialog(this, "This will only close the console not the main program.", "Close?", JOptionPane.OK_CANCEL_OPTION) == 0) {
            dispose();
        }
    }

    public void setStatus(final String statusText) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                statusLabel.setText(statusText);
            }
        });
    }

    private void print(final String appendText) {
        SwingUtilities.invokeLater(() -> consoleTextPane.setText(consoleTextPane.getText() + appendText));
    }

    public void println(String newLine) {
        print("\n" + newLine);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        scrollPane = new JScrollPane();
        consoleTextPane = new JTextPane();
        buttonPane = new JPanel();
        closeButton = new JButton();
        statusPanel = new JPanel();
        statusLabel = new JLabel();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CCIntelliSetup Console");

        consoleTextPane.setEditable(false);
        //consoleTextPane.setText("");
        scrollPane.setViewportView(consoleTextPane);

        closeButton.setText("Close");

        GroupLayout buttonPaneLayout = new GroupLayout(buttonPane);
        buttonPane.setLayout(buttonPaneLayout);
        buttonPaneLayout.setHorizontalGroup(buttonPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, buttonPaneLayout.createSequentialGroup().addContainerGap(362, Short.MAX_VALUE).addComponent(closeButton).addContainerGap()));
        buttonPaneLayout.setVerticalGroup(buttonPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(buttonPaneLayout.createSequentialGroup().addGap(0, 0, 0).addComponent(closeButton).addGap(0, 0, Short.MAX_VALUE)));

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setText("");

        GroupLayout statusPanelLayout = new GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(statusPanelLayout.createSequentialGroup().addContainerGap().addComponent(statusLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
        statusPanelLayout.setVerticalGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(statusPanelLayout.createSequentialGroup().addContainerGap().addComponent(statusLabel, GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(buttonPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(statusPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(scrollPane));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(buttonPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap()));

        pack();
    }// </editor-fold>

    // Variables declaration - do not modify
    private JPanel buttonPane;
    private JButton closeButton;
    private JTextPane consoleTextPane;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JPanel statusPanel;
    // End of variables declaration
}
