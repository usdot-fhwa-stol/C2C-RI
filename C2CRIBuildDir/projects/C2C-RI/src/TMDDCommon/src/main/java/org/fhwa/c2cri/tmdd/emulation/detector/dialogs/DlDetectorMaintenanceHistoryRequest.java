/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.detector.dialogs;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.emulation.DialogProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.tmdd.errorcodes.TMDDCenterDoesNotSupportMessageError;
import org.fhwa.c2cri.tmdd.errorcodes.TMDDMissingInformationError;

/**
 *
 * @author TransCore ITS, LLC Created: Mar 27, 2016
 */
public class DlDetectorMaintenanceHistoryRequest extends DialogProcessor {

    public DlDetectorMaintenanceHistoryRequest() {
        this.setDialogName("dlDetectorMaintenanceHistoryRequest");
        this.setIsPublication(false);
    }

    @Override
    protected void preprocessing(EntityEmulationRequests.EntityRequestMessageType requestMessageType, MessageSpecification requestMessage) throws TMDDCenterDoesNotSupportMessageError, TMDDMissingInformationError {
        if (!requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DETECTORMAINTENANCEHISTORYREQUESTMSG.relatedMessage())) {
            throw new TMDDCenterDoesNotSupportMessageError("The message received did not contain the correct message type for the dlDetectorMaintenanceHistoryRequest dialog.");
        }

        String msgDeviceInformationType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DETECTORMAINTENANCEHISTORYREQUESTMSG.relatedMessage() + "." + "device-information-request-header.device-information-type"));
        String msgDeviceType = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.DETECTORMAINTENANCEHISTORYREQUESTMSG.relatedMessage() + "." + "device-information-request-header.device-type"));
        if ((msgDeviceInformationType.equals("5") || msgDeviceInformationType.equals("device maintenance history"))
                && (msgDeviceType.equals("1") || msgDeviceType.equals("detector"))) {

        } else {
            throw new TMDDMissingInformationError("The message received did not contain correct device-type and device-information-type values for the dlDetectorMaintenanceHistoryRequest dialog.");
        }
        
    }

    @Override
    protected MessageSpecification generateResponse(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, EntityCommandProcessor commandProcessor, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg = TMDDMessageProcessor.processDetectorMaintenanceHistoryRequestMsg(requestMessage, entityDataType, dataCollector, false);
        return responseMsg;
    }

}