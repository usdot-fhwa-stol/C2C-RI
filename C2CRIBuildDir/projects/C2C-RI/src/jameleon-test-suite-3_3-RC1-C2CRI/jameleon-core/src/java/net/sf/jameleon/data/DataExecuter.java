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

import net.sf.jameleon.event.DataDrivableEventHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Ties DataDriver and DataDrivable together.
 */
public class DataExecuter{

    protected DataDriver driver;

    /**
     * Default constructor. At the moment this does nothing.
     */
    public DataExecuter(){}

    /**
     * Uses the given <code>driver</code> to drive the DataDrivable.
     * @param driver The drive used to drive the DataDrivable.
     */
    public DataExecuter(DataDriver driver){
        this.driver = driver;
    }

    /**
     * Gets the DataDriver used by this class.
     * @return the DataDriver used by this class.
     */
    public DataDriver getDataDriver(){
        return driver;
    }

    /**
     * When it is time to make the class data-driven, this method should be called.
     * @param drivable - The class to be data-driven, for example, a csv tag class
     * @param noExecute - If set to true, then the driver's methods will not actually get called
     *                    and the executeDrivableRow will get called with an empty data set.
     * @throws IOException the datasource handle cannot be opened or the datasource
     *                     cannot be read from.
     */
    public void executeData(DataDrivable drivable, boolean noExecute) throws IOException{
        int row = 1;
        if (noExecute) {
            drivable.addVariablesToRowData(new HashMap());
            drivable.executeDrivableRow(row);
            drivable.destroyVariables(new HashMap().keySet());
        }else{
            DataDrivableEventHandler eventHandler = DataDrivableEventHandler.getInstance();
            try{
                eventHandler.openEvent(drivable);
                driver.open();
                Map vars;
                while (driver.hasMoreRows()) {
                    vars = driver.getNextRow();
                    eventHandler.executeRowEvent(vars, drivable, row);
                    drivable.addVariablesToRowData(vars);
                    drivable.executeDrivableRow(row++);
                    drivable.destroyVariables(vars.keySet());
                }
            }finally{
                eventHandler.closeEvent(drivable);
                driver.close();
            }
        }
    }

}
