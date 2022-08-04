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
import com.opennote.components.CrashLog;
import com.opennote.components.ToolTipText;
import com.opennote.util.LoggerUnit;
import com.opennote.util.VersionManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolTip;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FilenameUtils;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Credits:
 * Freepik from flaticon.com
 * PixelPerfect
 */

/**
 *
 * @author shabman
 */
public final class Editor extends Event implements ActionListener {
    
    private static final LoggerUnit unit = new LoggerUnit("Editor");
    private static final Logger LOGGER = LoggerUnit.getLogger();
    
    private static final VersionManager version = VersionManager.load();
    private static final Event event = Event.init();
    
    private static final int ICON_WIDTH = 25;
    private static final int ICON_HEIGHT = 25;
    
    private final File file;
    private final String theme;
    
    private final int boundX;
    private final int boundY;
   
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice[] gs = ge.getScreenDevices();
    
    private final String[] fontLocations = new String[] {
        "src/main/java/resources/fonts/segoeuisl.ttf"
    };
    private final String[] topPanelIcons = new String[] {
        "src/main/java/resources/icons/editorIcons/diskette.png",
        "src/main/java/resources/icons/editorIcons/undo.png",
        "src/main/java/resources/icons/editorIcons/redo.png",
        "src/main/java/resources/icons/editorIcons/trash.png",
        "src/main/java/resources/icons/editorIcons/caution.png"
    };
    
    private boolean canGC = true;
    
    protected static JFrame frame;
    
    private javax.swing.Timer timerUpdate;
    private JPanel headerPanel;
    private JPanel dataProgressPanel;
    private JPanel sidePanelTree;
    private JTabbedPane tabs;
    private JScrollPane scroller;
    private JSplitPane paneSplitter;
    
    private RSyntaxTextArea codeHighLights;
    private FileWriter write;
    
    private MainWindow display;
    private JSONObject json;
    private final JSONParser jsonParse = new JSONParser();
    
    private CrashLog log;
    
    
    public Editor(File file, String theme, int boundX, int boundY) {
        this.file = file;
        this.theme = theme;
        this.boundX = boundX;
        this.boundY = boundY;
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
    
    protected void build() {
        
        switch (this.theme) {
            case "light" -> {
                try {
                    FlatLightLaf.setup();
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (UnsupportedLookAndFeelException e) {
                    LOGGER.warning(e.getMessage());
                }
            }
            case "dark" -> {
                try {
                    FlatDarkLaf.setup();
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch (UnsupportedLookAndFeelException e) {
                    LOGGER.warning(e.getMessage());
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
        
        event.add(this);
        
        GraphicsDevice gd = (gs.length > 1) ? gs[1] : gs[0];
        frame = new JFrame(gd.getDefaultConfiguration());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(frame);
        frame.setTitle("OpenNote Editor " + version.getVersion());
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        event.fireEvent("onReady");
        
        this.log = new CrashLog(theme);
        this.log.createLogWindow(frame);
        
        
        String extName = FilenameUtils.getExtension(file.getAbsolutePath());
        this.loadFile(file, extName);
        
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                event.fireEvent("onPause");
            }
            
            @Override
            public void windowGainedFocus(WindowEvent e) {
                event.fireEvent("onRestart");
            }
        });
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                timerUpdate.stop();
                display = new MainWindow("OpenNote " + version.getVersion(), false, false);
                display.build();
                fireBackData();
            }
        });
    }
    
    /*
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.add(filler);
        return panel;
    }
    */
    
    private void changeStyleViaThemeXml(RSyntaxTextArea textArea, String themeType) {
        try {
           Theme editorTheme = Theme.load(getClass().getResourceAsStream(
                 "/org/fife/ui/rsyntaxtextarea/themes/" + themeType + ".xml"));
           editorTheme.apply(textArea);
        } catch (IOException ex) { // Never happens
           LOGGER.warning(ex.getMessage());
        }
    }
    
