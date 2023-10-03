package tmddv3verification.utilities.inouts;


import javax.xml.namespace.NamespaceContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TransCore ITS
 */
public class TMDDMessageNameSpaceContext implements NamespaceContext{

            public String getNamespaceURI(String prefix)
            {
                if ("tmdd301".equals(prefix))
                    return "http://www.tmdd.org/301/messages";
                else if ("tmdd".equals(prefix))
                    return "http://www.tmdd.org/303/messages";
                else if ("c2c".equals(prefix))
                    return "http://www.ntcip.org/c2c-message-administration";
                else
                    return null;
            }
            public String getPrefix(String namespaceUri)
            {
                if ("http://www.tmdd.org/301/messages".equals(namespaceUri))
                    return "tmdd301";
                else if ("http://www.tmdd.org/303/messages".equals(namespaceUri))
                    return "tmdd";
                else if ("http://www.ntcip.org/c2c-message-administration".equals(namespaceUri))
                    return "c2c";
                else if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri))
                    return "soap";
                else
				{
					return null;
				}
            }
            public java.util.Iterator getPrefixes(String namespaceUri)
            {
                return null;
            }

}
