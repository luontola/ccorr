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

import fi.helsinki.cs.luontola.ccorr.*;

import java.io.*;
import java.util.*;

/**
 * For debugging purposes.
 */
public class Debug {
    
    /**
     * Where the test files are located.
     */
    private static final String PATH = "F:\\oht\\";
    
    public static void main(String[] args) throws Exception{
        
        System.out.println("\n\t*** CCorr Debugger ***\n\n");
        
        System.out.println("  0: Start program normally");
        System.out.println("  1: ChecksumFile");
        System.out.println("  2: Comparison");
        System.out.println("  3: createGoodCombination & writeFile & countChecksum");
        System.out.println("  4: writeFile index test");
        System.out.println("  5: createPossibleCombinations");
        System.out.println("  6: ObjectSaver");
        
        System.out.print("\nSelect test: ");
        
        int runTest = Lue.kluku();
//      int runTest = 6;

        System.out.println("\n\n================================================================================\n\n");
        
        if (runTest == 0) {
            // START
            
            Main.main(args);
            
            // END
        } else if (runTest == 1) {
            // START
            
            String[] alg = CRC.getSupportedAlgorithms();
            for (int i = 0; i < alg.length; i++) {
                System.out.println("Algorithm " + i + " = " + alg[i]);
            }
            System.out.println("\nUse which one? (give number)");
            String algorithm = alg[Lue.kluku()];
            
            ChecksumFile cf = newChecksumFile("dummy.file", 100 * 1000, algorithm);
            
            System.out.println("Press enter for checksum list.");
            Lue.rivi();
            
            System.out.println(cf);
            
            // END
        } else if (runTest == 2) {
            // START
            
            ChecksumFile file1 = newChecksumFile("dummy-bad1.file", 16*1024, "CRC-32");
            ChecksumFile file2 = newChecksumFile("dummy-bad2.file", 16*1024, "CRC-32");
            ChecksumFile file3 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
            
            Comparison c = new Comparison();
            
            c.addFile(file1);
            c.addFile(file2);
            c.addFile(file3);
            
            // testing how fast doCompare works - didn't take even a second with these files
//          ChecksumFile file4 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          ChecksumFile file5 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          ChecksumFile file6 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          ChecksumFile file7 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          ChecksumFile file8 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          ChecksumFile file9 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          ChecksumFile file10 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
//          c.addFile(file4);
//          c.addFile(file5);
//          c.addFile(file6);
//          c.addFile(file7);
//          c.addFile(file8);
//          c.addFile(file9);
//          c.addFile(file10);
            
            c.doCompare();
            
            // END
        } else if (runTest == 3) {
            // START
            
            ChecksumFile file1 = newChecksumFile("dummy-bad1.file", 16*1024, "CRC-32");
            ChecksumFile file2 = newChecksumFile("dummy-bad2.file", 16*1024, "CRC-32");
            ChecksumFile file3 = newChecksumFile("dummy-bad3.file", 16*1024, "CRC-32");
            ChecksumFile file4 = newChecksumFile("output3.file", 16*1024, "CRC-32");
            
            Comparison c = new Comparison();
            
            c.addFile(file1);
            c.addFile(file2);
            c.addFile(file3);
            c.addFile(file4);
            c.doCompare();
            
            c.setMark(0,0,Comparison.MARK_IS_GOOD);
            c.setMark(1,0,Comparison.MARK_IS_GOOD);
            c.setMark(2,0,Comparison.MARK_IS_GOOD);
            c.setMark(3,1,Comparison.MARK_IS_GOOD);
            c.setMark(4,1,Comparison.MARK_IS_GOOD);
            c.setMark(5,0,Comparison.MARK_IS_GOOD);
            c.setMark(6,0,Comparison.MARK_IS_GOOD);
            c.setMark(7,0,Comparison.MARK_IS_GOOD);
            c.setMark(8,2,Comparison.MARK_IS_GOOD);
            c.doCompare();
            
            FileCombination fc = c.createGoodCombination();
            System.out.println("Length "+ fc.getLength() +" bytes\n");
            
            File output = new File(PATH + "output3.file");
            fc.writeFile(output);
            fc.countChecksum("CRC-32");
            
            // END
        } else if (runTest == 4) {
            // START
            
            ChecksumFile file1 = newChecksumFile("hex00.file", 1024, "CRC-32");
            ChecksumFile file2 = newChecksumFile("hex20.file", 1024, "CRC-32");
            
            Comparison c = new Comparison();
            
            c.addFile(file1);
            c.addFile(file2);
            c.doCompare();
            
            c.setMark(0,0,Comparison.MARK_IS_GOOD);
            c.setMark(1,1,Comparison.MARK_IS_GOOD);
            c.setMark(2,0,Comparison.MARK_IS_GOOD);
            c.setMark(3,0,Comparison.MARK_IS_GOOD);
            c.setMark(4,0,Comparison.MARK_IS_GOOD);
            c.setMark(5,0,Comparison.MARK_IS_GOOD);
            c.setMark(6,0,Comparison.MARK_IS_GOOD);
            c.setMark(7,0,Comparison.MARK_IS_GOOD);
            c.setMark(8,0,Comparison.MARK_IS_GOOD);
            c.setMark(9,0,Comparison.MARK_IS_GOOD);
            c.doCompare();
            
            FileCombination fc = c.createGoodCombination();
            System.out.println();
            
            File output = new File(PATH + "output4.file");
            fc.writeFile(output);
            
            ChecksumFile file3 = newChecksumFile("output4.file", 1024, "CRC-32");
            c.addFile(file3);
            c.doCompare();
            
            // END
        } else if (runTest == 5) {
            // START
            
            ChecksumFile file1 = newChecksumFile("hex00.file", 1024, "CRC-32");
            ChecksumFile file2 = newChecksumFile("hex20.file", 1024, "CRC-32");
            
            Comparison c = new Comparison();
            
            c.addFile(file1);
            c.addFile(file2);
            c.doCompare();
            
            c.setMark(0,0,Comparison.MARK_IS_GOOD);
            c.setMark(1,0,Comparison.MARK_IS_UNSURE);
            c.setMark(1,1,Comparison.MARK_IS_UNSURE);
            c.setMark(2,0,Comparison.MARK_IS_GOOD);
            c.setMark(3,0,Comparison.MARK_IS_GOOD);
            c.setMark(4,0,Comparison.MARK_IS_GOOD);
            c.setMark(5,0,Comparison.MARK_IS_UNDEFINED);
            c.setMark(5,1,Comparison.MARK_IS_UNDEFINED);
            c.setMark(6,0,Comparison.MARK_IS_GOOD);
            c.setMark(7,0,Comparison.MARK_IS_GOOD);
            c.setMark(8,0,Comparison.MARK_IS_GOOD);
            c.setMark(9,1,Comparison.MARK_IS_GOOD);
            c.doCompare();
            
            FileCombination fc[] = c.createPossibleCombinations();
            System.out.println();
            
            for (int i = 0; i < fc.length; i++) {
                fc[i].writeFile(new File(PATH + "output5-"+ i +".file"));
            }
            
            ChecksumFile[] files = new ChecksumFile[fc.length];
            for (int i = 0; i < fc.length; i++) {
                files[i] = newChecksumFile("output5-"+ i +".file", 1024, "CRC-32");
                c.addFile(files[i]);
            }
            
            c.doCompare();
            FileCombination.countChecksums(fc, "CRC-32");
            
            
            // END
        } else if (runTest == 6) {
            // START
            
            ChecksumFile file1 = newChecksumFile("dummy-bad1.file", 16*1024, "CRC-32");
            ChecksumFile file2 = newChecksumFile("dummy-bad2.file", 16*1024, "CRC-32");
            
            Comparison c = new Comparison();
            
            c.addFile(file1);
            c.addFile(file2);
            
            c.setMark(0,0,Comparison.MARK_IS_GOOD);
            c.setMark(1,0,Comparison.MARK_IS_GOOD);
            c.setMark(2,1,Comparison.MARK_IS_BAD);
            c.doCompare();
            
            file1.saveToFile(new File(PATH + "output6-1.ccf"));
            file2.saveToFile(new File(PATH + "output6-2.ccf"));
            file1 = null;
            file2 = null;
            
            c = new Comparison();
            
            file1 = ChecksumFile.loadFromFile(new File(PATH + "output6-1.ccf"));
            file2 = ChecksumFile.loadFromFile(new File(PATH + "output6-2.ccf"));
            
            c.addFile(file1);
            c.addFile(file2);
            c.doCompare();
            
            System.out.println("\n======== part 2 ===========\n");
            
            c.setMark(0,0,Comparison.MARK_IS_GOOD);
            c.setMark(1,0,Comparison.MARK_IS_GOOD);
            c.setMark(2,1,Comparison.MARK_IS_BAD);
            c.setMark(3,0,Comparison.MARK_IS_GOOD);
            c.setMark(4,1,Comparison.MARK_IS_UNSURE);
            System.out.println(c +"\n");
            
            c.saveToFile(new File(PATH + "output6-3.ccp"));
            c = null;
            
            c = Comparison.loadFromFile(new File(PATH + "output6-3.ccp"));
            System.out.println(c);
            
            
            // END
        } else if (runTest == 7) {
            // START
            
            // END
        }
        
        System.out.println("\n\n================================================================================\n\n");
    }
    
    private static ChecksumFile newChecksumFile(String file, int partSize, String algorithm) {
        ChecksumFile cf = ChecksumFile.createChecksumFile(new File(PATH + file), partSize, algorithm);
        if (cf == null) {
            //System.exit(0);
        }
        return cf;
    }
}