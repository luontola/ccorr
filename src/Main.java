/* 
 * Copyright (C) 2003-2005 Esko Luontola, esko.luontola@cs.helsinki.fi
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

import fi.helsinki.cs.luontola.ccorr.*;
import fi.helsinki.cs.luontola.ccorr.gui.*;

/**
 * Starts Corruption Corrector
 */
public class Main {
    public static void main(String[] args){
        Log.print("Corruption Corrector: Starting");
        MainWindow.main(args);
        Log.println("Corruption Corrector: Started");
    }
}