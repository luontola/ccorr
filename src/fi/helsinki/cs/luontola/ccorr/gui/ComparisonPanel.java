/* 
 * Copyright (C) 2003-2005  Esko Luontola, http://ccorr.sourceforge.net
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
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * The user interface for a <code>Comparison</code>. It consists primarily of a
 * <code>JTable</code>, which shows the data a <code>Comparison</code> holds
 * and provides means for changing the markers.
 *
 * @author      Esko Luontola
 */
public class ComparisonPanel extends TabPanel {
    
    /**
     * The main part of this GUI.
     */
    private final JTable table;
    
    /**
     * The TableModel that the table uses.
     */
    private final ComparisonTableModel tableModel;
    
    /**
     * Creates a new ComparisonPanel that uses a new Comparison.
     */
    public ComparisonPanel() {
        this(new Comparison());
    }
   
    /**
     * Creates a new ComparisonPanel that uses the given Comparison.
     *
     * @param	comparison	the Comparison to be used, or null for a new Comparison
     */
    public ComparisonPanel(Comparison comparison) {
        if (comparison == null) {
            comparison = new Comparison();
        }
        
        setLayout(new BorderLayout());

        /**
         * TABLE
         */
        tableModel = new ComparisonTableModel(comparison);
        table = new JTable(tableModel);
        
        table.getTableHeader().setReorderingAllowed(false);
        //table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().addMouseListener(new ComparisonHeaderPopupMenu(tableModel));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        table.setDefaultRenderer(ComparisonItem.class, new ComparisonItemRenderer());
        
        // listen to mouse clicks that change markers
        table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    
                    // check if the cell is part of a larger selection
                    int[] rows = table.getSelectedRows();
                    int[] cols = table.getSelectedColumns();
                    
                    for (int i = 0; i < rows.length; i++) {
                        if (rows[i] == row) {
                            for (int j = 0; j < cols.length; j++) {
                                if (cols[j] == col) {
                                    markSelectedNext();
                                    return;
                                }
                            }
                        }
                    }
                    
                    if (col == 0) {
                        switchShowParts();
                    } else {
                        tableModel.nextMark(row, col);
                    }
                }
            }
            public void mouseReleased(MouseEvent e) {}
        });
        
//      // resize columns to fit the contents
//      for (int i = 0; i < table.getColumnCount(); i++) {
//          TableColumn column = table.getColumnModel().getColumn(i);
//          Component comp = table.getTableHeader().getDefaultRenderer().
//                  getTableCellRendererComponent(
//                      null, column.getHeaderValue(), 
//                      false, false, 0, 0);
//          int headerWidth = comp.getPreferredSize().width;
//          comp = table.getDefaultRenderer(tableModel.getColumnClass(i)).
//                  getTableCellRendererComponent(
//                      table, tableModel.getValueAt(0, i),
//                      false, false, 0, i);
//          int cellWidth = comp.getPreferredSize().width;
//          column.setPreferredWidth(Math.max(headerWidth, cellWidth) + 10);
//      }
        
        
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createBottomPanel() {
        JPanel p = new JPanel();
        JButton button;
        
        p.setBorder(BorderFactory.createEtchedBorder());
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        button = new JButton("Add File");
        final JButton button1 = button;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	button1.setEnabled(false);
            	Thread t = new Thread() {
            		public void run() {
            			createAndAddChecksumFile();
            			button1.setEnabled(true);
            		}
            	};
            	t.setPriority(Thread.NORM_PRIORITY);
            	t.start();
            }
        });
        p.add(button);
        
        button = new JButton("Add Checksum File");
        final JButton button2 = button;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	button2.setEnabled(false);
                addChecksumFile();
            	button2.setEnabled(true);
            }
        });
        p.add(button);
        
        button = new JButton("Write Output");
        final JButton button3 = button;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	button3.setEnabled(false);
            	Thread t = new Thread() {
            		public void run() {
            			writeOutput();
            			button3.setEnabled(true);
            		}
            	};
				t.setPriority(Thread.NORM_PRIORITY);
            	t.start();
            }
        });
        p.add(button);
        
