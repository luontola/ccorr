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
import javax.swing.event.*;
import javax.swing.table.*;
import util.SwingWorker;

/**
 * The user interface for a <code>Comparison</code>. It consists primarily of a
 * <code>JTable</code>, which shows the data a <code>Comparison</code> holds
 * and provides means for changing the markers.
 *
 * @version     1.01, 2003-02-13
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
        
        /*
         * TABLE
         */
        tableModel = new ComparisonTableModel(comparison);
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        //table.getTableHeader().setResizingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(false);
        //table.setSelectionModel(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(ComparisonItem.class, new ComparisonItemRenderer());
        
        // listen to mouse clicks that change markers
        table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    int row = table.rowAtPoint(e.getPoint());
                    int column = table.columnAtPoint(e.getPoint());
                    if (column == 0) {
                    	switchShowParts();
				    } else {
                    	tableModel.nextMark(row, column);
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
        
        /*
         * BOTTOM PANE
         */
        JPanel bottomPane = new JPanel();
        bottomPane.setBorder(BorderFactory.createEtchedBorder());
        bottomPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(bottomPane, BorderLayout.SOUTH);
        
        /*
         * BUTTONS
         */
        final JButton writeOutputButton = new JButton("Write Output");
        final ComparisonPanel parent = this;
        writeOutputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        parent.writeOutput();
                        writeOutputButton.setEnabled(true);
                        return null;
                    }
                };
                writeOutputButton.setEnabled(false);
                worker.start();
            }
        });
        bottomPane.add(writeOutputButton);
        
        JButton button = new JButton("Add Checksum File");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChecksumFile();
            }
        });
        bottomPane.add(button);
        
        button = new JButton("(Notepad)");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openNotepad();
            }
        });
//      bottomPane.add(button);
        
        button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        bottomPane.add(button);
        
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
            
            // setup progress monitor
            ProgressMonitor monitor = new ProgressMonitor(this, 
                    "Writing output...", "Please Wait", 0, 0);
            Settings.setProgressMonitor(monitor);
            
            // start writing
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
        return this.tableModel;
    }
    
    /**
     * Switches between showing part numbers and byte offsets in the first column.
     */
    public void switchShowParts() {
    	this.tableModel.switchShowParts();
    	
    	// update column header
    	TableColumn col = this.table.getColumnModel().getColumn(0);
    	col.setHeaderValue(this.tableModel.getColumnName(0));
    	this.table.getTableHeader().repaint();
    }
    
    /**
     * Marks the selected cell's ComparisonItem "good".
     */
    public void markSelectedGood() {
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        tableModel.setMark(row, column, Comparison.MARK_IS_GOOD);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "bad".
     */
    public void markSelectedBad() {
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        tableModel.setMark(row, column, Comparison.MARK_IS_BAD);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "unsure".
     */
    public void markSelectedUnsure() {
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        tableModel.setMark(row, column, Comparison.MARK_IS_UNSURE);
    }
    
    /**
     * Marks the selected cell's ComparisonItem "undefined".
     */
    public void markSelectedUndefined() {
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        tableModel.setMark(row, column, Comparison.MARK_IS_UNDEFINED);
    }
    
    /**
     * Changes the mark of the selected cell's ComparisonItem.
     */
    public void markSelectedNext() {
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
    	tableModel.nextMark(row, column);
    }
}