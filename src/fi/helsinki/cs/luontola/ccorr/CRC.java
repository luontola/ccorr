/* 
 * Copyright (C) 2003 Esko Luontola, esko.luontola@cs.helsinki.fi
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

import java.util.*;
import jonelo.jacksum.algorithm.*;

/**
 * A class that can be used to compute the checksum of a data stream
 * with many different algorithms.
 *
 * @version     1.00, 2003-02-06
 * @author      Esko Luontola
 */
public class CRC {
    
    /**
     * All algorithms known by this class.
     */
    private static final String[] ALGORITHM_NAMES = { 
        "CRC-32",       "MD5",          "Adler32",      "BSD sum", 
        "POSIX cksum",  "CRC-16",       "SHA-1",        "Unix System V"
        };
    
    /**
     * The checksum this <code>CRC</code> object uses.
     */
    private AbstractChecksum crc;
    
    /**
     * Name of the algorithm used.
     */
    private String algorithmName;
    
    /**
     * Creates a new <code>CRC</code> that uses the given algorithm.
     * If the algorithmn name is not recognized, CRC-32 will be used.
     *
     * @param   algorithmn  Name of the algorithmn as defined in 
     *                      <code>getSupportedAlgorithms</code>.
     * @see     #getSupportedAlgorithms()
     */
    public CRC(String algorithm) {
        if (algorithm == null) {
            algorithm = "CRC-32";
        }
        
        if (algorithm.equals("CRC-32")) {
            this.crc = new Crc32();
        } else if (algorithm.equals("MD5")) {
            this.crc = new MD("MD5");
        } else if (algorithm.equals("Adler32")) {
            this.crc = new Adler32();
        } else if (algorithm.equals("BSD sum")) {
            this.crc = new Sum();
        } else if (algorithm.equals("POSIX cksum")) {
            this.crc = new Cksum();
        } else if (algorithm.equals("CRC-16")) {    // CRC-16 is for some reason very slow
            this.crc = new Crc16();
        } else if (algorithm.equals("SHA-1")) {
            this.crc = new MD("SHA-1");
        } else if (algorithm.equals("Unix System V")) {
            this.crc = new SumSysV();
        } else {
            this.crc = new Crc32();
            algorithm = "CRC-32";
        }
        
        this.algorithmName = algorithm;
    }
    
    /**
     * Resets the checksum to initial value.
     */
    public void reset() {
        this.crc.reset();
    }
    
    /**
     * Updates the checksum with specified array of bytes.
     *
     * @param   bytes   the byte array to update the checksum with
     * @param   offset  the start offset of the data
     * @param   length  the number of bytes to use for the update
     */
    public void update(byte[] bytes, int offset, int length) {
        this.crc.update(bytes, offset, length);
    }
    
    /**
     * Returns the checksum value in HEX format.
     *
     * @return  the current checksum value
     */
    public String getHexValue() {
        return this.crc.getHexValue();
    }
    
    /**
     * Returns an array containing the names of the algorithms known by this class.
     *
     * @return  the algorithm names in an array
     * @see     #CRC(String)
     */
    public static String[] getSupportedAlgorithms() {
        String[] result = new String[ALGORITHM_NAMES.length];
        for (int i = 0; i < ALGORITHM_NAMES.length; i++) {
            result[i] = ALGORITHM_NAMES[i];
        }
        Arrays.sort(result);
        return result;
    }
    
    /**
     * Returns the name of the algorithmn used by this <code>CRC</code> object.
     *
     * @return  the name of the algorithm.
     */
    public String getAlgorithm() {
        return this.algorithmName;
    }
}