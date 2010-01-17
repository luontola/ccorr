/* 
 * Copyright (C) 2003-2006  Esko Luontola, www.orfjackal.net
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

package net.orfjackal.ccorr.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ToolBar for MainWindow.
 *
 * @author Esko Luontola
 */
public class MainToolBar extends JToolBar {

    private MainWindow parent;

    public MainToolBar(MainWindow m) {
        JButton button;

        this.parent = m;

        setFloatable(false);
        setRollover(true);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(153, 153, 153)));

        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/new.gif")));
        button.setToolTipText("New");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.newComparisonPanel(null);
            }
        });
        add(button);

        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/open.gif")));
        button.setToolTipText("Open");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.openComparison();
            }
        });
        add(button);

        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/close.gif")));
        button.setToolTipText("Close");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.closeCurrent();
            }
        });
        add(button);

        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/save.gif")));
        button.setToolTipText("Save");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.saveComparison();
            }
        });
        add(button);

        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/saveas.gif")));
        button.setToolTipText("Save As");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.saveComparisonAs();
            }
        });
        add(button);
    }

}