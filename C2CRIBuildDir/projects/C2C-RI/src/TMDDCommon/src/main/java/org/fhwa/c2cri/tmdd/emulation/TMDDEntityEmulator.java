/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.centermodel.RIEmulator;
import org.fhwa.c2cri.centermodel.RINRTMSelection;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.dbase.TMDDConnectionPool;
import org.fhwa.c2cri.tmdd.emulation.cav.CenterActiveVerificationHandler;
import org.fhwa.c2cri.tmdd.emulation.cctv.CCTVHandler;
import org.fhwa.c2cri.tmdd.emulation.detector.DetectorHandler;
import org.fhwa.c2cri.tmdd.emulation.dms.DMSHandler;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityCommandQueue;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestLog;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityControlRequestStatus;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataOrganizationInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.CCTVINVENTORY;
import static org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData.EntityDataType.CCTVSTATUS;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationRequests;
import org.fhwa.c2cri.tmdd.emulation.ess.ESSHandler;
import org.fhwa.c2cri.tmdd.emulation.events.EventsHandler;
import org.fhwa.c2cri.tmdd.emulation.exceptions.DuplicateEntityIdException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityContentException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityElementException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityIdValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidMessageTypeException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;
import org.fhwa.c2cri.tmdd.emulation.gate.GateHandler;
import org.fhwa.c2cri.tmdd.emulation.generic.GenericDeviceHandler;
import org.fhwa.c2cri.tmdd.emulation.har.HARHandler;
import org.fhwa.c2cri.tmdd.emulation.intersection.IntersectionSignalHandler;
import org.fhwa.c2cri.tmdd.emulation.lcs.LCSHandler;
import org.fhwa.c2cri.tmdd.emulation.link.LinkHandler;
import org.fhwa.c2cri.tmdd.emulation.node.NodeHandler;
import org.fhwa.c2cri.tmdd.emulation.ramp.RampMeterHandler;
import org.fhwa.c2cri.tmdd.emulation.section.SectionHandler;
import org.fhwa.c2cri.tmdd.emulation.videoswitch.VideoSwitchHandler;
import org.fhwa.c2cri.tmdd.interfaces.ntcip2306.NTCIP2306XMLValidator;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public abstract class TMDDEntityEmulator
        implements EntityDataOrganizationInformationCollector,
                   RIEmulator
{

    private CCTVHandler cctvHandler;
    private CenterActiveVerificationHandler centerActiveVerificationHandler;
    private GenericDeviceHandler genericDeviceHandler;
    private DetectorHandler detectorHandler;
    private EventsHandler eventsHandler;
    private DMSHandler dmsHandler;
    private ESSHandler essHandler;
    private GateHandler gateHandler;
    private HARHandler harHandler;
    private IntersectionSignalHandler intersectionSignalHandler;
    private LCSHandler lcsHandler;
    private LinkHandler linkHandler;
    private NodeHandler nodeHandler;
    private RampMeterHandler rampMeterHandler;
    private SectionHandler sectionHandler;
    private VideoSwitchHandler videoSwitchHandler;

    
    protected abstract String getTMDDEmulatorStandard();
    protected abstract String getDatabaseFileName();
    protected abstract InputStream getDatabaseStream();
    protected abstract String getTMDDSchemaDetailTableName();
    
    @Override
    public void initialize(RIEmulationParameters emulationParameters) throws EntityEmulationException
    {
        try{
        File f = new File(getDatabaseFileName());
        if (!f.exists())
        {
            // Store a copy of the TMDD database to disk for later use
            BufferedInputStream inputStream = new BufferedInputStream(getDatabaseStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        }
        TMDDConnectionPool.Initialize(getDatabaseFileName());        
        } catch (Exception ex){
            throw new EntityEmulationException("Unable to configure Emulation Database: "+ex.getMessage());
        }
        
        TMDDMessageProcessor.setVersion(getTMDDEmulatorStandard());
        
        EntityEmulationData.setTMDDSchemaTableName(getTMDDSchemaDetailTableName());
        this.cctvHandler = new CCTVHandler(this);
        this.centerActiveVerificationHandler = new CenterActiveVerificationHandler(this);
        this.detectorHandler = new DetectorHandler(this);
        this.eventsHandler = new EventsHandler(this);
        this.dmsHandler = new DMSHandler(this);
        this.essHandler = new ESSHandler(this);
        this.gateHandler = new GateHandler(this);
        this.harHandler = new HARHandler(this);
        this.intersectionSignalHandler = new IntersectionSignalHandler(this);
        this.lcsHandler = new LCSHandler(this);
        this.linkHandler = new LinkHandler(this);
        this.nodeHandler = new NodeHandler(this);
        this.rampMeterHandler = new RampMeterHandler(this);
        this.sectionHandler = new SectionHandler(this);
        this.videoSwitchHandler = new VideoSwitchHandler(this);

        for (RIEmulationEntityValueSet emulationValueSet: emulationParameters.getEntityDataMap())
        {
            long startTime = System.currentTimeMillis();
            switch (EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    cctvHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case CENTERACTIVEVERIFICATION:
                    centerActiveVerificationHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORMAINTENANCEHISTORY:
                case DETECTORSTATUS:
                    detectorHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;

                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    eventsHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;

                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    dmsHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    essHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    gateHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    harHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionSignalHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    lcsHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    linkHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    nodeHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    rampMeterHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    sectionHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    videoSwitchHandler.initialize(EntityEmulationData.EntityDataType.valueOf(emulationValueSet.getValueSetName()), emulationValueSet.getEntityDataSet());
                    break;
                default:
                    throw new EntityEmulationException("Unable to initialize data type " + emulationValueSet.getValueSetName() + ".  It is not valid in "+getTMDDEmulatorStandard()+".");
            }
            System.out.println("Initializing " + emulationValueSet.getValueSetName() + " took " + (System.currentTimeMillis() - startTime) + " ms.");
        }

        // Initialize the NRTM Selections that will be used for message generation and confirmation.
        TMDDNRTMSelections nrtmSelections = TMDDNRTMSelections.getInstance();
        nrtmSelections.initialize();

        for (RINRTMSelection entry: RIEmulation.getInstance().getNRTMSelections())
            nrtmSelections.addNRTM(entry.getNeedId(), entry.getRequirementId());

        this.genericDeviceHandler = new GenericDeviceHandler(this);
        EntityControlRequestStatus.initialize();
        EntityControlRequestLog.initialize();
        EntityCommandQueue.getInstance().initialize(emulationParameters.getCommandQueueLength());
    }

    @Override
    public Message getRRResponse(String dialog, Message requestMessage) throws EntityEmulationException, InvalidMessageTypeException
    {
        Message responseMsg = null;
        long startTime = System.currentTimeMillis();
        switch (dialog)
        {
            case "dlCCTVControlRequest":
            case "dlCCTVInventoryRequest":
            case "dlCCTVStatusRequest":
                MessageSpecification responseSpec = cctvHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(responseSpec);
                break;

            case "dlCenterActiveVerificationRequest":
                MessageSpecification cavResponseSpec = centerActiveVerificationHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(cavResponseSpec);
                break;

            case "dlDeviceCancelControlRequest":
            case "dlDeviceControlStatusRequest":
                MessageSpecification statusResponseSpec = genericDeviceHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(statusResponseSpec);
                break;

            case "dlArchivedDataProcessingDocumentationMetadataRequest":
            case "dlDetectorDataRequest":
            case "dlDetectorInventoryRequest":
            case "dlDetectorMaintenanceHistoryRequest":
            case "dlDetectorStatusRequest":
                MessageSpecification detectorResponseSpec = detectorHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(detectorResponseSpec);
                break;

            case "dlDMSControlRequest":
            case "dlDMSFontTableRequest":
            case "dlDMSInventoryRequest":
            case "dlDMSMessageAppearanceRequest":
            case "dlDMSMessageInventoryRequest":
            case "dlDMSPriorityQueueRequest":
            case "dlDMSStatusRequest":
                MessageSpecification dmsResponseSpec = dmsHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(dmsResponseSpec);
                break;
            case "dlESSInventoryRequest":
            case "dlESSObservationMetadataRequest":
            case "dlESSObservationReportRequest":
            case "dlESSStatusRequest":
                MessageSpecification essResponseSpec = essHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(essResponseSpec);
                break;
            case "dlActionLogRequest":
            case "dlEventIndexRequest":
            case "dlFullEventUpdateRequest":
                MessageSpecification eventResponseSpec = eventsHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(eventResponseSpec);
                break;
            case "dlGateControlRequest":
            case "dlGateControlScheduleRequest":
            case "dlGateInventoryRequest":
            case "dlGateStatusRequest":
                MessageSpecification gateResponseSpec = gateHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(gateResponseSpec);
                break;
            case "dlHARControlRequest":
            case "dlHARControlScheduleRequest":
            case "dlHARInventoryRequest":
            case "dlHARMessageInventoryRequest":
            case "dlHARPriorityQueueRequest":
            case "dlHARStatusRequest":
                MessageSpecification harResponseSpec = harHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(harResponseSpec);
                break;
            case "dlIntersectionSignalControlRequest":
            case "dlIntersectionSignalControlScheduleRequest":
            case "dlIntersectionSignalInventoryRequest":
            case "dlIntersectionSignalPriorityQueueRequest":
            case "dlIntersectionSignalStatusRequest":
            case "dlIntersectionSignalTimingPatternInventoryRequest":
                MessageSpecification signalResponseSpec = intersectionSignalHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(signalResponseSpec);
                break;
            case "dlLCSControlRequest":
            case "dlLCSControlScheduleRequest":
            case "dlLCSInventoryRequest":
            case "dlLCSStatusRequest":
                MessageSpecification lcsResponseSpec = lcsHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(lcsResponseSpec);
                break;
            case "dlLinkInventoryRequest":
            case "dlLinkStatusRequest":
                MessageSpecification linkResponseSpec = linkHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(linkResponseSpec);
                break;
            case "dlNodeInventoryRequest":
            case "dlNodeStatusRequest":
                MessageSpecification nodeResponseSpec = nodeHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(nodeResponseSpec);
                break;
            case "dlRampMeterControlRequest":
            case "dlRampMeterControlScheduleRequest":
            case "dlRampMeterInventoryRequest":
            case "dlRampMeterPlanInventoryRequest":
            case "dlRampMeterPriorityQueueRequest":
            case "dlRampMeterStatusRequest":
                MessageSpecification rampResponseSpec = rampMeterHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(rampResponseSpec);
                break;
            case "dlSectionControlRequest":
            case "dlSectionControlScheduleRequest":
            case "dlSectionControlStatusRequest":
            case "dlSectionPriorityQueueRequest":
            case "dlSectionStatusRequest":
            case "dlSectionSignalTimingPatternInventoryRequest":
                MessageSpecification sectionResponseSpec = sectionHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(sectionResponseSpec);
                break;
            case "dlVideoSwitchControlRequest":
            case "dlVideoSwitchInventoryRequest":
            case "dlVideoSwitchStatusRequest":
                MessageSpecification switchResponseSpec = videoSwitchHandler.getRRResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(switchResponseSpec);
                break;
            default:
                throw new EntityEmulationException("Dialog " + dialog + " is not currently supported.");
//                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                responseMsg = errorProcessor.getErrorResponseMsg("errorReportMsg", "other","The "+dialog + " is not supported in TMDD Entity Emulation.");
        }

//        String messageType = "";
//
//        switch (requestMessage.getMessageSpecification().getMessageTypes().size()) {
//            case 1:
//                messageType = requestMessage.getMessageSpecification().getMessageTypes().get(0);
//                break;
//        // process the request message
//            case 2:
//                messageType = requestMessage.getMessageSpecification().getMessageTypes().get(1);
//                break;
//            default:
//                throw new InvalidMessageTypeException("No valid message request was received.");
//        }
//         // process the message based on its type.
//        if(messageType.equals(EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage())){
//            String networkInformationType = (requestMessage.getMessageSpecification().getElementValue(EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "."+"device-type"));
//            String deviceInformationType = (requestMessage.getMessageSpecification().getElementValue(EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "."+"device-information-type"));
//
//            // CCTV Inventory
//            if ((networkInformationType.equals("2")||networkInformationType.equals("cctv camera"))&&
//                 deviceInformationType.equals("1")||deviceInformationType.equals("device inventory")){
//                 responseMsg = MessageManager.getInstance().createMessage(dialog);
//                 responseMsg.setMessageSpecification(cctvHandler.getRRResponse(requestMessage.getMessageSpecification()));
//
//            // Implement Other Device and Information Types here
//            } else {
//                throw new InvalidMessageTypeException("No valid message request was received.");
//            }
//
//        // Implement Other Message Types Here
//
//        } else {
//                throw new InvalidMessageTypeException(messageType + " is not a valid message type.");
//        }
        // Process information message
        // get Filters
        // Process Control Message
        if (requestMessage.getMessageBody() != null)
            System.out.println("Received Message" + (requestMessage.getMessageType() != null ? " " + requestMessage.getMessageType() : "") + " was " + requestMessage.getMessageBody().length + " bytes and took approximately " + (System.currentTimeMillis() - startTime) + " ms.");
        return responseMsg;
    }

    @Override
    public Message getSubResponse(String dialog, Message requestMessage) throws EntityEmulationException, InvalidMessageTypeException
    {
        long startTime = System.currentTimeMillis();
        Message responseMsg = null;

        switch (dialog)
        {
            case "dlCenterActiveVerificationSubscription":
                MessageSpecification cavResponseSpec = centerActiveVerificationHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(cavResponseSpec);
                break;
            case "dlDeviceInformationSubscription":
                // First determine if it has the appropriate request message.
                if (requestMessage.getMessageSpecification().containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage()))
                {
                    String deviceType = (requestMessage.getMessageSpecification().getElementValue(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type"));
                    switch (deviceType)
                    {
                        case "1":
                        case "detector":
                            MessageSpecification responseSpec = detectorHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(responseSpec);
                            break;
                        case "2":
                        case "cctv camera":
                            MessageSpecification cctvResponseSpec = cctvHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(cctvResponseSpec);
                            break;
                        case "3":
                        case "dynamic message sign":
                            MessageSpecification dmsResponseSpec = dmsHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(dmsResponseSpec);
                            break;
                        case "4":
                        case "environmental sensor station":
                            MessageSpecification essResponseSpec = essHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(essResponseSpec);
                            break;
                        case "5":
                        case "gate":
                            MessageSpecification gateResponseSpec = gateHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(gateResponseSpec);
                            break;
                        case "6":
                        case "highway advisory radio":
                            MessageSpecification harResponseSpec = harHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(harResponseSpec);
                            break;
                        case "7":
                        case "lane control signal":
                            MessageSpecification lcsResponseSpec = lcsHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(lcsResponseSpec);
                            break;
                        case "8":
                        case "ramp meter":
                            MessageSpecification rampResponseSpec = rampMeterHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(rampResponseSpec);
                            break;
                        case "9":
                        case "signal controller":
                            MessageSpecification signalResponseSpec = intersectionSignalHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(signalResponseSpec);
                            break;
                        case "10":
                        case "signal section":
                            MessageSpecification sectionResponseSpec = sectionHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(sectionResponseSpec);
                            break;
                        case "11":
                        case "video switch":
                            MessageSpecification switchResponseSpec = videoSwitchHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(switchResponseSpec);
                            break;
                        default:
                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                            MessageSpecification responseMsgSpec = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "RI Emulation does not support the dlDeviceInformationSubscription dialog with DeviceInformationRequestMsg device-type set to " + deviceType + ".");
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(responseMsgSpec);
                    }
                }
                else
                {
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = MessageManager.getInstance().createMessage(dialog);
                    responseMsg.setMessageSpecification(errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDeviceInformationSubscription dialog."));
                }

                // If so, then determine (if possible) which device type this subscription is related to.
                // forward the subscription request to the proper handler.
                break;
            case "dlDetectorDataSubscription":
                // First determine if it has the appropriate request message.
                if (requestMessage.getMessageSpecification().containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage()))
                {
                    MessageSpecification responseSpec = detectorHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                    responseMsg = MessageManager.getInstance().createMessage(dialog);
                    responseMsg.setMessageSpecification(responseSpec);
                }
                else
                {
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = MessageManager.getInstance().createMessage(dialog);
                    responseMsg.setMessageSpecification(errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDeviceInformationSubscription dialog."));
                }

                // If so, then determine (if possible) which device type this subscription is related to.
                // forward the subscription request to the proper handler.
                break;

            case "dlDMSMessageInventorySubscription":
                MessageSpecification responseSpec = dmsHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(responseSpec);
                break;
            case "dlActionLogSubscription":
            case "dlEventIndexSubscription":
            case "dlFullEventUpdateSubscription":
                MessageSpecification eventResponseSpec = eventsHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(eventResponseSpec);
                break;
            case "dlIntersectionSignalTimingPatternInventorySubscription":
                MessageSpecification signalResponseSpec = intersectionSignalHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(signalResponseSpec);
                break;
            case "dlRampMeterPlanInventorySubscription":
                MessageSpecification rampResponseSpec = rampMeterHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(rampResponseSpec);
                break;
            case "dlSectionSignalTimingPatternInventorySubscription":
                MessageSpecification sectionResponseSpec = sectionHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(sectionResponseSpec);
                break;
            case "dlTrafficNetworkInformationSubscription":
                // First determine if it has the appropriate request message.
                if (requestMessage.getMessageSpecification().containsMessageOfType(EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG.relatedMessage()))
                {
                    String networkInformationType = (requestMessage.getMessageSpecification().getElementValue(EntityEmulationRequests.EntityRequestMessageType.TRAFFICNETWORKINFORMATIONREQUESTMSG.relatedMessage() + "." + "network-information-type"));
                    switch (networkInformationType)
                    {
                        case "1":
                        case "node inventory":
                            MessageSpecification nodeInvResponseSpec = nodeHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(nodeInvResponseSpec);
                            break;
                        case "2":
                        case "node status":
                            MessageSpecification nodeStatResponseSpec = nodeHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(nodeStatResponseSpec);
                            break;
                        case "3":
                        case "link inventory":
                            MessageSpecification linkInvResponseSpec = linkHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(linkInvResponseSpec);
                            break;
                        case "4":
                        case "link status":
                            MessageSpecification linkStatResponseSpec = linkHandler.getSubResponse(dialog, requestMessage.getMessageSpecification());
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(linkStatResponseSpec);
                            break;
                        // If the request is not for link or node information do not set the response message.  The caller should interpret this as
                        // an indicator that the message is not supported by the emulator.
                        case "5":
                        case "route inventory":
                        case "6":
                        case "route status":
                        case "7":
                        case "network inventory":
                        case "8":
                        case "other":
                            break;
                        default:
                            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                            MessageSpecification responseMsgSpec = errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "missing information prevents processing message", "RI Emulation does not support the dlTrafficNetworkInformationSubscription dialog with TrafficNetworkInformationRequestMsg network-information-type set to " + networkInformationType + ".");
                            responseMsg = MessageManager.getInstance().createMessage(dialog);
                            responseMsg.setMessageSpecification(responseMsgSpec);
                    }
                }
                else
                {
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    responseMsg = MessageManager.getInstance().createMessage(dialog);
                    responseMsg.setMessageSpecification(errorProcessor.getErrorResponseMsgSpec("errorReportMsg", "center does not support this type message", "The message received did not contain the correct message type for the dlDeviceInformationSubscription dialog."));
                }
                break;
            default:
                throw new EntityEmulationException("Dialog " + dialog + " is not currently supported.");
//                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                responseMsg = errorProcessor.getErrorResponseMsg("errorReportMsg", "other","The "+dialog + " is not supported in TMDD Entity Emulation.");
        }

        if (requestMessage.getMessageBody() != null)
            System.out.println("Received Message " + (requestMessage.getMessageType() != null ? requestMessage.getMessageType() : "") + " was " + requestMessage.getMessageBody().length + " bytes and took approximately " + (System.currentTimeMillis() - startTime) + " ms.");
        return responseMsg;
    }

    @Override
    public Message getPubResponse(String dialog, Message requestMessage) throws EntityEmulationException, InvalidMessageTypeException
    {
        Message responseMsg = null;

        switch (dialog)
        {
            case "dlCCTVInventoryUpdate":
            case "dlCCTVStatusUpdate":
                MessageSpecification responseSpec = cctvHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(responseSpec);
                break;
            case "dlCenterActiveVerificationUpdate":
                MessageSpecification cavResponseSpec = centerActiveVerificationHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(cavResponseSpec);
                break;
            case "dlDetectorDataUpdate":
            case "dlDetectorInventoryUpdate":
            case "dlDetectorStatusUpdate":
                MessageSpecification detectorDataResponseSpec = detectorHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(detectorDataResponseSpec);
                break;
            case "dlActionLogUpdate":
            case "dlEventIndexUpdate":
            case "dlFullEventUpdateUpdate":
                MessageSpecification eventsDataResponseSpec = eventsHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(eventsDataResponseSpec);
                break;
            case "dlDMSInventoryUpdate":
            case "dlDMSMessageInventoryUpdate":
            case "dlDMSStatusUpdate":
                MessageSpecification dmsDataResponseSpec = dmsHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(dmsDataResponseSpec);
                break;
            case "dlESSInventoryUpdate":
            case "dlESSObservationReportUpdate":
            case "dlESSStatusUpdate":
                MessageSpecification essDataResponseSpec = essHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(essDataResponseSpec);
                break;
            case "dlGateControlScheduleUpdate":
            case "dlGateInventoryUpdate":
            case "dlGateStatusUpdate":
                MessageSpecification gateDataResponseSpec = gateHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(gateDataResponseSpec);
                break;
            case "dlHARControlScheduleUpdate":
            case "dlHARInventoryUpdate":
            case "dlHARMessageInventoryUpdate":
            case "dlHARStatusUpdate":
                MessageSpecification harDataResponseSpec = harHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(harDataResponseSpec);
                break;
            case "dlIntersectionSignalControlScheduleUpdate":
            case "dlIntersectionSignalInventoryUpdate":
            case "dlIntersectionSignalStatusUpdate":
            case "dlIntersectionSignalTimingPatternInventoryUpdate":
                MessageSpecification intDataResponseSpec = intersectionSignalHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(intDataResponseSpec);
                break;
            case "dlLCSControlScheduleUpdate":
            case "dlLCSInventoryUpdate":
            case "dlLCSStatusUpdate":
                MessageSpecification lcsDataResponseSpec = lcsHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(lcsDataResponseSpec);
                break;
            case "dlLinkInventoryUpdate":
            case "dlLinkStatusUpdate":
                MessageSpecification linkDataResponseSpec = linkHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(linkDataResponseSpec);
                break;
            case "dlNodeInventoryUpdate":
            case "dlNodeStatusUpdate":
                MessageSpecification nodeDataResponseSpec = nodeHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(nodeDataResponseSpec);
                break;
            case "dlRampMeterControlScheduleUpdate":
            case "dlRampMeterInventoryUpdate":
            case "dlRampMeterPlanInventoryUpdate":
            case "dlRampMeterStatusUpdate":
                MessageSpecification rampDataResponseSpec = rampMeterHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(rampDataResponseSpec);
                break;
            case "dlSectionControlScheduleUpdate":
            case "dlSectionStatusUpdate":
            case "dlSectionSignalTimingPatternInventoryUpdate":
                MessageSpecification sectionDataResponseSpec = sectionHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(sectionDataResponseSpec);
                break;
            case "dlVideoSwitchInventoryUpdate":
            case "dlVideoSwitchStatusUpdate":
                MessageSpecification videoSwitchDataResponseSpec = videoSwitchHandler.getPubResponse(dialog, requestMessage.getMessageSpecification());
                responseMsg = MessageManager.getInstance().createMessage(dialog);
                responseMsg.setMessageSpecification(videoSwitchDataResponseSpec);
                break;
            default:
                throw new EntityEmulationException("Dialog " + dialog + " is not currently supported.");
//                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
//                responseMsg = errorProcessor.getErrorResponseMsg("errorReportMsg", "other","The "+dialog + " is not supported in TMDD Entity Emulation.");
        }

        return responseMsg;
    }

    /**
     * Add a new entity to the set using the provided entityId. The elements
     * which make up the entity are pulled from the default entity definition.
     * The entityID will be used for the device-id value.
     * <p>
     * Each CCTV-Inventory-Item is assigned an item index. This index is used to
     * indicate the relative order of the CCTV Inventory items.
     * <p>
     * Checks: If entityID already exists throw an exception instead of adding
     * it. If the entityId is not valid (null or too long) throw an exception
     * instead of adding it.
     *
     * @param entityId
     *
     * @throws org.fhwa.c2cri.centermodel.exceptions.DuplicateEntityIdException
     * @throws
     * org.fhwa.c2cri.centermodel.exceptions.InvalidEntityIdValueException
     */
    @Override
    public void addEntity(String entityDataType, String entityId) throws DuplicateEntityIdException, InvalidEntityIdValueException
    {
        // check the Entity Id Value against the current entity information and requirements for the entity ID field
        try
        {
            switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    cctvHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case CENTERACTIVEVERIFICATION:
                    centerActiveVerificationHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORSTATUS:
                case DETECTORMAINTENANCEHISTORY:
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    detectorHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    eventsHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    dmsHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    essHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    gateHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    harHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionSignalHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    lcsHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    linkHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    nodeHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    rampMeterHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    sectionHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    videoSwitchHandler.addEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                default:
                    throw new EntityEmulationException("Unable to process data type " + entityDataType);
            }

            // Notify Listeners for the data type
        }
        catch (InvalidValueException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }
        catch (DuplicateEntityIdException ex)
        {
            throw ex;
        }
        catch (EntityEmulationException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }
    }

    @Override
    public void addEntityElement(String entityDataType, String entityId, String elementName, String elementValue) throws InvalidEntityIdValueException, InvalidValueException
    {
        try
        {
            switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    cctvHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case CENTERACTIVEVERIFICATION:
                    centerActiveVerificationHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORSTATUS:
                case DETECTORMAINTENANCEHISTORY:
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    detectorHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    eventsHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    dmsHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    essHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    gateHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    harHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionSignalHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    lcsHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    linkHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    nodeHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    rampMeterHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    sectionHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    videoSwitchHandler.addEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, elementName, elementValue);
                    break;
                default:
                    throw new EntityEmulationException("Unable to process data type " + entityDataType);
            }
        }
        catch (EntityEmulationException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

    }

    @Override
    public void deleteEntity(String entityDataType, String entityId) throws InvalidEntityIdValueException
    {
        try
        {
            switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    cctvHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case CENTERACTIVEVERIFICATION:
                    centerActiveVerificationHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORSTATUS:
                case DETECTORMAINTENANCEHISTORY:
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    detectorHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    eventsHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    dmsHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    essHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    gateHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    harHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionSignalHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    lcsHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    linkHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    nodeHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    rampMeterHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    sectionHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    videoSwitchHandler.deleteEntity(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId);
                    break;
                default:
                    throw new EntityEmulationException("The CCTVHandler is unable to process data type " + entityDataType);
            }
        }
        catch (InvalidValueException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }
        catch (EntityEmulationException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

    }

    @Override
    public void updateEntityElement(String entityDataType, String entityId, String entityElementName, String entityElementValue) throws InvalidEntityIdValueException, InvalidValueException
    {
        try
        {
            switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    cctvHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case CENTERACTIVEVERIFICATION:
                    centerActiveVerificationHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORSTATUS:
                case DETECTORMAINTENANCEHISTORY:
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    detectorHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    eventsHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    dmsHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    essHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    gateHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    harHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionSignalHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    lcsHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    linkHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    nodeHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    rampMeterHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    sectionHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    videoSwitchHandler.updateEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName, entityElementValue);
                    break;
                default:
                    throw new EntityEmulationException("Unable to process data type " + entityDataType);
            }
        }
        catch (EntityEmulationException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

    }

    @Override
    public void deleteEntityElement(String entityDataType, String entityId, String entityElementName) throws InvalidEntityIdValueException, InvalidEntityElementException
    {
        try
        {
            switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    cctvHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case CENTERACTIVEVERIFICATION:
                    centerActiveVerificationHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORSTATUS:
                case DETECTORMAINTENANCEHISTORY:
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    detectorHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    eventsHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    dmsHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    essHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    gateHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    harHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    intersectionSignalHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    lcsHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    linkHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    nodeHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    rampMeterHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    sectionHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    videoSwitchHandler.deleteEntityElement(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElementName);
                    break;
                default:
                    throw new EntityEmulationException("Unable to process data type " + entityDataType);
            }
        }
        catch (EntityEmulationException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

    }

    @Override
    public String getEntityElementValue(String entityDataType, String entityId, String entityElement) throws InvalidEntityIdValueException, InvalidEntityElementException, EntityEmulationException
    {
        String entityValue = "";
        try
        {
            switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
            {
                case CCTVINVENTORY:
                case CCTVSTATUS:
                    entityValue = cctvHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case CENTERACTIVEVERIFICATION:
                    entityValue = centerActiveVerificationHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case DETECTORDATA:
                case DETECTORINVENTORY:
                case DETECTORSTATUS:
                case DETECTORMAINTENANCEHISTORY:
                case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                    entityValue = detectorHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case EVENTSACTIONLOG:
                case EVENTSINDEX:
                case EVENTSUPDATES:
                    entityValue = eventsHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case DMSFONTTABLE:
                case DMSINVENTORY:
                case DMSMESSAGEAPPEARANCE:
                case DMSMESSAGEINVENTORY:
                case DMSSTATUS:
                    entityValue = dmsHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case ESSINVENTORY:
                case ESSOBSERVATIONMETADATA:
                case ESSOBSERVATIONREPORT:
                case ESSSTATUS:
                    entityValue = essHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case GATECONTROLSCHEDULE:
                case GATEINVENTORY:
                case GATESTATUS:
                    entityValue = gateHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case HARCONTROLSCHEDULE:
                case HARINVENTORY:
                case HARMESSAGEINVENTORY:
                case HARSTATUS:
                    entityValue = harHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case INTERSECTIONSIGNALCONTROLSCHEDULE:
                case INTERSECTIONSIGNALINVENTORY:
                case INTERSECTIONSIGNALSTATUS:
                case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
                    entityValue = intersectionSignalHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case LCSCONTROLSCHEDULE:
                case LCSINVENTORY:
                case LCSSTATUS:
                    entityValue = lcsHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case LINKINVENTORY:
                case LINKSTATUS:
                    entityValue = linkHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case NODEINVENTORY:
                case NODESTATUS:
                    entityValue = nodeHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case RAMPMETERCONTROLSCHEDULE:
                case RAMPMETERINVENTORY:
                case RAMPMETERPLANINVENTORY:
                case RAMPMETERSTATUS:
                    entityValue = rampMeterHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case SECTIONCONTROLSCHEDULE:
                case SECTIONSIGNALTIMINGPATTERNINVENTORY:
                case SECTIONSTATUS:
                    entityValue = sectionHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                case VIDEOSWITCHINVENTORY:
                case VIDEOSWITCHSTATUS:
                    entityValue = videoSwitchHandler.getEntityElementValue(EntityEmulationData.EntityDataType.valueOf(entityDataType), entityId, entityElement);
                    break;
                default:
                    throw new EntityEmulationException("The TMDDEntityEmulator is unable to process data type " + entityDataType);
            }
        }
        catch (EntityEmulationException ex)
        {
            throw new InvalidEntityIdValueException(ex.getMessage());
        }

        return entityValue;
    }

    @Override
    public MessageSpecification getEntityOrganizationInformation(TMDDEntityType.EntityType entityType, String entityId) throws NoMatchingDataException, EntityEmulationException
    {
        MessageSpecification results = null;
        switch (entityType)
        {
            case CCTV:
                results = cctvHandler.getEntityOrganizationInformation(entityId);
                break;
            case CENTERACTIVEVERIFICATION:
                results = centerActiveVerificationHandler.getEntityOrganizationInformation(entityId);
                break;
            case DETECTOR:
                results = detectorHandler.getEntityOrganizationInformation(entityId);
                break;
            case DMS:
                results = dmsHandler.getEntityOrganizationInformation(entityId);
                break;
            case ESS:
                results = essHandler.getEntityOrganizationInformation(entityId);
                break;
            case EVENT:
                results = eventsHandler.getEntityOrganizationInformation(entityId);
                break;
            case GATES:
                results = gateHandler.getEntityOrganizationInformation(entityId);
                break;
            case HAR:
                results = harHandler.getEntityOrganizationInformation(entityId);
                break;
            case TRAFFICSIGNALCONTROLLER:
                results = intersectionSignalHandler.getEntityOrganizationInformation(entityId);
                break;
            case LCS:
                results = lcsHandler.getEntityOrganizationInformation(entityId);
                break;
            case LINK:
                results = linkHandler.getEntityOrganizationInformation(entityId);
                break;
            case NODE:
                results = nodeHandler.getEntityOrganizationInformation(entityId);
                break;
            case RAMPMETER:
                results = rampMeterHandler.getEntityOrganizationInformation(entityId);
                break;
            case SECTION:
                results = sectionHandler.getEntityOrganizationInformation(entityId);
                break;
            case VIDEOSWITCH:
                results = videoSwitchHandler.getEntityOrganizationInformation(entityId);
                break;
            default:
                throw new EntityEmulationException("The TMDDEntityEmulator is unable to get Organization Information for entity type " + entityType + ".");

        }
        return results;
    }

    @Override
    public String getEmulatorStandard()
    {
        return getTMDDEmulatorStandard(); //To change body of generated methods, choose Tools | Templates.
    }

   
    public void validate(String entityDataType) throws InvalidEntityContentException, EntityEmulationException, Exception
    {
        HashMap<String, String> nameSpaceMap = new HashMap();
        nameSpaceMap.put("tmdd", "http://www.tmdd.org/303/messages");
        nameSpaceMap.put("c2c", "http://www.ntcip.org/c2c-message-administration");
        nameSpaceMap.put("tmddX", "http://www.tmdd.org/X");

        Message responseMsg = null;

        MessageSpecification responseSpec = null;
        MessageSpecification requestMessageSpec;
        ArrayList<String> messageElements = new ArrayList();

        switch (EntityEmulationData.EntityDataType.valueOf(entityDataType))
        {
            case CCTVINVENTORY:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type = device inventory");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type = cctv camera");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = cctvHandler.getRRResponse("dlCCTVInventoryRequest", requestMessageSpec);
                break;

            case CCTVSTATUS:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type = device status");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type = cctv camera");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = cctvHandler.getRRResponse("dlCCTVStatusRequest", requestMessageSpec);
                break;

            case CENTERACTIVEVERIFICATION:
                break;

            case DETECTORDATA:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DETECTORDATAREQUESTMSG.relatedMessage() + "." + "device-information-request-header.device-information-type = device data");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DETECTORDATAREQUESTMSG.relatedMessage() + "." + "device-information-request-header.device-type = detector");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = detectorHandler.getRRResponse("dlDetectorDataRequest", requestMessageSpec);
                break;
            case DETECTORINVENTORY:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type = device inventory");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type = detector");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = detectorHandler.getRRResponse("dlDetectorInventoryRequest", requestMessageSpec);
                break;
            case DETECTORSTATUS:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type = device status");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type = detector");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = detectorHandler.getRRResponse("dlDetectorStatusRequest", requestMessageSpec);
                break;
            case DETECTORMAINTENANCEHISTORY:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DETECTORMAINTENANCEHISTORYREQUESTMSG.relatedMessage() + "." + "device-information-request-header.device-information-type = device maintenance history");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DETECTORMAINTENANCEHISTORYREQUESTMSG.relatedMessage() + "." + "device-information-request-header.device-type = detector");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = detectorHandler.getRRResponse("dlDetectorMaintenanceHistoryRequest", requestMessageSpec);
                break;
            case ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATA:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATAREQUESTMSG.relatedMessage() + "." + "organization-information.organization-id = orgId");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = detectorHandler.getRRResponse("dlArchivedDataProcessingDocumentationMetadataRequest", requestMessageSpec);
                break;
            case EVENTSACTIONLOG:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-id = typeid");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-version = 1");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = eventsHandler.getRRResponse("dlActionLogRequest", requestMessageSpec);
                break;
            case EVENTSINDEX:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-id = typeid");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-version = 1");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = eventsHandler.getRRResponse("dlEventIndexRequest", requestMessageSpec);
                break;
            case EVENTSUPDATES:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-id = typeid");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.EVENTREQUESTMSG.relatedMessage() + "." + "request-header.message-type-version = 1");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = eventsHandler.getRRResponse("dlFullEventUpdateRequest", requestMessageSpec);
                break;
            case DMSFONTTABLE:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DMSFONTTABLEREQUESTMSG.relatedMessage() + "." + "element = name");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = dmsHandler.getRRResponse("dlDMSFontTableRequest", requestMessageSpec);
                break;
            case DMSINVENTORY:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type = device inventory");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type = dynamic message sign");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = dmsHandler.getRRResponse("dlDMSInventoryRequest", requestMessageSpec);
                break;
            case DMSMESSAGEAPPEARANCE:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEAPPEARANCEREQUESTMSG.relatedMessage() + "." + "element = element");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = dmsHandler.getRRResponse("dlDMSMessageAppearanceRequest", requestMessageSpec);
                break;
            case DMSMESSAGEINVENTORY:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DMSMESSAGEINVENTORYREQUESTMSG.relatedMessage() + "." + "element = element");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = dmsHandler.getRRResponse("dlDMSMessageInventoryRequest", requestMessageSpec);
                break;
            case DMSSTATUS:
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-information-type = device status");
                messageElements.add(EntityEmulationRequests.EntityRequestMessageType.DEVICEINFORMATIONREQUESTMSG.relatedMessage() + "." + "device-type = dynamic message sign");
                requestMessageSpec = new MessageSpecification(messageElements);
                responseSpec = dmsHandler.getRRResponse("dlDMSStatusRequest", requestMessageSpec);
                break;
            case ESSINVENTORY:
            case ESSOBSERVATIONMETADATA:
            case ESSOBSERVATIONREPORT:
            case ESSSTATUS:
