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

import java.util.*;

/**
 * @jameleon.function name="mock-sql"
 */
public class MockSqlTag extends SqlTag {
    public int totalNumOfRows;
    public LinkedList rows = new LinkedList();
    public Map keyMap = new HashMap();
    public boolean getKeyMappingCalled = false;

    /**
     * A DataDrivable implementation method that gets called once for every row in the data source.
     */
    public void executeDrivableRow(int rowNum) {
        totalNumOfRows = rowNum;
    }

    /**
     * Used to keep track of the variables and their original values.
     * This is mostly used for variable substitution.
     * @param vars - A map of key-value pairs.
     */
    public void addVariablesToRowData(Map rowData){
        super.addVariablesToRowData(rowData);
        rows.add(rowData);
    }

    public Map getKeyMapping(){
        getKeyMappingCalled = true;
        return keyMap;
    }

}

