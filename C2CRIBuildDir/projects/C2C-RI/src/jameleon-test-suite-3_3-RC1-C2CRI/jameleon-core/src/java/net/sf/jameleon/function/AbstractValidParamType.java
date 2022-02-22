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
package net.sf.jameleon.function;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

/**
 * An Abstract class for implementations of {@link ParamTypeValidatable}.
 * To add a valid param type to the list, call the {@link #addValidType(java.lang.String)} method.
 */
public abstract class AbstractValidParamType implements ParamTypeValidatable{

    protected HashMap types;

    public AbstractValidParamType(){
        types = new HashMap();
    }

    /**
     * @return true if the param type is valid.
     */
    public boolean isValidType(String type) {
        boolean bValid = true;
        if (type != null) {
            if (types.get(type.toLowerCase()) == null){
                bValid = false;
            }
        }
        return bValid;
    }

    /**
     * @return a String listing the valid param types.
     */
    public String getValidTypes(){
        String msg;
        msg = "null";
        Collection col = types.values();
        Iterator ite = col.iterator();
        while (ite.hasNext()){
            String value = (String) ite.next();
            msg += "\n" + value;
        }
        return msg;
    }

    /**
     * Adds a valid parameter type to the list.
     * @param type - The valid type.
     * <b>NOTE</b> - This lowercases the actual type so when the compare occurs,
     * the type is case-insensitive.
     */
    public void addValidType(String type){
        String key = type.toLowerCase();
        types.put(key, type);
    }
}
