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
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.fhwa.c2cri.utilities.CSVFileParser;

/**
 * The Class TestCaseMatrix represents the associations defined between the needs, requirements and test cases.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCaseMatrix implements Serializable{

   static final long serialVersionUID = -3726583686381232662L;
    
    /** The need id index. */
    private int needID_Index;
    
    /** The test case id index. */
    private int tcID_Index;
    
    /** The requirement id index. */
    private int requirementID_Index;
    
    /** The item type index. */
    private int itemType_Index;
    
    /** The precondition index. */
    private int precondition_Index;

    /** The test case matrix. */
    @XmlTransient
    private HashMap<MatrixIndex, ArrayList<TestCaseMatrixEntry>> testCaseMatrix;

    /** The new test case matrix. */
    @XmlTransient
    private LinkedHashMap<MatrixIndex, ArrayList<TestCaseMatrixEntry>> lh_testCaseMatrix;
    
    /** The test case matrix items. */
    @XmlElement
    private ArrayList<TestCaseMatrixEntry> testCaseMatrixItems = new ArrayList<TestCaseMatrixEntry>();

    /** The header list. */
    @XmlTransient
    public ArrayList<String> headerList;

    /**
     * Instantiates a new test case matrix.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCaseMatrix(){
        // for JAXB XML Serialization
    }
    
    /**
     * Instantiates a new test case matrix.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteNRTMPath the test suite nrtm path
     * @throws Exception the exception
     */
    public TestCaseMatrix(URL testSuiteNRTMPath) throws Exception {
        lh_testCaseMatrix = new LinkedHashMap(loadTestCaseMatrix(testSuiteNRTMPath));
    }


    /**
     * Instantiates a new test case matrix.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuiteTCMPath the predefined test suite tcm path
     * @param customTestSuiteTCMPath the custom test suite tcm path
     * @throws Exception the exception
     */
    public TestCaseMatrix(URL predefinedTestSuiteTCMPath, URL customTestSuiteTCMPath) throws Exception {
        lh_testCaseMatrix = new LinkedHashMap(loadTestCaseMatrix(predefinedTestSuiteTCMPath));
        HashMap<MatrixIndex, ArrayList<TestCaseMatrixEntry>> tempMap = loadTestCaseMatrix(customTestSuiteTCMPath);

        Collection c = tempMap.keySet();
        Iterator itr = c.iterator();

        System.out.println(tempMap);
        // iterate through the custom NRTM to be sure no needs are duplicates of predefined test suite needs
        while (itr.hasNext()){
            MatrixIndex thisKey = (MatrixIndex) itr.next();
            if (!lh_testCaseMatrix.containsKey(thisKey)){
                // This Matrix Index (need,requirment,itemType) is unique so add to the TestCaseMatrix
                try {
                    lh_testCaseMatrix.put(thisKey, tempMap.get(thisKey));
                    System.out.println("TCM Performed Lookup for Key "+ thisKey);
                    
                } catch (Exception e){
                    System.out.println("***** TCM Failed Lookup for Key "+ thisKey);
                }
            } else{
                System.err.println(" Need "+ thisKey + " is already defined in predefined test suite");

            }
        }


        c = lh_testCaseMatrix.values();
        itr = c.iterator();

        // iterate through the custom NRTM to be sure no needs are duplicates of predefined test suite needs
        while (itr.hasNext()){
            ArrayList<TestCaseMatrixEntry> thisEntries = (ArrayList<TestCaseMatrixEntry>) itr.next();
            for (TestCaseMatrixEntry theEntry: thisEntries){
                testCaseMatrixItems.add(theEntry);                
            }
        }
    }

    /**
     * Load test case matrix.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuiteTCMPath the test suite tcm path
     * @return the hash map
     * @throws Exception the exception
     */
    private HashMap<MatrixIndex, ArrayList<TestCaseMatrixEntry>> loadTestCaseMatrix(URL testSuiteTCMPath) throws Exception{
        HashMap<MatrixIndex, ArrayList<TestCaseMatrixEntry>> tempTCMatrix = new HashMap<MatrixIndex, ArrayList<TestCaseMatrixEntry>>();
        CSVFileParser csvParser = new CSVFileParser();
        csvParser.parse(testSuiteTCMPath);
        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        needID_Index = headerList.indexOf(TestCaseMatrixInterface.NEEDID_Header);
        requirementID_Index = headerList.indexOf(TestCaseMatrixInterface.REQUIREMENTID_Header);
        itemType_Index = headerList.indexOf(TestCaseMatrixInterface.ITEMTYPE_Header);
        tcID_Index = headerList.indexOf(TestCaseMatrixInterface.TCID_Header);
        precondition_Index = headerList.indexOf(TestCaseMatrixInterface.PRECONDITION_Header);


        ArrayList<ArrayList<String>> tcmData = csvParser.getCsvData();

        int need_index = 1;
        for (ArrayList<String> dataList : tcmData) {

            String needID = "";
            String requirementID = "";
            String itemType = "";
            String tcID = "";
            String precondition = "";

            if ((needID_Index == -1)
                    || (requirementID_Index == -1)
                    || (itemType_Index == -1)
                    || (tcID_Index == -1)
                    || (precondition_Index == -1)) {
                throw new Exception(" Missing Required Index in CSV File ");
            } else {
                try{
                if (needID_Index > -1) {
                    needID = dataList.get(needID_Index);
                }
                if (requirementID_Index > -1) {
                    requirementID = dataList.get(requirementID_Index);
                }
                if (itemType_Index > -1) {
                    itemType = dataList.get(itemType_Index);
                }
                if (tcID_Index > -1) {
                    tcID = dataList.get(tcID_Index);
                }
                if (precondition_Index > -1) {
                    precondition = dataList.get(precondition_Index);
                }
                } catch (Exception ex) {
 //                   ex.printStackTrace();
                    System.err.println("Missing CSV Component in record: \n"
                            + "NeedId - " + needID_Index
                            + "\n requirementID_Index " + requirementID_Index
                            + "\n itemType_Index " + itemType_Index
                            + "\n tcID_Index " + tcID_Index
                            + "\n precondition_Index " + precondition_Index);
                   throw new Exception("Missing CSV Component in record: \n"
                            + "NeedId - " + needID_Index
                            + "\n requirementID_Index " + requirementID_Index
                            + "\n itemType_Index " + itemType_Index
                            + "\n tcID_Index " + tcID_Index
                            + "\n precondition_Index " + precondition_Index, ex);
               }

                // Check to see if the indexes are already within the map.
                // If yes, add the TestCase Trace to the TestCase Trace list.
                MatrixIndex theIndex = new MatrixIndex(needID, requirementID, itemType);
                if (tempTCMatrix.containsKey(theIndex)){
                    ArrayList<TestCaseMatrixEntry> testCaseList = (ArrayList<TestCaseMatrixEntry>)tempTCMatrix.get(theIndex);

                    TestCaseMatrixEntry theEntry = new TestCaseMatrixEntry(needID,tcID,requirementID,itemType,precondition);
                    // Only add the TestCaseMatrixEntry if it is not already included in the list
                    if (!testCaseList.contains(theEntry)){
                        testCaseList.add(theEntry);
                        System.out.println("*** Added List Entry "+ testCaseList.size());
                    }
                } else{
                    // add the matrix index and matrix Entry to the testCaseMatrix map
                    ArrayList<TestCaseMatrixEntry> testCaseList = new ArrayList<TestCaseMatrixEntry>();
                    TestCaseMatrixEntry theEntry = new TestCaseMatrixEntry(needID,tcID,requirementID,itemType,precondition);
                    testCaseList.add(theEntry);
                    System.out.println("*** Added First Entry in List");

                    tempTCMatrix.put(theIndex, testCaseList);
                }
                System.out.println(" Processing Need " + needID + " => Requirement "+requirementID+ " => ItemType "+itemType);
            }

        }
        return tempTCMatrix;
    }

    /**
     * Gets the test case matrix list.
     *
     * @param matrixID the matrix id
     * @return the test case matrix list
     */
    public ArrayList<TestCaseMatrixEntry> getTestCaseMatrixList(MatrixIndex matrixID){
        ArrayList<TestCaseMatrixEntry> returnList = null;

        if (lh_testCaseMatrix.containsKey(matrixID)){
            returnList = (ArrayList<TestCaseMatrixEntry>)lh_testCaseMatrix.get(matrixID);
        }

        return returnList;

    }

    /**
     * Gets the test cases.
     *
     * @param needID the need id
     * @param requirementID the requirement id
     * @param itemType the item type
     * @return the test cases
     */
    public List<TestCaseMatrixEntry> getTestCases(String needID, String requirementID, String itemType){
        List<TestCaseMatrixEntry> tempList = new ArrayList<TestCaseMatrixEntry>();

        String[] itemTypeList = itemType.split(";");

        for (int ii =0; ii<itemTypeList.length; ii++){
        MatrixIndex theIndex = new MatrixIndex(needID, requirementID, itemTypeList[ii]);

        if (lh_testCaseMatrix.containsKey(theIndex)){
            tempList.addAll(lh_testCaseMatrix.get(theIndex));
        };

        }
        return tempList;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return lh_testCaseMatrix.toString();
    }

    /**
     * The Class MatrixIndex.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class MatrixIndex implements Serializable{

        /** The need id. */
        String needID;
        
        /** The requirement id. */
        String requirementID;
        
        /** The item type. */
        String itemType;

        /**
         * Instantiates a new matrix index.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param needID the need id
         * @param requirementID the requirement id
         * @param itemType the item type
         */
        public MatrixIndex(String needID, String requirementID, String itemType){
            this.needID = needID;
            this.requirementID = requirementID;
            this.itemType = itemType;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            return ("\nMatrixIndex:  needID="+ needID+
                    "  requirementID="+requirementID+
                    "  itemType="+itemType+"\n");
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object other){
            if (other == this) return true;
            if (other==null) return false;
            if (getClass() != other.getClass()) return false;
            MatrixIndex theIndex = (MatrixIndex)other;
            return(needID.equals(theIndex.needID)&&
                   requirementID.equals(theIndex.requirementID)&&
                   itemType.equals(theIndex.itemType));
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.needID != null ? this.needID.hashCode() : 0);
            hash = 47 * hash + (this.requirementID != null ? this.requirementID.hashCode() : 0);
            hash = 47 * hash + (this.itemType != null ? this.itemType.hashCode() : 0);
            return hash;
        }
    }
    
    // using readObject method to set default values
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        // deserialize the non-transient data members first;
        input.defaultReadObject();
        if (lh_testCaseMatrix == null){
            lh_testCaseMatrix = new LinkedHashMap(testCaseMatrix);
            testCaseMatrix.clear();
            testCaseMatrix = null;
        }

    }
    
}
