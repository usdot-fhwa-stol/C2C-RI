/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

import org.fhwa.c2cri.reports.LoggedMessage;
import org.fhwa.c2cri.reports.TestResultNeeds;
import org.fhwa.c2cri.reports.TestResultNeedsPK;
import org.fhwa.c2cri.reports.TestResultsTestCase;
import org.fhwa.c2cri.reports.TestResultRequirements;
import org.fhwa.c2cri.reports.TestResultRequirementsPK;
import org.fhwa.c2cri.reports.UserEvent;

import org.xml.sax.SAXException;


/**
 * The Class LogFileProcessor processes the contents of a RI Test Log file and places the contents into the c2cri database for report generation.
 * 
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class LogFileProcessorTest {

    /** The event tag. */
    private static String EVENT = "event";
    
    /** The message tag. */
    private static String MESSAGE = "message";
    
    /** The initevent tag. */
    private static String INITEVENTTAG = "initEvent";
    
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

    /** The target of the testing. */
    private static String RITESTTARGETTAG = "testTarget";
    
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
//    private static String OWNERCENTEROPERATION = "externalCenterOperation";
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
    
//    private static String TESTDESCRIPTION = "testDescription";
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
    private XMLEvent xmlevent = null;
    
    /** The init event. */
    private InitEvent initEvent = new InitEvent();
    
    /** The user event list. */
    private ArrayList<UserEvent> userEventList = new ArrayList<UserEvent>();
    
    private String currentTestCase="";
    private String currentTestProcedure="";
    private String currentTestStep="";
    private String currentTestStepResult="";
    
    
    private static FileWriter testFilesFile;
    private static FileWriter testStepsFile;
    private static FileWriter testVerificationFile;
    private static long testFileId = 0L;
    private long testStepId = 0L;
    private long verificationRecordId = 0L;
    private HashMap<String,Integer> testCaseCounterMap = new HashMap();
    private String riTestTarget = "";

