/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.data;

import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * Used to test the getKeyMapping method
 * 
 * @jameleon.function name="mock-dd"
 */
public class MockDataDrivableTag extends AbstractDataDrivableTag {

    protected CollectionDataDriver dataDriver;
    protected Collection items;
    protected String varName = "testVar";

    /**
     * Gets the logger used for this tag
     *
     * @return the logger used for this tag.
     */
    protected Logger getLogger() {
        return Logger.getLogger(MockDataDrivableTag.class.getName());
    }

    /**
     * Gets the DataDriver used for this tag.
     *
     * @return the DataDriver used for this tag.
     */
    protected DataDriver getDataDriver() {
        if(dataDriver == null) {
            dataDriver = new CollectionDataDriver();
        }
        return dataDriver;
    }
 
    /**
     * Sets up the DataDriver by calling any implementation-dependent
     * methods.
     */
    protected void setupDataDriver() {
        dataDriver.setItems(items);
        dataDriver.setKey(varName);
    }
 
    /**
     * Gets the trace message when the execution is beginning and ending.
     * The message displayed will already start with BEGIN: or END:
     *
     * @return the trace message when the execution is just beginning and ending.
     */
    protected String getTagTraceMsg() {
        return "mock trace msg";
    }
 
    /**
     * Describe the tag when error messages occur.
     * The most appropriate message might be the tag name itself.
     *
     * @return A brief description of the tag or the tag name itself.
     */
    public String getTagDescription() {
        return "mock data-drivable tag";
    }
 
    /**
     * Gets an error message to be displayed when a error occurs due to the DataDriver.
     *
     * @return an error message to be displayed when a error occurs due to the DataDriver.
     */
    protected String getDataExceptionMessage() {
        return "mock data-drivable tag exception message.";
    }
 
    /**
     * Calculates the location of the state to be stored for any tags under this tag.
     * The result should be a relative path that simply has the row number in it along
     * with some unique indentifier for this tag like the handle name or something.
     *
     * @return the location of the state to be stored for any tags under this tag minus the baseDir calculation stuff.
     */
    protected String getNewStateStoreLocation(int rowNum) {
        return "mock-dd-"+rowNum;
    }
 
    /**
     * @jameleon.attribute required="true"
     */
    public void setItems(Collection items) {
        this.items = items;
    }
 
    /**
     * @jameleon.attribute required="true"
     */
    public void setVarName(String varName) {
        this.varName = varName;
    }
 
}
