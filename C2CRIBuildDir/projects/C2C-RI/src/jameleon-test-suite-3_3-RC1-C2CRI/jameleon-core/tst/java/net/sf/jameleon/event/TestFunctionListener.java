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

public class TestFunctionListener implements FunctionListener {

    public boolean beginFunctionCalled, endFunctionCalled;
    public FunctionEvent beginFunctionEvent, endFunctionEvent;

    public void beginFunction(FunctionEvent event, int rowNum) {
        beginFunctionCalled = true;
        beginFunctionEvent = event;
    }

    public void endFunction(FunctionEvent event, int rowNum) {
        endFunctionCalled = true;
        endFunctionEvent = event;
    }

    public void reset(){
        beginFunctionCalled = false;
        beginFunctionEvent = null;
        endFunctionCalled = false;
        endFunctionEvent = null;
    }
}

