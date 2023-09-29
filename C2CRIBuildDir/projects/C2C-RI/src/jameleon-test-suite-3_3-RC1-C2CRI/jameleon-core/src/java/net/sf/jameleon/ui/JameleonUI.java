/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005 Christian W. Hargraves (engrean@hotmail.com)

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jameleon.launch.JameleonLauncher;
import net.sf.jameleon.launch.JameleonMain;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class JameleonUI extends JFrame implements JameleonMain{
	private static final long serialVersionUID = 1L;
	protected static final int TREE_TABS_WIDTH = 195;
	protected static final int WINDOW_HEIGHT = 450;
    protected static final int WINDOW_WIDHT = 600;
    protected String jameleontitle = "Jameleon";
    protected static Logger log = Logger.getLogger(JameleonUI.class.getName());

    public static void main(String[] args){
        final JameleonUI ui = new JameleonUI();
 
        /*
         *   *****************Added for RI POC*****************8
         */
    	PropertyConfigurator.configure("log4j.properties");
        /*
         *   *****************End of Added for RI POC*****************8
         */
        
        ui.startJameleon();
    }

    public void startJameleon(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                genUI();
            }
        });
    }

    public JameleonUI(){
        super();
        setTitle(jameleontitle);
        setUpFrame();
    }

    public JameleonUI(String title){
        super(title);
        this.jameleontitle = title;
        setUpFrame();
    }

    private void setUpFrame(){
        setName(jameleontitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void genUI(){
        JameleonTagsPanel jtPanel = new JameleonTagsPanel();
        final JPanel rightPanels = new JPanel(new CardLayout());
        final TestCasePane tcPanel = new TestCasePane(this);

        rightPanels.add(tcPanel, "Test Cases");
        rightPanels.add(jtPanel, "Function Tags");

        rightPanels.setPreferredSize(new Dimension(WINDOW_WIDHT, WINDOW_HEIGHT));

        SpringUtilities.makeCompactGrid(jtPanel,
                                        7, 2,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad


        final JTabbedPane treeTabs = new JTabbedPane(JTabbedPane.BOTTOM);
        treeTabs.setPreferredSize(new Dimension(TREE_TABS_WIDTH, WINDOW_HEIGHT));

        TestCaseTree tct = new TestCaseTree(tcPanel);
        JScrollPane sp2 = new JScrollPane(tct);
        treeTabs.addTab("Test Cases", sp2);
        genMenu(tct);

        FunctionalPointTree jt = new FunctionalPointTree(jtPanel);
        JScrollPane sp = new JScrollPane(jt);
        treeTabs.addTab("Function Tags", sp);

        treeTabs.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    String componentName = treeTabs.getTitleAt(treeTabs.getSelectedIndex());
                    CardLayout rcl = (CardLayout) rightPanels.getLayout();
                    rcl.show( rightPanels, componentName);
                }
                });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeTabs, rightPanels);
        splitPane.setDividerSize(4);
        getContentPane().add(splitPane);
        pack();
        setVisible(true);
    }

    protected void genMenu(final TestCaseTree tct){
        final JFrame owner =  this;
        JMenuBar menuBar = new JMenuBar();
        JMenu tools = new JMenu("Tools");
        tools.setMnemonic(KeyEvent.VK_T);
        JMenuItem options = new JMenuItem("Options...", KeyEvent.VK_O);
        options.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new ConfigDialog(owner);
                    tct.generateScriptsTree();
                }
                });
        tools.add(options);
        JMenuItem deleteResults = new JMenuItem("Delete Results", KeyEvent.VK_D);
        final JameleonUI ui = this;
        deleteResults.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    String baseDir = Configurator.getInstance().getValue("baseDir", JameleonDefaultValues.BASE_DIR.getPath());
                    String resDir = Configurator.getInstance().getValue("resultsDir", JameleonDefaultValues.RESULTS_DIR);
                    File resDirF = new File(baseDir, resDir);
                    JameleonUtility.deleteDirStructure(resDirF);
                    JOptionPane.showMessageDialog(ui, "Results have been deleted");

                }
                });
        tools.add(deleteResults);
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About", KeyEvent.VK_A);
        about.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new AboutDialog(owner);
                }
                });
        help.add(about);
        menuBar.add(tools);
        menuBar.add(help);
        setJMenuBar(menuBar);
    }

}
