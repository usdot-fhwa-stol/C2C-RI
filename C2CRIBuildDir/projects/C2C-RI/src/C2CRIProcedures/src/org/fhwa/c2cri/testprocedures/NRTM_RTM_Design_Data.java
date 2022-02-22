/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import tmddv3verification.DataConcept;
import tmddv3verification.DesignDetail;
import tmddv3verification.DesignElement;
import tmddv3verification.Need;
import tmddv3verification.NeedHandler;
import tmddv3verification.Predicate;
import tmddv3verification.Requirement;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class NRTM_RTM_Design_Data {

    private DesignDetail tmddDesign;
    private List<Need> needsList;
    private static NRTM_RTM_Design_Data nrtm_RTM_Design_Data_Store;

    public static NRTM_RTM_Design_Data getInstance() {
        if (nrtm_RTM_Design_Data_Store == null) {
            nrtm_RTM_Design_Data_Store = new NRTM_RTM_Design_Data();
        }
        return nrtm_RTM_Design_Data_Store;
    }

    public List<Need> getNeedsList() {
        return needsList;
    }

    public DesignDetail getTmddDesign() {
        return tmddDesign;
    }

    public String getNeedID(String needNumber) {
        String needID = "Not Defined for Need Number " + needNumber;
        Integer number = Integer.parseInt(needNumber);
        if (number <= needsList.size()) {
            needID = needsList.get(number - 1).getUnID();
        }

        return needID;
    }

    public String getNeedText(String needNumber) {
        String needID = "Not Defined for Need Number " + needNumber;
        Integer number = Integer.parseInt(needNumber);
        if (number <= needsList.size()) {
            needID = needsList.get(number - 1).getUserNeed();
        }
        return needID;

    }

    private NRTM_RTM_Design_Data() {
        String wsdlFileName = "file:/C:/TMDDv303c/TMDDv303crPS/tmdd.wsdl";
//        String wsdlFileName = "file:\\c:\\inout\\tmddschemasv9\\tmdd.wsdl";
        tmddDesign = new DesignDetail(wsdlFileName);

        needsList = new ArrayList<Need>();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


        ResultSet needIDRS = theDatabase.queryReturnRS("SELECT DISTINCT NeedsList.NeedNumber, NRTM.UNID, NRTM.UserNeed, NRTM.UNSelected "
                + "FROM NRTM INNER JOIN NeedsList ON NRTM.UNID = NeedsList.UNID ORDER BY NeedsList.NeedNumber");
        ArrayList<NeedClass> needs = new ArrayList<>();
        try {
            while (needIDRS.next()) {
                NeedClass thisNeed = new NeedClass();
                thisNeed.setNeedNumber(Integer.parseInt(needIDRS.getString("NeedNumber")));
                thisNeed.setUnID(needIDRS.getString("UNID"));
                thisNeed.setUserNeed(needIDRS.getString("UserNeed"));
                thisNeed.setSelected(needIDRS.getString("UNSelected"));
                needs.add(thisNeed);
            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }

        try {
            for (NeedClass dbNeed : needs) {
                Integer needNumber = dbNeed.getNeedNumber();
                String unID = dbNeed.getUnID();
                String userNeed = dbNeed.getUserNeed();
                String selected = dbNeed.getSelected();

                Need thisNeed = new Need();
                thisNeed.setNeedNumber(needNumber);
                thisNeed.setUnID(unID);
                thisNeed.setUserNeed(userNeed);
                thisNeed.setUserNeedSelected(selected);

                ResultSet needRS = theDatabase.queryReturnRS("Select * from NRTM where UNID ='" + unID + "' order by ID");
                ArrayList<RequirementClass> requirements = new ArrayList<>();

                int requirementIterations = 0;
                try {
                    while (needRS.next()) {
                        requirementIterations++;
                        RequirementClass dbRequirement = new RequirementClass();
                        String reqID = "";
                        try {
                            dbRequirement.setRequirementID(needRS.getString("RequirementID"));
                            reqID = dbRequirement.getRequirementID();
                            dbRequirement.setRequirement(needRS.getString("Requirement"));
                            dbRequirement.setConformance(needRS.getString("Conformance"));
                            dbRequirement.setSupport(needRS.getString("Support"));
                            dbRequirement.setRequirementType(needRS.getString("RequirementType"));
                            dbRequirement.setOtherRequirement(needRS.getString("OtherRequirements"));
                        } catch (Exception ex) {
                            System.out.println("Error with UN ID " + unID + " and ReqID " + reqID + " @row " + needRS.getRow());
                            ex.printStackTrace();

                        }
                        if (unID.equals("2.3.3.2")) {
                            System.out.println("**UNID 2.3.3.2 with RequirementID " + dbRequirement.getRequirementID() + " Type=" + dbRequirement.getRequirementType());
                        }
                        if (!(reqID.isEmpty())) {
                            requirements.add(dbRequirement);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Error with UN ID " + unID);
                    ex.printStackTrace();
                } finally {
                    needRS.close();
                    needRS = null;
                }
                System.out.println("Total Requirement Iterations for Need " + unID + " = " + requirementIterations);

                Integer itemNumber = 1;

                try {
                    for (RequirementClass dbRequirement : requirements) {
                        String requirementID = dbRequirement.getRequirementID();
                        String requirement = dbRequirement.getRequirement();
                        String conformance = dbRequirement.getConformance();
                        String support = dbRequirement.getSupport();
                        String otherRequirement = dbRequirement.getOtherRequirement();
                        String requirementType = dbRequirement.getRequirementType();
                        System.out.println(unID + " (" + needNumber + ") --" + userNeed);


                        Requirement thisRequirement = new Requirement();
                        thisRequirement.setIndex(itemNumber);
                        thisRequirement.setRequirementID(requirementID);
                        thisRequirement.setRequirement(requirement);
                        thisRequirement.setConformance(conformance);
                        thisRequirement.setOtherRequirement(otherRequirement);
                        thisRequirement.setRequirementType(requirementType);
                        thisNeed.getRequirementList().add(thisRequirement);
                        itemNumber = itemNumber + 1;

                        ResultSet designRS = null;
                        try {
                            designRS = theDatabase.queryReturnRS("Select Dialog, StrippedConceptName, DCType, StandardsClause, [Data Concept Instance Name] from RTMforTMDDVerificationQuery where RequirementID ='" + requirementID + "' order by ID asc");
                            if (designRS != null) {
                                Integer designItemNumber = 1;
                                while (designRS.next()) {
//                                String designRequirementID = designRS.getString("RequirementID");
//                                String designRequirement = designRS.getString("Requirement");
                                    String dialog = designRS.getString("Dialog");
                                    String dataConceptName = designRS.getString("StrippedConceptName");
                                    String dcType = designRS.getString("DCType");
                                    String standardsClause = designRS.getString("StandardsClause");
                                    String instanceName = designRS.getString("Data Concept Instance Name");
                                    DataConcept thisConcept = new DataConcept();
                                    thisConcept.setDataConceptName(dataConceptName);
                                    thisConcept.setDataConceptType(dcType);
                                    thisConcept.setStandardsClause(standardsClause);
                                    thisConcept.setIndex(designItemNumber);
                                    thisConcept.setInstanceName(instanceName);

                                    thisRequirement.getDataConceptList().add(thisConcept);

                                    designItemNumber = designItemNumber + 1;

                                }
                                System.out.println("Number of DataConcepts added to tbis requirement = " + thisRequirement.getDataConceptList().size());
                                designRS.getStatement().close();
//                                designRS.close();
                                //       designRS.getStatement().close();
                                designRS = null;
//                            designRS.close();
//                            designRS = null;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                        }
                    }
                    System.out.println("Number of Requirements added to this need = " + thisNeed.getRequirementList().size());
                    needsList.add(thisNeed);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {

                }


            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }

        // Now Do the Predicates List
        ResultSet predicateRS = theDatabase.queryReturnRS("Select * from Predicates");

        try {
            while (predicateRS.next()) {
                String predicate = predicateRS.getString("Predicate");
                String section = predicateRS.getString("Section");

                System.out.println(predicate + "--" + section);

                Predicate thisPredicate = new Predicate();
                thisPredicate.setPredicateName(predicate);
                thisPredicate.setSection(section);


                ResultSet designRS = theDatabase.queryReturnRS("Select * from RTMforTMDDVerificationQuery where RequirementID ='" + section + "' order by ID");
                Integer designItemNumber = 1;

                try {
                    while (designRS.next()) {

                        String dialog = designRS.getString("Dialog");
                        String dataConceptName = designRS.getString("StrippedConceptName");
                        String dcType = designRS.getString("DCType");
                        String standardsClause = designRS.getString("StandardsClause");

                        DataConcept thisConcept = new DataConcept();
                        thisConcept.setDataConceptName(dataConceptName);
                        thisConcept.setDataConceptType(dcType);
                        thisConcept.setStandardsClause(standardsClause);
                        thisConcept.setIndex(designItemNumber);

                        thisPredicate.getDataConceptList().add(thisConcept);

                        designItemNumber = designItemNumber + 1;

                    }
                    if (thisPredicate.getDataConceptList() != null) {
                        System.out.println("Number of DataConcepts added to tbis predicate = " + thisPredicate.getDataConceptList().size());
                    }

                    designRS.close();
                    designRS = null;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                predicatesList.add(thisPredicate);
            }
            predicateRS.close();
            predicateRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("Number of Needs Added = " + needsList.size());

    }

    public static ArrayList<String> makeTestProcedureRequirementsList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));

        // We are always testing the requirement related to the dialog in each test case.
        List<String> theDialogs = theHandler.getNeedRelatedDialogs();
        for (String thisDialog : theDialogs) {
            if (thisDialog.equals(dialog)) {
                List<Requirement> theReqList = theHandler.getDialogSpecificRequirements(dialog);
                for (Requirement theRequirement : theReqList) {
                    if (!theRequirements.contains(theRequirement.getRequirementID())) {
                        theRequirements.add(theRequirement.getRequirementID());
                    }
                }
            }
        }

        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            String messageName = "";
            if (sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else {
                    messageName = theElement.getSubElements().get(0).getElementName();
                }


            } else // mode is OC
            {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else {
                    messageName = theElement.getSubElements().get(1).getElementName();
                }
            }

            if (!messageName.equals("")) {
                try {
                    int reqCount = theHandler.getMessageConceptElementCount(messageName);
                    for (int ii = 0; ii < reqCount; ii++) {
                        String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                        if (!theRequirements.contains(reqID)) {
                            theRequirements.add(reqID);
                        }
                    }

            if ((sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE) && !dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX))||
                (sutCenterMode.equals(TMDDParameters.TMDD_OC_Mode))){
                    messageName = "errorReportMsg";
                    reqCount = theHandler.getMessageConceptElementCount(messageName);
                    for (int ii = 0; ii < reqCount; ii++) {
                        String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                        if (!theRequirements.contains(reqID)) {
                            theRequirements.add(reqID);
                        }
                    }
            }
                    
                } catch (Exception ex) {
                    System.err.println("For need: " + theHandler.getNeedID() + "(" + needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");

                }
            }

        }
        return theRequirements;

    }

    public static ArrayList<String> makeTestCaseRequirementsList(String needID, String sutCenterMode, String dialog, boolean valid) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));

        // We are always testing the requirement related to the dialog in each test case.
        List<String> theDialogs = theHandler.getNeedRelatedDialogs();
        for (String thisDialog : theDialogs) {
            if (thisDialog.equals(dialog)) {
                List<Requirement> theReqList = theHandler.getDialogSpecificRequirements(dialog);
                for (Requirement theRequirement : theReqList) {
                    if (!theRequirements.contains(theRequirement.getRequirementID())) {
                        theRequirements.add(theRequirement.getRequirementID());
                    }
                }
                if (thisDialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    try {
                        String pubDialog = TMDDSubPubMapping.getInstance().getPublicationDialog(thisDialog, needNumber + 1);
                        theReqList = theHandler.getDialogSpecificRequirements(pubDialog);
                        for (Requirement theRequirement : theReqList) {
                            if (!theRequirements.contains(theRequirement.getRequirementID())) {
                                theRequirements.add(theRequirement.getRequirementID());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }

        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            String messageName = "";
            if (sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        if (valid) {
                            messageName = theElement.getSubElements().get(2).getElementName();
                        } else {
                            messageName = theElement.getSubElements().get(3).getElementName();   // Try and Get the Error Message
                        }
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (valid) {
                        messageName = theElement.getSubElements().get(1).getElementName();
                    }
                } else {
                    if (valid) {
                        messageName = theElement.getSubElements().get(0).getElementName();
                    }
                }


            } else // mode is OC
            {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (valid) {
                        messageName = theElement.getSubElements().get(1).getElementName();
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        if (valid) {
                            messageName = theElement.getSubElements().get(2).getElementName();
                        } else {
                            messageName = theElement.getSubElements().get(3).getElementName();   // Try and Get the Error Message

                        }
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else {
                    if (valid) {
                        messageName = theElement.getSubElements().get(1).getElementName();
                    } else {
                        messageName = theElement.getSubElements().get(2).getElementName();   // Try and Get the Error Message
                    }
                }
            }

            if (!messageName.equals("")) {
                try {
                    int reqCount = theHandler.getMessageConceptElementCount(messageName);
                    for (int ii = 0; ii < reqCount; ii++) {
                        String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                        if (!theRequirements.contains(reqID)) {
                            theRequirements.add(reqID);
                        }
                    }

                } catch (Exception ex) {
                    System.err.println("For need: " + theHandler.getNeedID() + "(" + needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");

                }
            }

        }
        return theRequirements;

    }

    public static ArrayList<String> makeMessageOptionalRequirementsList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));


        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            String messageName = "";
            if (sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else {
                    messageName = theElement.getSubElements().get(0).getElementName();
                }


            } else // mode is OC
            {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else {
                    messageName = theElement.getSubElements().get(1).getElementName();
                }
            }

            if (!messageName.equals("")) {
                try {
                    int reqCount = theHandler.getMessageConceptElementCount(messageName);
                    for (int ii = 0; ii < reqCount; ii++) {
                        if (!theHandler.getMessageConceptElementConformance(messageName, ii).equals("M")) {
                            String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                            if (!theRequirements.contains(reqID)) {
                                theRequirements.add(reqID);
                            }
                        }
                    }

                } catch (Exception ex) {
                    System.err.println("For need: " + theHandler.getNeedID() + "(" + needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");

                }
            }

        }
        return theRequirements;
    }

    public static ArrayList<String> makeErrorMessageOptionalRequirementsList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));
        String messageName = "errorReportMsg";
        try {
            int reqCount = theHandler.getMessageConceptElementCount(messageName);
            for (int ii = 0; ii < reqCount; ii++) {
                if (!theHandler.getMessageConceptElementConformance(messageName, ii).equals("M")) {
                    String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                    if (!theRequirements.contains(reqID)) {
                        theRequirements.add(reqID);
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("For need: " + theHandler.getNeedID() + "(" + needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");

        }

        return theRequirements;
    }

    public ArrayList<String> makeMessageMandatoryRequirementsList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));


        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            String messageName = "";
            if (sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else {
                    messageName = theElement.getSubElements().get(0).getElementName();
                }


            } else // mode is OC
            {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else {
                    messageName = theElement.getSubElements().get(1).getElementName();
                }
            }

            if (!messageName.equals("")) {
                try {
                    int reqCount = theHandler.getMessageConceptElementCount(messageName);
                    for (int ii = 0; ii < reqCount; ii++) {
                        if (theHandler.getMessageConceptElementConformance(messageName, ii).equals("M")) {
                            String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                            if (!theRequirements.contains(reqID)) {
                                theRequirements.add(reqID);
                            }
                        }
                    }

                } catch (Exception ex) {
                    System.err.println("For need: " + theHandler.getNeedID() + "(" + needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");

                }
            }

        }
        return theRequirements;
    }

    public ArrayList<String> makeErrorMessageMandatoryRequirementsList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));
        String messageName = "errorReportMsg";

        try {
            int reqCount = theHandler.getMessageConceptElementCount(messageName);
            for (int ii = 0; ii < reqCount; ii++) {
                if (theHandler.getMessageConceptElementConformance(messageName, ii).equals("M")) {
                    String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                    if (!theRequirements.contains(reqID)) {
                        theRequirements.add(reqID);
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("For need: " + theHandler.getNeedID() + "(" + needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");

        }


        return theRequirements;
    }

    public static ArrayList<String> makeDialogRequirementsList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));

        List<Requirement> dialogList = theHandler.getDialogSpecificRequirements(dialog);
        for (Requirement thisRequirement : dialogList) {
            if (!theRequirements.contains(thisRequirement.getRequirementID())) {
                theRequirements.add(thisRequirement.getRequirementID());
            }
        }


        return theRequirements;
    }

    public static String getPartnerDialog(String needID, String sutCenterMode, String dialog) {

        Integer needNumber = Integer.parseInt(needID);
        try {
            if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                return TMDDSubPubMapping.getInstance().getPublicationDialog(dialog, needNumber);
            } else {
                return TMDDSubPubMapping.getInstance().getSubscriptionDialog(dialog, needNumber);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));

            List<String> dialogList = theHandler.getNeedRelatedDialogs();
            for (String thisDialog : dialogList) {
                if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (thisDialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                        return thisDialog;
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (thisDialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                        return thisDialog;
                    }
                }
            }
        }
        return "";
    }

    public static ArrayList<String> getRelatedMessagesList(String needID, String sutCenterMode, String dialog) {
        ArrayList<String> theMessages = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));


        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            if (sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        theMessages.add(theElement.getSubElements().get(1).getElementName());  // C2C Header
                        theMessages.add(theElement.getSubElements().get(2).getElementName());  // Message
                        theMessages.add(theElement.getSubElements().get(3).getElementName());  // Message
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    theMessages.add(theElement.getSubElements().get(0).getElementName());  // C2C Header
                    theMessages.add(theElement.getSubElements().get(1).getElementName());  // Message
                } else {
                    theMessages.add(theElement.getSubElements().get(0).getElementName());  // Message
                }


            } else // mode is OC
            {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    theMessages.add(theElement.getSubElements().get(0).getElementName());  // C2C Header
                    theMessages.add(theElement.getSubElements().get(1).getElementName());  // Message
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        theMessages.add(theElement.getSubElements().get(1).getElementName());  // Message
                        theMessages.add(theElement.getSubElements().get(2).getElementName());  // Message
                        theMessages.add(theElement.getSubElements().get(3).getElementName());  // Message
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else {
                    theMessages.add(theElement.getSubElements().get(1).getElementName());  // Message
                    theMessages.add(theElement.getSubElements().get(2).getElementName());  // Message
                }
            }

        }
        return theMessages;

    }

    public static ArrayList<String> makeMessageOptionalElementsList(String needNumber, String messageName, String requirementID) {
        Integer needNumberValue = Integer.parseInt(needNumber) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumberValue));
        ArrayList<String> messageElementsList = new ArrayList<String>();
        try {
            int reqCount = theHandler.getMessageConceptElementCount(messageName);
            for (int ii = 0; ii < reqCount; ii++) {
                if ((!theHandler.getMessageConceptElementConformance(messageName, ii).equals("M"))
                        && (theHandler.getMessageConceptElementRequirementID(messageName, ii).equals(requirementID))) {
                    if (!messageElementsList.contains(theHandler.getMessageDataConcept(messageName, ii).getInstanceName())) {
                        messageElementsList.add(theHandler.getMessageDataConcept(messageName, ii).getInstanceName());
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("For need: " + theHandler.getNeedID() + "(" + needNumber + ") -- there does not appear to be a " + messageName + " element.");

        }

        return messageElementsList;
    }

    public static ArrayList<String> makeMessageMandatoryElementsList(String needNumber, String messageName, String requirementID) {
        Integer needNumberValue = Integer.parseInt(needNumber) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumberValue));
        ArrayList<String> messageElementsList = new ArrayList<String>();
        try {
            int reqCount = theHandler.getMessageConceptElementCount(messageName);
            for (int ii = 0; ii < reqCount; ii++) {
                if ((theHandler.getMessageConceptElementConformance(messageName, ii).equals("M"))
                        && (theHandler.getMessageConceptElementRequirementID(messageName, ii).equals(requirementID))) {
                    if (!messageElementsList.contains(theHandler.getMessageDataConcept(messageName, ii).getInstanceName())) {
                        messageElementsList.add(theHandler.getMessageDataConcept(messageName, ii).getInstanceName());
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("For need: " + theHandler.getNeedID() + "(" + needNumber + ") -- there does not appear to be a " + messageName + " element.");

        }

        return messageElementsList;
    }

    public static String getDialogPrecondition(String needID, String sutCenterMode, String dialog) {
        String precondition = "";
        Integer needNumber = Integer.parseInt(needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));

        // We are always testing the requirement related to the dialog in each test case.
        List<String> theDialogs = theHandler.getNeedRelatedDialogs();
        for (String thisDialog : theDialogs) {
            if (thisDialog.equals(dialog)) {
                List<Requirement> theReqList = theHandler.getDialogSpecificRequirements(dialog);
                for (Requirement theRequirement : theReqList) {
                    if (theRequirement.getConformance().contains(":")) {
                        String conformance = theRequirement.getConformance();
                        precondition = conformance.substring(0, conformance.indexOf(":")).trim();
                        return precondition;
                    }
                }
            }
        }
        return precondition;
    }

    public static String getOtherReqVariableName(String needNumber, String requirement) {
        String variableName = "";
        Integer intneedNumber = Integer.parseInt(needNumber) - 1;

        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(intneedNumber));

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        String[] requirementList = requirement.split(",");
        for (String thisRequirement : requirementList){
            ResultSet needIDRS = theDatabase.queryReturnRS("Select * from RI_NRTM_Query where [Requirement ID] ='" + thisRequirement.trim() + "' and [UN ID] ='" + theHandler.getNeedID() + "' order by ID");
        try {
            while (needIDRS.next()) {
                String tmpVariableName = needIDRS.getString("OtherReqParameter");
                String otherRequirements = needIDRS.getString("Other Requirements");
                // The TMDD NRTM does some wierd things sometimes with subscription dialogs -- Like two consecutive
                // requirements related to the same subscription or request dialog.  One might have an other parameter defined
                // while the other does not.  This code attempts to make sure whichever has the parameter defined is selected.
                if (variableName.isEmpty()&&(!(otherRequirements==null) && !otherRequirements.isEmpty()))variableName = tmpVariableName;
            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        }
        theDatabase.disconnectFromDatabase();

        return variableName;
    }

    public static ArrayList<DialogRecord> getOptionalErrorRequirementVariableNames(String needNumber) {
        ArrayList<DialogRecord> variableName = new ArrayList();
        Integer intneedNumber = Integer.parseInt(needNumber) - 1;

        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(intneedNumber));

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet needIDRS = theDatabase.queryReturnRS("Select * from RI_NRTM_Query where [UN ID] ='" + theHandler.getNeedID() + "' and RequirementType='Error Report Message' and Conformance='O' order by ID");
        try {
            while (needIDRS.next()) {
                DialogRecord newRecord = new DialogRecord();
                newRecord.setRequirementParameter(needIDRS.getString("ReqFlag"));
                newRecord.setOtherRequirementParameter(needIDRS.getString("OtherReqParameter"));
                newRecord.setMandatory(needIDRS.getString("Conformance").equals("M")?true:false);
                newRecord.setRequirement(needIDRS.getString("Requirement"));
                newRecord.setRequirementID(needIDRS.getString("Requirement ID"));
                variableName.add(newRecord);
            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        theDatabase.disconnectFromDatabase();

        return variableName;
    }    
    
    public static ArrayList<DialogRecord> getDialogRequirementVariableNames(String needNumber) {
        ArrayList<DialogRecord> variableName = new ArrayList();
        Integer intneedNumber = Integer.parseInt(needNumber) - 1;

        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(intneedNumber));

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet needIDRS = theDatabase.queryReturnRS("Select * from RI_NRTM_Query where [UN ID] ='" + theHandler.getNeedID() + "' and RequirementType='Dialogs' order by ID");
        try {
            while (needIDRS.next()) {
                DialogRecord newRecord = new DialogRecord();
                newRecord.setRequirementParameter(needIDRS.getString("ReqFlag"));
                newRecord.setOtherRequirementParameter(needIDRS.getString("OtherReqParameter"));
                newRecord.setMandatory(needIDRS.getString("Conformance").equals("M")?true:false);
                newRecord.setRequirement(needIDRS.getString("Requirement"));
                newRecord.setRequirementID(needIDRS.getString("Requirement ID"));
                variableName.add(newRecord);
            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        theDatabase.disconnectFromDatabase();

        return variableName;
    }
    
    public static class DialogRecord {
        private String otherRequirementParameter;
        private String requirementParameter;
        private boolean mandatory;
        private String requirement;
        private String requirementID;

        public String getOtherRequirementParameter() {
            return otherRequirementParameter;
        }

        public void setOtherRequirementParameter(String otherRequirementParameter) {
            this.otherRequirementParameter = otherRequirementParameter;
        }

        public String getRequirementParameter() {
            return requirementParameter;
        }

        public void setRequirementParameter(String requirementParameter) {
            this.requirementParameter = requirementParameter;
        }

        public boolean isMandatory() {
            return mandatory;
        }

        public void setMandatory(boolean mandatory) {
            this.mandatory = mandatory;
        }

        public String getRequirement() {
            return requirement;
        }

        public void setRequirement(String requirement) {
            this.requirement = requirement;
        }

        public String getRequirementID() {
            return requirementID;
        }

        public void setRequirementID(String requirementID) {
            this.requirementID = requirementID;
        }
        
        
    }
    
    class NeedClass {

        private Integer needNumber;
        private String unID;
        private String userNeed;
        private String selected;

        public Integer getNeedNumber() {
            return needNumber;
        }

        public void setNeedNumber(Integer needNumber) {
            this.needNumber = needNumber;
        }

        public String getUnID() {
            return unID;
        }

        public void setUnID(String unID) {
            this.unID = unID;
        }

        public String getUserNeed() {
            return userNeed;
        }

        public void setUserNeed(String userNeed) {
            this.userNeed = userNeed;
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }
    }

    class RequirementClass {

        private String requirementID;
        private String requirement;
        private String conformance;
        private String support;
        private String otherRequirement;
        private String requirementType = "";

        public String getRequirementID() {
            return requirementID;
        }

        public void setRequirementID(String requirementID) {
            this.requirementID = requirementID;
        }

        public String getRequirement() {
            return requirement;
        }

        public void setRequirement(String requirement) {
            this.requirement = requirement;
        }

        public String getConformance() {
            return conformance;
        }

        public void setConformance(String conformance) {
            this.conformance = conformance;
        }

        public String getSupport() {
            return support;
        }

        public void setSupport(String support) {
            this.support = support;
        }

        public String getOtherRequirement() {
            return otherRequirement;
        }

        public void setOtherRequirement(String otherRequirement) {
            this.otherRequirement = otherRequirement;
        }

        public String getRequirementType() {
            return requirementType;
        }

        public void setRequirementType(String requirementType) {
            this.requirementType = requirementType;
        }
    }
}
