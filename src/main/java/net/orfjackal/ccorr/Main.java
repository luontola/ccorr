// Copyright © 2003-2006, 2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import net.orfjackal.ccorr.gui.MainWindow;

/**
 * Starts Corruption Corrector
 */
public class Main {

    // TODO: redirect stdout and stderr to file (CCorr.log, overwrite if exists)

    public static void main(String[] args) {
        Log.print("Corruption Corrector: Starting");
        MainWindow.main(args);
        Log.println("Corruption Corrector: Started");
    }
}
