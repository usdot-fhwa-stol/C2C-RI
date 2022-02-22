/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.centermodel;

import net.xeoh.plugins.base.Plugin;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.messagemanager.Message;

/**
 *
 * @author TransCore ITS, LLC
 * Created: Mar 1, 2016
 */
public interface RIEmulator extends Plugin {

    /**
     * Add a new entity to the set using the provided entityId. The elements
     * which make up the entity are pulled from the default entity definition.
     * The entityID will be used for the device-id value.
     *
     * Each Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the Inventory items.
     *
     * Checks: If entityID already exists throw an exception instead of adding
     * it. If the entityId is not valid (null or too long) throw an exception
     * instead of adding it.
     *
     * @param entityDataType
     * @param entityId
     * @throws org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException
     */
    void addEntity(String entityDataType, String entityId) throws EntityEmulationException;

    /**
     *
     * @param entityDataType
     * @param entityId
     * @param elementName
     * @param elementValue
     * @throws EntityEmulationException
     */
    void addEntityElement(String entityDataType, String entityId, String elementName, String elementValue) throws EntityEmulationException;

    /**
     *
     * @param entityDataType
     * @param entityId
     * @throws EntityEmulationException
     */
    void deleteEntity(String entityDataType, String entityId) throws EntityEmulationException;

    /**
     *
     * @param entityDataType
     * @param entityId
     * @param entityElementName
     * @throws EntityEmulationException
     */
    void deleteEntityElement(String entityDataType, String entityId, String entityElementName) throws EntityEmulationException;

    /**
     *
     * @param entityDataType
     * @param entityId
     * @param entityElement
     * @return
     * @throws EntityEmulationException
     */
    String getEntityElementValue(String entityDataType, String entityId, String entityElement) throws EntityEmulationException;

    /**
     *
     * @param dialog
     * @param requestMessage
     * @return
     * @throws EntityEmulationException
     */
    Message getPubResponse(String dialog, Message requestMessage) throws EntityEmulationException;

    /**
     *
     * @param dialog
     * @param requestMessage
     * @return
     * @throws EntityEmulationException
     */
    Message getRRResponse(String dialog, Message requestMessage) throws EntityEmulationException;

    /**
     *
     * @param dialog
     * @param requestMessage
     * @return
     * @throws EntityEmulationException
     */
    Message getSubResponse(String dialog, Message requestMessage) throws EntityEmulationException;

    /**
     *
     * @param emulationParameters
     * @throws EntityEmulationException
     */
    void initialize(RIEmulationParameters emulationParameters) throws EntityEmulationException;

    /**
     *
     * @param entityDataType
     * @param entityId
     * @param entityElementName
     * @param entityElementValue
     * @throws EntityEmulationException
     */
    void updateEntityElement(String entityDataType, String entityId, String entityElementName, String entityElementValue) throws EntityEmulationException;
 
    /**
     *
     * @return
     */
    String getEmulatorStandard();
    
}
