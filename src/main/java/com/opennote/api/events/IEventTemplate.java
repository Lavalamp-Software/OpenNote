/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.api.events;

/**
 *
 * @author shabman
 */
public interface IEventTemplate {
    
    public void add(Event e);
    
    public void remove(Event e);
    
    public void remove(int i);
    
}
