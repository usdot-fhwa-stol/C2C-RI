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

import java.io.IOException;

import java.util.Map;

/**
 * This interface is used to implement different ways to get
 * data from a source. Some examples might be a database, an xml
 * file or even an Excel spreadsheet (besides just using a CSV file).
 */
public interface DataDriver{

    /**
     * Opens the handle to the data source
     * @throws IOException when the data source can not be found.
     */
    public void open() throws IOException;

    /**
     * Closes the handle to the data source
     */
    public void close();

    /**
     * Gets the next row from the data source
     * @return a key-value HashMap representing the data row or null if 
     * no data row is available
     */
    public Map getNextRow();

    /**
     * Tells whether the data source has another row
     * @return true if the data source still has more rows
     */
    public boolean hasMoreRows();

}
