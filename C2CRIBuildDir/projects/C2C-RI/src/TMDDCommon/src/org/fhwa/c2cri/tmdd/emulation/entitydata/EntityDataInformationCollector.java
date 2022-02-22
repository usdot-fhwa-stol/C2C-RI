/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface EntityDataInformationCollector {

    public ArrayList<EntityDataRecord> getEntityElements(ArrayList<DataFilter> filters) throws NoMatchingDataException, EntityEmulationException;
   
}
