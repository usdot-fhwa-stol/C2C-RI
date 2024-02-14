/**
 * 
 */
package org.fhwa.c2cri.plugin.c2cri.tags;

import net.sf.jameleon.function.FunctionTag;
import javax.swing.*;

/**
 * Prompt the user for string input.
 * This tag may be used anywhere inside the testcase tag.
 * @jameleon.function name="sample"
 */
public class RIUserPromptTag extends FunctionTag {

    /** the input text. 
     * @jameleon.attribute default="RIUserPromptTag" */
    protected String sampleString;
    
    /** the user provided input. 
     * @jameleon.attribute contextName="RIUserInputReturn" */
    protected String returnVal;
    
    /** the flag indicating whether to skip the confirmation or not.. 
     * @jameleon.attribute */
    protected boolean skipConfirmation = false;

    /** The return string. */
    private String returnString;

    /**
     * Promptuser.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param strPrompt the str prompt
     */
    public void promptuser(String strPrompt, boolean skipFeedback) {
        String str = JOptionPane.showInputDialog(null, strPrompt,
                "C2C RI", 1);
        if (str != null) {
            if (!skipFeedback)JOptionPane.showMessageDialog(null, "You entered the value : " + str,
                    "C2C RI", 1);
            returnString = str;
        } else {
            JOptionPane.showMessageDialog(null, "You cancelled the entry  ... returning nothing.",
                    "C2C RI", 1);
            returnString = "";
        }
    }

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {

        returnString = " ";
        promptuser(sampleString.replace("/n","\n"), skipConfirmation);
        System.out.println("Setting variable " + returnVal + " to " + returnString);
        setVariable(returnVal, returnString);

    }
    ///////////////////////////////////////////////////////////////////
    //			FIELD NAMES						                    ///
    // These attributes are used to identify the fields in the form ///
    // So they can be easily changedif the form fields ever change  ///
    ///////////////////////////////////////////////////////////////////
}
