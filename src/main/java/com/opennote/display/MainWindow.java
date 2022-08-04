/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.display;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import com.opennote.api.events.Event;

import com.opennote.components.MenuBuilder;
import com.opennote.components.ToolTipText;

import com.opennote.util.LoggerUnit;
import com.opennote.util.OperatingSys;
import com.opennote.util.VersionManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Credits:
 * Chanut from flaticon.com
 */


/**
 *
 * @author shabman
 * @version 1.0.1
 */
public final class MainWindow extends Event implements ActionListener {
    
    private static final LoggerUnit unit = new LoggerUnit("Main Window");
    private static final Logger LOGGER = LoggerUnit.getLogger();
    
    private static final VersionManager VERSION = VersionManager.load();
    private static final OperatingSys sys = OperatingSys.create();
    
    private static final Event event = Event.init();
    private static final Event pluginEventHandler = Event.init();
    
    private static int BOUND_X = 500;
    private static int BOUND_Y = 1027;
    
    private final String name;
    private final String[] iconButtonLocations = new String[] {
        "src/main/java/resources/icons/greenplus.png",
        "src/main/java/resources/icons/redx.png"
    };
    private final String[] fontLocations = new String[] {
        "src/main/java/resources/fonts/segoeuisl.ttf"
    };
    private String themeName;
    
    private final boolean fullscreen;
    private boolean hasBuilded = false;
    private boolean showDownloaderResult;

    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice[] gs = ge.getScreenDevices();
    
    private JFrame window;
    private JMenuBar parent;
    private JPanel menuPanel;
    private JButton addProjectBtn;
    private JButton removeProjectBtn;
    private JLabel recentsTitle;
    
    private Event pluginEvent;
    private final JSONArray jsonArray = new JSONArray();
    private JSONObject settings = new JSONObject();
    private final JSONParser jsonReader = new JSONParser();
    
    private FileReader reader = null;
    private FileWriter file = null;
      
    public MainWindow(String name) {
        this(name, false, true);
    }
    
    public MainWindow(String name, boolean fullscreen) {
        this.name = name;
        this.fullscreen = fullscreen;
    }
    
    public MainWindow(String name, boolean fullscreen, boolean showDownloaderResult) {
        this.name = name;
        this.fullscreen = fullscreen;
        this.showDownloaderResult = showDownloaderResult;
        this.read();
    }
    
    public static Font setStreamFont(String where){
        try {
            File input = new File(where);
            Font font = Font.createFont(Font.TRUETYPE_FONT, input);
            return font;
        } catch (FontFormatException | IOException e) {
            LOGGER.warning(e.getMessage());
            return null;
        }
    } 
    
