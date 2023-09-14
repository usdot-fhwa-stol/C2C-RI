/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testassertions;

import java.io.StringReader;
import java.io.StringWriter;
import tmddprocedures.*;
import java.sql.ResultSet;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author TransCore ITS
 */
public class TestAssertionsMaker {

    public static void main(String[] args) {
        TestAssertionsMaker defaultProcedureMaker = new TestAssertionsMaker("Trial Procedure");
        defaultProcedureMaker.makeProcedure();

    }

    public TestAssertionsMaker(String procedureName) {
    }

    public void makeProcedure() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase("C:\\Documents and Settings\\Projects\\RI\\InitialDeployment\\TMDD\\TMDDMetrics.accdb");


        ResultSet requirementIDRS = theDatabase.queryReturnRS("Select RequirementID, RequirementTitle from RequirementList");

//        ResultSet rs = theDatabase.queryReturnRS("Select DataConcept, ReqPara, ConceptType from TMDDBasicFrameAndElementDefsQuery where ConceptType = 'data-frame'");
        try {
            while (requirementIDRS.next()) {
                String thisRequirementID = requirementIDRS.getString("RequirementID");
                String thisRequirementText = requirementIDRS.getString("RequirementTitle");
                ResultSet messageDetailRS = theDatabase.queryReturnRS("Select Message, FinalPath, Target from MessageDetailsQuery where RequirementId = '" + thisRequirementID + "'");

                String message = "";
                String finalPath = "";
                String target = "";
                boolean firstRecord = true;

                while (messageDetailRS.next()) {
                    message = messageDetailRS.getString("Message");
                    String thisFinalPath = messageDetailRS.getString("FinalPath");
                    target = messageDetailRS.getString("Target");

                    if (!firstRecord) {
                        finalPath = finalPath.concat(" and " + thisFinalPath);
                    } else {
                        finalPath = finalPath.concat(thisFinalPath);
                    }

                    firstRecord = false;
                }
                if (!firstRecord) {
                    System.out.println(createTestAssertion(thisRequirementID, thisRequirementText, target, finalPath) + "\n\n");
                }
                messageDetailRS.getStatement().close();
//                messageDetailRS.close();
                messageDetailRS = null;

            }
            requirementIDRS.getStatement().close();
            requirementIDRS = null;


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();
    }

    public String createTestAssertion(String requirementID, String requirementText, String target, String predicate) {
        String assertionString = "<testAssertion xmlns=\"http://www.ws-i.org/2002/08/12/ProfileDoc-2.0.xsd\" \n"
                + "xmlns:h=\"http://www.ws-i.org/2002/08/12/ProfileMarkup-2.0.xsd\" \n"
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n"
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n"
                + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" \n"
                + "xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\" \n"
                + "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\" \n"
                + "xmlns:tmdd=\"http://www.tmdd.org/3/messages\" \n"
                + "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" \n"
                + "enable=\"true\" \n"
                + "id=\"" + requirementID + "\"> \n"
                + "<description>" + requirementText + "</description> \n"
                + "<target>" + target + "</target> \n"
                + "<predicate>" + predicate + "</predicate> \n"
                + "<prescription level=\"mandatory\"/> \n"
                + "<errorMessage> \n"
                + "Error encountered verifying requirement " + requirementID
                + "</errorMessage> \n"
                + "<diagnostic>"
                + "Error message from the XML parser"
                + "</diagnostic> \n"
                + "</testAssertion>";

        return assertionString;
    }

    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));

            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    public static String prettyFormat(String input) {
        System.out.println(input);
        return prettyFormat(input, 4);
    }
}
