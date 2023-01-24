/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.fhwa.c2cri.utilities.CSVFileParser;

/**
 * The Class TestCases contains a collection of the test cases defined within a configuration file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCases implements Serializable {

    static final long  serialVersionUID = 1182468301496558568L;
    
    /** The Constant name_Header. */
    public final static String name_Header = "Name";
    
    /** The Constant scriptFile_Header. */
    public final static String scriptFile_Header = "ScriptFile";
    
    /** The Constant dataFile_Header. */
    public final static String dataFile_Header = "DataFile";
    
    /** The Constant description_Header. */
    public final static String description_Header = "Description";
    
    /** The Constant type_Header. */
    public final static String type_Header = "Type";
    
    /** The name_ index. */
    private int name_Index;
    
    /** The script file_ index. */
    private int scriptFile_Index;
    
    /** The data file_ index. */
    private int dataFile_Index;
    
    /** The description_ index. */
    private int description_Index;
    
    /** The type_ index. */
    private int type_Index;
    
    /** The header list. */
    @XmlTransient
    public ArrayList<String> headerList;
    
    /** The test cases. */
    @XmlElement(name = "testCase")
    public List<TestCase> testCases = new ArrayList<TestCase>();
    
    /** The test cases map. */
    @XmlTransient
    public HashMap testCasesMap;

    /** The test cases map. */
    @XmlTransient
    public LinkedHashMap lh_testCasesMap;
    
    /** The extended test suite. */
    private boolean extendedTestSuite;
    
    /** The baseline test suite. */
    private String baselineTestSuite = "";
    
    /** The custom test suite. */
    private String customTestSuite = "";

    /**
     * Instantiates a new test cases.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCases() {
        // For JAXB creation of XML
    }

    /**
     * Instantiates a new test cases.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param selectedTestSuite the selected test suite
     * @throws Exception the exception
     */
    public TestCases(String selectedTestSuite) throws Exception {
        lh_testCasesMap = new LinkedHashMap();
        extendedTestSuite = false;
        baselineTestSuite = selectedTestSuite;
        testCases = loadTestCases(TestSuites.getInstance().getTestSuiteTestCasesURL(selectedTestSuite), false);

        // Make the testCases locatable via the HashMap
        for (TestCase thisTestCase : testCases) {
            lh_testCasesMap.put(thisTestCase.getName(), thisTestCase);
        }

    }

    /**
     * Instantiates a new test cases.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuite the predefined test suite
     * @param customTestSuite the custom test suite
     * @throws Exception the exception
     */
    public TestCases(String predefinedTestSuite, String customTestSuite) throws Exception {
        lh_testCasesMap = new LinkedHashMap();
        extendedTestSuite = true;
        this.baselineTestSuite = predefinedTestSuite;
        this.customTestSuite = customTestSuite;
        List<TestCase> predefinedTestCases = new ArrayList<TestCase>();
        List<TestCase> customTestCases = new ArrayList<TestCase>();

        predefinedTestCases = loadTestCases(TestSuites.getInstance().getTestSuiteTestCasesURL(predefinedTestSuite), false);
        customTestCases = loadTestCases(TestSuites.getInstance().getTestSuiteTestCasesURL(customTestSuite), true);

        List<TestCase> subTestCases = new ArrayList<TestCase>();

        for (TestCase thisTestCase : customTestCases) {
            String testCaseName = thisTestCase.getName();
            boolean okToAdd = true;
            for (TestCase existingTestCase : predefinedTestCases) {
                if (testCaseName.equals(existingTestCase.getName())) {
                    System.err.println("Duplicate testcase included =>" + testCaseName);

                    okToAdd = false;
                    break;
                }
            }
            if (okToAdd) {
                subTestCases.add(thisTestCase);
            }

        }
        // The testCases from a predefined test suite are always included.
        testCases = predefinedTestCases;
        for (TestCase subTestCase : subTestCases) {
            testCases.add(subTestCase);
        }

        // Make the testCases locatable via the HashMap
        for (TestCase thisTestCase : testCases) {
            lh_testCasesMap.put(thisTestCase.getName(), thisTestCase);
        }

    }

    /**
     * Instantiates a new test cases.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param selectedTestSuite the selected test suite
     * @throws Exception the exception
     */
    public TestCases(URL selectedTestSuite) throws Exception {
        lh_testCasesMap = new LinkedHashMap();
        extendedTestSuite = false;
//        baselineTestSuite = selectedTestSuite;
        testCases = loadTestCases(selectedTestSuite, false);

        // Make the testCases locatable via the HashMap
        for (TestCase thisTestCase : testCases) {
            lh_testCasesMap.put(thisTestCase.getName(), thisTestCase);
        }

    }

    /**
     * Instantiates a new test cases.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuite the predefined test suite
     * @param customTestSuite the custom test suite
     * @throws Exception the exception
     */
    public TestCases(URL predefinedTestSuite, URL customTestSuite) throws Exception {
        lh_testCasesMap = new LinkedHashMap();
        extendedTestSuite = true;
//        this.baselineTestSuite = predefinedTestSuite;
//        this.customTestSuite = customTestSuite;
        List<TestCase> predefinedTestCases = new ArrayList<TestCase>();
        List<TestCase> customTestCases = new ArrayList<TestCase>();

        predefinedTestCases = loadTestCases(predefinedTestSuite, false);
        customTestCases = loadTestCases(customTestSuite, true);

        List<TestCase> subTestCases = new ArrayList<TestCase>();

        for (TestCase thisTestCase : customTestCases) {
            String testCaseName = thisTestCase.getName();
            boolean okToAdd = true;
            for (TestCase existingTestCase : predefinedTestCases) {
                if (testCaseName.equals(existingTestCase.getName())) {
                    System.err.println("Duplicate testcase included =>" + testCaseName);
                    okToAdd = false;
                    break;
                }
            }
            if (okToAdd) {
                subTestCases.add(thisTestCase);
            }

        }
        // The testCases from a predefined test suite are always included.
        testCases = predefinedTestCases;
        for (TestCase subTestCase : subTestCases) {
            testCases.add(subTestCase);
        }

        // Make the testCases locatable via the HashMap
        for (TestCase thisTestCase : testCases) {
            lh_testCasesMap.put(thisTestCase.getName(), thisTestCase);
        }

    }

    /**
     * Load test cases.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteTestCasesPath the test suite test cases path
     * @param customSuite the custom suite
     * @return the list
     * @throws Exception the exception
     */
    private List<TestCase> loadTestCases(URL testSuiteTestCasesPath, boolean customSuite) throws Exception {
        List<TestCase> testSuiteTestCases = new ArrayList<TestCase>();

        CSVFileParser csvParser = new CSVFileParser();
        csvParser.parse(testSuiteTestCasesPath);
        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        name_Index = headerList.indexOf(TestCasesInterface.name_Header);
        scriptFile_Index = headerList.indexOf(TestCasesInterface.scriptFile_Header);
        dataFile_Index = headerList.indexOf(TestCasesInterface.dataFile_Header);
        description_Index = headerList.indexOf(TestCasesInterface.description_Header);
        type_Index = headerList.indexOf(TestCasesInterface.type_Header);


        ArrayList<ArrayList<String>> testCasesData = csvParser.getCsvData();

        int testCase_index = 1;
        for (ArrayList<String> dataList : testCasesData) {

            String name = "";
            String scriptFile = "";
            String dataFile = "";
            String description = "";
            String type = "";

            if (name_Index == -1){
                throw new Exception(" Missing Required "+TestCasesInterface.name_Header+" Index in CSV File ");
                
            } else if (scriptFile_Index == -1){
                throw new Exception(" Missing Required "+TestCasesInterface.scriptFile_Header+" Index in CSV File ");
                
            } else if (dataFile_Index == -1) {
                throw new Exception(" Missing Required "+TestCasesInterface.dataFile_Header+" Index in CSV File ");
                
            } else if (description_Index == -1){
                throw new Exception(" Missing Required "+TestCasesInterface.description_Header+" Index in CSV File ");
                
            } else if (type_Index == -1) {
                throw new Exception(" Missing Required "+TestCasesInterface.type_Header+" Index in CSV File ");
            } else {
                try {
                    if (name_Index > -1) {
                        name = dataList.get(name_Index);
                    }
                    if (scriptFile_Index > -1) {
                        scriptFile = dataList.get(scriptFile_Index);
                    }
                    if (dataFile_Index > -1) {
                        dataFile = dataList.get(dataFile_Index);
                    }
                    if (description_Index > -1) {
                        description = dataList.get(description_Index);
                    }
                    if (type_Index > -1) {
                        type = dataList.get(type_Index);
                    }
                } catch (Exception ex) {
 //                   ex.printStackTrace();
                    System.err.println("Missing CSV Component in record: \n"
                            + "Name - " + name
                            + "\n scriptFile " + scriptFile
                            + "\n dataFile " + dataFile
                            + "\n description " + description
                            + "\n type " + type);
                   throw new Exception("Missing CSV Component in record: \n"
                            + "Name - " + name
                            + "\n scriptFile " + scriptFile
                            + "\n dataFile " + dataFile
                            + "\n description " + description
                            + "\n type " + type,ex);
                }
                try {
                    TestCase testCase;
                    if (customSuite) {
                        testCase = new TestCase(name, scriptFile, dataFile, description, type, customTestSuite);
                    } else {
                        testCase = new TestCase(name, scriptFile, dataFile, description, type, baselineTestSuite);
                    }
                    testCase_index++;
                    System.out.println(" Adding testcase " + testCase.getName() + " #" + testCase_index + " with Description " + description);
                    testSuiteTestCases.add(testCase);
                } catch (Exception ex) {
                    System.out.println("Error with : " + name + " " + scriptFile + " " + dataFile + " " + description + " " + type);
                    ex.printStackTrace();
                    //Likely a URL error, skip this record.
                }
//                needsMap.put(projecttestcase.getTitle(), projecttestcase);
            }

        }
        return testSuiteTestCases;
    }

    /**
     * Gets the name_ index.
     *
     * @return the name_ index
     */
    public int getName_Index() {
        return name_Index;
    }

    /**
     * Gets the text_ index.
     *
     * @return the text_ index
     */
    public int getText_Index() {
        return description_Index;
    }

    /**
     * Gets the title_ index.
     *
     * @return the title_ index
     */
    public int getTitle_Index() {
        return dataFile_Index;
    }

    /**
     * Gets the type_ index.
     *
     * @return the type_ index
     */
    public int getType_Index() {
        return type_Index;
    }
    

   // using readObject method to set default values
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        // deserialize the non-transient data members first;
        input.defaultReadObject();
        if (lh_testCasesMap == null){
            lh_testCasesMap = new LinkedHashMap(testCasesMap);
            testCasesMap.clear();
            testCasesMap = null;
        }
    }
    
}
