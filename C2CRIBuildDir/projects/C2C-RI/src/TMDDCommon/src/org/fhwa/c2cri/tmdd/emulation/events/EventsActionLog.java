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
public class EventsActionLog extends TMDDDefaultEntityDataType{

    public EventsActionLog(EntityEmulationData.EntityDataType entityDataType){
        super(entityDataType);
    }
    
    
    /**
     * 
     * @param entityDataType
     * @param entityId
     * @return the matching related entity Id if there is an existing match.  Otherwise, return the next available match.
     */
    public String getMatchingOrNextEntityIdEntry(EntityEmulationData.EntityDataType entityDataType, String entityId){
        String response = "";
        Integer nextValue = 0;
        
        ArrayList<String> eventIdFilterValues = new ArrayList();
        ArrayList<DataFilter> filters = new ArrayList();

        String relatedEventId = getEventsActionLogEventId(entityId);
        if (!relatedEventId.isEmpty()){
            eventIdFilterValues.add(relatedEventId);
            ValueInSetFilter eventIdFilter = new ValueInSetFilter(entityDataType, "EventId", eventIdFilterValues);
            filters.add(eventIdFilter);              
        }      
        
        try{
                             
            if (entityDataType.equals(EntityEmulationData.EntityDataType.EVENTSINDEX)){
                response = EntityEmulationData.getDefaultEntityElementNameValue(entityDataType, "EventId");
                if (!filters.isEmpty()){
                    ArrayList<EntityDataRecord> results = EntityEmulationData.getRelatedEntityElementsWithValueType(entityDataType, filters,"EventId");
                    if (results.size() > 0){
                        for (EntityDataRecord result : results){                    
                            System.out.println("   Matching "+entityDataType.name()+" Entry EntityId = "+result.getEntityElementValue() + " the current Max Value = "+response + " and the next value is "+nextValue);                    
                            response = result.getEntityElementValue();
                        }
                    }                
                }
            } else {
                response = EntityEmulationData.getMaxEntityIdValue(entityDataType);
                nextValue = Integer.parseInt(response) + 1;
                if (!filters.isEmpty()){
                    ArrayList<EntityDataRecord> results = EntityEmulationData.getRelatedEntityElementsWithValueType(entityDataType, filters, "EntityId");
                    if (results.size() > 0){
                        for (EntityDataRecord result : results){                    
                            System.out.println("   Matching "+entityDataType.name()+" Entry EntityId = "+result.getEntityElementValue() + " the current Max Value = "+response + " and the next value is "+nextValue);                    
                            response = result.getEntityElementValue();
                        }
                    }
                } else {
                    response= nextValue.toString();
                }
            }
        } catch (Exception ex){        
            if (entityDataType.equals(EntityEmulationData.EntityDataType.EVENTSUPDATES)){
                response = nextValue.toString();                
            } else {
                
            }
//            ex.printStackTrace();
            
        }
        System.out.println("    EventsActionLog:  "+entityDataType.name() + " index used = "+response + "  computed next value was "+nextValue);
        return response;
    }    

    public String getEventsActionLogEventId(String entityId){
        String results = "";
        
        try {
           ArrayList<String> entityIdFilterValues = new ArrayList();
            entityIdFilterValues.add(entityId);
            ValueInSetFilter entityIdFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, "EntityId", entityIdFilterValues);
            ArrayList<DataFilter> entityFilters = new ArrayList();
            entityFilters.add(entityIdFilter);
            ArrayList<EntityDataRecord> eventResults = EntityEmulationData.getRelatedEntityElementsWithValueType(EntityEmulationData.EntityDataType.EVENTSACTIONLOG, entityFilters,"EventId");
            if (eventResults.size() > 0){
                return (eventResults.get(0)).getEntityElementValue();
            }
        } catch (Exception ex){
            System.out.println(" There are no current events associated with "+EntityEmulationData.EntityDataType.EVENTSACTIONLOG+" " + entityId);
            
        }
        return results;
    }
}