//            case "dlESSInventoryRequest":
//            case "dlESSObservationMetadataRequest":
//            case "dlESSObservationReportRequest":
//            case "dlESSStatusRequest":
                break;
            case GATECONTROLSCHEDULE:
            case GATEINVENTORY:
            case GATESTATUS:
//            case "dlGateControlScheduleRequest":
//            case "dlGateInventoryRequest":
//            case "dlGateStatusRequest":
                break;
            case HARCONTROLSCHEDULE:
            case HARINVENTORY:
            case HARMESSAGEINVENTORY:
            case HARSTATUS:
//            case "dlHARControlScheduleRequest":
//            case "dlHARInventoryRequest":
//            case "dlHARMessageInventoryRequest":
//            case "dlHARStatusRequest":
                break;
            case INTERSECTIONSIGNALCONTROLSCHEDULE:
            case INTERSECTIONSIGNALINVENTORY:
            case INTERSECTIONSIGNALSTATUS:
            case INTERSECTIONSIGNALTIMINGPATTERNINVENTORY:
//            case "dlIntersectionSignalControlScheduleRequest":
//            case "dlIntersectionSignalInventoryRequest":
//            case "dlIntersectionSignalStatusRequest":
//            case "dlIntersectionSignalTimingPatternInventoryRequest":
                break;
            case LCSCONTROLSCHEDULE:
            case LCSINVENTORY:
            case LCSSTATUS:
