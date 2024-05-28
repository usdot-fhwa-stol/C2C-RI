/**
 *
 */
package org.fhwa.c2cri.logger;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.FileAppender;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;

import org.apache.logging.log4j.core.config.Property;
import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.java.net.TrafficLogger;
import org.fhwa.c2cri.utilities.RIParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Maintains the RI logging settings to be used during RI operation and testing.
 * It also provides for log4j configuration for logging to the RI log file.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2012
 */
public class RILogging implements Serializable {

    /**
     * The ri init event.
     */
    public static String RI_INIT_EVENT = "InitEvent";

    /**
     * The ri user event.
     */
    public static String RI_USER_EVENT = "UserEvent";

    /**
     * The ri message event.
     */
    public static String RI_MESSAGE_EVENT = "MessageEvent";

    /**
     * The ri script event.
     */
    public static String RI_SCRIPT_EVENT = "ScriptEvent";

    /**
     * The ri verification event.
     */
    public static String RI_VERIFICATION_EVENT = "VerificationEvent";

    /**
     * The ri appender.
     */
    private static FileAppender riAppender;

    /**
     * The log2.
     */
    private Logger log, log2;

    /**
     * The log file.
     */
    private String logFile;

    /**
     * The ri gui appender.  
     */
    private static ActionLogAppender riGUIAppender = new ActionLogAppender("RIGUIIAppender", null, new RIXMLLayout(StandardCharsets.UTF_8), true, new Property[0]);
    static
	{
		riGUIAppender.start();
	}
    //Basic Constructor for the RILogging Class
    /**
     * Instantiates a new rI logging.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public RILogging() {
        this(false, false, true);
    }

    //Constructor with arguments
    /**
     * Instantiates a new rI logging.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param messagesOnly the messages only
     * @param failuresOnly the failures only
     * @param actionsAndMessages the actions and messages
     */
    public RILogging(boolean messagesOnly, boolean failuresOnly, boolean actionsAndMessages) {
    }

