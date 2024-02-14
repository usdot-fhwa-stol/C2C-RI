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

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.function.FunctionTag;

public class FunctionDebugDialog extends DebugDialog{

    private transient FunctionTag functionTag;

    public FunctionDebugDialog(Object source, JFrame rootFrame, TestCaseResultsPane tcrf, String title){
        super(source, rootFrame, tcrf, title);
    }

    /**
     * Sets the source where the data is pulled and where the data
     * needs to be changed if any values are changed in the table.
     * @param source - The tag that represents this debug dialog
     */
    protected void setBreakPointSource(Object source){
        functionTag = (FunctionTag)source;
    }

    /**
     * Gets the key-value pairs that are set via this tag
     * @return the key-value pairs that are set via this tag
     */
    protected Map getAttributes(){
        Map vars = new LinkedHashMap();
        Map attrs = functionTag.getFunctionalPoint().getAttributes();
        Iterator it = attrs.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String)it.next();
            vars.put(key, ((Attribute)attrs.get(key)).getValue());
        }
        return vars;
    }

    /**
     * Gets the description that shows up in the dialog box
     * @return the description that shows up in the dialog box
     */
    protected String getTagDescription(){
        return getTagsAsString(functionTag.getFunctionalPoint().getTagNames());
    }

    /**
     * Changes the value of the given attribute's name real-time
     * @param attributeName - The name of the attribute to change
     * @param newValue - The new value to set the attribute to
     */
    protected void setNewAttributeValue(String attributeName, String newValue){
        Attribute attr = functionTag.getFunctionalPoint().getAttribute(attributeName);
        if (isSupported(attr.getType())) {
            functionTag.setAttribute(attributeName, newValue);
        }
    }

    private String getTagsAsString(List tagNames){
        StringBuffer tags = new StringBuffer();
        Iterator it = tagNames.iterator();
        String tag;
        while (it.hasNext()) {
            tag = (String)it.next();
            tags.append("<").append(tag).append("/> "); 
        }
        return tags.toString();
    }

    private boolean isSupported(String type){
        boolean supported = true;
        if (type.startsWith("java.util")){
            if (!type.equals("java.util.Date") && 
                !type.equals("java.util.Calendar") &&
                !type.equals("java.util.Currency")) {
                supported = false;
            }
        }
        return supported;
    }

}
