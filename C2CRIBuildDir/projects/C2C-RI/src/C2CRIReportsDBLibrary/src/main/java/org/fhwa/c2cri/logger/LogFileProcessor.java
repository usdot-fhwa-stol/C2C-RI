/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPathExpressionException;
import org.fhwa.c2cri.reports.EventSet;
import org.fhwa.c2cri.reports.RawOTWMessage;
import org.fhwa.c2cri.reports.InitEvent;
import org.fhwa.c2cri.reports.InitEventPK;
import org.fhwa.c2cri.reports.ScriptEvent;
import org.fhwa.c2cri.reports.dao.EventSetDAO;
import org.fhwa.c2cri.reports.dao.InitEventDAO;
import org.fhwa.c2cri.reports.dao.ScriptEventDAO;
import org.fhwa.c2cri.utilities.ProgressMonitor;
import org.fhwa.c2cri.reports.LoggedMessage;
import org.fhwa.c2cri.reports.TestResultNeeds;
import org.fhwa.c2cri.reports.TestResultNeedsPK;
import org.fhwa.c2cri.reports.TestResultsTestCase;
import org.fhwa.c2cri.reports.TestResultRequirements;
import org.fhwa.c2cri.reports.TestResultRequirementsPK;
import org.fhwa.c2cri.reports.UserEvent;
import org.fhwa.c2cri.reports.dao.LoggedMessageDAO;
import org.fhwa.c2cri.reports.dao.RawOTWMessageDAO;
import org.fhwa.c2cri.reports.dao.TestResultNeedsDAO;
import org.fhwa.c2cri.reports.dao.TestResultRequirementsDAO;
import org.fhwa.c2cri.reports.dao.TestResultsTestCaseDAO;
import org.fhwa.c2cri.reports.dao.UserEventDAO;
import org.xml.sax.SAXException;


