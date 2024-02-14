/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.TMDDAuthenticationProcessor;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandProcessor;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.exceptions.MessageAuthenticationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NRTMViolationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.tmdd.errorcodes.TMDDCenterDoesNotSupportMessageError;
import org.fhwa.c2cri.tmdd.errorcodes.TMDDMissingInformationError;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 *
 * @author TransCore ITS, LLC Created: Mar 26, 2016
 */
public abstract class DialogProcessor {
    private String dialogName;
    private boolean isPublication;
    
    public final MessageSpecification handle(MessageSpecification requestMessage, EntityEmulationRequests.EntityRequestMessageType requestMessageType, TMDDEntityType.EntityType entityType, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, EntityCommandProcessor commandProcessor) {
        MessageSpecification responseMsg = null;
        try {
            preprocessing(requestMessageType, requestMessage);
            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialogName, requestMessage, entityType);

            // Verify that the request message satisfies any project Authentication requirements.
        
            String userName = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_AUTHENTICATIONUSERID(), TMDDSettingsImpl.getTMDD_AUTHENTICATIONUSERID_DEFAULT_VALUE());
            String password = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_AUTHENTICATIONPASSWORD(), TMDDSettingsImpl.getTMDD_AUTHENTICATIONPASSWORD_DEFAULT_VALUE());
            String operatorId = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_AUTHENTICATIONOPERATORID(), TMDDSettingsImpl.getTMDD_AUTHENTICATIONOPERATORID_DEFAULT_VALUE());
            TMDDAuthenticationProcessor.getInstance(userName, password, operatorId);
            
            TMDDAuthenticationProcessor.getInstance().processRequestMessageAuthentication(dialogName, requestMessage, entityType);
            
            responseMsg = generateResponse(requestMessage, entityDataType, dataCollector, commandProcessor, isPublication);                    
            
        } catch (TMDDCenterDoesNotSupportMessageError ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", ex.getErrorCode(), ex.getMessage());
        } catch (TMDDMissingInformationError ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", ex.getErrorCode(), ex.getMessage());
        } catch (NRTMViolationException ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", ex.getMessage());
        } catch (MessageAuthenticationException ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "authentication not recognized", ex.getMessage());
        } catch (NoMatchingDataException ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "no valid data available", ex.getMessage());
        } catch (EntityEmulationException ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
        }

        /**
         * Verify that the response message conforms to the project NRTM
         * specification.
         */
        try {
            TMDDNRTMSelections.getInstance().verifyMessageNRTM(dialogName, responseMsg, entityType);
        } catch (NRTMViolationException ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
        } catch (EntityEmulationException ex) {
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            responseMsg = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "other", ex.getMessage());
        }

        return responseMsg;
    }

    protected void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    protected void setIsPublication(boolean isPublication) {
        this.isPublication = isPublication;
    }        
    
    protected abstract void preprocessing(EntityEmulationRequests.EntityRequestMessageType requestMessageType, MessageSpecification requestMessage) throws TMDDCenterDoesNotSupportMessageError, TMDDMissingInformationError;

    protected abstract MessageSpecification generateResponse(MessageSpecification requestMessage, EntityEmulationData.EntityDataType entityDataType, EntityDataInformationCollector dataCollector, EntityCommandProcessor commandProcessor, boolean isPublication) throws NoMatchingDataException, EntityEmulationException;   
    
}
