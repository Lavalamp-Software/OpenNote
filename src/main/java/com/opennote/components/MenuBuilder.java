/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.components;

import javax.swing.JFrame;
import javax.swing.JMenu;

/**
 *
 * @author shabman
 */


public class MenuBuilder {
    
    private final JFrame frameParent;
    private final String menuParentName;
    
    public MenuBuilder(JFrame frameParent, String menuParentName) {
        this.frameParent = frameParent;
        this.menuParentName = menuParentName;
    }
    
    public final JMenu createParentMenu() {
        JMenu menuParent = new JMenu(this.menuParentName);
        this.frameParent.add(menuParent);
        return menuParent;
    }
}
