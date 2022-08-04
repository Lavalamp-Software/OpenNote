/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.api.annotations;

/**
 *
 * @author shabman
 */
public class LockedMethodException extends Exception {
    
    public LockedMethodException(String message) {
        super(message);
    }
    
    public LockedMethodException(String message, Throwable t) {
        super(message, t);
    }
}
