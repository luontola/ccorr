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
import util.SwingWorker;

/**
 * User interface for creating checksum files.
 *
 * @version     1.00, 2003-02-06
 * @author      Esko Luontola
 */
public class CreateChecksumFilePanel extends TabPanel {
    
    /**
     * Used in addToComboBox to indicate if the ChecksumFile 
     * should not be added to a Comparison.
     */
    private final String DO_NOT_ADD = "< do not add >";
    
    private JLabel inputFileLabel = new JLabel("Input File:");
    private JTextField inputFileText = new JTextField(10);
    private JButton inputFileButton = new JButton("Browse");
    
    private JLabel outputFileLabel = new JLabel("Output CCF File:");
    private JTextField outputFileText = new JTextField(10);
    private JButton outputFileButton = new JButton("Browse");
    
    private JLabel addToLabel = new JLabel("Add to Project:");
    private JComboBox addToComboBox = new JComboBox();
    
    private JButton createFileButton = new JButton("Create Checksum File");
    private JButton closeButton = new JButton("Close");
    
    /**
     * Creates a new instance of this class.
     */
    public CreateChecksumFilePanel() {
        
        addToComboBox.addItem(DO_NOT_ADD);
        
        JTabbedPane tabbedPane = MainWindow.getTabbedPane();
        if (tabbedPane != null) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getComponentAt(i) instanceof ComparisonPanel) {
                    addToComboBox.addItem(tabbedPane.getTitleAt(i));
                }
            }
        }
        
