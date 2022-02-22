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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sf.jameleon.util.Configurator;

public class ConfigDialog extends JDialog{
	private static final long serialVersionUID = 1L;
    protected JTabbedPane tabbedPane;
    protected GeneralConfigPanel generalPanel;
    protected EnvironmentConfigPanel envPanel;
    protected UIConfigPanel uiConfigPanel;
    protected static final String ENV_PANEL = "Environment Settings";
    protected static final String GENERAL_PANEL = "General Settings";
    protected static final String UI_PANEL = "UI Settings";

    public ConfigDialog(JFrame rootFrame){
        super(rootFrame, "Jameleon Settings", true);
        init();
        setVisible(true);
    }

    private void init(){
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        generalPanel = new GeneralConfigPanel();
        envPanel = new EnvironmentConfigPanel();
        uiConfigPanel = new UIConfigPanel();
        tabbedPane.add(ENV_PANEL, envPanel);
        tabbedPane.add(GENERAL_PANEL, generalPanel);
        tabbedPane.add(UI_PANEL, uiConfigPanel);
        getContentPane().add(tabbedPane, "Center");

        JPanel p2 = new JPanel(new FlowLayout());
        JButton ok = new JButton("Ok");
        p2.add(ok);
        JButton cancel = new JButton("Cancel");
        p2.add(cancel);
        getContentPane().add(p2, "South");
    
        ok.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              envPanel.updateProperties();
              generalPanel.updateProperties();
              uiConfigPanel.updateProperties();
              Configurator config = Configurator.getInstance();
              OutputStream os = null;
              try{
                  Properties props = config.getProperties();
                  File f = new File(config.getConfigName());
                  if (!f.exists()) {
                      f.createNewFile();
                  }
                  os = new FileOutputStream(f);
                  props.store(os, null);
              }catch(IOException ioe){
                  ioe.printStackTrace();
              }finally{
                  if (os != null) {
                      try{
                          os.close();
                      }catch(IOException ioe){
                          //So what, we couldn't close the file?
                      }
                  }
              }
              Configurator.clearInstance();
              setVisible(false);
              dispose();
          }
        });
        cancel.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
                });
        pack();
        //setSize(400, 300);
    }

}
