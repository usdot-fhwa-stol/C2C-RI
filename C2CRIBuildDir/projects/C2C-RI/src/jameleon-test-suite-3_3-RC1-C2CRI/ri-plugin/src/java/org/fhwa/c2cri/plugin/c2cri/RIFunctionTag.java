/*
    Jameleon C2CRI plug-in - A plug-in developed for the RI to test for C2C Conformance.
*/
package org.fhwa.c2cri.plugin.c2cri;


import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.util.Configurator;


/**
 * The Class RIFunctionTag is the base tag for a number of C2C RI developed function tags.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/6/2012
 */
public abstract class RIFunctionTag extends FunctionTag{

    /** The Constant STORE_SOURCE_CONFIG_NAME. */
    public static final String STORE_SOURCE_CONFIG_NAME = "RI.storeSourceOnStateChange";
    
    /** The store source on state change. */
    protected boolean storeSourceOnStateChange;
    
    /** The session tag. */
    protected RISessionTag sessionTag;
    


    /**
     * Gets the references of the session and the test results from the parent TestCase
     * This method gets called once all attributes are set from the macro language. Any required
     * setup should go here.
     */
    public void setupEnvironment() {
        super.setupEnvironment();
        storeSourceOnStateChange = Boolean.valueOf(Configurator.getInstance().getValue(STORE_SOURCE_CONFIG_NAME,"true")).booleanValue();
        sessionTag = (RISessionTag)findAncestorWithClass(RISessionTag.class);
        if (sessionTag == null) {
            throw new JameleonScriptException("a "+fp.getDefaultTagName() +" can only execute inside a C2CRI-session tag", this);
        }
    }



}
