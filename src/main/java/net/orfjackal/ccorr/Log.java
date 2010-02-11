// Copyright © 2003-2006, 2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class used for logging debug information. The messages are printed to <code>System.out</code> with a timestamp.
 *
 * @author Esko Luontola
 */
public class Log {

    /**
     * The format of the timestamp.
     */
    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

    /**
     * Private constructor.
     */
    private Log() {
    }

    /**
     * Prints a timestamped line of text.
     *
     * @param s the text to be logged
     */
    public static void print(String s) {
        System.out.print("[" + df.format(new Date()) + "] ");
        System.out.println(s);
    }

    /**
     * Prints a timestamped line of text and an empty line.
     *
     * @param s the text to be logged
     */
    public static void println(String s) {
        Log.print(s);
        System.out.println();
    }

}