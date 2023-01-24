/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.dms.dialogs;

import java.util.ArrayList;
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
public class DlDMSMessageAppearanceRequest extends DialogProcessor {

    public DlDMSMessageAppearanceRequest() {
        this.setDialogName("dlDMSMessageAppearanceRequest");
        this.setIsPublication(false);
    }

    @Override
    protected void preprocessing(EntityEmulationRequests.EntityRequestMessageType requestMessageType, MessageSpecification requestMessage) throws TMDDCenterDoesNotSupportMessageError, TMDDMissingInformationError {
        if (!requestMessage.containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEAPPEARANCEREQUESTMSG.relatedMessage())) {
            throw new TMDDCenterDoesNotSupportMessageError("The message received did not contain the correct message type for the dlDMSMessageAppearanceRequest dialog.");
        }

        // device-id test
        ArrayList<String> deviceIdFilterValues = requestMessage.getValuesOfInstances("tmdd:dMSMessageAppearanceRequestMsg.device-information-request-header.device-filter.device-id-list[*].device-id");
        if (deviceIdFilterValues.size() == 0) {           
            throw new TMDDMissingInformationError("The message received must specify one device id.");
         } else if (deviceIdFilterValues.size() > 1){
            throw new TMDDMissingInformationError("The message received must specify only one device id.");
        }
        
    }

    @Override
    protected MessageSpecification generateResponse(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, EntityCommandProcessor commandProcessor, boolean isPublication) throws NoMatchingDataException, EntityEmulationException {

        MessageSpecification responseMsg = TMDDMessageProcessor.processDMSMessageAppearanceRequestMsg(requestMessage, entityDataType, dataCollector, false);
        return responseMsg;
    }

}