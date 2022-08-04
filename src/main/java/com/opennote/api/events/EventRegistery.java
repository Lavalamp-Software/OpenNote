/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.api.events;

/**
 *
 * @author shabman
 */
public abstract class EventRegistery implements IEventTemplate {
    
    public abstract void onReady();
    
    public abstract void onPause();
    
    public abstract void onClose();
    
    public abstract void onRestart();
    
}
