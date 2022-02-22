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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.event;

import net.sf.jameleon.data.DataDrivable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

public class DataDrivableEventHandler{

    private static DataDrivableEventHandler eventHandler;
    private final List dataDrivableListeners = Collections.synchronizedList(new LinkedList());

    private DataDrivableEventHandler(){}

    public static DataDrivableEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new DataDrivableEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    public void addDataDrivableListener(DataDrivableListener ddl){
        if (ddl != null && !dataDrivableListeners.contains(ddl)){
            dataDrivableListeners.add(ddl);
        }
    }

    public List getDataDrivableListeners(){
        return dataDrivableListeners;
    }

    public void removeDataDrivableListener(DataDrivableListener ddl){
        dataDrivableListeners.remove(ddl);
    }

    public void openEvent(DataDrivable dd){
        DataDrivableEvent dde = new DataDrivableEvent(dd);
        synchronized(dataDrivableListeners){
            Iterator it = dataDrivableListeners.iterator();
            DataDrivableListener ddl;
            while (it.hasNext()) {
                ddl = (DataDrivableListener)it.next();
                ddl.openEvent(dde);
            }
        }
    }

    public void closeEvent(DataDrivable dd){
        DataDrivableEvent dde = new DataDrivableEvent(dd);
        synchronized(dataDrivableListeners){
            Iterator it = dataDrivableListeners.iterator();
            DataDrivableListener ddl;
            while (it.hasNext()) {
                ddl = (DataDrivableListener)it.next();
                ddl.closeEvent(dde);
            }
        }
    }

    public void executeRowEvent(Map vars, DataDrivable dd, int rowNum){
        DataDrivableEvent dde = new DataDrivableEvent(dd);
        dde.setRowData(vars);
        synchronized(dataDrivableListeners){
            Iterator it = dataDrivableListeners.iterator();
            DataDrivableListener ddl;
            while (it.hasNext()) {
                ddl = (DataDrivableListener)it.next();
                ddl.executeRowEvent(dde, rowNum);
            }
        }
    }

}
