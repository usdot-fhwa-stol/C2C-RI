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
import org.fhwa.c2cri.utilities.CSVFileParser;

/**
 * The Class UserNeeds.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class UserNeeds implements Serializable{

    /** The unique i d_ index. */
    private int uniqueID_Index;
    
    /** The official i d_ index. */
    private int officialID_Index;
    
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


    /** The needs. */
    @XmlElement(name="need")
    public List<Need> needs = new ArrayList<Need>();
//    private HashMap needsMap;

    /**
 * Instantiates a new user needs.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 */
public UserNeeds(){
        // For JAXB creation of XML
    }

    /**
     * Instantiates a new user needs.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteNeedsPath the test suite needs path
     * @throws Exception the exception
     */
    public UserNeeds(URL testSuiteNeedsPath) throws Exception {
        needs = loadNeeds(testSuiteNeedsPath);

    }

    /**
     * Instantiates a new user needs.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuiteNeedsPath the predefined test suite needs path
     * @param customTestSuiteNeedsPath the custom test suite needs path
     * @throws Exception the exception
     */
    public UserNeeds(URL predefinedTestSuiteNeedsPath, URL customTestSuiteNeedsPath) throws Exception {
        List<Need> predefinedNeeds = new ArrayList<Need>();
        List<Need> customNeeds = new ArrayList<Need>();

        predefinedNeeds = loadNeeds(predefinedTestSuiteNeedsPath);
        customNeeds = loadNeeds(customTestSuiteNeedsPath);

        List<Need> subNeeds = new ArrayList<Need>();

        for (Need thisNeed : customNeeds){
            String needTitle = thisNeed.getTitle();
            thisNeed.setExtension(Boolean.TRUE);
            boolean okToAdd = true;
            for (Need existingNeed: predefinedNeeds){
                 if (needTitle.equals(existingNeed.getTitle())){
                    System.err.println("Duplicate Need included =>"+needTitle);
                    okToAdd = false;
                    break;
                }
            }
            if (okToAdd)subNeeds.add(thisNeed);

        }
        needs = predefinedNeeds;
        System.out.println(" Final count of custom user needs = "+ subNeeds.size());
        for (Need subNeed: subNeeds){
            needs.add(subNeed);
        }

    }

    /**
     * Load needs.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteNeedsPath the test suite needs path
     * @return the list
     * @throws Exception the exception
     */
    private List<Need> loadNeeds(URL testSuiteNeedsPath) throws Exception{
        ArrayList<String> headerList;
        List<Need> testSuiteNeeds = new ArrayList<Need>();
        //        needsMap = new HashMap();
        CSVFileParser csvParser = new CSVFileParser();

        csvParser.parse(testSuiteNeedsPath);

        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        uniqueID_Index = headerList.indexOf(UserNeedsInterface.uniqueID_Header);
        officialID_Index = headerList.indexOf(UserNeedsInterface.officialID_Header);
        title_Index = headerList.indexOf(UserNeedsInterface.title_Header);
        text_Index = headerList.indexOf(UserNeedsInterface.text_Header);
        type_Index = headerList.indexOf(UserNeedsInterface.type_Header);
        flagName_Index = headerList.indexOf(UserNeedsInterface.flagName_Header);
        flagValue_Index = headerList.indexOf(UserNeedsInterface.flagValue_Header);


        ArrayList<ArrayList<String>> needsData = csvParser.getCsvData();

        int need_index = 1;
        for (ArrayList<String> dataList : needsData) {

            int uniqueID = 0;
            String officialID = "";
            String title = "";
            String text = "";
            String type = "";
            String flagName = "";
            Boolean flagValue = false;

            if ((uniqueID_Index == -1)
                    || (officialID_Index == -1)
                    || (title_Index == -1)
                    || (text_Index == -1)
                    || (type_Index == -1)
                    || (flagName_Index == -1)
                    || (flagValue_Index == -1)) {
                throw new Exception(" A Required Index is Missing in the CSV File ");
            } else {
                if (uniqueID_Index > -1) {
                    uniqueID = Integer.parseInt(dataList.get(uniqueID_Index));
                }
                if (officialID_Index > -1) {
                    officialID = dataList.get(officialID_Index);
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

                Need userNeed = new Need(uniqueID, officialID, title, text, type, flagName, flagValue);
                need_index++;
                System.out.println(" Adding Need " + userNeed.getTitle() + " #"+need_index);
                testSuiteNeeds.add(userNeed);
//                needsMap.put(userNeed.getTitle(), userNeed);
            }
        }
        return testSuiteNeeds;
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
     * Adds the need.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param theNeed the the need
     */
    public void addNeed(Need theNeed){
        needs.add(theNeed);
    }

    /**
     * Gets the need.
     *
     * @param needID the need id
     * @return the need
     */
    public Need getNeed(String needID){

        for (Need theNeed : needs){
            if (theNeed.getTitle().equals(needID)){
               return theNeed;
            }
        }

        return null;
    }
}
