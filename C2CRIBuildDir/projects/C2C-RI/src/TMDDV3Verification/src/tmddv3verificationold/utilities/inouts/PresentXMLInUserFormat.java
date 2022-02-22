/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verificationold.utilities.inouts;

import java.io.File;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xpathgen.XPathGenerator;

/**
 *
 * @author TransCore ITS
 */
public class PresentXMLInUserFormat {

    public static void main(String[] args) {

        String xmlFileName = "c:/inout/tmdd.xml";
//        String xmlFileName = "c:/inout/sampleXML.xml";

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
                            String elementPath = XPathGenerator.generateXPath(thisCursor2, null, new TMDDMessageNameSpaceContext());
//                            System.out.println(elementPath);
                            thisCursor2.pop();
                            thisCursor2.toNextToken();
                            if (!thisCursor2.getChars().trim().equals("")){
                               elementPath= elementPath.substring(elementPath.lastIndexOf(":")+1).replace("/", ".");
                               if (elementPath.startsWith("."))elementPath = elementPath.substring(1);
                               System.out.println(elementPath+" = " + thisCursor2.getChars());
                            }
                }
/**
                if (thisCursor2.toFirstChild()) {
                    System.out.println("Going to first child ..." + thisCursor2.getName());
                            thisCursor2.push();
                            thisCursor2.toParent();
                            String elementPath = XPathGenerator.generateXPath(thisCursor2, null, new TMDDMessageNameSpaceContext());
                            System.out.println(elementPath);
                            thisCursor2.pop();
                } else if (thisCursor2.toNextSibling()) {
                    System.out.println("Going to next sibling ..." + thisCursor2.getName());
                            thisCursor2.push();
                            thisCursor2.toParent();
                            String elementPath = XPathGenerator.generateXPath(thisCursor2, null, new TMDDMessageNameSpaceContext());
                            System.out.println(elementPath);
                            thisCursor2.pop();

                } else if (thisCursor2.toNextToken() != TokenType.NONE) {
                    System.out.println("Going to next token ..." + thisCursor2.getName());
                    thisCursor2.toNextToken();
                    System.out.println("Going to next token2 ..." + thisCursor2.getName());

                } else {
                    System.out.println("DON'T Know what this is!!!");
                    break;
                }
*/
            }
            thisCursor2.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
