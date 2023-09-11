/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005-2006 Christian W. Hargraves (engrean@hotmail.com)

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

import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.bean.TestCase;
import net.sf.jameleon.event.DataDrivableEventHandler;
import net.sf.jameleon.event.FunctionEventHandler;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;
import net.sf.jameleon.util.TemplateProcessor;
import org.apache.commons.jelly.JellyException;
import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Enumeration;

public class TestCasePane extends JPanel {

    protected JEditorPane tcDocsPane = new JEditorPane();
    protected JButton executeButton = new JButton();
    protected JButton stopExecutionButton = new JButton();
    protected JButton debugButton = new JButton();
    protected JButton resultsButton = new JButton("View All Results");
    protected JTextArea tcSourcePane = new JTextArea();
    protected TestCaseTree tcTree;
    protected TestCaseResultsPane resultsPane;
    protected JTabbedPane tabbedPane;
    protected JFrame parent;
    protected boolean stopExecution = false;
    protected BasicHtmlBrowser bugBrowser = new BasicHtmlBrowser("Bug Info");
    protected BasicHtmlBrowser resultsBrowser = new BasicHtmlBrowser("Jameleon Test Run Results");
    private static final String DOCS = "Test Case Docs";
    private static final String SOURCE = "Test Case Source";
    private static final String RESULTS = "Test Case Results";
    public static final int DOCS_INDEX = 0;
    public static final int SOURCE_INDEX = 1;
    public static final int RESULTS_INDEX = 2;
    public static final int SRC_TAB_SIZE = 2;

    public TestCasePane(JFrame parent) {
        super(new BorderLayout());
        this.parent = parent;
        resultsPane = new TestCaseResultsPane(parent);
        resultsPane.setSourceArea(tcSourcePane);
        genUI();
    }

    public JFrame getJameleonUI(){
        return parent;
    }

    public void setTestCaseTree(TestCaseTree tcTree) {
        this.tcTree = tcTree;
        resultsPane.setTestCaseTree(tcTree);
    }

    /**
     * @return ImageIcon, or null if the path was invalid.
     */
    protected ImageIcon createImageIcon(String path, String description) {
        URL imgURL = this.getClass().getResource(path);
        ImageIcon icon = null;
        if (imgURL != null) {
            icon = new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
        }
        return icon;
    }

