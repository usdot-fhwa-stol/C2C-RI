/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.event;

import java.util.EventListener;

/**
 * A listener for TestSuite events. Currently only two events are tracked:
 * <ul>
 *     <li>beginTestSuite - gets called before any test cases in the provided test suite are executed.</li>
 *     <li>endTestSuite   - gets called after all test cases in the proced test suite are executed.</li>
 * </ul>
 */
public interface TestSuiteListener extends EventListener{

    /**
     * Gets called before the execution of a test suite
     * @param event - a TestSuiteEvent Object
     */
    public void beginTestSuite(TestSuiteEvent event);

    /**
     * Gets called after the execution of a test suite
     * @param event - a TestSuiteEvent Object
     */
    public void endTestSuite(TestSuiteEvent event);

}
