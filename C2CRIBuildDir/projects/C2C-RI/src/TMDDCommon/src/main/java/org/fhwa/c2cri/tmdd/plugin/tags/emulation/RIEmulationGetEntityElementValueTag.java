/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags.emulation;

import org.fhwa.c2cri.tmdd.plugin.tags.C2CRIFunctionTag;
import javax.swing.JOptionPane;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.centermodel.RIEmulation;

/**
 * Get the value for a specified element of an entity. Sets the value to the
 * ENTITYELEMENTVALUE context variable.
 *
 * @author TransCore ITS, LLC Last updated: 3/1/2016
 * @jameleon.function name="ri-EmulationGetEntityElementValue" type="action"
 * @jameleon.step Get the value of an element of an entity
 */
public class RIEmulationGetEntityElementValueTag extends C2CRIFunctionTag {

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
        try {
            if (dataType == null) {
                dataType = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Data Type value:",
                        "RI Emulation Get Entity Element Value",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            if (entityId == null) {
                entityId = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity ID value:",
                        "RI Emulation Get Entity Element Value",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }

            if (entityElementName == null) {
                entityElementName = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter the Entity Element Name:",
                        "RI Emulation Get Entity Element Value",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
            }
            String entityElementValue = RIEmulation.getInstance().getEntityElementValue(dataType, entityId, entityElementName);
            setVariable("ENTITYELEMENTVALUE", entityElementValue);
            JOptionPane.showMessageDialog(null,
                    "The " + entityElementName + " associated with entity Id " + entityId + " of data type " + dataType + " has a value of: \n" + entityElementValue,
                    "RI Emulation Get Entity Element Value",
                    JOptionPane.PLAIN_MESSAGE);
            log.info("<userEvent>\nUser requested element value: The " + entityElementName + " associated with entity Id " + entityId + " of data type " + dataType + " has a value of: \n" + entityElementValue + "\n</userEvent>");
  
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "RI Emulation Get Entity Element Value Error",
                    JOptionPane.ERROR_MESSAGE);
            log.info("<userEvent>\nUser requested element value: The " + entityElementName + " associated with entity Id " + entityId + " of data type " + dataType + " failed and received error: " + ex.getMessage()+ "\n</userEvent>");
        }

    }
}