    /**
     * Sets up log4j logging parameters for the test. log4j can be set to log
     * various test levels. Based on the logging parameters these levels will be
     * set appropriately. Next this method, logs the current Test Configuration
     * in the Test Log logs the name of the Test Operator in the Test Log logs
     * the unique test name in the Test Log logs the current date and time in
     * the Test Log logs any descriptive test notes in the Test Log
     *
     * @param logFileName the log file name
     * @param configFileName the config file name
     * @param logDescription the log description
     * @param checkSum the check sum
     */
    public void configureLogging(String logFileName, String configFileName, String logDescription, String checkSum, boolean enableEmulation, boolean reinitializeEmulation) {

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();

			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			logFile = logFileName + "." + dateFormat.format(date) + ".xml";
			riAppender = FileAppender.newBuilder().withFileName(logFile)
				.setName("STDOUT").setImmediateFlush(true).setBufferedIo(true).setLayout(riGUIAppender.getLayout()).build();
            
			
            log = (Logger)LogManager.getLogger("net.sf.jameleon");
            log.addAppender(riAppender);
            log.addAppender(riGUIAppender);
            log2 = (Logger)LogManager.getLogger("org.fhwa.c2cri");
            log2.addAppender(riAppender);
            log2.addAppender(riGUIAppender);
			log.setLevel(Level.INFO);
			log2.setLevel(Level.INFO);
			
			ctx.updateLoggers();
            System.out.println("Set the file for riAppender to --> " + logFile);
            System.out.println("STDOUT riAppender was created and successfully activated");

            TrafficLogger tmpLogger = new TrafficLogger() {

                @Override
                public void log(TrafficLogger.LoggingLevel level, String trafficData) {
                    System.out.println("Logging Data !!!\n" + trafficData);
                    log2.info(trafficData);
                }

            };

            ConnectionsDirectory.getInstance().setTrafficLogger(tmpLogger);

            String initLogEvent = "";
            initLogEvent = initLogEvent + "<fileName>" + logFile + "</fileName>";
            initLogEvent = initLogEvent + "<startTime>" + dateFormat.format(date) + "</startTime>";
            initLogEvent = initLogEvent + "<creator>" + System.getProperty("user.name") + "</creator>";
            initLogEvent = initLogEvent + "<description>" + logDescription + "</description>";
            initLogEvent = initLogEvent + "<c2criVersion>" + RIParameters.RI_VERSION_NUMBER + "</c2criVersion>";
            initLogEvent = initLogEvent + "<configFileName>" + configFileName + "</configFileName>";
            initLogEvent = initLogEvent + "<checksum>" + checkSum + "</checksum>";
            initLogEvent = initLogEvent + "<enableEmulation>" + enableEmulation + "</enableEmulation>";
            initLogEvent = initLogEvent + "<reinitializeEmulation>" + reinitializeEmulation + "</reinitializeEmulation>";
            logEvent(RILogging.class.getName(), RILogging.RI_INIT_EVENT, initLogEvent);

        } catch (Exception ex) {
            System.out.println("No STDOUT Appender was found");
        }

    }

    /**
     * Stop logging test events.  This includes removing log4j appenders and digitally signing the log file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void stopLogging() {
        TestLogList.getInstance().pauseTestLogListing();
        //remove the custom appender to stop logging to the file.
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        log.removeAppender(riAppender);
        log2.removeAppender(riAppender);

		riAppender.stop();

        log.removeAppender(riGUIAppender);
        log2.removeAppender(riGUIAppender);
        riGUIAppender.close();
		
		ctx.updateLoggers();
        System.out.println("Now Altering file " + logFile + " to remove log4j: references");
        GenEnveloped xmlSigner = new GenEnveloped();
        try {
            if (readReplace(logFile, "log4j:", "")) {
                Path source = Paths.get(new File(logFile).toURI());
                Path out = Paths.get(new File(logFile + ".bak").toURI());
                Files.copy(source, out);
                xmlSigner.signFile(logFile);
                Files.deleteIfExists(out);
            }
        } catch (Exception ex) {
            
            try (StringWriter sw = new StringWriter(); 
                PrintWriter oPw = new PrintWriter(sw))
            {
                ex.printStackTrace(oPw);
                javax.swing.JOptionPane.showMessageDialog(null, "Error Signing the Log File:\n" + ex.getMessage() + "\n" + sw.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            catch (Exception oEx)
            {
                log.debug(oEx, oEx);
            }
        } catch (Error ex){
            try (StringWriter sw = new StringWriter(); 
                PrintWriter oPw = new PrintWriter(sw))
            {
                ex.printStackTrace(oPw);
                javax.swing.JOptionPane.showMessageDialog(null, "Error Signing the Log File:\n" + ex.getMessage() + "\n" + sw.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                javax.swing.JOptionPane.showMessageDialog(null,"The Application can not recover from this error and will be shutdown.  \nContact the Application Support team for assistance in recovering the Test Log file.","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            catch (Exception oEx)
            {
                log.debug(oEx, oEx);
            }
            System.exit(1);            
        } finally {
            TestLogList.getInstance().resumeTestLogListing();
        }
        System.out.println("Now Finished creating the LogXML file " + logFile + ".xml");
    }

    /**
     * Add a listener for log events that need to be reported to the GUI.
     * @param listener 
     */
    public static void addGUIListener(ActionLogAppenderListener listener){
        riGUIAppender.addListener(listener);
    }
       
    /**
     * Remove the provided listener so that they will no longer received log events.
     * @param listener 
     */
    public static void removeGUIListener(ActionLogAppenderListener listener){
        riGUIAppender.removeListener(listener);        
    }
    
    /**
     * Log the provided event.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param theEvent the the event
     */
    public static void logEvent(String theEvent) {
        Logger log = (Logger)LogManager.getLogger("net.sf.jameleon");
        log.info(theEvent);
    }

    /**
     * Log the provided event.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logName the log name
     * @param eventType the event type
     * @param theEvent the details of the log event
     */
    public static void logEvent(String logName, String eventType, String theEvent) {
        if (eventType.equals(RILogging.RI_INIT_EVENT)) {
            RILogging.logInitEvent(logName, theEvent);
        } else if (eventType.equals(RILogging.RI_MESSAGE_EVENT)) {
            RILogging.logMessageEvent(logName, theEvent);
        } else if (eventType.equals(RILogging.RI_USER_EVENT)) {
            RILogging.logUserEvent(logName, theEvent);
        } else if (eventType.equals(RILogging.RI_SCRIPT_EVENT)) {
            RILogging.logScriptEvent(logName, theEvent);
        } else if (eventType.equals(RILogging.RI_VERIFICATION_EVENT)) {
            RILogging.logVerificationEvent(logName, theEvent);
        }
    }

    /**
     * Log a test initialization event to the test log.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logName the log name
     * @param theEvent the details of the event
     */
    private static void logInitEvent(String logName, String theEvent) {
        Logger log = (Logger)LogManager.getLogger(logName);
        StringBuffer output = new StringBuffer();
        output.append("<initEvent>\n");
        output.append(theEvent);
        output.append("</initEvent>");
        log.info(output);
    }

    /**
     * Log a user event to the test log.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logName the log name
     * @param theEvent the details of the event
     */
    private static void logUserEvent(String logName, String theEvent) {
        Logger log = (Logger)LogManager.getLogger(logName);
        log.info("<userEvent>\n" + theEvent + "</userEvent>");
    }

    /**
     * Log a message event to the test log.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logName the log name
     * @param theEvent the details of the event
     */
    private static void logMessageEvent(String logName, String theEvent) {
        Logger log = (Logger)LogManager.getLogger(logName);
        log.info("<messageEvent>\n" + theEvent + "</messageEvent>");
    }

    /**
     * Log a test script event to the test log.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logName the log name
     * @param theEvent the the event
     */
    private static void logScriptEvent(String logName, String theEvent) {
        Logger log = (Logger)LogManager.getLogger(logName);
        log.info("<scriptEvent>\n" + theEvent + "</scriptEvent>");
    }

    /**
     * Log a verification event to the test log.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logName the log name
     * @param theEvent the details of the event
     */
    private static void logVerificationEvent(String logName, String theEvent) {
        Logger log = (Logger)LogManager.getLogger(logName);
        log.info("<verificationEvent>\n" + theEvent + "</verificationEvent>");
    }

    /**
     * Replaces the log4j: namespace references from the log file and creates a
     * new version which is a well formed XML Document.
     *
     * @param fname the fname
     * @param oldPattern the old pattern
     * @param replPattern the repl pattern
     */
    public static boolean readReplace(String fname, String oldPattern, String replPattern) {
        String line;
        StringBuffer sb = new StringBuffer();
        boolean successfulResult = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname),StandardCharsets.UTF_8));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname + ".tmp"),StandardCharsets.UTF_8)))
		{
            // Create a copy of the original xml file that includes additional "wrapper" tags.
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            out.write("<logFile>\n");
            out.write("<eventSet version=\"1.2\" xmlns:log4j=\"http://jakarta.apache.org/log4j/\">");
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll(oldPattern, replPattern);
                out.write(line + "\n");
                out.flush();
            }
            out.write("</eventSet>\n");
            out.write("</logFile>\n");
            } 
            catch (Throwable e) 
            {
                System.err.println("*** exception ***");
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                javax.swing.JOptionPane.showMessageDialog(null, "Error Processing the Log File:\n" + e.getMessage() + "\n" + sw.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
             
            try
            {
                // Delete the original xml file.
                Path oOldPath = Paths.get(fname);
                if (Files.deleteIfExists(oOldPath)) 
                {
                    Path oNewPath = Paths.get(fname + ".tmp");
                    // Create a new file from the temporary file with the original file name.
                    // This file will be indented for easier readability.
                    boolean result = prettyPrint(oNewPath.toString(), oOldPath.toString());

                    // If sucdessful delete the temporary file.
                    if (result) 
                    {
                        Files.move(oNewPath, oOldPath, StandardCopyOption.REPLACE_EXISTING);
                        successfulResult = true;
                    } else {
                        successfulResult = false;
                        javax.swing.JOptionPane.showMessageDialog(null, "Error Processing the Log File:\n Was not able to successfully complete the pretty print process.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Could not Delete File.");
                    javax.swing.JOptionPane.showMessageDialog(null, "Error Processing the Log File:\nCould not perform delete of old " + fname + " file.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (IOException oIoEx)
            {
                StringWriter sw = new StringWriter();
                oIoEx.printStackTrace(new PrintWriter(sw));
                javax.swing.JOptionPane.showMessageDialog(null, "Error Processing the Log File:\n" + oIoEx.getMessage() + "\n" + sw.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        return successfulResult;
    }

    /**
     * Create a "Pretty print" copy of the input xml file and save it as the output file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param xmlInput the xml input
     * @param xmlOutputFile the xml output file
     */
    private static boolean prettyPrint(String xmlInput, String xmlOutputFile) {
        String result = xmlInput;
        boolean printResult = false;

        try {
            Source xmlInputSource = new StreamSource(new File(xmlInput));
            StreamResult xmlOutput = new StreamResult(new File(xmlOutputFile));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(xmlInputSource, xmlOutput);
            printResult = true;
            try {
                if ((xmlOutput != null) && (xmlOutput.getOutputStream() != null)) {
                    xmlOutput.getOutputStream().flush();
                    xmlOutput.getOutputStream().close();
                }
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                javax.swing.JOptionPane.showMessageDialog(null, "Error Processing the Log File:\n" + ex.getMessage() + "\n" + sw.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            javax.swing.JOptionPane.showMessageDialog(null, "Error Processing the Log File:\n" + ex.getMessage() + "\n" + sw.toString(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return printResult;
    }

    /**
     * Removes the element.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param inputXML the input xml
     * @param targetElement the target element
     * @return the string
     */
    public static String removeElement(String inputXML, String targetElement) {
       

        try {
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			Document document = null;
            builder = factory.newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(inputXML.getBytes(StandardCharsets.UTF_8)));

            filterElements(document, targetElement);

            StringWriter out;
            out = new StringWriter();
            OutputFormat newFormat = new OutputFormat();
            newFormat.setOmitXMLDeclaration(true);
            XMLSerializer serializer = new XMLSerializer(out, newFormat);
            serializer.serialize(document);
            return out.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputXML;
    }

    /*
     * filter all elements whose tag name = filter
     */
    /**
     * Filter elements.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param parent the parent
     * @param filter the filter
     */
    private static void filterElements(Node parent, String filter) {
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

// only interested in elements
            if (child.getNodeType() == Node.ELEMENT_NODE) {

// remove elements whose tag name  = filter
// otherwise check its children for filtering with a recursive call
                if (child.getNodeName().equals(filter)) {
                    parent.removeChild(child);
                } else {
                    filterElements(child, filter);
                }
            }
        }
    }

    /*
     * print a nodes elements and children
     */
    /**
     * Prints the node elements.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param node the node
     * @return the string
     */
    private static String printNodeElements(Node node) {
        String results = node.getNodeName();

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                results = results.concat(printNodeElements(child));
            }
        }
        return results;
    }
}