//    /** The script event dao. */
//    private ScriptEventDAO scriptEventDAO;
//    
//    /** The raw otw message dao. */
//    private RawOTWMessageDAO rawOTWMessageDAO;
    
    
    public static void main(String[] args) throws Exception{
       testFilesFile = new FileWriter("c:\\c2cri\\traceability\\TestFiles.csv");
       testStepsFile = new FileWriter("c:\\c2cri\\traceability\\TestSteps.csv");
       testVerificationFile = new FileWriter("c:\\c2cri\\traceability\\TestVerification.csv");

       testFilesFile.write("id|name|description|cfgFile|checkSum|C2CRIVersion|testMode|RIMode\n");
       testStepsFile.write("id|testId|step|status|testCase|testCaseIteration|testProcedure|timeStamp\n");
       testVerificationFile.write("id|testId|needId|needSelected|needResults|reqId|reqSupported|reqResults|testCaseName|testCaseIteration|testCaseResult\n");

       String[] fileList = {"C:\\C2CRI\\atpFiles\\TMDDv3TstResultOCS.2016-12-07_10-34-50.xml",
                            "C:\\C2CRI\\atpFiles\\ecs\\TMDDv3TstResultECS.2016-12-07_10-35-38.xml",
                            "C:\\C2CRI\\atpFiles\\TMDDv3TstResultOCS2.2016-12-07_11-22-08.xml",
                            "C:\\C2CRI\\atpFiles\\ecs\\TMDDv3TstResultECS2.2016-12-07_11-22-39.xml",
                            "C:\\C2CRI\\atpFiles\\TMDDv3TstResultOCS3.2016-12-07_15-43-38.xml",
                            "C:\\C2CRI\\atpFiles\\ecs\\TMDDv3TstResultECS3.2016-12-07_15-44-19.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_1.2017-01-30_15-13-04.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_1.2017-01-30_15-12-41.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_2.2017-01-30_17-00-08.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_2.2017-01-30_17-00-19.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_3.2017-01-31_08-51-08.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_3.2017-01-31_08-52-17.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_4.2017-01-31_08-57-59.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_4.2017-01-31_08-58-55.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_5.2017-01-31_09-04-16.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_5.2017-01-31_09-04-08.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_6.2017-01-31_13-55-14.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_6.2017-01-31_13-54-42.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_7.2017-01-31_12-41-40.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_7.2017-01-31_12-41-29.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_8.2017-01-31_13-20-26.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_8.2017-01-31_13-20-34.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_Set_9.2017-01-31_13-31-38.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_Set_9.2017-01-31_13-31-29.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultOCS_TCS_110.2017-01-31_15-31-11.xml",
                            "C:\\C2CRI\\atpfILES\\supplemental\\TMDDv3TstResultECS_TCS_110.2017-01-31_15-30-06.xml"
                            };
       for (int ii=0; ii<fileList.length; ii++){
            LogFileProcessorTest lfpt = new LogFileProcessorTest(fileList[ii]);
            testFileId++;
            lfpt.writeLogFileDataToTempDB();
       }
         
       testFilesFile.close();
       testStepsFile.close();
       testVerificationFile.close();
    }    
    
    /**
     * Instantiates a new log file processor.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param logFileName the log file name
     * @param databaseName the database name
     */
    public LogFileProcessorTest(String logFileName) {
        try {

            logFile = new File(logFileName);
            if (!logFile.exists()) {
                throw new Exception("Log file " + logFileName + " does not exist.");
            }
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
        // Standard of reading a XML file
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        DocumentBuilder builder;
//        Document doc = null;
//        XPathExpression expr = null;
//        builder = factory.newDocumentBuilder();
////        doc = builder.parse(logFileName);
//
//        // Create a XPathFactory
//        XPathFactory xFactory = XPathFactory.newInstance();
//
//        // Create a XPath object
//        XPath xpath = xFactory.newXPath();
//
//        // Compile the XPath expression
//        expr = xpath.compile("count(/logFile/eventSet/event)");
        
        
            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            System.out.println("LogFileProcessor: About to Set Up Event Reader.");
            // Setup a new eventReader
            InputStream in = new FileInputStream(logFileName);
            eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document

            Long result = 0L;
            
            while (eventReader.hasNext()) {
                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
                    // If we have a item element we create a new item
                    if (EVENT.equals(startElement.getName().getLocalPart())) {
                        result++;
                    }

                }
            }
        
        // Run the query and get a nodeset
//        Double result = (Double) expr.evaluate(doc, XPathConstants.NUMBER);

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
                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
                    // If we have a item element we create a new item
                    if (EVENT.equals(startElement.getName().getLocalPart())) {
                        eventSet = processEvent(startElement);
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (xmlevent.isEndElement()) {
                    EndElement endElement = xmlevent.asEndElement();
                    if (EVENT.equals(endElement.getName().getLocalPart())) {
                        eventSetList.add(eventSet);
                        eventId++;
                        //                      items.add(item);
                    }
                }

            }

            System.out.println("Commented out InitEvent");
//       lfpt.testFilesFile.write("id,name,description,cfgFile,checkSum,C2CRIVersion,testMode,RIMode");
              testFilesFile.write(this.testFileId+"|"+initEvent.getFileName()+"|"+(initEvent.getDescription()==null?"":initEvent.getDescription().replace("\n",""))+"|"+initEvent.getConfigFileName()
              + "|"+initEvent.getChecksum()+"|"+initEvent.getC2criVersion()+"|"+(initEvent.getExternalCenterOperation().equals("true")?"EC":"OC")+"|"+this.riTestTarget+"\n");
//            initEventDAO.insert(initEvent);
            System.out.println("Commented out UserEvent");
//            userEventDAO.insert(userEventList);
            System.out.println("Commented out EventSet");
//            eventSetDAO.insert(eventSetList);
            eventReader.close();
            in.close();
            
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
     * Process event tags.
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


        while (!(xmlevent.isEndElement() && (xmlevent.asEndElement().getName().getLocalPart().equals(EVENT)))) {

            try {
                if (xmlevent.isStartElement()) {
                    if (xmlevent.asStartElement().getName().getLocalPart().equals(MESSAGE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            eventSet.setDebugInfo(xmlevent.asCharacters().getData());
                        }
                        continue;
                    }
                }
                if (xmlevent.isStartElement()) {
                    if (xmlevent.asStartElement().getName().getLocalPart().equals(SCRIPTEVENT)) {
                        eventSet.setEventType(SCRIPTEVENT);
                        ScriptEvent returnedScriptEvent = processScriptEvent();
                        returnedScriptEvent.setEventTypeID(eventSet.getEventID());
                        returnedScriptEvent.setEventID(eventSet.getEventID());
//                        ScriptEventDAO scriptEventDAO = new ScriptEventDAO(outdb);
                        if (returnedScriptEvent.getSrc().equals("test-case")&&returnedScriptEvent.getType().equals("Begin")){
                            this.currentTestCase = returnedScriptEvent.getTestCaseName();
                            if (this.testCaseCounterMap.containsKey(this.currentTestCase)){
                                Integer currentValue = this.testCaseCounterMap.get(this.currentTestCase);
                                currentValue++;
                                this.testCaseCounterMap.remove(this.currentTestCase);
                                this.testCaseCounterMap.put(this.currentTestCase, currentValue);
                            } else {
                                this.testCaseCounterMap.put(this.currentTestCase, 1);                                
                            }
                        } else if (returnedScriptEvent.getSrc().equals("function-point")&&returnedScriptEvent.getTag().equals("testStep")&&returnedScriptEvent.getType().equals("End")){
                            String functionId = returnedScriptEvent.getFunctionId().trim();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(eventSet.getTimestamp()));                
                            
                            this.testStepId++;
                            Integer iteration = this.testCaseCounterMap.get(this.currentTestCase);
//       lfpt.testStepsFile.write("id,testId,step,status,testCase,testCaseIteration,testProcedure,timeStamp");
                            testStepsFile.write(this.testStepId+"|"+testFileId+"|"+functionId.substring("Step ".length(),functionId.indexOf(" ","Step ".length()))+"|"+returnedScriptEvent.getOutcome()+"|"+(currentTestCase==null?"":currentTestCase)+"|"+iteration+"|"+returnedScriptEvent.getFile().substring(returnedScriptEvent.getFile().lastIndexOf("/")+1).replace(".xml", "")+"|"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+"."+calendar.get(Calendar.MILLISECOND)+"\n");
                            System.out.println("Commented out Script Event DAO -- Step "+functionId.substring("Step ".length(),functionId.indexOf(" ","Step ".length()))+"  "+returnedScriptEvent.getOutcome()+" "+(currentTestCase==null?"":currentTestCase)+"  @"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+"."+calendar.get(Calendar.MILLISECOND)+" "+returnedScriptEvent.getFile().substring(returnedScriptEvent.getFile().lastIndexOf("/")+1).replace(".xml", "")+" "+initEvent.getFileName() + " " + initEvent.getConfigFileName()+ " " + initEvent.getChecksum()+" " +(initEvent.getExternalCenterOperation().equals("true")?"EC":"OC")+" "+ initEvent.getC2criVersion());
                        }
//                        scriptEventDAO.insert(returnedScriptEvent);
                        xmlevent = eventReader.nextEvent();
                        continue;
                    } else if (xmlevent.asStartElement().getName().getLocalPart().equals(INITEVENTTAG)) {
                        eventSet.setEventType(INITEVENTTAG);
                        processInitEvent();
                        InitEventPK initEventPK = new InitEventPK();
                        initEventPK.setEventID(eventSet.getEventID());
                        initEventPK.setId(1);
                        initEvent.setInitEventPK(initEventPK);
                        xmlevent = eventReader.nextEvent();
                        continue;
                    } else if (xmlevent.asStartElement().getName().getLocalPart().equals(USEREVENT)) {
                        UserEvent thisUserEvent = new UserEvent();
                        eventSet.setEventType(USEREVENT);
                        thisUserEvent = processUserEvent();
                        thisUserEvent.setId(eventSet.getEventID());
                        userEventList.add(thisUserEvent);
                        xmlevent = eventReader.nextEvent();
                        continue;
                    } else if (xmlevent.asStartElement().getName().getLocalPart().equals(RAWOTWMESSAGE)) {
                        eventSet.setEventType(RAWOTWMESSAGE);
                        RawOTWMessage rawOTWMessage = processRawOTWMessage();
                        rawOTWMessage.setId(eventSet.getEventID());
//                        RawOTWMessageDAO rawOTWMessageDAO = new RawOTWMessageDAO(outdb);
                        System.out.println("Commented out RAWOTWMessageDAO");
//                        rawOTWMessageDAO.insert(rawOTWMessage);
                        xmlevent = eventReader.nextEvent();
                        continue;
                    } else if (xmlevent.asStartElement().getName().getLocalPart().equals(LOGGEDMESSAGE)) {
                        eventSet.setEventType(LOGGEDMESSAGE);
                        LoggedMessage loggedMessage = processLoggedMessage();
                        loggedMessage.setId(eventSet.getEventID());
                        System.out.println("Commented out LoggedMessageDAO");
//                        LoggedMessageDAO loggedMessageDAO = new LoggedMessageDAO(outdb);
//                        loggedMessageDAO.insert(loggedMessage);
                        xmlevent = eventReader.nextEvent();
                        continue;
                    } else if (xmlevent.asStartElement().getName().getLocalPart().equals(VERIFICATIONEVENT)) {
                        eventSet.setEventType(VERIFICATIONEVENT);
                        processVerificationEvent();
                        xmlevent = eventReader.nextEvent();
                        continue;
                    }

                }
                if (eventReader.hasNext()) {
                    xmlevent = eventReader.nextEvent();

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
     * Process script event tags.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the script event
     */
    private ScriptEvent processScriptEvent() {

        ScriptEvent thisEvent = new ScriptEvent();
        thisEvent.setExecutionTimeMillis(0);
        while (eventReader.hasNext() && !(xmlevent.isEndElement() && xmlevent.asEndElement().getName().getLocalPart().equals(TAG))) {
            try {

                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
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
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setLine(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(COLUMN)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setColumn(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(FILE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setFile(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(FUNCTIONID)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setFunctionId(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(TESTCASENAME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setTestCaseName(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(OUTCOME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setOutcome(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(EXECUTIONTIME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setExecutionTime(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(EXECUTIONTIMEMILLIS)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setExecutionTimeMillis(new Integer(xmlevent.asCharacters().getData()));
                        }
                    } else if (startElement.getName().getLocalPart().equals(ERROR)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisEvent.setError(xmlevent.asCharacters().getData());
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
     * Process init event tags.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void processInitEvent() {

        while (eventReader.hasNext() && !(xmlevent.isEndElement() && xmlevent.asEndElement().getName().getLocalPart().equals(INITEVENTTAG))) {
            try {

                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(FILENAME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setFileName(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(STARTTIME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setStartTime(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CREATOR)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setCreator(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(DESCRIPTION)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setDescription(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(C2CRIVERSION)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setC2criVersion(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONFIGFILENAME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setConfigFileName(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CHECKSUM)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setChecksum(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONFIGURATIONAUTHOR)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setConfigurationAuthor(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SELECTEDAPPLAYERTESTSUITE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setSelectedAppLayerTestSuite(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SELECTEDINFOLAYERTESTSUITE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setSelectedInfoLayerTestSuite(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONFIGVERSION)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setConfigVersion(xmlevent.asCharacters().getData());
                        }
                    }  else if (startElement.getName().getLocalPart().equals(EXTERNALCENTEROPERATION)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            initEvent.setExternalCenterOperation(xmlevent.asCharacters().getData());
                        }
                    }  else if(startElement.getName().getLocalPart().equals(ENABLEEMULATION)){
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters())
                        {
                            initEvent.setEnableEmulation(xmlevent.asCharacters().getData());
                        }
                    }  else if(startElement.getName().getLocalPart().equals(REINITIALIZEEMULATION)){
                        xmlevent = eventReader.nextEvent();
                        if(xmlevent.isCharacters())
                        {
                            initEvent.setReInitializeEmulation(xmlevent.asCharacters().getData());
                        }
                    }
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Process user event tags.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the user event
     */
    private UserEvent processUserEvent() {
        UserEvent thisEvent = new UserEvent();
        try {
            if (xmlevent.isStartElement()) {
                StartElement startElement = xmlevent.asStartElement();
                // If we have a item element we create a new item
                if (startElement.getName().getLocalPart().equals(USEREVENT)) {
                    xmlevent = eventReader.nextEvent();
                    if (xmlevent.isCharacters()) {
                        thisEvent.setDescription(xmlevent.asCharacters().getData());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return thisEvent;
    }

    /**
     * Process rawotwmessage tags.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the raw otw message
     */
    private RawOTWMessage processRawOTWMessage() {

        RawOTWMessage thisRawMessageEvent = new RawOTWMessage();
        while (eventReader.hasNext() && !(xmlevent.isEndElement() && xmlevent.asEndElement().getName().getLocalPart().equals(RAWOTWMESSAGE))) {
            try {

                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(TESTCASE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setTestCase(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(CONNECTIONNAME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setConnectionName(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(PROCESSTYPE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setProcessType(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SOURCEADDRESS)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setSourceAddress(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(DESTINATIONADDRESS)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setDestinationAddress(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(SEQUENCECOUNT)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setSequenceCount(new Integer(xmlevent.asCharacters().getData()));
                        }
                    } else if (startElement.getName().getLocalPart().equals(TIMESTAMPINMILLIS)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisRawMessageEvent.setTimeStampInMillis(new BigInteger(xmlevent.asCharacters().getData()));
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGE)) {
//                        event = eventReader.nextEvent();
                        if (!xmlevent.isCharacters()) xmlevent = eventReader.nextEvent();  // Get To CDATA!! (Sometimes Varies by Parser whether this step is needed)

                        StringBuilder sb = new StringBuilder();
                        if (xmlevent.isCharacters()) {
                           sb.append(xmlevent.asCharacters().getData());
                           while (eventReader.peek().isCharacters()){  
                               xmlevent = eventReader.nextEvent();
                                sb.append(xmlevent.asCharacters().getData());
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
     * Process the loggedmessage tag.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the logged message
     */
    private LoggedMessage processLoggedMessage() {

        LoggedMessage thisLoggedMessageEvent = new LoggedMessage();
        while (eventReader.hasNext() && !(xmlevent.isEndElement() && xmlevent.asEndElement().getName().getLocalPart().equals(LOGGEDMESSAGE))) {
            try {

                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
                    // If we have a item element we create a new item
                    if (startElement.getName().getLocalPart().equals(PARENTDIALOG)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setParentDialog(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGENAME)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setMessageName(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGETYPE)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setMessageType(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGEENCODING)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setMessageEncoding(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(VIATRANSPORTPROTOCOL)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setViaTransportProtocol(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGESOURCEADDRESS)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setMessageSourceAddress(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGEDESTINATIONADDRESS)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setMessageDestinationAddress(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(RELATEDCOMMAND)) {
                        xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            thisLoggedMessageEvent.setRelatedCommand(xmlevent.asCharacters().getData());
                        }
                    } else if (startElement.getName().getLocalPart().equals(MESSAGEBODY)) {
//                        event = eventReader.nextEvent();
                       
                        if (!xmlevent.isCharacters()) xmlevent = eventReader.nextEvent();  // CDATA!!
                        StringBuilder sb = new StringBuilder();
                        if (xmlevent.isCharacters()) {
                           sb.append(xmlevent.asCharacters().getData());
                           while (eventReader.peek().isCharacters()){  
                               xmlevent = eventReader.nextEvent();
                                sb.append(xmlevent.asCharacters().getData());
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
     * Process verificationevent tags.
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
        TestResultNeeds currentNeed = null;
        TestResultRequirements currentRequirement = null;

        while (eventReader.hasNext() && !(xmlevent.isEndElement() && xmlevent.asEndElement().getName().getLocalPart().equals(TESTRESULTS))) {
            try {

                xmlevent = eventReader.nextEvent();

                if (xmlevent.isStartElement()) {
                    StartElement startElement = xmlevent.asStartElement();
                    // If we have a item element we create a new item
                    if ((startElement.getName().getLocalPart().equals(INFORMATIONLEVELSTANDARD))
                            || (startElement.getName().getLocalPart().equals(APPLICATIONLEVELSTANDARD))) {
                        currentLevelStandard = startElement.getName().getLocalPart();
                        xmlevent = eventReader.nextEvent();
                    } else if (startElement.getName().getLocalPart().equals(RITESTTARGETTAG)) {
                        
                    xmlevent = eventReader.nextEvent();
                        if (xmlevent.isCharacters()) {
                            riTestTarget = xmlevent.asCharacters().getData();
                        }
                            
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
                        
                        currentNeed = thisTestResultNeed;
//                        testResultNeedsList.add(thisTestResultNeed);

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
                            currentRequirement = thisTestResultRequirement;
//                            testResultRequirementsList.add(thisTestResultRequirement);


                    } else if (startElement.getName().getLocalPart().equals(VERIFICATIONTESTCASE)) {
                        currentTestCaseID++;
                        String testCaseRunID = "";
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
                            } else if (attribute.getName().toString().equals("testCaseRunID")) {
                                testCaseRunID = attribute.getValue();
                            }
                        }
                        thisTestResultsTestCase.setTcid(currentTestCaseID);
                        thisTestResultsTestCase.setNeedID(currentNeedID);
                        thisTestResultsTestCase.setRequirementID(currentRequirementID);

                        verificationRecordId++;

//       lfpt.testVerificationFile.write("id,testId,needId,needSelected,needResults,reqId,reqSupported,reqResults,testCaseName,testCaseIteration,testCaseResult");
                        testVerificationFile.write(verificationRecordId+"|"+testFileId+"|"+currentNeed.getUnID()+"|"+currentNeed.getUnSelected()+"|"+currentNeed.getResults()
                        +"|"+currentRequirement.getReqID()+"|"+currentRequirement.getSupported()+"|"+currentRequirement.getResults()
                        +"|"+thisTestResultsTestCase.getTestCaseID()+"|"+testCaseRunID + "|"+thisTestResultsTestCase.getResult()+"\n");
                        
//                        testResultsTestCaseList.add(thisTestResultsTestCase);
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            
            System.out.println("Commented out Test Results DAO Stuff.");
//            TestResultNeedsDAO thisTestResultNeedsDAO = new TestResultNeedsDAO(outdb);
//            thisTestResultNeedsDAO.insert(testResultNeedsList);
//
//            TestResultRequirementsDAO thisTestResultRequirementsDAO = new TestResultRequirementsDAO(outdb);
//            thisTestResultRequirementsDAO.insert(testResultRequirementsList);
//
//            TestResultsTestCaseDAO thisTestResultsTestCaseDAO = new TestResultsTestCaseDAO(outdb);
//            thisTestResultsTestCaseDAO.insert(testResultsTestCaseList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
