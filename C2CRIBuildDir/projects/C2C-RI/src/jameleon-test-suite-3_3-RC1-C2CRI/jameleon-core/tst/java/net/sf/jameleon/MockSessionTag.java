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
package net.sf.jameleon;

import net.sf.jameleon.result.SessionResult;

/**
 * @jameleon.function name="junit-test-session-tag"
 */
public class MockSessionTag extends SessionTag {

    public boolean applicationStarted;

    public MockSessionTag() {
        super();
        setParent(new MockTestCaseTag());
        sessionResults = new SessionResult(fp);
    }

    /**
     * Used for the plug-in to implement if something special is required during the session setup.
     * The properties from the CSV and testEnvironment.properties are setup before this method is called.
     * The default implementation does nothing.
     */
    public void startApplication(){
        applicationStarted = true;
    }

    public Object getAncestorTag(Class c){
        return findAncestorWithClass(c);
    }

}