    protected void genUI() {
        executeButton.setMnemonic(KeyEvent.VK_E);
        debugButton.setMnemonic(KeyEvent.VK_D);
        stopExecutionButton.setMnemonic(KeyEvent.VK_S);
        tcDocsPane.setEditable(false);
        tcDocsPane.setEditorKit(new HTMLEditorKit());
        tcDocsPane.addHyperlinkListener(new HyperlinkListener(){
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        bugBrowser.goToUrl(e.getURL());
                    }
                }
                });
        tcSourcePane.setEditable(false);
        tcSourcePane.setTabSize(SRC_TAB_SIZE);
        tcSourcePane.getCaret().setSelectionVisible(true);
        ExecuteButtonAction buttonListener = new ExecuteButtonAction(this);
        executeButton.addActionListener(buttonListener);
        executeButton.setIcon(createImageIcon("/icons/running.gif", "Run Test(s)"));
        executeButton.setToolTipText("Run Test(s)");

        stopExecutionButton.setIcon(createImageIcon("/icons/stop.gif", "Stop Execution"));
        stopExecutionButton.setToolTipText("Stop test case execution");
        stopExecutionButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        stopExecution = true;
                    }
                });

        debugButton.addActionListener(buttonListener);
        debugButton.setIcon(createImageIcon("/icons/debug.gif", "Debug Test(s)"));
        debugButton.setToolTipText("Debug Test(s)");
        debugButton.setName("debug button");

        resultsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String baseDir = Configurator.getInstance().getValue("baseDir", JameleonDefaultValues.BASE_DIR.getPath());
                String resDir = Configurator.getInstance().getValue("resultsDir", JameleonDefaultValues.RESULTS_DIR);
                String results = JameleonDefaultValues.TEST_RUN_SUMMARY_FILE_NAME;
                File resultsFile = new File(baseDir + File.separator + resDir + File.separator + results);
                try {
                    resultsBrowser.goToUrl( resultsFile.toURI().toURL() );
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        resultsButton.setToolTipText("View the HTML Test Run Summary Results");
        resultsButton.setName("test run results button");

        JPanel northP = new JPanel(new SpringLayout());
        northP.add(executeButton);
        northP.add(stopExecutionButton);
        northP.add(debugButton);
        northP.add(resultsButton);

        SpringUtilities.makeCompactGrid(northP,
                                        1, 4,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad
                                        
        add(northP, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

        tabbedPane.add(DOCS, new JScrollPane(tcDocsPane));
        tabbedPane.add(SOURCE, new JScrollPane(tcSourcePane));
        tabbedPane.add(RESULTS, resultsPane);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane(){
        return tabbedPane;
    }

    public void setTestCaseInfo(TestCase tc) {
        TemplateProcessor xfrmr = new TemplateProcessor("TestCaseDocsTemplate.txt");
        HashMap vars = new HashMap();
        vars.put("tc", tc);
        String bugUrl = Configurator.getInstance().getValue("bugTrackerUrl");
        if (bugUrl != null) {
            vars.put("bugTrackerUrl", bugUrl);
        }
        vars.put("printFileName", Boolean.FALSE);
        String contents = xfrmr.transformToString(vars);
        tcDocsPane.setText(contents);
        tcDocsPane.setCaretPosition(0);
        if (tabbedPane.getSelectedIndex() == RESULTS_INDEX) {
            tabbedPane.setSelectedIndex(DOCS_INDEX);
        }
    }

    public void setTestCaseSource(String contents) {
        tcSourcePane.setText(contents);
        tcSourcePane.setCaretPosition(0);
    }

    protected class ExecuteButtonAction implements ActionListener {

        TestCasePane tcPane;
        private boolean isExecuting;

        protected ExecuteButtonAction(TestCasePane tcPane) {
            this.tcPane = tcPane;
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            resultsPane.resetTable();
            JButton button = (JButton)e.getSource();
            final boolean debug = "debug button".equals(button.getName());
            final TreePath[] selectedTcs = tcTree.getSelectionPaths();
            tabbedPane.setSelectedIndex(RESULTS_INDEX);
            
            resultsPane.setVisible(true);
            stopExecution = false;
            resultsPane.proceedExecution();

 
            
            Thread t = new Thread() {
                public void run() {
                    ExecuteTestCase executor = new ExecuteTestCase();
                    executor.registerEventListeners();
 
                    
                    for (int i = 0; i < selectedTcs.length && !stopExecution; i++) {
                        Object obj = selectedTcs[i].getLastPathComponent();
                        if (obj instanceof TCTreeNode) {
                            TCTreeNode tn = (TCTreeNode)obj;
                            FileNode fn = (FileNode)tn.getUserObject();
                            if (!fn.isDir()) {

                                if (debug) {
                                    tabbedPane.setSelectedIndex(SOURCE_INDEX);
                                }


                                executeTestCase(fn, debug, executor);
                                while (isExecuting) {
                                    if (stopExecution) {                                        
                                        resultsPane.stopExecution();
                                        this.interrupt();
                                    }
                                    try{
                                        Thread.sleep(100);
                                    }catch(InterruptedException ie){
                                        //Apparently people don't like a stack trace printing out to the screen.
                                        //ie.printStackTrace();
                                    }
                                }
                                if (debug) {
                                    tabbedPane.setSelectedIndex(RESULTS_INDEX);
                                }
                            }
                        }
                    }
                    executor.deregisterEventListeners();
                }
            };

            t.setContextClassLoader(Utils.createClassLoader());
            t.start();
        }


        protected Thread executeTestCase(final FileNode fn, final boolean debug, final ExecuteTestCase executor) {
            isExecuting = true;
            Thread t = new Thread(){
                    public void run() {
                        tcTree.setTestCaseSource(fn.getFile(), false);
                        /***********************************************************
                         * Added for RI POC 
                         *
                         */
                        
                        /**
                         * Change the output of logging messages to go to the new log file.
                         */
                        boolean fileChanged = false;
                        String fileName = "";
                        
                        try{
                        Logger log = Logger.getRootLogger();
                        System.out.println("The rootLogger returned -->" + log.getName());
                        Enumeration appender_enum = log.getAllAppenders();
                        System.out.println("The first appender returned = " + appender_enum.toString());
                        FileAppender stdOutAppender = (FileAppender)log.getAppender("STDOUT");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        Date date = new Date();
                        System.out.println("The current output file is set to --> " + stdOutAppender.getFile());	
                        System.out.println("Setting the file to -->" + fn.getFile().toString());
                        fileName = fn.getFile().toString() + "-" + dateFormat.format(date);
                        stdOutAppender.setFile(fn.getFile().toString() + "-" + dateFormat.format(date));
                        stdOutAppender.activateOptions();
                    	System.out.println("STDOUT Appender was found and successfully activated");
                    	fileChanged = true;
                        }
                        catch (Exception ex) {
                        	System.out.println("No STDOUT Appender was found");
                        }
                        /***********************************************************
                         * End Added for RI POC 
                         *
                         */

                        
                        TestCaseEventHandler tcEventHandler = TestCaseEventHandler.getInstance();
                        FunctionEventHandler fEventHandler = FunctionEventHandler.getInstance();
                        DataDrivableEventHandler ddEventHandler = DataDrivableEventHandler.getInstance();
                        try {
                            if (debug) {
                                resultsPane.setDebug(true);
                            }
                            fEventHandler.addFunctionListener(resultsPane);
                            tcEventHandler.addTestCaseListener(resultsPane);
                            ddEventHandler.addDataDrivableListener(resultsPane);

                            try {
                                executor.runScript(fn.getFile());

                            
                                /***********************************************************
                                 * Added for RI POC 
                                 *
                                 */
                                if (fileChanged) {
                                   Logger templog = Logger.getRootLogger();
                                   FileAppender tempstdOutAppender = (FileAppender)templog.getAppender("STDOUT");

                                   tempstdOutAppender.close();
                                   System.out.println("Now Altering file "+ fileName + " to remove log4j: references");

                                   readReplace(fileName, "log4j:", "");
                                   createLogXML(fileName);
                                   System.out.println("Now Finished creating the LogXML file "+ fileName + ".xml");
                                   fileChanged = false;
                                
                                }
                                /***********************************************************
                                 * End Added for RI POC 
                                 *
                                 */
                            
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            } catch (JellyException je) {
                                //ignore this for now.
                            }
                        } finally {
                            resultsPane.setDebug(false);
                            fEventHandler.removeFunctionListener(resultsPane);
                            tcEventHandler.removeTestCaseListener(resultsPane);
                            ddEventHandler.removeDataDrivableListener(resultsPane);
                            isExecuting = false;

                        }
                    }
                    };
            t.start();
            return t;
        }

    }

    protected class JiffiePluginAction implements ActionListener {

        private Object jiffieUI;

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent ae) {
            if (jiffieUI == null) {
                try {

                    final Class clss = Class.forName("net.sf.jameleon.plugin.jiffie.ui.IEActionPointGenerator");
                    jiffieUI = clss.newInstance();

                    Thread t = new Thread() {
                        public void run() {
                            try {
                                Class[] args = {WindowAdapter.class};
                                Method setCloseListener = clss.getDeclaredMethod("setExternalCloseListener", args);
                                Object[] ms = {
                                    new WindowAdapter() {
                                        public void windowClosing(WindowEvent e) {
                                            jiffieUI = null;
                                        }
                                    }
                                };
                                setCloseListener.invoke(jiffieUI, ms);
                                Method genGUI = clss.getDeclaredMethod("genGUI", new Class[0]);
                                genGUI.invoke(jiffieUI, new Object[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    
    /***********************************************************
     * Added for RI POC 
     *
     */
    private void readReplace(String fname, String oldPattern, String replPattern){
    	String line;
    	StringBuffer sb = new StringBuffer();
    	try 
		{
			try (BufferedReader reader=new BufferedReader ( new InputStreamReader(new FileInputStream(fname))))
			{
				while((line = reader.readLine()) != null) {
					line = line.replaceAll(oldPattern, replPattern);
					sb.append(line+"\n");
				}
			}
			try (BufferedWriter out=new BufferedWriter ( new FileWriter(fname)))
			{
				out.write(sb.toString());
			}
    	}
    	catch (Throwable e) {
    	            System.err.println("*** exception ***");
    	}
    }

    private void createLogXML(String fname)
    {    
       File file = new File(fname+".xml");
       try (Writer output = new BufferedWriter(new FileWriter(file)))
	   {
          
          output.write("<?xml version=\"1.0\" ?>\n");
          output.write("<!DOCTYPE log4j [<!ENTITY data SYSTEM \""+fname.substring(fname.lastIndexOf("\\")+1, fname.length())+"\">]>\n");
          output.write("<logFile>\n");
          output.write("<eventSet version=\"1.2\" xmlns:log4j=\"http://jakarta.apache.org/log4j/\"> &data; </eventSet>\n");
          output.write("</logFile>\n");    
    
         System.out.println("Your file has been written");      
       } catch (Exception e){
        System.out.println("Error opening File " + fname + ": Exception: " + e.getMessage());          	
       }
    }

    /***********************************************************
     * End Added for RI POC 
     *
     */
    
    
}
