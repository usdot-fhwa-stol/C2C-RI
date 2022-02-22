/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.transports;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xpathgen.XPathGenerator;

/**
 *
 * @author TransCore ITS
 */
public class XMLMessageVerification {

    public static ArrayList<String> errorsFound = new ArrayList<String>();

/**
 *
 * @param messageSpec
 * @param messageSpecList
 * @return
 */
    public static HashMap<String, ArrayList<String>> getInstancesOf(String messageSpec, ArrayList<String> messageSpecList){
        HashMap<String, ArrayList<String>> returnMap = new HashMap<String, ArrayList<String>>();

        for (String thisMessageElement : messageSpecList){
            if (isInstanceof(messageSpec, thisMessageElement)){
                String instanceName = getInstanceName(messageSpec, thisMessageElement);
                if (returnMap.containsKey(instanceName)){
                   returnMap.get(instanceName).add(thisMessageElement);
                } else {
                    ArrayList<String> returnList = new ArrayList<String>();
                    returnList.add(thisMessageElement);
                    returnMap.put(instanceName, returnList);
                }

            }
        }

        return returnMap;
    }


/**
 * 
 * @param dataConceptInstanceName
 * @param messageInstancesMap
 * @return
 */
    public static boolean existsInEveryInstance(String dataConceptInstanceName, HashMap<String, ArrayList<String>> messageInstancesMap){
        boolean results = true;

        Iterator instanceIterator = messageInstancesMap.keySet().iterator();

        while (instanceIterator.hasNext()){
            String thisInstance = (String)instanceIterator.next();
            ArrayList<String> instanceElements = messageInstancesMap.get(thisInstance);
            boolean foundMatchForInstance = false;
            for (String thisElement : instanceElements){
                // Strip out the intance part and see if the element exists
                String strippedInstance = thisElement.substring(thisInstance.length());

                if (strippedInstance.startsWith(".")){  // remove the leading "."
                    strippedInstance = strippedInstance.substring(".".length());
                }

                if (strippedInstance.contains(".")){  // Strip off any additional elemements
                    strippedInstance = strippedInstance.substring(0,strippedInstance.indexOf("."));
                }

                if (strippedInstance.contains("[")){  // Strip off any additional content includeing the index
                    strippedInstance = strippedInstance.substring(0,strippedInstance.indexOf("["));
                }

                if (strippedInstance.equals(dataConceptInstanceName)){
                    foundMatchForInstance = true;
                    break;
                }


            }

            if (!foundMatchForInstance) {
                errorsFound.add("Unable to find "+dataConceptInstanceName+" in "+thisInstance+".");
                results = false;
            }
        }

        return results;
    }

/**
 * 
 * @param messageSpec
 * @param messageSpecInstance
 * @return
 */
    public static String getInstanceName(String messageSpec, String messageSpecInstance){
        String instanceName="";
        
        // process the message spec instance
        String[] specInstanceList = messageSpecInstance.split("\\.");

        // check the instance against the spec
        String[] specList = messageSpec.split("\\.");

        if (specInstanceList.length > specList.length){
           for (int ii = 0; ii<specList.length; ii++){
               if (instanceName.equals("")){
                  instanceName =instanceName.concat(specInstanceList[ii]);
               } else {
                  instanceName =instanceName.concat("."+specInstanceList[ii]);
               }
           }
        }         
        
        return instanceName;
    }

 /**
 *
 * @param messageSpec
 * @param messageSpecInstance
 * @return
 */
    public static boolean isInstanceof(String messageSpec, String messageSpecInstance){
        boolean instanceMatch = true;

        // process the message spec instance
        String[] specInstanceList = messageSpecInstance.split("\\.");

        // check the instance against the spec
        String[] specList = messageSpec.split("\\.");

        if (specInstanceList.length > specList.length){
           for (int ii = 0; ii<specList.length; ii++){
              if (!specInstanceList[ii].startsWith(specList[ii])){
                  instanceMatch = false;
                  break;
              }
           }
        } else {
            instanceMatch = false;
        }

        return instanceMatch;
    }


    public static void main(String[] args) {

        String xmlFileName = "c:/inout/tmdd.xml";
//        String xmlFileName = "c:/inout/sampleXML.xml";

        ArrayList<String> messageSpecList = new ArrayList<String>();

        try {
            XmlObject thisObject1 = XmlObject.Factory.parse(new File(xmlFileName));
            XmlCursor thisCursor1 = thisObject1.newCursor();
            thisCursor1.selectPath("./messageElement/messagElement1[1]/messageElement24");
            System.out.println(thisCursor1.getSelectionCount());
            System.out.println(thisObject1.toString());
            thisCursor1.clearSelections();
            thisCursor1.dispose();

            System.out.println("\n\n");

            XmlCursor thisCursor2 = thisObject1.newCursor();
            while (thisCursor2.hasNextToken()) {
                TokenType thisToken = thisCursor2.toNextToken();

//                System.out.println(thisToken.toString() +" "+ thisCursor2.getName()+" = " + thisCursor2.getChars());
                if (thisToken.isStart()){
                            thisCursor2.push();
 //                           thisCursor2.toParent();
                            String elementPath = XPathGenerator.generateXPath(thisCursor2, null, new TempMessageNameSpaceContext());
//                            System.out.println(elementPath);
                            thisCursor2.pop();
                            thisCursor2.toNextToken();
                            if (!thisCursor2.getChars().trim().equals("")){
                               elementPath= elementPath.substring(elementPath.lastIndexOf(":")+1).replace("/", ".");
                               if (elementPath.startsWith("."))elementPath = elementPath.substring(1);
                               messageSpecList.add(elementPath);
                               System.out.println(elementPath+" = " + thisCursor2.getChars()+ " - "+isInstanceof("actionLogMsg.log-entry", elementPath));
                            }
                }

            }
            thisCursor2.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("\n\n\n");
        HashMap<String,ArrayList<String>> instanceMap = getInstancesOf("actionLogMsg.log-entry", messageSpecList);
        System.out.println("CANDIDATE LIST");

        Iterator instanceIterator = instanceMap.keySet().iterator();

        while (instanceIterator.hasNext()){
            String thisInstance = (String)instanceIterator.next();
            System.out.println(thisInstance);
            ArrayList<String> instanceElements = instanceMap.get(thisInstance);
            for (String thisElement : instanceElements){
                System.out.println("     "+thisElement);
            }
        }

        System.out.println("\n\n\n");
        System.out.println("Exists in Every Instance?");
        System.out.println("event-id  " + existsInEveryInstance("event-id", instanceMap));
        System.out.println("organization-information-forwarding-restrictions  " + existsInEveryInstance("organization-information-forwarding-restrictions", instanceMap));
        System.out.println("action-time  " + existsInEveryInstance("action-time", instanceMap));
        System.out.println("action-description  " + existsInEveryInstance("action-description", instanceMap));

        System.out.println("\n\n\n");
        System.out.println(errorsFound);
    }
}
