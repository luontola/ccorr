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

import java.io.*;
import java.util.*;

/**
 * This class is used to represent a CCorr Comparison Project. 
 * <code>Comparison</code> is the central class of Corruption Corrector and it 
 * is used to compare <code>ChecksumFile</code> objects, mark corrupt/uncorrupt 
 * parts and create <code>FileCombination</code> objects. The files to be 
 * compared need to be first added to the <code>Comparison</code>, after which
 * they are compared with the <code>doCompare</code> method. 
 * <code>doCompare</code> needs to be run after adding or removing files, 
 * because otherwise the data is not up to date and most of the methods will 
 * refuse to work.
 *
 * @version     1.01, 2003-02-13
 * @author      Esko Luontola
 */
public class Comparison implements Serializable {
    
    /**
     * Constant used by <code>setMark</code> and <code>getMark</code> 
     * to indicate an undefined mark.
     */
    public static final int MARK_IS_UNDEFINED = 0;
    
    /**
     * Constant used by <code>setMark</code> and <code>getMark</code> 
     * to indicate a good mark.
     */
    public static final int MARK_IS_GOOD = 1;
    
    /**
     * Constant used by <code>setMark</code> and <code>getMark</code> 
     * to indicate a bad mark.
     */
    public static final int MARK_IS_BAD = 2;
    
    /**
     * Constant used by <code>setMark</code> and <code>getMark</code> 
     * to indicate an unsure mark.
     */
    public static final int MARK_IS_UNSURE = 3;
    
    /**
     * The file extension used by CCorr Comparison Project.
     */
    public static final String FILE_EXTENSION = "ccp";
    
    /**
     * The the file type name of CCorr Comparison Project.
     */
    public static final String FILE_TYPE = "CCorr Comparison Project";
    
    /**
     * The <code>ChecksumFile</code> objects that are being compared.
     */
    private ChecksumFile[] files;
    
    /**
     * Used to store the mark information.
     */
    private ComparisonItem[][] items;
    
    /**
     * Used to store the similarity between the compared files.
     */
    private double[][] similarity;
    
    /**
     * Indicates in the comparison data needs to be updated.
     *
     * @see     #doCompare()
     */
    private boolean needsUpdating;
    
    /**
     * Comments connected to this <code>Comparison</code>.
     */
    private String comments;
    
    /**
     * The file to which this <code>Comparison</code> was saved.
     */
    private File savedAsFile;
    
    /**
     * Creates a new empty <code>Comparison</code>.
     */
    public Comparison() {
        this.files = new ChecksumFile[0];
        this.items = new ComparisonItem[0][0];
        this.similarity = new double[0][0];
        this.needsUpdating = false;
        this.comments = "";
    }
    
