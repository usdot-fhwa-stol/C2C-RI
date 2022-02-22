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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.fhwa.c2cri.utilities.CSVFileParser;

/**
 * The Class NRTM represents a single entry in the NRTM.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NRTM implements Serializable {

    static final long serialVersionUID = -2296348678119809894L;
    
    /** The id_ index. */
    private int id_Index;
    
    /** The u ni d_ index. */
    private int uNID_Index;
    
    /** The user need_ index. */
    private int userNeed_Index;
    
    /** The need conformance_ index. */
    private int needConformance_Index;
    
    /** The need support_ index. */
    private int needSupport_Index;
    
    /** The requirement i d_ index. */
    private int requirementID_Index;
    
    /** The requirement_ index. */
    private int requirement_Index;
    
    /** The conformance_ index. */
    private int conformance_Index;
    
    /** The support_ index. */
    private int support_Index;
    
    /** The other requirements_ index. */
    private int otherRequirements_Index;
    
    /** The need flag_ index. */
    private int needFlag_Index;
    
    /** The need flag value_ index. */
    private int needFlagValue_Index;
    
    /** The req flag_ index. */
    private int reqFlag_Index;
    
    /** The req flag value_ index. */
    private int reqFlagValue_Index;
    
    /** The other req parameter_ index. */
    private int otherReqParameter_Index;
    
    /** The other requirement values_ index. */
    private int otherRequirementValues_Index;
    
    /** The nrtm. */
    private HashMap nrtm;
    
    /** The linkedHashMap NRTM */
    private LinkedHashMap lh_nrtm;
    
    /** The header list. */
    @XmlTransient
    public ArrayList<String> headerList;

    /**
     * Instantiates a new nrtm.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private NRTM() {
        // for JAXB XML serialization
    }

    /**
     * Instantiates a new nrtm.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteNRTMPath the test suite nrtm path
     * @throws Exception the exception
     */
    public NRTM(URL testSuiteNRTMPath) throws Exception {
        lh_nrtm = new LinkedHashMap(loadNRTM(testSuiteNRTMPath, false));
    }

    /**
     * Instantiates a new nrtm.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuiteNRTMPath the predefined test suite nrtm path
     * @param customTestSuiteNRTMPath the custom test suite nrtm path
     * @throws Exception the exception
     */
    public NRTM(URL predefinedTestSuiteNRTMPath, URL customTestSuiteNRTMPath) throws Exception {
        lh_nrtm = new LinkedHashMap(loadNRTM(predefinedTestSuiteNRTMPath, false));
        HashMap tempMap = loadNRTM(customTestSuiteNRTMPath, true);

        Collection c = tempMap.keySet();
        Iterator itr = c.iterator();

        System.out.println(tempMap);
        // iterate through the custom NRTM to be sure no needs are duplicates of predefined test suite needs
        while (itr.hasNext()) {
            String thisKey = (String) itr.next();
            if (!lh_nrtm.containsKey(thisKey)) {
                // This need is unique so add to the NRTM
                try {

                    lh_nrtm.put(thisKey, tempMap.get(thisKey));
                    System.out.println("NRTM Performed Lookup for Key " + thisKey);

                } catch (Exception e) {
                    System.out.println("***** NRTM Failed Lookup for Key " + thisKey);
                }
            } else {
                System.err.println(" Need " + thisKey + " is already defined in predefined test suite");

            }
        }

    }

    /**
     * Load nrtm.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteNRTMPath the test suite nrtm path
     * @param extension the extension
     * @return the hash map
     * @throws Exception the exception
     */
    private HashMap loadNRTM(URL testSuiteNRTMPath, Boolean extension) throws Exception {
        HashMap tempNRTM = new HashMap();
        HashMap<String, Need> tempNeedMap = new HashMap<String, Need>();
        CSVFileParser csvParser = new CSVFileParser();
        csvParser.parse(testSuiteNRTMPath);
        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        id_Index = headerList.indexOf(NRTMInterface.ID_HEADER);
        uNID_Index = headerList.indexOf(NRTMInterface.UN_ID_HEADER);
        userNeed_Index = headerList.indexOf(NRTMInterface.USER_NEED_HEADER);
        needConformance_Index = headerList.indexOf(NRTMInterface.NEEDCONFORMANCE_HEADER);
        needSupport_Index = headerList.indexOf(NRTMInterface.NEEDSUPPORT_HEADER);
        requirementID_Index = headerList.indexOf(NRTMInterface.REQUIREMENT_ID_HEADER);
        requirement_Index = headerList.indexOf(NRTMInterface.REQUIREMENT_HEADER);
        conformance_Index = headerList.indexOf(NRTMInterface.CONFORMANCE_HEADER);
        support_Index = headerList.indexOf(NRTMInterface.SUPPORT_HEADER);
        otherRequirements_Index = headerList.indexOf(NRTMInterface.OTHER_REQUIREMENTS_HEADER);
        needFlag_Index = headerList.indexOf(NRTMInterface.NEEDFLAG_HEADER);
        needFlagValue_Index = headerList.indexOf(NRTMInterface.NEEDFLAGVALUE_HEADER);
        reqFlag_Index = headerList.indexOf(NRTMInterface.REQFLAG_HEADER);
        reqFlagValue_Index = headerList.indexOf(NRTMInterface.REQFLAGVALUE_HEADER);
        otherReqParameter_Index = headerList.indexOf(NRTMInterface.OTHERREQPARAMETER_HEADER);
        otherRequirementValues_Index = headerList.indexOf(NRTMInterface.OTHERREQUIREMENTVALUES_HEADER);


        ArrayList<ArrayList<String>> ntrmData = csvParser.getCsvData();

        int need_index = 1;
        for (ArrayList<String> dataList : ntrmData) {


            String id = "";
            String uNID = "";
            String userNeed = "";
            String needConformance = "";
            String needSupport = "";
            String requirementID = "";
            String requirement = "";
            String conformance = "";
            String support = "";
            String otherRequirements = "";
            String needFlag = "";
            String needFlagValue = "";
            String reqFlag = "";
            String reqFlagValue = "";
            String otherReqParameter = "";
            String otherRequirementValues = "";



            if ((id_Index == -1)
                    || (uNID_Index == -1)
                    || (userNeed_Index == -1)
                    || (needConformance_Index == -1)
                    || (needSupport_Index == -1)
                    || (requirementID_Index == -1)
                    || (requirement_Index == -1)
                    || (conformance_Index == -1)
                    || (support_Index == -1)
                    || (otherRequirements_Index == -1)
                    || (needFlag_Index == -1)
                    || (needFlagValue_Index == -1)
                    || (reqFlag_Index == -1)
                    || (reqFlagValue_Index == -1)
                    || (otherReqParameter_Index == -1)
                    || (otherRequirementValues_Index == -1)) {


                throw new Exception(" Missing Required Index in CSV File ");
            } else {
                if (dataList.size() > 0) {
                    if (id_Index > -1) {
                        id = dataList.get(id_Index);
                    }
                    if (uNID_Index > -1) {
                        uNID = dataList.get(uNID_Index);
                    }
                    if (userNeed_Index > -1) {
                        userNeed = dataList.get(userNeed_Index);
                    }
                    if (needConformance_Index > -1) {
                        needConformance = dataList.get(needConformance_Index);
                    }
                    if (needSupport_Index > -1) {
                        needSupport = dataList.get(needSupport_Index);
                    }
                    if (requirementID_Index > -1) {
                        requirementID = dataList.get(requirementID_Index);
                    }
                    if (requirement_Index > -1) {
                        requirement = dataList.get(requirement_Index);
                    }
                    if (conformance_Index > -1) {
                        conformance = dataList.get(conformance_Index);
                    }
                    if (support_Index > -1) {
                        support = dataList.get(support_Index);
                    }
                    if (otherRequirements_Index > -1) {
                        otherRequirements = dataList.get(otherRequirements_Index);
                    }
                    if (needFlag_Index > -1) {
                        needFlag = dataList.get(needFlag_Index);
                    }
                    if (needFlagValue_Index > -1) {
                        needFlagValue = dataList.get(needFlagValue_Index);
                    }
                    if (reqFlag_Index > -1) {
                        reqFlag = dataList.get(reqFlag_Index);
                    }
                    if (reqFlagValue_Index > -1) {
                        reqFlagValue = dataList.get(reqFlagValue_Index);
                    }
                    if (otherReqParameter_Index > -1) {
                        otherReqParameter = dataList.get(otherReqParameter_Index);
                    }
                    if (otherRequirementValues_Index > -1) {
                        otherRequirementValues = dataList.get(otherRequirementValues_Index);
                    }

                    // Check to see if the need is already within the map.
                    // If yes, add the requirement to the requirements list.
                    if (tempNRTM.containsKey(uNID)) {
                        ArrayList<String> reqList = (ArrayList<String>) tempNRTM.get(uNID);
                        // Only add the requirement if it is not already included in the list
                        if (!reqList.contains(requirementID)) {
                            reqList.add(requirementID);

                            Need thisNeed = tempNeedMap.get(uNID);
                            Requirement projectRequirement = new Requirement(Integer.parseInt(id), requirementID, requirementID, requirement, conformance, reqFlag, (reqFlagValue.equalsIgnoreCase("true") ? true : false));
                            projectRequirement.setExtension(extension);
                            thisNeed.addRequirement(projectRequirement);

                            if (!otherRequirements.isEmpty()||(!otherRequirementValues.isEmpty())) {
                                OtherRequirement theOtherRequirement = new OtherRequirement(requirementID, otherRequirements, otherRequirementValues, otherReqParameter, 0, 9999, "Integer");
                                projectRequirement.addOtherRequirement(theOtherRequirement);
                            }

                        }
                    } else {
                        // add the need and requirement to the nrtm map
                        ArrayList<String> reqList = new ArrayList<String>();
                        reqList.add(requirementID);
                        tempNRTM.put(uNID, reqList);

                        Need thisNeed = new Need(need_index, uNID, uNID, userNeed, needConformance, needFlag, (needFlagValue.equalsIgnoreCase("true") ? true : false));
                        thisNeed.setExtension(extension);
                        userNeeds.addNeed(thisNeed);
                        tempNeedMap.put(uNID, thisNeed);
                        Requirement projectRequirement = new Requirement(Integer.parseInt(id), requirementID, requirementID, requirement, conformance, reqFlag, (reqFlagValue.equalsIgnoreCase("true") ? true : false));
                        projectRequirement.setExtension(extension);
                        thisNeed.addRequirement(projectRequirement);

                        if (!otherRequirements.isEmpty()||!otherRequirementValues.isEmpty()) {
                            OtherRequirement theOtherRequirement = new OtherRequirement(requirementID, otherRequirements, otherRequirementValues, otherReqParameter, 0, 9999, "Integer");
                            projectRequirement.addOtherRequirement(theOtherRequirement);
                        }

                    }
                    System.out.println(" Processing Need " + uNID + " => Requirement " + requirementID);
                }
            }
        }
        return tempNRTM;
    }

    /**
     * Gets the requirements list.
     *
     * @param needID the need id
     * @return the requirements list
     */
    public ArrayList<String> getRequirementsList(String needID) {
        ArrayList<String> returnList = new ArrayList<String>();

        if (lh_nrtm.containsKey(needID)) {
            returnList = (ArrayList<String>) lh_nrtm.get(needID);
        }

        return returnList;

    }
    
    /** Temporary Constructors to transition from segmented NRTM to united NRTM. */
    @XmlElement
    private UserNeeds userNeeds = new UserNeeds();

    /**
     * returns the current information layer User Needs.
     *
     * @return the user needs
     */
    public UserNeeds getUserNeeds() {
        return userNeeds;
    }

    /**
     * returns the current information layer Project Requirements.
     *
     * @param needID the need id
     * @return the need related requirements
     */
    public ArrayList<Requirement> getNeedRelatedRequirements(String needID) {
        ArrayList<Requirement> tempRequirementList = new ArrayList<Requirement>();
        for (Need thisNeed : userNeeds.needs) {
            if (thisNeed.getTitle().equals(needID)) {
                tempRequirementList = new ArrayList(thisNeed.getProjectRequirements().requirements);
            }
        }

        return tempRequirementList;
    }

    /**
     * returns the current information layer Project Requirements.
     *
     * @param needID the need id
     * @param requirementID the requirement id
     * @return the other requirements
     */
    public ArrayList<OtherRequirement> getOtherRequirements(String needID, String requirementID) {
        ArrayList<OtherRequirement> tempOtherRequirementList = new ArrayList<OtherRequirement>();

        for (Need thisNeed : userNeeds.needs) {
            if (thisNeed.getTitle().equals(needID)) {
                for (Requirement thisRequirement : thisNeed.getProjectRequirements().requirements) {
                    if (thisRequirement.getTitle().equals(requirementID)) {
                        tempOtherRequirementList = new ArrayList<OtherRequirement>(thisRequirement.getOtherRequirements().otherRequirements);
                    }
                }
            }
        }

        return tempOtherRequirementList;
    }

    // using readObject method to set default values
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        // deserialize the non-transient data members first;
        input.defaultReadObject();
        if (lh_nrtm == null){
            lh_nrtm = new LinkedHashMap(nrtm);
            nrtm.clear();
            nrtm = null;
        }

    }
    
}
