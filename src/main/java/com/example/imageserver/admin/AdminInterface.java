package com.example.imageserver.admin;

import com.example.imageserver.data.FolderRepository;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

@Component
public class AdminInterface {

    private final FolderRepository folderRepository;

    private final JFrame mainWindow;
    @SuppressWarnings("FieldCanBeLocal")
    private final JToolBar toolBar;
    private final FolderTable folderTable;
    private final JFileChooser folderChooser;

    public AdminInterface(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;

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

        folderTable = new FolderTable(this.folderRepository);
        JScrollPane textAreaScrollPane = new JScrollPane(folderTable);

        Dimension tableSize = folderTable.getPreferredSize();
        textAreaScrollPane.setPreferredSize(new Dimension(tableSize!=null ? tableSize.width+30 : 500,300));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        contentPane.add(toolBar, BorderLayout.PAGE_START);
        contentPane.add(textAreaScrollPane, BorderLayout.CENTER);

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        String key = folder.getName();
        while (folderRepository.containsKey(key)) {
            String[] msg = {
                    "A folder with name \"%s\" was already added.".formatted(key),
                    "Please enter modified name for this folder:"
            };
            Object result = JOptionPane.showInputDialog(mainWindow, msg, "title", JOptionPane.QUESTION_MESSAGE, null, null, key);
            if (result==null) return;
            key = result.toString();
        }
        folderRepository.add(key, folder);
        folderTable.updateTable();
    }

    @SuppressWarnings("SameParameterValue")
    private static JButton createButton(String text, boolean enabled, ActionListener al) {
        JButton comp = new JButton(text);
        comp.setEnabled(enabled);
        if (al!=null) comp.addActionListener(al);
        return comp;
    }
}
