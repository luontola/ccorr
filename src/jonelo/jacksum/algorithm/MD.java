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
 *****************************************************************************/


package jonelo.jacksum.algorithm;
import java.security.*;
public class MD extends AbstractChecksum {

    private MessageDigest md = null;    
    
    /** Creates new MD */
    public MD(String arg)
    { 
      // value=0; we don't use value, we use md
      length=0;        
      filename=null;      
      separator=" ";
      try {
        md = MessageDigest.getInstance(arg);
      } catch (NoSuchAlgorithmException e) {};
      uppercase=false;
    }
    
    public void reset()
    {
       md.reset();        
       length=0;
    }
    
    public void update(byte[] buffer, int offset, int len)
    {
        md.update(buffer,offset,len);
        length+=len;
    }
    
    public String toString() {
       return getHexValue()+separator+getFilename();
    }
    
    public String getHexValue()
    {
       String s = Service.format(md.digest());
       return (uppercase ? s.toUpperCase() : s);       
    }

}
