// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import java.io.*;

/**
 * @author Esko Luontola
 */
public class TestDataUtil {

    public static final String ALGORITHM = "CRC-32";
    public static final int PART_LENGTH = 1024;
    public static final int START_OFFSET_0 = 0;
    public static final int START_OFFSET_1 = PART_LENGTH;
    public static final int START_OFFSET_2 = PART_LENGTH * 2;
    public static final int END_OFFSET_0 = START_OFFSET_1 - 1;
    public static final int END_OFFSET_1 = START_OFFSET_2 - 1;

    private final TempDirectory temp = new TempDirectory();
    private int nextFileId = 1;

    public void create() {
        temp.create();
    }

    public void dispose() {
        temp.dispose();
    }

    public ChecksumFile createChecksumFile(int length) throws IOException {
        File file = createDummyFile(length);
        return ChecksumFile.createChecksumFile(file, PART_LENGTH, ALGORITHM);
    }

    public File createDummyFile(int length) throws IOException {
        File file = uniqueFile();
        writeDummyData(file, length);
        return file;
    }

    private static void writeDummyData(File file, int length) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        for (int i = 0; i < length; i++) {
            out.write(i);
        }
        out.close();
    }

    public File uniqueFile() {
        File file = new File(temp.getDirectory(), "file" + nextFileId + ".tmp");
        nextFileId++;
        return file;
    }
}