/**
 * The Class LogFileProcessor processes the contents of a RI Test Log file and places the contents into the c2cri database for report generation.
 * 
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class LogFileProcessor {

    /** The event tag. */
    private static String EVENT = "event";
    
    /** The message tag. */
    private static String MESSAGE = "message";
    
    /** The initevent tag. */
    private static String INITEVENT = "initEvent";
    
    /** The userevent tag. */
    private static String USEREVENT = "userEvent";
    
    /** The scriptevent tag. */
    private static String SCRIPTEVENT = "scriptEvent";
    
    /** The tag tag. */
    private static String TAG = "tag";
    
    /** The src tag. */
    private static String SRC = "src";
    
    /** The type tag. */
    private static String TYPE = "type";
    
    /** The line tag. */
    private static String LINE = "line";
    
    /** The column tag. */
    private static String COLUMN = "column";
    
    /** The file tag. */
    private static String FILE = "file";
    
    /** The testcasename tag. */
    private static String TESTCASENAME = "test-case-name";
    
    /** The outcome tag. */
    private static String OUTCOME = "outcome";
    
    /** The executiontime tag. */
    private static String EXECUTIONTIME = "execution-time";
    
    /** The executiontimemillis tag. */
    private static String EXECUTIONTIMEMILLIS = "execution-time-millis";
    
    /** The functionid tag. */
    private static String FUNCTIONID = "functionId";
    
    /** The rawotwmessage tag. */
    private static String RAWOTWMESSAGE = "rawOTWMessage";
    
    /** The loggedmessage tag. */
    private static String LOGGEDMESSAGE = "loggedMessage";
    
    /** The parentdialog tag. */
    private static String PARENTDIALOG = "parentDialog";
    
    /** The messagename tag. */
    private static String MESSAGENAME = "messageName";
    
    /** The messagetype tag. */
    private static String MESSAGETYPE = "messageType";
    
    /** The messageencoding tag. */
    private static String MESSAGEENCODING = "messageEncoding";
    
    /** The viatransportprotocol tag. */
    private static String VIATRANSPORTPROTOCOL = "viaTransportProtocol";
    
    /** The messagesourceaddress tag. */
    private static String MESSAGESOURCEADDRESS = "messageSourceAddress";
    
    /** The messagedestinationaddress tag. */
    private static String MESSAGEDESTINATIONADDRESS = "messageDestinationAddress";
    
    /** The relatedcommand tag. */
    private static String RELATEDCOMMAND = "relatedCommand";
    
    /** The messagebody tag. */
    private static String MESSAGEBODY = "MessageBody";
    
    /** The testcase tag. */
    private static String TESTCASE = "testCase";
    
    /** The connectionname tag. */
    private static String CONNECTIONNAME = "connectionName";
    
    /** The processtype tag. */
    private static String PROCESSTYPE = "ProcessType";
    
    /** The sourceaddress tag. */
    private static String SOURCEADDRESS = "SourceAddress";
    
    /** The destinationaddress tag. */
    private static String DESTINATIONADDRESS = "DestinationAddress";
    
    /** The sequencecount attribute. */
    private static String SEQUENCECOUNT = "sequenceCount";
    
    /** The timestampinmillis attribute. */
    private static String TIMESTAMPINMILLIS = "timestampInMillis";
    
    /** The filename tag. */
    private static String FILENAME = "fileName";
    
    /** The starttime tag. */
    private static String STARTTIME = "startTime";
    
    /** The creator tag. */
    private static String CREATOR = "creator";
    
    /** The description tag. */
    private static String DESCRIPTION = "description";
    
    /** The C2cri version tag. */
    private static String C2CRIVERSION = "c2criVersion";
    
    /** The configuration tag. */
    private static String CONFIGFILENAME = "configFileName";
    
    /** The checksum tag. */
    private static String CHECKSUM = "checksum";
    
    /** The level attribute. */
    private static String LEVEL = "level";
    
    /** The logger attribute. */
    private static String LOGGER = "logger";
    
    /** The thread attribute. */
    private static String THREAD = "thread";
    
    /** The timestamp attribute. */
    private static String TIMESTAMP = "timestamp";
    
    /** The verificationevent tag. */
    private static String VERIFICATIONEVENT = "verificationEvent";
    
    /** The testresult tags. */
    private static String TESTRESULTS = "testResults";
    
    /** The informationlevelstandard. */
    private static String INFORMATIONLEVELSTANDARD = "informationLevelStandard";
    
    /** The applicationlevelstandard. */
    private static String APPLICATIONLEVELSTANDARD = "applicationLevelStandard";
    
    /** The need. */
    private static String NEED = "need";
    
    /** The requirement. */
    private static String REQUIREMENT = "requirement";
    
    /** The verificationtestcase. */
    private static String VERIFICATIONTESTCASE = "testCase";
    
    /** The extension. */
    private static String EXTENSION = "extension";
    
    /** The results. */
    private static String RESULTS = "results";
    
    /** The unid. */
    private static String UNID = "unID";
    
    /** The unselected. */
    private static String UNSELECTED = "unSelected";
    
    /** The userneed. */
    private static String USERNEED = "userNeed";
    
    /** The conformance. */
    private static String CONFORMANCE = "conformance";
    
    /** The otherreq. */
    private static String OTHERREQ = "otherReq";
    
    /** The reqid. */
    private static String REQID = "reqID";
    
    /** The reqtext. */
    private static String REQTEXT = "reqText";
    
    /** The supported. */
    private static String SUPPORTED = "supported";
    
    /** The errordescription. */
    private static String ERRORDESCRIPTION = "errorDescription";
    
    /** The testcaseid. */
    private static String TESTCASEID = "testCaseID";
    
    /** The verificationtimestamp. */
    private static String VERIFICATIONTIMESTAMP = "timeStamp";
    
    /** The result. */
    private static String RESULT = "result";
    
    /** The externalcenteroperation. */
    private static String EXTERNALCENTEROPERATION = "externalCenterOperation";

    /** The configurationauthor. */
    private static String CONFIGURATIONAUTHOR = "configurationAuthor";
    
    /** The selectedapplayertestsuite. */
    private static String SELECTEDAPPLAYERTESTSUITE = "selectedAppLayerTestSuite";
    
    /** The selectedinfolayertestsuite. */
    private static String SELECTEDINFOLAYERTESTSUITE = "selectedInfoLayerTestSuite";
    
    /** The configversion. */
    private static String CONFIGVERSION = "configVersion";
    
    /** The error tag. */
    private static String ERROR= "error";   
    
    /** The enable Emulation flag*/
    private static String ENABLEEMULATION = "enableEmulation";

    /** The reinitialize emulation setting.*/
    private static String REINITIALIZEEMULATION = "reinitializeEmulation";     
    
    /** The output database file. */
    private File outdb;
    
    /** The conn. */
    private Connection conn = null;
    
    /** The pstmt. */
    private PreparedStatement pstmt = null;
    
    /** The log file. */
    private File logFile;
    
    /** The event id. */
    private int eventId = 1;
    
    /** The script event id. */
    private int scriptEventID = 0;
    
    /** The event reader. */
    private XMLEventReader eventReader = null;
    
    /** The event. */
    private XMLEvent event = null;
    
    /** The init event. */
    private InitEvent initEvent = new InitEvent();
    
    /** The user event list. */
    private ArrayList<UserEvent> userEventList = new ArrayList<UserEvent>();
    
    /** The script event dao. */
    private ScriptEventDAO scriptEventDAO;
    
    /** The raw otw message dao. */
    private RawOTWMessageDAO rawOTWMessageDAO;
    
    /**
     * Instantiates a new log file processor.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param logFileName the log file name
     * @param databaseName the database name
     */
    public LogFileProcessor(String logFileName, String databaseName) {
        try {
            File sourcedb = new File(databaseName);
            outdb = new File("./tempOutDb.db3");

            // Make a temporary copy of C2C RI SQLite database
            FileChannel sourceCh = new FileInputStream(sourcedb).getChannel();
            FileChannel destCh = new FileOutputStream(outdb).getChannel();
            sourceCh.transferTo(0, sourceCh.size(), destCh);
            sourceCh.close();
            destCh.close();

            // Create a SQLite connection
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + outdb.getAbsolutePath());

            logFile = new File(logFileName);
            if (!logFile.exists()) {
                throw new Exception("Log file " + logFileName + " does not exist.");
            }
            scriptEventDAO = new ScriptEventDAO(outdb);
            rawOTWMessageDAO = new RawOTWMessageDAO(outdb);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Configuration Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
     * Determines the number of events contained within the log file.  This method
     * currently uses xQuery to quickly run through the log file and get a count of event tags.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param logFileName the log file name
     * @return the long
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XPathExpressionException the x path expression exception
     */
    private Long queryLogEvents(String logFileName) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, XMLStreamException {
        
            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            System.out.println("LogFileProcessor: About to Set Up Event Reader.");
            // Setup a new eventReader
            InputStream in = new FileInputStream(logFileName);
            eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document

            Long result = 0L;
            
            while (eventReader.hasNext()) {
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (EVENT.equals(startElement.getName().getLocalPart())) {
                        result++;
                    }

                }
            }
        

        return result.longValue();
    }

    /**
     * Write out log data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public void writeLogFileDataToTempDB() throws SQLException, Exception {
        try {


            // note that we create the dialog without an owning frame; normally you
            // would associate it with some other frame in your application
            ProgressMonitor monitor = ProgressMonitor.getInstance(
                    null,
                    "C2C RI",
                    "Processing Events in the Log File.",
                    ProgressMonitor.Options.CENTER,
                    ProgressMonitor.Options.SHOW_STATUS,
                    ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

            // because we didn't set an initial progress value, the dialog will
            // appear in indeterminate ("Cylon") mode; let it do that for a while

            System.out.println("LogFileProcessor: About to Query Log Events.");
            Long result = queryLogEvents(logFile.getAbsolutePath());


            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            System.out.println("LogFileProcessor: About to Set Up Event Reader.");
            // Setup a new eventReader
            InputStream in = new FileInputStream(logFile);
            eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document


            ArrayList<EventSet> eventSetList = new ArrayList<EventSet>();


            System.out.println("LogFileProcessor: About to read each event. Total = "+result);
            while (eventReader.hasNext()) {
                EventSet eventSet = new EventSet();
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (EVENT.equals(startElement.getName().getLocalPart())) {
                        eventSet = processEvent(startElement);
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (EVENT.equals(endElement.getName().getLocalPart())) {
                        eventSetList.add(eventSet);
                        eventId++;
                        monitor.setProgress(0, eventId, result.intValue());
                        monitor.setStatus("Loading Event " + (eventId-1) + " of " + result.intValue());
                        //                      items.add(item);
                    }
                }

            }

            // write the events to the database
            InitEventDAO initEventDAO = new InitEventDAO(outdb);            
            initEventDAO.insert(initEvent);
            UserEventDAO userEventDAO = new UserEventDAO(outdb);
            userEventDAO.insert(userEventList);
            EventSetDAO eventSetDAO = new EventSetDAO(outdb);
            eventSetDAO.insert(eventSetList);
            scriptEventDAO.flush();
            rawOTWMessageDAO.flush();
            eventReader.close();
            in.close();
            
            monitor.hide();
            monitor.dispose();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Configuration Report failed: \n"
                    + e.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Configuration Report failed: \n"
                    + e.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Configuration Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Process the given event tag.  Process all attributes and tags related to the event and return an EventSet object.  
     * Here is an example of an event that encapsulates a userEvent tag.
     * 
     *   <event logger="org.fhwa.c2cri.gui.TestExecutionCoordinator" timestamp="1639590486626" level="INFO" thread="AWT-EventQueue-0">
     *      <message>
     *         <userEvent>
     *             User Started the Test with selected Test Case Scripts: Information Layer: EntityEmulationTester</userEvent>
     *      </message>
     *   </event>
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the event set
     * @throws SQLException the sQL exception
     */
    private EventSet processEvent(StartElement thisEvent) throws SQLException {
        EventSet eventSet = null;
        eventSet = new EventSet();
        eventSet.setEventID(eventId);
        eventSet.setEventTypeID(eventSet.getEventID());  
        // We read the attributes from this tag and add the date
        // attribute to our object
        Iterator<Attribute> attributes = thisEvent.getAttributes();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (attribute.getName().toString().equals(LEVEL)) {
                eventSet.setLevel(attribute.getValue());
            } else if (attribute.getName().toString().equals(LOGGER)) {
                eventSet.setLogger(attribute.getValue());
            } else if (attribute.getName().toString().equals(THREAD)) {
                eventSet.setThread(attribute.getValue());
            } else if (attribute.getName().toString().equals(TIMESTAMP)) {
                eventSet.setTimestamp(attribute.getValue());
            }
        }


        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(EVENT)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(MESSAGE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setDebugInfo(event.asCharacters().getData());
                        }
                        continue;
                    }
                }
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(SCRIPTEVENT)) {
                        eventSet.setEventType(SCRIPTEVENT);
                        ScriptEvent returnedScriptEvent = processScriptEvent();
                        returnedScriptEvent.setEventTypeID(eventSet.getEventID());
                        returnedScriptEvent.setEventID(eventSet.getEventID());
                        // write to the database
                        scriptEventDAO.insert(returnedScriptEvent);
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(INITEVENT)) {
                        eventSet.setEventType(INITEVENT);
                        processInitEvent();
                        InitEventPK initEventPK = new InitEventPK();
                        initEventPK.setEventID(eventSet.getEventID());
                        initEventPK.setId(1);
                        initEvent.setInitEventPK(initEventPK);
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(USEREVENT)) {
                        UserEvent thisUserEvent = new UserEvent();
                        eventSet.setEventType(USEREVENT);
                        thisUserEvent = processUserEvent();
                        thisUserEvent.setId(eventSet.getEventID());
                        userEventList.add(thisUserEvent);
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(RAWOTWMESSAGE)) {
                        eventSet.setEventType(RAWOTWMESSAGE);
                        RawOTWMessage rawOTWMessage = processRawOTWMessage();
                        rawOTWMessage.setId(eventSet.getEventID());
                        // write to the database
                        rawOTWMessageDAO.insert(rawOTWMessage);
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(LOGGEDMESSAGE)) {
                        eventSet.setEventType(LOGGEDMESSAGE);
                        LoggedMessage loggedMessage = processLoggedMessage();
                        loggedMessage.setId(eventSet.getEventID());
                        // write to the database
                        LoggedMessageDAO loggedMessageDAO = new LoggedMessageDAO(outdb);
                        loggedMessageDAO.insert(loggedMessage);
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(VERIFICATIONEVENT)) {
                        eventSet.setEventType(VERIFICATIONEVENT);
                        processVerificationEvent();
                        event = eventReader.nextEvent();
                        continue;
                    }

                }
                if (eventReader.hasNext()) {
                    event = eventReader.nextEvent();

                } else {
                    System.out.println("No More Events");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
        return eventSet;
    }

    /**
     * Process script type event tags.
     * Here is an example of a script Event
     * 
     *             <scriptEvent>
     *          <tag src="test-case" type="Begin">
     *             <line>14</line>
     *             <column>58</column>
     *             <file>jar:file:/C:/C2CRIDev/C2CRIBuildDir/projects/C2C-RI/src/RIGUI/dist/RIGUI.jar!/org/fhwa/c2cri/testmodel/TestCaseLauncher.xml</file>
     *
     *		                <test-case-name>EntityEmulationTester</test-case-name>
     *
     *		                <outcome>PASSED</outcome>
     *		                <execution-time>0.000s</execution-time>
     *		                <execution-time-millis>0</execution-time-millis>
     *		
     *		                <outcome>PASSED</outcome>
     *		                <execution-time>0.000s</execution-time>
     *		                <execution-time-millis>0</execution-time-millis>
     *		
     *	              </tag>
     *       </scriptEvent>
     * 
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the script event
     */
    private ScriptEvent processScriptEvent() {

        ScriptEvent thisEvent = new ScriptEvent();
        thisEvent.setExecutionTimeMillis(0);
        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(TAG))) {
            try {

                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(TAG)) {
                        Iterator<Attribute> scriptEventAttributes = startElement.getAttributes();
                        while (scriptEventAttributes.hasNext()) {
                            Attribute attribute = scriptEventAttributes.next();
                            if (attribute.getName().toString().equals(SRC)) {
                                thisEvent.setSrc(attribute.getValue());
                            } else if (attribute.getName().toString().equals(TAG)) {
                                thisEvent.setTag(attribute.getValue());
                            } else if (attribute.getName().toString().equals(TYPE)) {
                                thisEvent.setType(attribute.getValue());
                            }
                        }
                    } else if (startElement.getName().getLocalPart().equals(LINE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setLine(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(COLUMN)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setColumn(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(FILE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setFile(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(FUNCTIONID)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setFunctionId(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(TESTCASENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setTestCaseName(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(OUTCOME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setOutcome(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(EXECUTIONTIME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setExecutionTime(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(EXECUTIONTIMEMILLIS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setExecutionTimeMillis(new Integer(event.asCharacters().getData()));
                        }
                    } else if (startElement.getName().getLocalPart().equals(ERROR)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setError(event.asCharacters().getData());
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return thisEvent;
    }

    /**
     * Process init type event tags.
     * Here is an example of an initEvent tag:
     * 
     *           <initEvent>
     *               <fileName>C:\c2cri\OCSTesting.2021-12-15_12-46-14.xml</fileName>
     *               <startTime>2021-12-15_12-46-14</startTime>
     *               <creator>user</creator>
     *               <description/>
     *               <c2criVersion>Release 3.2</c2criVersion>
     *               <configFileName>C:\C2CRIDev\C2CRIBuildDir\projects\C2C-RI\src\RIGUI\TestConfigurationFiles\TMDDv31EntityEmuOriginalOCS.ricfg</configFileName>
     *               <checksum>0F84A8C12948537A009CBA1C2C7E883B</checksum>
     *               <enableEmulation>true</enableEmulation>
     *               <reinitializeEmulation>false</reinitializeEmulation>
     *            </initEvent>
     * 
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: Stores the init event content into the initEvent object.
     */
    private void processInitEvent() {

        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(INITEVENT))) {
            try {

                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(FILENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setFileName(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(STARTTIME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setStartTime(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CREATOR)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setCreator(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(DESCRIPTION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setDescription(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(C2CRIVERSION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setC2criVersion(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONFIGFILENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setConfigFileName(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CHECKSUM)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setChecksum(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONFIGURATIONAUTHOR)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setConfigurationAuthor(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SELECTEDAPPLAYERTESTSUITE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setSelectedAppLayerTestSuite(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SELECTEDINFOLAYERTESTSUITE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setSelectedInfoLayerTestSuite(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONFIGVERSION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setConfigVersion(event.asCharacters().getData());
                        }
                    }  else if (startElement.getName().getLocalPart().equals(EXTERNALCENTEROPERATION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            initEvent.setExternalCenterOperation(event.asCharacters().getData());
                        }
                    }  else if(startElement.getName().getLocalPart().equals(ENABLEEMULATION)){
                        event = eventReader.nextEvent();
                        if (event.isCharacters())
                        {
                            initEvent.setEnableEmulation(event.asCharacters().getData());
                        }
                    }  else if(startElement.getName().getLocalPart().equals(REINITIALIZEEMULATION)){
                        event = eventReader.nextEvent();
                        if(event.isCharacters())
                        {
                            initEvent.setReInitializeEmulation(event.asCharacters().getData());
                        }
                    }
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Process user type event tags.
     * Here is an example of a userEvent tag:
     * 
     *    <userEvent>
     *      User Started the Test with selected Test Case Scripts: Information Layer: EntityEmulationTester
     *    </userEvent>
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the user event
     */
    private UserEvent processUserEvent() {
        UserEvent thisEvent = new UserEvent();
        try {
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                // If we have a item element we create a new item
                if (startElement.getName().getLocalPart().equals(USEREVENT)) {
                    event = eventReader.nextEvent();
                    if (event.isCharacters()) {
                        thisEvent.setDescription(event.asCharacters().getData());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return thisEvent;
    }

    /**
     * Process rawotwmessage type tags.
     * Here is an example of a rawOTWMessage event.
     * 
     *      <rawOTWMessage>
     *               <testCase>EntityEmulationTester</testCase>
     *               <connectionName>http://C2CRIOC:8084/TMDDWS/TMDDWS.svc/OC</connectionName>
     *               <ProcessType>Request</ProcessType>
     *               <SourceAddress>127.0.0.1:63656</SourceAddress>
     *               <DestinationAddress>127.0.0.1:8084</DestinationAddress>
     *               <timestampInMillis>1639590528227</timestampInMillis>
     *               <sequenceCount>1</sequenceCount>
     *               <message>
     *
     *                  50 4F 53 54 20 2F 54 4D 44 44 57 53 2F 54 4D 44 :POST /TMDDWS/TMD
     *                  44 57 53 2E 73 76 63 2F 4F 43 20 48 54 54 50 2F :DWS.svc/OC HTTP/
     *                  31 2E 31 0D 0A 41 63 63 65 70 74 2D 45 6E 63 6F :1.1..Accept-Enco
     *                  ....... 
     *          </message>
     *      </rawOTWMessage>
     * 
     * 
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the raw otw message
     */
    private RawOTWMessage processRawOTWMessage() {

        RawOTWMessage thisRawMessageEvent = new RawOTWMessage();
        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(RAWOTWMESSAGE))) {
            try {

                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(TESTCASE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setTestCase(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONNECTIONNAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setConnectionName(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(PROCESSTYPE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setProcessType(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SOURCEADDRESS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setSourceAddress(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(DESTINATIONADDRESS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setDestinationAddress(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SEQUENCECOUNT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setSequenceCount(new Integer(event.asCharacters().getData()));
                        }
                    } else if (startElement.getName().getLocalPart().equals(TIMESTAMPINMILLIS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisRawMessageEvent.setTimeStampInMillis(new BigInteger(event.asCharacters().getData()));
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGE)) {
                        if (!event.isCharacters()) event = eventReader.nextEvent();  // Get To CDATA!! (Sometimes Varies by Parser whether this step is needed)

                        StringBuilder sb = new StringBuilder();
                        if (event.isCharacters()) {
                           sb.append(event.asCharacters().getData());
                           while (eventReader.peek().isCharacters()){  
                               event = eventReader.nextEvent();
                                sb.append(event.asCharacters().getData());
                            };
                            thisRawMessageEvent.setMessage(sb.toString());
                        }
                       
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return thisRawMessageEvent;
    }

    /**
     * Process the loggedmessage type tag.
     * Here is an example of a loggedMessage Event
     * 
     *   <loggedMessage>
     *      <parentDialog>dlCenterActiveVerificationSubscription</parentDialog>
     *      <messageName>REQUEST</messageName>
     *      <messageType>centerActiveVerificationRequestMsg</messageType>
     *      <messageEncoding>SOAP</messageEncoding>
     *      <viaTransportProtocol>HTTP</viaTransportProtocol>
     *      <messageSourceAddress>127.0.0.1:63656</messageSourceAddress>
     *      <messageDestinationAddress>127.0.0.1:8084</messageDestinationAddress>
     *      <relatedCommand/>
     *      <MessageBody>
     *
     *          c2c:c2cMessageSubscription.informationalText = ?
     *          c2c:c2cMessageSubscription.returnAddress = http://C2CRIEC:8187/TMDDWS/EC/tmddECSoapHttpService/tmddECSoapHttpServicePort/dlCenterActiveVerificationUpdate
     *          c2c:c2cMessageSubscription.subscriptionAction.subscriptionAction-item = newSubscription
     *          c2c:c2cMessageSubscription.subscriptionType.subscriptionType-item = onChange
     *          c2c:c2cMessageSubscription.subscriptionID = Center Active 1
     *          ....
     *      </MessageBody>
     *   </loggedMessage>
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the logged message
     */
    private LoggedMessage processLoggedMessage() {

        LoggedMessage thisLoggedMessageEvent = new LoggedMessage();
        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(LOGGEDMESSAGE))) {
            try {

                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(PARENTDIALOG)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setParentDialog(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setMessageName(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGETYPE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setMessageType(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGEENCODING)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setMessageEncoding(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(VIATRANSPORTPROTOCOL)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setViaTransportProtocol(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGESOURCEADDRESS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setMessageSourceAddress(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGEDESTINATIONADDRESS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setMessageDestinationAddress(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(RELATEDCOMMAND)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisLoggedMessageEvent.setRelatedCommand(event.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGEBODY)) {
                       
                        if (!event.isCharacters()) event = eventReader.nextEvent();  // CDATA!!
                        StringBuilder sb = new StringBuilder();
                        if (event.isCharacters()) {
                           sb.append(event.asCharacters().getData());
                           while (eventReader.peek().isCharacters()){  
                               event = eventReader.nextEvent();
                                sb.append(event.asCharacters().getData());
                            }
                         thisLoggedMessageEvent.setMessageBody(sb.toString());
                       }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return thisLoggedMessageEvent;
    }

    /**
     * Process verificationEvent type tags.  Write the results to the database.
     *  Here is an example of a verification event message:
     * 
     *      <verificationEvent>
     *         <testResults>
     *             <informationLevelStandard>
     *                <need unID="2.3.1.1" userNeed="Verify Connection Active" unSelected="Yes" results="Not Tested" extension="false">
     *                   <requirement reqID="3.3.1.1.1" reqText="Send Center Active Verification Upon Request" conformance="M" supported="Yes" otherReq="The owner center shall respond within ___ (100 ms - 1 hour; Default = 1 minute) after receiving the request. See Section 3.4.2." results="Not Tested" extension="false">
     *                      <testCase testCaseID="TCS-1-dlCenterActiveVerificationRequest-OC-Valid" timeStamp="0" errorDescription="No Test Cases Applicable in this Test Mode" result="No Test Cases Applicable in this Test Mode"/>
     *                      <testCase testCaseID="TCS-1-dlCenterActiveVerificationRequest-OC-InValid-2" timeStamp="0" errorDescription="No Test Cases Applicable in this Test Mode" result="No Test Cases Applicable in this Test Mode"/>
     *                      ...
     *                   </requirement>
     *                   ...
     *                </need>
     *                ...
     *             </informationLevelStandard>
     *             <applicationLevelStandard>
     *                <need unID="1" userNeed="SOAP over HTTP" unSelected="Yes" results="No Test Cases Applicable in this Test Mode" extension="false">
     *                   <requirement reqID="1.a" reqText="WSDL Request-Response (SOAPWSDL)" conformance="O" supported="Yes" otherReq="" results="No Test Cases Applicable in this Test Mode" extension="false">
     *                      <testCase testCaseID="TCS-C2CRI-NTCIP2306-WSDL-SUT" timeStamp="0" errorDescription="No Test Cases Applicable in this Test Mode" result="No Test Cases Applicable in this Test Mode"/>
     *                   </requirement>
     *                   <requirement reqID="1.b" reqText="WSDL Subscription-Publication (SOAPWSDL)" conformance="O" supported="No" otherReq="" results="No Test Cases Applicable in this Test Mode" extension="false"/>
     *                   ...
     *                </need>
     *                ...
     *             </applicationLevelStandard>
     *             <testTarget>SUT/RI</testTarget>
     *          </testResults>
     *       </verificationEvent>
     * 
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void processVerificationEvent() {

        ArrayList<TestResultNeeds> testResultNeedsList = new ArrayList<TestResultNeeds>();
        ArrayList<TestResultRequirements> testResultRequirementsList = new ArrayList<TestResultRequirements>();
        ArrayList<TestResultsTestCase> testResultsTestCaseList = new ArrayList<TestResultsTestCase>();

        String currentLevelStandard = "";
        Integer currentNeedID = 0;
        Integer currentRequirementID = 0;
        Integer currentTestCaseID = 0;


        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(TESTRESULTS))) {
            try {

                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if ((startElement.getName().getLocalPart().equals(INFORMATIONLEVELSTANDARD))
                            || (startElement.getName().getLocalPart().equals(APPLICATIONLEVELSTANDARD))) {
                        currentLevelStandard = startElement.getName().getLocalPart();
                        event = eventReader.nextEvent();
                    } else if (startElement.getName().getLocalPart().equals(NEED)) {
                        currentNeedID++;
                        TestResultNeeds thisTestResultNeed = new TestResultNeeds();
                        Iterator<Attribute> needAttributes = startElement.getAttributes();
                        while (needAttributes.hasNext()) {
                            Attribute attribute = needAttributes.next();
                            if (attribute.getName().toString().equals(EXTENSION)) {
                                thisTestResultNeed.setExtension(attribute.getValue());
                            } else if (attribute.getName().toString().equals(RESULTS)) {
                                thisTestResultNeed.setResults(attribute.getValue());
                            } else if (attribute.getName().toString().equals(UNID)) {
                                thisTestResultNeed.setUnID(attribute.getValue());
                            } else if (attribute.getName().toString().equals(UNSELECTED)) {
                                thisTestResultNeed.setUnSelected(attribute.getValue());
                            } else if (attribute.getName().toString().equals(USERNEED)) {
                                thisTestResultNeed.setUserNeed(attribute.getValue());
                            }
                        }

                        TestResultNeedsPK testResultsNeedPK = new TestResultNeedsPK();
                        testResultsNeedPK.setNeedID(currentNeedID);
                        testResultsNeedPK.setStandardType(currentLevelStandard);
                        thisTestResultNeed.setTestResultNeedsPK(testResultsNeedPK);
                        testResultNeedsList.add(thisTestResultNeed);

                    } else if (startElement.getName().getLocalPart().equals(REQUIREMENT)) {
                        currentRequirementID++;
                        TestResultRequirements thisTestResultRequirement = new TestResultRequirements();
                        Iterator<Attribute> requirementAttributes = startElement.getAttributes();
                        while (requirementAttributes.hasNext()) {
                            Attribute attribute = requirementAttributes.next();
                            if (attribute.getName().toString().equals(CONFORMANCE)) {
                                thisTestResultRequirement.setConformance(attribute.getValue());
                            } else if (attribute.getName().toString().equals(EXTENSION)) {
                                thisTestResultRequirement.setExtension(attribute.getValue());
                            } else if (attribute.getName().toString().equals(OTHERREQ)) {
                                thisTestResultRequirement.setOtherReq(attribute.getValue());
                            } else if (attribute.getName().toString().equals(REQID)) {
                                thisTestResultRequirement.setReqID(attribute.getValue());
                            } else if (attribute.getName().toString().equals(REQTEXT)) {
                                thisTestResultRequirement.setReqText(attribute.getValue());
                            } else if (attribute.getName().toString().equals(RESULTS)) {
                                thisTestResultRequirement.setResults(attribute.getValue());
                            } else if (attribute.getName().toString().equals(SUPPORTED)) {
                                thisTestResultRequirement.setSupported(attribute.getValue());
                            }

                       }
                            TestResultRequirementsPK testResultRequirementsPK = new TestResultRequirementsPK();
                            testResultRequirementsPK.setNeedID(currentNeedID);
                            testResultRequirementsPK.setRequirementID(currentRequirementID);
                            thisTestResultRequirement.setTestResultRequirementsPK(testResultRequirementsPK);
                            testResultRequirementsList.add(thisTestResultRequirement);


                    } else if (startElement.getName().getLocalPart().equals(VERIFICATIONTESTCASE)) {
                        currentTestCaseID++;
                        TestResultsTestCase thisTestResultsTestCase = new TestResultsTestCase();
                        Iterator<Attribute> testCaseAttributes = startElement.getAttributes();
                        while (testCaseAttributes.hasNext()) {
                            Attribute attribute = testCaseAttributes.next();
                            if (attribute.getName().toString().equals(ERRORDESCRIPTION)) {
                                thisTestResultsTestCase.setErrorDescription(attribute.getValue());
                            } else if (attribute.getName().toString().equals(RESULT)) {
                                thisTestResultsTestCase.setResult(attribute.getValue());
                            } else if (attribute.getName().toString().equals(TESTCASEID)) {
                                thisTestResultsTestCase.setTestCaseID(attribute.getValue());
                            } else if (attribute.getName().toString().equals(VERIFICATIONTIMESTAMP)) {
                                thisTestResultsTestCase.setTimeStamp(attribute.getValue());
                            }
                        }
                        thisTestResultsTestCase.setTcid(currentTestCaseID);
                        thisTestResultsTestCase.setNeedID(currentNeedID);
                        thisTestResultsTestCase.setRequirementID(currentRequirementID);

                        testResultsTestCaseList.add(thisTestResultsTestCase);
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Transfer the test results information to the database.
        try {
            TestResultNeedsDAO thisTestResultNeedsDAO = new TestResultNeedsDAO(outdb);
            thisTestResultNeedsDAO.insert(testResultNeedsList);

            TestResultRequirementsDAO thisTestResultRequirementsDAO = new TestResultRequirementsDAO(outdb);
            thisTestResultRequirementsDAO.insert(testResultRequirementsList);

            TestResultsTestCaseDAO thisTestResultsTestCaseDAO = new TestResultsTestCaseDAO(outdb);
            thisTestResultsTestCaseDAO.insert(testResultsTestCaseList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
