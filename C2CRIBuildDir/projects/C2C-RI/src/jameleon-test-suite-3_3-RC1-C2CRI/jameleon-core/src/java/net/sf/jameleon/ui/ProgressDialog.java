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
import java.io.File;
import static java.lang.Thread.sleep;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;

public class ProgressDialog extends JDialog {

    protected int maxTimeInSeconds;
    protected JProgressBar progressBar;
    protected Thread task;
    protected boolean keepGoing = true;
    protected final int MULTIPLIER = 10;
    protected TestCaseDocsExecutor tcde;

    public ProgressDialog(JFrame rootFrame, int maxTimeInSeconds, TestCasePane testCasePane, File script){
        super(rootFrame);
        this.maxTimeInSeconds = maxTimeInSeconds;
        Point rootLocation = rootFrame.getLocation();
        int height = rootLocation.y + rootFrame.getHeight() / 2;
        int width = rootLocation.x + rootFrame.getWidth() / 2;
        setUndecorated(true);

        tcde = new TestCaseDocsExecutor(script,testCasePane);
        tcde.setContextClassLoader(Utils.createClassLoader());
        init();
        setLocation(width, height);
        tcde.setProgressDialog(this);
        beginWaitingOnTask();
        setModal(true);
        setVisible(true);
    }

    private void init(){
        JPanel progressPanel = new JPanel(new SpringLayout());
        progressBar = new JProgressBar(0, maxTimeInSeconds * MULTIPLIER);
        JLabel generating = new JLabel("Generating Documentation");
        progressPanel.add(generating);

        progressPanel.add(progressBar);
        SpringUtilities.makeCompactGrid(progressPanel,
                                        2, 1, //rows, cols  
                                        6, 6, //initX, initY
                                        6, 6); //xPad, yPad
        getContentPane().add(progressPanel);
        pack();
    }

    private void beginWaitingOnTask(){
        task = new Thread(){
            public void run() {
                int interval = 100;
                for (int i = 0; keepGoing && i <= (maxTimeInSeconds * MULTIPLIER); i++) {
                    try {
                        sleep(interval);
                        progressBar.setValue(i);
                    } catch (InterruptedException e) {
						Thread.currentThread().interrupt();
                        keepGoing = false;
                        break;
                    }
                }
            }
        };
        tcde.start();
        task.start();
    }

    public void taskCompleted(){
        keepGoing = false;
        progressBar.setValue(maxTimeInSeconds);
        dispose();
    }
}
