// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import org.junit.*;

import java.io.*;

import static net.orfjackal.ccorr.Comparison.*;
import static net.orfjackal.ccorr.TestDataUtil.ALGORITHM;

/**
 * @author Esko Luontola
 */
@Ignore("Is very slow, about 3 min on Intel X25-M G2 80GB SSD")
public class LargeFilesTest extends Assert {

    private static final long MB = 1024 * 1024;
    private static final long BIG_PART_LENGTH = 10 * MB;

    private static final int DIFF_0 = 0;
    private static final int DIFF_1 = 1;

    private TestDataUtil util = new TestDataUtil();

    @Before
    public void initUtil() throws IOException {
        util.create();
    }

    @After
    public void disposeUtil() {
        util.dispose();
    }

    @Test
    public void files_larger_than_2GB_can_be_processed() throws IOException {
        long length = (long) Integer.MAX_VALUE + (100 * MB);

        log("Creating 2GB file 1/2");
        ChecksumFile cf1 = util.createChecksumFile(length, BIG_PART_LENGTH, ALGORITHM, 10);

        log("Creating 2GB file 2/2");
        ChecksumFile cf2 = util.createChecksumFile(length, BIG_PART_LENGTH, ALGORITHM, length - 10);

        Comparison c = new Comparison();
        c.addFile(cf1);
        c.addFile(cf2);
        c.doCompare();

        assertEquals(2, c.getFiles());
        assertEquals(2, c.getDifferences());

        c.setMark(DIFF_0, 0, MARK_IS_BAD);
        c.setMark(DIFF_0, 1, MARK_IS_GOOD);

        c.setMark(DIFF_1, 0, MARK_IS_GOOD);
        c.setMark(DIFF_1, 1, MARK_IS_BAD);

        log("Writing resulting 2GB file");
        File output = util.uniqueFile();
        c.createGoodCombination().writeFile(output);

        log("Checking resulting 2GB file");
        c.addFile(ChecksumFile.createChecksumFile(output, BIG_PART_LENGTH, ALGORITHM));
        c.doCompare();

        assertEquals(3, c.getFiles());
        assertEquals(2, c.getDifferences());
        assertEquals(MARK_IS_GOOD, c.getMark(DIFF_0, 2));
        assertEquals(MARK_IS_GOOD, c.getMark(DIFF_1, 2));

        log("Done");
    }

    private void log(String message) {
        Log.println(getClass().getName() + ": " + message);
    }
}
