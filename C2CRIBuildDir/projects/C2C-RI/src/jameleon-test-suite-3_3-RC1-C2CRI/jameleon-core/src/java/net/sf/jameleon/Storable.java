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

import java.io.IOException;

/**
 * Used to represent an object that needs to store it's state. This is intended to store the state that an function point was in
 * at a certain moment.
 */
public interface Storable{

    /**
     * Stores the current state of the object to a given <code>File</code>.
     * @param filename - the name of file to store the state to
     * @param event - The even that occured (error, state change ...)
     * @throws IOException - If the state of the object could not be stored in File <code>f</code>.
     */
    public void store(String filename, int event) throws IOException;


    /**
     * Gets the filename to store the state of the application to. 
     * The default implementation is to simply use timestamps. 
     * If this is not the desired behavior, override this method.
     * @param event - the StateStorer Event
     * @return the appropriate filename which starts with ERROR- if the StateStorer Event was an Error
     */
    public String getStoreToFileName(int event);

}
