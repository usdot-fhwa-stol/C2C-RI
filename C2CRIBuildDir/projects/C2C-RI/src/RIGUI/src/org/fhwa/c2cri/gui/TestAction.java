/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.fhwa.c2cri.reports.LoggedMessage;
import org.fhwa.c2cri.reports.RawOTWMessage;
import org.fhwa.c2cri.reports.ScriptEvent;
import org.fhwa.c2cri.reports.UserEvent;
import javax.xml.stream.events.Attribute;
import java.util.Iterator;
import javax.xml.XMLConstants;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestAction {

    /**
     * The event tag.
     */
    private static String EVENT = "event";

    /**
     * The message tag.
     */
    private static String MESSAGETAG = "message";

    /**
     * The userevent tag.
     */
    private static String USEREVENT = "userEvent";

    /**
     * The scriptevent tag.
     */
    private static String SCRIPTEVENT = "scriptEvent";

    /**
     * The tag tag.
     */
    private static String TAG = "tag";

    /**
     * The src tag.
     */
    private static String SRC = "src";

    /**
     * The type tag.
     */
    private static String TYPE = "type";

    /**
     * The line tag.
     */
    private static String LINE = "line";

    /**
     * The column tag.
     */
    private static String COLUMN = "column";

    /**
     * The file tag.
     */
    private static String FILE = "file";

    /**
     * The testcasename tag.
     */
    private static String TESTCASENAME = "test-case-name";

    /**
     * The outcome tag.
     */
    private static String OUTCOME = "outcome";

    /**
     * The executiontime tag.
     */
    private static String EXECUTIONTIME = "execution-time";

    /**
     * The executiontimemillis tag.
     */
    private static String EXECUTIONTIMEMILLIS = "execution-time-millis";

    /**
     * The functionid tag.
     */
    private static String FUNCTIONID = "functionId";

    /**
     * The rawotwmessage tag.
     */
    private static String RAWOTWMESSAGE = "rawOTWMessage";

    /**
     * The loggedmessage tag.
     */
    private static String LOGGEDMESSAGE = "loggedMessage";

    /**
     * The parentdialog tag.
     */
    private static String PARENTDIALOG = "parentDialog";

    /**
     * The messagename tag.
     */
    private static String MESSAGENAME = "messageName";

    /**
     * The messagetype tag.
     */
    private static String MESSAGETYPE = "messageType";

    /**
     * The messageencoding tag.
     */
    private static String MESSAGEENCODING = "messageEncoding";

    /**
     * The viatransportprotocol tag.
     */
    private static String VIATRANSPORTPROTOCOL = "viaTransportProtocol";

    /**
     * The messagesourceaddress tag.
     */
    private static String MESSAGESOURCEADDRESS = "messageSourceAddress";

    /**
     * The messagedestinationaddress tag.
     */
    private static String MESSAGEDESTINATIONADDRESS = "messageDestinationAddress";

    /**
     * The relatedcommand tag.
     */
    private static String RELATEDCOMMAND = "relatedCommand";

    /**
     * The messagebody tag.
     */
    private static String MESSAGEBODY = "MessageBody";

    /**
     * The testcase tag.
     */
    private static String TESTCASE = "testCase";

    /**
     * The connectionname tag.
     */
    private static String CONNECTIONNAME = "connectionName";

    /**
     * The processtype tag.
     */
    private static String PROCESSTYPE = "ProcessType";

    /**
     * The sourceaddress tag.
     */
    private static String SOURCEADDRESS = "SourceAddress";

    /**
     * The destinationaddress tag.
     */
    private static String DESTINATIONADDRESS = "DestinationAddress";

    /**
     * The sequencecount attribute.
     */
    private static String SEQUENCECOUNT = "sequenceCount";

    /**
     * The timestampinmillis attribute.
     */
    private static String TIMESTAMPINMILLIS = "timestampInMillis";

    /**
     * The error tag.
     */
    private static String ERROR = "error";

    private String timestamp;
    private String message;
    private String result;
    private boolean processed;
    private boolean validType;
    private String rawEvent="";
    private int testIndex;

    public TestAction(String timeStamp, String message, String result, int testIndex) {
        this.timestamp = timeStamp;
        this.message = message;
        this.result = result;
        this.processed = true;
        this.validType = true;
        this.testIndex = testIndex;
    }

    public TestAction(long timeStamp, String event, int testIndex) {
        this.timestamp = new Date(timeStamp).toString();
        this.rawEvent = event;
        this.processed = false;
        this.testIndex = testIndex;
    }

    public String getTimestamp() {
        if (!processed) process();
        return timestamp;
    }

    public String getMessage() {
        if (!processed) process();
        return message;
    }

    public String getResult() {
        if (!processed) process();
        return result;
    }

    public boolean isValidType() {
        if (!processed) process();
        return validType;
    }

    public int getTestIndex() {
        return testIndex;
    }

    
    
    
    public static void main(String[] args) {
        Date now = new Date();
        TestAction ta1 = new TestAction(now.getTime(), "<userEvent>\n"
                + "User Started the Test with selected Test Case Scripts: Information Layer: TCS-1-dlCenterActiveVerificationRequest-EC-Valid</userEvent>",1);
        TestAction ta2 = new TestAction(now.getTime(), "            <scriptEvent>\n"
                + "               <tag src=\"test-case\" type=\"Begin\">\n"
                + "                  <line>14</line>\n"
                + "                  <column>58</column>\n"
                + "                  <file>jar:file:/C:/C2CRIDev/C2CRIBuildDir/projects/C2C-RI/src/RIGUI/dist/RIGUI.jar!/org/fhwa/c2cri/testmodel/TestCaseLauncher.xml</file>\n"
                + "\n"
                + "\t\t                <test-case-name>TCS-1-dlCenterActiveVerificationRequest-EC-Valid</test-case-name>\n"
                + "\n"
                + "\t\t                <outcome>PASSED</outcome>\n"
                + "\t\t                <execution-time>0.000s</execution-time>\n"
                + "\t\t                <execution-time-millis>0</execution-time-millis>\n"
                + "\t\t\n"
                + "\n"
                + "\t\t                <outcome>PASSED</outcome>\n"
                + "\t\t                <execution-time>0.000s</execution-time>\n"
                + "\t\t                <execution-time-millis>0</execution-time-millis>\n"
                + "\t\t\n"
                + "\t\t             </tag>\n"
                + "            </scriptEvent>",2);
        TestAction ta3 = new TestAction(now.getTime(), "<rawOTWMessage>\n"
                + "               <testCase>TCS-1-dlCenterActiveVerificationRequest-EC-Valid</testCase>\n"
                + "               <connectionName>http://C2CRIOC:8084/TMDDWS/TMDDWS.svc/OC</connectionName>\n"
                + "               <ProcessType>Request</ProcessType>\n"
                + "               <SourceAddress>127.0.0.1:60638</SourceAddress>\n"
                + "               <DestinationAddress>127.0.0.1:8084</DestinationAddress>\n"
                + "               <timestampInMillis>1630705383496</timestampInMillis>\n"
                + "               <sequenceCount>1</sequenceCount>\n"
                + "               <message>\n"
                + "\n"
                + "50 4F 53 54 20 2F 54 4D 44 44 57 53 2F 54 4D 44 :POST /TMDDWS/TMD\n"
                + "44 57 53 2E 73 76 63 2F 4F 43 20 48 54 54 50 2F :DWS.svc/OC HTTP/\n"
                + "31 2E 31 0D 0A 53 4F 41 50 41 63 74 69 6F 6E 3A :1.1..SOAPAction:\n"
                + "20 64 6C 43 65 6E 74 65 72 41 63 74 69 76 65 56 : dlCenterActiveV\n"
                + "65 72 69 66 69 63 61 74 69 6F 6E 52 65 71 75 65 :erificationReque\n"
                + "73 74 0D 0A 43 6F 6E 74 65 6E 74 2D 4C 65 6E 67 :st..Content-Leng\n"
                + "74 68 3A 20 33 35 31 30 0D 0A 43 6F 6E 74 65 6E :th: 3510..Conten\n"
                + "74 2D 54 79 70 65 3A 20 74 65 78 74 2F 78 6D 6C :t-Type: text/xml\n"
                + "3B 20 63 68 61 72 73 65 74 3D 75 74 66 2D 38 0D :; charset=utf-8.\n"
                + "0A 48 6F 73 74 3A 20 43 32 43 52 49 4F 43 3A 38 :.Host: C2CRIOC:8\n"
                + "30 38 34 0D 0A 43 6F 6E 6E 65 63 74 69 6F 6E 3A :084..Connection:\n"
                + "20 4B 65 65 70 2D 41 6C 69 76 65 0D 0A 55 73 65 : Keep-Alive..Use\n"
                + "72 2D 41 67 65 6E 74 3A 20 41 70 61 63 68 65 2D :r-Agent: Apache-\n"
                + "48 74 74 70 43 6C 69 65 6E 74 2F 34 2E 30 2E 33 :HttpClient/4.0.3\n"
                + "20 28 6A 61 76 61 20 31 2E 35 29 0D 0A 45 78 70 : (java 1.5)..Exp\n"
                + "65 63 74 3A 20 31 30 30 2D 43 6F 6E 74 69 6E 75 :ect: 100-Continu\n"
                + "65 0D 0A 0D 0A                                  :e....\n"
                + "\n"
                + "</message>\n"
                + "            </rawOTWMessage>",3);

        TestAction ta4 = new TestAction(now.getTime(), "<loggedMessage>\n"
                + "               <parentDialog>dlCenterActiveVerificationRequest</parentDialog>\n"
                + "               <messageName>REQUEST</messageName>\n"
                + "               <messageType>centerActiveVerificationRequestMsg</messageType>\n"
                + "               <messageEncoding>SOAP</messageEncoding>\n"
                + "               <viaTransportProtocol>HTTP</viaTransportProtocol>\n"
                + "               <messageSourceAddress>127.0.0.1:60638</messageSourceAddress>\n"
                + "               <messageDestinationAddress>127.0.0.1:8084</messageDestinationAddress>\n"
                + "               <relatedCommand/>\n"
                + "               <MessageBody>\n"
                + "\n"
                + "tmdd:centerActiveVerificationRequestMsg.authentication.user-id = UserId\n"
                + "tmdd:centerActiveVerificationRequestMsg.authentication.password = P@ssW0rd\n"
                + "tmdd:centerActiveVerificationRequestMsg.authentication.operator-id = StateDOT_OPRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-id = agency.com\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-name = State DOT\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-location = 1234567890\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-function = State DOT Traffic Subsystems\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.contact-id = StateDOTContactRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.person-name = John Doe\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.person-title = Software Engineer\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.phone-number = (123) 321 - 0987\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.phone-alternate = (123) 987 - 6543\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.mobile-phone-number = (456) 123 - 4567\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.mobile-phone-id = StateDOTMobilePhoneRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.fax-number = (123) 123 - 5467\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.pager-number = (800) 123 - 4567\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.pager-id = StatePagerRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.email-address = state@dot.gov\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.radio-unit = StateRadioDevice\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.address-line1 = 161 Main Street\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.address-line2 = Suite 200\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.city = StateCity\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.state = NM\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.zip-code = 12345\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.country = United States Of America\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-id = agency_test\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-name = State DOT Organized Center\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.latitude = 33964380\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.longitude = -84217945\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.horizontalDatum = wgs-84\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.height.altitude.kmDec = 1000.00\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.height.verticalDatum = 1\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-description = This traffic center manages traffic for the whole state.\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-type = 2\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.contact-id = StateDOTContactRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.person-name = John Doe\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.person-title = Software Engineer\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.phone-number = (123) 321 - 0987\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.phone-alternate = (123) 987 - 6543\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.mobile-phone-number = (456) 123 - 4567\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.mobile-phone-id = StateDOTMobilePhoneRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.fax-number = (123) 123 - 5467\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.pager-number = (800) 123 - 4567\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.pager-id = StatePagerRef\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.email-address = state@dot.gov\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.radio-unit = StateRadioDevice\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.address-line1 = 162 Main Street\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.address-line2 = Suite 200\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.city = StateCity\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.state = NM\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.zip-code = 12345\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.country = United States Of America\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.last-update-time.date = 20130531\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.last-update-time.time = 105805\n"
                + "tmdd:centerActiveVerificationRequestMsg.organization-requesting.last-update-time.offset = 01:00\n"
                + "   </MessageBody>\n"
                + "            </loggedMessage>",3);

        TestAction ta5 = new TestAction(now.getTime(), "<FakeMessage>\n",4);

        System.out.println(ta1.getMessage()+"  " + ta1.getResult());
        System.out.println(ta2.getMessage()+"  " + ta1.getResult());
        System.out.println(ta3.getMessage()+"  " + ta1.getResult());
        System.out.println(ta4.getMessage()+"  " + ta1.getResult());
        System.out.println(ta5.getMessage()+"  " + ta1.getResult());

    }

    private void process(){
        
        try {
            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            // Setup a new eventReader
            InputStream in = new ByteArrayInputStream(rawEvent.getBytes());
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext()) {
                XMLEvent xmlEvent = eventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    processEvent(startElement, eventReader);
                }

            }
            in.close();
            eventReader.close();
            this.processed = true;
            
        } catch (Exception ex) {
            this.processed = true;
            this.validType = false;            
        }        
    }
    
    
    
    private void processEvent(StartElement thisEvent, XMLEventReader eventReader) throws Exception {
        // We read the attributes from this tag and add the date
        // attribute to our object
        Iterator<Attribute> attributes = thisEvent.getAttributes();
        while (attributes.hasNext()) {
//            Attribute attribute = attributes.next();
//            if (attribute.getName().toString().equals(LEVEL)) {
//                eventSet.setLevel(attribute.getValue());
//            } else if (attribute.getName().toString().equals(LOGGER)) {
//                eventSet.setLogger(attribute.getValue());
//            } else if (attribute.getName().toString().equals(THREAD)) {
//                eventSet.setThread(attribute.getValue());
//            } else if (attribute.getName().toString().equals(TIMESTAMP)) {
//                eventSet.setTimestamp(attribute.getValue());
//            }
        }

        XMLEvent event = thisEvent;

        while (!(event.isEndDocument() && event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(EVENT)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(MESSAGETAG)) {
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(SCRIPTEVENT)) {
                        processScriptEvent(event, eventReader);
                        this.validType = true;
                        return;

                    } else if (event.asStartElement().getName().getLocalPart().equals(USEREVENT)) {
                        processUserEvent(event, eventReader);
                        this.validType = true;
                        return;

                    } else if (event.asStartElement().getName().getLocalPart().equals(RAWOTWMESSAGE)) {
                        processRawOTWMessage(event, eventReader);
                        this.validType = true;
                        return;

                    } else if (event.asStartElement().getName().getLocalPart().equals(LOGGEDMESSAGE)) {
                        processLoggedMessage(event, eventReader);
                        this.validType = true;
                        return;

                    }

                }
                if (eventReader.hasNext()) {
                    event = eventReader.nextEvent();
                } else {
                    break;
                }

            } catch (Exception ex) {
//                ex.printStackTrace();
                break;
            }
        }
        throw new Exception("Unable to process event with know event types.");
    }

    /**
     * Process script event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the script event
     */
    private void processScriptEvent(XMLEvent event, XMLEventReader eventReader) throws Exception {

        ScriptEvent thisEvent = new ScriptEvent();
        thisEvent.setExecutionTimeMillis(0);
        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(TAG))) {
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
        }
        scriptEventToTestAction(thisEvent);
    }

    /**
     *
     * @param event
     * @return
     */
    private void scriptEventToTestAction(ScriptEvent event) {
        String resultMsg = "";
        if (event.getSrc().equals("test-case")) {
            resultMsg = String.format("testCase - %s \nScript: %s\nline: %s column: %s", event.getType(), event.getFile(), event.getLine(), event.getColumn());
        } else {
            resultMsg = String.format("%s - %s \nScript: %s\nline: %s column: %s\nfunctionId: %s", event.getTag(), event.getType(), event.getFile(), event.getLine(), event.getColumn(), event.getFunctionId());
        }
        String result = (event.getType().equals("End") ? event.getOutcome() : "");
        this.message = resultMsg;
        this.result = result;
    }

    /**
     * Process user event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the user event
     */
    private void processUserEvent(XMLEvent event, XMLEventReader eventReader) throws Exception {
        UserEvent thisEvent = new UserEvent();

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

        userEventToTestAction(thisEvent);
    }

    /**
     *
     * @param event
     * @return
     */
    private void userEventToTestAction( UserEvent event) {
        String resultMsg = event.getDescription();
        String result = "";
        this.message = resultMsg;
        this.result = result;
    }

    /**
     * Process rawotwmessage tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the raw otw message
     */
    private void processRawOTWMessage(XMLEvent event, XMLEventReader eventReader) throws Exception {

        RawOTWMessage thisRawMessageEvent = new RawOTWMessage();
        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(RAWOTWMESSAGE))) {

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
                    } else if (startElement.getName().getLocalPart().equals(MESSAGETAG)) {
                        if (!event.isCharacters()) {
                            event = eventReader.nextEvent();  // Get To CDATA!! (Sometimes Varies by Parser whether this step is needed)
                        }
                        StringBuilder sb = new StringBuilder();
                        if (event.isCharacters()) {
                            sb.append(event.asCharacters().getData());
                            while (eventReader.peek().isCharacters()) {
                                event = eventReader.nextEvent();
                                sb.append(event.asCharacters().getData());
                            };
                            thisRawMessageEvent.setMessage(sb.toString());
                        }

                    }
                }

        }
        rawOTWMessageEventToTestAction(thisRawMessageEvent);
    }

    /**
     *
     * @param event
     * @return
     */
    private void rawOTWMessageEventToTestAction(RawOTWMessage event) {
        String resultMsg = String.format("Over the Wire Message\n\nTestCase: %s\nConnectionName: %s\nSourceAddress: %s\nDestinationAddress: %s\nTimeStampInMilliseconds: %s\nSequenceCount: %s\nMessage:\n\n%s", event.getTestCase(), event.getConnectionName(), event.getSourceAddress(), event.getDestinationAddress(), event.getTimeStampInMillis(), event.getSequenceCount(), event.getMessage());
        String result = "";
        this.message = resultMsg;
        this.result = result;
    }

    /**
     * Process the loggedmessage tag.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the logged message
     */
    private void processLoggedMessage(XMLEvent event, XMLEventReader eventReader) throws Exception{

        LoggedMessage thisLoggedMessageEvent = new LoggedMessage();
        while (eventReader.hasNext() && !(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(LOGGEDMESSAGE))) {
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

                        if (!event.isCharacters()) {
                            event = eventReader.nextEvent();  // CDATA!!
                        }
                        StringBuilder sb = new StringBuilder();
                        if (event.isCharacters()) {
                            sb.append(event.asCharacters().getData());
                            while (eventReader.peek().isCharacters()) {
                                event = eventReader.nextEvent();
                                sb.append(event.asCharacters().getData());
                            }
                            thisLoggedMessageEvent.setMessageBody(sb.toString());
                        }
                    }
                }
        }
        loggedMessageEventToTestAction(thisLoggedMessageEvent);
    }

    /**
     *
     * @param event
     * @return
     */
    private void loggedMessageEventToTestAction(LoggedMessage event) {
        String resultMsg = String.format("Logged Message\n\nDialog: %s\nMessage Type: %s\nMessage Transport Encoding: %s\nMessage Transport Protocol: %s\nMessage Transport Source Address: %s\nMessage Transport Destination Address: %s\nDecoded Message:\n\n%s", event.getParentDialog(), event.getMessageType(), event.getMessageEncoding(), event.getViaTransportProtocol(), event.getMessageSourceAddress(), event.getMessageDestinationAddress(), event.getMessageBody());
        String result = "";
        this.message = resultMsg;
        this.result = result;
    }

}
