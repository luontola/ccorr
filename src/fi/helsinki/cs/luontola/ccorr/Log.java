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

package fi.helsinki.cs.luontola.ccorr;

import java.text.*;
import java.util.*;

/**
 * A class used for logging debug information. The messages are printed to 
 * <code>System.out</code> with a timestamp.
 *
 * @author      Esko Luontola
 */
public class Log {
    
    /**
     * The format of the timestamp.
     */
    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * Private constructor.
     */
    private Log() {}
    
    /**
     * Prints a timestamped line of text.
     *
     * @param   s   the text to be logged
     */
    public static void print(String s) {
        System.out.print("["+ df.format(new Date()) +"] ");
        System.out.println(s);
    }
    
    /**
     * Prints a timestamped line of text and an empty line.
     *
     * @param   s   the text to be logged
     */
    public static void println(String s) {
        Log.print(s);
        System.out.println();
    }
    
}