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
package net.sf.jameleon.event;


import java.util.EventListener;

/**
 * A listener for Test Run events. A Test Run is the action of running one or tests
 * through the GUI or the command line.
 *
 * Currently the following two events are tracked:
 * <ul>
 *     <li>beginTestRun - gets called before any tests are run.</li>
 *     <li>endTestRun   - gets called after all tests are executed.</li>
 * </ul>
 */
public interface TestRunListener extends EventListener{
    /**
     * Gets called before the execution of a test run
     * @param event - a TestRunEvent Object
     */
    public void beginTestRun(TestRunEvent event);

    /**
     * Gets called after the execution of a test suite
     * @param event - a TestRunEvent Object
     */
    public void endTestRun(TestRunEvent event);

}