//            case "dlLCSControlScheduleRequest":
//            case "dlLCSInventoryRequest":
//            case "dlLCSStatusRequest":
                break;
            case LINKINVENTORY:
            case LINKSTATUS:
                break;
            case NODEINVENTORY:
            case NODESTATUS:
                break;                
            case RAMPMETERCONTROLSCHEDULE:
            case RAMPMETERINVENTORY:
            case RAMPMETERPLANINVENTORY:
            case RAMPMETERSTATUS:
//            case "dlRampMeterControlScheduleRequest":
//            case "dlRampMeterInventoryRequest":
//            case "dlRampMeterPlanInventoryRequest":
//            case "dlRampMeterStatusRequest":

                break;
            case SECTIONCONTROLSCHEDULE:
            case SECTIONSIGNALTIMINGPATTERNINVENTORY:
            case SECTIONSTATUS:
//            case "dlSectionControlScheduleRequest":
//            case "dlSectionStatusRequest":
//            case "dlSectionSignalTimingPatternInventoryRequest":
                break;
            case VIDEOSWITCHINVENTORY:
            case VIDEOSWITCHSTATUS:
//            case "dlVideoSwitchInventoryRequest":
//            case "dlVideoSwitchStatusRequest":
                break;
            default:
                throw new EntityEmulationException("Unable to process data type " + entityDataType);
        }

        if (responseSpec != null)
        {

            responseMsg = MessageManager.getInstance().createMessage("ENTITYTYPE");
            responseMsg.setMessageSpecification(responseSpec);
            C2CRIMessageAdapter.updateMessageFromSpec(responseMsg, nameSpaceMap);

            NTCIP2306XMLValidator xmlValidator = NTCIP2306XMLValidator.getInstance();
            boolean schemaValidated = false;
            if (responseMsg.getMessageBodyParts().size() == 1)
                schemaValidated = xmlValidator.isXMLValidatedToSchema(responseMsg.getMessageBodyParts().get(0));
            else if (responseMsg.getMessageBodyParts().size() >= 2)
                schemaValidated = xmlValidator.isXMLValidatedToSchema(responseMsg.getMessageBodyParts().get(1));

            ArrayList<String> combinedErrorList = new ArrayList<String>();
            combinedErrorList.addAll(xmlValidator.getSchemaValidationContentErrorList());
            combinedErrorList.addAll(xmlValidator.getSchemaValidationValueErrorList());
            combinedErrorList.addAll(xmlValidator.getParserValidationErrors());
            combinedErrorList.addAll(xmlValidator.getSchemaValidationOtherErrorList());

            MessageManager.getInstance().createMessage(entityDataType);

            if (combinedErrorList.size() > 0)
                throw new InvalidEntityContentException("The following errors exist within the " + entityDataType + " data: " + combinedErrorList.toString());

        }
        else
            throw new EntityEmulationException("No response message was generated for data type " + entityDataType);
    }
}
