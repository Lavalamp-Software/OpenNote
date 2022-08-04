/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JToolTip;

/**
 *
 * @author shabman
 */
public final class ToolTipText extends JToolTip {
    
    private final JComponent comp;
    private final Color color;
    private final Color foreground;
    
    public ToolTipText(JComponent comp) {
        this(comp, null, null);
    }
    
    public ToolTipText(JComponent comp, Color color) {
        this(comp, color, null);
    }
    
    public static Font setStreamFont(String where){
        try {
            File input = new File(where);
            Font font = Font.createFont(Font.TRUETYPE_FONT, input);
            return font;
        } catch (FontFormatException | IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    } 
    
    public ToolTipText(JComponent comp, Color color, Color foreground) {
        super();      
        this.comp = comp;
        this.color = color;
        this.foreground = foreground;
        
        this.setComponent(this.comp);
        this.setBackground(this.color);
        this.setForeground(this.foreground);
        
        Font custom = setStreamFont("src/main/java/resources/fonts/segoeuisl.ttf");
        Font result = custom.deriveFont(11f);
        
        this.setFont(result);
    }
}