    private void loadFile(File file, String fileType) {
        /*
        this.tabs = new JTabbedPane();
        var tab1 = this.makeTextPanel(null);
        this.tabs.addTab(file.getName(), tab1);
        this.tabs.setMnemonicAt(0, KeyEvent.VK_1);
        
        frame.add(this.tabs, BorderLayout.SOUTH);
        */

        if (fileType.equalsIgnoreCase("txt")) {
            fileType = "text/plain";
        }
        else if (fileType.equalsIgnoreCase("py")) {
            fileType = SyntaxConstants.SYNTAX_STYLE_PYTHON;
        }
        else if (fileType.equalsIgnoreCase("js")) {
            fileType = SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
        }
        else if (fileType.equalsIgnoreCase("ts")) {
            fileType = SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT;
        }
        else {
            fileType = "text/"+fileType;
        }

        //Font customTextFont = setStreamFont(this.fontLocations[0]);
        
        codeHighLights = new RSyntaxTextArea(20, 60);
        //SyntaxConstants
        codeHighLights.setSyntaxEditingStyle(fileType);
        codeHighLights.setCodeFoldingEnabled(true);
        codeHighLights.setFont(new Font(null, 0, 15));
        codeHighLights.setAnimateBracketMatching(true);

        //codeHighLights.setFont(codeHighLights.getFont().deriveFont(30f));
        //codeHighLights.revalidate();
               
        if (this.theme.equalsIgnoreCase("dark") || this.theme.equalsIgnoreCase("darcula")) {
            this.changeStyleViaThemeXml(codeHighLights, "dark");
            /*
            codeHighLights.setSelectionColor(Color.WHITE);
            codeHighLights.setCurrentLineHighlightColor(Color.WHITE);
            codeHighLights.setBackground(this.determineTheme(this.sidePanelTree, 1f));
            codeHighLights.revalidate();   
            */
        }/* else {
            this.changeStyleViaThemeXml(codeHighLights, "eclipse");
        }*/
        
        RTextScrollPane panes = new RTextScrollPane(codeHighLights);
        frame.add(panes, BorderLayout.CENTER);
        
        Scanner scanFile = null;
        
        try {
            scanFile = new Scanner(new File(file.getAbsolutePath()));
            while (scanFile.hasNext()) {
                codeHighLights.append(scanFile.nextLine()+"\n");
            }
        } catch (FileNotFoundException ex) {
            this.log.crashDump(ex.getMessage());
        } finally {
            assert scanFile != null;
            scanFile.close();
        }
        
        this.log.iDump("File loader finished loading");
    }
    
    private void createSidePanel() {     
        this.sidePanelTree = new JPanel();
        this.sidePanelTree.setLayout(new BorderLayout());
        this.sidePanelTree.setBackground(this.determineTheme(this.sidePanelTree, 0.8f));
        this.sidePanelTree.setBorder(BorderFactory.createLineBorder(this.determineTheme(this.sidePanelTree, 0.7f), 4));
        this.sidePanelTree.setPreferredSize(new Dimension(400, 100));
        this.sidePanelTree.setOpaque(true);
        this.sidePanelTree.add(new JLabel());   
        
        this.headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.headerPanel.setPreferredSize(new Dimension(this.sidePanelTree.getPreferredSize().width, 35));
        this.headerPanel.setBackground(this.determineTheme(this.sidePanelTree, 0.9f));
        
        Font customJPaneFont = setStreamFont(this.fontLocations[0]);
        JLabel headerText = new JLabel(this.file.getAbsolutePath());
        headerText.setFont(customJPaneFont.deriveFont(15f));
        headerText.setMinimumSize(new Dimension(0, 10));
        this.headerPanel.add(headerText);
        this.sidePanelTree.add(this.headerPanel, BorderLayout.NORTH);
        
        
        JPanel support = new JPanel();
        support.setSize(new Dimension(450, 200));
        support.setEnabled(false);
        support.setOpaque(true);
        
        this.paneSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.sidePanelTree, this.scroller);
        this.paneSplitter.setOneTouchExpandable(true);
        this.paneSplitter.setContinuousLayout(true);
        this.paneSplitter.resetToPreferredSizes();
        
