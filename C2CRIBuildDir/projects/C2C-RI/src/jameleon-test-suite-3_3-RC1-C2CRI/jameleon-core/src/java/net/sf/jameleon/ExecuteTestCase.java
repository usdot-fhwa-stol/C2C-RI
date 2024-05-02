/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)

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
package net.sf.jameleon;

import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.event.TestRunEventHandler;
import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.reporting.ReporterUtils;
import net.sf.jameleon.reporting.ResultsReporter;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;
import net.sf.jameleon.util.JameleonUtility;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


/**
 * Executes a Jameleon Test Case.
 */
public class ExecuteTestCase {

    /**
     * A list of files to execute
     */
    protected final List files = new LinkedList();
    /**
     * A list of files to execute
     */
    protected final List testCaseListeners = new LinkedList();
    /**
     * A set of key-value variables to be set in the context
     */
    protected Map contextVars;
    /**
     * Print out stack trace of each failed Jameleon Test Case
     * to stdout.
     */
    protected boolean debug = false;
    /**
     * Print the name of the script being executed to stdout
     */
    protected boolean printFooter = true;
    /**
     * The # of milliseconds to wait between each script execution
     */
    protected long waitTimeBetweenScripts = 0;
    /**
     * A line of dashes
     */
    protected static final String DASH = "\n-------------------------------------------------------------\n";
    /**
     * A line of underscores
     */
    protected static final String US = "\n_______________________________________________________________\n";
    protected final Logger log = LogManager.getLogger(ExecuteTestCase.class.getName());
    private ResultsReporter reporter;

    protected final static TestCaseTagLibrary tagLibrary = new TestCaseTagLibrary();
    /**
     * The jameleon.conf configuration name for a list of desired TestCaseListeners to be registered.
     */
    public static final String TEST_CASE_LISTENERS = "TestCaseListeners";

    public ExecuteTestCase(){
        contextVars = new HashMap();
		((LoggerContext)LogManager.getContext(false)).setConfigLocation(new File("log4j.properties").toURI());
        log.info("Check for Loaded the Config File - plain");
    }
    
    public ExecuteTestCase(boolean debug){
        this();
        this.debug = debug;
//        PropertyConfigurator.configure("log4j.properties");
        log.info("Check for Loaded the Config File - with Boolean");
    }

    /**
     * Read in desired TestCaseListeners defined in jameleon.conf and register them
     * in the TestCaseEventHandler.
     */
    public void registerEventListeners(){
        registerEventListeners(true);
    }

    public ResultsReporter getResultsReporter(){
        return reporter;
    }
    
    /**
     * Read in desired TestCaseListeners defined in jameleon.conf and register them
     * in the TestCaseEventHandler.
     * @param useCurrentThread if set to true, then use the current thread's classloader
     */
    public void registerEventListeners(boolean useCurrentThread){
        //Add the ResultsReporter as test case and test run listener
        beginTestRun();
        String[] tcListeners = Configurator.getInstance().getValueAsArray(TEST_CASE_LISTENERS);
        TestCaseListener tcListener;
        Class c;
        for (int i = 0; tcListeners != null && i < tcListeners.length; i++) {
            try{
                //This is a hack to get this working under both Ant and the GUI.
                //When not running in a separate VM for Ant, it prefers the Class.forName().
                if (useCurrentThread) {
                    c = Thread.currentThread().getContextClassLoader().loadClass(tcListeners[i]);
                }else{
                    c = Class.forName(tcListeners[i]);
                }
                tcListener = (TestCaseListener)c.newInstance();
                //Store it for later removal
                registerEventListener(tcListener);
            }catch(Exception e){
                e.printStackTrace();
                System.err.println("Could not register TestCaseListeners: "+e.getMessage());
            }
        }
    }

    /**
     * Register a new TestCaseListener
     * in the TestCaseEventHandler.
     * @param tcListener The TestCaseListener to register
     */
    public void registerEventListener(TestCaseListener tcListener){
        if (tcListener != null) {
            TestCaseEventHandler tcHandler = TestCaseEventHandler.getInstance();
            testCaseListeners.add(tcListener);
            tcHandler.addTestCaseListener(tcListener);
        }
    }

    /**
     * Remove all TestCaseListeners registered via the registerEventListeners method.
     */
    public void deregisterEventListeners(){
        endTestRun();
        TestCaseListener listener;
        for (Iterator it = testCaseListeners.iterator(); it.hasNext(); ) {
            listener = (TestCaseListener)it.next();
            TestCaseEventHandler.getInstance().removeTestCaseListener(listener);
        }
    }

