/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.generictests;

import java.util.ArrayList;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Mar 24, 2016
 */
public class EntityElementCountTest {
    public static boolean test(int expectedCount, EntityEmulationData.EntityDataType dataType, String entityId, boolean printDetails){
        boolean result = false;

        ArrayList<DataFilter> theFilters = new ArrayList();

        // device-id filter
        ArrayList<String> deviceIdFilterValues = new ArrayList();
        deviceIdFilterValues.add(entityId);
        ValueInSetFilter deviceIdFilter = new ValueInSetFilter(dataType, "EntityId", deviceIdFilterValues);
        theFilters.add(deviceIdFilter);
        
        try {
            ArrayList<EntityDataRecord> records = EntityEmulationData.getEntityElements(dataType, theFilters);
            if ( records.size() == expectedCount) result = true;
            if (!result && printDetails){
                System.out.println("EntityElementCountTest::  Expected Count = "+ expectedCount + " Actual Count = "+records.size());
            }
        } catch (Exception ex){            
            result = false;
        }
        
        return result;
    }
}
