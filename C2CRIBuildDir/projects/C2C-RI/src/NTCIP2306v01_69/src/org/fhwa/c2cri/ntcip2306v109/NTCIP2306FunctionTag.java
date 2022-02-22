/*
    Jameleon C2CRI plug-in - A plug-in developed for the RI to test for C2C Conformance of NTCIP 2306.
*/
package org.fhwa.c2cri.ntcip2306v109;



import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.util.Configurator;
import org.fhwa.c2cri.ntcip2306v109.status.NTCIP2306Status;


/**
 * The abstract parent class for all NTCIP2306 function tags.
 * 
 * @author TransCore ITS LLC
 * Created 11/15/2013
 */
public abstract class NTCIP2306FunctionTag extends FunctionTag{

    /**
     * constant
     */
    public static final String STORE_SOURCE_CONFIG_NAME = "RI.storeSourceOnStateChange";
    /**
     * flag for whether to store the source on state change
     */
    protected boolean storeSourceOnStateChange;
    /**
     * the session tag reference object
     */
    protected NTCIP2306SessionTag sessionTag;
    


    /**
     * Gets the references of the session and the test results from the parent TestCase
     * This method gets called once all attributes are set from the macro language. Any required
     * setup should go here.
     */
    @Override
    public void setupEnvironment() {
        super.setupEnvironment();
        storeSourceOnStateChange = Boolean.valueOf(Configurator.getInstance().getValue(STORE_SOURCE_CONFIG_NAME,"true")).booleanValue();
        sessionTag = (NTCIP2306SessionTag)findAncestorWithClass(NTCIP2306SessionTag.class);
        if (sessionTag == null) {
            throw new JameleonScriptException("a "+fp.getDefaultTagName() +" can only execute inside a C2CRI-session tag", this);
        }
    }
    
    /**
     * All NTCIP2306 function tags are capable of storing context variables. Initialize those variables here.
     */
    public void initializeReturnParameters(){
         setVariable("TRANSPORTERRORRESULT", true);
         setVariable("TRANSPORTERRORTYPE", NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
         setVariable("ENCODINGERRORRESULT", true);
         setVariable("ENCODINGERRORTYPE", NTCIP2306Status.ENCODINGERRORTYPES.NONE.toString());
         setVariable("MESSAGEERRORRESULT", true);
         setVariable("MESSAGEERRORTYPE", NTCIP2306Status.MESSAGEERRORTYPES.NONE.toString());        
    }



}
