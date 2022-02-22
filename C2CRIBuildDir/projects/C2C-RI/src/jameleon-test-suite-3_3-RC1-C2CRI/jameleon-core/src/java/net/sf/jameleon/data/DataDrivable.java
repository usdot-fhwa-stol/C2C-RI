/*

    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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

import java.util.Set;
import java.util.Map;

/**
 * The class that needs to be data-driven should implement this interface.
 */
public interface DataDrivable{

    /**
     * Called with the a key-value pair of variables for each row found in the
     * data source.
     * @param rowNum - The row number being executed
     */
    public void executeDrivableRow(int rowNum);

    /**
     * Removes the keys from the context and the variable set in addVariablesToRowData()
     * @param keys - A Set of variable names to clean up.
     */
    public void destroyVariables(Set keys);

    /**
     * Used to keep track of the variables and their original values.
     * Any variable substitution should also be done here
     * @param vars - A map of key-value pairs.
     */
    public void addVariablesToRowData(Map vars);

    /**
     * Used to tell whether this DataDrivable object is meant
     * to report on each row execution or to report on them as
     * a whole
     */
    public boolean isCountRow();
}
