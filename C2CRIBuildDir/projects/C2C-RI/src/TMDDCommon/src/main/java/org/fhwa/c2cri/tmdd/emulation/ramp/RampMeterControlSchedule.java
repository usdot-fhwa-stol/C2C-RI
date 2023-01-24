/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.ramp;

import org.fhwa.c2cri.tmdd.emulation.TMDDDefaultEntityDataType;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;

/**
 * This class represents the set of Ramp Meter Control Schedule entity information for the
 * center. The information that is maintained is described in TMDD Volume II -
 * Section 3.2.2.2.
 *
 * @author TransCore ITS, LLC Created: Jan 31, 2016
 */
public class RampMeterControlSchedule extends TMDDDefaultEntityDataType{

    public RampMeterControlSchedule(EntityEmulationData.EntityDataType entityDataType){
        super(entityDataType);
    }
}
