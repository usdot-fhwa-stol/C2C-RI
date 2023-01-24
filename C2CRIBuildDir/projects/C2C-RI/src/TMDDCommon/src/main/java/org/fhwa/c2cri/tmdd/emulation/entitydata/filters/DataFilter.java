/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata.filters;

import org.fhwa.c2cri.tmdd.emulation.exceptions.FilterGenerationException;


/**
 *
 * @author TransCore ITS, LLC
 */
public interface DataFilter {

    public enum BaseType {STRING,
                          ANYSIMPLETYPE,
                          DATETIME,
                          DECIMAL,
                          FLOAT,
                          INT,
                          INTLATITUDE32,
                          INTLONGITUDE32,
                          SHORT,
                          UNSIGNEDBYTE,
                          UNSIGNEDINT,
                          UNSIGNEDSHORT}
    
    
    //This method provides a specification for the filter that can be used
    //to describe the data that is included in the filter.  If no specification
    //is provided then an empty string is returned.  No specification will be provided
    //if the DataFilter does not contain a valid Item Identifier.
    public String getFilterSpecification() throws FilterGenerationException;
    
    //This method provides the name of the element that is being filtered.  This name 
    //can/will be used to look up the identifier for the element type for the Entity Data Type.
    public String getFilteredItem();
    
    // This method sets identifier for the value that will be used in the filter.
    public void setFilterItemIdentifier(int SchemaId, BaseType baseType, String enumeration);
}
