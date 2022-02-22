/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags.emulation;

import org.fhwa.c2cri.tmdd.plugin.tags.C2CRIFunctionTag;
import javax.swing.JOptionPane;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.centermodel.RIEmulation;

/**
 * Add a new element to an entity
 *
 * @author TransCore ITS, LLC Last updated: 3/1/2016
 * @jameleon.function name="ri-EmulationAddEntityElement" type="action"
 * @jameleon.step Add a new element to an entity
 */
public class RIEmulationAddEntityElementTag extends C2CRIFunctionTag {

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
     * The value to assign to the entity element.
     *
     * @jameleon.attribute
     */
    protected String entityElementValue;

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
                        "RI Emulation Add Entity Element",
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
                        "RI Emulation Add Entity Element",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            if (entityElementName == null) {
                userInput = true;
                entityElementName = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Element name:",
                        "RI Emulation Add Entity Element",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            if (entityElementValue == null) {
                userInput = true;
                entityElementValue = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Element value:",
                        "RI Emulation Add Entity Element",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            RIEmulation.getInstance().addEntityElement(dataType, entityId, entityElementName, entityElementValue);
            if (userInput) {
                log.info("<userEvent>\nUser added element: " + entityElementName + " with value: " + entityElementValue.replace("<", "&lt;").replace(">", "&gt;") + " for entityId: " + entityId + " for data type: " + dataType + "\n</userEvent>");
            }
            JOptionPane.showMessageDialog(null,
                    "The entity element was successfully added.",
                    "RI Emulation Add Entity Element",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "RI Emulation Add Entity Element Error",
                    JOptionPane.ERROR_MESSAGE);
            log.info("<userEvent>\nUser added element: " + entityElementName + " with value: " +(entityElementValue==null?"null":entityElementValue.replace("<", "&lt;").replace(">", "&gt;")) + " for entityId: " + entityId + " for data type: " + dataType+ " and received error: " + ex.getMessage() + "\n</userEvent>");
        }

    }
}