//        button = new JButton("(Notepad)");
//        button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                openNotepad();
//            }
//        });
//        bottomPane.add(button);
        
        button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        p.add(button);
        
        return p;
    }
    
    /**
     * Writes a good combination to a file.
     */
    public void writeOutput() {
        FileCombination fc = tableModel.createGoodCombination();
        if (fc == null) {
            
            // TODO: possible combinations
            JOptionPane.showMessageDialog(this, 
                    "It is not possible to create a good output file. "
                    + "More good parts are needed.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(Settings.getCurrentDirectory());
            
            boolean ok = false;
            do {
                int returnVal = chooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    
                    if (chooser.getSelectedFile().exists()) {
                        returnVal = JOptionPane.showConfirmDialog(this, 
                                "The selected file exists. Do you want to overwrite it?", 
                                "Overwrite File?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (returnVal == JOptionPane.YES_OPTION) {
                            ok = true;
                        } else if (returnVal == JOptionPane.NO_OPTION) {
                            ok = false;
                        } else if (returnVal == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    } else {
                        ok = true;
                    }
                    
                } else {
                    return;
                }
            } while (!ok);
            
            // start writing
            Settings.setProgressMonitor(new ProgressMonitor(this, 
                    "Writing output", "", 0, 0));
            
            boolean successful = fc.writeFile(chooser.getSelectedFile());
            Settings.setCurrentDirectory(chooser.getSelectedFile());
            if (!successful) {
                JOptionPane.showMessageDialog(this, 
                        "There was an error in writing the file \""
                        + chooser.getSelectedFile().getName() + "\".", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Creates a new ChecksumFile and adds it to the Comparison.
     */
    public void createAndAddChecksumFile() {
        File inputFile;
        File outputFile;
        String algorithm;
        int partLength;
        ChecksumFile ccf;
        
        // select input file, algorithm and part length
        CreateChecksumsFileChooser chooser = new CreateChecksumsFileChooser();
        chooser.setCurrentDirectory(Settings.getCurrentDirectory());
        
        algorithm = tableModel.getAlgorithm();
        partLength = tableModel.getPartLength();
        
        if (algorithm != null && partLength > 0) {
    	  	chooser.setAlgorithm(algorithm);
	        chooser.setPartLength(partLength);
        	chooser.setOptionsEnabled(false);
        }
        	
        int returnVal = chooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	algorithm = chooser.getAlgorithm();
        	partLength = chooser.getPartLength();
        	
        	Settings.setDefaultAlgorithm(algorithm);
        	Settings.setDefaultPartLength(partLength);
            Settings.setCurrentDirectory(chooser.getSelectedFile());
            
            if (chooser.getSelectedFile().isFile()) {
                inputFile = chooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "This is not a file.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
        	return;
        }
        
        // select where to save the output file
        outputFile = new File(inputFile.getAbsolutePath() + ".ccf");
        
        // check input file
        if (!inputFile.exists() || !inputFile.canRead()) {
            JOptionPane.showMessageDialog(this, 
                    "Unable to read "+ inputFile, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // check output file
        try {
            if (!(outputFile.exists() && outputFile.canWrite()) && !(outputFile.createNewFile() && outputFile.canWrite())) {
                JOptionPane.showMessageDialog(this, 
                        "Unable to write "+ outputFile, 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Unable to write "+ outputFile, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // do the checksums
        Settings.setProgressMonitor(new ProgressMonitor(this, 
                "Creating checksums", "", 0, 0));
        
        ChecksumFile cf = ChecksumFile.createChecksumFile(inputFile, partLength, algorithm);
        
        if (cf == null) {
            JOptionPane.showMessageDialog(this, 
                    "An error happened in making the checksum file.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (!cf.saveToFile(outputFile)) {
                JOptionPane.showMessageDialog(this, 
                        "There was an error in writing "+ outputFile,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            addChecksumFile(cf);
            System.out.println("OK");
        }
    }
    
    /**
     * Adds a ChecksumFile to the Comparison.
     *
     * @param	cf	the ChecksumFile to be added
     */
    public void addChecksumFile(ChecksumFile cf) {
        if (cf != null) {
            tableModel.addFile(cf);
        }
    }
    
    /**
     * Shows a file dialog for loading a ChecksumFile from a file.
     */
    public void addChecksumFile() {
        JFileChooser chooser = new JFileChooser();
        String[] ext = { "ccf" };
        GeneralFileFilter filter = new GeneralFileFilter(ext, "CCorr Checksum File");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File("*.ccf"));
        chooser.setCurrentDirectory(Settings.getCurrentDirectory());
        int returnVal = chooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            
            ChecksumFile cf = ChecksumFile.loadFromFile(chooser.getSelectedFile());
            Settings.setCurrentDirectory(chooser.getSelectedFile());
            if (cf != null) {
                tableModel.addFile(cf);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "The file \"" + chooser.getSelectedFile().getName() 
                        + "\" was not recognized as a valid CCorr Checksum File.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * NOT IMPLEMENTED
     */
    public void openNotepad() {
        
    }
    
    /**
     * Saves any modifications made to the Comparison.
     */
    public boolean saveComparison() {
        // save where previously
        if (tableModel.getSavedAsFile() != null) {
            if (tableModel.saveToFile(tableModel.getSavedAsFile()) == true) {
                return true;
            }
        }
        
        // otherwise save to a new file
        return this.saveComparisonAs();
    }
        
    /**
     * Saves the Comparison to a file and prompts for the file name.
     */
    public boolean saveComparisonAs() {
        JFileChooser chooser = new JFileChooser();
        String[] ext = { "ccp" };
        GeneralFileFilter filter = new GeneralFileFilter(ext, "CCorr Comparison Projects");
        chooser.setFileFilter(filter);
        File selectedFile;
        if (tableModel.getSavedAsFile() != null) {
            selectedFile = tableModel.getSavedAsFile();
        } else {
            selectedFile = new File("*.ccp");
        }
        chooser.setSelectedFile(selectedFile);
        chooser.setCurrentDirectory(Settings.getCurrentDirectory());
        
        boolean ok = false;
        do {
            int returnVal = chooser.showDialog(this, "Save As");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                
                if (chooser.getSelectedFile().exists()) {
                    returnVal = JOptionPane.showConfirmDialog(this, 
                            "The selected file exists. Do you want to overwrite it?", 
                            "Overwrite File?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (returnVal == JOptionPane.YES_OPTION) {
                        ok = true;
                    } else if (returnVal == JOptionPane.NO_OPTION) {
                        ok = false;
                    } else if (returnVal == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                } else {
                    ok = true;
                }
                
            } else {
                return false;
            }
        } while (!ok);
        
        boolean successful = tableModel.saveToFile(chooser.getSelectedFile());
        Settings.setCurrentDirectory(chooser.getSelectedFile());
        if (!successful) {
            JOptionPane.showMessageDialog(this, 
                    "There was an error writing the file "
                    + chooser.getSelectedFile().getName() + ".", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return successful;
    }
    
    /**
     * Closes this ComparisonPanel and prompts for saving any changes.
     */
    public boolean close() {
        if (tableModel.isModified()) {
            int returnVal = JOptionPane.showConfirmDialog(this,
                    "The comparison has been modified. Do you want to save the changes?",
                    "Save Comparison?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (returnVal == JOptionPane.CANCEL_OPTION) {
                return false;
            }
            if (returnVal == JOptionPane.YES_OPTION) {
                if (this.saveComparison() == false) {
                    return false;
                }
            }
        }
        
        return super.close();
    }
    
    /**
     * Returns the TableModel connected to this ComparisonPanel.
     */
    public ComparisonTableModel getTableModel() {
        return tableModel;
    }
    
    /**
     * Switches between showing part numbers and byte offsets in the first column.
     */
    public void switchShowParts() {
        tableModel.switchShowParts();
        
        // update column header
        TableColumn col = this.table.getColumnModel().getColumn(0);
        col.setHeaderValue(this.tableModel.getColumnName(0));
        this.table.getTableHeader().repaint();
    }
    
    /**
     * Sets the marker for all selected cells 
     * if all of them have the same old marker
     *
     * @param   mark  new marker value to be set
     * @return	true if successful, false otherwise
     */
    private void markSelected(int mark) {
        int[] rows = table.getSelectedRows();
        int[] cols = table.getSelectedColumns();
        tableModel.setMarks(rows, cols, mark);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "good".
     */
    public void markSelectedGood() {
        markSelected(Comparison.MARK_IS_GOOD);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "bad".
     */
    public void markSelectedBad() {
        markSelected(Comparison.MARK_IS_BAD);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "unsure".
     */
    public void markSelectedUnsure() {
        markSelected(Comparison.MARK_IS_UNSURE);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "undefined".
     */
    public void markSelectedUndefined() {
        markSelected(Comparison.MARK_IS_UNDEFINED);
    }
    
    /**
     * Changes the mark of the selected cell's ComparisonItem.
     */
    public void markSelectedNext() {
        markSelected(Comparison.NEXT_MARK);
    }
    
    /**
     * Finds and marks cells in the table which appear to be good parts.
     */
    public void markGoodPartsInTable() {
        tableModel.markGoodParts(0, table.getRowCount() - 1);
    }

    /**
     * Sets all parts in the table to MARK_IS_UNDEFINED.
     */
    public void markTableUndefined() {
        tableModel.markRowUndefined(0, table.getRowCount() - 1);
    }

    /**
     * Returns the name of the comparison.
     *
     * @return the name of the comparison as a <code>String</code>
     */
    public String getName() {
        return tableModel.getName();
    }

    /**
     * Sets the name of the comparison to the given <code>String</code>.
     *
     * @param name the new name of the comparison
     */
    public void setName(String name) {
        tableModel.setName(name);
    }
}