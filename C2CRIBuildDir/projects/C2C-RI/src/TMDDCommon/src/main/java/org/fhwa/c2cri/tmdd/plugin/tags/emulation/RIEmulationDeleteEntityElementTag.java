/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags.emulation;

import org.fhwa.c2cri.tmdd.plugin.tags.C2CRIFunctionTag;
import javax.swing.JOptionPane;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.centermodel.RIEmulation;

/**
 * Delete an element from an entity
 *
 * @author TransCore ITS, LLC Last updated: 3/1/2016
 * @jameleon.function name="ri-EmulationDeleteEntityElement" type="action"
 * @jameleon.step Delete an element from an entity
 */
public class RIEmulationDeleteEntityElementTag extends C2CRIFunctionTag {

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
     * The name of the entity element to be set.
     *
     * @jameleon.attribute
     */
    protected String entityElementName;

    /**
     * Test block.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void testBlock() {
        boolean userInput = false;
        try {
            if (dataType == null) {
                userInput = true;
                dataType = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Data Type value:",
                        "RI Emulation Delete Entity Element",
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
                        "RI Emulation Delete Entity Element",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            if (entityElementName == null) {
                userInput = true;
                entityElementName = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Element Name:",
                        "RI Emulation Delete Entity Element",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            RIEmulation.getInstance().deleteEntityElement(dataType, entityId, entityElementName);
            if (userInput) {
                log.info("<userEvent>\nUser delete element: " + entityElementName + " of entityId: " + entityId + " for data type: " + dataType + "\n</userEvent>");
            }
            JOptionPane.showMessageDialog(null,
                    "The entity element was successfully deleted.",
                    "RI Emulation Delete Entity Element",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "RI Emulation Delete Entity Element Error",
                    JOptionPane.ERROR_MESSAGE);
            log.info("<userEvent>\nUser attempted to delete element: " + entityElementName + " of entityId: " + entityId + " for data type: " + dataType + " and received error: " + ex.getMessage()+".\n</userEvent>");
        }

    }
}
