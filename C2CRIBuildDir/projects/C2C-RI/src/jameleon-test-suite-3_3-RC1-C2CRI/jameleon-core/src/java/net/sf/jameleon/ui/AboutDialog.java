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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class AboutDialog extends JDialog{
	private static final long serialVersionUID = 1L;

    public AboutDialog(JFrame rootFrame){
        super(rootFrame, "About Jameleon", true);
        init();
        setVisible(true);
    }

    private void init(){
        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(new JLabel("Description:"));
        b.add(new JLabel("<html><blockquote>Jameleon is a data-driven automated testing tool that is easily "+
                         "extensible via plug-ins. Features of applications are automated in Java and tied "+
                         "together independently in XML, creating self-documenting automated test cases."+
                         "</blockquote></html>"));

        ResourceBundle rs = ResourceBundle.getBundle("jameleon-core-version");
        b.add(new JLabel("Version: "+rs.getString("version")));
        b.add(Box.createGlue());
        getContentPane().add(b, "Center");
    
        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        getContentPane().add(p2, "South");
    
        ok.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              setVisible(false);
              dispose();
          }
        });
    
        setSize(350, 250);
    }

}
