/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata.filters;

import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.util.HashMap;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.exceptions.FilterGenerationException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 3, 2016
 */
public class ValueInDateRangeFilter implements MultipleElementDataFilter {

    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String dateFilter;
    private String timeFilter;
    private String offsetFilter;
    private ArrayList<Integer> applicableEntities = new ArrayList();

    private ValueInDateRangeFilter() {
    }

    ;
    
    public ValueInDateRangeFilter(String dateFilter, String timeFilter, String offsetFilter, String date, String time, String offset, String periodInSeconds) throws EntityEmulationException {
        try{
            this.startDateTime = toZonedDateTime(date, time, offset);            
        } catch (Exception ex){
            throw new EntityEmulationException ("Invalid start date/time value provided in request: "+ex.getMessage());
        }

        
        try{
            this.endDateTime = periodInSeconds==null?this.startDateTime:this.startDateTime.plusSeconds(Long.parseLong(periodInSeconds));
        } catch (NumberFormatException ex){            
            throw new EntityEmulationException ("Invalid time range value provided in request: "+ex.getMessage());
        }
        
        this.dateFilter = dateFilter;
        this.timeFilter = timeFilter;
        this.offsetFilter = offsetFilter;
    }

    @Override
    public String getFilterSpecification() throws FilterGenerationException {
        String filterSpecification = "";

        try {
            filterSpecification = "(EntityIndex in (" + generateMatchingEntityIndexList() + "))";
        } catch (Exception ex) {
            throw new FilterGenerationException(ex);
        }

        return filterSpecification;
    }

    /**
     *
     * @return a comma separated list of entities that have a match for the
     * given date range.
     */
    private String generateMatchingEntityIndexList() throws EntityEmulationException {
        if (!this.applicableEntities.isEmpty()) {
            StringBuilder entityList = new StringBuilder();
            for (Integer entityIndex : this.applicableEntities) {
                if (entityList.length() == 0) {
                    entityList.append(entityIndex);
                } else {
                    entityList.append(",").append(entityIndex);
                }
            }
            return entityList.toString();
        }
        throw new EntityEmulationException("No Matching time-ranges were found.");
    }

    @Override
    public String getFilteredItem() {
        return "";
    }

    @Override
    public void setFilterItemIdentifier(int schemaId, BaseType baseType, String enumeration) {
        // Do nothing.  This does not apply to this filter type.
    }

    @Override
    public String[] getFilteredItems() {
        return new String[]{dateFilter,timeFilter,offsetFilter};
    }

    @Override
    /**
     * Receives the provided data and updates the applicable Entities List
     * 
     * Updates the list of applicable entity indexes.
     */
    public void setFilterItems(ArrayList<EntityDataRecord> entityRecords, HashMap<String, Integer> schemaIdMapping) {
        HashMap<String, TimeRecord> timeRecords = new HashMap();
        for (EntityDataRecord dataRecord : entityRecords){
            String parentPath = dataRecord.getEntityElement().substring(0, dataRecord.getEntityElement().lastIndexOf("."));
            TimeRecord currentPathTimeRecord;
            if (!timeRecords.containsKey(parentPath)){
                currentPathTimeRecord = new TimeRecord(dataRecord.getEntityIndex());
                timeRecords.put(parentPath, currentPathTimeRecord);                
            } else {
                currentPathTimeRecord = timeRecords.get(parentPath);
            }
            
            // Update the date/time/offset values of the TimeRecord associated with this message path
            if (dataRecord.getSchemaDetailId() == schemaIdMapping.get(dateFilter)){
                currentPathTimeRecord.setDate(dataRecord.getEntityElementValue());
            } else if (dataRecord.getSchemaDetailId() == schemaIdMapping.get(timeFilter)){
                currentPathTimeRecord.setTime(dataRecord.getEntityElementValue());
            } else if (dataRecord.getSchemaDetailId() == schemaIdMapping.get(offsetFilter)){
                currentPathTimeRecord.setOffset(dataRecord.getEntityElementValue());            
            }
        }
        
        for (TimeRecord thisRecord : timeRecords.values()){
            if (!applicableEntities.contains(thisRecord.getEntityIndex()) && thisRecord.isInRange(startDateTime, endDateTime)){
                applicableEntities.add(thisRecord.getEntityIndex());
            }
        }
        
    }

    /**
     * Converts the provided string date parts into a ZoneDateTime object.The format of the string that will be parsed is "2020-04-05T12:46:56.000-04:00[UTC]"
     * @param date
     * @param time
     * @param offset
     * @return 
     * @throws java.lang.Exception 
     */
    public static ZonedDateTime toZonedDateTime(String date, String time, String offset) throws Exception {
        String defaultTime = "00:00:00";
        String defaultSubSeconds ="0000";
        String defaultOffset = "+00:00";
        
        String preparedDate = date.substring(0, 4)+"-"+date.substring(4, 6)+"-"+date.substring(6,8);
        String preparedTime = time==null?defaultTime:time.substring(0, 2)+":"+time.substring(2, 4)+":"+time.substring(4, 6)+"."+(time.length()>6?String.format("%1$-4s",time.substring(6)).replace(' ', '0'):defaultSubSeconds);
        String preparedZone = defaultOffset;
        if (offset != null)
        {
            preparedZone = offset;
            if (!preparedZone.startsWith("-") || !preparedZone.startsWith("+"))
                preparedZone = "+" + preparedZone;
            if (!preparedZone.contains(":"))
                preparedZone = preparedZone.substring(0,3) + ":" + preparedZone.substring(3);
        }

        return ZonedDateTime.parse(preparedDate+"T"+preparedTime+  preparedZone+"[UTC]");
    }
    
    class TimeRecord {

        private final int entityIndex;
        private String date;
        private String time;
        private String offset;

        public TimeRecord(Integer entityIndex) {
            this.entityIndex = entityIndex;
        }

        public int getEntityIndex() {
            return entityIndex;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public ZonedDateTime getZonedDateTime() throws Exception{
             return toZonedDateTime(date, time, offset);
        }
        
        // Returns true if the Time Record date lies between or equal to the two given dates
        public boolean isInRange(ZonedDateTime startDate, ZonedDateTime endDate){
            try{
                return startDate.compareTo(getZonedDateTime()) * getZonedDateTime().compareTo(endDate) >= 0;
            } catch (Exception ex){
                System.out.println("ValueInDateRangeFilter Exception : " +ex.getMessage());                
            }
            return false;
        }
    }
}