    public void setPrintFooter(boolean printFooter){
        this.printFooter = printFooter;
    }

    public void setWaitTimeBetweenScripts(long waitTimeBetweenScripts){
        this.waitTimeBetweenScripts = waitTimeBetweenScripts;
    }

    public long getWaitTimeBetweenScripts(){
        return waitTimeBetweenScripts;
    }

    /**
     * Begins the test run. This creates a new html reporter which in turn ends up
     * generating another results file.
     */
    public void beginTestRun(){
/**
        Calendar startTime = Calendar.getInstance();
        try{
            File resultsDir = ReporterUtils.getResultsDir();
            File resultsFile = new File(ResultsReporter.getResultsDir(resultsDir, startTime),
                    "TestResults.html");
            File testRunSummaryFile = new File(resultsDir, JameleonDefaultValues.TEST_RUN_SUMMARY_FILE_NAME);
            JameleonUtility.createDirStructure(resultsFile.getParentFile());
            reporter = ResultsReporter.getInstance();
            reporter.getHtmlTestRunReporter().setWriter(new FileWriter(resultsFile));
            reporter.getHtmlTestRunSummaryReporter().setPrintHeader(testRunSummaryFile.length() == 0);
            
            reporter.getHtmlTestRunSummaryReporter().setWriter(new FileWriter(testRunSummaryFile, true));
        }catch(IOException ioe){
            throw new JameleonException("Could not configure Jameleon to write out results file", ioe);
        }
        TestCaseEventHandler.getInstance().addTestCaseListener(reporter);
        TestRunEventHandler.getInstance().addTestRunListener(reporter);
        TestRunEventHandler.getInstance().beginTestRun(startTime);
 */
    }

    /**
     * Ends the test run.
     */
    public void endTestRun(){
/**
        TestRunEventHandler.getInstance().endTestRun(Calendar.getInstance());
        TestCaseEventHandler.getInstance().removeTestCaseListener(reporter);
        TestRunEventHandler.getInstance().removeTestRunListener(reporter);
        try {
            reporter.getHtmlTestRunReporter().getWriter().close();
        } catch (IOException e) {
            System.err.println("Error closing test results summary writer");
            e.printStackTrace();
        }
 */
    }

    /*
     * Runs through a list of files or directories (args[]),
     * executing each one.
     */
    public static void main (String args[]) {
        StringBuffer errMsg = new StringBuffer();
        ExecuteTestCase exec = new ExecuteTestCase();
        exec.registerEventListeners();
        boolean printFooterStatic = true;
        int startingPoint = 0;
        if (args.length > 0 && args[0].equalsIgnoreCase("false")) {
            printFooterStatic = false;
            startingPoint = 1;
        }
        if (args.length > startingPoint) {
            File f = null;
            for (int i = startingPoint; i < args.length; i++) {
                f  = new File(args[i]);
                if (f.exists()) {
                    exec.addFile(f);
                }
            }
        }
        errMsg.append(exec.executeFiles());
        try{
            if ( errMsg.length() > 0 ) {
                System.out.println("The following test cases failed:"+errMsg.toString());
                System.out.println("");
                System.out.println("");
                throw new JameleonException("See 'TestResults.xml' and 'TestResults.html' for a more detailed reason (stack trace) as why the test case(s) failed.");
            }
        }finally{
            exec.deregisterEventListeners();
            if (printFooterStatic) {
                closeAllLogs();
            }
        }
    }

    /**
     * This is a workaround to the fact the shutdown is not called in log4j.
     */
    public static void closeAllLogs(){
		LogManager.shutdown();
    }

    /**
     * Runs through all files in the given file or directory
     * and executes each file as a Jameleon test script.
     * @param f The file to execute
     * @return the error message if one exists
     */
    public String execute(File f) {
        StringBuffer errMsg = new StringBuffer();
        if (f.isDirectory()) {
            File[] filesA = f.listFiles();
            for (int i = 0; i < filesA.length; i++) {
                if (filesA[i].isDirectory()) {
                    errMsg.append(execute(filesA[i]));
                }else if(filesA[i].isFile() && filesA[i].getName().endsWith(".xml")){
                    errMsg.append(executeJellyScript(filesA[i]));
                }else{
                    System.out.println("Skipping "+filesA[i].getAbsolutePath());
                }
            }
        }else if(f.isFile() && f.getName().endsWith(".xml")){
            errMsg.append(executeJellyScript(f));
        }else{
            System.out.println("Skipping "+f.getAbsolutePath());
        }
        return errMsg.toString();
    }

