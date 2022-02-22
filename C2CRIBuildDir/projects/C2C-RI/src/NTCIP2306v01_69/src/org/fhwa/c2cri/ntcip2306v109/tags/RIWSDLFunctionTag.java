/*
    Jameleon RIPOC plug-in - A plug-in developed for the RI to test for C2C Conformance.  
    Based on the Watij plug-in, so some residue remains.
*/
package org.fhwa.c2cri.ntcip2306v109.tags;


import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.util.Configurator;


/**
 * The Class RIWSDLFunctionTag establishes the base class for Testing various WSDL functions.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public abstract class RIWSDLFunctionTag extends FunctionTag{

    /** The Constant STORE_SOURCE_CONFIG_NAME. */
    public static final String STORE_SOURCE_CONFIG_NAME = "RI.storeSourceOnStateChange";
    
    /** The store source on state change. */
    protected boolean storeSourceOnStateChange;
    
    /** The session tag. */
    protected WSDLSessionTag sessionTag;
    


    /**
     * Gets the references of the session and the test results from the parent TestCase
     * This method gets called once all attributes are set from the macro language. Any required
     * setup should go here.
     */
    public void setupEnvironment() {
        super.setupEnvironment();
        storeSourceOnStateChange = Boolean.valueOf(Configurator.getInstance().getValue(STORE_SOURCE_CONFIG_NAME,"true")).booleanValue();
        sessionTag = (WSDLSessionTag)findAncestorWithClass(WSDLSessionTag.class);
        if (sessionTag == null) {
            throw new JameleonScriptException("a "+fp.getDefaultTagName() +" can only execute inside a WSDL-session tag", this);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////


}
