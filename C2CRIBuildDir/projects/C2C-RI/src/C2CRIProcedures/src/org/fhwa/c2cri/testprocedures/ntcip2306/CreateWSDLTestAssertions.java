/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;

import java.util.ArrayList;

/**
 *
 * @author TransCore ITS
 */
public class CreateWSDLTestAssertions {

    private static String assertionTemplate = "	<testAssertion xmlns=\"http://www.ws-i.org/2002/08/12/ProfileDoc-2.0.xsd\"\n"
            + "				   xmlns:h=\"http://www.ws-i.org/2002/08/12/ProfileMarkup-2.0.xsd\"\n"
            + "				   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "				   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "				   xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "				   xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + "				   xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "				   xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n"
            + "			           enable=\"true\"\n"
            + "				   id=\"<#REQUIREMENTID#>\">\n"
            + "	      <description>\n"
            + "		 #DESCRIPTION# \n"
            + "	      </description>\n"
            + "	      <target>#XSLTARGET#</target>\n"
            + "	      <predicate>#XSLPREDICATE#</predicate>\n"
            + "	      <prescription level=\"mandatory\"/>\n"
            + "	      <errorMessage>\n"
            + "                 #ERRORMESSAGE#\n"
            + "	      </errorMessage>\n"
            + "	      <diagnostic>\n"
            + "			 #DIAGNOSTIC#\n"
            + "	      </diagnostic>\n"
            + "	</testAssertion> ";

    public static void main(String[] args) {
        ArrayList<NTCIP2306Specification> theList = NTCIP2306Specifications.getInstance().getWSDLSpecifications();
        for (NTCIP2306Specification thisSpecification : theList) {
            if (thisSpecification.getMandatory().equals("T")) {
                String description = thisSpecification.getRequirementDescription().replace("<LF>", "\n").replace("<", "'").replace(">", "'");
                String assertion = assertionTemplate.replace("#XSLTARGET#", thisSpecification.getXslTarget()).replace("#XSLPREDICATE#", thisSpecification.getXslPredicate());
                System.out.println(assertion.replace("<#REQUIREMENTID#>", thisSpecification.getRqmtID()).replace("#DESCRIPTION#", description).replace("ERRORMESSAGE#", "FAILURE with: " + description).replace("#DIAGNOSTIC#", "DIAGNOSTIC: Failure with - " + description));
            }
        }

    }
}
