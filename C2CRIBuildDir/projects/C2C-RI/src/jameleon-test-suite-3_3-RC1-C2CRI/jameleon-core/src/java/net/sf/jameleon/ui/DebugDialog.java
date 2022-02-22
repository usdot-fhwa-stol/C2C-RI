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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

public abstract class DebugDialog extends JDialog{

    private TestCaseResultsPane resultsPane;
    private boolean step;

    public DebugDialog(Object source, JFrame rootFrame, TestCaseResultsPane tcrf, String title){
        super(rootFrame);
        setBreakPointSource(source);
        resultsPane = tcrf;
        Point rootLocation = rootFrame.getLocation();
        int height = (int)(rootFrame.getHeight()/2);
        int width = (int)(rootFrame.getWidth()/2);
        int x = (int)(rootLocation.getX() + width/2);
        int y = (int)(rootLocation.getY() + height/4 + 20);
        setBounds(x, y, width, height);
        setTitle(title);
        setModal(true);
        init();
        setVisible(true);
    }

    /**
     * Sets the source where the data is pulled and where the data
     * needs to be changed if any values are changed in the table.
     * @param source - The tag that represents this debug dialog
     */
    protected abstract void setBreakPointSource(Object source);
    /**
     * Gets the key-value pairs that are set via this tag
     * @return the key-value pairs that are set via this tag
     */
    protected abstract Map getAttributes();

    /**
     * Gets the description that shows up in the dialog box
     * @return the description that shows up in the dialog box
     */
    protected abstract String getTagDescription();

    /**
     * Changes the value of the given attribute's name real-time
     * @param attributeName - The name of the attribute to change
     * @param newValue - The new value to set the attribute to
     */
    protected abstract void setNewAttributeValue(String attributeName, String newValue);

    private void init(){
        final DefaultTableModel attributesM = new DefaultTableModel(new Object[]{"Variable", "Value"}, 0);
        Map attributes = getAttributes();
        Iterator it = attributes.keySet().iterator();
        while (it.hasNext()) {
            Object[] values = new Object[2];
            values[0] = it.next();
            values[1] = attributes.get(values[0]);
            attributesM.addRow(values);
        }
        JTable attTable = new JTable(attributesM);
        JPanel attPanel = new JPanel(new SpringLayout());
        JPanel bttnPanel = new JPanel(new SpringLayout());
        
        attPanel.setPreferredSize(getSize());
        attPanel.add(new JLabel("Tag: "+getTagDescription()));
        attPanel.add(new JScrollPane(attTable));
        JButton goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    registerNewAttributes(attributesM,false);
                }
                });

        bttnPanel.add(goButton);
        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    registerNewAttributes(attributesM,true);
                }
                });

        bttnPanel.add(stepButton);
        SpringUtilities.makeCompactGrid(bttnPanel,
                                        1, 2,
                                        6, 6,
                                        6, 6);
        attPanel.add(bttnPanel);
        SpringUtilities.makeCompactGrid(attPanel,
                                        3, 1,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad

        getContentPane().add(attPanel);
        
        pack();
    }

    private void registerNewAttributes(DefaultTableModel attributesM, boolean stepNextTag){
        resultsPane.setStepNextTag(stepNextTag);
        while (attributesM.getRowCount() > 0) {
            String name = (String) attributesM.getValueAt(0,0);
            Object val = attributesM.getValueAt(0,1);
            if (val instanceof String) {
                setNewAttributeValue(name, (String)val);
            }
            attributesM.removeRow(0);
        }
        dispose();
    }

    public boolean isStep(){
        return step;
    }

}
