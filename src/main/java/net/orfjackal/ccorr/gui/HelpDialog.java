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

import net.orfjackal.ccorr.Settings;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Help dialog.
 *
 * @author Esko Luontola
 */
public class HelpDialog extends JDialog implements HyperlinkListener {

    private static int HELP_WIDTH = 500;

    private static HelpDialog openedDialog;

    private JEditorPane help;

    public HelpDialog(Frame parent) {
        super(parent);

        setTitle(Settings.APP_NAME_SHORT + " Help");
        setModal(false);
        setResizable(true);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createHelp(), "Center");
        getContentPane().add(createButtons(), "South");

        pack();
        positionWindows(parent, this);
        setVisible(true);

        if (openedDialog != null) {
            openedDialog.close();
        }
        openedDialog = this;
    }

    private void positionWindows(Window parent, Window child) {
        Rectangle p = parent.getBounds();
        Rectangle c = child.getBounds();
        Rectangle max = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        // right from parent
        child.setLocation(
                p.x + p.width,
                p.y
        );
        child.setSize(
                HELP_WIDTH,
                p.height
        );

        // check if outside the screen
        if (c.x + c.width >= max.width) {
            int moveX = max.width - (c.x + c.width);
            parent.setLocation(p.x + moveX, p.y);
            child.setLocation(c.x + moveX, p.y);
        }
    }

    private JComponent createHelp() {
        try {
            help = new JEditorPane(ClassLoader.getSystemResource("help/index.html"));
        } catch (IOException e) {
            e.printStackTrace();
            return new JLabel("Error in opening " + ClassLoader.getSystemResource("help/index.html"));
        }
        help.setEditable(false);
        help.addHyperlinkListener(this);

        return new JScrollPane(help);
    }

    private JComponent createButtons() {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.RIGHT);
        JPanel p = new JPanel(layout);

        JButton button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        p.add(button);

        return p;
    }

    private void close() {
        setVisible(false);
        dispose();
        openedDialog = null;
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                help.setPage(e.getURL());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}