//      inputFileText.setEditable(false);
//      outputFileText.setEditable(false);
        
        // look for changes in inputFileText and update outputFileText
        inputFileText.addCaretListener(new CaretListener() {
            private String lastText = "";
            public void caretUpdate(CaretEvent e) {
                if (!lastText.equals(inputFileText.getText())) {
                    lastText = inputFileText.getText();
                    outputFileText.setText(inputFileText.getText() + ".ccf");
                }
            }
        });
        
        inputFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseInputFile();
            }
        });
        
        outputFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseOutputFile();
            }
        });
        
        createFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        createChecksumFile();
                        createFileButton.setEnabled(true);
                        return null;
                    }
                };
                createFileButton.setEnabled(false);
                worker.start();
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        
        
        /*
         * place components to panels
         */
        JPanel content = new JPanel();
        SpringLayout layout = new SpringLayout();
        content.setLayout(layout);
        content.setBorder(BorderFactory.createEmptyBorder(11, 11, 12, 12));
        
        content.add(inputFileLabel);
        content.add(inputFileText);
        content.add(inputFileButton);
        
        content.add(outputFileLabel);
        content.add(outputFileText);
        content.add(outputFileButton);
        
        content.add(addToLabel);
        content.add(addToComboBox);
        
        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEtchedBorder());
        bottom.add(createFileButton);
        bottom.add(closeButton);
        
        this.setLayout(new BorderLayout());
        this.add(content, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
        
        /*
         * do SpringLayout
         */
        Spring xPad = Spring.constant(5);
        Spring yPad = Spring.constant(5);
        
        Spring maxEastSpring = Spring.max(
                layout.getConstraint("East", inputFileLabel), 
                Spring.max(
                        layout.getConstraint("East", outputFileLabel), 
                        layout.getConstraint("East", addToLabel)));
        Spring maxHeightSpring;
        Spring rX = Spring.sum(maxEastSpring, xPad); // right column start
        Spring negRX = Spring.minus(rX);
        Spring parentWidth = layout.getConstraint("East", content);
        Spring nextY;
        
        SpringLayout.Constraints consL;
        SpringLayout.Constraints consR;
        SpringLayout.Constraints consR2;
        
        // 1st row
        consL = layout.getConstraints(inputFileLabel);
        consR = layout.getConstraints(inputFileText);
        consR2 = layout.getConstraints(inputFileButton);
        
        consR.setX(rX);
        consR.setWidth(
            Spring.sum(
                parentWidth,
                Spring.sum(
                    Spring.minus(xPad),
                    Spring.sum(
                        negRX, 
                        Spring.minus(consR2.getWidth())))));
        consR2.setX(Spring.sum(consR.getConstraint("East"), xPad));
        
        nextY = Spring.sum(
                yPad,
                Spring.max(
                        consL.getConstraint("South"), 
                        Spring.max(
                                consR.getConstraint("South"), 
                                consR2.getConstraint("South"))));
        
        // 2nd row
        consL = layout.getConstraints(outputFileLabel);
        consR = layout.getConstraints(outputFileText);
        consR2 = layout.getConstraints(outputFileButton);
        
        consR.setX(rX);
        consR.setWidth(
            Spring.sum(
                parentWidth,
                Spring.sum(
                    Spring.minus(xPad),
                    Spring.sum(
                        negRX, 
                        Spring.minus(consR2.getWidth())))));
        consR2.setX(Spring.sum(consR.getConstraint("East"), xPad));
        
        consL.setY(nextY);
        consR.setY(nextY);
        consR2.setY(nextY);
        
        nextY = Spring.sum(
                yPad,
                Spring.max(
                        consL.getConstraint("South"), 
                        Spring.max(
                                consR.getConstraint("South"), 
                                consR2.getConstraint("South"))));
        
        // 3rd row
        consL = layout.getConstraints(addToLabel);
        consR = layout.getConstraints(addToComboBox);
        
        consR.setX(rX);
        consR.setWidth(Spring.sum(parentWidth, negRX));
        
        consL.setY(nextY);
        consR.setY(nextY);
        
        nextY = Spring.sum(
                yPad,
                Spring.max(
                        consL.getConstraint("South"), 
                        Spring.max(
                                consR.getConstraint("South"), 
                                consR2.getConstraint("South"))));
        
    }
    
    /**
     * Opens a file dialog for choosing the input file.
     */
    public void chooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(inputFileText.getText()));
        
        if (inputFileText.getText().equals("")) {
            chooser.setCurrentDirectory(Settings.getCurrentDirectory());
        } else {
            chooser.setCurrentDirectory(new File(inputFileText.getText()).getParentFile());
        }
        
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Settings.setCurrentDirectory(chooser.getSelectedFile());
            if (chooser.getSelectedFile().isFile()) {
                inputFileText.setText(chooser.getSelectedFile().getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this, 
                        "A valid file needs to be selected.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Opens a file dialog for choosing the output file.
     */
    public void chooseOutputFile() {
        JFileChooser chooser = new JFileChooser();
        String[] ext = { "ccf" };
        GeneralFileFilter filter = new GeneralFileFilter(ext, "CCorr Checksum File");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File(outputFileText.getText()));
        
        if (outputFileText.getText().equals("")) {
            chooser.setCurrentDirectory(Settings.getCurrentDirectory());
        } else {
            chooser.setCurrentDirectory(new File(outputFileText.getText()).getParentFile());
        }
        
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String text = chooser.getSelectedFile().getAbsolutePath();
            if (!".ccf".equals(text.substring(text.length()-4, text.length()))) {
                text += ".ccf";
            }
            outputFileText.setText(text);
            Settings.setCurrentDirectory(chooser.getSelectedFile());
        }
    }
    
    /**
     * Creates a ChecksumFile out of the input file, writes it to the output file, 
     * and adds it to a Comparison if requested.
     */
    public void createChecksumFile() {
        File input = new File(inputFileText.getText());
        File output = new File(outputFileText.getText());
        
        // verify input file
        if (!input.exists() || !input.canRead()) {
            JOptionPane.showMessageDialog(this, 
                    "The input file \""+ input +"\" can not be read.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // verify output file
        try {
            if (!(output.exists() && output.canWrite()) && !(output.createNewFile() && output.canWrite())) {
                JOptionPane.showMessageDialog(this, 
                        "Unable to write the output file.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Unable to write the output file.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // setup progress monitor
        ProgressMonitor monitor = new ProgressMonitor(this, 
                "Creating checksums...", "Please Wait", 0, 0);
        Settings.setProgressMonitor(monitor);
        
        // start reading checksums
        ChecksumFile cf = ChecksumFile.createChecksumFile(input, 
                Settings.getDefaultPartLength(), 
                Settings.getDefaultAlgorithm());
        
        // check results
        if (cf == null) {
            JOptionPane.showMessageDialog(this, 
                    "An error happened in making the Checksum File.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            
            // add to project
            String target = (String) (addToComboBox.getSelectedItem());
            if (!target.equals(DO_NOT_ADD)) {
                
                // eliminate duplicate names
                int duplicates = 0;
                for (int i = 0; i < addToComboBox.getSelectedIndex(); i++) {
                    String s = (String) (addToComboBox.getItemAt(i));
                    if (s.equals(target)) {
                        duplicates++;
                    }
                }
                
                // add to panel
                JTabbedPane tab = MainWindow.getTabbedPane();
                for (int i = 0; i < tab.getTabCount(); i++) {
                    if (tab.getComponentAt(i) instanceof ComparisonPanel) {
                        ComparisonPanel cp = (ComparisonPanel) (tab.getComponentAt(i));
                        if (!target.equals(tab.getTitleAt(i))) {
                            // do nothing
                        } else if (duplicates == 0) {
                            cp.addChecksumFile(cf);
                            break;
                        } else {
                            duplicates--;
                        }
                    }
                }
                
                
            }
            
            // save to file
            if (!cf.saveToFile(output)) {
                JOptionPane.showMessageDialog(this, 
                        "There was an error in writing the file \""+ output +"\".",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}