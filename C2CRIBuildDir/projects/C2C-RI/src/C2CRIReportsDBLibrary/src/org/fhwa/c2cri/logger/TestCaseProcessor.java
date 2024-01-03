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
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPathExpressionException;
import org.fhwa.c2cri.utilities.ProgressMonitor;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.Parameter;
import org.fhwa.c2cri.reports.C2CRIAppLayerTestCaseData;
import org.fhwa.c2cri.reports.C2CRIAppLayerTestCaseDescription;
import org.fhwa.c2cri.reports.C2CRIInfoLayerTestCaseData;
import org.fhwa.c2cri.reports.C2CRIInfoLayerTestCaseDescription;
import org.fhwa.c2cri.reports.dao.C2CRIAppLayerTestCaseDataDAO;
import org.fhwa.c2cri.reports.dao.C2CRIAppLayerTestCaseDescriptionsDAO;
import org.fhwa.c2cri.reports.dao.C2CRIInfoLayerTestCaseDataDAO;
import org.fhwa.c2cri.reports.dao.C2CRIInfoLayerTestCaseDescriptionsDAO;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataParser;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataSource;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseFile;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The Class TestCaseProcessor processes the contents of a Test Case xml file and
 * places the contents into the c2cri database for report generation.
 *
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestCaseProcessor {

    /**
     * The TEST Case Descriptions tag.
     */
    private static String TESTCASEDESCRIPTIONS = "TestCaseDescriptions";

    /**
     * The Test Case Description tag.
     */
    private static String TESTCASEDESCRIPTION = "TestCaseDescription";

    /**
     * The Test Case ID tag.
     */
    private static String TESTCASEID = "testCaseId";

    /**
     * The internal test Case Description tag.
     */
    private static String INTERNALTESTCASEDESCRIPTION = "testCaseDescription";

    /**
     * The test case items tag.
     */
    private static String TESTCASEITEMS = "testCaseItems";

    /**
     * The test case inputs tag.
     */
    private static String TESTCASEINPUTS = "testCaseInputs";

    /**
     * The test case input procedures tag.
     */
    private static String TESTCASEINPUTPROCEDURES = "testCaseInputProcedures";

    /**
     * The test case outputs tag.
     */
    private static String TESTCASEOUTPUTS = "testCaseOutputs";

    /**
     * The test case input procedures tag.
     */
    private static String TESTCASEOUTPUTPROCEDURES = "testCaseOutputProcedures";
    /**
     * The test case hardware tag.
     */
    private static String TESTCASEHARDWARE = "testCaseHardware";
    /**
     * The test case software tag.
     */
    private static String TESTCASESOFTWARE = "testCaseSoftware";

    /**
     * The Test Case Other tag.
     */
    private static String TESTCASEOTHER = "testCaseOther";

    /**
     * The Test Case Special tag.
     */
    private static String TESTCASESPECIAL = "testCaseSpecial";

    /**
     * The Test Case Intercase Dependency tag.
     */
    private static String TESTCASEINTERCASE = "testCaseIntercase";

    /**
     * The output database file.
     */
    private File outdb;

    /**
     * The test case configuration information.
     */
    private TestCaseConfiguration testCaseConfiguration;

    /**
     * The event id.
     */
    private int eventId = 1;

    /**
     * The event reader.
     */
    private XMLEventReader eventReader = null;

    /**
     * The event.
     */
    private XMLEvent event = null;


    /**
     * The app layer test descriptions dao.
     */
    private C2CRIAppLayerTestCaseDescriptionsDAO appLayerTestCaseDescriptionsDAO;

    /**
     * The info layer test descriptions dao.
     */
    private C2CRIInfoLayerTestCaseDescriptionsDAO infoLayerTestCaseDescriptionsDAO;

    
    /**
     * The info layer test data dao.
     */
    private C2CRIInfoLayerTestCaseDataDAO infoLayerTestCaseDataDAO;

    /**
     * The app layer test data dao.
     */
    private C2CRIAppLayerTestCaseDataDAO appLayerTestCaseDataDAO;
    
    /**
     * Assigns a unique index to each applicable test case.
     */
    private int testCaseIndexCounter;
    
    /**
     * Instantiates a new Test Case processor.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param testCaseConfiguration the test case configuration information
     * @param databaseName the database name
     */
    public TestCaseProcessor(TestCaseConfiguration testCaseConfiguration, String databaseName) {
        try {
            File sourcedb = new File(databaseName);
            outdb = new File("./temp.db3");

            // Make a temporary copy of C2C RI SQLite database
			try (FileChannel sourceCh = new FileInputStream(sourcedb).getChannel();
				 FileChannel destCh = new FileOutputStream(outdb).getChannel())
			{
				sourceCh.transferTo(0, sourceCh.size(), destCh);
			}

            // Process the Application Layer Test Cases
            this.testCaseConfiguration = testCaseConfiguration;
            if ((this.testCaseConfiguration.getBaseApplicationTestCaseURL() != null) && !exists(this.testCaseConfiguration.getBaseApplicationTestCaseURL().toString())) {
                throw new Exception("Base Application Layer test Case file " + this.testCaseConfiguration.getBaseApplicationTestCaseURL().toString() + " does not exist.");
            }

            if ((this.testCaseConfiguration.getExtensionApplicationTestCaseURL() != null) && !exists(this.testCaseConfiguration.getExtensionApplicationTestCaseURL().toString())) {
                throw new Exception("Extension Application Layer test Case file " + this.testCaseConfiguration.getExtensionApplicationTestCaseURL() + " does not exist.");
            }

            if ((this.testCaseConfiguration.getApplicableAppplicationTestCases() == null)) {
                throw new Exception("The Application Layer test Case List is null. ");
            }

            if ((this.testCaseConfiguration.getBaseInformationTestCaseURL() != null) && !exists(this.testCaseConfiguration.getBaseInformationTestCaseURL().toString())) {
                throw new Exception("Base Information Layer test Case file " + this.testCaseConfiguration.getBaseInformationTestCaseURL().toString() + " does not exist.");
            }

            if ((this.testCaseConfiguration.getExtensionInformationTestCaseURL() != null) && !exists(this.testCaseConfiguration.getExtensionInformationTestCaseURL().toString())) {
                throw new Exception("Extension Information Layer test Case file " + this.testCaseConfiguration.getExtensionInformationTestCaseURL() + " does not exist.");
            }

            if ((this.testCaseConfiguration.getApplicableInformationTestCases() == null)) {
                throw new Exception("The Information Layer test Case List is null. ");
            }
            appLayerTestCaseDescriptionsDAO = new C2CRIAppLayerTestCaseDescriptionsDAO(outdb);
            infoLayerTestCaseDescriptionsDAO = new C2CRIInfoLayerTestCaseDescriptionsDAO(outdb);
            appLayerTestCaseDataDAO = new C2CRIAppLayerTestCaseDataDAO(outdb);
            infoLayerTestCaseDataDAO = new C2CRIInfoLayerTestCaseDataDAO(outdb);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Determines whether the file at the given URL exists..
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param logFileName the log file name
     * @return true if the file exists at the URL.
     */
    private boolean exists(String urlName) {
        boolean result = false;
        try {
            URL inputurl = new URL(urlName);

            InputStream input = inputurl.openStream();
            input.close();
            System.out.println("SUCCESS");
            result = true;

        } catch (Exception ex) {
        }
        return result;
    }

    /**
     * Determines whether the file at the given path exists.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param fileName the log file name
     * @return true if the file exists at the URL.
     */
    private boolean fileExists(String fileName) {
        boolean result = false;

        File testCaseFile = new File(fileName);
        if (testCaseFile.exists()) {
            result = true;
        }

        return result;
    }

    /**
     * Determines the number of events contained within the test case file. This
     * method currently uses xQuery to quickly run through the test case file and get
     * a count of event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param testCaseFileLocation the test case file name
     * @return the long
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XPathExpressionException the x path expression exception
     */
    private Long queryTestCaseDescriptions(InputStream testCaseFile) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, XPathFactoryConfigurationException {
        // Standard of reading a XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        XPathExpression expr = null;
        builder = factory.newDocumentBuilder();
        doc = builder.parse(testCaseFile);

        // Create a XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance(
            XPathFactory.DEFAULT_OBJECT_MODEL_URI,
            "net.sf.saxon.xpath.XPathFactoryImpl",
            ClassLoader.getSystemClassLoader());  
        
        // Create a XPath object
        XPath xpath = xFactory.newXPath();

        // Compile the XPath expression
        expr = xpath.compile("count(/TestCaseDescriptions/TestCaseDescription)");
        // Run the query and get a nodeset
        Double result = (Double) expr.evaluate(doc, XPathConstants.NUMBER);

        return result.longValue();
    }

    /**
     * Write out test case data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public void writeTestCasesToTempDB() throws SQLException, Exception {
        eventId = 0;
        if ((this.testCaseConfiguration.getBaseApplicationTestCaseURL() != null) && exists(this.testCaseConfiguration.getBaseApplicationTestCaseURL().toString())) {
            writeAppLayerTestCasesToTempDB(this.testCaseConfiguration.getBaseApplicationTestCaseURL());
        }

        if ((this.testCaseConfiguration.getExtensionApplicationTestCaseURL() != null) && exists(this.testCaseConfiguration.getExtensionApplicationTestCaseURL().toString())) {
            writeAppLayerTestCasesToTempDB(this.testCaseConfiguration.getExtensionApplicationTestCaseURL());
        }

        eventId = 0;
        if ((this.testCaseConfiguration.getBaseInformationTestCaseURL() != null) && exists(this.testCaseConfiguration.getBaseInformationTestCaseURL().toString())) {
            writeInfoLayerTestCasesToTempDB(this.testCaseConfiguration.getBaseInformationTestCaseURL());
        }

        if ((this.testCaseConfiguration.getExtensionInformationTestCaseURL() != null) && exists(this.testCaseConfiguration.getExtensionInformationTestCaseURL().toString())) {
            writeInfoLayerTestCasesToTempDB(this.testCaseConfiguration.getExtensionInformationTestCaseURL());
        }
        
        testCaseIndexCounter = 0;
        processTestCaseData(false); // Information Layer Test Case Data
        processTestCaseData(true);  // Application Layer Test Case Data
    }

    /**
     * Write out application layer test case data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public int writeAppLayerTestCasesToTempDB(URL fileURL) throws SQLException, Exception {
        try {
            LinkedHashMap<String,C2CRIAppLayerTestCaseDescription> testCaseMap = new LinkedHashMap();
 
            // note that we create the dialog without an owning frame; normally you
            // would associate it with some other frame in your application
            ProgressMonitor monitor = ProgressMonitor.getInstance(
                    null,
                    "C2C RI",
                    "Processing Application Layer Test Cases.",
                    ProgressMonitor.Options.CENTER,
                    ProgressMonitor.Options.SHOW_STATUS,
                    ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

            // because we didn't set an initial progress value, the dialog will
            // appear in indeterminate ("Cylon") mode; let it do that for a while            
            Long result = queryTestCaseDescriptions(fileURL.openStream());

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(fileURL.openStream());
            // Read the XML document

            while (eventReader.hasNext()) {
                C2CRIAppLayerTestCaseDescription eventSet = new C2CRIAppLayerTestCaseDescription();
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (TESTCASEDESCRIPTION.equals(startElement.getName().getLocalPart())) {
                        eventSet = processAppEvent();
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (TESTCASEDESCRIPTION.equals(endElement.getName().getLocalPart())) {

                        if (!testCaseMap.containsKey(eventSet.getTestCaseName())){
                            testCaseMap.put(eventSet.getTestCaseName(), eventSet);                            
                        }

                    }
                }

            }

            for (TestCaseInformation tci : testCaseConfiguration.getApplicableAppplicationTestCases()) {
                eventId++;
                if (testCaseMap.containsKey(tci.getTestCaseName())){
                    C2CRIAppLayerTestCaseDescription thisTestCase = testCaseMap.get(tci.getTestCaseName());
                    thisTestCase.setId(eventId);
                    appLayerTestCaseDescriptionsDAO.insert(thisTestCase);
                } else {
                    C2CRIAppLayerTestCaseDescription emptyTestCase = new C2CRIAppLayerTestCaseDescription();
                    emptyTestCase.setId(eventId);
                    emptyTestCase.setTestCaseName(tci.getTestCaseName() + " (Not Specified within Test Suite");
                    emptyTestCase.setTestCaseDescription("Not Available");
                    emptyTestCase.setDependencyDescription("Not Available");
                    emptyTestCase.setHardwareEnvironmental("Not Available");
                    emptyTestCase.setInputProcedures("Not Available");
                    emptyTestCase.setInputs("Not Available");
                    emptyTestCase.setItemList("Not Available");
                    emptyTestCase.setOtherEnvironmental("Not Available");
                    emptyTestCase.setOutputProcedures("Not Available");
                    emptyTestCase.setOutputs("Not Available");
                    emptyTestCase.setSoftwareEnvironmental("Not Available");
                    emptyTestCase.setSpecialProcedureRequirements("Not Available");
                    appLayerTestCaseDescriptionsDAO.insert(emptyTestCase);
                }
                monitor.setProgress(0, eventId, result.intValue());
                monitor.setStatus("Checking Test Case " + (eventId - 1) + " of " + result.intValue());
            }
            
            appLayerTestCaseDescriptionsDAO.flush();
            eventReader.close();

            monitor.hide();
            monitor.dispose();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + e.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + e.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        }
        return eventId;
    }

    /**
     * Write out information layer test case data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public int writeInfoLayerTestCasesToTempDB(URL fileURL) throws SQLException, Exception {
        try {

            LinkedHashMap<String,C2CRIInfoLayerTestCaseDescription> testCaseMap = new LinkedHashMap();

            // note that we create the dialog without an owning frame; normally you
            // would associate it with some other frame in your application
            ProgressMonitor monitor = ProgressMonitor.getInstance(
                    null,
                    "C2C RI",
                    "Processing Information Layer Test Cases.",
                    ProgressMonitor.Options.CENTER,
                    ProgressMonitor.Options.SHOW_STATUS,
                    ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

            // because we didn't set an initial progress value, the dialog will
            // appear in indeterminate ("Cylon") mode; let it do that for a while            
            Long result = queryTestCaseDescriptions(fileURL.openStream());

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(fileURL.openStream());
            // Read the XML document

            while (eventReader.hasNext()) {
                C2CRIInfoLayerTestCaseDescription eventSet = new C2CRIInfoLayerTestCaseDescription();
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (TESTCASEDESCRIPTION.equals(startElement.getName().getLocalPart())) {
                        eventSet = processInfoEvent();
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (TESTCASEDESCRIPTION.equals(endElement.getName().getLocalPart())) {

                        if (!testCaseMap.containsKey(eventSet.getTestCaseName())){
                            testCaseMap.put(eventSet.getTestCaseName(), eventSet);                            
                        }

                    }
                }

            }

            for (TestCaseInformation tci : testCaseConfiguration.getApplicableInformationTestCases()) {
               eventId++;
                if (testCaseMap.containsKey(tci.getTestCaseName())){
                    C2CRIInfoLayerTestCaseDescription thisTestCase = testCaseMap.get(tci.getTestCaseName());
                    thisTestCase.setId(eventId);
                    infoLayerTestCaseDescriptionsDAO.insert(testCaseMap.get(tci.getTestCaseName()));
                } else {
                    C2CRIInfoLayerTestCaseDescription emptyTestCase = new C2CRIInfoLayerTestCaseDescription();
                    emptyTestCase.setId(eventId);
                    emptyTestCase.setTestCaseName(tci.getTestCaseName() + " (Not Specified in Test Suite)");
                    emptyTestCase.setTestCaseDescription("Not Available");
                    emptyTestCase.setDependencyDescription("Not Available");
                    emptyTestCase.setHardwareEnvironmental("Not Available");
                    emptyTestCase.setInputProcedures("Not Available");
                    emptyTestCase.setInputs("Not Available");
                    emptyTestCase.setItemList("Not Available");
                    emptyTestCase.setOtherEnvironmental("Not Available");
                    emptyTestCase.setOutputProcedures("Not Available");
                    emptyTestCase.setOutputs("Not Available");
                    emptyTestCase.setSoftwareEnvironmental("Not Available");
                    emptyTestCase.setSpecialProcedureRequirements("Not Available");
                    infoLayerTestCaseDescriptionsDAO.insert(emptyTestCase);
                }
                monitor.setProgress(0, eventId, result.intValue());
                monitor.setStatus("Checking Test Case " + (eventId - 1) + " of " + result.intValue());
            }
 
            infoLayerTestCaseDescriptionsDAO.flush();
            eventReader.close();

            monitor.hide();
            monitor.dispose();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + e.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + e.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        }
        return eventId;
    }

    /**
     * Process Application Test Case event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the test case description 
     * @throws SQLException the sQL exception
     */
    private C2CRIAppLayerTestCaseDescription processAppEvent() throws SQLException {
        C2CRIAppLayerTestCaseDescription eventSet = new C2CRIAppLayerTestCaseDescription();
        eventSet.setId(eventId);

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTCASEDESCRIPTION)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTCASEID)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setTestCaseName(event.asCharacters().getData());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(INTERNALTESTCASEDESCRIPTION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setTestCaseDescription(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEITEMS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setItemList(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEINPUTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setInputs(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEINPUTPROCEDURES)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setInputProcedures(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEOUTPUTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setOutputs(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEOUTPUTPROCEDURES)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setOutputProcedures(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEHARDWARE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setHardwareEnvironmental(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASESOFTWARE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setSoftwareEnvironmental(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEOTHER)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setOtherEnvironmental(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASESPECIAL)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setSpecialProcedureRequirements(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEINTERCASE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setDependencyDescription(fullText.toString());
                        }
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
     * Process Information Layer Test Case event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the test case description
     * @throws SQLException the sQL exception
     */
    private C2CRIInfoLayerTestCaseDescription processInfoEvent() throws SQLException {
        C2CRIInfoLayerTestCaseDescription eventSet = new C2CRIInfoLayerTestCaseDescription();
        eventSet.setId(eventId);

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTCASEDESCRIPTION)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTCASEID)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setTestCaseName(event.asCharacters().getData());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(INTERNALTESTCASEDESCRIPTION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setTestCaseDescription(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEITEMS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setItemList(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEINPUTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setInputs(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEINPUTPROCEDURES)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setInputProcedures(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEOUTPUTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setOutputs(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEOUTPUTPROCEDURES)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setOutputProcedures(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEHARDWARE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setHardwareEnvironmental(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASESOFTWARE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setSoftwareEnvironmental(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEOTHER)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setOtherEnvironmental(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASESPECIAL)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setSpecialProcedureRequirements(fullText.toString());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTCASEINTERCASE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()) {
                                event = eventReader.nextEvent();
                                if (event.isCharacters()) {
                                    fullText.append(event.asCharacters().getData());
                                }
                            }
                            eventSet.setDependencyDescription(fullText.toString());
                        }
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

    private TestCaseFile baseTCFile;
    /**
     * The iteration list.
     */
    private ArrayList<ArrayList<LinkedHashMap<String, Parameter>>> iterationGroupParameterList = new ArrayList<ArrayList<LinkedHashMap<String, Parameter>>>();

    /**
     * The group array list.
     */
    private ArrayList<String[]> iterationGroupArrayList = new ArrayList<String[]>();

    /**
     * The user tc file.
     */
    TestCaseFile userTCFile;

    /**
     * The more rows available.
     */
    boolean moreRowsAvailable;

    /**
     * Process the test case data files and write the content to the database.
     * 
     * @param generateAppLayerTestCaseData flag indicating whether application layer test case data is being processed.
     */
    public void processTestCaseData(boolean generateAppLayerTestCaseData) {
        int dataElementIndex = 0;

        for (TestCaseInformation tci : (generateAppLayerTestCaseData ? testCaseConfiguration.getApplicableAppplicationTestCases():testCaseConfiguration.getApplicableInformationTestCases())) {
            testCaseIndexCounter++;
            try {
                                
                String secondaryTestCaseFile = tci.getExtensionTestCaseDataFile();
                
                if (tci.getBaseTestCaseDataFile() != null){
                    baseTCFile = new TestCaseFile(tci.getBaseTestCaseDataFile().toURI());
                    baseTCFile.init();
                   
                    if ((tci.getExtensionTestCaseDataFile() != null) && (!tci.getExtensionTestCaseDataFile().isEmpty())){
                        secondaryTestCaseFile = tci.getExtensionTestCaseDataFile();
                    }
                } else if ((tci.getExtensionTestCaseDataFile() != null) && (!tci.getExtensionTestCaseDataFile().isEmpty())) {
                    baseTCFile = new TestCaseFile(tci.getExtensionTestCaseDataFile());
                    baseTCFile.init();                   
                } else {
                    break;
                }
                                
                int n = TestCaseDataParser.parsePropertyFile(baseTCFile);

                if (n > 0) {
                    if ((secondaryTestCaseFile != null) && (!secondaryTestCaseFile.isEmpty())) {
                        userTCFile = new TestCaseFile(tci.getExtensionTestCaseDataFile());

                        userTCFile.init();

                        n = TestCaseDataParser.parsePropertyFile(userTCFile);

                        if (n > 0) {
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(null,
                                    "Could not parse property file:\n" + tci.getExtensionTestCaseDataFile(),
                                    "Error in Property File",
                                    javax.swing.JOptionPane.ERROR_MESSAGE);
                        }

                    }

                    mergeFiles();

                } else {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Could not parse property file:\n" + tci.getBaseTestCaseDataFile().toString(),
                            "Error in Property File",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }

                if (n > 0) {

                    for (int ll = 0; ll < baseTCFile.numIteration(); ll++) {
                        for (int jj = 0; jj < baseTCFile.iterationAt(ll).numGroups(); jj++) {
                            for (int kk = 0; kk < baseTCFile.iterationAt(ll).groupAt(jj).numParameters(); kk++) {
                                if (jj == 0) {
                                    Parameter theParameter = baseTCFile.iterationAt(ll).groupAt(jj).parameterAt(kk);
                                    String finalValue = theParameter.getValue();
                                    if (theParameter.getValue().contains(TestCaseDataSource.MESSAGESPECVALUE)) {
                                        finalValue = getMessageStringFromSpec(theParameter.getValue(), ll);
                                    } else if (theParameter.getValue().contains(TestCaseDataSource.VALUESPECVALUE)) {
                                        finalValue = getValueStringFromSpec(theParameter.getValue(), ll);
                                    }

                                    if (generateAppLayerTestCaseData){
                                        dataElementIndex++;
                                        C2CRIAppLayerTestCaseData theData = new C2CRIAppLayerTestCaseData();
                                        theData.setId(dataElementIndex);
                                        theData.setStandard("ApplicationLayer");
                                        theData.setTestCase(tci.getTestCaseName());
                                        theData.setIteration(Integer.toString(ll+1));
                                        theData.setVariableName(theParameter.getName());
                                        theData.setDescription(theParameter.getDoc());
                                        theData.setDataType(theParameter.getType());
                                        theData.setVariableValue(finalValue);
                                        theData.setValidValues("");
                                        theData.setTestCaseIndex(testCaseIndexCounter);
                                        appLayerTestCaseDataDAO.insert(theData);
                                    } else {
                                         dataElementIndex++;
                                        C2CRIInfoLayerTestCaseData theData = new C2CRIInfoLayerTestCaseData();
                                        theData.setId(dataElementIndex);
                                        theData.setStandard("InformationLayer");
                                        theData.setTestCase(tci.getTestCaseName());
                                        theData.setIteration(Integer.toString(ll+1));
                                        theData.setVariableName(theParameter.getName());
                                        theData.setDescription(theParameter.getDoc());
                                        theData.setDataType(theParameter.getType());
                                        theData.setVariableValue(finalValue);
                                        theData.setValidValues("");
                                        theData.setTestCaseIndex(testCaseIndexCounter);
                                        infoLayerTestCaseDataDAO.insert(theData);                                       
                                    }                                    
                                }
                            }
                        }
                    }
                    if (generateAppLayerTestCaseData){
                        appLayerTestCaseDataDAO.flush();
                    } else {
                        infoLayerTestCaseDataDAO.flush();
                    }
               } else {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Could not parse property file:\n" + tci.getBaseTestCaseDataFile().toURI().toString(),
                            "Error in Property File",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }

                iterationGroupArrayList.clear();
                iterationGroupParameterList.clear();
 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Merge user and base test case data files.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private void mergeFiles() {
        // Create a Map of the Base Test Case Data File
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++) {

            // Create an Array of groups and a HashMap of parameters (maintaining order) for each iteration.
            String[] iterationGroupArray = new String[baseTCFile.iterationAt(ii).numGroups()];
            ArrayList<LinkedHashMap<String, Parameter>> groupParameterList = new ArrayList<LinkedHashMap<String, Parameter>>();

            // Iterate through each group that exists within the current iteration of the base Test Case Data File.
            for (int jj = 0; jj < baseTCFile.iterationAt(ii).numGroups(); jj++) {
                // Add the group to the array
                iterationGroupArray[jj] = baseTCFile.iterationAt(ii).groupAt(jj).getName();

                // Create a parameter map which stores each parameter defined within this group and this iteration.
                LinkedHashMap<String, Parameter> parameterMap = new LinkedHashMap<String, Parameter>();
                for (int kk = 0; kk < baseTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++) {
                    Parameter theParameter = baseTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk);
                    parameterMap.put(theParameter.getName(), theParameter);
                }

                // Add the parameter name and all of its details to the group list.
                groupParameterList.add(parameterMap);
            }
            // Store the current set of groups and parameters.
            iterationGroupArrayList.add(iterationGroupArray);
            iterationGroupParameterList.add(groupParameterList);
        }

        // The user can not add additional iterations or groups to those in the base file, but the user can rename a group (for message specification changes).
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++) {
            for (int jj = 0; jj < baseTCFile.iterationAt(ii).numGroups(); jj++) {

                if (userTCFile != null && userTCFile.numIteration() > ii) {
                    // The number of groups should be 1 greater than the group (zero-based) index.
                    if (userTCFile.iterationAt(ii).numGroups() > jj) { //

                        // If the user file changes this group name, then update the groupname stored in the array.
                        // Also, clear the existing parameter list at this group index and this iteration.
                        if (!iterationGroupArrayList.get(ii)[jj].equals(userTCFile.iterationAt(ii).groupAt(jj).getName())) {
                            iterationGroupArrayList.get(ii)[jj] = userTCFile.iterationAt(ii).groupAt(jj).getName();
                            iterationGroupParameterList.get(ii).get(jj).clear();
                        }

                        // Get a copy of the parametr map fir this index and group.
                        LinkedHashMap<String, Parameter> parameterMap = iterationGroupParameterList.get(ii).get(jj);

                        // loop through all parameters for this group in the user test case file.
                        for (int kk = 0; kk < userTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++) {
                            Parameter theParameter = userTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk);

                            // If the user file attempts to replace a base variable then verify that it is editable
                            if (parameterMap.containsKey(theParameter.getName())) {
                                Parameter originalParameter = parameterMap.get(theParameter.getName());
                                if (originalParameter.isEditable()) {
                                    iterationGroupParameterList.get(ii).get(jj).put(theParameter.getName(), theParameter);
                                    System.out.println("Updating a parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + kk);
                                }
                            } else {  // Add the new user file parameter
                                iterationGroupParameterList.get(ii).get(jj).put(theParameter.getName(), theParameter);
                                System.out.println("Adding a brand new user parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + kk);
                            }
                        }

                    } else {  // Skip extra groups
                    }
                } else { // Skip extra iterations
                }
            }
        }
    }

    /**
     * Return a concatenated list of test case value specifications.
     * 
     * @param inputValue
     * @param iterationNumber
     * @return the contents of the value specification included in the test case data files 
     */
    private String getValueStringFromSpec(String inputValue, int iterationNumber) {
        String result = "";
        String groupName = inputValue.replace(TestCaseDataSource.VALUESPECVALUE, "");
        for (int jj = 0; jj < baseTCFile.iterationAt(iterationNumber).numGroups(); jj++) {
            if (baseTCFile.iterationAt(iterationNumber).groupAt(jj).getName().equals(groupName)) {
                for (int kk = 0; kk < baseTCFile.iterationAt(iterationNumber).groupAt(jj).numParameters(); kk++) {
                    Parameter thisParameter = baseTCFile.iterationAt(iterationNumber).groupAt(jj).parameterAt(kk);
                    result = result.concat(result.isEmpty() ? "ValueSpecification: [ " + thisParameter.getName() + " = " + thisParameter.getValue() : "; " + thisParameter.getName() + " = " + thisParameter.getValue());
                }
            }
        }
        result = result.concat(!result.isEmpty() ? "]" : "");
        return result;
    }

    /**
     * Return a concatenated list of message specification values.
     * 
     * @param inputValue
     * @param iterationNumber
     * @return the contents of the message specification detailed in the test case data files.
     */
    private String getMessageStringFromSpec(String inputValue, int iterationNumber) {
        String result = "";
        String groupName = inputValue.replace(TestCaseDataSource.MESSAGESPECVALUE, "");
        for (int jj = 0; jj < baseTCFile.iterationAt(iterationNumber).numGroups(); jj++) {
            if (baseTCFile.iterationAt(iterationNumber).groupAt(jj).getName().equals(groupName)) {
                for (int kk = 0; kk < baseTCFile.iterationAt(iterationNumber).groupAt(jj).numParameters(); kk++) {
                    Parameter thisParameter = baseTCFile.iterationAt(iterationNumber).groupAt(jj).parameterAt(kk);
                    result = result.concat(result.isEmpty() ? "MessageSpecification: [ " + thisParameter.getName() + " = " + thisParameter.getValue() : "; " + thisParameter.getName() + " = " + thisParameter.getValue());
                }

            }
        }
        result = result.concat(!result.isEmpty() ? "]" : "");

        return result;
    }

}
