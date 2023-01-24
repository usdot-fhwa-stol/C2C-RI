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
public class ValueInSetFilter implements DataFilter {

    private String filteredItem;
    private ArrayList valueSet;
    private int schemaId;
    private BaseType baseType;
    private EntityEmulationData.EntityDataType entityDataType;
    private String enumeration;
    
    private ValueInSetFilter(){};
    
    public ValueInSetFilter(EntityEmulationData.EntityDataType entityDataType, String filteredItem, ArrayList valueSet) {
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
                    case UNSIGNEDSHORT:
                    case UNSIGNEDINT:
                        StringBuffer valueListString = new StringBuffer();
                        for (String thisValue : (ArrayList<String>) valueSet) {
                            if (valueListString.length() == 0) {
                                valueListString.append("\"" + thisValue + "\"");
                            } else {
                                valueListString.append(",\"" + thisValue + "\"");
                            }
                        }
                        filterSpecification= "(EntityIndex in (select EntityIndex from " + entityDataType + " where (SchemaDetailId = " + schemaId + " and EntityElementValue in (" + valueListString.toString() + "))))";
                        break;
                        
                    case ANYSIMPLETYPE:
                        if (enumeration != null){
                        String[] enumerationValues = enumeration.split(";");
                        StringBuffer enumValueListString = new StringBuffer();
                        for (String thisValue : (ArrayList<String>) valueSet) {
                            for (int ii = 0; ii<enumerationValues.length; ii++){
                                String enumNumber = enumerationValues[ii].substring(enumerationValues[ii].indexOf("(")+1, enumerationValues[ii].indexOf(")")-1);
                                String enumValue = enumerationValues[ii].substring(enumerationValues[ii].indexOf(")")+1).trim();
                                if (thisValue.equals(enumNumber)||thisValue.equals(enumValue)){
                                    if (enumValueListString.length() == 0) {
                                        enumValueListString.append("\"" + enumNumber + "\",\""+enumValue+"\"");
                                    } else {
                                        enumValueListString.append(",\"" + enumNumber + "\",\""+enumValue+"\"");
                                    }
                                    continue;
                                }                                
                            }
                        }
                        filterSpecification= "(EntityIndex in (select EntityIndex from " + entityDataType + " where (SchemaDetailId = " + schemaId + " and EntityElementValue in (" + enumValueListString.toString() + "))))";                        
                        } else {
                            throw new Exception("Filter BaseType "+baseType+" failed because the enumeration value (id = "+schemaId+") was null in the ValueInSetFilter.");                            
                        }
                        break;
                    default: 
                        throw new Exception("Filter BaseType "+baseType+" has not been implemented in the ValueInSetFilter.");
                }       

            } catch (Exception ex) {
                throw new FilterGenerationException(ex);
            }
        }
        return filterSpecification;
    }

        @Override
        public String getFilteredItem
        
            () {
        return this.filteredItem;
        }

        @Override
        public void setFilterItemIdentifier
        (int schemaId, BaseType baseType, String enumeration) {
           this.schemaId = schemaId;
           this.baseType = baseType;
           if (enumeration != null) {
              this.enumeration = enumeration.replace("{","").replace("}","");
           }
        }

    }
