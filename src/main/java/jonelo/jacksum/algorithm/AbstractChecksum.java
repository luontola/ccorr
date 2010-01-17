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
import java.util.zip.Checksum;
import java.io.*;

abstract public class AbstractChecksum implements Checksum {

    protected long value;
    protected long length;
    protected String separator;
    protected String filename;
    protected boolean hex;  // output in hex?
    protected boolean uppercase; // hex in uppercase?
    
    
    public AbstractChecksum() {
        value=0;
        length=0;
        separator="\t";
        filename=null;
        hex=false;
        uppercase=true;
    }

    public void update(byte[] bytes, int offset, int length) {
       for (int i=offset; i < length; i++)
       {
         update(bytes[i]);
       }           
    }
    
    public void reset() {
      value = 0;
      length = 0;
    }
    
    public void update(byte[] bytes) {
       update(bytes,0,bytes.length);
    }

    public void update(int b) {    
       length++;
    }
    
    public void update(byte b) {
        update((int)(b & 0xFF));
    }
    
    public long getValue() {
       return value;
    }
    
    public long getLength() {
       return length;
    }
    
    public void setSeparator(String separator) {
      this.separator=separator;
    }
    
    public String getSeparator() {
        return separator;
    }
    
    public String toString() {       
       return (hex ? getHexValue() : Long.toString(getValue())) + 
       separator+length+separator+filename;
    }      
    
    public void setFilename(String filename)
    {
        this.filename=filename;
    }
    
    public String getFilename()
    {
      return filename;
    }
    
    public void setHex(boolean bool)
    {
      this.hex=bool;
    }
    
    public void setUpperCase(boolean uppercase)
    {
       this.uppercase=uppercase;
    }
    
    public String getHexValue()
    {
       String s = Long.toHexString(getValue());
       return (uppercase ? s.toUpperCase() : s);
    }

    public void readFile(String filename) throws IOException {
        this.filename=filename;
        BufferedInputStream bis = 
        new BufferedInputStream(new FileInputStream(filename));
        reset();            
        int len = 0;
        byte[] buffer = new byte[8192];
        while ((len = bis.read(buffer)) > -1) {
           update(buffer,0,len);    
        }
    }
}
