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
 * A <code>TableCellRenderer</code> for rendering a <code>ComparisonItem</code> 
 * in a <code>JTable</code>.
 *
 * @version     1.00, 2003-02-06
 * @author      Esko Luontola
 */
class ComparisonItemRenderer extends JLabel implements TableCellRenderer {
    
    /**
     * Creates a new instance of this class.
     */
    public ComparisonItemRenderer() {
        this.setOpaque(true);
        this.setHorizontalAlignment(JLabel.CENTER);
        //this.setVerticalAlignment(JLabel.CENTER);
    }
    
    /**
     * Returns the component that represents a ComparisonItem cell.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (value instanceof ComparisonItem) {
            ComparisonItem item = (ComparisonItem) (value);
            this.setText(item.getCaption());
            this.setFont(new Font("Courier New", Font.PLAIN, 12));
            
            Color color;
            switch (item.getMark()) {
            case Comparison.MARK_IS_UNDEFINED:
                color = Color.white;
                break;
            
            case Comparison.MARK_IS_GOOD:
                color = new Color(150, 255, 150);
                break;
            
            case Comparison.MARK_IS_BAD:
                color = new Color(255, 150, 150);
                break;
            
            case Comparison.MARK_IS_UNSURE:
                color = new Color(255, 255, 150);
                break;
            
            default:
                color = Color.white;
                break;
            }
            this.setBackground(color);
            
            if (hasFocus) {
                this.setBorder(BorderFactory.createLineBorder(new Color(128, 128, 196), 1));
            } else {
                this.setBorder(null);
            }
            
        } else {
            // TODO: special cells?
        }
        
        return this;
    }
}