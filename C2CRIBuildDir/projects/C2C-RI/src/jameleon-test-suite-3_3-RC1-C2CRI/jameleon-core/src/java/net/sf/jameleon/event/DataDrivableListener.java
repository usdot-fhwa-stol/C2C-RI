/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.event;

import java.util.EventListener;

public interface DataDrivableListener extends EventListener{

    /**
     * Gets called before the open method of a DataDrivable
     * @param event - a DataDrivableEvent Object
     */
    public void openEvent(DataDrivableEvent event);

    /**
     * Gets called before the close method of a DataDrivable
     * @param event - a DataDrivableEvent Object
     */
    public void closeEvent(DataDrivableEvent event);

    /**
     * Gets called before the executeDrivableRow
     * @param event - a DataDrivableEvent Object
     * @param rowNum - the current row number being executed from the data source.
     */
    public void executeRowEvent(DataDrivableEvent event, int rowNum);

}
