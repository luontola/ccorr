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
import javax.swing.*;
import javax.swing.text.*;

/**
 * JFileChooser with extra fields for selecting the part length and algorithm.
 *
 * @author      Esko Luontola
 */
public class CreateChecksumsFileChooser extends JFileChooser {
	
	private JFormattedTextField partLengthField;
	
	private JComboBox algorithmField;
	
	public CreateChecksumsFileChooser() {
		initAlgorithmInputField();
		initPartLengthInputField();
	}
	
	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog dialog = super.createDialog(parent);
		Container origCP = dialog.getContentPane();
		JPanel newCP = new JPanel(new BorderLayout(0, 0));
		
		newCP.add(origCP, "Center");
		newCP.add(createExtraButtons(), "South");
		dialog.setContentPane(newCP);
		
		Dimension d = dialog.getSize();
		dialog.setSize((int)d.getWidth(), (int)d.getHeight() + 70);
		
		return dialog;
	}
	
	private JComponent createExtraButtons() {
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(0);
		layout.setVgap(0);
		
		JPanel p = new JPanel(layout);
		p.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
		
		p.add(new JLabel("Part Length:  "));
		p.add(partLengthField);
		p.add(new JLabel(" KB"));
		
		p.add(Box.createRigidArea(new Dimension(20, 10)));
		
		p.add(new JLabel("Algorithm:  "));
		p.add(algorithmField);
		
		return p;
	}
	
	private void initAlgorithmInputField() {
		algorithmField = new JComboBox(CRC.getSupportedAlgorithms());
		algorithmField.setSelectedItem(Settings.getDefaultAlgorithm());
	}
	
	private void initPartLengthInputField() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setMinimum(new Integer(ChecksumFile.MIN_PART_SIZE / 1024));
		formatter.setMaximum(new Integer(ChecksumFile.MAX_PART_SIZE / 1024));
		
		partLengthField = new JFormattedTextField(formatter);
		partLengthField.setValue(new Integer(Settings.getDefaultPartLength() / 1024));
		partLengthField.setColumns(5);
	}
	
	public void setAlgorithm(String algorithm) {
		algorithmField.setSelectedItem(algorithm);
	}
    
	public String getAlgorithm() {
		return (String)algorithmField.getSelectedItem();
	}
    
	public void setPartLength(int partLength) {
		partLengthField.setValue(new Integer(partLength / 1024));
	}
    
	public int getPartLength() {
		return ((Integer)partLengthField.getValue()).intValue() * 1024;
	}
    
    public void setOptionsEnabled(boolean b) {
    	algorithmField.setEnabled(b);
    	partLengthField.setEnabled(b);
    }
}