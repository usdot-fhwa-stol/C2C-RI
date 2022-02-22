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
package net.sf.jameleon.function;

import net.sf.jameleon.util.Configurator;

import org.apache.commons.jelly.JellyContext;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * The <code>ContextHelper</code> class is used as a helper class to remove the context-specific
 * calls from Jameleon Tags.
 */
public final class ContextHelper {

    /**
     * This class was not meant to be instantiated
     */
    private ContextHelper(){
    }
    
    // Pass-through methods (no change from JellyContext)

    public static void setVariable(JellyContext context, String name, Object value) {
        context.setVariable(name,value);
    }

    public static Object getVariable(JellyContext context, String name) {
        Object value = null;
        if (context != null && name != null && name.length() > 0) {
            value = context.getVariable(name);
        }
        return value;
    }

    public static void removeVariable(JellyContext context, String name) {
        context.removeVariable(name);
    }

    // Jameleon-added methods for convenience

    public static String getVariableAsString(JellyContext context, String name) {
        String value = null;
        if (context != null && name != null && name.length() > 0) {
            Object obj = context.getVariable(name);
            if (obj != null) {
                value = (String) obj;
            }
        }
        return value;
    }

    public static boolean getValueAsBooleanWithConfig(JellyContext context, String contextName, String configName, boolean defaultValue){
        boolean value = Boolean.valueOf(Configurator.getInstance().getValue(configName, defaultValue+"")).booleanValue();
        if (ContextHelper.getVariable(context, contextName) != null) {
            value = ContextHelper.getVariableAsBoolean(context, contextName);
        }
        return value;
    }

    public static int getValueAsIntWithConfig(JellyContext context, String contextName, String configName, int defaultValue){
        int value;
        try{
            value = Integer.parseInt(Configurator.getInstance().getValue(configName, defaultValue+""));
        }catch(NumberFormatException nfe){
            value = -1;
        }
        if (ContextHelper.getVariable(context, contextName) != null) {
            value = ContextHelper.getVariableAsInt(context, contextName);
        }
        return value;
    }

    public static String getValueAsStringWithConfig(JellyContext context, String contextName, String configName, String defaultValue){
        String value = Configurator.getInstance().getValue(configName, defaultValue);
        String contextValue = ContextHelper.getVariableAsString(context, contextName);
        if (contextValue != null) {
            value = contextValue;
        }
        return value;
    }

    public static boolean getVariableAsBoolean(JellyContext context, String name) {
        Object o = ContextHelper.getVariable(context, name);
        boolean returnValue = false;
        if (o != null && o instanceof Boolean) {
            returnValue = ((Boolean)o).booleanValue();
        } else if (o != null && o instanceof String) {
            returnValue = Boolean.valueOf((String)o).booleanValue();
        } 
        return returnValue;
    }

    public static int getVariableAsInt(JellyContext context, String name) {
        int returnValue = -1;
        Object o = ContextHelper.getVariable(context, name);
        if (o != null && o instanceof Integer) {
            returnValue = ((Integer)o).intValue();
        } else if (o != null && o instanceof String) {
            try{
                returnValue = Integer.parseInt((String)o);
            }catch(NumberFormatException nfe){
                returnValue = -1;
            }
        } 
        return returnValue;
    }

    public static List getVariableAsList(JellyContext context, String key){
        Object obj = context.getVariable(key);
        return makeList(obj);
    }

    public static boolean isVariableNull(JellyContext context, String name) {
        Object obj = context.getVariable(name);
        boolean isNull = false;
        if (obj != null) {
            if (obj instanceof String) {
                String str = (String)obj;
                if (str.equals("")) {
                    isNull = true;
                }
            }else if (obj instanceof List) {
                List l = (List)obj;
                if ( l.size() < 1 ) {
                    isNull = true;
                }
            }
        }else{
            isNull = true;
        }
        return isNull;
    }

    public static List makeList(Object obj) {
        List l;
        if ( obj != null && obj instanceof List ) {
            l = (List)obj;
        }else{
            l = new ArrayList();
            if ( obj != null ) {
                l.add(obj);
            }
        }
        return l;
    }

    public static void removeVariables(JellyContext context, List vars){
        if (vars != null) {
            Iterator it = vars.iterator();
            while (it.hasNext()) {
                context.removeVariable((String)it.next());
            }
        }
    }
}

