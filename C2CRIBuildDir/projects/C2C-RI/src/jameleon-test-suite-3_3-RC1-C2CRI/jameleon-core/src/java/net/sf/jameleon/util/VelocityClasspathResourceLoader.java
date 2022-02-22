/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2005 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.util;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityClasspathResourceLoader extends ClasspathResourceLoader
{

    public void init( ExtendedProperties configuration)
    {
        rsvc.info("VelocityClasspathResourceLoader : initialization starting.");
        rsvc.info("VelocityClasspathResourceLoader : initialization complete.");
    }

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param name name of template to get
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *         in  classpath.
     */
    public synchronized InputStream getResourceStream( String name )
        throws ResourceNotFoundException {


        InputStream input = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
            input= classLoader.getResourceAsStream( name );
        } else {
            input= classLoader.getResourceAsStream( name );
            if (input == null) {
                classLoader = this.getClass().getClassLoader();
                input= classLoader.getResourceAsStream( name );
            }
        }
        if (input == null) {
            throw new ResourceNotFoundException("Could not find "+name+" in the classpath.");
        }
        return input;

    }
    
}

