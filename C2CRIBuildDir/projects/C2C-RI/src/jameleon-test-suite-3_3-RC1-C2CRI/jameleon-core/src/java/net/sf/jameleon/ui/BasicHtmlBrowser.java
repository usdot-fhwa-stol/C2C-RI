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

import org.jdesktop.jdic.browser.*;

import javax.swing.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BasicHtmlBrowser extends JFrame{

    private IWebBrowser browser;
    private boolean browserNotSupported;
    protected JButton backButton = new JButton("Back");
    protected JButton forwardButton = new JButton("Forward");

    public BasicHtmlBrowser(String title){
        super(title);
	    setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        init();
    }

    public BasicHtmlBrowser(String title, URL url){
        this(title);
        goToUrl(url);
    }

    public void goToUrl(URL url){
        if (browser != null){
            browser.setURL(url);
            setVisible(true);
        }else{
            String errMsg = "<html>Please set your default browser to IE (if under windows) or install Mozilla (if not " +
                    "under Windows) to enable this feature.</html>";
            if (browserNotSupported){
                errMsg = "<html><body>The browser functionality is not supported on your operating system.</body></html>";
            }
            JOptionPane.showMessageDialog(
                            this,
                            errMsg,
                            "Error Starting Default Browser",
                            JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void init(){
        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (browser.isBackEnabled()){
                    browser.back();
                }
            }
        });

        forwardButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (browser.isForwardEnabled()){
                    browser.forward();
                }
            }
        });


        JPanel northP = new JPanel(new SpringLayout());
        northP.add(backButton);
        northP.add(forwardButton);

        SpringUtilities.makeCompactGrid(northP,
                                        1, 2,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad


        try{
            BrowserEngineManager bem = BrowserEngineManager.instance();
            IBrowserEngine be = bem.getActiveEngine();
            if (be != null){
                browser = be.getWebBrowser();
                browser.addWebBrowserListener(new BrowserListener());
                getContentPane().add(northP, BorderLayout.NORTH);
                getContentPane().add(browser.asComponent(), BorderLayout.CENTER);
            }
            pack();
            setSize(850,600);
        }catch(UnsatisfiedLinkError err){
            browserNotSupported = true;
        }
    }

    private class BrowserListener implements WebBrowserListener{
        public void documentCompleted(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void downloadCompleted(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void downloadError(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void downloadProgress(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void downloadStarted(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void statusTextChange(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void titleChange(WebBrowserEvent event) {
            setTitle(event.getData());
        }
        public void initializationCompleted(WebBrowserEvent event) {
			// original implementation was empty
        }
        public void windowClose(WebBrowserEvent event) {
			// original implementation was empty
        }
    }
}
