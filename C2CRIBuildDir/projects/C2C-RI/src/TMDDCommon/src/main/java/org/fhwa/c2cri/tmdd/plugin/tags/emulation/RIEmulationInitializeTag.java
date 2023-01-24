/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags.emulation;

import org.fhwa.c2cri.tmdd.plugin.tags.C2CRIFunctionTag;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.centermodel.RIEmulation;

/**
 * Triggers the initialization of RIEmulation
 *
 * @author TransCore ITS, LLC Last updated: 3/1/2016
 * @jameleon.function name="ri-EmulationInitialize" type="action"
 * @jameleon.step Initialize RI Emulation to its initial state
 */
public class RIEmulationInitializeTag extends C2CRIFunctionTag {

    private javax.swing.ProgressMonitor progressMonitor;

    /**
     * RIEmulationInitializeTag Test block.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void testBlock() {

        BasicGUIActionWrapper initRIAction = new BasicGUIActionWrapper(null, "Initializing the Entity Emulation Data") {

            @Override
            protected Boolean actionMethod() throws Exception {
         try {
                   RIEmulation.getInstance().initialize();
                   log.info("<userEvent>\nUser Initialized RI Emulation\n</userEvent>");
                   JOptionPane.showMessageDialog(null,
                            "RI Emulation has been successfully Initialized.",
                            "RI Emulation Initialization",
                            JOptionPane.PLAIN_MESSAGE);                
//           RIEmulation.getInstance().initialize();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "RI Emulation Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
                   log.info("<userEvent>\nUser attempt to initialize RI Emulation failed with error: " + ex.getMessage()+"\n</userEvent>");
        }
                return true;
            }

            @Override
            protected void wrapUp(Boolean result) {
                if (!result) {
                    javax.swing.JOptionPane.showMessageDialog(null, "An error was encountered trying to complete the Emulation Initialization action.");
                }
            }
        };
        initRIAction.execute();

    }

}
