/* 
 * Copyright (C) 2003-2005  Esko Luontola, http://ccorr.sourceforge.net
 *
 * This file is part of Corruption Corrector (CCorr).
 *
 * CCorr is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * CCorr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CCorr; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package fi.helsinki.cs.luontola.ccorr.gui;

import fi.helsinki.cs.luontola.ccorr.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The common properties of a <code>JTabbedPane</code> item.
 *
 * @author      Esko Luontola
 */
public abstract class TabPanel extends JPanel {
    
    /**
     * Removes this object from the JTabbedPane. Override this method if you
     * need to do something before the tab is removed or you want to cancel 
     * closing the tab.
     *
     * @return	true if this.getParent() is a JTabbedPane, otherwise false.
     */
    public boolean close() {
        if (this.getParent() instanceof JTabbedPane) {
            JTabbedPane parent = (JTabbedPane) (this.getParent());
            parent.remove(this);
            return true;
        } else {
            Log.println("TabPanel.close(): Aborted, parent of "+ this +" is "+ this.getParent());
            return false;
        }
    }
    
}