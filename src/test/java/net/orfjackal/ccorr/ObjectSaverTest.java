// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import org.junit.*;

import java.io.*;

/**
 * @author Esko Luontola
 */
public class ObjectSaverTest extends Assert {

    private final TempDirectory temp = new TempDirectory();
    private File file;

    @Before
    public void createFile() throws IOException {
        temp.create();
        file = new File(temp.getDirectory(), "file.tmp");
    }

    @After
    public void deleteFile() {
        temp.dispose();
    }

    @Test
    public void loads_and_saves_objects() {
        Comparison orig = new Comparison();
        orig.setName("foo");

        ObjectSaver.saveToFile(file, orig);

        assertTrue(file.length() > 0);

        Comparison loaded = (Comparison) ObjectSaver.loadFromFile(file);

        assertEquals(orig.getName(), loaded.getName());
        assertNotSame(orig, loaded);
    }
}
