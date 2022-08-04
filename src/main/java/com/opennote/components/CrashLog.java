/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.components;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import com.opennote.api.events.Event;
import com.opennote.util.LoggerUnit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author shabman
 */
public final class CrashLog extends Event {
    
    private static final Event event = Event.init();
    private static final LoggerUnit unit = new LoggerUnit("CrashLogger");
    private static final Logger LOGGER = LoggerUnit.getLogger();
    
    private final Object parentClass;
    private final JFrame frame = new JFrame();
    
    private JPanel backing;
    private JScrollPane scroller;
    private JTextArea text;
    
    private final String theme;
    private final String cause;
    
    public CrashLog() {
        this("light", "An unspecified exception occurred", null);
    }
    
    public CrashLog(String theme) {
        this(theme, null, null);
    }
    
    public CrashLog(String theme, String cause) {
        this(theme, cause, null);
    }
    
    public CrashLog(String theme, String cause, Object parentClass) {
        this.theme = theme;
        this.cause = cause;
        this.parentClass = parentClass;
    }
    
    public void createLogWindow(JFrame parent) {
        
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
        
        frame.setTitle("Crash Log");
        frame.setLocationRelativeTo(parent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.getContentPane().setLayout(new BorderLayout());
        
        this.backing = new JPanel();
        this.backing.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5, true));
        this.backing.setLayout(new BoxLayout(this.backing, BoxLayout.Y_AXIS));
        this.text = new JTextArea();
        this.text.setFocusable(false);
        this.text.setEnabled(false);
        this.scroller = new JScrollPane(this.text);
        this.backing.add(this.scroller);
        this.frame.add(this.backing, BorderLayout.CENTER);
        
        this.frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                event.fireEvent("onPause");
            }
            
            @Override
            public void windowGainedFocus(WindowEvent e) {
                event.fireEvent("onRestart");
            }
        });
        
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });
    }
    
    public void show() {
        this.frame.setVisible(true);
    }
    
    public String getTheme() {
        return this.theme;
    }
    
    public String getCause() {
        return this.cause;
    }
    
    public Object getParentClass() {
        return this.parentClass;
    }
    
    public JFrame getFrame() {
        return this.frame;
    }
    
    public JPanel getPanel() {
        return this.backing;
    }
    
    public JScrollPane getScroller() {
        return this.scroller;
    }
    
    public void iDump(String info) {
        this.text.append(info+"\n");
    }
    
    public void wDump(String warning) {
        this.text.append(warning+"\n");
    }
    
    public void crashDump() {
        this.text.append(this.cause+"\n");
    }
    
    public void crashDump(String error) {
        this.text.append(error+"\n");
    }
    
    @Override
    public void onReady() {
    }
    
    @Override
    public void onRestart() {
        
    }
    
    @Override
    public void onPause() {
        
    }
    
    @Override
    public void onClose() {

    }
}
