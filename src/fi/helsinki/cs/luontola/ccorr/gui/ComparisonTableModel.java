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

package fi.helsinki.cs.luontola.ccorr.gui;

import fi.helsinki.cs.luontola.ccorr.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * A <code>TableModel</code> for showing a <code>Comparison</code>
 * in a <code>JTable</code>.
 *
 * @version     1.01, 2003-02-13
 * @author      Esko Luontola
 */
public class ComparisonTableModel extends AbstractTableModel {
    
    /**
     * The Comparison object that this ComparisonPanel represents.
     */
    private Comparison comparison;
    
    /**
     * Indicates if this Comparison has been modified after saving.
     */
    private boolean isModified = false;
    
    /**
     * Indicates whether part numbers or byte offsets are shown.
     */
    private boolean showParts = true;
    
    /**
     * Creates a new instance of this class.
     *
     * @param	comparison	the Comparison object that this should represent, 
     *						or null to create a new Comparison
     */
    public ComparisonTableModel(Comparison comparison) {
        if (comparison == null) {
            comparison = new Comparison();
        }
        this.comparison = comparison;
    }
    
    /**
     * Returns the number of columns in the model.
     */
    public int getColumnCount() {
        return this.comparison.getFiles() + 1;
    }
    
    /**
     * Returns the number of rows in the model.
     */
    public int getRowCount() {
        return this.comparison.getDifferences();
    }
    
    /**
     * Returns the name of the column at columnIndex.
     */
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
        	if (this.showParts) {
        		return "Part #";
		    } else {
		    	return "Bytes";
		    }
        } else {
            return this.comparison.getFile(columnIndex - 1).getSourceFile().getName();
        }
    }
    
    /**
     * Returns the value for the cell at rowIndex and columnIndex.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result;
        if (columnIndex == 0) {
        	if (this.showParts) {
        		result = Integer.toString(comparison.getPart(rowIndex) + 1);
		    } else {
        		result = comparison.getStartOffset(rowIndex) 
        				 + "-" + comparison.getEndOffset(rowIndex);
		    }
        } else {
            result = this.comparison.getItem(rowIndex, columnIndex - 1);
        }
        return result;
    }
    
    /**
     * Returns the class of the cell at <code>columnIndex</code> and row index 0.
     */
    public Class getColumnClass(int columnIndex) {
        return this.getValueAt(0, columnIndex).getClass();
    }
    
    /**
     * Returns true if the cell at rowIndex and columnIndex is editable.
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	if (columnIndex == 0) {
    		return true;	// enable copying to clipboard, modifying not possible
	    } else {
        	return false;
        }
    }
    
    /**
     * Returns true if the Comparison has been modified after saving.
     */
    public boolean isModified() {
        return this.isModified;
    }
    
    /**
     * Switches between showing part numbers and byte offsets in the first column.
     * Does only fireTableDataChanged and not update the column header.
     */
    public void switchShowParts() {
    	this.showParts = !this.showParts;
    	this.fireTableDataChanged();
	}
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#doCompare()
     */
    public void doCompare() {
        this.isModified = true;
        this.comparison.doCompare();
        this.fireTableStructureChanged();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#getFiles()
     */
    public int getFiles() {
        return this.comparison.getFiles();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#setMark(int, int, int)
     */
    public void setMark(int row, int col, int mark) {
        this.isModified = true;
        this.comparison.setMark(row, col - 1, mark);
        this.fireTableRowsUpdated(row, row);
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#nextMark(int, int)
     */
    public int nextMark(int row, int col) {
        this.isModified = true;
        int result = this.comparison.nextMark(row, col - 1);
        this.fireTableRowsUpdated(row, row);
        return result;
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#setComments(String)
     */
    public void setComments(String comments) {
        this.isModified = true;
        this.comparison.setComments(comments);
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#getComments()
     */
    public String getComments() {
        return this.comparison.getComments();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#addFile(ChecksumFile)
     */
    public void addFile(ChecksumFile file) {
        this.isModified = true;
        this.comparison.addFile(file);
        this.comparison.doCompare();
        this.fireTableStructureChanged();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#removeFile(ChecksumFile)
     */
    public void removeFile(ChecksumFile file) {
        this.isModified = true;
        this.comparison.removeFile(file);
        this.comparison.doCompare();
        this.fireTableStructureChanged();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#createGoodCombination()
     */
    public FileCombination createGoodCombination() {
        return this.comparison.createGoodCombination();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#getPossibleCombinations()
     */
    public int getPossibleCombinations() {
        return this.comparison.getPossibleCombinations();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#createPossibleCombinations()
     */
    public FileCombination[] createPossibleCombinations() {
        return this.comparison.createPossibleCombinations();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#getSavedAsFile()
     */
    public File getSavedAsFile() {
        return this.comparison.getSavedAsFile();
    }
    
    /**
     * Represents the corresponding method in the comparison.
     *
     * @see Comparison#saveToFile(File)
     */
    public boolean saveToFile(File file) {
        boolean successful = this.comparison.saveToFile(file);
        if (successful) {
            this.isModified = false;
        }
        return successful;
    }
}