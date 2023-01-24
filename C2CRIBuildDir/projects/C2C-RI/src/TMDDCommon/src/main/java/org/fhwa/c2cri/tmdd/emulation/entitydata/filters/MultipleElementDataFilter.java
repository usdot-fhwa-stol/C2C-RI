/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata.filters;

import java.util.ArrayList;
import java.util.HashMap;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface MultipleElementDataFilter extends DataFilter{
 
    //This method provides the names of the elements that are being used to filter.  These names 
    //can/will be used to look up the identifiers for the element types for the Entity Data Types.
    public String[] getFilteredItems();
    
    // This method sets identifier for the value that will be used in the filter.
    public void setFilterItems(ArrayList<EntityDataRecord> relatedRecords, HashMap<String,Integer> filteredItemsMap);    
    
}