    public String executeFiles(){
        StringBuffer errMsg = new StringBuffer();
        Iterator it = getFiles().iterator();
        while (it.hasNext()) {
            File f = (File)it.next();
            errMsg.append(execute(f));
            if (it.hasNext()) {
                delay();
            }
        }
        return errMsg.toString();
    }

    /**
     * Executes the given file as a Jameleon test script and returns the error message if the test failed.
     * Only a single file should be passed to this method.
     * @param file - the file to execute.
     * @return a String representing the error(s), if any, that occured
     */
    public String executeJellyScript(File file){
        String errMsg = "";
        JellyContext context = null;
        try{
            context = runScript(file);
        }catch(FileNotFoundException fnfe){
            errMsg = "Could not execute \""+file.getPath() + "\"! File NOT FOUND!";
            return errMsg;
        }catch(Exception je){
            if ( debug ) {
                je.printStackTrace();
            }
            errMsg = US + file + DASH + "CAUSE:\n" + je.getMessage();
        }finally{
            if (context != null) {
                context.getVariables().clear();
                context.clear();
                context = null;
            }
        }
        return errMsg;
    }

    /**
     * Executes the given file as a Jameleon test script and returns the error message if the test failed.
     * Only a single file should be passed to this method.
     * @param urlFile - the file to execute specified as a URL.
     * @return a String representing the error(s), if any, that occured
     */
    public String executeJellyScript(URL urlFile){
        String errMsg = "";
        JellyContext context = null;
        try{
            context = runScript(urlFile);
        }catch(FileNotFoundException fnfe){
            errMsg = "Could not execute \""+urlFile.getPath() + "\"! File NOT FOUND!";
            return errMsg;
        }catch(Exception je){
            if ( debug ) {
                je.printStackTrace();
            }
            errMsg = US + urlFile + DASH + "CAUSE:\n" + je.getMessage();
        }finally{
            if (context != null) {
                context.getVariables().clear();
                context.clear();
                context = null;
            }
        }
        return errMsg;
    }

     public JellyContext runScript(File file) throws JellyException, FileNotFoundException{
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath() +" not found!");
        }
        XMLOutput out = XMLOutput.createDummyXMLOutput();
        JellyContext context = new JellyContext();
        //This seems to cause jelly to max out on memory if set.
        //context.setCacheTags(false);
        String fileName = file.getPath();
        JellyContext scriptContext = null;
        try{
            context.registerTagLibrary("jelly:jameleon", tagLibrary);
            setContextVariables(context);
            scriptContext = context.runScript(file, out);
        }finally{
            try{
                out.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
            out = null;
        }
        return scriptContext;
    }

/**
 * This method is added to allow for the running of a script referenced as a URL
 * instead of a File object.  It is basically the same method as the runScrpt method included in
 * ExecuteTestCase, with that one exception.
 *
 * @param file - a URL for the script file to be run.
 * @return - JellyContext
 * @throws JellyException
 * @throws FileNotFoundException
 */
   public JellyContext runScript(URL file) throws JellyException, FileNotFoundException{
       // See if the URL is valid before running the script.
       try {
         InputStream is =  file.openStream();
         is.close();
       } catch (Exception e){
            throw new FileNotFoundException(file.getPath() +" not found!");
       }
        XMLOutput out = XMLOutput.createDummyXMLOutput();
        JellyContext context = new JellyContext();
        //This seems to cause jelly to max out on memory if set.
        //context.setCacheTags(false);
        JellyContext scriptContext = null;
        try{
            context.registerTagLibrary("jelly:jameleon", tagLibrary);
            setContextVariables(context);
            scriptContext = context.runScript(file, out);
        }finally{
            try{
                out.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
            out = null;
        }
        return scriptContext;
    }

    protected void setContextVariables(JellyContext context){
        if (contextVars.size() > 0) {
            context.setVariables(contextVars);
        }
    }

    /**
     * Adds a file or directory to the list of files to be executed by this instance.
     * @param f The file to add
     */
    public void addFile(File f){
        files.add(f);
    }

    /**
     * @return a List of files and/or directories to be executed by this instance.
     */
    public List getFiles(){
        return files;
    }

    public Map getContextVars(){
        return contextVars;
    }


    protected void delay(){
		if (waitTimeBetweenScripts > 0)
		{
			synchronized (this)
			{
				try
				{
					while (waitTimeBetweenScripts > 0)
					{
						this.wait(waitTimeBetweenScripts);
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}
    }

    public void setContextVariablesforRI(JellyContext context){
//            context.setVariables(contextVars);
            contextVars.putAll(context.getVariables());
    }
    
    
}

