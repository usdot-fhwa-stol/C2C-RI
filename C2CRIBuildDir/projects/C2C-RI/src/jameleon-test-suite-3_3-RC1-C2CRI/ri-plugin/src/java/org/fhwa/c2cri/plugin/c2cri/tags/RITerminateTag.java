/**
 * 
 */
package org.fhwa.c2cri.plugin.c2cri.tags;

import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;

/**
 * Terminate the RI Script.
 *
 * @author TransCore ITS
 * Last Updated 12/10/2012
 * @jameleon.function name="ri-terminate" type="action"
 * @jameleon.step Pause the test script until the user confirms
 */

public class RITerminateTag extends FunctionTag {

    /** The message associated with this script termination. 
     * @jameleon.attribute required="false" */
    protected String Message="";

	/**
	 * Test block.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
    public void testBlock()
    {
	throw new JameleonScriptException("Script Termination: '"+this.getFunctionId()+"\n"+Message, this);
    }

}
