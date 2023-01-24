/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.events;

import java.util.ArrayList;
import org.fhwa.c2cri.tmdd.emulation.TMDDDefaultEntityDataType;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;

/**
 * This class represents the set of CCTV Inventory entity information for the
 * center. The information that is maintained is described in TMDD Volume II -
 * Section 3.2.2.2.
 *
 * @author TransCore ITS, LLC Created: Jan 31, 2016
 */
public class EventsIndex extends TMDDDefaultEntityDataType{

    public EventsIndex(EntityEmulationData.EntityDataType entityDataType){
        super(entityDataType);
    }
    
    
    /**
     * 
     * @param entityDataType
     * @param eventsIndexEntityId
     * @return the matching related entity Id if there is an existing match.  Otherwise, return the next available match.
     */
    public String getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType entityDataType, String eventsIndexEntityId){
        String response = "";
        Integer nextValue = 0;
        
        ArrayList<String> eventIdFilterValues = new ArrayList();
        eventIdFilterValues.add(eventsIndexEntityId);
        ValueInSetFilter eventIdFilter = new ValueInSetFilter(entityDataType, "EventId", eventIdFilterValues);
        ArrayList<DataFilter> filters = new ArrayList();
        filters.add(eventIdFilter);  
        try{
            response = EntityEmulationData.getMaxEntityIdValue(entityDataType);
            nextValue = Integer.parseInt(response) + 1;
            ArrayList<EntityDataRecord> results = EntityEmulationData.getRelatedEntityElementsWithValueType(entityDataType, filters, "EntityId");
            if (results.size() > 0){
                for (EntityDataRecord result : results){                    
                    System.out.println("   Matching "+entityDataType.name()+" Entry EntityId = "+result.getEntityElementValue() + " the current Max Value = "+response + " and the next value is "+nextValue);                    
                    response = result.getEntityElementValue();
                }
            }
        } catch (Exception ex){            
            response = nextValue.toString();
 //           ex.printStackTrace();
            
        }
        System.out.println("    EventsIndex:  "+entityDataType.name() + " index used = "+response + "  computed next value was "+nextValue);
                
        return response;
    }
}
