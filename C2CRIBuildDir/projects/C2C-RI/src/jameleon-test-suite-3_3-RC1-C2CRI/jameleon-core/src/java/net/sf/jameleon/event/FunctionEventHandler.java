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

import net.sf.jameleon.function.FunctionTag;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FunctionEventHandler{

    private static FunctionEventHandler eventHandler;
    private final List functionListeners = Collections.synchronizedList(new LinkedList());;

    private FunctionEventHandler(){}

    public static FunctionEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new FunctionEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    public void addFunctionListener(FunctionListener fl){
        if (fl != null && !functionListeners.contains(fl)){
            functionListeners.add(fl);
        }
    }

    public List getFunctionListeners(){
        return functionListeners;
    }

    public void removeFunctionListener(FunctionListener fl){
        functionListeners.remove(fl);
    }

    public void beginFunction(FunctionTag ft, int rowNum){
        FunctionEvent fe = new FunctionEvent(ft);
        synchronized(functionListeners){
            Iterator it = functionListeners.iterator();
            FunctionListener fl;
            while (it.hasNext()) {
                fl = (FunctionListener)it.next();
                fl.beginFunction(fe, rowNum);
            }
        }
    }

    public void endFunction(FunctionTag ft, int rowNum){
        FunctionEvent fe = new FunctionEvent(ft);
        synchronized(functionListeners){
            Iterator it = functionListeners.iterator();
            FunctionListener fl;
            while (it.hasNext()) {
                fl = (FunctionListener)it.next();
                fl.endFunction(fe, rowNum);
            }
        }
    }

}
