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
public class NeedLister {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String wsdlFileName = "file:\\c:\\inout\\tmp\\tmdd_modified.wsdl";
//        String wsdlFileName = "file:\\c:\\inout\\tmddschemasv9\\tmdd.wsdl";
//        DesignDetail tmddDesign = new DesignDetail(wsdlFileName);

        Map<String,Integer> needMap = new HashMap<String,Integer>();
        TreeSet<String> needsList = new TreeSet<String>(new MyComparator());
//        List<Predicate> predicatesList = new ArrayList<Predicate>();

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


        ResultSet needIDRS = theDatabase.queryReturnRS("Select distinct UNID, UserNeed, UNSelected from NRTM order by UNID");
        int needCount = 1;
        try {
            while (needIDRS.next()) {
                String unID = needIDRS.getString("UNID");
                String userNeed = needIDRS.getString("UserNeed");
                String selected = needIDRS.getString("UNSelected");

                if (!needsList.contains(unID)){
                    needsList.add(unID);
                    needCount++;
                }
//                if (!needMap.containsKey(unID)){
//                    needMap.put(unID, needCount);
//                    needCount++;
//                }

            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Deleting all records from the NeedsList table.");
        theDatabase.queryNoResult("delete * from NeedsList");
//        System.out.println("ID,UNID");
        Iterator needIter = needMap.keySet().iterator();
        System.out.println("Adding records to the NeedsList table.");

        Integer needNumber = 1;
        for (String needID : needsList){
//        while (needIter.hasNext()){
//            String needID = (String)needIter.next();
            theDatabase.queryNoResult("insert INTO NeedsList (NeedNumber, UNID) " +
                    "VALUES (" + needNumber+", "+
                            "'" + needID + "')");
            needNumber++;
//                    "VALUES (" + needMap.get(needID)+", "+
//                            "'" + needID + "')");

//            System.out.println(needMap.get(needID)+","+needID);
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