    /**
     * Compares the <code>ChecksumFile</code> objects and updates all
     * the comparison data.
     */
    public void doCompare() {
        Vector partsThatDiffer = new Vector();
        int[][] numberOfDifferences = new int[files.length][files.length];
        Vector filesToBeMirrored = new Vector();
        
        if (files.length > 1) {
            
            // compare all files to eachother to find similar files
            for (int i = 0; i < (files.length - 1); i++) {
                for (int j = (i + 1); j < files.length; j++) {
                    
                    // one file being shorter does not necessarily make it different
                    int shortest = files[i].getParts();
                    if (files[j].getParts() < shortest) {
                        shortest = files[j].getParts();
                    }
                    
                    for (int part = 0; part < shortest; part++) {
                        if (files[i].getChecksum(part) != null && files[j].getChecksum(part) != null    // takes short files in account
                                && !files[i].getChecksum(part).equals(files[j].getChecksum(part))) {
                            
                            // difference found between files i and j (i is always lower index than j)
                            numberOfDifferences[i][j]++;
                            
                            // update our part list
                            Integer tmp = new Integer(part);
                            if (!partsThatDiffer.contains(tmp)) {
                                partsThatDiffer.add(tmp);
                            }
                        }
                    }
                }
            }
            
            // clean up the differing parts information gathered earlier
            int[] partsThatDiffer2 = new int[partsThatDiffer.size()];
            for (int i = 0; i < partsThatDiffer2.length; i++) {
                partsThatDiffer2[i] = ((Integer) (partsThatDiffer.get(i))).intValue();
            }
            Arrays.sort(partsThatDiffer2);
            
            // store gathered comparison data to new arrays
            ComparisonItem[][] newItems = new ComparisonItem[partsThatDiffer2.length][files.length];
            for (int i = 0; i < newItems.length; i++) {
                for (int j = 0; j < newItems[i].length; j++) {
                    newItems[i][j] = new ComparisonItem(partsThatDiffer2[i], files[j]);
                }
            }
            
            if (items.length > 0 && newItems.length > 0){
                
                // copy old markers to new items
                for (int newFile = 0; newFile < newItems[0].length; newFile++) {
                    for (int oldFile = 0; oldFile < items[0].length; oldFile++) {
                        
                        // find the same old files from the new array
                        if (newItems[0][newFile].getFile() == items[0][oldFile].getFile()) {
                            int jStartIndex = 0;
                            filesToBeMirrored.add(new Integer(newFile));
                            
                            // proceed with moving markers if the files, parts and checksums are the same
                            for (int i = 0; i < newItems.length; i++) {
                                for (int j = jStartIndex; j < items.length; j++) {
                                    
                                    if (newItems[i][newFile].getFile() == items[j][oldFile].getFile()
                                            && newItems[i][newFile].getPart() == items[j][oldFile].getPart()
                                            && newItems[i][newFile].getChecksum() == items[j][oldFile].getChecksum()) {
                                        newItems[i][newFile].setMark(items[j][oldFile].getMark());
                                        
                                        // we don't need to go through all the indexes again
                                        jStartIndex = j + 1;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            items = newItems;
            
            // process and save data for similarity
            similarity = new double[files.length][files.length];
            for (int i = 0; i < numberOfDifferences.length; i++) {          // each file is also compared to itself
                for (int j = i; j < numberOfDifferences[i].length; j++) {
                    int shortest = files[i].getParts();
                    if (files[j].getParts() < shortest) {
                        shortest = files[j].getParts();
                    }
                    double d = 1.0 - ((1.0 * numberOfDifferences[i][j]) / shortest);
                    similarity[i][j] = d;
                    similarity[j][i] = d;
                }
            }
            
        } else if (files.length == 1) {
            items = new ComparisonItem[0][0];
            similarity = new double[1][1];
            similarity[0][0] = 1.0;
        } else if (files.length == 0) {
            items = new ComparisonItem[0][0];
            similarity = new double[0][0];
        }
        
        needsUpdating = false;
        
        // mirror old file's markers in new array (in case files were added to this comparison)
        if (Settings.isMarkMirroringEnabled()) {
            for (int i = 0; i < filesToBeMirrored.size(); i++) {
                int file = ((Integer) (filesToBeMirrored.elementAt(i))).intValue();
                for (int j = 0; j < this.items.length; j++) {
                    this.mirrorMark(j, file);
                }
            }
        }
        
        if (this.getDifferences() > 100) {
            Log.println("doCompare:\n[over 100 differences, output hidden]");
        } else {
            Log.println("doCompare:\n"+ this.toString());
        }
    }
    
    /**
     * Returns the number of parts the have differences between the compared files.
     *
     * @return  the number of differences, or -1 if needs updating
     */
    public int getDifferences() {
        if (needsUpdating) {
            return -1;
        } else {
            return this.items.length;
        }
    }
    
    /**
     * Returns the number of <code>ChecksumFile</code>s in this <code>Comparison</code>.
     *
     * @return  the number of files
     */
    public int getFiles() {
        return this.files.length;
    }
    
    /**
     * Returns the part related to the given difference.
     *
     * @param   difference  the index of the difference
     * @return  the index of the part, or -1 if parameter invalid
     */
    public int getPart(int difference) {
        if (difference < 0 || difference >= this.items.length) {
            return -1;
        } else {
            return this.items[difference][0].getPart();
        }
    }
    
    /**
     * Returns the similarity between two compared files. Similarity is 
     * indicated by a double between 0.0 and 1.0; the greater the more similar.
     *
     * @param   file1   the index of the first file
     * @param   file2   the index of the second file
     * @return  similarity between the two files
     */
    public double getSimilarity(int file1, int file2) {
        if (this.needsUpdating
                || file1 < 0    
                || file1 >= this.similarity.length  
                || file2 < 0    
                || file2 >= this.similarity.length) {       // similarity is always a square array
            return -1;
        } else {
            return this.similarity[file1][file2];
        }
    }
    
    /**
     * Used to check that the given index is not out of bounds and that this 
     * <code>Comparison</code> does not need updating.
     *
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     */
    private boolean isGoodIndex(int difference, int file) {
        if (this.needsUpdating
                || difference < 0   
                || difference >= this.items.length
                || file < 0 
                || file >= this.items[difference].length) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Returns the <code>ComparisonItem</code> in the given index.
     *
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     * @return  reference to the item
     */
    public ComparisonItem getItem(int difference, int file) {
        if (this.isGoodIndex(difference, file)) {
            return this.items[difference][file];
        } else {
            return null;
        }
    }
    
    /**
     * Returns the checksum in the given index.
     * 
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     * @return  the checksum, or "" if the requested item does not exist
     */
    public String getChecksum(int difference, int file) {
        if (this.isGoodIndex(difference, file)) {
            return this.items[difference][file].getChecksum();
        } else {
            return "";
        }
    }
    
    /**
     * Returns the start offset of the given difference.
     *
     * @param   difference  the index of the difference
     * @return	the biggest index of the part's first byte in all files
     */
    public long getStartOffset(int difference) {
    	if (this.isGoodIndex(difference, 0)) {
	    	long offset = -1;
	    	for (int i = 0; i < this.getFiles(); i++) {
	    		if (this.items[difference][i].getStartOffset() > offset) {
	    			offset = this.items[difference][i].getStartOffset();
			    }
		    }
		    return offset;
	    } else {
	    	return -1;
	    }
    }
    
    /**
     * Returns the start offset of the given difference.
     *
     * @param   difference  the index of the difference
     * @return	the biggest index of the part's last byte in all files
     */
    public long getEndOffset(int difference) {
    	if (this.isGoodIndex(difference, 0)) {
	    	long offset = -1;
	    	for (int i = 0; i < this.getFiles(); i++) {
	    		if (this.items[difference][i].getEndOffset() > offset) {
	    			offset = this.items[difference][i].getEndOffset();
			    }
		    }
		    return offset;
	    } else {
	    	return -1;
	    }
    }
    
    /**
     * Sets the mark in the given index. If {@link Settings#isMarkMirroringEnabled() 
     * isMarkMirroringEnabled} returns true, the mark is mirrored.
     * 
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     * @param   mark        the mark, which can be MARK_IS_UNDEFINED, MARK_IS_GOOD, 
     *          MARK_IS_BAD, MARK_IS_UNSURE, or any integer
     * @see     #mirrorMark(int, int)
     */
    public void setMark(int difference, int file, int mark) {
        if (this.isGoodIndex(difference, file)) {
            this.items[difference][file].setMark(mark);
            if (Settings.isMarkMirroringEnabled()) {
                this.mirrorMark(difference, file);
            }
        }
    }
    
    /**
     * Returns the mark in the given index.
     * 
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     * @return  the current mark, or -1 if the requested item does not exist
     */
    public int getMark(int difference, int file) {
        if (this.isGoodIndex(difference, file)) {
            return this.items[difference][file].getMark();
        } else {
            return -1;
        }
    }
    
    /**
     * Changes to the next mark in the given index.
     * 
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     * @return  the new mark that was set
     * @see     ComparisonItem#nextMark()
     */
    public int nextMark(int difference, int file) {
        if (this.isGoodIndex(difference, file)) {
            int result = this.items[difference][file].nextMark();
            if (Settings.isMarkMirroringEnabled()) {
                this.mirrorMark(difference, file);
            }
            return result;
        } else {
            return -1;
        }
    }
    
    /**
     * Mirrors the mark in the given index. All items that have the same 
     * checksum and part as the given index, will get the same mark.
     * 
     * @param   difference  the index of the difference
     * @param   file        the index of the file
     * @see     Settings#setMarkMirroringEnabled(boolean)
     * @see     Settings#isMarkMirroringEnabled()
     */
    public void mirrorMark(int difference, int file) {
        if (this.isGoodIndex(difference, file)) {
            String crc = this.items[difference][file].getChecksum();
            int mark = this.items[difference][file].getMark();
            for (int i = 0; i < this.items[difference].length; i++) {
                if (crc.equals(this.items[difference][i].getChecksum())) {
                    this.items[difference][i].setMark(mark);
                }
            }
        }
    }
    
    /**
     * Sets the comments for this <code>Comparison</code>.
     *
     * @param   comments    the comments, or null to empty them
     */
    public void setComments(String comments) {
        if (comments != null) {
            this.comments = comments;
        } else {
            this.comments = "";
        }
    }
    
    /**
     * Returns the comments of this <code>Comparison</code>.
     *
     * @return  the comments
     */
    public String getComments() {
        return this.comments;
    }
    
    /**
     * Returns the <code>ChecksumFile</code> in the given index.
     *
     * @param   file    the index of the file
     * @return  the requested <code>ChecksumFile</code>, 
     *          or null if parameter invalid
     */
    public ChecksumFile getFile(int file) {
        if (file < 0 || file >= this.files.length) {
            return null;
        } else {
            return this.files[file];
        }
    }
    
    /**
     * Adds a <code>ChecksumFile</code> the this <code>Comparison</code>. 
     * The <code>ChecksumFile</code> given as parameter must have the same part 
     * size and algorithm as all the other <code>ChecksumFile</code>s that are
     * part of this <code>Comparison</code>, and the same object is not accepted 
     * twise. If the requirements are not met, nothing is done. After adding 
     * files {@link #doCompare() doCompare} must be run.
     *
     * @param   file    the <code>ChecksumFile</code> to be added
     */
    public void addFile(ChecksumFile file) {
        if (file != null) {
            
            // no duplicates wanted, must have same part size and algorithm
            for (int i = 0; i < this.files.length; i++) {
                if (file == this.files[i]
                        || file.getPartLength() != this.files[i].getPartLength()
                        || !file.getAlgorithm().equals(this.files[i].getAlgorithm())) {
                    return;
                }
            }
            
            // resize files array to fit the new file
            ChecksumFile[] newArray = new ChecksumFile[this.files.length + 1];
            for (int i = 0; i < this.files.length; i++) {
                newArray[i] = this.files[i];
            }
            newArray[newArray.length - 1] = file;
            this.files = newArray;
            this.needsUpdating = true;      // somebody should run doCompare()
        }
    }
    
    /**
     * Removes the <code>ChecksumFile</code> in the given index from this 
     * <code>Comparison</code>. After removing files 
     * {@link #doCompare() doCompare} must be run or most of this 
     * <code>Comparison</code>'s methods will refuse to work.
     *
     * @param   file    the index of the file
     */
    public void removeFile(int file) {
        if (file >= 0 && file < this.files.length) {
            this.removeFile(this.files[file]);
        }
    }
    
    /**
     * Removes the given <code>ChecksumFile</code> object from this 
     * <code>Comparison</code>. After removing files 
     * {@link #doCompare() doCompare} must be run or most of this 
     * <code>Comparison</code>'s methods will refuse to work.
     *
     * @param   file    a reference to the file
     */
    public void removeFile(ChecksumFile file) {
        
        // find the right index...
        int removeFromIndex = -1;
        for (int i = 0; i < this.files.length; i++) {
            if (file == this.files[i]) {
                removeFromIndex = i;
                break;
            }
        }
        
        // ...and remove the file
        if (removeFromIndex >= 0) {
            ChecksumFile[] newArray = new ChecksumFile[this.files.length - 1];
            for (int i = removeFromIndex; i < (this.files.length - 1); i++) {
                this.files[i] = this.files[i + 1];
            }
            for (int i = 0; i < newArray.length; i++) {
                newArray[i] = this.files[i];
            }
            this.files = newArray;
            this.needsUpdating = true;      // somebody should run doCompare()
        }
    }
    
    /**
     * Creates a <code>FileCombination</code> from the parts marked as good. 
     * If in one difference index there is no item with MARK_IS_GOOD as the 
     * marker, a good combination will not be possible.
     *
     * @return  a good combination, or null if it is not possible 
     *          or if updating is needed or if there are no files
     */
    public FileCombination createGoodCombination() {
        Log.print("createGoodCombination: Start");
        if (this.needsUpdating) {
            Log.print("createGoodCombination: Aborted, needsUpdating == true");
            return null;
        }
        if (this.items.length == 0) {
            Log.print("createGoodCombination: Aborted, no parts available");
            return null;
        }
        
        
        FileCombination fc = new FileCombination();
        long nextStart = 0;
        
item:   for (int i = 0; i < this.items.length; i++) {
            for (int j = 0; j < this.files.length; j++) {
                if (this.items[i][j].getMark() == MARK_IS_GOOD) {
                    File file = this.items[i][j].getFile().getSourceFile();
                    long start = nextStart;
                    long end;
                    
                    if (i == (this.items.length - 1)) {
                        // last part
                        end = this.items[i][j].getFile().getSourceFileLength();
                    } else {
                        end = this.items[i][j].getEndOffset();
                        nextStart = this.items[i][j].getEndOffset() + 1;
                    }
                    
                    fc.addItem(file, start, end);
                    continue item;
                } else if (j == (this.files.length - 1)) {
                    // no good parts found, abort
                    fc = null;
                    break item;
                }
            }
        }
        
        Log.print("createGoodCombination: Done");
        return fc;
    }
    
    /**
     * Returns the number of all <code>FileCombination</code>s that are not 
     * marked as bad.
     * If in one difference index there is no item with MARK_IS_GOOD as the 
     * marker, marks MARK_IS_UNDEFINED and MARK_IS_UNSURE will be looked for. 
     * If in one difference index all items are marked as MARK_IS_BAD, 
     * no combinations are possible.
     *
     * @return  the number of the combinations, or -1 if updating is needed 
     *          or if there are no files
     */
    public int getPossibleCombinations() {
        if (this.needsUpdating) {
            Log.print("getPossibleCombinations = -1 (needsUpdating == true)");
            return -1;
        }
        if (this.items.length == 0) {
            Log.print("getPossibleCombinations = -1 (no parts available)");
            return -1;
        }
        
        int result = 1;
        
item:   for (int item = 0; item < this.items.length; item++) {
            int inThisItem = 0;
            boolean isPossible = false;
            
            for (int i = 0; i < this.files.length; i++) {
                if (this.items[item][i].getMark() == MARK_IS_GOOD) {
                    continue item;
                } else if (this.items[item][i].getMark() == MARK_IS_UNSURE
                        || this.items[item][i].getMark() == MARK_IS_UNDEFINED) {
                    inThisItem++;
                    isPossible = true;
                }
            }
            
            if (isPossible) {
                result = result * inThisItem;
            } else {
                Log.print("getPossibleCombinations = 0 (item "+ item +" is impossible)");
                return 0;
            }
        }
        
        Log.print("getPossibleCombinations = "+ result);
        return result;
    }
    
    /**
     * Returns all <code>FileCombination</code>s that are not marked as bad. 
     * The number of combinations is that returned by 
     * {@link #getPossibleCombinations() getPossibleCombinations}.
     *
     * @return  an array containing the combinations, or null if updating is 
     *          needed or if there are no files
     */
    public FileCombination[] createPossibleCombinations() {
        // TODO: optimoi countChecksumsia silmällä pitäen, eli luetaan mahdollisimman paljon samasta tiedostosta
        
        Log.print("createPossibleCombinations: Start");
        if (this.needsUpdating) {
            Log.print("createPossibleCombinations: Aborted, needsUpdating == true");
            return null;
        }
        
        int possibleCombinations = this.getPossibleCombinations();
        if (possibleCombinations < 1) {
            Log.print("createPossibleCombinations: Aborted, no possibilities");
            return new FileCombination[0];          
        }
        
        FileCombination[] fc = new FileCombination[possibleCombinations];
        for (int i = 0; i < fc.length; i++) {
            fc[i] = new FileCombination();
        }
        Vector possibleFiles = new Vector();
        int goodFile;
        int combinationsDone = 1;
        long nextStart = 0;
        
        for (int item = 0; item < this.items.length; item++) {
            possibleFiles.removeAllElements();
            goodFile = -1;
            
            for (int i = 0; i < this.files.length; i++) {
                if (this.items[item][i].getMark() == MARK_IS_GOOD) {
                    goodFile = i;
                } else if (this.items[item][i].getMark() == MARK_IS_UNSURE
                        || this.items[item][i].getMark() == MARK_IS_UNDEFINED) {
                    possibleFiles.add(new Integer(i));
                }
            }
            
            // a good file is always the best possibility
            if (goodFile != -1) {
                possibleFiles.removeAllElements();
                possibleFiles.add(new Integer(goodFile));
            }
            
            // count all possibilities and add items to FileCombinations
            int successives = (fc.length / combinationsDone) / possibleFiles.size();
            combinationsDone = combinationsDone * possibleFiles.size();
            RepeatCounter counter = new RepeatCounter(possibleFiles, successives);
            
            for (int i = 0; i < fc.length; i++) {
                int inTurn = counter.getNext();
                File file = this.items[item][inTurn].getFile().getSourceFile();
                long start = nextStart;
                long end;
                
                if (item == (this.items.length - 1)) {
                    // last part
                    end = this.items[item][inTurn].getFile().getSourceFileLength();
                } else {
                    end = this.items[item][inTurn].getEndOffset();
                }
                
                fc[i].addItem(file, start, end);
            }
            nextStart = this.items[item][0].getEndOffset() + 1;
        }
        
        Log.print("createPossibleCombinations: Done");
        return fc;
    }
    
    /**
     * A class used by {@link Comparison#createPossibleCombinations() 
     * createPossibleCombinations}. <code>RepeatCounter</code> rotates goes 
     * through a list of numbers and returns each of them as many times as 
     * defined in the constructor before switching to the next number.
     *
     * @version     0.01, 2003-02-06
     * @author      Esko Luontola     
     */
    private class RepeatCounter {
        
        /**
         * The numbers in the rotation.
         */
        int[] values;
        
        /**
         * How many times each number is returned in a row.
         */
        int successives;
        
        /**
         * How many times the current number has been returned.
         */
        int successivesDone = 0;
        
        /**
         * The index of the current number.
         */
        int inTurn = 0;
        
        /**
         * Creates a <code>RepeatCounter</code> from an <code>int</code> array.
         *
         * @param   values          the numbers to be rotated
         * @param   successives     how many times each number is repeated
         */
        public RepeatCounter(int[] values, int successives) {
            if (values == null || values.length == 0 || successives < 1) {
                this.values = new int[1];
                this.values[0] = 0;
                this.successives = 1;
            } else {
                this.values = values;
                this.successives = successives;
            }
        }
        
        /**
         * Creates a <code>RepeatCounter</code> from a <code>Vector</code> 
         * of <code>Integer</code>s.
         *
         * @param   integers        a <code>Vector</code> of <code>Integer</code>s
         *                          containing the numbers to be rotated
         * @param   successives     how many times each number is repeated
         */
        public RepeatCounter(Vector integers, int successives) {
            if (integers == null || integers.size() == 0 || successives < 1) {
                this.values = new int[1];
                this.values[0] = 0;
                this.successives = 1;
            } else {
                this.values = new int[integers.size()];
                for (int i = 0; i < values.length; i++) {
                    try {
                        this.values[i] = ((Integer) (integers.elementAt(i))).intValue();
                    } catch (ClassCastException e) {}
                }
                this.successives = successives;
            }
        }
        
        /**
         * Returns the number in turn and switches to the next one when needed.
         *
         * @return  the number in turn
         */
        public int getNext() {
            int result = this.values[inTurn];
            this.successivesDone++;
            if (this.successivesDone == this.successives) {
                this.successivesDone = 0;
                this.inTurn++;
                if (this.inTurn == this.values.length) {
                    this.inTurn = 0;
                }
            }
            return result;
        }
        
    }
    
    /**
     * Returns where this <code>Comparison</code> was previously saved.
     *
     * @return  where this was saved, or null if not saved
     * @see     #saveToFile(File)
     */
    public File getSavedAsFile() {
        return this.savedAsFile;
    }
    
    /**
     * Saves this <code>Comparison</code> object into a file.
     *
     * @param   file    the file in which to save
     * @return  true if successful, otherwise false
     * @see     #loadFromFile(File)
     */
    public boolean saveToFile(File file) {
        boolean successful = ObjectSaver.saveToFile(file, this);
        if (successful) {
            this.savedAsFile = file;
        }
        return successful;
    }
    
    /**
     * Loads a previously saved <code>Comparison</code> from a file.
     *
     * @param   file    the file from which to load
     * @return  a new <code>Comparison</code> loaded from the file, 
     *          or null if operation failed
     * @see     #saveToFile(File)
     */
    public static Comparison loadFromFile(File file) {
        Comparison result;
        try {
            result = (Comparison) (ObjectSaver.loadFromFile(file));
        } catch (Exception e) {
            result = null;
        }
        return result;
    }
    
    /**
     * Returns a text representation of this object.
     *
     * @return  a text representation of this object.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        
        result.append("\t*** Comparison ***\n");
        
        result.append(" * Differences:\n");
        for (int i = 0; i < getDifferences(); i++) {
            result.append("part " + getPart(i) + ": ");
            for (int j = 0; j < getFiles(); j++) {
                result.append("\t" + getChecksum(i, j) + " (" + getMark(i, j) + ")");
            }
            result.append("\t" + getFile(0).getStartOffset(getPart(i)) + "-" + getFile(0).getEndOffset(getPart(i)) + "\n");
        }
        
        result.append("length:   ");
        for (int i = 0; i < getFiles(); i++) {
            result.append("\t"+ getFile(i).getSourceFileLength() +" bytes");
        }
        
        result.append("\n * Similarity:");
        for (int i = 0; i < getFiles(); i++) {
            result.append("\nfile " + i + ":");
            for (int j = 0; j < getFiles(); j++) {
                result.append("\t" + getSimilarity(i, j));
            }
        }
        
        return result.toString();
    }
}