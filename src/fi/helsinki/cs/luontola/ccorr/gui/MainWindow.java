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

/**
 * Corruption Corrector's main window. It consists of menus, a toolbar and a
 * <code>JTabbedPane</code> that holds all the program's views.
 *
 * @version     1.00, 2003-02-06
 * @author      Esko Luontola
 */
public class MainWindow extends JFrame {
    
    /**
     * An instance of <code>MainWindow</code> for the static methods.
     */
    private static MainWindow main;
    
    /**
     * The central <code>JTabbedPane</code>.
     */
    private final JTabbedPane tabbedPane;
    
//  private final JLabel statusbar;
    
    /**
     * Creates a new <code>MainWindow</code>. This should be run only once
     * when the program starts.
     */
    public MainWindow() {
        main = this;
        
        JButton button;
        JMenu menu;
        JMenuBar menuBar;
        JMenuItem menuItem;
        JPanel panel;
        JToolBar toolBar;
        
        setTitle(Settings.APP_NAME + " " + Settings.VERSION_NUMBER);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        try {
        	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    	} catch (Exception e) {}
        
        /*
         * MENU BAR
         */
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        // FILE MENU
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("New Comparison", KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newComparisonPanel(null);
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Open Comparison", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openComparison();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Create Checksum File", KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newCreateChecksumFilePanel();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Close", KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeCurrent();
            }
        });
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Save Comparison", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveComparison();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save Comparison As", KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveComparisonAs();
            }
        });
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        });
        menu.add(menuItem);
        
        // EDIT MENU
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Mark Good", KeyEvent.VK_G);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedComponent() instanceof ComparisonPanel) {
                    ComparisonPanel panel = (ComparisonPanel) (tabbedPane.getSelectedComponent());
                    panel.markSelectedGood();
                }
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Mark Bad", KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedComponent() instanceof ComparisonPanel) {
                    ComparisonPanel panel = (ComparisonPanel) (tabbedPane.getSelectedComponent());
                    panel.markSelectedBad();
                }
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Mark Unsure", KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedComponent() instanceof ComparisonPanel) {
                    ComparisonPanel panel = (ComparisonPanel) (tabbedPane.getSelectedComponent());
                    panel.markSelectedUnsure();
                }
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Mark Undefined", KeyEvent.VK_D);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedComponent() instanceof ComparisonPanel) {
                    ComparisonPanel panel = (ComparisonPanel) (tabbedPane.getSelectedComponent());
                    panel.markSelectedUndefined();
                }
            }
        });
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Next Marker", KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getSelectedComponent() instanceof ComparisonPanel) {
                    ComparisonPanel panel = (ComparisonPanel) (tabbedPane.getSelectedComponent());
                    panel.markSelectedNext();
                }
            }
        });
        menu.add(menuItem);
        
        /*
         * TOOL BAR
         */
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(153, 153, 153)));
        getContentPane().add(toolBar, BorderLayout.NORTH);
        
        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/new.gif")));
        button.setToolTipText("New Comparison");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newComparisonPanel(null);
            }
        });
        toolBar.add(button);
        
        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/open.gif")));
        button.setToolTipText("Open Comparison");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openComparison();
            }
        });
        toolBar.add(button);
        
        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/checksum.gif")));
        button.setToolTipText("Create Checksum File");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newCreateChecksumFilePanel();
            }
        });
        toolBar.add(button);
        
        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/close.gif")));
        button.setToolTipText("Close");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeCurrent();
            }
        });
        toolBar.add(button);
        
        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/save.gif")));
        button.setToolTipText("Save Comparison");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveComparison();
            }
        });
        toolBar.add(button);
        
        button = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/saveas.gif")));
        button.setToolTipText("Save Comparison As");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveComparisonAs();
            }
        });
        toolBar.add(button);
        
        /*
         * STATUS BAR
         */
        // TODO: class StatusBar
//      panel = new JPanel();
//      panel.setBorder(BorderFactory.createLoweredBevelBorder());
//      getContentPane().add(panel, BorderLayout.SOUTH);
//      statusbar = new JLabel("Status Bar");
//      statusbar.setFont(statusbar.getFont().deriveFont(Font.PLAIN));
//      panel.add(statusbar);
        
        /*
         * MAIN CONTENT
         */
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        //tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        newComparisonPanel(null);
        
        // TEST:
//      newComparisonPanel(Comparison.loadFromFile(new File("F:\\oht\\dummy.ccp")));
//      newCreateChecksumFilePanel();
        
        
        /*
         * ACTION LISTENERS
         */
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitProgram();
            }
        });
    }
    
    /**
     * Returns the central <code>JTabbedPane</code>.
     *
     * @return	the central <code>JTabbedPane</code>
     */
    public static JTabbedPane getTabbedPane() {
        return main.tabbedPane;
    }
    
    /**
     * Used to create a unique name for each <code>ComparisonPanel</code>.
     */
    private int comparisonNumber = 0;
    
    /**
     * Opens a <code>ComparisonPanel</code> to the central <code>JTabbedPane</code>.
     *
     * @param	c	the <code>Comparison</code> to be opened, 
     *				or null to create a new one
     */
    public void newComparisonPanel(Comparison c) {
        if (c != null) {
            
            // try opening to an empty tab
            int lastIndex = tabbedPane.getTabCount() - 1;
            if (lastIndex >= 0) {
                
                // open to selected index?
                if (tabbedPane.getSelectedComponent() instanceof ComparisonPanel) {
                    Component comp = tabbedPane.getSelectedComponent();
                    
                    ComparisonPanel cp = (ComparisonPanel) (comp);
                    ComparisonTableModel model = cp.getTableModel();
                    if (!model.isModified() && model.getFiles() == 0) {
                        ComparisonPanel panel = new ComparisonPanel(c);
                        tabbedPane.setComponentAt(tabbedPane.indexOfComponent(comp), panel);
                        tabbedPane.setSelectedComponent(panel);
                        return;
                    }
                }
                
                // open to the last index?
                if (tabbedPane.getComponentAt(lastIndex) instanceof ComparisonPanel) {
                    Component comp = tabbedPane.getComponentAt(lastIndex);
                    
                    ComparisonPanel cp = (ComparisonPanel) (comp);
                    ComparisonTableModel model = cp.getTableModel();
                    if (!model.isModified() && model.getFiles() == 0) {
                        ComparisonPanel panel = new ComparisonPanel(c);
                        tabbedPane.setComponentAt(tabbedPane.indexOfComponent(comp), panel);
                        tabbedPane.setSelectedComponent(panel);
                        return;
                    }
                }
            }
        } else {
            c = new Comparison();
        }
        
        // create a new tab
        comparisonNumber++;
        ComparisonPanel panel = new ComparisonPanel(c);
        tabbedPane.addTab("Comparison " + comparisonNumber, panel);
        tabbedPane.setSelectedComponent(panel);
    }
    
    /**
     * Saves the the currenty selected <code>ComparisonPanel</code> (if selected) 
     * by calling <code>ComparisonPanel.saveComparison()</code>.
     */
    public void saveComparison() {
        Component comp = tabbedPane.getSelectedComponent();
        if (comp instanceof ComparisonPanel) {
            ComparisonPanel cp = (ComparisonPanel) (comp);
            cp.saveComparison();
        }
    }
    
    /**
     * Saves the the currenty selected <code>ComparisonPanel</code> (if selected) 
     * by calling <code>ComparisonPanel.saveComparisonAs()</code>.
     */
    public void saveComparisonAs() {
        Component comp = tabbedPane.getSelectedComponent();
        if (comp instanceof ComparisonPanel) {
            ComparisonPanel cp = (ComparisonPanel) (comp);
            cp.saveComparisonAs();
        }
    }
    
    /**
     * Shows a file dialog and asks to open a <code>Comparison</code> from a file.
     */
    public void openComparison() {
        JFileChooser chooser = new JFileChooser();
        String[] ext = { "ccp" };
        GeneralFileFilter filter = new GeneralFileFilter(ext, "CCorr Comparison Projects");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File("*.ccp"));
        chooser.setCurrentDirectory(Settings.getCurrentDirectory());
        
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            
            Comparison comp = Comparison.loadFromFile(chooser.getSelectedFile());
            Settings.setCurrentDirectory(chooser.getSelectedFile());
            if (comp != null) {
                newComparisonPanel(comp);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "The file " + chooser.getSelectedFile().getName() 
                        + " was not recognized as a valid CCorr Comparison Project.", 
                        "File Format Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Opens a new <code>CreateChecksumFilePanel</code> to a new tab.
     */
    public void newCreateChecksumFilePanel() {
        CreateChecksumFilePanel panel = new CreateChecksumFilePanel();
        tabbedPane.addTab("Create Checksum File", panel);
        tabbedPane.setSelectedComponent(panel);
    }
    
    /**
     * Closes the currently selected tab.
     */
    public boolean closeCurrent() {
        Component comp = tabbedPane.getSelectedComponent();
        
        if (comp instanceof TabPanel) {
            TabPanel tp = (TabPanel) (comp);
            return tp.close(); // if successful or if was cancelled
            
        } else { // for tabs that are not instanceof TabPanel
            main.tabbedPane.remove(comp);
            return true;
        }
    }
    
    /**
     * Exits the program after saving the settings.
     */
    public static void exitProgram() {
        
        // close open files, prompts automatically for saving modified files
        while (main.tabbedPane.getTabCount() > 0) {
            if (main.closeCurrent() == false) {
                return;     // operation cancelled
            }
        }
        
        // save settings
//      Rectangle bounds = ;
        Settings.setWindowBounds(main.getBounds());
//      Settings.setWindowPosition(bounds.x, bounds.y);
//      Settings.setWindowSize(bounds.width, bounds.height);
        Settings.saveSettings();
        System.exit(0);
    }
    
    /**
     * Starts the program.
     */
    public static void main(String[] args) {
        MainWindow frame = new MainWindow();
        frame.pack();
        
        // window size and position
        Rectangle bounds = Settings.getWindowBounds();
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds(); 
        if (bounds.x < 0) {
            bounds.x = 0;
        }
        if (bounds.y < 0) {
            bounds.y = 0;
        }
        if (bounds.width > maxBounds.width) {
            bounds.width = maxBounds.width;
        }
        if (bounds.height > maxBounds.height) {
            bounds.height = maxBounds.height;
        }
        if (bounds.x + bounds.width > maxBounds.width) {
            bounds.x = maxBounds.width - bounds.width;
        }
        if (bounds.y + bounds.height > maxBounds.height) {
            bounds.y = maxBounds.height - bounds.height;
        }
        frame.setBounds(bounds);
        frame.setVisible(true);
    }
}