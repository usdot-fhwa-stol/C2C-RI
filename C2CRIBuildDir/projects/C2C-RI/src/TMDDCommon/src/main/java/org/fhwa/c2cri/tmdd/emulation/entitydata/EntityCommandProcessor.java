/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType.EntityType;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidCommandException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestLogException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityControlRequestStatusException;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface EntityCommandProcessor {
    public void verifyCommand(MessageSpecification commandMessage) throws InvalidCommandException, EntityEmulationException;
    public void executeCommand(MessageSpecification commandMessage) throws EntityEmulationException;
    public void queueCommand(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, InvalidEntityControlRequestLogException, EntityEmulationException;
    public MessageSpecification getControlResponseMessage(MessageSpecification commandMessage) throws InvalidEntityControlRequestStatusException, EntityEmulationException;
}
