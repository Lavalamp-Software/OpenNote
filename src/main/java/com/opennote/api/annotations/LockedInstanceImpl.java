/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.api.annotations;

import java.lang.reflect.Method;

/**
 *
 * @author shabman
 */
public class LockedInstanceImpl {
    
    protected LockedInstanceImpl() { }
    
    private void lockMethod(Object o) throws LockedMethodException {
        Class<?> clazz = o.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(LockedInstance.class)) {
                // TODO: Lock the method
                if (!clazz.getPackageName().startsWith("com.opennote")) {
                    throw new LockedMethodException("Unauthorised access");
                }
                method.setAccessible(false);
            }
        }
    }
}