        frame.add(this.paneSplitter, BorderLayout.WEST);
        this.buildTopPanelTools();
    }
    
    private void buildTopPanelTools() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topPanel.setPreferredSize(new Dimension(frame.getSize().width, 35));
        topPanel.setBackground(this.determineTheme(this.sidePanelTree, 0.7f));
        
        JButton saveIcon = new JButton() {
            @Override
            public JToolTip createToolTip() {
               return new ToolTipText(this, determineTheme(sidePanelTree, 0.6f));
            }
        };
        saveIcon.setIcon(this.resizeImage(this.topPanelIcons[0], ICON_WIDTH, ICON_HEIGHT));
        saveIcon.setToolTipText("Save File");
        saveIcon.setBorderPainted(false);
        saveIcon.setOpaque(false);
        saveIcon.setFocusable(false);
        saveIcon.addActionListener((ActionEvent e) -> {
            try {
                saveIcon.setEnabled(false);
                write = new FileWriter(file);
                write.write(this.codeHighLights.getText());
                write.flush();
            } catch (IOException ex) {
                this.log.crashDump(ex.getMessage());
            } finally {
                assert write != null;
                try {
                    write.close();
                    this.log.iDump("Written information to file successfully");
                } catch (IOException ex) {
                    this.log.crashDump(ex.getMessage());
                }
            }
            saveIcon.setEnabled(true);
        });
        topPanel.add(saveIcon);
        
        JButton undoIcon = new JButton() {
            @Override
            public JToolTip createToolTip() {
                return new ToolTipText(this, determineTheme(sidePanelTree, 0.6f));
            }
        };
        undoIcon.setIcon(this.resizeImage(this.topPanelIcons[1], ICON_WIDTH, ICON_HEIGHT));
        undoIcon.setToolTipText("Undo Changes");
        undoIcon.setBorderPainted(false);
        undoIcon.setOpaque(false);
        undoIcon.setFocusable(false);
        undoIcon.addActionListener((ActionEvent e) -> {
            this.codeHighLights.undoLastAction();
        });
        topPanel.add(undoIcon);
        
        JButton redoIcon = new JButton() {
            @Override
            public JToolTip createToolTip() {
               return new ToolTipText(this, determineTheme(sidePanelTree, 0.6f));
            }
        };
        redoIcon.setIcon(this.resizeImage(this.topPanelIcons[2], ICON_WIDTH, ICON_HEIGHT));
        redoIcon.setToolTipText("Redo Changes");
        redoIcon.setBorderPainted(false);
        redoIcon.setOpaque(false);
        redoIcon.setFocusable(false);
        redoIcon.addActionListener((ActionEvent e) -> {
            this.codeHighLights.redoLastAction();
        });
        topPanel.add(redoIcon);
        
        JButton gcIcon = new JButton() {
            @Override
            public JToolTip createToolTip() {
                return new ToolTipText(this, determineTheme(sidePanelTree, 0.6f));
            }
        };
        gcIcon.setIcon(this.resizeImage(this.topPanelIcons[3], ICON_WIDTH, ICON_HEIGHT));
        gcIcon.setToolTipText("Discard edits and clear memory");
        gcIcon.setBorderPainted(false);
        gcIcon.setOpaque(false);
        gcIcon.setFocusable(false);
        gcIcon.addActionListener((ActionEvent e) -> {
            if (this.canGC) {
                System.gc();
                this.codeHighLights.discardAllEdits();
                this.canGC = false;
                gcIcon.setEnabled(canGC);
                Thread sleeper = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(60000);
                            canGC = true;
                            gcIcon.setEnabled(canGC);
                        } catch (InterruptedException ex) {
                            log.crashDump(ex.getMessage());
                        }
                    }
                };
                sleeper.start();
            }
        });
        topPanel.add(gcIcon);
        
        JLabel jvmMem = new JLabel() {
            @Override
            public JToolTip createToolTip() {
                return new ToolTipText(this, determineTheme(sidePanelTree, 0.6f));
            }
        };
        jvmMem.setBorder(BorderFactory.createLineBorder(new Color(0x123478), 3));
        jvmMem.setToolTipText("Current memory / Total memory allocated");
        topPanel.add(jvmMem);
        
        ActionListener updateJvmText = (ActionEvent e) -> {
            jvmMem.setText("Mem: "+ Runtime.getRuntime().freeMemory() / 1048576 + "/" + Runtime.getRuntime().totalMemory() / 1048576 + "MB");
        };
        
        timerUpdate = new javax.swing.Timer(1000, updateJvmText);
        timerUpdate.setRepeats(true);
        timerUpdate.start();
        
        frame.add(topPanel, BorderLayout.NORTH);
        
        this.createBottomPanel();
    }
    
    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setPreferredSize(new Dimension(frame.getSize().width, 25));
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        // TODO: COPY JETBRAINS ERROR LOG
        JButton err = new JButton() {
            @Override
            public JToolTip createToolTip() {
                return new ToolTipText(this, determineTheme(bottomPanel, 0.6f));
            }
        };
        err.setOpaque(false);
        err.setFocusable(false);
        err.setToolTipText("Click to see crash log");
        err.setIcon((this.resizeImage(this.topPanelIcons[4], ICON_WIDTH - 10, ICON_HEIGHT - 10)));
        err.addActionListener((ActionEvent e) -> {
            this.log.show();
        });
        bottomPanel.add(err);
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
    
    private Color determineTheme(JComponent comp, float factor) {
        Color compBg = comp.getBackground();
        
        int red = Math.round(compBg.getRed() * factor);
        int green = Math.round(compBg.getGreen() * factor);
        int blue = Math.round(compBg.getBlue() * factor);
        
        return new Color(red, green, blue);
    }
    
    private void fireBackData() {
        display.onFired("editorLeftPanelDimensions", new Object[] {
            this.sidePanelTree.getSize().width,
            this.sidePanelTree.getSize().height
        });
    }
        
    @Override
    public void onReady() {
        this.createSidePanel();
    }
    
    @Override
    public void onRestart() {
        this.log.iDump("Application restarted");
    }
    
    @Override
    public void onPause() {
        this.log.iDump("Application paused");
    }
    
    @Override
    public void onClose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }  
}
