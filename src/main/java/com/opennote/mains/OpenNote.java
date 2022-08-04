/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.mains;

import com.opennote.display.Loader;
import com.opennote.util.OperatingSys;

/**
 *
 * @author shabman
 * @version 1.0.1
 */
public final class OpenNote {
  
    public static void main(String[] args) {
        OperatingSys system = OperatingSys.create();
        String os = system.determineOS();
        
        if (os.equals("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "OpenNote"); 
        }
        
        Loader loader = new Loader();
        loader.build();
        loader.display();
        loader.load();
    }
}
