/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Modified for use in the C2CRI
 */
package org.fhwa.c2cri.messagemanager;

import org.apache.xmlbeans.XmlObject;
import java.util.ArrayList;
import java.net.URL;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;



/**
 * The Class MessageBodyGenerator creates a message based on a given schema.
 *
 * Last Updated:  1/8/2014
 */
public class MessageBodyGenerator {
    
    /** The schemas loaded. */
    private boolean schemasLoaded;
    
    /** The sts. */
    private SchemaTypeSystem sts;
   
    /**
     * Instantiates a new message body generator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private MessageBodyGenerator(){
        
    }
    
    /**
     * Instantiates a new message body generator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemaFiles the schema files
     * @throws Exception the exception
     */
    public MessageBodyGenerator(URL[] schemaFiles) throws Exception{
        this.schemasLoaded = true;

        boolean dl = true; // enable network downloads for imports and includes
        boolean nopvr = false;  //disable particle valid (restriction) rule
        boolean noupa = false;  //diable unique particle attributeion rule

        // Process Schema files
        List sdocs = new ArrayList();
        for (int i = 0; i < schemaFiles.length; i++) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[i],
                        (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            } catch (Exception e) {
                System.err.println("Can not load schema file: " + schemaFiles[i] + ": ");
                e.printStackTrace();
            }
        }

        XmlObject[] schemas = (XmlObject[]) sdocs.toArray(new XmlObject[sdocs.size()]);

        if (schemas.length > 0) {
            Collection errors = new ArrayList();
            XmlOptions compileOptions = new XmlOptions();
            if (dl) {
                compileOptions.setCompileDownloadUrls();
            }
            if (nopvr) {
                compileOptions.setCompileNoPvrRule();
            }
            if (noupa) {
                compileOptions.setCompileNoUpaRule();
            }

            try {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            } catch (Exception e) {
                if (errors.isEmpty() || !(e instanceof XmlException)) {
                    e.printStackTrace();
                }

                System.out.println("Schema compilation errors: ");
                String schemaErrors = "";
                for (Iterator i = errors.iterator(); i.hasNext();) {
                    schemaErrors = schemaErrors.concat((String) i.next() + "\n");
                }
                System.out.println(schemaErrors);

                throw new Exception("Schema compilation errors: \n" + schemaErrors);
            }
        }

        if (sts == null) {
            throw new Exception("No Schemas provided to process.");
        }

    }



    /**
     * Creates the message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param msgName the msg name
     * @return the string
     * @throws Exception the exception
     */
    public String createMessage(String msgName) throws Exception {
        String result = "";

        SchemaType[] globalElems = sts.documentTypes();
        SchemaType elem = null;

        for (int ii = 0; ii < globalElems.length; ii++) {
            System.out.println("Checking Element: "+ globalElems[ii].getDocumentElementName().getLocalPart());
            if (msgName.equals(globalElems[ii].getDocumentElementName().getLocalPart())) {
                elem = globalElems[ii];
                break;
            }
        }
        if (elem == null) {
            throw new Exception("Could not find a global element with name \"" + msgName + "\"");
        }
        result = XmlGeneratorUtility.createSampleForType(elem);

        return result;
    }
}
