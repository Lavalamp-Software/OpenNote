/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.util;

import java.util.logging.Logger;

/**
 *
 * @author mustafamalik
 */
public class LoggerUnit {
    
    private static Logger log;
    
    public LoggerUnit(String name) {
        log = Logger.getLogger(name);
    }
    
    public static Logger getLogger() {
        return log;
    }
}
