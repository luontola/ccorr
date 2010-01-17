// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

//import java.beans.*;

import java.io.*;
import java.util.zip.*;

/**
 * Saves and loads objects to and from a file. The objects must to be serializable. The file format used is gzipped
 * {@link ObjectOutputStream ObjectOutputStream}.
 *
 * @author Esko Luontola
 */
public class ObjectSaver {

    /**
     * Private constructor.
     */
    private ObjectSaver() {
    }

    /**
     * Saves an object to a file.
     *
     * @param file the file in which to save
     * @param obj  the object to be saved
     * @return true if successful, otherwise false
     */
    public static boolean saveToFile(File file, Serializable obj) {
        if (file == null || obj == null) {
            Log.print("ObjectSaver.saveToFile: Aborted, null arguments");
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
            e.printStackTrace();
            Log.print("ObjectSaver.saveToFile: Aborted, " + e);
            return false;
        }

        Log.print("ObjectSaver.saveToFile: Done, wrote " + obj.getClass() + " to " + file);
        return true;
    }

    /**
     * Loads an object from a file.
     *
     * @param file the file from which to load
     * @return a new object that was loaded, or null if operation failed
     */
    public static Object loadFromFile(File file) {
        if (file == null) {
            Log.print("ObjectSaver.loadFromFile: Aborted, null arguments");
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
            e.printStackTrace();
            Log.print("ObjectSaver.loadFromFile: Aborted, " + e);
            return null;
        }

        Log.print("ObjectSaver.loadFromFile: Done, read " + result.getClass() + " from " + file);
        return result;
    }
}