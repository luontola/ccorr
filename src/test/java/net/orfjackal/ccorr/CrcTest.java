// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import org.junit.*;

/**
 * @author Esko Luontola
 */
public class CrcTest extends Assert {

    private static final byte[] DATA = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    @Test
    public void calculates_CRC32() {
        CRC crc32 = new CRC("CRC-32");
        crc32.update(DATA, 0, DATA.length);
        assertEquals("456CD746", crc32.getHexValue());
    }

    @Test
    public void calculates_MD5() {
        CRC md5 = new CRC("MD5");
        md5.update(DATA, 0, DATA.length);
        assertEquals("c56bd5480f6e5413cb62a0ad9666613a", md5.getHexValue());

    }

    @Test
    public void calculates_SHA1() {
        CRC sha1 = new CRC("SHA-1");
        sha1.update(DATA, 0, DATA.length);
        assertEquals("494179714a6cd627239dfededf2de9ef994caf03", sha1.getHexValue());
    }

    @Test
    public void when_data_contains_entirely_the_same_byte_then_the_byte_is_shown_in_hex() {
        CRC sha1 = new CRC("CRC-32");
        byte[] SPACES = {' ', ' ', ' ', ' ', ' ', ' ', ' '};
        sha1.update(SPACES, 0, SPACES.length);
        assertEquals("0x20", sha1.getHexValue());
    }
}
