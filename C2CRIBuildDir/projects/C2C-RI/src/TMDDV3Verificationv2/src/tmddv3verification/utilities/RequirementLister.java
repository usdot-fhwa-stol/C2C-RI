/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification.utilities;

import tmddv3verification.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import tmddv3verification.testing.DialogTester;
import tmddv3verification.testing.ElementTester;
import tmddv3verification.testing.FrameTester;
import tmddv3verification.testing.MessageTester;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class RequirementLister {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String wsdlFileName = "file:\\c:\\inout\\tmp\\tmdd_modified.wsdl";
//        String wsdlFileName = "file:\\c:\\inout\\tmddschemasv9\\tmdd.wsdl";
//        DesignDetail tmddDesign = new DesignDetail(wsdlFileName);

        Map<String,Integer> requirementMap = new HashMap<String,Integer>();
        TreeSet<String> requirementsList = new TreeSet<String>(new MyComparator());
//        List<Predicate> predicatesList = new ArrayList<Predicate>();

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


        ResultSet requirementIDRS = theDatabase.queryReturnRS("Select distinct RequirementID, Requirement from NRTM order by RequirementID");
        int requirementCount = 1;
        try {
            while (requirementIDRS.next()) {
                String requirementID = requirementIDRS.getString("RequirementID");
                String requirement = requirementIDRS.getString("Requirement");
 
                if (!requirementsList.contains(requirementID)){
                    requirementsList.add(requirementID);
                    System.out.println("Added "+requirementID+" for Requirement "+requirement);
                    requirementCount++;
                } else {
                    System.out.println("!!! Duplicate? !!!"+requirementID+" for requirement "+requirement);
                }
//                if (!requirementMap.containsKey(requirementID)){
//                    requirementMap.put(requirementID, requirementCount);
//                    requirementCount++;
//                }

            }
            requirementIDRS.close();
            requirementIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Deleting all records from the RequirementsList table.");
        theDatabase.queryNoResult("delete * from RequirementsList");
//        System.out.println("ID,UNID");
        Iterator requirementIter = requirementMap.keySet().iterator();
        System.out.println("Adding records to the RequirementsList table.");

        Integer requirementNumber = 1;
        for (String requirementsID : requirementsList){
//        while (requirementIter.hasNext()){
//            String needID = (String)requirementIter.next();
            theDatabase.queryNoResult("insert INTO RequirementsList (RequirementNumber, RequirementID) " +
                    "VALUES (" + requirementNumber+", "+
                            "'" + requirementsID + "')");
            requirementNumber++;
//                    "VALUES (" + requirementMap.get(needID)+", "+
//                            "'" + needID + "')");

//            System.out.println(requirementMap.get(needID)+","+needID);
        }
        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
    }


static class MyComparator implements Comparator<String> {
    private Integer greater = 1;
    private Integer less = -1;
    private Integer equal = 0;

    public int compare(String value, String compareTo ){
        String[] valueElements = value.split("\\.");
        String[] compareToElements = compareTo.split("\\.");

   
        Integer minLength = Math.min(valueElements.length,compareToElements.length);

        // First check the parts that can be compared
        for (int ii = 0; ii<minLength; ii++){
            if (Integer.parseInt(valueElements[ii]) < Integer.parseInt(compareToElements[ii])){
 //               System.out.println("Compare "+value + " to " +compareTo+"  : "+valueElements[ii] +" is less than"+compareToElements[ii]);
                return less;
            } else if (Integer.parseInt(valueElements[ii]) > Integer.parseInt(compareToElements[ii])){
//                System.out.println("Compare "+value + " to " +compareTo+"  : "+valueElements[ii] +" is greater than"+compareToElements[ii]);
                return greater;
            }
        }

        // The parts that can be compared were equal.
        if (valueElements.length > compareToElements.length) return greater;
        if (valueElements.length < compareToElements.length) return less;
        return equal;

    }
 }


}
