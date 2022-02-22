/**
 * 
 */
package org.fhwa.c2cri.plugin.c2cri.tags;

import javax.swing.JOptionPane;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;

/**
 * Pause the RI Script.
 *
 * @author TransCore ITS, LLC
 * 
 * Last Updated: 10/2/2012
 * @jameleon.function name="ri-pause" type="navigation"
 * @jameleon.step Pause the test script until the user confirms
 */

public class RIPauseTag extends FunctionTag {


    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock()
    {
        String pauseMessage = "The script has paused the test.\nPress OK to continue.";
        try{
          if (!this.getFunctionId().equals("")){
              pauseMessage = this.getFunctionId();
          }

          JOptionPane.showMessageDialog(null, pauseMessage, "Pause", JOptionPane.INFORMATION_MESSAGE);
          log.info("<userEvent>\nUser Acknowledged PauseTag Message\n</userEvent>");
        } catch (Exception ex){
            ex.printStackTrace();
            throw new JameleonScriptException("Error Pausing the RI Script: '"+ex.getMessage(), this);
        }
    }

}