    public void build() {
        if (!this.hasBuilded) {

            switch (this.themeName) {
                case "light" -> {
                    try {
                        FlatLightLaf.setup();
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    } catch (UnsupportedLookAndFeelException ex) {
                        LOGGER.warning(ex.getMessage());
                    }
                }
                case "dark" -> {
                    try {
                        FlatDarkLaf.setup();
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                    } catch (UnsupportedLookAndFeelException ex) {
                        LOGGER.warning(ex.getMessage());
                    }
                }
                case "darcula" -> {
                    try {
                        FlatDarculaLaf.setup();
                        UIManager.setLookAndFeel(new FlatDarculaLaf());
                    } catch (UnsupportedLookAndFeelException ex) {
                        LOGGER.warning(ex.getMessage());
                    }
                }
                case "intellij" -> {
                    try {
                        FlatIntelliJLaf.setup();
                        UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    } catch (UnsupportedLookAndFeelException ex) {
                        LOGGER.warning(ex.getMessage());
                    }
                }
                default -> {
                    try {
                        FlatLightLaf.setup();
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    } catch (UnsupportedLookAndFeelException ex) {
                        LOGGER.warning(ex.getMessage());
                    }
                }
            }
            
            this.settings = new JSONObject();
            GraphicsDevice gd = (gs.length > 1) ? gs[1] : gs[0];
            this.window = new JFrame(gd.getDefaultConfiguration());
            
            Font customJPaneFont = setStreamFont(this.fontLocations[0]);
            JLabel paneText = new JLabel("Failed to fetch update installer");
            paneText.setFont(customJPaneFont.deriveFont(14f));
            
            if (sys.determineOS().equals("mac")) {
                this.createMenuBar();
                this.window.setJMenuBar(this.parent);   
                UIDefaults uiDefaults = UIManager.getDefaults();
                uiDefaults.put("activeCaption", new javax.swing.plaf.ColorUIResource(new Color(0x123478)));
                uiDefaults.put("activeCaptionText", new javax.swing.plaf.ColorUIResource(new Color(0x123478)));

            }
            
            this.window.setTitle(this.name);
                        
            if (this.fullscreen) {
                this.window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                this.window.setBounds(0, 0, 420, 420);
            }  
            
            this.window.setLocationRelativeTo(this.window);
            this.window.setLayout(new BorderLayout());
            this.window.setResizable(false);
            
            this.hasBuilded = true;
            this.loadDropDown();
            
            this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.window.setVisible(true);
            
            event.add(this);
            
            if (this.showDownloaderResult) {
                JOptionPane.showMessageDialog(this.window, paneText, "OpenNote Updates", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            LOGGER.warning("Window has builded already!");
        }
        
        this.window.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                event.fireEvent("onPause");
            }
            
            @Override
            public void windowGainedFocus(WindowEvent e) {
                event.fireEvent("onRestart");
            }
        });
        
        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                write();
                event.fireEvent("onClose");
            }
        });
    }
    
    protected void loadDropDown() {
        if (this.hasBuilded) {
            this.menuPanel = new JPanel(new BorderLayout());
            this.menuPanel.setBackground(new Color(0xFFFFFF)); //0x123478
            this.menuPanel.setOpaque(true);
            this.window.add(this.menuPanel, BorderLayout.CENTER);
            this.topPanelControls(this.menuPanel);
            
            JPanel topTitleDisplayBar = new JPanel(new FlowLayout(FlowLayout.LEADING));
            Font customFont = setStreamFont(this.fontLocations[0]);
            Font sizedFont = customFont.deriveFont(14f);
            
            this.recentsTitle = new JLabel("Recent Projects Appear here");
            this.recentsTitle.setFont(sizedFont);
            this.menuPanel.add(topTitleDisplayBar);
            topTitleDisplayBar.add(this.recentsTitle);
            
//            JPanel listOfProjects = new JPanel();
//            listOfProjects.setLayout(new BoxLayout(listOfProjects, BoxLayout.Y_AXIS));
//            listOfProjects.setBackground(new Color(0x000000));
//            this.menuPanel.add(listOfProjects, BorderLayout.CENTER);
        }
     }
    
    protected void showRecentProjects() {
        // TODO: Read JSON file and display recent projects
    }
    
    protected void topPanelControls(JPanel parent) {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        controls.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        controls.setOpaque(true);
        controls.setBackground(new Color(0x123478)); // 0x123589
        parent.add(controls, BorderLayout.NORTH);
               
        // Buttons
        this.addProjectBtn = new JButton(this.resizeImage(this.iconButtonLocations[0], 25, 25)) {
            @Override
            public JToolTip createToolTip() {
                return new ToolTipText(this, new Color(0xFFFFFF));
            }
        };
        
        this.addProjectBtn.setToolTipText("New Project");
        this.addProjectBtn.setSize(25, 25);
        this.addProjectBtn.setOpaque(false);
        this.addProjectBtn.setContentAreaFilled(false);
        this.addProjectBtn.setBorderPainted(false);
        this.addProjectBtn.setFocusable(false);
        controls.add(addProjectBtn);
            
        this.addProjectBtn.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
            //fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".rtf", "rtf"));
            //fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".json", "json"));
            int result = fileChooser.showOpenDialog(this.window);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                this.onDispose();
                this.window.dispose();
                new Editor(selectedFile, this.themeName, 0, 0).build();
            }
        });
        
        this.removeProjectBtn = new JButton(this.resizeImage(this.iconButtonLocations[1], 25, 25)) {
            @Override
            public JToolTip createToolTip() {
                return new ToolTipText(this, new Color(0xFFFFFF));
            }
        };
        
        this.removeProjectBtn.setSize(25, 25);
        this.removeProjectBtn.setOpaque(false);
        this.removeProjectBtn.setContentAreaFilled(false);
        this.removeProjectBtn.setBorderPainted(false);
        this.removeProjectBtn.setToolTipText("Remove Project");
        this.removeProjectBtn.setFocusable(false);
        controls.add(removeProjectBtn);
    }
    
    protected void createMenuBar() {
        final MenuBuilder menu = new MenuBuilder(this.window, "Settings");
        this.parent = new JMenuBar();
        
        JMenu settingsMenu = menu.createParentMenu();
        JMenu themes = new JMenu("Themes");
        
        JMenuItem lightMode = new JMenuItem("Light Mode");
        JMenuItem darkMode = new JMenuItem("Dark Mode");
        JMenuItem darculaMode = new JMenuItem("Darcula Mode");
        JMenuItem intelliJMode = new JMenuItem("IntelliJ IDEA Mode");
        
        themes.add(lightMode);
        themes.add(darkMode);
        themes.add(darculaMode);
        themes.add(intelliJMode);
        
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem logWindow = new JMenuItem("Log Window");
        
        toolsMenu.add(logWindow);
        settingsMenu.add(themes);
        
        this.parent.add(settingsMenu);
        this.parent.add(toolsMenu);
        
        lightMode.addActionListener((ActionEvent e) -> {
            if (!this.themeName.equals("light")) {
                try {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (UnsupportedLookAndFeelException ex) {
                    LOGGER.warning(ex.getMessage());
                }
                this.themeName = "light";
                SwingUtilities.updateComponentTreeUI(window);
            }
        });
        
        darkMode.addActionListener((ActionEvent e) -> {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                LOGGER.warning(ex.getMessage());
            }
            this.themeName = "dark";
            SwingUtilities.updateComponentTreeUI(window);
        });
        
        darculaMode.addActionListener((ActionEvent e) -> {
            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                LOGGER.warning(ex.getMessage());
            }
            this.themeName = "darcula";
            SwingUtilities.updateComponentTreeUI(window);
        });
        
        intelliJMode.addActionListener((ActionEvent e) -> {
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                LOGGER.warning(ex.getMessage());
            }
            this.themeName = "intellij";
            SwingUtilities.updateComponentTreeUI(window);
        });
    }
    
    public ImageIcon resizeImage(String path, int width, int height) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(path));
            Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(dimg);
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
            return null;
        }
    }
    
    // TODO: Finish support for plugin events
    public void setPluginEventHandlerr(Event e) {
        this.pluginEvent = e;
        pluginEventHandler.add(event);
    }
    
    public Object getPluginEventHandler() {
        return pluginEventHandler;
    }
    
    public void triggerPluginEventHandler(Object...sender) {
        pluginEventHandler.fireEvent(sender);
    }
    
    @Override
    public void onPause() {
        LOGGER.info("Application Paused");
    }
    
    @Override
    public void onRestart() {
        LOGGER.info("Application resumed");
    }
    
    @Override
    public void onClose() {
        LOGGER.info("Application closed");
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    protected void onDispose() {
        this.write();
    }
    
    protected void onFired(String key, Object[] args) {
        this.setEditorConfig(key, args);
    }
    
    private void setEditorConfig(String key, Object[] args) {
        JSONObject editor = new JSONObject();
        JSONObject editorBounds = new JSONObject();
        
        editorBounds.put("boundX", args[0]);
        editorBounds.put("boundY", args[1]);
        
        editor.put(key, editorBounds);
        this.jsonArray.add(editor);
    }
    
    private void read() {
        try {
            this.reader = new FileReader("src/main/java/resources/Storage/Settings/settings.json");
            Object info = this.jsonReader.parse(this.reader);
            JSONArray themeConfig = (JSONArray) info;
            themeConfig.forEach(data -> this.parse((JSONObject) data, "theme"));
        } catch (IOException | ParseException ex) {
            LOGGER.warning(ex.getMessage());
        }
    }
    
    // FIX READ WHEN SWITCHING WINDOWS
    private void parse(JSONObject obj, String prefix) {
        String result = (String) obj.get(prefix);
        if (prefix.equalsIgnoreCase("theme")) {
            this.themeName = result;
        }
    }
    
    private void write() {
        try {
            this.settings.put("theme", this.themeName);
            this.jsonArray.add(this.settings);
            this.file = new FileWriter("src/main/java/resources/Storage/Settings/settings.json");
            this.file.write(this.jsonArray.toJSONString());
            this.file.flush();
        } catch (IOException ex) {
            LOGGER.warning(ex.getMessage());
        } finally {
            try { 
                this.file.close();
                this.file = null;
            } catch (IOException er) {
                LOGGER.warning(er.getMessage());
            }
        }
    }
}
