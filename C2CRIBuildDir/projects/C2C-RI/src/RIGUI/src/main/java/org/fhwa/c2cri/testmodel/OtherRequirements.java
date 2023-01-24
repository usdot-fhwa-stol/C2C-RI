/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.fhwa.c2cri.utilities.CSVFileParser;

/**
 * The Class OtherRequirements contains the list of "other requirements" defined within an NRTM.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OtherRequirements implements Serializable {

    /** The req i d_ index. */
    private int reqID_Index;
    
    /** The other requirement_ index. */
    private int otherRequirement_Index;
    
    /** The value_ index. */
    private int value_Index;
    
    /** The value name_ index. */
    private int valueName_Index;
    
    /** The type_ index. */
    private int type_Index;
    
    /** The minimum_ index. */
    private int minimum_Index;
    
    /** The maximum_ index. */
    private int maximum_Index;
    
    /** The header list. */
    @XmlTransient
    public ArrayList<String> headerList;
    
    /** The other requirements. */
    @XmlElement(name="otherRequirement")
    public List<OtherRequirement> otherRequirements = new ArrayList<OtherRequirement>();
//    private HashMap needsMap;

    /**
 * Instantiates a new other requirements.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 */
public OtherRequirements(){
 //       otherRequirements = new ArrayList<OtherRequirement>();// For JAXB creation of XML
    }
    
    /**
     * Instantiates a new other requirements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteOtherRequirementsPath the test suite other requirements path
     * @throws Exception the exception
     */
    public OtherRequirements(URL testSuiteOtherRequirementsPath) throws Exception {
//        needsMap = new HashMap();
        otherRequirements = loadOtherRequirements(testSuiteOtherRequirementsPath);
    }

    /**
     * Instantiates a new other requirements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuiteOtherRequirementsPath the predefined test suite other requirements path
     * @param customTestSuiteOtherRequirementsPath the custom test suite other requirements path
     * @throws Exception the exception
     */
    public OtherRequirements(URL predefinedTestSuiteOtherRequirementsPath, URL customTestSuiteOtherRequirementsPath) throws Exception {
        List<OtherRequirement> predefinedOtherRequirements = new ArrayList<OtherRequirement>();
        List<OtherRequirement> customOtherRequirements = new ArrayList<OtherRequirement>();

        predefinedOtherRequirements = loadOtherRequirements(predefinedTestSuiteOtherRequirementsPath);
        customOtherRequirements = loadOtherRequirements(customTestSuiteOtherRequirementsPath);

        List<OtherRequirement> subOtherRequirements = new ArrayList<OtherRequirement>();

        for (OtherRequirement thisOtherRequirement : customOtherRequirements) {
            String otherRequirementReqID = thisOtherRequirement.getReqID();
            boolean okToAdd = true;
            for (OtherRequirement existingOtherRequirement : predefinedOtherRequirements) {
                if (otherRequirementReqID.equals(existingOtherRequirement.getReqID())) {
                    System.err.println("Duplicate OtherRequirement included =>" + otherRequirementReqID);
                    okToAdd = false;
                    break;
                }
            }
            if (okToAdd) {
                subOtherRequirements.add(thisOtherRequirement);
            }

        }
        // The OtherRequirements from a predefined test suite are always included.
        otherRequirements = predefinedOtherRequirements;
        for (OtherRequirement subOtherRequirement : subOtherRequirements) {
            otherRequirements.add(subOtherRequirement);
        }

    }

    /**
     * Load other requirements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteOtherRequirementsPath the test suite other requirements path
     * @return the list
     * @throws Exception the exception
     */
    private List<OtherRequirement> loadOtherRequirements(URL testSuiteOtherRequirementsPath) throws Exception {
        List<OtherRequirement> testSuiteOtherRequirements = new ArrayList<OtherRequirement>();

        CSVFileParser csvParser = new CSVFileParser();
        csvParser.parse(testSuiteOtherRequirementsPath);
        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        reqID_Index = headerList.indexOf(OtherRequirementsInterface.ReqID_Header);
        otherRequirement_Index = headerList.indexOf(OtherRequirementsInterface.OtherRequirement_Header);
        value_Index = headerList.indexOf(OtherRequirementsInterface.Value_Header);
        valueName_Index = headerList.indexOf(OtherRequirementsInterface.ValueName_Header);
        type_Index = headerList.indexOf(OtherRequirementsInterface.Type_Header);
        minimum_Index = headerList.indexOf(OtherRequirementsInterface.Minimum_Header);
        maximum_Index = headerList.indexOf(OtherRequirementsInterface.Maximum_Header);


        ArrayList<ArrayList<String>> otherRequirementsData = csvParser.getCsvData();

        int need_index = 1;
        for (ArrayList<String> dataList : otherRequirementsData) {

            String reqID = "";
            String otherRequirementID = "";
            String value = "";
            String valueName = "";
            String type = "";
            int minimum = 0;
            int maximum = 0;

            if ((reqID_Index == -1)
                    || (otherRequirement_Index == -1)
                    || (value_Index == -1)
                    || (valueName_Index == -1)
                    || (type_Index == -1)
                    || (minimum_Index == -1)
                    || (maximum_Index == -1)) {
                throw new Exception(" Missing Required Index in CSV File ");
            } else {
                if (reqID_Index > -1) {
                    reqID = dataList.get(reqID_Index);
                }
                if (otherRequirement_Index > -1) {
                    otherRequirementID = dataList.get(otherRequirement_Index);
                }
                if (value_Index > -1) {
                    value = dataList.get(value_Index);
                }
                if (valueName_Index > -1) {
                    valueName = dataList.get(valueName_Index);
                }
                if (type_Index > -1) {
                    type = dataList.get(type_Index);
                }
                if (minimum_Index > -1) {
                    minimum = Integer.parseInt(dataList.get(minimum_Index));
                }
                if (maximum_Index > -1) {
                    maximum = Integer.parseInt(dataList.get(maximum_Index));
                }

                OtherRequirement otherRequirement = new OtherRequirement(reqID, otherRequirementID, value, valueName, minimum, maximum, type);
                need_index++;
                System.out.println(" Adding Other Requirement " + otherRequirement.getValue() + " #" + need_index);
                testSuiteOtherRequirements.add(otherRequirement);
//                needsMap.put(projectRequirement.getvalue(), projectRequirement);
            }

        }
        return testSuiteOtherRequirements;
    }

    /**
     * Gets the maximum_ index.
     *
     * @return the maximum_ index
     */
    public int getmaximum_Index() {
        return maximum_Index;
    }

    /**
     * Gets the value name_ index.
     *
     * @return the value name_ index
     */
    public int getvalueName_Index() {
        return valueName_Index;
    }

    /**
     * Gets the value_ index.
     *
     * @return the value_ index
     */
    public int getvalue_Index() {
        return value_Index;
    }

    /**
     * Gets the type_ index.
     *
     * @return the type_ index
     */
    public int getType_Index() {
        return type_Index;
    }

    /**
     * Adds the other requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param theOtherRequirement the the other requirement
     */
    public void addOtherRequirement(OtherRequirement theOtherRequirement){
        otherRequirements.add(theOtherRequirement);
    }

}
