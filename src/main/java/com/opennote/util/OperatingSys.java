/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.util;

/**
 *
 * @author shabman
 * @note This will not support Linux Machines
 */
public class OperatingSys {
    
    private static OperatingSys sys = null;
    
    protected OperatingSys() { }
    
    public static OperatingSys create() {
        if (sys == null) {
            sys = new OperatingSys();
        }
        return sys;
    }
    
    private String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }
    
    public String determineOS() {
        if (this.getOS().startsWith("mac")) {
            return "mac";
        } else if (this.getOS().startsWith("windows")) {
            return "windows";
        } else {
            return "unknown"; // We do not support linux :)
        }
    }
}
