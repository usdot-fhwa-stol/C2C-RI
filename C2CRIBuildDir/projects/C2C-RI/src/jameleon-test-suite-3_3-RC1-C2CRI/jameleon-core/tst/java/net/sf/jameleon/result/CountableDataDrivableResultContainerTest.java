/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CountableDataDrivableResultContainerTest extends TestCase {

    public CountableDataDrivableResultContainerTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( CountableDataDrivableResultContainerTest.class );
    }

     public void testIsCountable(){
         CountableDataDrivableResultContainer cddrc = new CountableDataDrivableResultContainer(new FunctionalPoint());
         assertTrue(cddrc.isCountable());
     }

}
