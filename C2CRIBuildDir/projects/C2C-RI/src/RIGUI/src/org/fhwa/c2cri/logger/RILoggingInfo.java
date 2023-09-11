/**
 * 
 */
package org.fhwa.c2cri.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.xml.XMLLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Maintains the RI logging settings to be used during RI operation and testing.
 * It also provides for log4j configuration for logging to the RI log file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RILoggingInfo implements Serializable{

	/** flag which indicates that all Actions and Messages should be stored in the log file. **/
	private boolean logActionsAndMessages;
	
	/** flag which indicates that only Messages should be logged *. */
	private boolean logMessagesOnly;
	
	/** flag which indicates that only failure events should be logged *. */
	private boolean logFailuresOnly;

        /** The ri appender. */
        private FileAppender riAppender;
        
        /** The log. */
        private Logger log;
        
        /** The log file. */
        private String logFile;


        //Basic Constructor for the RILoggingInfo Class
        /**
         * Instantiates a new rI logging info.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        public RILoggingInfo(){
            this(false, false, true);
        }
        
        //Constructor with arguments
        /**
         * Instantiates a new rI logging info.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param messagesOnly the messages only
         * @param failuresOnly the failures only
         * @param actionsAndMessages the actions and messages
         */
        public RILoggingInfo(boolean messagesOnly, boolean failuresOnly, boolean actionsAndMessages){
            this.logActionsAndMessages = actionsAndMessages;
            this.logFailuresOnly = failuresOnly;
            this.logMessagesOnly = messagesOnly;
        }
	
	/**
	 * Checks if is log actions and messages.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the logActionsAndMessages
	 */
	public boolean isLogActionsAndMessages() {
		return logActionsAndMessages;
	}
	
	/**
	 * Sets the log actions and messages.
	 *
	 * @param logActionsAndMessages the logActionsAndMessages to set
	 */
	public void setLogActionsAndMessages(boolean logActionsAndMessages) {
		this.logActionsAndMessages = logActionsAndMessages;
	}
	
	/**
	 * Checks if is log failures only.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the logFailuresOnly
	 */
	public boolean isLogFailuresOnly() {
		return logFailuresOnly;
	}
	
	/**
	 * Sets the log failures only.
	 *
	 * @param logFailuresOnly the logFailuresOnly to set
	 */
	public void setLogFailuresOnly(boolean logFailuresOnly) {
		this.logFailuresOnly = logFailuresOnly;
	}
	
	/**
	 * Checks if is log messages only.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the logMessagesOnly
	 */
	public boolean isLogMessagesOnly() {
		return logMessagesOnly;
	}
	
	/**
	 * Sets the log messages only.
	 *
	 * @param logMessagesOnly the logMessagesOnly to set
	 */
	public void setLogMessagesOnly(boolean logMessagesOnly) {
		this.logMessagesOnly = logMessagesOnly;
	}
	
	/**
	 * verifies that the parameters that are to be set are valid.
	 */
	public void verifyLoggingParameters(){
	}

        /**
         * Sets up log4j logging parameters for the test.  log4j can be set to log various test levels.  Based on the logging parameters these levels will be set appropriately.
         * Next this method,
         * logs the current Test Configuration in the Test Log
         * logs  the name of the Test Operator in the Test Log
         * logs  the unique test name in the Test Log
         * logs the current date and time in the Test Log
         * logs any descriptive test notes in the Test Log
         *
         * @param logFileName the log file name
         * @param testConfiguration the test configuration
         * @param logDescription the log description
         */
	public void configureLogging(String logFileName,org.fhwa.c2cri.testmodel.TestConfiguration testConfiguration, String logDescription){

                     try{

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        Date date = new Date();

                        logFile = logFileName + "." + dateFormat.format(date)+".xml";

                        riAppender = new FileAppender();
                        log = Logger.getLogger("net.sf.jameleon");
                        log.addAppender(riAppender);
                        riAppender.setName("STDOUT");
                        riAppender.setFile(logFile);
                        XMLLayout xmlLayout = new XMLLayout();
                        riAppender.setLayout(xmlLayout);
                        riAppender.activateOptions();
                        System.out.println("Set the file for riAppender to --> " + logFile);
                    	System.out.println("STDOUT riAppender was created and successfully activated");

                        String initLogEvent = "";
                        initLogEvent = initLogEvent+"<initEvent>";
                        initLogEvent = initLogEvent+"<fileName>"+logFile+"</fileName>";
                        initLogEvent = initLogEvent+"<startTime>"+dateFormat.format(date)+"</startTime>";
                        initLogEvent = initLogEvent+"<creator>"+System.getProperty("user.name")+"</creator>";
                        initLogEvent = initLogEvent+"<description>"+ logDescription+"</description>";
                        initLogEvent = initLogEvent+"</initEvent>";
                        log.info(initLogEvent);

                     }
                     catch (Exception ex) {
                        	System.out.println("No STDOUT Appender was found");
                     }

        }

        /**
         * Stop logging.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        public void stopLogging(){
            //remove the custom appender to stop logging to the file.
            log.removeAppender(riAppender);
                        System.out.println("Now Altering file "+ logFile + " to remove log4j: references");
//                        readReplace(logFile, "log4j:", "");
                        System.out.println("Now Finished creating the LogXML file "+ logFile + ".xml");
        }

	/**
	 * Log event.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param theEvent the the event
	 */
	public static void logEvent(String theEvent){
            Logger log = Logger.getLogger("net.sf.jameleon");
            log.info(theEvent);
        }

        /**
         * Replaces the log4j: namespace references from the log file and creates a new
         * version which is a well formed XML Document.
         *
         * @param fname the fname
         * @param oldPattern the old pattern
         * @param replPattern the repl pattern
         */
        public static void readReplace(String fname, String oldPattern, String replPattern){
    	String line;
    	StringBuffer sb = new StringBuffer();
    	try {
    		try (BufferedReader reader=new BufferedReader ( new InputStreamReader(new FileInputStream(fname))))
			{
				while((line = reader.readLine()) != null) {
					line = line.replaceAll(oldPattern, replPattern);
					sb.append(line+"\n");
				}
			}
    		try (BufferedWriter out=new BufferedWriter ( new FileWriter(fname)))
			{
                out.write("<?xml version=\"1.0\" ?>\n");
                out.write("<logFile>\n");
                out.write("<eventSet version=\"1.2\" xmlns:log4j=\"http://jakarta.apache.org/log4j/\">");
                out.write(sb.toString());
                out.write("</eventSet>\n");
                out.write("</logFile>\n");
			}
    	}
    	catch (Throwable e) {
    	            System.err.println("*** exception ***");
    	}
    }



}
