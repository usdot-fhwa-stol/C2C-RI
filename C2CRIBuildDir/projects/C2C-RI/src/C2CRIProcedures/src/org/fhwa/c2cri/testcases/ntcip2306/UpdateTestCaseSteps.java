/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases.ntcip2306;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 *
 * @author TransCore ITS, LLC
 */
public class UpdateTestCaseSteps {

    private static HashMap<String, String> replaceMap = new HashMap();

    public static void main(String[] args) {


        replaceMap.put("Step 1 VERIFY that the specified WSDL file exists.", "Step 9 VERIFY that the specified WSDL file exists.");
        replaceMap.put("Step 2 VALIDATE the WSDL file using the W3C WSDL 1.1 Schema", "Step 10 VALIDATE the WSDL file using the W3C WSDL 1.1 Schema");
        replaceMap.put("Step 3 VERIFY that the WSDL file consists of 5 child sections: types/schema, message, portType, binding, service.", "Step 11 VERIFY that the WSDL file consists of 5 child sections: types/schema, message, portType, binding, service.");
        replaceMap.put("Step 4 VERIFY that the definitions tag includes a name attribute.", "Step 12 VERIFY that the definitions tag includes a name attribute.");
        replaceMap.put("Step 5 VERIFY that the type section begins with the types tag.", "Step 13 VERIFY that the type section begins with the types tag.");
        replaceMap.put("Step 6 VERIFY that the schema tag immediately follows the types tag.", "Step 14 VERIFY that the schema tag immediately follows the types tag.");
        replaceMap.put("Step 7 VERIFY that the import tag follows the types tag and specifies any namespace being imported and the schemaLocation.", "Step 15 VERIFY that the import tag follows the types tag and specifies any namespace being imported and the schemaLocation.");
        replaceMap.put("Step 8 VERIFY that message section includes all top-level messages defined in the XML Schema that apply to the project implementation for Request-Response transmission pattern.", "Step 16.1 VERIFY that the message section includes the top-level messages defined in the XML Schema that apply to the project implementation and use a Request-Response transmission pattern.");
        replaceMap.put("Step 9 VERIFY that each message type begins with [MSG_].", "Step 16.2 VERIFY that each message name is defined by the name of the message type, as defined in the schema, with the prefix [MSG_] at the front part of the name.");
        replaceMap.put("Step 10 VERIFY that the name attribute of each message part is ?message?.", "Step 16.3 VERIFY that the name attribute of each message part is set to [message].");
        replaceMap.put("Step 11 VERIFY that the element attribute of the message part name starts with [tmdd:].", "Step 16.4 VERIFY that the element attribute of the message part name starts with a prefix of the form [tmdd:].");
        replaceMap.put("Step 12 VERIFY that message section contains subscription messages.", "Step 17.1 VERIFY that message section contains all top-level messages defined in the XML Schema that apply to the project implementation and are used as a subscription message.");
        replaceMap.put("Step 13 VERIFY that each message name each message name begins with the prefix [MSG_].  Also, confirm that the remainder of the message exists within the schema.", "Step 17.2 VERIFY that each message name is defined by the name of the message type, as defined in the schema, with the prefix [MSG_] at the front part of the name.");
        replaceMap.put("Step 14 VERIFY that the message has 2 parts.  The name attribute of the first part shall always be [c2cMsgAdmin], and the element attribute is [c2c:c2cMessageSubscription]", "Step 17.3 VERIFY that the message has 2 parts.  The name attribute of the first part shall always be [c2cMsgAdmin], and the element attribute is [c2c:c2cMessageSubscription].");
        replaceMap.put("Step 15 VERIFY that the element attribute of the second message part name starts with [tmdd:].", "Step 17.4 VERIFY that the name attribute of the second message part is set to [message] and the element attribute uses the form [tmdd:] followed by the name of the XML Schema element being imported into the WSDL.");
        replaceMap.put("Step 16 VERIFY that the message section contains subscription messages.", "Step 17.5 VERIFY that the message section specifies all top-level messages defined in the XML Schema that apply to the project implementation and are used as publication messages.");
        replaceMap.put("Step 17 VERIFY that each message name each message name begins with the prefix [MSG_].  Also, confirm that the remainder of the message exists within the schema.", "Step 17.6 VERIFY that each message name begins with the prefix [MSG_].  Also, confirm that the remainder of the message exists within the schema.");
        replaceMap.put("Step 18 VERIFY that the message has 2 parts.  The name attribute of the first part shall always be [c2cMsgAdmin], and the element attribute is [c2c:c2cMessageSubscription]", "Step 17.7 VERIFY that the message has 2 parts.  The name attribute of the first part shall always be [c2cMsgAdmin], and the element attribute is [c2c:c2cMessagePublication].");
        replaceMap.put("Step 19 VERIFY that the element attribute of the second message part name starts with [tmdd:].", "Step 17.8 VERIFY that the name attribute of the second message part is set to [message] and the element uses the form [tmdd:] followed by the name of the XML Schema element being imported into the WSDL.");
        replaceMap.put("Step 20 VERIFY that one (or more) service endpoints of the WSDL file use [https:] .", "Step 21.1 VERIFY that one (or more) service endpoints of the WSDL file use [https:].");
        replaceMap.put("Step 21 If NTCIP2306_N1R1_WSDL_Request_Response_Flag is equal to True CONTINUE; Otherwise skip the following substeps.", "Step 16 If NTCIP2306_N1R1_WSDL_Request_Response_Flag is equal to True CONTINUE; Otherwise skip the following substeps.  Note: If a verification step fails, then test execution will proceed at the next subsequent Post Condition step, if present.  Otherwise, test execution will proceed to the final Exit step.");
        replaceMap.put("Step 21.1 VERIFY that at least one binding tag is followed by a soap:binding tag.", "Step 16.5 VERIFY that at least one binding tag is followed by a soap:binding tag.");
        replaceMap.put("Step 21.2 VERIFY that portType tag (or multiple) exists and contains a list of SOAP ?operation? tags.  There must be at least one operation for each portType element.", "Step 16.6 VERIFY that portType tag (or multiple) exists and contains a list of SOAP operation tags.  There must be at least one operation for each portType element.");
        replaceMap.put("Step 21.3 VERIFY that each operation name begins with the ?OP_? prefix.", "Step 16.7 VERIFY that each operation name begins with the [OP_] prefix.");
        replaceMap.put("Step 21.4 VERIFY that each operation has one input tag followed by one output tag.", "Step 16.8 VERIFY that each operation has one input tag followed by one output tag.");
        replaceMap.put("Step 21.5 VERIFY that the input message references a message defined in the Message section of the WSDL.  The prefix for the message part of the operation must be [tns:].", "Step 16.9 VERIFY that the input message references a message defined in the Message section of the WSDL.  The prefix for the message part of the operation must be [tns:].");
        replaceMap.put("Step 21.6 VERIFY that the style attribute of all soap:binding tags is [document]", "Step 16.10 VERIFY that the style attribute of all soap:binding tags is [document].");
        replaceMap.put("Step 21.7 VERIFY that the transport attribute of all soap:binding tags is [http://schemas.xmloap.org/soap/http]", "Step 16.11 VERIFY that the transport attribute of all soap:binding tags is [http://schemas.xmloap.org/soap/http].");
        replaceMap.put("Step 21.8 VERIFY that each the operation name matches at least one operation specified in the portType section(s) of the WSDL.", "Step 16.12 VERIFY that each the operation name matches at least one operation specified in the portType section(s) of the WSDL.");
        replaceMap.put("Step 21.9 VERIFY that the operation tag following a soap:binding tag  is followed by the soap:operation tag.", "Step 16.13 VERIFY that the operation tag following a soap:binding tag  is followed by the soap:operation tag.");
        replaceMap.put("Step 21.10 VERIFY that the soapAction attribute exists within each soap:operation tag. Confirm that the soapAction attribute is either blank or contains characters.", "Step 16.14 VERIFY that the soapAction attribute exists within each soap:operation tag. Confirm that the soapAction attribute is either written as two double quotes that enclose to single quote characters or as two double quotes that enclose a URL that indicates the message handler for the endpoint.");
        replaceMap.put("Step 21.11 VERIFY that the soap:operation tag is followed by an input tag set.", "Step 16.15 VERIFY that the soap:operation tag is followed by an input tag set.");
        replaceMap.put("Step 21.12 VERIFY that the input tag is followed by a soap:body tag with a use attribute set to [literal].  (**Note this requirement appears in error.  Shouldn?t the XML message appear within the soap:body tag specifically.)", "Step 16.16 VERIFY that the input tag is followed by a soap:body tag with a use attribute set to [literal].");
        replaceMap.put("Step 21.13 VERIFY that the input tag within the soap:operation tag is followed by an output tag set.", "Step 16.17 VERIFY that the input tag within the soap:operation tag is followed by an output tag set.");
        replaceMap.put("Step 21.14 VERIFY that the output tag is followed by a soap:body tag with a use attribute set to [literal].", "Step 16.18 VERIFY that the output tag is followed by a soap:body tag with a use attribute set to [literal].");
        replaceMap.put("Step 21.15 VERIFY that each service tag contains a name attribute.", "Step 16.19 VERIFY that each service tag contains a name attribute.");
        replaceMap.put("Step 21.16 **Error in requirement:  Even if no documentation tag is included, a port tag is still required. VERIFY that the service tag is followed by a port tag.", "Step 16.20 VERIFY that the service tag is followed by a port tag.  If the service tag includes a documentation tag, it must precede the port tag.");
        replaceMap.put("Step 21.17 VERIFY that a soap:addres tag follows a port tag.  There should be at least one present in the WSDL.  Verify that the port tag contains a location attribute.  Does the location attribute specify the service endpoint of the SOAP se", "Step 16.21 VERIFY that a soap:address tag follows a port tag.  There should be at least one present in the WSDL.  Verify that the port tag contains a location attribute.");
        replaceMap.put("Step 21.18 VERIFY that the location attribute of the soap:address tag specifies a valid URL.", "Step 16.22 VERIFY that the location attribute of the soap:address tag specifies a valid URL.");
        replaceMap.put("Step 22.1 VERIFY that the WSDL file contains a specification(s) for a subscriber callback listener.", "Step 17.9 VERIFY that the WSDL file contains a specification(s) for a subscriber callback listener.");
        replaceMap.put("Step 22.2 VERIFY that the subscription and publication operations use the same XML encoding and transmission message patterns.", "Step 17.10 VERIFY that the subscription and publication operations use the same XML encoding and transmission message patterns.");
        replaceMap.put("Step 22.3 VERIFY that the portType tag is followed by a list of operations supported by the subscriber for listening for asynchronous SOAP message delivery.", "Step 17.11 VERIFY that the portType tag is followed by a list of operations supported by the subscriber for listening for asynchronous SOAP message delivery.");
        replaceMap.put("Step 22.4 VERIFY that the list of operations a subscriber listener must support are defined in the WSDL.", "Step 17.12 VERIFY that the list of operations a subscriber listener must support are defined in the WSDL.");
        replaceMap.put("Step 22.5 VERIFY that the operation name input message and output message follow the same rules as those defined for a SOAP Request-Response portType defined in Section 7.1.3.", "Step 17.13 VERIFY that the operation name, input message and output message follow the same rules as those defined for a SOAP Request-Response portType defined in Section 7.1.3. *** Error in Spec:  Should reference 7.1.1 ***");
        replaceMap.put("Step 22.6 VERIFY that each callback message is defined as an input message.", "Step 17.14 VERIFY that each callback message is defined as an input message.");
        replaceMap.put("Step 22.7 VERIFY that the subscriber response is defined in the output message.", "Step 17.15 VERIFY that the subscriber response is defined in the output message.");
        replaceMap.put("Step 22.8 VERIFY that the normative rules from the SOAP Request-Response Binding defined in Section 7.1.3 apply with the exception that the soapAction attribute will be left blank.", "Step 17.16 VERIFY that the normative rules from the SOAP Request-Response Binding defined in Section 7.1.3 apply with the exception that the soapAction attribute will be left blank.");
        replaceMap.put("Step 22.9 VERIFY that the service tag contains a name attribute of the service.", "Step 17.17 VERIFY that the service tag contains a name attribute of the service.");
        replaceMap.put("Step 22.10 VERIFY that the port tag follows the documentation tag, if one is present.  The name attribute of the port tag reflects the name of a SOAP port followed by the binding attribute indicating the binding attribute indicating the name", "Step 17.18 VERIFY that the port tag follows the documentation tag, if one is present.  The name attribute of the port tag reflects the name of a SOAP port followed by the binding attribute indicating the binding attribute indicating the name");
        replaceMap.put("Step 22.11 VERIFY that following the port tag, a soap:address tags is present.", "Step 17.19 VERIFY that following the port tag, a soap:address tags is present.");
        replaceMap.put("Step 23.1 VERIFY that the portType section for XML over HTTP follows the same rules as those in Section 7.1.1.", "Step 18.5 VERIFY that the portType section for XML over HTTP follows the same rules as those in Section 7.1.1.");
        replaceMap.put("Step 23.2 VERIFY that the requirement of the Request Only Binding is applied.", "Step 18.6 VERIFY that the requirement of the Request Only Binding is applied.");
        replaceMap.put("Step 23.3 VERIFY that the content of the input tag contains at least one form parameter, which contains an XML message.", "Step 18.7 VERIFY that the content of the input tag contains at least one form parameter, which contains an XML message.");
        replaceMap.put("Step 23.4 VERIFY that the rules for a SOAP service are applied except for exceptions listed in this section.", "Step 18.8 VERIFY that the rules for a SOAP service are applied except for exceptions listed in this section.");
        replaceMap.put("Step 23.5 VERIFY that the XML HTTP endpoint uses the http:address tag to specify an endpoint rather than the soap:address tag.", "Step 18.9 VERIFY that the XML HTTP endpoint uses the http:address tag to specify an endpoint rather than the soap:address tag.");
        replaceMap.put("Step 24.1 VERIFY that the WSDL to describe the simple retrieval of a file is described as a one-way operation.", "Step 19.5 VERIFY that the WSDL to describe the simple retrieval of a file is described as a one-way operation.");
        replaceMap.put("Step 24.2 VERIFY that the portType tag is followed by a single operation which maps to a specific file.", "Step 19.6 VERIFY that the portType tag is followed by a single operation which maps to a specific file.");
        replaceMap.put("Step 24.3 VERIFY that the operation is defined as a one way operation.", "Step 19.7 VERIFY that the operation is defined as a one way operation.");
        replaceMap.put("Step 24.4 VERIFY that the operation name begins with the prefix [OP_] followed by a name for the operation.", "Step 19.8 VERIFY that the operation name begins with the prefix [OP_] followed by a name for the operation.");
        replaceMap.put("Step 24.5 VERIFY that each operation has one input tag.", "Step 19.9 VERIFY that each operation has one input tag.");
        replaceMap.put("Step 24.6 VERIFY that the binding tag is followed by a http:binding tag.", "Step 19.10 VERIFY that the binding tag is followed by a http:binding tag.");
        replaceMap.put("Step 24.7 VERIFY that at least one section contains a http:binding verb attribute set to [GET].", "Step 19.11 VERIFY that at least one section contains a http:binding verb attribute set to [GET].");
        replaceMap.put("Step 24.8 VERIFY that the operation name matches the operation as specified in the portType section of the WSDL for each operation (file) supported by the center.", "Step 19.12 VERIFY that the operation name matches the operation as specified in the portType section of the WSDL for each operation (file) supported by the center.");
        replaceMap.put("Step 24.9 VERIFY that the operation tag is followed by the http:operation tag.", "Step 19.13 VERIFY that the operation tag is followed by the http:operation tag.");
        replaceMap.put("Step 24.10 VERIFY that the location attribute shall be the name of the file. The name of the file is to be defined by each LFcenter. However, it is recommended that the file name reflect the name of the message or messages LFcontained. Documentation", "Step 19.14 VERIFY that the location attribute shall be the name of the file. The name of the file is to be defined by each center. However, it is recommended that the file name reflect the name of the message or messages contained. Documentation outlining these naming rules should be established and a link provided to it in the meta-data of the documentation section.");
        replaceMap.put("Step 24.11 VERIFY that the http:operation tag is followed by the input ,/input tag set.", "Step 19.15 VERIFY that the http:operation tag is followed by the input ,/input tag set.");
        replaceMap.put("Step 24.12 VERIFY that the input tag is followed by a http:urlEncoded tag.", "Step 19.16 VERIFY that the input tag is followed by a http:urlEncoded tag.");
        replaceMap.put("Step 24.13 VERIFY that the input tag set is followed by an output, /output tag set.", "Step 19.17 VERIFY that the input tag set is followed by an output, /output tag set.");
        replaceMap.put("Step 24.14 VERIFY that the output tag is followed by a mime:content tag.", "Step 19.18 VERIFY that the output tag is followed by a mime:content tag.");
        replaceMap.put("Step 24.15 VERIFY that the valid type attribute of the mime:content tag is any MIME type.", "Step 19.19 VERIFY that the valid type attribute of the mime:content tag is any MIME type.");
        replaceMap.put("Step 24.16 VERIFY that the rules for a SOAP service are applied except for exceptions listed in this section.", "Step 19.20 VERIFY that the rules for a SOAP service are applied except for exceptions listed in this section.");
        replaceMap.put("Step 24.17 VERIFY that the XML HTTP endpoint uses the http:address tag to specify an endpoint rather than the soap:address tag.", "Step 19.21 VERIFY that the XML HTTP endpoint uses the http:address tag to specify an endpoint rather than the soap:address tag.");
        replaceMap.put("Step 25.1 VERIFY that the operation name and output message follow the same rules as those defined for a SOAP Service portType.", "Step 20.5 VERIFY that the operation name and output message follow the same rules as those defined for a SOAP Service portType.");
        replaceMap.put("Step 25.2 VERIFY that the operation name matches the operation as specified in the portType section of the WSDL for each operation (file) supported by the center.", "Step 20.6 VERIFY that the operation name matches the operation as specified in the portType section of the WSDL for each operation (file) supported by the center.");
        replaceMap.put("Step 25.3 VERIFY that the operation tag is followed by the ftp:operation tag.", "Step 20.7 VERIFY that the operation tag is followed by the ftp:operation tag.");
        replaceMap.put("Step 25.4 VERIFY that the location attribute is the name of the file. The name of the file is to be defined by each center.", "Step 20.8 VERIFY that the location attribute is the name of the file. The name of the file is to be defined by each center.");
        replaceMap.put("Step 25.5 VERIFY that the ftp:operation tag is followed by the input ,/input tag set.", "Step 20.9 VERIFY that the ftp:operation tag is followed by the input ,/input tag set.");
        replaceMap.put("Step 25.6 VERIFY that the input tag is followed by a ftp:urlEncoded tag.", "Step 20.10 VERIFY that the input tag is followed by a ftp:urlEncoded tag.");
        replaceMap.put("Step 25.7 VERIFY that the input tag set is followed by an output, /output tag set.", "Step 20.11 VERIFY that the input tag set is followed by an output, /output tag set.");
        replaceMap.put("Step 25.8 VERIFY that the output tag is followed by a mime:content tag. The valid type attribute of the mime:content tag is MIME type supported by the center server (assuming that the center MIME types are also valid per the FTP specification).", "Step 20.12 VERIFY that the output tag is followed by a mime:content tag. The valid type attribute of the mime:content tag is MIME type supported by the center server (assuming that the center MIME types are also valid per the FTP specification).");
        replaceMap.put("Step 25.9 VERIFY that the XML FTP endpoint uses the ftp:address tag to specify an endpoint rather than the soap:address tag.", "Step 20.13 VERIFY that the XML FTP endpoint uses the ftp:address tag to specify an endpoint rather than the soap:address tag.");
        replaceMap.put("Step 25.10 VERIFY that the address is a valid URL.", "Step 20.14 VERIFY that the address is a valid URL.");

        replaceAll("C:\\projects\\projects\\C2C-RI\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\CustomTestSuites\\NTCIPExtensionToBlankTestSuite\\AppLayer\\Data");
    }

    private static void replaceAll(String path) {
        File[] datalFiles = finder(path);


        for (File thisFile : datalFiles) {

            try {
                // input the file content to the String "input"
                BufferedReader file = new BufferedReader(new FileReader(thisFile));
                String line;
                String input = "";

                while ((line = file.readLine()) != null) {
                    input += line + '\n';
                }

                for (String thisStep : replaceMap.keySet()) {
                    input = input.replace(thisStep, replaceMap.get(thisStep));
                }

                // write the new String with the replaced line OVER the same file
                FileOutputStream outFile = new FileOutputStream(thisFile);
                outFile.write(input.getBytes());


            } catch (Exception e) {
                System.out.println("Problem reading file.");
            }

            System.out.println("Completed " + thisFile.getName() + ".");

        }
    }

    private static File[] finder(String dirName) {
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".data");
            }
        });

    }
}
