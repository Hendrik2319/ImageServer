package net.schwarzbaer.java.tools.imageserver.admin;

import net.schwarzbaer.java.tools.imageserver.ImageServerApplication;
import net.schwarzbaer.java.tools.imageserver.data.Folder;
import net.schwarzbaer.java.tools.imageserver.data.FolderRepository;
import net.schwarzbaer.java.lib.copyimages.ImageComments;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

@Component
public class AdminInterface {

    public static final String PREFKEY_LAST_CONFIGURATION = "Last Configuration";
    private final FolderRepository folderRepository;

    private final Preferences preferences;
    private final JFrame mainWindow;
    @SuppressWarnings("FieldCanBeLocal")
    private final ToolBar toolBar;
    private final FolderTable folderTable;
    private final JFileChooser folderChooser;
    private final JFileChooser configChooser;
    private Folder selectedFolder;

    public AdminInterface(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;

        preferences = Preferences.userNodeForPackage(ImageServerApplication.class);

        folderChooser = new JFileChooser("./");
        folderChooser.setMultiSelectionEnabled(false);
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        configChooser = new JFileChooser("./");
        configChooser.setMultiSelectionEnabled(false);
        configChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        configChooser.setFileFilter(new ConfigFileFilter());

        mainWindow = new JFrame("Image Server - Admin Interface");

        toolBar = new ToolBar();

        selectedFolder = null;
        folderTable = new FolderTable(this.folderRepository);
        JScrollPane textAreaScrollPane = new JScrollPane(folderTable);
        folderTable.addSelectionListener(e -> {
            selectedFolder = folderTable.getSelectedRowItem();
            toolBar.updateAccess();
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

        initConfig();
    }

    private static class ConfigFileFilter extends FileFilter {

        public static final String EXT_IMGSRV = ".imgsrv";

        @Override public boolean accept(File file) {
            return file.isFile() && hasExtension(file);
        }

        static boolean hasExtension(File file) {
            return file.getName().toLowerCase().endsWith(EXT_IMGSRV);
        }

        static File addExtension(File file) {
	        return new File( file.getPath()+ EXT_IMGSRV);
        }

        @Override public String getDescription() {
            return "ImageServer Config File (*%s)".formatted(EXT_IMGSRV);
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private class ToolBar extends JToolBar {

        private final JButton btnAddFolder;
        private final JButton btnAddExampleFolder;
        private final JButton btnSetMetaDataFolder;
        private final JButton btnSetExampleMetaDataFolder;

        ToolBar() {
            setFloatable(false);

            add(createButton("Load Configuration", true, e->{
                if (configChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = configChooser.getSelectedFile();
                    loadConfig(selectedFile);
                }
            }));

            add(createButton("Save Current Configuration", true, e->{
                if (configChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = configChooser.getSelectedFile();
                    if (!ConfigFileFilter.hasExtension(selectedFile)) {
                        String[] message = {
                                "Selected file \"%s\"".formatted(selectedFile.getPath()),
                                "hasn't expected filename extension (%s)".formatted(ConfigFileFilter.EXT_IMGSRV),
                                "Do you want to add it?"
                        };
                        int result = JOptionPane.showConfirmDialog(
                                mainWindow, message, "Wrong Filename Extension",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
                        );

	                    switch (result) {
                            case JOptionPane.CANCEL_OPTION:
			                    return;
                            case JOptionPane.YES_OPTION:
                                selectedFile = ConfigFileFilter.addExtension(selectedFile);
                                break;
	                    }
                    }
                    saveConfig(selectedFile);
                }
            }));

            addSeparator();

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
                if (selectedFolder!=null && folderChooser.showOpenDialog(mainWindow)==JFileChooser.APPROVE_OPTION)
	                setImageComments(folderChooser.getSelectedFile());
            }));

            add(btnSetExampleMetaDataFolder = createButton("Set Example Folder for Meta Data", selectedFolder!=null, e->{
                File folder = new File("/Users/hendrik/Desktop/Tools/ImageComments");
                if (folder.isDirectory())
                    setImageComments(folder);
            }));
        }

        private void setImageComments(File folder) {
            AdminInterface.setImageComments(selectedFolder, folder.getPath());
            folderTable.updateColumn(FolderTable.FolderTableModel.ColumnID.Meta);
        }

        public void updateAccess() {
            btnSetMetaDataFolder.setEnabled(selectedFolder != null);
            btnSetExampleMetaDataFolder.setEnabled(selectedFolder != null);
        }
    }

    private static void setImageComments(Folder folder, String imageCommentsFilePath) {
        ImageComments imageComments = new ImageComments(imageCommentsFilePath);

        if (!imageComments.wasFileFound())
            folder.clearCommentsStorage("No data file found");
        else if (imageComments.isEmpty())
            folder.clearCommentsStorage("Data file is empty");
        else
            folder.setCommentsStorage(imageComments);
    }

    private void initConfig() {
        String lastConfigPath = preferences.get(PREFKEY_LAST_CONFIGURATION, null);
        if (lastConfigPath==null) return;

        File lastConfigFile = new File(lastConfigPath);
        if (!lastConfigFile.isFile()) return;

        String[] message = { "Found a saved configuration.", "Do you want to load it?" };
        int result = JOptionPane.showConfirmDialog(
                mainWindow, message, "Last Configuration",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION)
            loadConfig(lastConfigFile);
    }

    private void loadConfig(File file) {
        System.out.printf("Read configuration from file \"%s\" ...%n", file.getAbsolutePath());

        folderRepository.clear();

        try (Stream<String> lineStream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {

            List<String> lines = lineStream.toList();
            String valueStr, folderKey = null;
            Folder folder = null;

            for (String line : lines) {

                if ( (valueStr=getValue(line, "folderKey="))!=null ) {
                    folderKey = valueStr;
                    folder = null;
                }

                if ( (valueStr=getValue(line, "folder="))!=null && folderKey!=null) {
                    File folderFile = new File(valueStr);
                    if (!folderFile.isDirectory()) {
                        folderKey = null;
                        folder = null;
                    } else
                        folder = folderRepository.add(folderKey, folderFile);
                }

                if ( (valueStr=getValue(line, "comments="))!=null && folder!=null) {
                    setImageComments(folder, valueStr);
                }

            }

        } catch (IOException e) {
	        throw new RuntimeException(e);
        }

        System.out.println("... done");
    }

    private static String getValue(String line, String prefix) {
	    if (line != null && line.startsWith(prefix))
            return line.substring(prefix.length());
	    return null;
    }

    private void saveConfig(File file) {
	    System.out.printf("Write current configuration to file \"%s\" ...%n", file.getAbsolutePath());

        try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {

            List<Folder> folders = folderRepository.getAllFolders(false);
            for (Folder folder : folders) {
                out.printf("folderKey=%s%n", folder.getKey());
                out.printf("folder=%s%n", folder.getPath());
                if (folder.hasCommentsStorage())
                    out.printf("comments=%s%n", folder.getCommentsStoragePath());
            }

        } catch (IOException e) {
	        throw new RuntimeException(e);
        }

        preferences.put(PREFKEY_LAST_CONFIGURATION, file.getAbsolutePath());
        System.out.println("... done");
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
