// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr.gui;

import net.orfjackal.ccorr.Log;

import javax.swing.*;

/**
 * The common properties of a <code>JTabbedPane</code> item.
 *
 * @author Esko Luontola
 */
public abstract class TabPanel extends JPanel {

    /**
     * Removes this object from the JTabbedPane. Override this method if you need to do something before the tab is
     * removed or you want to cancel closing the tab.
     *
     * @return true if this.getParent() is a JTabbedPane, otherwise false.
     */
    public boolean close() {
        if (this.getParent() instanceof JTabbedPane) {
            JTabbedPane parent = (JTabbedPane) (this.getParent());
            parent.remove(this);
            return true;
        } else {
            Log.println("TabPanel.close(): Aborted, parent of " + this + " is " + this.getParent());
            return false;
        }
    }

}