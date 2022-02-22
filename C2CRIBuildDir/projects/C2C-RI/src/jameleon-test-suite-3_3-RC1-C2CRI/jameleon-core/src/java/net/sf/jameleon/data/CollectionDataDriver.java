/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com) and
                       Andrey Chernyshov (achernyshov@valuecommerce.ne.jp)
    
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This DataDriver is used to iterator over a Collection of Objects.
 * @author Andrey Chernyshov
 * @author Christian Hargraves
 */
public class CollectionDataDriver implements DataDriver {
    protected Collection items;
    protected String key;
    protected Iterator iterator;

    /**
     * Creates a CollectionDataDriver
     * You must call setKey() and setItems() in order for this DataDriver to work correctly
     */
    public CollectionDataDriver() {
    }

    /**
     * Creates a CollectionDataDriver
     * @param key - the key to store the object in the collection as
     * @param items - the collection of items to iterator over
     */
    public CollectionDataDriver(String key, Collection items) {
        this.items = items;
        this.key = key;
    }

    public void setItems(Collection items) {
        this.items = items;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Opens the handle to the data source
     *
     * @throws java.io.IOException when the data source can not be found.
     */
    public void open() throws IOException {
        if (items != null && key != null) {
            iterator = items.iterator();
        }else{
            String errorMsg = null;
            if (items == null) {
                errorMsg = "No Data provided!";
            }else{
                errorMsg = "No Key provided!";
            }
            throw new IOException(errorMsg);
        }
    }

    /**
     * Closes the handle to the data source
     */
    public void close() {
        iterator = null;
    }

    /**
     * Gets the next row from the data source
     *
     * @return a key-value HashMap representing the data row or null if
     *         no data row is available
     */
    public Map getNextRow() {
        Map vars = null;
        if (iterator.hasNext()) {
            vars = new HashMap();
            vars.put(key, iterator.next());
        }
        return vars;
    }

    /**
     * Tells whether the data source has another row
     *
     * @return true if the data source still has more rows
     */
    public boolean hasMoreRows() {
        return iterator.hasNext();
    }
}


