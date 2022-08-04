/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com) - All Rights Reserved.

*
 
* Unauthorized copying of this file, via any medium is strictly prohibited

* Proprietary and confidential

* This file is part of OpenNote.

*

* OpenNote can not be copied and/or distributed without the express

* permission of the owner of this Software

*******************************************************/
package com.opennote.util;

/**
 *
 * @author shabman
 */
public class VersionManager {
    
    private static VersionManager manager;
    
    private VersionManager() { }
    
    public static VersionManager load() {
        if (manager == null) manager = new VersionManager();
        return manager;
    }
    
    public String getVersion() {
        return "1.0.1";
    }
}
