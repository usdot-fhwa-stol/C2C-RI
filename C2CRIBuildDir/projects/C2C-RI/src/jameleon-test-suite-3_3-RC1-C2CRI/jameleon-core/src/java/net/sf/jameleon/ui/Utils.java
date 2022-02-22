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
package net.sf.jameleon.ui;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import net.sf.jameleon.util.Configurator;

public class Utils{

    public static ClassLoader createClassLoader(){
        Configurator.clearInstance();
        Utils utils = new Utils();
        URL[] urls = utils.getURLS();
        Configurator.clearInstance();
        return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    private URL[] getURLS(){
        Set urlsS = getEntriesForClasspath();
        URL[] urls = new URL[urlsS.size()];
        Iterator it = urlsS.iterator();
        for (int i = 0; it.hasNext(); i++) {
            urls[i] = (URL) it.next();
        }
        return urls;
    }

    private Set getEntriesForClasspath(){
        Set fileNames = Configurator.getInstance().getKeysStartingWith("classpath.entry");
        return getURLS(fileNames);
    }

    private Set getURLS(Set fileNames){
        Set urls = new HashSet();
        Iterator it = fileNames.iterator();
        String value;
        File f;
        while (it.hasNext()) {
            value = Configurator.getInstance().getValue((String)it.next());
            f = new File(value);
            if (f.exists()) {
                try{
                    urls.add(f.toURL());
                }catch(Exception e){
                    System.err.println("Couldn't add "+value);
                }
            }
        }
        return urls;
    }

}
