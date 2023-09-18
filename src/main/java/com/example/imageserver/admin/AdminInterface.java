package com.example.imageserver.admin;

import com.example.imageserver.data.FolderList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class AdminInterface {

    private final JFrame mainWindow;
    private final JToolBar toolBar;
    private final JTextArea textArea;
    private final JFileChooser folderChooser;

    public AdminInterface() {
        folderChooser = new JFileChooser("./");
        folderChooser.setMultiSelectionEnabled(false);
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        mainWindow = new JFrame("Image Server - Admin Interface");

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(createButton("Add Folder", true, e->{
            if (folderChooser.showOpenDialog(mainWindow)==JFileChooser.APPROVE_OPTION)
                addFolder(folderChooser.getSelectedFile());
        }));
        toolBar.add(createButton("Add Example Folder", true, e->{
            File folder = new File("/Users/hendrik/Pictures/Hintergrund");
            if (folder.isDirectory())
                addFolder(folder);
        }));

        textArea = new JTextArea();
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setPreferredSize(new Dimension(500,300));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(toolBar, BorderLayout.PAGE_START);
        contentPane.add(textAreaScrollPane, BorderLayout.CENTER);

        mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainWindow.setContentPane(contentPane);
        mainWindow.setLocationByPlatform(true);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }

    public static void setLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {}
    }


    private void addFolder(File folder) {
        FolderList.instance.add(folder);
        textArea.setText(FolderList.instance.toString());
    }

    public void initialize() {

    }

    private static JButton createButton(String text, boolean enabled, ActionListener al) {
        JButton comp = new JButton(text);
        comp.setEnabled(enabled);
        if (al!=null) comp.addActionListener(al);
        return comp;
    }
}
