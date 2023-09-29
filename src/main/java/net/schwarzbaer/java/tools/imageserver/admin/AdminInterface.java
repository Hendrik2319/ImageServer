package net.schwarzbaer.java.tools.imageserver.admin;

import net.schwarzbaer.java.tools.imageserver.data.Folder;
import net.schwarzbaer.java.tools.imageserver.data.FolderRepository;
import net.schwarzbaer.java.lib.copyimages.ImageComments;
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
    private final ToolBar toolBar;
    private final FolderTable folderTable;
    private final JFileChooser folderChooser;
    private Folder selectedFolder;

    public AdminInterface(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;

        folderChooser = new JFileChooser("./");
        folderChooser.setMultiSelectionEnabled(false);
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        mainWindow = new JFrame("Image Server - Admin Interface");

        toolBar = new ToolBar();

        selectedFolder = null;
        folderTable = new FolderTable(this.folderRepository);
        JScrollPane textAreaScrollPane = new JScrollPane(folderTable);
        folderTable.addSelectionListener(e -> {
            selectedFolder = folderTable.getSelectedRowItem();
            toolBar.btnSetMetaDataFolder.setEnabled(selectedFolder != null);
        });

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

    @SuppressWarnings("FieldCanBeLocal")
    private class ToolBar extends JToolBar {

        private final JButton btnAddFolder;
        private final JButton btnAddExampleFolder;
        private final JButton btnSetMetaDataFolder;

        ToolBar() {
            setFloatable(false);
            add(btnAddFolder = createButton("Add Folder", true, e->{
                if (folderChooser.showOpenDialog(mainWindow)==JFileChooser.APPROVE_OPTION)
                    addFolder(folderChooser.getSelectedFile());
            }));
            add(btnAddExampleFolder = createButton("Add Example Folder", true, e->{
                File folder = new File("/Users/hendrik/Pictures/Hintergrund");
                if (folder.isDirectory())
                    addFolder(folder);
            }));

            addSeparator();

            add(btnSetMetaDataFolder = createButton("Set Folder for Meta Data", selectedFolder!=null, e->{
                if (selectedFolder!=null && folderChooser.showOpenDialog(mainWindow)==JFileChooser.APPROVE_OPTION) {
                    File folder = folderChooser.getSelectedFile();
                    ImageComments imageComments = new ImageComments(folder.getPath());

                    if (!imageComments.wasFileFound())
                        selectedFolder.clearCommentsStorage("No data file found");
                    else if (imageComments.isEmpty())
                        selectedFolder.clearCommentsStorage("Data file is empty");
                    else
                        selectedFolder.setCommentsStorage(imageComments);
                }
            }));
        }
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
