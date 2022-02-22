/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)

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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;

public class UIConfigPanel extends JPanel{

    private JTextField scriptDirF = new JTextField();
    private DefaultListModel cpEntriesM = new DefaultListModel();
    private JList cpEntriesF = new JList(cpEntriesM);

    public UIConfigPanel(){
        super(new SpringLayout());
        init();
    }

    public void init(){
        JLabel jl = new JLabel("Scripts Directory:");
        add(jl);
        scriptDirF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        jl.setLabelFor(scriptDirF);
        scriptDirF.setToolTipText("<html>The directory containing the test scripts</html>");
        
        add(scriptDirF);
        scriptDirF.setText(Configurator.getInstance().getValue(JameleonDefaultValues.SCRIPT_DIR_CONFIG_NAME,
                                                                JameleonDefaultValues.SCRIPT_DIR.getPath()));
        add(new JLabel("Classpath:"));
        add(new JLabel(""));
        JPanel cpPanel = new JPanel();
        cpPanel.setLayout(new BoxLayout(cpPanel, BoxLayout.Y_AXIS));
        populateClassPathEntries();
        JScrollPane scroll = new JScrollPane(cpEntriesF);
        cpPanel.add(scroll);
        add(cpPanel);
        setUpButtons();

        SpringUtilities.makeCompactGrid(this,
                                        3, 2,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad
    }

    private void setUpButtons(){
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        JButton add = new JButton("Add");
        final UIConfigPanel owner = this;
        add.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser(".");
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    
                    int returnVal = fileChooser.showDialog(owner, "Choose File/Directory");
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fileChooser.getSelectedFile();
                        if (f.exists()) {
                            String absolutePath = f.getAbsolutePath();
                            String relativePath = new File(".").getAbsolutePath();
                            if (relativePath.endsWith(".")) {
                                relativePath = relativePath.substring(0,relativePath.length()-2);
                            }
                            if (absolutePath.startsWith(relativePath) && 
                                absolutePath.length() > relativePath.length()) {
                                relativePath = absolutePath.substring(relativePath.length()+1);
                            }else{
                                relativePath = absolutePath;
                            }
                            if (!cpEntriesM.contains(relativePath)) {
                                cpEntriesM.addElement(relativePath.replaceAll("\\\\", "/"));
                            }
                        }
                    }
                }
                });
        JButton remove = new JButton("Remove");
        remove.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    int[] indices = cpEntriesF.getSelectedIndices();
                    for (int i = indices.length - 1; i >= 0; i--) {
                        if (indices[i] > -1) {
                            cpEntriesM.remove(indices[i]);
                        }
                    }
                }
                });
        buttonsPanel.add(add);
        buttonsPanel.add(remove);
        add(buttonsPanel);
    }

    private void populateClassPathEntries(){
        Configurator config = Configurator.getInstance();
        Iterator it = config.getKeysStartingWith(JameleonDefaultValues.ENTRIES_CONFIG_NAME).iterator();
        while (it.hasNext()) {
            cpEntriesM.addElement(config.getValue((String)it.next()));
        }
    }

    protected void updateProperties(){
        String scriptDir = scriptDirF.getText();
        Configurator config = Configurator.getInstance();
        if (JameleonDefaultValues.SCRIPT_DIR.getPath().equals(scriptDir)) {
            scriptDir = null;
        }
        config.setValue(JameleonDefaultValues.SCRIPT_DIR_CONFIG_NAME, scriptDir);
        Iterator it = config.getKeysStartingWith(JameleonDefaultValues.ENTRIES_CONFIG_NAME).iterator();
        while (it.hasNext()) {
            config.setValue((String)it.next(), null);
        }
        int size = cpEntriesM.size();
        for (int i = 0; i < size; i++) {
            config.setValue(JameleonDefaultValues.ENTRIES_CONFIG_NAME+padNumber(i+1, size), (String)cpEntriesM.get(i));
        }
    }

    protected String padNumber(int num, int maxNum){
        String numS = ""+num;
        String maxNumS = ""+maxNum;
        int numOfZeros = maxNumS.length() - numS.length();
        for (int i = 0; i < numOfZeros; i++) {
            numS = 0 + numS;
        }
        return numS;
    }

}
