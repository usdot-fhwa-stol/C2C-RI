/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.result;

import java.util.Map;

import net.sf.jameleon.bean.FunctionalPoint;

/**
 * An implementation of @see TestResult that represents the results of a data-drivable tag.
 * A DataDrivableRowResult can contain session results and function point results.
 */
public class DataDrivableRowResult extends TestResultWithChildren{
    private static final long serialVersionUID = 1L;

    /**
     * The row number of exeuction for the data-drivable tag
     */
    protected int rowNum;
    /**
     * The row data for the data-drivable tag
     */
    protected Map rowData;

    public DataDrivableRowResult(FunctionalPoint tag){
        super(tag);
    }

    public DataDrivableRowResult(FunctionalPoint tag, HasChildResults parentResults){
        super(tag, parentResults);
    }

    public Map getRowData(){
        return rowData;
    }

    public void setRowData(Map rowData){
        this.rowData = rowData;
    }

    public int getRowNum(){
        return rowNum;
    }

    public void setRowNum(int rowNum){
        this.rowNum = rowNum;
    }

    public boolean isDataDriven(){
        return true;
    }

    /**
     * @return a XML String representation of the results
     */
    public String toXML() {
        StringBuffer buffer = new StringBuffer("\t<data-drivable-row>\n");
        buffer.append(super.toXML());
        buffer.append("\t</data-drivable-row>\n");
        return buffer.toString();
    }

    /**
     * @return A String representation of the results
     */
    public String toString() {
        return super.toString();
    }
}