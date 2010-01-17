/******************************************************************************
 *
 * Jacksum version 1.1.1 - checksum utility in Java
 * Copyright (C) 2001, 2002 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, 
 * All Rights Reserved, http://www.jonelo.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: jonelo@jonelo.de
 *
 * The algorithm is from CRC16.java (1.6 95/09/06 Chuck McManis)
 *
 *****************************************************************************/

/*
 * @(#)CRC16.java   1.6 95/09/06 Chuck McManis
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package jonelo.jacksum.algorithm;

/**
 * The CRC-16 class calculates a 16 bit cyclic redundancy check of a set
 * of bytes. This error detecting code is used to determine if bit rot
 * has occured in a byte stream.
 */

public class Crc16 extends AbstractChecksum {

    public Crc16() { super(); }

    /** update CRC with byte b */
    public void update(byte aByte) {
      int a, b;

      a = (int) aByte;
      for (int count = 7; count >=0; count--) {
          a = a << 1;
          b = (a >>> 8) & 1;
          if ((value & 0x8000) != 0) {
             value = ((value << 1) + b) ^ 0x1021;
          } else {
             value = (value << 1) + b;
          }
      }
      value = value & 0xffff;
      length++;
    }
    
    public String getHexValue()
    {
       String s = Service.hexformat(getValue(),4);
       return (uppercase ? s.toUpperCase() : s);       
    }        
    
}
