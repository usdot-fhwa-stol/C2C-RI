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
package net.sf.jameleon.ui;

import java.util.Map;

import javax.swing.JFrame;

import net.sf.jameleon.data.AbstractDataDrivableTag;
import net.sf.jameleon.event.DataDrivableEvent;

public class DataDrivableDebugDialog extends DebugDialog{

    private DataDrivableEvent ddEvent;

    public DataDrivableDebugDialog(Object source, JFrame rootFrame, TestCaseResultsPane tcrf, String title){
        super(source, rootFrame, tcrf, title);
    }

    /**
     * Sets the source where the data is pulled and where the data
     * needs to be changed if any values are changed in the table.
     * @param source - The tag that represents this debug dialog
     */
    protected void setBreakPointSource(Object source){
        ddEvent = (DataDrivableEvent)source;
    }

    /**
     * Gets the key-value pairs that are set via this tag
     * @return the key-value pairs that are set via this tag
     */
    protected Map getAttributes(){
        return ddEvent.getRowData();
    }

    /**
     * Gets the description that shows up in the dialog box
     * @return the description that shows up in the dialog box
     */
    protected String getTagDescription(){
        String tagName = "data-drivable tag";
        if (ddEvent.getSource() instanceof AbstractDataDrivableTag) {
            tagName = ((AbstractDataDrivableTag)ddEvent.getSource()).getTagDescription();
        }
        return tagName;
    }

    /**
     * Changes the value of the given attribute's name real-time
     * @param attributeName - The name of the attribute to change
     * @param newValue - The new value to set the attribute to
     */
    protected void setNewAttributeValue(String attributeName, String newValue){
        getAttributes().put(attributeName, newValue);
    }

}
