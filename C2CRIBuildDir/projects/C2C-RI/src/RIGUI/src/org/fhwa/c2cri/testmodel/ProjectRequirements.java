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
 * The Class ProjectRequirements contains the set of defined Project Requirement objects.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ProjectRequirements implements Serializable{

    static final long  serialVersionUID = -655316874204551189L;
    
    /** The unique i d_ index. */
    private int uniqueID_Index;
    
    /** The other requirement_ index. */
    private int otherRequirement_Index;
    
    /** The title_ index. */
    private int title_Index;
    
    /** The text_ index. */
    private int text_Index;
    
    /** The type_ index. */
    private int type_Index;
    
    /** The flag name_ index. */
    private int flagName_Index;
    
    /** The flag value_ index. */
    private int flagValue_Index;

    /** The header list. */
    @XmlTransient
    public ArrayList<String> headerList;
    
    /** The requirements. */
    @XmlElement(name="requirement")
    public List<Requirement> requirements = new ArrayList<Requirement>();
    
    /** The requirements map. */
    @XmlTransient
    public HashMap requirementsMap = new HashMap<String,Requirement>();

    /** The requirements map. */
    @XmlTransient
    public LinkedHashMap lh_requirementsMap = new LinkedHashMap<String,Requirement>();

    /**
     * Instantiates a new project requirements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public ProjectRequirements(){
        // For JAXB creation of XML
//        requirementsMap = new HashMap<String,Requirement>();
    }

    /**
     * Instantiates a new project requirements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteRequirementsPath the test suite requirements path
     * @throws Exception the exception
     */
    public ProjectRequirements(URL testSuiteRequirementsPath) throws Exception {
        lh_requirementsMap = new LinkedHashMap();
        requirements = loadRequirements(testSuiteRequirementsPath);

        // Make the requirements locatable via the HashMap
        for (Requirement thisRequirement : requirements){
            lh_requirementsMap.put(thisRequirement.getTitle(), thisRequirement);
        }

    }

     /**
      * Instantiates a new project requirements.
      * 
      * Pre-Conditions: N/A
      * Post-Conditions: N/A
      *
      * @param predefinedTestSuiteRequirementsPath the predefined test suite requirements path
      * @param customTestSuiteRequirementsPath the custom test suite requirements path
      * @throws Exception the exception
      */
     public ProjectRequirements(URL predefinedTestSuiteRequirementsPath, URL customTestSuiteRequirementsPath) throws Exception {
        lh_requirementsMap = new LinkedHashMap();
        List<Requirement> predefinedRequirements = new ArrayList<Requirement>();
        List<Requirement> customRequirements = new ArrayList<Requirement>();

        predefinedRequirements = loadRequirements(predefinedTestSuiteRequirementsPath);
        customRequirements = loadRequirements(customTestSuiteRequirementsPath);

        List<Requirement> subRequirements = new ArrayList<Requirement>();

        for (Requirement thisRequirement : customRequirements){
            String requirementTitle = thisRequirement.getTitle();
            thisRequirement.setExtension(Boolean.TRUE);
            boolean okToAdd = true;
            for (Requirement existingRequirement: predefinedRequirements){
                if (requirementTitle.equals(existingRequirement.getTitle())){
                    System.err.println("Duplicate Requirement included =>"+requirementTitle);
                    okToAdd = false;
                    break;
                }
            }
            if (okToAdd)subRequirements.add(thisRequirement);

        }
        // The requirements from a predefined test suite are always included.
        requirements = predefinedRequirements;
        for (Requirement subRequirement: subRequirements){
            requirements.add(subRequirement);
        }

        // Make the requirements locatable via the HashMap
        for (Requirement thisRequirement : requirements){
            lh_requirementsMap.put(thisRequirement.getTitle(), thisRequirement);
        }

    }

    /**
     * Load requirements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteRequirementsPath the test suite requirements path
     * @return the list
     * @throws Exception the exception
     */
    private List<Requirement> loadRequirements(URL testSuiteRequirementsPath) throws Exception{
        List<Requirement> testSuiteRequirements = new ArrayList<Requirement>();

        CSVFileParser csvParser = new CSVFileParser();
        csvParser.parse(testSuiteRequirementsPath);
        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        uniqueID_Index = headerList.indexOf(ProjectRequirementsInterface.uniqueID_Header);
        otherRequirement_Index = headerList.indexOf(ProjectRequirementsInterface.otherRequirement_Header);
        title_Index = headerList.indexOf(ProjectRequirementsInterface.title_Header);
        text_Index = headerList.indexOf(ProjectRequirementsInterface.text_Header);
        type_Index = headerList.indexOf(ProjectRequirementsInterface.type_Header);
        flagName_Index = headerList.indexOf(ProjectRequirementsInterface.flagName_Header);
        flagValue_Index = headerList.indexOf(ProjectRequirementsInterface.flagValue_Header);


        ArrayList<ArrayList<String>> requirementsData = csvParser.getCsvData();

        int need_index = 1;
        for (ArrayList<String> dataList : requirementsData) {

            int uniqueID = 0;
            String officialID = "";
            String title = "";
            String text = "";
            String type = "";
            String flagName = "";
            Boolean flagValue = false;

            if ((uniqueID_Index == -1)
                    || (otherRequirement_Index == -1)
                    || (title_Index == -1)
                    || (text_Index == -1)
                    || (type_Index == -1)
                    || (flagName_Index == -1)
                    || (flagValue_Index == -1)) {
                throw new Exception(" Missing Required Index in CSV File ");
            } else {
                if (uniqueID_Index > -1) {
                    uniqueID = Integer.parseInt(dataList.get(uniqueID_Index));
                }
                if (otherRequirement_Index > -1) {
                    officialID = dataList.get(otherRequirement_Index);
                }
                if (title_Index > -1) {
                    title = dataList.get(title_Index);
                }
                if (text_Index > -1) {
                    text = dataList.get(text_Index);
                }
                if (type_Index > -1) {
                    type = dataList.get(type_Index);
                }
                if (flagName_Index > -1) {
                    flagName = dataList.get(flagName_Index);
                }
                if (flagValue_Index > -1) {
                    flagValue = Boolean.parseBoolean(dataList.get(flagValue_Index));
                }

                Requirement projectRequirement = new Requirement(uniqueID, officialID, title, text, type, flagName, flagValue);
                need_index++;
                System.out.println(" Adding Requirement " + projectRequirement.getTitle() + " #"+need_index);
                testSuiteRequirements.add(projectRequirement);
//                needsMap.put(projectRequirement.getTitle(), projectRequirement);
            }

        }
        return testSuiteRequirements;
    }

    /**
     * Gets the flag value_ index.
     *
     * @return the flag value_ index
     */
    public int getFlagValue_Index() {
        return flagValue_Index;
    }

    /**
     * Gets the text_ index.
     *
     * @return the text_ index
     */
    public int getText_Index() {
        return text_Index;
    }

    /**
     * Gets the title_ index.
     *
     * @return the title_ index
     */
    public int getTitle_Index() {
        return title_Index;
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
     * Adds the requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param thisRequirement the this requirement
     */
    public void addRequirement(Requirement thisRequirement){
        requirements.add(thisRequirement);
        lh_requirementsMap.put(thisRequirement.getTitle(), thisRequirement);
    }

    /**
     * Gets the requirement.
     *
     * @param requirementID the requirement id
     * @return the requirement
     */
    public Requirement getRequirement(String requirementID){
        if (lh_requirementsMap.containsKey(requirementID)){
            return (Requirement)lh_requirementsMap.get(requirementID);
        }

        return null;
    }
    
    /**
     * Gets the other requirements.
     *
     * @param requirementID the requirement id
     * @return the other requirements
     */
    public ArrayList<OtherRequirement> getOtherRequirements(String requirementID){
         ArrayList<OtherRequirement> tempOtherRequirementList = new ArrayList<OtherRequirement>();

         Requirement thisRequirement = (Requirement)lh_requirementsMap.get(requirementID);
         if ((thisRequirement!=null)&&(thisRequirement.getOtherRequirements().otherRequirements != null)) tempOtherRequirementList = new ArrayList(thisRequirement.getOtherRequirements().otherRequirements);

         return tempOtherRequirementList;
    }

   // using readObject method to set default values
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        // deserialize the non-transient data members first;
        input.defaultReadObject();
        if (lh_requirementsMap == null){
            lh_requirementsMap = new LinkedHashMap(requirementsMap);
            requirementsMap.clear();
            requirementsMap = null;
        }
    }
     
}
