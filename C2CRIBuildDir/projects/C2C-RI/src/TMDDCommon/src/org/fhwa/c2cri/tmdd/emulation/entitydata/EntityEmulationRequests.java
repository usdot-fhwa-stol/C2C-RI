/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class EntityEmulationRequests {

    public enum EntityRequestMessageType {
        ARCHIVEDDATAPROCESSINGDOCUMENTATIONMETADATAREQUESTMSG("tmdd:archivedDataProcessingDocumentationMetadataRequestMsg"),
        CCTVCONTROLREQUESTMSG("tmdd:cCTVControlRequestMsg"),
        CENTERACTIVEVERIFICATIONREQUESTMSG("tmdd:centerActiveVerificationRequestMsg"),
        DETECTORDATAREQUESTMSG("tmdd:detectorDataRequestMsg"),
        DETECTORMAINTENANCEHISTORYREQUESTMSG("tmdd:detectorMaintenanceHistoryRequestMsg"),
        DEVICECANCELCONTROLREQUESTMSG("tmdd:deviceCancelControlRequestMsg"),
        DEVICECONTROLSTATUSREQUESTMSG("tmdd:deviceControlStatusRequestMsg"),
        DEVICEINFORMATIONREQUESTMSG("tmdd:deviceInformationRequestMsg"),
        DEVICEPRIORITYQUEUEREQUESTMSG("tmdd:devicePriorityQueueRequestMsg"),
        DMSCONTROLREQUESTMSG("tmdd:dMSControlRequestMsg"),
        DMSFONTTABLEREQUESTMSG("tmdd:dMSFontTableRequestMsg"),
        DMSMESSAGEAPPEARANCEREQUESTMSG("tmdd:dMSMessageAppearanceRequestMsg"),
        DMSMESSAGEINVENTORYREQUESTMSG("tmdd:dMSMessageInventoryRequestMsg"),
        EVENTREQUESTMSG("tmdd:eventRequestMsg"),
        GATECONTROLREQUESTMSG("tmdd:gateControlRequestMsg"),
        HARCONTROLREQUESTMSG("tmdd:hARControlRequestMsg"),
        INTERSECTIONSIGNALCONTROLREQUESTMSG("tmdd:intersectionSignalControlRequestMsg"),
        INTERSECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG("tmdd:intersectionSignalTimingPatternInventoryRequestMsg"),
        LCSCONTROLREQUESTMSG("tmdd:lCSControlRequestMsg"),
        TRAFFICNETWORKINFORMATIONREQUESTMSG("tmdd:trafficNetworkInformationRequestMsg"),
        RAMPMETERCONTROLREQUESTMSG("tmdd:rampMeterControlRequestMsg"),
        SECTIONCONTROLREQUESTMSG("tmdd:sectionControlRequestMsg"),
        SECTIONCONTROLSTATUSREQUESTMSG("tmdd:sectionControlStatusRequestMsg"),
        SECTIONSIGNALTIMINGPATTERNINVENTORYREQUESTMSG("tmdd:sectionSignalTimingPatternInventoryRequestMsg"),
        VIDEOSWITCHCONTROLREQUESTMSG("tmdd:videoSwitchControlRequestMsg");

        private final String messageName;  // the message name associated with this entity data type

        EntityRequestMessageType(String messageName) {
            this.messageName = messageName;
        }

        public String relatedMessage() {
            return messageName;
        }

    }

}
