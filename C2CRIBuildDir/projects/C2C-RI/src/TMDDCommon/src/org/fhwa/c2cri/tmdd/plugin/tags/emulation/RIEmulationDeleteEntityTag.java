/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags.emulation;

import org.fhwa.c2cri.tmdd.plugin.tags.C2CRIFunctionTag;
import javax.swing.JOptionPane;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.centermodel.RIEmulation;

/**
 * Delete an entity from the specified entity data.
 *
 * @author TransCore ITS, LLC Last updated: 3/1/2016
 * @jameleon.function name="ri-EmulationDeleteEntity" type="action"
 * @jameleon.step Add a new Entity
 */
public class RIEmulationDeleteEntityTag extends C2CRIFunctionTag {

    /**
     * The name of the entity data type
     *
     * @jameleon.attribute
     */
    protected String dataType;

    /**
     * The entity Identifier to use.
     *
     * @jameleon.attribute
     */
    protected String entityId;

    /**
     * Test block.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void testBlock() {
        try {
            boolean userInput = false;
            if (dataType == null) {
                userInput = true;
                dataType = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Data Type value:",
                        "RI Emulation Delete Entity",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            if (entityId == null) {
                userInput = true;
                entityId = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity ID value:",
                        "RI Emulation Delete Entity",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }
            RIEmulation.getInstance().deleteEntity(dataType, entityId);
            if (userInput) {
                log.info("<userEvent>\nUser deleted entityId: " + entityId + " for data type: " + dataType + "\n</userEvent>");
            }
            JOptionPane.showMessageDialog(null,
                    "The entity was successfully deleted.",
                    "RI Emulation Delete Entity",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "RI Emulation Delete Entity Error",
                    JOptionPane.ERROR_MESSAGE);
                log.info("<userEvent>\nUser deleted entityId: " + entityId + " for data type: " + dataType+ " and received error: " + ex.getMessage() + "\n</userEvent>");
        }

    }
}
