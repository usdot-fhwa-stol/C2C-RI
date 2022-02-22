/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.result;

import net.sf.jameleon.bean.FunctionalPoint;

public class MockCountableResult extends MockTestResultWithChildren implements CountableResult {

    protected boolean countFailure;

    public MockCountableResult(){
        super();
    }

    public MockCountableResult(FunctionalPoint tag){
        super(tag);
    }

    public MockCountableResult(FunctionalPoint tag, HasChildResults parentResult){
        super(tag, parentResult);
    }

    public void countFailure(){
        countFailure = true;
    }

    public boolean isCountableResultFailed(){
        return countFailure;
    }

}
