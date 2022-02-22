/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.events.dialogs;

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
public class DlEventIndexRequest extends DialogProcessor {

    public DlEventIndexRequest() {
        this.setDialogName("dlEventIndexRequest");
        this.setIsPublication(false);
    }

    @Override
    protected void preprocessing(EntityEmulationRequests.EntityRequestMessageType requestMessageType, MessageSpecification requestMessage) throws TMDDCenterDoesNotSupportMessageError, TMDDMissingInformationError {
        if (!requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage())) {
            throw new TMDDCenterDoesNotSupportMessageError("The message received did not contain the correct message type for the dlEventIndexRequest dialog.");
        }

        String msgTypeVersion = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-id"));
        String msgTypeID = (requestMessage.getElementValue(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-version"));
        if (msgTypeVersion!=null && !msgTypeVersion.isEmpty() && msgTypeID !=null && !msgTypeID.isEmpty()) {

        } else {
            throw new TMDDMissingInformationError("The message received did not contain correct message-type-id and message-type-version values for the dlEventIndexRequest dialog.");
        }
        
    }

    @Override
    protected MessageSpecification generateResponse(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, EntityCommandProcessor commandProcessor, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg = TMDDMessageProcessor.processEventRequestMsg(requestMessage, entityDataType, dataCollector, false);
        return responseMsg;
    }

}