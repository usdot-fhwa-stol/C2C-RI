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
package net.sf.jameleon.data;

import net.sf.jameleon.util.JameleonDefaultValues;

import java.io.File;

/**
 * This DataDrivable implementation is for file based data sources.
 */
public abstract class AbstractFileDrivableTag extends AbstractDataDrivableTag {

    protected File dataDir = JameleonDefaultValues.DATA_DIR;
    protected String charset;
    protected String fileName;

    /**
     * Sets the character set to use to read the data file in.
     * @param charset the character set to use to read the data file in.
     * @jameleon.attribute
     */
    public void setCharset(String charset){
        this.charset = charset;
    }

    /**
     * Sets the directory of where the data will be looked for.
     * The baseDir is then prepended on this
     * @param dataDir the directory of where the data will be looked for.
     * @jameleon.attribute
     */
    public void setDataDir(File dataDir){
        this.dataDir = dataDir;
    }

    /**
     * Sets the name of the file to use as a data source.
     * @param fileName the name of the file to use as a data source.
     * @jameleon.attribute
     */
    public void setFile(String fileName){
        this.fileName = fileName;
    }

    /**
     * Gets the directory of where the data will be looked for.
     * @return the directory of where the data will be looked for.
     */
    public File getDataDir(){
        String testEnvironment = tct.getTestEnvironment();
        String organization = tct.getOrganization();
        String filename = tct.getBaseDir().getPath() + File.separator + dataDir.getPath() + File.separator;
        if (testEnvironment != null && testEnvironment.length() > 0){
            filename += testEnvironment + File.separator;
        }
        if (organization != null && organization.length() > 0 ) {
            filename += organization + File.separator;
        }
        return new File(filename);
    }

    /**
     * Gets the directory of where the data will be looked for.
     * @param calculate - if set to false, then the plain old dataDir is return. otherwise
     *                    a calculation is done to add baseDir and testEnvironment and organization.
     * @return the directory of where the data will be looked for.
     */
    public File getDataDir(boolean calculate){
        File f = dataDir;
        if (calculate) {
            f = getDataDir();
        }
        return f;
    }
}
