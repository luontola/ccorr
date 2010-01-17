// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import org.junit.*;

import javax.swing.*;
import java.io.*;

import static org.mockito.Mockito.*;

/**
 * @author Esko Luontola
 */
public class ChecksumFileTest extends Assert {

    private static final String ALGORITHM = "CRC-32";
    private static final int PART_LENGTH = 1024;
    private static final int START_OFFSET_0 = 0;
    private static final int START_OFFSET_1 = PART_LENGTH;
    private static final int START_OFFSET_2 = PART_LENGTH * 2;
    private static final int END_OFFSET_0 = START_OFFSET_1 - 1;
    private static final int END_OFFSET_1 = START_OFFSET_2 - 1;

    private final TempDirectory temp = new TempDirectory();
    private int nextFileId = 1;

    @Before
    public void createFile() throws IOException {
        temp.create();
    }

    @After
    public void deleteFile() {
        temp.dispose();
    }

    private ChecksumFile createChecksumFile(int length) throws IOException {
        File file = createDummyFile(length);
        return ChecksumFile.createChecksumFile(file, PART_LENGTH, ALGORITHM);
    }

    private File createDummyFile(int length) throws IOException {
        File file = uniqueFile();
        writeDummyData(file, length);
        return file;
    }

    private File uniqueFile() {
        File file = new File(temp.getDirectory(), "file" + nextFileId + ".tmp");
        nextFileId++;
        return file;
    }

    private static void writeDummyData(File file, int length) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        for (int i = 0; i < length; i++) {
            out.write(i);
        }
        out.close();
    }

    @Test
    public void calculates_the_checksum_for_all_parts() throws IOException {
        int parts = 2;
        ChecksumFile cf = createChecksumFile(PART_LENGTH * parts);

        assertEquals(parts, cf.getParts());

        assertEquals("B70B4C26", cf.getChecksum(0));
        assertEquals(START_OFFSET_0, cf.getStartOffset(0));
        assertEquals(END_OFFSET_0, cf.getEndOffset(0));

        assertEquals("B70B4C26", cf.getChecksum(1));
        assertEquals(START_OFFSET_1, cf.getStartOffset(1));
        assertEquals(END_OFFSET_1, cf.getEndOffset(1));
    }

    @Test
    public void last_part_can_be_shorter() throws IOException {
        int parts = 2;
        int shorter = 100;
        ChecksumFile cf = createChecksumFile(PART_LENGTH * parts - shorter);

        assertEquals(parts, cf.getParts());
        assertEquals("B70B4C26", cf.getChecksum(0));
        assertEquals(START_OFFSET_0, cf.getStartOffset(0));
        assertEquals(END_OFFSET_0, cf.getEndOffset(0));

        assertEquals("294D3872", cf.getChecksum(1));
        assertEquals(START_OFFSET_1, cf.getStartOffset(1));
        assertEquals(END_OFFSET_1 - shorter, cf.getEndOffset(1));
    }

    @Test
    public void trying_to_get_a_part_from_too_high_index_returns_an_error_value() throws IOException {
        int parts = 2;
        int tooHighIndex = 2;
        ChecksumFile cf = createChecksumFile(PART_LENGTH * parts);

        assertTrue(cf.getParts() >= parts);
        assertEquals(null, cf.getChecksum(tooHighIndex));
        assertEquals(-1, cf.getStartOffset(tooHighIndex));
        assertEquals(-1, cf.getEndOffset(tooHighIndex));
    }

    @Test
    public void calculating_the_checksums_can_be_monitored_by_a_progress_bar() throws IOException {
        ProgressMonitor monitor = spy(new ProgressMonitor(null, null, null, 0, 0));

        Settings.setProgressMonitor(monitor);
        createChecksumFile(PART_LENGTH * 4);

        verify(monitor).setMinimum(0);
        verify(monitor).setMaximum(100);

        verify(monitor).setProgress(0);
        verify(monitor).setNote("Completed 0%");
        verify(monitor).setProgress(25);
        verify(monitor).setNote("Completed 25%");
        verify(monitor).setProgress(50);
        verify(monitor).setNote("Completed 50%");
        verify(monitor).setProgress(75);
        verify(monitor).setNote("Completed 75%");
        verify(monitor).setProgress(100);
    }

    @Test
    public void can_be_saved_to_a_file_and_loaded_from_it() throws IOException {
        ChecksumFile original = createChecksumFile(PART_LENGTH * 2);

        File tmp = uniqueFile();
        original.saveToFile(tmp);
        ChecksumFile loaded = ChecksumFile.loadFromFile(tmp);

        assertEquals(original.getParts(), loaded.getParts());
        assertEquals(original.getChecksum(0), loaded.getChecksum(0));
        assertEquals(original.getAlgorithm(), loaded.getAlgorithm());
        assertEquals(original.getPartLength(), loaded.getPartLength());
        assertEquals(original.getSourceFile(), loaded.getSourceFile());
        assertEquals(original.getSourceFileLength(), loaded.getSourceFileLength());
    }

    @Test
    public void the_source_file_can_be_relocated_if_the_file_size_is_the_same() throws IOException {
        int length = PART_LENGTH * 2;
        ChecksumFile cf = createChecksumFile(length);
        File moved = createDummyFile(length);

        cf.setSourceFile(moved);

        assertEquals(moved, cf.getSourceFile());
    }

    @Test
    public void the_source_file_can_not_be_relocated_if_the_file_size_is_not_the_same() throws IOException {
        int length = PART_LENGTH * 2;
        ChecksumFile cf = createChecksumFile(length);
        File original = cf.getSourceFile();
        File moved = createDummyFile(length - 1);

        cf.setSourceFile(moved);

        assertEquals(original, cf.getSourceFile());
    }
}
