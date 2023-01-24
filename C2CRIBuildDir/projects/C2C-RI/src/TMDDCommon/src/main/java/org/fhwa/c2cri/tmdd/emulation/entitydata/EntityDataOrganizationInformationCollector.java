/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface EntityDataOrganizationInformationCollector {
    
    public MessageSpecification getEntityOrganizationInformation(TMDDEntityType.EntityType entityType, String entityId)throws NoMatchingDataException, EntityEmulationException;
}
