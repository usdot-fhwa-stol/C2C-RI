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
 * The Class Predicates contains the set of defined predicates for a standard or project.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Predicates implements Serializable{

    static final long serialVersionUID = 4828171542243348571L;
    
    /** The predicate_ index. */
    private int predicate_Index;
    
    /** The section_ index. */
    private int section_Index;
    
    /** The parent need_ index. */
    private int parentNeed_Index;


    /** The predicates map. */
    @XmlTransient
    private HashMap<String, Predicate> predicatesMap = new HashMap();

    /** The predicates map. */
    @XmlElement
    private LinkedHashMap<String, Predicate> predicates_Map = new LinkedHashMap();
    
    /**
     * Instantiates a new predicates.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private Predicates(){
        // for JAXB Serialization
    }

    /**
     * Instantiates a new predicates.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuitePredicatesPath the test suite predicates path
     * @throws Exception the exception
     */
    public Predicates(URL testSuitePredicatesPath) throws Exception {
        predicates_Map = new LinkedHashMap(loadPredicates(testSuitePredicatesPath));

    }

    /**
     * Instantiates a new predicates.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param predefinedTestSuitePredicatesPath the predefined test suite predicates path
     * @param customTestSuitePredicatesPath the custom test suite predicates path
     * @throws Exception the exception
     */
    public Predicates(URL predefinedTestSuitePredicatesPath, URL customTestSuitePredicatesPath) throws Exception {
        predicates_Map = new LinkedHashMap(loadPredicates(predefinedTestSuitePredicatesPath));
        HashMap<String, Predicate> tempMap = loadPredicates(customTestSuitePredicatesPath);

        Collection c = tempMap.keySet();
        Iterator itr = c.iterator();

        System.out.println(tempMap);
        // iterate through the custom NRTM to be sure no needs are duplicates of predefined test suite needs
        while (itr.hasNext()){
            String thisKey = (String) itr.next();
            if (!predicates_Map.containsKey(thisKey)){
                // This need is unique so add to the NRTM
                try {
                    predicates_Map.put(thisKey, tempMap.get(thisKey));
                    System.out.println("Predicates Performed Lookup for Key "+ thisKey);

                } catch (Exception e){
                    System.out.println("***** Predicates Failed Lookup for Key "+ thisKey);
                }
            } else{
                System.err.println(" Predicate "+ thisKey + " is already defined in predefined test suite");

            }
        }

    }

    /**
     * Load predicates.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testSuitePredicatesPath the test suite predicates path
     * @return the hash map
     * @throws Exception the exception
     */
    private HashMap<String, Predicate> loadPredicates(URL testSuitePredicatesPath) throws Exception{
        HashMap tempPredicates = new HashMap();
        ArrayList<String> headerList;
        //        PredicatesMap = new HashMap();
        CSVFileParser csvParser = new CSVFileParser();

        csvParser.parse(testSuitePredicatesPath);

        System.out.println(" Parsed the file");
        headerList = csvParser.getHeaderList();

        predicate_Index = headerList.indexOf(PredicatesInterface.Predicate_Header);
        section_Index = headerList.indexOf(PredicatesInterface.Section_Header);
        parentNeed_Index = headerList.indexOf(PredicatesInterface.ParentNeed_Header);


        ArrayList<ArrayList<String>> PredicatesData = csvParser.getCsvData();

        for (ArrayList<String> dataList : PredicatesData) {

            String predicate = "";
            String section = "";
            String parentNeed = "";

            if ((predicate_Index == -1)
                    || (section_Index == -1)) {
                throw new Exception(" A Required Index is Missing in the CSV File ");
            } else {
                if (predicate_Index > -1) {
                    predicate = dataList.get(predicate_Index).trim();
                }
                if (section_Index > -1) {
                    section = dataList.get(section_Index).trim();
                }
                
                Predicate thisPredicate = null;
                if (parentNeed_Index > -1){
                    parentNeed = dataList.get(parentNeed_Index).trim();
                    thisPredicate = new Predicate(predicate,section,parentNeed);
                } else {
                    thisPredicate = new Predicate(predicate,section);
                }
               // Check to see if the predicate is already within the map.
                // If not, add the predicate to the predicates list.
                if (tempPredicates.containsKey(thisPredicate.getName())){
                    System.out.println(" Predicate " + predicate + " Already exists in the Map");

                } else{
                    // add the predicate and section to the predicates map
                    tempPredicates.put(thisPredicate.getName(), thisPredicate);
                }
                System.out.println(" Processing Predicate " + predicate + " => Section "+section);
            }

        }
        return tempPredicates;
    }

    /**
     * Gets the predicates map.
     *
     * @return the predicates map
     */
    public HashMap getPredicatesMap() {
        HashMap tmpMap = new HashMap();
        for (String keyName : predicates_Map.keySet()){
            tmpMap.put(keyName, predicates_Map.get(keyName).getSection());
        }
        return tmpMap;
    }

    /**
     * Gets the predicates map new.
     *
     * @return the predicates map new
     */
    public HashMap<String, Predicate> getPredicatesMapNew() {
        return predicates_Map;
    }

    // using readObject method to set default values
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        // deserialize the non-transient data members first;
        input.defaultReadObject();
        if (predicates_Map == null){
            predicates_Map = new LinkedHashMap(predicatesMap);
            predicatesMap.clear();
            predicatesMap = null;
        }

    }
    
}
