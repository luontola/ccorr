/* 
 * Copyright (C) 2003-2004  Esko Luontola, http://ccorr.sourceforge.net
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
import javax.swing.table.*;

/**
 * Popupmenu that shows up when right clicking a file's header in a comparison.
 *
 * @author      Esko Luontola
 */
public class ComparisonHeaderPopupMenu extends JPopupMenu implements MouseListener {
	
	private int selectedColumn = -1;
	
	private ComparisonTableModel tableModel;
	
	public ComparisonHeaderPopupMenu(ComparisonTableModel model) {
		tableModel = model;
		
        JMenuItem menuItem = new JMenuItem("Remove File");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int column = selectedColumn - 1;
                if (column >= 0) {
                    tableModel.removeFile(column);
                }
            }
        });
        add(menuItem);
	}
		
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JTableHeader tableHeader = (JTableHeader)e.getComponent();
            selectedColumn = tableHeader.columnAtPoint(new Point(e.getX(), e.getY()));
            if (selectedColumn > 0) {
            	show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}