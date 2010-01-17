// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A <code>FileFilter</code> for a <code>FileChooser</code>. Can be used to show only some file types, determined by
 * extension.
 *
 * @author Esko Luontola
 */
public class GeneralFileFilter extends FileFilter {

    /**
     * Extensions to be shown.
     */
    private String[] extensions;

    /**
     * File type desription for the extensions.
     */
    private String description;

    /**
     * Creates a new instance of this class.
     *
     * @param extensions  the file extensions that should be shown
     * @param description a description for the file type
     */
    public GeneralFileFilter(String[] extensions, String description) {
        if (extensions == null) {
            extensions = new String[0];
        }
        if (description == null) {
            description = "";
        }

        if (extensions.length > 0) {
            description += " (*." + extensions[0];
            for (int i = 1; i < extensions.length; i++) {
                description += ", *." + extensions[i];
            }
            description += ")";
        }

        this.extensions = extensions;
        this.description = description;
    }

    /**
     * Tests whether or not the specified abstract pathname should be included in a pathname list.
     *
     * @param pathname the abstract pathname to be tested
     * @return true if and only if pathname should be included
     */
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        }

        String extension = getExtension(pathname);
        if (extension != null) {
            for (String ext : this.extensions) {
                if (extension.equals(ext)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the extension of the given file.
     *
     * @param f the file which's extension is wanted
     * @return the characters after the last dot in the file name
     */
    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Returns the file type description.
     */
    public String getDescription() {
        return this.description;
    }
}