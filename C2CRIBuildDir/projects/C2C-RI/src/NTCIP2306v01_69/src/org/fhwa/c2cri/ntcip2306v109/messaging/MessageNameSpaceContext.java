package org.fhwa.c2cri.ntcip2306v109.messaging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

    /**
 * The Class MessageNameSpaceContext.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageNameSpaceContext implements NamespaceContext {

        /** The name space map. */
        private Map nameSpaceMap;

        /**
         * Instantiates a new message name space context.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param nameSpaceMap the name space map
         */
        public MessageNameSpaceContext(Map nameSpaceMap) {
            this.nameSpaceMap = nameSpaceMap;
        }

        /* (non-Javadoc)
         * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
         */
        @Override
        public String getNamespaceURI(String prefix) {
            if (nameSpaceMap.containsKey(prefix)) {
                return (String) nameSpaceMap.get(prefix);
            } else {
                return "UNRECOGNIZED";
            }

        }

        /* (non-Javadoc)
         * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
         */
        @Override
        public String getPrefix(String namespaceUri) {

            if (nameSpaceMap.containsValue(namespaceUri)) {
                Iterator<String> nsIterator = nameSpaceMap.keySet().iterator();
                while (nsIterator.hasNext()) {
                    String thisValue = nsIterator.next();
                    if (nameSpaceMap.get(thisValue).equals(namespaceUri)) {
                        return thisValue;
                    }
                }
            } else if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return "soap";
            }
            System.out.println("MessageNameSpaceContext:  !!!No Match for URL" + namespaceUri);
            return "UNRECOGNIZED";

        }

        /* (non-Javadoc)
         * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
         */
        @Override
        public java.util.Iterator getPrefixes(String namespaceUri) {
            ArrayList<String> list = new ArrayList();
            if (nameSpaceMap.containsValue(namespaceUri)){
                for (Object thisKey : nameSpaceMap.keySet()){
                    String thisURI = (String)nameSpaceMap.get(thisKey);
                    if (thisURI.equals(namespaceUri)){
                        list.add((String)thisKey);
                    }
                }
                return list.iterator();
            } else {
                return null;
            }
        }
    }

