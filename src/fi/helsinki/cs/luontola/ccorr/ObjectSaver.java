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

//import java.beans.*;
import java.io.*;
import java.util.zip.*;

/**
 * Saves and loads objects to and from a file. The objects must to be 
 * serializable. The file format used is gzipped 
 * {@link ObjectOutputStream ObjectOutputStream}.
 *
 * @version     1.00, 2003-02-06
 * @author      Esko Luontola
 */
public class ObjectSaver {
    
    /**
     * Private constructor.
     */
    private ObjectSaver() {}
    
    /**
     * Saves an object to a file.
     *
     * @param   file    the file in which to save
     * @param   obj     the object to be saved
     * @return  true if successful, otherwise false
     */
    public static boolean saveToFile(File file, Object obj) {
        if (file == null || obj == null) {
            Log.println("ObjectSaver.saveToFile: Aborted, null arguments");
            return false;
        }
        
        try {
//          XMLEncoder output = 
//              new XMLEncoder(
//                  new GZIPOutputStream(
//                      new BufferedOutputStream(
//                          new FileOutputStream(file)
//                      )
//                  )
//              );
            ObjectOutputStream output = 
                new ObjectOutputStream(
                    new GZIPOutputStream(
                        new BufferedOutputStream(
                            new FileOutputStream(file)
                        )
                    )
                );
            output.writeObject(obj);
            output.close();
        } catch (Exception e) {
            Log.println("ObjectSaver.saveToFile: Aborted, "+ e);
            return false;
        }
        
        Log.println("ObjectSaver.saveToFile: Done, wrote "+ obj.getClass() +" to "+ file);
        return true;
    }
    
    /**
     * Loads an object from a file.
     *
     * @param   file    the file from which to load
     * @return  a new object that was loaded, or null if operation failed
     */
    public static Object loadFromFile(File file) {
        if (file == null) {
            Log.println("ObjectSaver.loadFromFile: Aborted, null arguments");
            return null;
        }
        
        Object result;
        try {
//          XMLDecoder input = 
//              new XMLDecoder(
//                  new GZIPInputStream(
//                      new BufferedInputStream(
//                          new FileInputStream(file)
//                      )
//                  )
//              );
            ObjectInputStream input = 
                new ObjectInputStream(
                    new GZIPInputStream(
                        new BufferedInputStream(
                            new FileInputStream(file)
                        )
                    )
                );
            result = input.readObject();
            input.close();
        } catch (Exception e) {
            Log.println("ObjectSaver.loadFromFile: Aborted, "+ e);
            return null;
        }
        
        Log.println("ObjectSaver.loadFromFile: Done, read "+ result.getClass() +" from "+ file);
        return result;
    }
}