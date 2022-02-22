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
package net.sf.jameleon.util;

import org.apache.tools.ant.launch.Locator;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Extracts contents from jar file to the defined directory.
 */
public class JarResourceExtractor {

    private JarFile jarFile;

    /**
     * Searches the classpath for the jameleon-core.jar file to use
     * to find the desired resources.
     * @throws IOException if a jar file containing the jameleon core version config
     * file cannot be found.
     */
    public JarResourceExtractor() throws IOException {
        File f = Locator.getResourceSource(Thread.currentThread().getContextClassLoader(),
                JameleonDefaultValues.JAMELEON_VERSION_FILE);
        if (f == null){
            f = Locator.getResourceSource(getClass().getClassLoader(),
                JameleonDefaultValues.JAMELEON_VERSION_FILE);
        }
        jarFile = new JarFile(f);
    }

    /**
     * Gets the jar file that contains the desired resources
     * @return
     */
    public JarFile getJarFile(){
        return jarFile;
    }

    /**
     * Extracts the files from the jar file to the desired directory.
     * @param baseDirectory The directory that contains the files to extract
     * @param directoryToExtractTo The directory to copy the files to
     */
    public void extractFilesInDirectory(String baseDirectory, File directoryToExtractTo)
            throws IOException {
        if (jarFile != null){
            Enumeration entries = jarFile.entries();
            ZipEntry entry; String name;
            while(entries.hasMoreElements()){
                entry = (ZipEntry)entries.nextElement();
                name = entry.getName();
                if (name.startsWith(baseDirectory+"/")){
                    if (entry.isDirectory()){
                        JameleonUtility.createDirStructure(new File(directoryToExtractTo, name));
                    }else{
                        extractFile(entry, directoryToExtractTo);
                    }
                }
            }
        }
    }

    public void extractFile(ZipEntry entry, File extractDir)
            throws IOException {
        InputStream in = getJarFile().getInputStream(entry);
        File outputFile = new File(extractDir, entry.getName());
        OutputStream out = new FileOutputStream(outputFile);
        try{
            int c;
            while ((c = in.read()) != -1){
                out.write(c);
            }
        }finally{
            in.close();
            out.close();
        }
    }
}
