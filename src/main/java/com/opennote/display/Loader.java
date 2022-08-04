/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.display;

import com.formdev.flatlaf.FlatLightLaf;
import com.opennote.api.events.Event;
import com.opennote.util.LoggerUnit;
import com.opennote.util.VersionManager;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author shabman
 */
public final class Loader extends Event {
    
    private static final int WIDTH = 350;
    private static final int HEIGHT = 250;
    private static final VersionManager version = VersionManager.load();
    
    private final String title;
    private final LoggerUnit unit = new LoggerUnit("Note.Launcher");
    private final Logger log = LoggerUnit.getLogger();
    private final Taskbar taskbar = Taskbar.getTaskbar();
    private final Event event = Event.init();
    private final boolean decorated;
    
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice[] gs = ge.getScreenDevices();
    
    private JFrame frame;
    private boolean hasBuilded = false;
    private JLayeredPane overlay;
    private ImageIcon icon;
    
    public Loader() {
        this(null, false);
    }
    
    public Loader(String title, boolean decorated) {
        this.title = title;
        this.decorated = decorated;
    }
    
    public JFrame getFrame() {
        return this.frame;
    }
    
    public String getJTitle() {
        return this.title;
    }
    
    public boolean isDecorated() {
        return this.decorated;
    }
    
    public void build() {
        assert this.frame != null;
        assert !this.hasBuilded;
        
        UIManager.put( "ProgressBar.arc", 900);
        
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            this.log.warning(e.getMessage());
        }
        
        GraphicsDevice gd = (gs.length > 1) ? gs[1] : gs[0];
        
        this.frame = new JFrame(gd.getDefaultConfiguration());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(WIDTH, HEIGHT);
        this.frame.setLayout(null);
        this.frame.getContentPane().setBackground(new Color(0x123456));
        
        this.overlay = new JLayeredPane();
        this.overlay.setBounds(0, 0, WIDTH, HEIGHT);
        
        final Image img = this.resizeLogo();
        
        try {
            this.taskbar.setIconImage(img);
        } catch (UnsupportedOperationException | SecurityException e) {
            this.log.warning(e.getMessage());
        }
        
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setBounds(0, 0, WIDTH, HEIGHT);
        
        this.overlay.add(imgLabel, 1);
        this.frame.add(overlay);
        this.hasBuilded = true;
        
    }
    
    @Override
    public void onReady() {
        log.info("System active");
    }
    
    public void display() {
        this.event.add(this);
        EventQueue.invokeLater(() -> this.event.fireEvent("onReady"));
        
        this.frame.setUndecorated(true);
        this.frame.setLocationRelativeTo(this.frame);
        this.frame.setVisible(true);
        
        this.render();
        this.log.info("Launcher started");
    }
    
    private void render() {
        this.frame.repaint();
        this.frame.revalidate();
    }
    
    protected static void render(JFrame frame) {
        frame.repaint();
        frame.validate();
    }
    
    public Image resizeLogo() {
        BufferedImage img;
        try {
            img = ImageIO.read(new File("src/main/java/resources/icons/opennote3.png"));
            Image dimg = img.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
            this.icon = new ImageIcon(dimg);
            return dimg;
        } catch (IOException e) {
            this.log.info(e.getMessage());
            return null;
        }
    }
    
    public void load() {        
        JProgressBar bar = new JProgressBar();
        bar.setForeground(new Color(0x00A3FF));
        bar.setValue(0);
        bar.setBounds(-5, 245, WIDTH + 10, 5);
        bar.setFocusable(false);
        bar.setMaximum(10);
        this.overlay.add(bar, 0);
        
        for (int i = 0; i < 10; i++) {
            try {
                bar.setValue(i);
                Thread.sleep(new Random().nextInt(5) * 100);
            } catch (InterruptedException e) {
                this.log.info(e.getMessage());
            }
        }
        this.frame.dispose();
        this.log.info("Main display active");
        MainWindow mainDisplay = new MainWindow("OpenNote " + version.getVersion());
        mainDisplay.build();
    }
}
