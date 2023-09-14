/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verificationold.utilities.inouts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tmddv3verificationold.utilities.TMDDWSDL;

/**
 *
 * @author TransCore ITS, LLC
 */
public class inoutTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String wsdlFileName = "file:\\c:\\inout\\tmp\\tmdd_modified.wsdl";
        String wsdlFileName = "file:\\c:\\inout\\tmddschemasv9\\tmdd.wsdl";
        //    TMDDWSDL tmddWSDL = new TMDDWSDL(wsdlFileName);


        String xmlFileName = "file:/c:/inout/tmdd.xml";

        String tmpxmlFileName = "c:/inout/sampleXML.xml";

        /**
        try {
        XmlOptions theOptions = new XmlOptions();
        theOptions.put(XmlOptions.SAVE_OUTER);

        XmlObject thisObject1 = XmlObject.Factory.parse(new File(tmpxmlFileName), theOptions);
        //        XmlObject thisObject1 = XmlObject.Factory.newInstance();
        XmlCursor thisCursor1 = thisObject1.newCursor();
        makeMessageElement(thisObject1, thisCursor1, "messageElement/messagElement1[3]/messageElement24", "cat2");
        makeMessageElement(thisObject1, thisCursor1, "messageElement/messagElement1/messageElement24", "cat");
        makeMessageElement(thisObject1, thisCursor1, "messageElement/messagElement2/messageElement25", "dog");
        makeMessageElement(thisObject1, thisCursor1, "messageElement/messagElement2[2]/messageElement27", "dogAgain");

        makeMessageElement(thisObject1, thisCursor1, "messageElement/messagElement1[1]/messageElement24", "cat3");
        thisCursor1.dispose();

        System.out.println(thisObject1.toString());
        } catch (Exception ex) {
        ex.printStackTrace();
        }
         */
        String initialMessage = "";
        initialMessage = updateMessagefromElement(initialMessage, "messageElement/messagElement1[3]/messageElement24", "cat2");
        initialMessage = updateMessagefromElement(initialMessage, "messageElement/messagElement1/messageElement24", "cat");
        initialMessage = updateMessagefromElement(initialMessage, "messageElement/messagElement2/messageElement25", "dog");
        initialMessage = updateMessagefromElement(initialMessage, "messageElement/messagElement2[2]/messageElement27", "dogAgain");

        initialMessage = updateMessagefromElement(initialMessage, "messageElement/messagElement1[1]/messageElement24", "cat3");
        System.out.println(initialMessage);

        System.out.println("\n\n");

        XmlObject thisObject = XmlObject.Factory.newInstance();
        XmlCursor thisCursor = thisObject.newCursor();
        // -> <docroot>^
        thisCursor.toNextToken();

        thisCursor.insertElement("messageElement");

        /// -> <elem>^</elem>
        thisCursor.toPrevToken();
        thisCursor.insertElement("messageElement1");
        thisCursor.insertElement("messageElement2");

        thisCursor.toStartDoc();
        thisCursor.selectPath("./messageElement/messageElement1");
        if (thisCursor.hasNextSelection()) {
            thisCursor.toNextSelection();
            thisCursor.toNextToken();
            thisCursor.insertElement("messageElement1AddOn");
        }

        thisCursor.toStartDoc();
        thisCursor.selectPath("./messageElement/messageElement25");
        if (thisCursor.hasNextSelection()) {
            System.out.println("for some reason the softare thinks messageElement25 exists");
        } else {
            thisCursor.selectPath("./messageElement");
            thisCursor.toNextSelection();
            thisCursor.toNextToken();
//            thisCursor.toPrevToken();
            thisCursor.insertElement("messageElement25");
        }
        thisCursor.dispose();
        System.out.println(thisObject.toString());

    }

    private static void makeMessageElement(XmlObject xmlObject, XmlCursor xmlc, String messageElementSpec, String value) {

        String previousElementSpec = "";
        String nextElementInMessageSpec = "";

        // grab the next message node in the messageElementSpec; or return parent if the spec is empty
        String[] partsOfMessageElementSpec = messageElementSpec.trim().split("/");


        for (int ii = 0; ii < partsOfMessageElementSpec.length; ii++) {

            nextElementInMessageSpec = nextElementInMessageSpec.concat("/" + partsOfMessageElementSpec[ii]);
            xmlc.toStartDoc();
            xmlc.selectPath("$this/" + nextElementInMessageSpec);

            // If this element doesn't exist we need to create it.
            if (!xmlc.hasNextSelection()) {
                if (ii != 0) {
                    // Create elements within the message

                    if (containsIndex(partsOfMessageElementSpec[ii])) {
                        // Determine how many instances of this element are currently in the document?
                        String currentPath = previousElementSpec.concat("/" + partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
                        xmlc.selectPath("$this/" + currentPath);


                        if (xmlc.hasNextSelection()) {
                            Integer currentNodes = xmlc.getSelectionCount();
                            Integer difference = getIndex(partsOfMessageElementSpec[ii]) - currentNodes;
                            xmlc.toSelection(currentNodes - 1);
                            xmlc.toEndToken();
                            xmlc.toNextToken();
                            for (int jj = 0; jj < difference; jj++) {
                                xmlc.insertElement(partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
                            }
                            xmlc.push();
                            if (ii == partsOfMessageElementSpec.length - 1) {
                                xmlc.toPrevToken();
                                xmlc.insertChars(value);
                            }
                            xmlObject = xmlObject.set(xmlObject);
                            xmlc = xmlObject.newCursor();
                            previousElementSpec = nextElementInMessageSpec;
                        }
                    } else {

                        xmlc.selectPath("$this/" + previousElementSpec);
                        if (xmlc.hasNextSelection()) {
                            xmlc.toNextSelection();
//                        xmlc.toNextToken();
                            xmlc.toEndToken();
                            xmlc.insertElement(partsOfMessageElementSpec[ii]);
                            if (ii == partsOfMessageElementSpec.length - 1) {
                                xmlc.toPrevToken();
                                String existingContent = xmlc.getChars();
                                if (!existingContent.isEmpty()) {
                                    xmlc.removeChars(existingContent.length());
                                }
                                xmlc.insertChars(value);
                            }
                            previousElementSpec = nextElementInMessageSpec;
                        } else {  // The xpath doesn't work right with [] searches
                            System.out.println("What's going on here??? !!!!!! - " + previousElementSpec + " of " + messageElementSpec);
                        }
                    }
                } else {  // Create the first "message" Element
                    // -> <docroot>^
                    xmlc.toNextToken();
                    xmlc.insertElement(partsOfMessageElementSpec[ii]);
                    previousElementSpec = partsOfMessageElementSpec[ii];
                    if (ii == partsOfMessageElementSpec.length - 1) {
                        xmlc.toPrevToken();
                        String existingContent = xmlc.getChars();
                        if (!existingContent.isEmpty()) {
                            xmlc.removeChars(existingContent.length());
                        }
                        xmlc.insertChars(value);
                    }

                }
            } else {  // the element does exist.  No need to create it.
                if (ii == partsOfMessageElementSpec.length - 1) {
                    xmlc.toNextSelection();
                    xmlc.toNextToken();
                    String existingContent = xmlc.getChars();
                    if (!existingContent.isEmpty()) {
                        xmlc.removeChars(existingContent.length());
                    }
                    xmlc.insertChars(value);
                }
                previousElementSpec = nextElementInMessageSpec;

            }

        }
        //           return xmlc;

        xmlc.clearSelections();

    }

    private static String updateMessagefromElement(String message, String messageElementSpec, String value) {

        String previousElementSpec = "";
        String nextElementInMessageSpec = "";

        // grab the next message node in the messageElementSpec; or return parent if the spec is empty
        String[] partsOfMessageElementSpec = messageElementSpec.trim().split("/");

        String currentMessage = message;

        XmlObject xmlObj = XmlObject.Factory.newInstance();
        XmlCursor xmlc = xmlObj.newCursor();

        if (!currentMessage.isEmpty()) {
            try {
                xmlObj = XmlObject.Factory.parse(message);
                xmlc = xmlObj.newCursor();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }




        for (int ii = 0; ii < partsOfMessageElementSpec.length; ii++) {

            nextElementInMessageSpec = nextElementInMessageSpec.concat("/" + partsOfMessageElementSpec[ii]);
            xmlc.toStartDoc();
            xmlc.selectPath("$this/" + nextElementInMessageSpec);

            // If this element doesn't exist we need to create it.
            if (!xmlc.hasNextSelection()) {
                if (ii != 0) {
                    // Create elements within the message

                    if (containsIndex(partsOfMessageElementSpec[ii])) {
                        // Determine how many instances of this element are currently in the document?
                        String currentPath = previousElementSpec.concat("/" + partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
                        xmlc.selectPath("$this/" + currentPath);


                        if (xmlc.hasNextSelection()) {
                            Integer currentNodes = xmlc.getSelectionCount();
                            Integer difference = getIndex(partsOfMessageElementSpec[ii]) - currentNodes;
                            xmlc.toSelection(currentNodes - 1);
                            xmlc.toEndToken();
                            xmlc.toNextToken();
                            for (int jj = 0; jj < difference; jj++) {
                                xmlc.insertElement(partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
                            }
                            if (ii == partsOfMessageElementSpec.length - 1) {
                                xmlc.toPrevToken();
                                xmlc.insertChars(value);
                            }
                            currentMessage = xmlObj.toString();
                            try {
                                xmlObj = XmlObject.Factory.parse(currentMessage);
                                xmlc = xmlObj.newCursor();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            previousElementSpec = nextElementInMessageSpec;
                        } else {
                            xmlc.selectPath("$this/" + previousElementSpec);


                            if (xmlc.hasNextSelection()) {
                                Integer totalNodes = getIndex(partsOfMessageElementSpec[ii]);
                                xmlc.toNextSelection();
 //                               xmlc.toEndToken();
                                xmlc.toNextToken();
                                for (int jj = 0; jj < totalNodes; jj++) {
                                    xmlc.insertElement(partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
                                }
                                if (ii == partsOfMessageElementSpec.length - 1) {
                                    xmlc.toPrevToken();
                                    xmlc.insertChars(value);
                                }

                                currentMessage = xmlObj.toString();
                                try {
                                    xmlObj = XmlObject.Factory.parse(currentMessage);
                                    xmlc = xmlObj.newCursor();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                previousElementSpec = nextElementInMessageSpec;
                            }
                        }
                    } else {

                        xmlc.selectPath("$this/" + previousElementSpec);
                        if (xmlc.hasNextSelection()) {
                            xmlc.toNextSelection();
//                        xmlc.toNextToken();
                            xmlc.toEndToken();
                            xmlc.insertElement(partsOfMessageElementSpec[ii]);
                            if (ii == partsOfMessageElementSpec.length - 1) {
                                xmlc.toPrevToken();
                                String existingContent = xmlc.getChars();
                                if (!existingContent.isEmpty()) {
                                    xmlc.removeChars(existingContent.length());
                                }
                                xmlc.insertChars(value);
                            }

                            currentMessage = xmlObj.toString();
                            try {
                                xmlObj = XmlObject.Factory.parse(currentMessage);
                                xmlc = xmlObj.newCursor();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            previousElementSpec = nextElementInMessageSpec;
                        } else {  // The xpath doesn't work right with [] searches
                            System.out.println("What's going on here??? !!!!!! - " + previousElementSpec + " of " + messageElementSpec);
                        }
                    }
                } else {  // Create the first "message" Element
                    // -> <docroot>^
                    xmlc.toNextToken();
                    xmlc.insertElement(partsOfMessageElementSpec[ii]);
                    previousElementSpec = partsOfMessageElementSpec[ii];
                    if (ii == partsOfMessageElementSpec.length - 1) {
                        xmlc.toPrevToken();
                        String existingContent = xmlc.getChars();
                        if (!existingContent.isEmpty()) {
                            xmlc.removeChars(existingContent.length());
                        }
                        xmlc.insertChars(value);
                    }

                    currentMessage = xmlObj.toString();
                    try {
                        xmlObj = XmlObject.Factory.parse(currentMessage);
                        xmlc = xmlObj.newCursor();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            } else {  // the element does exist.  No need to create it.
                if (ii == partsOfMessageElementSpec.length - 1) {
                    xmlc.toNextSelection();
                    xmlc.toNextToken();
                    String existingContent = xmlc.getChars();
                    if (!existingContent.isEmpty()) {
                        xmlc.removeChars(existingContent.length());
                    }
                    xmlc.insertChars(value);
                }
                currentMessage = xmlObj.toString();
                try {
                    xmlObj = XmlObject.Factory.parse(currentMessage);
                    xmlc = xmlObj.newCursor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                previousElementSpec = nextElementInMessageSpec;

            }

        }

        return currentMessage;
    }

    private static void makeDOMMessageElement(Node node, String messageElementSpec, String value) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        String previousElementSpec = "";
        String nextElementInMessageSpec = "";

        // grab the next message node in the messageElementSpec; or return parent if the spec is empty
        String[] partsOfMessageElementSpec = messageElementSpec.trim().split("/");


        for (int ii = 0; ii < partsOfMessageElementSpec.length; ii++) {

            nextElementInMessageSpec = nextElementInMessageSpec.concat("/" + partsOfMessageElementSpec[ii]);

            Document doc = node.getOwnerDocument();
            System.out.println(doc.getDocumentElement().getLocalName());


            // Create a XPathFactory
            XPathFactory xFactory = XPathFactory.newInstance();

            // Create a XPath object
            XPath xpath = xFactory.newXPath();

            XPathExpression expr = null;
            // Compile the XPath expression
            expr = xpath.compile("./" + messageElementSpec);
            // Run the query and get a nodeset
            Object result = expr.evaluate(doc, XPathConstants.NODESET);

            // Cast the result to a DOM NodeList
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println(nodes.item(i).getNodeValue());
            }

            /**
            xmlc.selectPath("$this/" + nextElementInMessageSpec);

            // If this element doesn't exist we need to create it.
            if (!xmlc.hasNextSelection()) {
            if (ii != 0) {
            // Create elements within the message

            if (containsIndex(partsOfMessageElementSpec[ii])) {
            // Determine how many instances of this element are currently in the document?
            String currentPath = previousElementSpec.concat("/" + partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
            xmlc.selectPath("$this/" + currentPath);


            if (xmlc.hasNextSelection()) {
            Integer currentNodes = xmlc.getSelectionCount();
            Integer difference = getIndex(partsOfMessageElementSpec[ii]) - currentNodes;
            xmlc.toSelection(currentNodes-1);
            xmlc.toEndToken();
            xmlc.toNextToken();
            for (int jj = 0; jj < difference; jj++) {
            xmlc.insertElement(partsOfMessageElementSpec[ii].substring(0, partsOfMessageElementSpec[ii].lastIndexOf("[")));
            }
            xmlc.push();
            if (ii == partsOfMessageElementSpec.length - 1) {
            xmlc.toPrevToken();
            xmlc.insertChars(value);
            }
            previousElementSpec = nextElementInMessageSpec;
            }
            } else {

            xmlc.selectPath("$this/" + previousElementSpec);
            if (xmlc.hasNextSelection()) {
            xmlc.toNextSelection();
            //                        xmlc.toNextToken();
            xmlc.toEndToken();
            xmlc.insertElement(partsOfMessageElementSpec[ii]);
            if (ii == partsOfMessageElementSpec.length - 1) {
            xmlc.toPrevToken();
            String existingContent = xmlc.getChars();
            if (!existingContent.isEmpty()) {
            xmlc.removeChars(existingContent.length());
            }
            xmlc.insertChars(value);
            }
            previousElementSpec = nextElementInMessageSpec;
            } else {  // The xpath doesn't work right with [] searches
            xmlc.pop();
            xmlc.toPrevToken();
            xmlc.insertElement(partsOfMessageElementSpec[ii]);
            xmlc.push();
            if (ii == partsOfMessageElementSpec.length - 1) {
            xmlc.toPrevToken();
            String existingContent = xmlc.getChars();
            if (!existingContent.isEmpty()) {
            xmlc.removeChars(existingContent.length());
            }
            xmlc.insertChars(value);
            }
            previousElementSpec = nextElementInMessageSpec;
            }
            }
            } else {  // Create the first "message" Element
            // -> <docroot>^
            xmlc.toNextToken();
            xmlc.insertElement(partsOfMessageElementSpec[ii]);
            previousElementSpec = partsOfMessageElementSpec[ii];
            if (ii == partsOfMessageElementSpec.length - 1) {
            xmlc.toPrevToken();
            String existingContent = xmlc.getChars();
            if (!existingContent.isEmpty()) {
            xmlc.removeChars(existingContent.length());
            }
            xmlc.insertChars(value);
            }

            }
            } else {  // the element does exist.  No need to create it.
            if (ii == partsOfMessageElementSpec.length - 1) {
            xmlc.toNextSelection();
            xmlc.toNextToken();
            String existingContent = xmlc.getChars();
            if (!existingContent.isEmpty()) {
            xmlc.removeChars(existingContent.length());
            }
            xmlc.insertChars(value);
            }
            previousElementSpec = nextElementInMessageSpec;

            }
             */
        }
        //           return xmlc;

//        xmlc.clearSelections();

    }

    private static Boolean containsIndex(String messagePart) {
        return ((messagePart.contains("[")) && (messagePart.endsWith("]")));
    }

    private static Integer getIndex(String messagePart) {
        String index = messagePart.substring(messagePart.indexOf("[") + 1, messagePart.indexOf("]"));
        return index.isEmpty() ? 1 : Integer.parseInt(index);
    }

    /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
     */
    private static String readFileAsString(String filePath)
            throws java.io.IOException, java.net.URISyntaxException {

        URI fileURI = new URI(filePath);
        URL fileURL = fileURI.toURL();
        StringBuffer fileData = new StringBuffer(1000);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileURL.openStream())))
		{
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		}
        return fileData.toString();
    }
}
