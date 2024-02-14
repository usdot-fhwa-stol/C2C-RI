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
import org.fhwa.c2cri.reports.C2CRIAppLayerTestProcedure;
import org.fhwa.c2cri.reports.C2CRIAppLayerTestProcedureStep;
import org.fhwa.c2cri.reports.C2CRIInfoLayerTestProcedure;
import org.fhwa.c2cri.reports.C2CRIInfoLayerTestProcedureStep;
import org.fhwa.c2cri.reports.dao.C2CRIAppLayerTestProcedureStepsDAO;
import org.fhwa.c2cri.reports.dao.C2CRIAppLayerTestProceduresDAO;
import org.fhwa.c2cri.reports.dao.C2CRIInfoLayerTestProcedureStepsDAO;
import org.fhwa.c2cri.reports.dao.C2CRIInfoLayerTestProceduresDAO;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The Class TestProcedureProcessor processes the contents of a Test Procedure xml file and
 * places the contents into the c2cri database for report generation.
 *
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestProcedureProcessor {

    /**
     * The TEST Procedure Descriptions tag.
     */
    private static String TESTPROCEDUREDESCRIPTIONS = "TestProcedureDescriptions";

    /**
     * The Test Procedure Description tag.
     */
    private static String TESTPROCEDUREDESCRIPTION = "TestProcedureDescription";

    /**
     * The Test Procedure Name tag.
     */
    private static String TESTPROCEDURENAME = "testProcedureName";

    /**
     * The test procedure id tag.
     */
    private static String TESTPROCEDUREID = "testProcedureId";

    /**
     * The internal test Procedure Description tag.
     */
    private static String INTERNALTESTPROCEDUREDESCRIPTION = "testProcedureDescription";

    /**
     * The requirements tag.
     */
    private static String REQUIREMENTS = "Requirements";

    /**
     * The variables tag.
     */
    private static String VARIABLES = "Variables";

    /**
     * The pass fail criteria tag.
     */
    private static String PASSFAILCRITERIA = "PassFailCriteria";

    /**
     * The test procedure steps tag.
     */
    private static String TESTPROCEDURESTEPS = "TestProcedureSteps";

    /**
     * The test procedure step tag.
     */
    private static String TESTPROCEDURESTEP = "TestProcedure";

    /**
     * The test procedure step tag.
     */
    private static String TESTPROCEDURENUMBER = "procedureNumber";

    /**
     * The test step tag.
     */
    private static String STEP = "step";

    /**
     * The test step tag.
     */
    private static String STEPCOUNT = "stepCount";

    /**
     * The test step Number tag.
     */
    private static String STEPNUMBER = "testStepNumber";

    /**
     * The test step tag.
     */
    private static String TESTPROCEDURE = "testProcedure";

    /**
     * The test procedure results tag.
     */
    private static String TESTPROCEDURERESULTS = "TestProcedureResults";

    /**
     * The output database file.
     */
    private File outdb;

    /**
     * The test procedure configuration information.
     */
    private TestProcedureConfiguration testProcedureConfiguration;

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
     * The app layer test procedures dao.
     */
    private C2CRIAppLayerTestProceduresDAO appLayerTestProceduresDAO;

    /**
     * The info layer test procedures dao.
     */
    private C2CRIInfoLayerTestProceduresDAO infoLayerTestProceduresDAO;

    /**
     * The app layer test procedure Steps dao.
     */
    private C2CRIAppLayerTestProcedureStepsDAO appLayerTestProcedureStepsDAO;

    /**
     * The info layer test procedure Steps dao.
     */
    private C2CRIInfoLayerTestProcedureStepsDAO infoLayerTestProcedureStepsDAO;

     /**
     * Instantiates a new Test Procedure processor.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param testProcedureConfiguration the test case configuration information
     * @param databaseName the database name
     */
    public TestProcedureProcessor(TestProcedureConfiguration testProcedureConfiguration, String databaseName) {
        try {
            File sourcedb = new File(databaseName);
            outdb = new File("./temp.db3");

            // Make a temporary copy of C2C RI SQLite database
			try (FileChannel sourceCh = new FileInputStream(sourcedb).getChannel();
				 FileChannel destCh = new FileOutputStream(outdb).getChannel())
			{
				sourceCh.transferTo(0, sourceCh.size(), destCh);
			}

            // Process the Application Layer Test Procedures
            this.testProcedureConfiguration = testProcedureConfiguration;
            if ((this.testProcedureConfiguration.getBaseApplicationTestProcedureURL() != null) && !exists(this.testProcedureConfiguration.getBaseApplicationTestProcedureURL().toString())) {
                throw new Exception("Base Application Layer test Procedure file " + this.testProcedureConfiguration.getBaseApplicationTestProcedureURL().toString() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getExtensionApplicationTestProcedureURL() != null) && !exists(this.testProcedureConfiguration.getExtensionApplicationTestProcedureURL().toString())) {
                throw new Exception("Extension Application Layer test Procedure file " + this.testProcedureConfiguration.getExtensionApplicationTestProcedureURL() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getBaseApplicationTestProcedureStepsURL() != null) && !exists(this.testProcedureConfiguration.getBaseApplicationTestProcedureStepsURL().toString())) {
                throw new Exception("Base Application Layer test Procedure Steps file " + this.testProcedureConfiguration.getBaseApplicationTestProcedureStepsURL().toString() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getExtensionApplicationTestProcedureStepsURL() != null) && !exists(this.testProcedureConfiguration.getExtensionApplicationTestProcedureStepsURL().toString())) {
                throw new Exception("Extension Application Layer test Procedure Steps file " + this.testProcedureConfiguration.getExtensionApplicationTestProcedureStepsURL() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getApplicableAppplicationTestProcedures() == null)) {
                throw new Exception("The Application Layer test Procedure List is null. ");
            }

            if ((this.testProcedureConfiguration.getBaseInformationTestProcedureURL() != null) && !exists(this.testProcedureConfiguration.getBaseInformationTestProcedureURL().toString())) {
                throw new Exception("Base Information Layer test Procedure file " + this.testProcedureConfiguration.getBaseInformationTestProcedureURL().toString() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getExtensionInformationTestProcedureURL() != null) &&  !exists(this.testProcedureConfiguration.getExtensionInformationTestProcedureURL().toString())) {
                throw new Exception("Extension Information Layer test Procedure file " + this.testProcedureConfiguration.getExtensionInformationTestProcedureURL() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getBaseInformationTestProcedureStepsURL() != null) && !exists(this.testProcedureConfiguration.getBaseInformationTestProcedureStepsURL().toString())) {
                throw new Exception("Base Information Layer test Procedure Steps file " + this.testProcedureConfiguration.getBaseInformationTestProcedureStepsURL().toString() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getExtensionInformationTestProcedureStepsURL() != null) &&  !exists(this.testProcedureConfiguration.getExtensionInformationTestProcedureStepsURL().toString())) {
                throw new Exception("Extension Information Layer test Procedure Steps file " + this.testProcedureConfiguration.getExtensionInformationTestProcedureStepsURL() + " does not exist.");
            }

            if ((this.testProcedureConfiguration.getApplicableInformationTestProcedures() == null)) {
                throw new Exception("The Information Layer test Procedure List is null. ");
            }
            appLayerTestProceduresDAO = new C2CRIAppLayerTestProceduresDAO(outdb);
            infoLayerTestProceduresDAO = new C2CRIInfoLayerTestProceduresDAO(outdb);
            appLayerTestProcedureStepsDAO = new C2CRIAppLayerTestProcedureStepsDAO(outdb);
            infoLayerTestProcedureStepsDAO = new C2CRIInfoLayerTestProcedureStepsDAO(outdb);
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
     * Determines the number of events contained within the test procedure file.
     * This method currently uses xQuery to quickly run through the log file and
     * get a count of event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param testCaseFileLocation the log file name
     * @return the long
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XPathExpressionException the x path expression exception
     */
    private Long queryTestProcedureDescriptions(InputStream testCaseFile) throws ParserConfigurationException, SAXException,
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
        expr = xpath.compile("count(/TestProcedureDescriptions/TestProcedureDescription)");
        // Run the query and get a nodeset
        Double result = (Double) expr.evaluate(doc, XPathConstants.NUMBER);

        return result.longValue();
    }

    /**
     * Determines the number of events contained within the test procedure Steps
     * file. This method currently uses xQuery to quickly run through the log
     * file and get a count of event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param testCaseFileLocation the log file name
     * @return the long
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XPathExpressionException the x path expression exception
     */
    private Long queryTestProcedureSteps(InputStream testProcedureStepsFile) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, XPathFactoryConfigurationException {
        // Standard of reading a XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        XPathExpression expr = null;
        builder = factory.newDocumentBuilder();
        doc = builder.parse(testProcedureStepsFile);

        // Create a XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance(
            XPathFactory.DEFAULT_OBJECT_MODEL_URI,
            "net.sf.saxon.xpath.XPathFactoryImpl",
            ClassLoader.getSystemClassLoader());        

        // Create a XPath object
        XPath xpath = xFactory.newXPath();

        // Compile the XPath expression
        expr = xpath.compile("count(/TestProcedureSteps/TestProcedure)");
        // Run the query and get a nodeset
        Double result = (Double) expr.evaluate(doc, XPathConstants.NUMBER);

        return result.longValue();
    }

    /**
     * Write out test procedure information to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public void writeTestProceduresToTempDB() throws SQLException, Exception {
        eventId = 0;
        if ((this.testProcedureConfiguration.getBaseApplicationTestProcedureURL() != null) && exists(this.testProcedureConfiguration.getBaseApplicationTestProcedureURL().toString())) {
            writeAppLayerTestProceduresToTempDB(this.testProcedureConfiguration.getBaseApplicationTestProcedureURL());
        }

        if ((this.testProcedureConfiguration.getExtensionApplicationTestProcedureURL() != null) && exists(this.testProcedureConfiguration.getExtensionApplicationTestProcedureURL().toString())) {
            writeAppLayerTestProceduresToTempDB(this.testProcedureConfiguration.getExtensionApplicationTestProcedureURL());
        }

        eventId = 0;
        if ((this.testProcedureConfiguration.getBaseInformationTestProcedureURL() != null) && exists(this.testProcedureConfiguration.getBaseInformationTestProcedureURL().toString())) {
            writeInfoLayerTestProceduresToTempDB(this.testProcedureConfiguration.getBaseInformationTestProcedureURL());
        }

        if ((this.testProcedureConfiguration.getExtensionInformationTestProcedureURL() != null) && exists(this.testProcedureConfiguration.getExtensionInformationTestProcedureURL().toString())) {
            writeInfoLayerTestProceduresToTempDB(this.testProcedureConfiguration.getExtensionInformationTestProcedureURL());
        }

        eventId = 0;
        if ((this.testProcedureConfiguration.getBaseApplicationTestProcedureStepsURL() != null) && exists(this.testProcedureConfiguration.getBaseApplicationTestProcedureStepsURL().toString())) {
            writeAppLayerTestProcedureStepsToTempDB(this.testProcedureConfiguration.getBaseApplicationTestProcedureStepsURL());
        }

        if ((this.testProcedureConfiguration.getExtensionApplicationTestProcedureStepsURL() != null) && exists(this.testProcedureConfiguration.getExtensionApplicationTestProcedureStepsURL().toString())) {
             writeAppLayerTestProcedureStepsToTempDB(this.testProcedureConfiguration.getExtensionApplicationTestProcedureStepsURL());
        }

        eventId = 0;
        if ((this.testProcedureConfiguration.getBaseInformationTestProcedureStepsURL() != null) && exists(this.testProcedureConfiguration.getBaseInformationTestProcedureStepsURL().toString())) {
            writeInfoLayerTestProcedureStepsToTempDB(this.testProcedureConfiguration.getBaseInformationTestProcedureStepsURL());
        }

        if ((this.testProcedureConfiguration.getExtensionInformationTestProcedureURL() != null) && exists(this.testProcedureConfiguration.getExtensionInformationTestProcedureStepsURL().toString())) {
            writeInfoLayerTestProcedureStepsToTempDB(this.testProcedureConfiguration.getExtensionInformationTestProcedureStepsURL());
        }

    }

    /**
     * Write out application layer test procedure data to temporary SQLite database.
     *
     * @param fileURL url of the test procedure xml file
     * @return the number of records processed
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public int writeAppLayerTestProceduresToTempDB(URL fileURL) throws SQLException, Exception {
        LinkedHashMap<String,C2CRIAppLayerTestProcedure> testProcedureMap = new LinkedHashMap();
        
        try {

            // note that we create the dialog without an owning frame; normally you
            // would associate it with some other frame in your application
            ProgressMonitor monitor = ProgressMonitor.getInstance(
                    null,
                    "C2C RI",
                    "Processing Application Layer Test Procedures.",
                    ProgressMonitor.Options.CENTER,
                    ProgressMonitor.Options.SHOW_STATUS,
                    ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

            // because we didn't set an initial progress value, the dialog will
            // appear in indeterminate ("Cylon") mode; let it do that for a while            
            Long result = queryTestProcedureDescriptions(fileURL.openStream());

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(fileURL.openStream());
            // Read the XML document

            while (eventReader.hasNext()) {
                C2CRIAppLayerTestProcedure eventSet = new C2CRIAppLayerTestProcedure();
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (TESTPROCEDUREDESCRIPTION.equals(startElement.getName().getLocalPart())) {
                        eventSet = processAppEvent();
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (TESTPROCEDUREDESCRIPTION.equals(endElement.getName().getLocalPart())) {
                        if (!testProcedureMap.containsKey(eventSet.getProcedure().trim())){
                            testProcedureMap.put(eventSet.getProcedure().trim(), eventSet);
                        }

                    }
                }

            }

            ArrayList<String> applicableTestProcedures = new ArrayList();
            for (TestCaseInformation tci : testProcedureConfiguration.getApplicableAppplicationTestProcedures()) {
                eventId++;
                if (!applicableTestProcedures.contains(tci.getRelatedTestProcedure())){
                    applicableTestProcedures.add(tci.getRelatedTestProcedure());
                    if (testProcedureMap.containsKey(tci.getRelatedTestProcedure())){
                        C2CRIAppLayerTestProcedure thisProcedure = testProcedureMap.get(tci.getRelatedTestProcedure());
                        thisProcedure.setId(eventId);
                        appLayerTestProceduresDAO.insert(thisProcedure);
                    } else {
                        C2CRIAppLayerTestProcedure missingProcedure = new C2CRIAppLayerTestProcedure();
                        missingProcedure.setProcedure(tci.getRelatedTestProcedure() + "(Not Specified in Test Suite)");
                        missingProcedure.setProcedureId(tci.getRelatedTestProcedure() + "(Not Specified in Test Suite)");
                        missingProcedure.setId(eventId);
                        missingProcedure.setDescription("Not Available");
                        missingProcedure.setRequirements("Not Available");
                        missingProcedure.setVariables("Not Available");
                        missingProcedure.setPassFailCriteria("Not Available");
                        appLayerTestProceduresDAO.insert(missingProcedure);                    
                    }
                } 
                monitor.setProgress(0, eventId, result.intValue());
                monitor.setStatus("Checking Test Case " + (eventId - 1) + " of " + result.intValue());               
            }

            appLayerTestProceduresDAO.flush();
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
     * Write out information layer test procedure data to temporary SQLite database.
     *
     * @param fileURL the url for the test procedure xml file
     * @return the number of records written to database
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public int writeInfoLayerTestProceduresToTempDB(URL fileURL) throws SQLException, Exception {
        LinkedHashMap<String,C2CRIInfoLayerTestProcedure> testProcedureMap = new LinkedHashMap();
            
        try {

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
            Long result = queryTestProcedureDescriptions(fileURL.openStream());

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(fileURL.openStream());
            // Read the XML document

            while (eventReader.hasNext()) {
                C2CRIInfoLayerTestProcedure eventSet = new C2CRIInfoLayerTestProcedure();
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (TESTPROCEDUREDESCRIPTION.equals(startElement.getName().getLocalPart())) {
                        eventSet = processInfoEvent();
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (TESTPROCEDUREDESCRIPTION.equals(endElement.getName().getLocalPart())) {
                        if (!testProcedureMap.containsKey(eventSet.getProcedure().trim())){
                            testProcedureMap.put(eventSet.getProcedure().trim(), eventSet);
                        }

                    }
                }

            }

            ArrayList<String> applicableTestProcedures = new ArrayList();
            for (TestCaseInformation tci : testProcedureConfiguration.getApplicableInformationTestProcedures()) {
                eventId++;
                if (!applicableTestProcedures.contains(tci.getRelatedTestProcedure())){
                    applicableTestProcedures.add(tci.getRelatedTestProcedure());
                    if (testProcedureMap.containsKey(tci.getRelatedTestProcedure())){
                        C2CRIInfoLayerTestProcedure thisProcedure = testProcedureMap.get(tci.getRelatedTestProcedure());
                        thisProcedure.setId(eventId);
                        infoLayerTestProceduresDAO.insert(thisProcedure);
                    } else {
                        C2CRIInfoLayerTestProcedure missingProcedure = new C2CRIInfoLayerTestProcedure();
                        missingProcedure.setProcedure(tci.getRelatedTestProcedure() + "(Not Specified in Test Suite)");
                        missingProcedure.setProcedureId(tci.getRelatedTestProcedure() + "(Not Specified in Test Suite)");
                        missingProcedure.setId(eventId);
                        missingProcedure.setDescription("Not Available");
                        missingProcedure.setRequirements("Not Available");
                        missingProcedure.setVariables("Not Available");
                        missingProcedure.setPassFailCriteria("Not Available");
                        infoLayerTestProceduresDAO.insert(missingProcedure);                    
                    }
                } 
                monitor.setProgress(0, eventId, result.intValue());
                monitor.setStatus("Checking Test Case " + (eventId - 1) + " of " + result.intValue());               
            }
                        
            infoLayerTestProceduresDAO.flush();
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
     * Process Application Test Procedure description event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the event set
     * @throws SQLException the sQL exception
     */
    private C2CRIAppLayerTestProcedure processAppEvent() throws SQLException {
        C2CRIAppLayerTestProcedure eventSet = new C2CRIAppLayerTestProcedure();
        eventSet.setId(eventId);

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTPROCEDUREDESCRIPTION)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {

                            String tempString = event.asCharacters().getData();
                            int lengthBefore = tempString.length();
                            eventSet.setProcedure(tempString.replaceAll("[\n\r]", "").trim());
                            System.out.println("The last character is = "+Integer.toHexString((int)eventSet.getProcedure().charAt(eventSet.getProcedure().length()-1)));
                            System.out.println("The before length was "+lengthBefore + " and the current length is "+eventSet.getProcedure().length());
                            eventSet.setPassFailCriteria(eventSet.getProcedure());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDUREID)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setProcedureId(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(INTERNALTESTPROCEDUREDESCRIPTION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setDescription(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(REQUIREMENTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setRequirements(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(VARIABLES)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setVariables(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(PASSFAILCRITERIA)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setPassFailCriteria(fullText.toString());    
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
     * Process Information Layer Test Procedure description event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the event set
     * @throws SQLException the sQL exception
     */
    private C2CRIInfoLayerTestProcedure processInfoEvent() throws SQLException {
        C2CRIInfoLayerTestProcedure eventSet = new C2CRIInfoLayerTestProcedure();
        eventSet.setId(eventId);

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTPROCEDUREDESCRIPTION)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {

                            String tempString = event.asCharacters().getData();
                            int lengthBefore = tempString.length();
                            eventSet.setProcedure(tempString.replaceAll("[\n\r]", "").trim());
                            System.out.println("The last character is = "+Integer.toHexString((int)eventSet.getProcedure().charAt(eventSet.getProcedure().length()-1)));
                            System.out.println("The before length was "+lengthBefore + " and the current length is "+eventSet.getProcedure().length());
                            eventSet.setPassFailCriteria(eventSet.getProcedure());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDUREID)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setProcedureId(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(INTERNALTESTPROCEDUREDESCRIPTION)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setDescription(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(REQUIREMENTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setRequirements(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(VARIABLES)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setVariables(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(PASSFAILCRITERIA)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            eventSet.setPassFailCriteria(fullText.toString());    
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
     * Write out application layer test procedure steps to temporary SQLite
     * database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public int writeAppLayerTestProcedureStepsToTempDB(URL fileURL) throws SQLException, Exception {
        try {

            // note that we create the dialog without an owning frame; normally you
            // would associate it with some other frame in your application
            ProgressMonitor monitor = ProgressMonitor.getInstance(
                    null,
                    "C2C RI",
                    "Processing Application Layer Test Procedure Steps.",
                    ProgressMonitor.Options.CENTER,
                    ProgressMonitor.Options.SHOW_STATUS,
                    ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

            // because we didn't set an initial progress value, the dialog will
            // appear in indeterminate ("Cylon") mode; let it do that for a while            
            Long result = queryTestProcedureSteps(fileURL.openStream());

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(fileURL.openStream());
            // Read the XML document

            while (eventReader.hasNext()) {
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (TESTPROCEDURESTEPS.equals(startElement.getName().getLocalPart())) {
                        processAppStepsEvent();
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (TESTPROCEDURESTEPS.equals(endElement.getName().getLocalPart())) {
                        monitor.setProgress(0, eventId, result.intValue());
                        monitor.setStatus("Checking Test Procedure Steps" + (eventId - 1) + " of " + result.intValue());
                        //                      items.add(item);
                    }
                }

            }

            appLayerTestProcedureStepsDAO.flush();
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
     * Process Application layer Test Procedure Steps event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the event set
     * @throws SQLException the sQL exception
     */
    private void processAppStepsEvent() throws SQLException {

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals((TESTPROCEDURESTEPS))))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURESTEP)) {

                        processAppProcedureEvent();

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
        appLayerTestProcedureStepsDAO.flush();
    }

    /**
     * Process application layer procedure event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the user event
     */
    private void processAppProcedureEvent() {
        LinkedHashMap<String,ArrayList<C2CRIAppLayerTestProcedureStep>> testProcedureStepMap = new LinkedHashMap();

        String procedureName = "";
        ArrayList<String> applicableAppTestProcedures = new ArrayList();
        for (TestCaseInformation tci : testProcedureConfiguration.getApplicableAppplicationTestProcedures()) {
            if (!applicableAppTestProcedures.contains(tci.getRelatedTestProcedure())){
                applicableAppTestProcedures.add(tci.getRelatedTestProcedure());
            }
        }

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTPROCEDURESTEP)))) {

            try {

                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            procedureName = event.asCharacters().getData().trim();
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(STEP)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                                C2CRIAppLayerTestProcedureStep thisEvent = processAppStepEvent();
                                thisEvent.setProcedure(procedureName);
                                thisEvent.setKeyid(eventId);
                                if (testProcedureStepMap.containsKey(procedureName)){
                                    testProcedureStepMap.get(procedureName).add(thisEvent);                                    
                                } else {
                                    ArrayList<C2CRIAppLayerTestProcedureStep> stepList = new ArrayList();
                                    stepList.add(thisEvent);
                                    testProcedureStepMap.put(procedureName, stepList);
                                }
//                                appLayerTestProcedureStepsDAO.insert(thisEvent);
                            eventId++;
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
            }
        }
                
        for (String testProcedureName : applicableAppTestProcedures){
            if (testProcedureStepMap.containsKey(testProcedureName)){
                appLayerTestProcedureStepsDAO.insert(testProcedureStepMap.get(testProcedureName));
            }
        }

    }

    /**
     * Process application layer test step event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the user event
     */
    private C2CRIAppLayerTestProcedureStep processAppStepEvent() {
        C2CRIAppLayerTestProcedureStep thisEvent = new C2CRIAppLayerTestProcedureStep();

        thisEvent.setKeyid(eventId);

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(STEP)))) {

            try {

                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(STEPCOUNT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setId(Integer.parseInt(event.asCharacters().getData()));
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(STEPNUMBER)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setStep(event.asCharacters().getData());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            thisEvent.setAction(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURERESULTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            thisEvent.setPassFail(fullText.toString());    
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
            }
        }
        return thisEvent;
    }

    /**
     * Write out information layer test procedure steps to temporary SQLite
     * database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    public int writeInfoLayerTestProcedureStepsToTempDB(URL fileURL) throws SQLException, Exception {
        try {

            ArrayList<String> applicableTestProcedures = new ArrayList();
            for (TestCaseInformation tci : testProcedureConfiguration.getApplicableInformationTestProcedures()) {
                applicableTestProcedures.add(tci.getRelatedTestProcedure());
            }

            // note that we create the dialog without an owning frame; normally you
            // would associate it with some other frame in your application
            ProgressMonitor monitor = ProgressMonitor.getInstance(
                    null,
                    "C2C RI",
                    "Processing Information Layer Test Procedure Steps.",
                    ProgressMonitor.Options.CENTER,
                    ProgressMonitor.Options.SHOW_STATUS,
                    ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

            // because we didn't set an initial progress value, the dialog will
            // appear in indeterminate ("Cylon") mode; let it do that for a while            
            Long result = queryTestProcedureSteps(fileURL.openStream());

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(fileURL.openStream());
            // Read the XML document

            while (eventReader.hasNext()) {
                event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (TESTPROCEDURESTEPS.equals(startElement.getName().getLocalPart())) {
                        processInfoStepsEvent();
                    }

                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (TESTPROCEDURESTEPS.equals(endElement.getName().getLocalPart())) {
                        monitor.setProgress(0, eventId, result.intValue());
                        monitor.setStatus("Checking Test Procedure Steps" + (eventId - 1) + " of " + result.intValue());
                        //                      items.add(item);
                    }
                }

            }

            infoLayerTestProcedureStepsDAO.flush();
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
     * Process Information Test Procedure Steps event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisEvent the this event
     * @return the event set
     * @throws SQLException the sQL exception
     */
    private void processInfoStepsEvent() throws SQLException {

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTPROCEDURESTEPS)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURESTEP)) {

                        processInfoProcedureEvent();

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
        infoLayerTestProcedureStepsDAO.flush();
    }

    /**
     * Process information layer test procedure step event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the user event
     */
    private void processInfoProcedureEvent() {
        LinkedHashMap<String,ArrayList<C2CRIInfoLayerTestProcedureStep>> testProcedureStepMap = new LinkedHashMap();
        
        String procedureName = "";
        ArrayList<String> applicableInfoTestProcedures = new ArrayList();
        for (TestCaseInformation tci : testProcedureConfiguration.getApplicableInformationTestProcedures()) {
            if (!applicableInfoTestProcedures.contains(tci.getRelatedTestProcedure())){
                applicableInfoTestProcedures.add(tci.getRelatedTestProcedure());                
            } 
        }

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(TESTPROCEDURESTEP)))) {

            try {

                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURENAME)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            procedureName = event.asCharacters().getData().trim();
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(STEP)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                                C2CRIInfoLayerTestProcedureStep thisEvent = processInfoStepEvent();
                                thisEvent.setProcedure(procedureName);
                                thisEvent.setKeyid(eventId);
                                if (testProcedureStepMap.containsKey(procedureName)){
                                    testProcedureStepMap.get(procedureName).add(thisEvent);                                    
                                } else {
                                    ArrayList<C2CRIInfoLayerTestProcedureStep> stepList = new ArrayList();
                                    stepList.add(thisEvent);
                                    testProcedureStepMap.put(procedureName, stepList);
                                }
                                if(procedureName.contains("-OC")) System.out.println("TestProcedureProcessor:  Found  &"+procedureName+"& in the list.");
                            eventId++;
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
            }
        }
        
        for (String testProcedureName : applicableInfoTestProcedures){
            if (testProcedureStepMap.containsKey(testProcedureName)){
                infoLayerTestProcedureStepsDAO.insert(testProcedureStepMap.get(testProcedureName));
            }
        }
    }

    /**
     * Process information layer step event tags.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the user event
     */
    private C2CRIInfoLayerTestProcedureStep processInfoStepEvent() {
        C2CRIInfoLayerTestProcedureStep thisEvent = new C2CRIInfoLayerTestProcedureStep();

        thisEvent.setKeyid(eventId);

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(STEP)))) {
            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(STEPCOUNT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setId(Integer.parseInt(event.asCharacters().getData()));
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(STEPNUMBER)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            thisEvent.setStep(event.asCharacters().getData());
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURE)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            thisEvent.setAction(fullText.toString());    
                        }
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(TESTPROCEDURERESULTS)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            StringBuilder fullText = new StringBuilder();
                            fullText.append(event.asCharacters().getData());
                            while (!event.isEndElement()){
                                event = eventReader.nextEvent();
                                if (event.isCharacters()){
                                    fullText.append(event.asCharacters().getData());                                    
                                }
                            }
                            thisEvent.setPassFail(fullText.toString());    
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
            }
        }
        return thisEvent;
    }

}
