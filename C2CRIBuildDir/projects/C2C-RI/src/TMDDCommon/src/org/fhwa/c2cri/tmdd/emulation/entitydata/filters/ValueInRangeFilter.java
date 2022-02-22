/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata.filters;

import java.util.ArrayList;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.exceptions.FilterGenerationException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 3, 2016
 */
public class ValueInRangeFilter implements DataFilter {

    private String rangeStart;
    private String rangeEnd;
    private String filteredItem;
    private ArrayList valueSet;
    private int schemaId;
    private BaseType baseType;
    private String enumeration;
    private EntityEmulationData.EntityDataType entityDataType;

    private ValueInRangeFilter() {
    }

    ;
    
    public ValueInRangeFilter(EntityEmulationData.EntityDataType entityDataType, String filteredItem, String rangeStart, String rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.filteredItem = filteredItem;
        this.valueSet = valueSet;
        this.entityDataType = entityDataType;
    }

    @Override
    public String getFilterSpecification() throws FilterGenerationException {
        String filterSpecification = "";
        if (schemaId == 0) {
            return "";
        } else {
            try {
                switch (baseType) {
                    case STRING:
                        filterSpecification = "(EntityIndex in (select EntityIndex from " + entityDataType + " where (SchemaDetailId = " + schemaId + " and EntityElementValue >= \"" + rangeStart + "\" and EntityElementValue <= \"" + rangeEnd + "\")))";
                        break;

                    default:
                        throw new Exception("Filter BaseType " + baseType + " has not been implemented in the ValueInRangeFilter.");
                }

            } catch (Exception ex) {
                throw new FilterGenerationException(ex);
            }
        }
        return filterSpecification;
    }

    @Override
    public String getFilteredItem() {
        return this.filteredItem;
    }

    @Override
    public void setFilterItemIdentifier(int schemaId, BaseType baseType, String enumeration) {
        this.schemaId = schemaId;
        this.baseType = baseType;
        if (enumeration != null) {
            this.enumeration = enumeration.replace("{", "").replace("}", "");
        }
    }

}
