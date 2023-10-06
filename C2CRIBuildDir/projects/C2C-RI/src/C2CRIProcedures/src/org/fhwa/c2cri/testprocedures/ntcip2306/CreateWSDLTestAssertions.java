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

    private static String assertionTemplate = "\t<testAssertion xmlns=\"http://www.ws-i.org/2002/08/12/ProfileDoc-2.0.xsd\"\n"
            + "\t\t\t\t   xmlns:h=\"http://www.ws-i.org/2002/08/12/ProfileMarkup-2.0.xsd\"\n"
            + "\t\t\t\t   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "\t\t\t\t   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "\t\t\t\t   xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "\t\t\t\t   xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + "\t\t\t\t   xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "\t\t\t\t   xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n"
            + "\t\t\t           enable=\"true\"\n"
            + "\t\t\t\t   id=\"<#REQUIREMENTID#>\">\n"
            + "\t      <description>\n"
            + "\t\t #DESCRIPTION# \n"
            + "\t      </description>\n"
            + "\t      <target>#XSLTARGET#</target>\n"
            + "\t      <predicate>#XSLPREDICATE#</predicate>\n"
            + "\t      <prescription level=\"mandatory\"/>\n"
            + "\t      <errorMessage>\n"
            + "                 #ERRORMESSAGE#\n"
            + "\t      </errorMessage>\n"
            + "\t      <diagnostic>\n"
            + "\t\t\t #DIAGNOSTIC#\n"
            + "\t      </diagnostic>\n"
            + "\t</testAssertion> ";

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
