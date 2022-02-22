/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;

import java.sql.ResultSet;
import java.util.ArrayList;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class NTCIP2306Specifications {

    private ArrayList<NTCIP2306Specification> SpecificationsList = new ArrayList<NTCIP2306Specification>();
    private static NTCIP2306Specifications ntcip2306_nrtm_RTM_Spec;

    public static NTCIP2306Specifications getInstance() {
        if (ntcip2306_nrtm_RTM_Spec == null) {
            ntcip2306_nrtm_RTM_Spec = new NTCIP2306Specifications();
        }
        return ntcip2306_nrtm_RTM_Spec;
    }

    private NTCIP2306Specifications() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet wSDLStepDescriptionRS = theDatabase.queryReturnRS("SELECT * FROM NTCIP2306ProcedureDetailsUpdatedTable "
                + "WHERE (Mandatory='T')");
//                "WHERE ((Mandatory='T') AND(InStr([PrimaryTarget],'WSDL')>0))");

        try {
            while (wSDLStepDescriptionRS.next()) {

                String rqmtID = wSDLStepDescriptionRS.getString("RqmtID");
                String requirementDescription = wSDLStepDescriptionRS.getString("requirementDescription");
                String section = wSDLStepDescriptionRS.getString("Section");
                String prlTraceID = wSDLStepDescriptionRS.getString("2306PRLTraceID");
                String profile = wSDLStepDescriptionRS.getString("Profile");
                String mandatory = wSDLStepDescriptionRS.getString("Mandatory");
                String primaryTarget = wSDLStepDescriptionRS.getString("PrimaryTarget");
                String verificationTime = wSDLStepDescriptionRS.getString("VerificationTime");
                String verificationApproach = wSDLStepDescriptionRS.getString("VerificationApproach");
                String preconditionAssertion = wSDLStepDescriptionRS.getString("PreconditionAssertion");
                String testSteps = wSDLStepDescriptionRS.getString("TestSteps");
                String subRequirements = wSDLStepDescriptionRS.getString("SubRequirements");
                String scriptText = wSDLStepDescriptionRS.getString("ScriptText");
                String xPathText = wSDLStepDescriptionRS.getString("XPathText");
                String xslTarget = wSDLStepDescriptionRS.getString("XSLTarget");
                String xslPredicate = wSDLStepDescriptionRS.getString("XSLPredicate");

                NTCIP2306Specification thisWSDLStepDescription = new NTCIP2306Specification();
                thisWSDLStepDescription.setRqmtID(rqmtID);
                thisWSDLStepDescription.setRequirementDescription(requirementDescription);
                thisWSDLStepDescription.setSection(section);
                thisWSDLStepDescription.setpRLTraceID(prlTraceID);
                thisWSDLStepDescription.setProfile(profile);
                thisWSDLStepDescription.setMandatory(mandatory);
                thisWSDLStepDescription.setPrimaryTarget(primaryTarget);
                thisWSDLStepDescription.setVerificationTime(verificationTime);
                thisWSDLStepDescription.setVerificationApproach(verificationApproach);
                thisWSDLStepDescription.setPreconditionAssertion(preconditionAssertion);
                thisWSDLStepDescription.setTestSteps(testSteps);
                thisWSDLStepDescription.setSubRequirments(subRequirements);
                thisWSDLStepDescription.setScriptText(scriptText);
                thisWSDLStepDescription.setxPathText(xPathText);
                thisWSDLStepDescription.setXslTarget(xslTarget);
                thisWSDLStepDescription.setXslPredicate(xslPredicate);
                SpecificationsList.add(thisWSDLStepDescription);
            }
            wSDLStepDescriptionRS.close();
            wSDLStepDescriptionRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("Number of WSDL Step Descriptions Added = " + SpecificationsList.size());

    }

    public ArrayList<NTCIP2306Specification> getProfileSpecifications(String profile, String target) {
        ArrayList<NTCIP2306Specification> returnList = new ArrayList<NTCIP2306Specification>();

        for (NTCIP2306Specification thisStepDescription : SpecificationsList) {
            if ((thisStepDescription.getProfile() != null) && thisStepDescription.getProfile().equals(profile)) {
                if ((thisStepDescription.getPrimaryTarget() != null) && thisStepDescription.getPrimaryTarget().contains(target)) {
                    returnList.add(thisStepDescription);
                }
            }
        }


        return returnList;
    }

    public ArrayList<String> getWSDLRequirements() {
        ArrayList<String> returnList = new ArrayList<String>();

        //There's only one procedure for WSDL so send back all requirements
            for (NTCIP2306Specification thisStepDescription : SpecificationsList) {
                if ((thisStepDescription.getPrimaryTarget() != null) && thisStepDescription.getPrimaryTarget().contains(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET)) {
                    returnList.add(thisStepDescription.getRqmtID());
                }
            }
        return returnList;
    }

    public ArrayList<NTCIP2306Specification> getWSDLSpecifications() {
        ArrayList<NTCIP2306Specification> returnList = new ArrayList<NTCIP2306Specification>();

        //There's only one procedure for WSDL so send back all requirements
            for (NTCIP2306Specification thisSpecification : SpecificationsList) {
                if ((thisSpecification.getPrimaryTarget() != null) && thisSpecification.getPrimaryTarget().contains(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET)) {
                    returnList.add(thisSpecification);
                }
            }
        return returnList;
    }


    public ArrayList<String> getRequirements(String target, String profile, String centerMode) {
        ArrayList<String> returnList = new ArrayList<String>();

        //There's only one procedure for WSDL so send back all requirements
        if (target.contains(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET)) {
            for (NTCIP2306Specification thisStepDescription : SpecificationsList) {
                if ((thisStepDescription.getPrimaryTarget() != null) && thisStepDescription.getPrimaryTarget().contains(target)) {
                    returnList.add(thisStepDescription.getRqmtID());
                }
            }
        }
        return returnList;
    }

    public static void main(String[] args) {
        // Test Out this Class
        ArrayList<NTCIP2306Specification> responseList;

        NTCIP2306Specifications firstOne = new NTCIP2306Specifications();
        System.out.println("Sent General, WSDL");
        responseList = firstOne.getProfileSpecifications("General", "WSDL");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent General, Message");
        responseList = firstOne.getProfileSpecifications("General", "Message");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent SOAP,WSDL");
        responseList = firstOne.getProfileSpecifications("SOAP", "WSDL");
        printResult(responseList);
        responseList.clear();

    }

    public static void printResult(ArrayList<NTCIP2306Specification> reportList) {
        if (reportList.size() == 0) {
            System.out.println("Nothing returned!!");
        } else {
            for (NTCIP2306Specification thisWSDLStep : reportList) {
                System.out.println("Returned " + thisWSDLStep.getRqmtID());
            }
        }

    }
}
