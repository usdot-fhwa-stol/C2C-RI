/*
    Jameleon C2CRI plug-in - A plug-in developed for the RI to test for C2C Conformance.
*/
package org.fhwa.c2cri.tmdd.plugin.tags;


import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.function.TestStepTag;
import net.sf.jameleon.util.Configurator;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;
//import org.fhwa.c2cri.plugin.c2cri.C2CRISessionTag;


/**
 * The Class C2CRIFunctionTag.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public abstract class C2CRIFunctionTag extends FunctionTag{

    /** The Constant STORE_SOURCE_CONFIG_NAME. */
    public static final String STORE_SOURCE_CONFIG_NAME = "RI.storeSourceOnStateChange";
    
    /** The store source on state change. */
    protected boolean storeSourceOnStateChange;
    
    /** The session tag. */
    protected C2CRISessionTag sessionTag;
    


    /**
     * Gets the references of the session and the test results from the parent TestCase
     * This method gets called once all attributes are set from the macro language. Any required
     * setup should go here.
     */
    public void setupEnvironment() {
        super.setupEnvironment();
        storeSourceOnStateChange = Boolean.valueOf(Configurator.getInstance().getValue(STORE_SOURCE_CONFIG_NAME,"true")).booleanValue();
        sessionTag = (C2CRISessionTag)findAncestorWithClass(C2CRISessionTag.class);
        if (sessionTag == null) {
            throw new JameleonScriptException("a "+fp.getDefaultTagName() +" can only execute inside a C2CRI-session tag", this);
        }
    }

    /**
     * 
     * @param errorResults 
     */
    protected void processError(String errorResults){
        boolean continueAfterError = Boolean.valueOf(RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER(), TMDDSettingsImpl.getTMDD_CONTINUE_AFTER_MSG_VERIFY_DEFAULT_VALUE()));
        if (continueAfterError) {
            try {
                TestStepTag thisOne = (TestStepTag) this.getParent();
                thisOne.getFunctionResults().getParentResults().getFailedResults().clear();
                this.getFunctionResults().setError(new JameleonScriptException(errorResults));
                thisOne.getFunctionResults().setFailed();
            } catch (Exception ex) {
            }      
        } else {
            throw new JameleonScriptException(errorResults, this);           
        }
    }        

}
