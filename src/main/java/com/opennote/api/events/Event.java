/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.api.events;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mustafamalik
 */
public class Event extends EventRegistery {
    
    protected List<Event> events = new ArrayList<>();
    
    private static Event obj; 
    
    protected Event() {
        
    }
    
    public static Event init() {
        if (obj == null) {
            obj = new Event();
        }
        return obj;
    }
    
    @Override
    public void add(Event e) {
        this.events.add(e);
    }
    
    @Override
    public void remove(Event o) {
        this.events.remove(o);
    }
    
    @Override
    public void remove(int i) {
        this.events.remove(i);
    }
    
    // TODO: Add checks for duplicate events
    
    public void fireEvent(Object...data) {
        if (this.events.size() > 0 && data.length > 0) {
            int index = 0;
            for (Object d : data) {
                if (data[index].equals("onReady")) {
                    for (int i = 0; i < this.events.size(); i++) {
                        this.events.get(i).onReady();
                    }
                }
                if (data[index].equals("onPause")) {
                    for (int i = 0; i < this.events.size(); i++) {
                       this.events.get(i).onPause();
                    } 
                }
                if (data[index].equals("onRestart")) {
                    for (int i = 0; i < this.events.size(); i++) {
                        this.events.get(i).onRestart();
                    } 
                }
                if (data[index].equals("onClose")) {
                    for (int i = 0; i < this.events.size(); i++) {
                        this.events.get(i).onClose();
                    }
                }
            }
            index = 0;
        }
    }
    
    protected List<Event> getEvents() {
        return this.events;
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onRestart() {

    }
}
