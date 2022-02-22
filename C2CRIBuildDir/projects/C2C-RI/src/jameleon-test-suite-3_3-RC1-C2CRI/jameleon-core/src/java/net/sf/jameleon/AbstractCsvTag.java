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

import java.io.File;

import net.sf.jameleon.data.AbstractFileDrivableTag;
import net.sf.jameleon.data.CsvDataDriver;
import net.sf.jameleon.data.DataDriver;
import net.sf.jameleon.util.Configurator;

/**
 * This DataDrivable tag is an implementation of a CSV data source.
 */
public abstract class AbstractCsvTag extends AbstractFileDrivableTag {
    
    protected CsvDataDriver csv;
    protected String name;
    protected String csvFileName;

    /**
     * Calculates the location of the state to be stored for any tags under this tag.
     * The result should be a relative path that simply has the row number in it along
     * with some unique indentifier for this tag like the handle name or something.
     * @return the location of the state to be stored for any tags under this tag minus the baseDir calculation stuff.
     */
    protected String getNewStateStoreLocation(int rowNum){
        String fName = getCsvFile().getName();
        int index = fName.lastIndexOf(".");
        if (index == -1) {
            index = fName.length();
        }
        fName = fName.substring(0, index);
        if (rowNum > 0) {
            fName = fName + File.separator + rowNum;
        }
        return fName;
    }

    /**
     * Gets an error message to be displayed when a error occurs due to the DataDriver.
     * @return an error message to be displayed when a error occurs due to the DataDriver.
     */
    protected String getDataExceptionMessage(){
        return "Trouble reading file "+getCsvFile();
    }

    /**
     * Sets up the DataDriver by calling any implementation-dependent
     * methods.
     */
    protected void setupDataDriver(){
        csv.setFile(getCsvFile());
        if (getCsvCharset() == null) {
            Configurator config = Configurator.getInstance();
            config.setConfigName(tct.getJameleonConfigName());
            setCsvCharset(config.getValue("csvCharset"));
        }
        if (getCsvCharset() != null && getCsvCharset().trim().length() > 0) {
            csv.setEncoding(getCsvCharset());
        }
    }

    /**
     * Gets the file that will be used as a data source.
     * @return The csv file to run the test against
     */
    public File getCsvFile(){
        File csvFile = null;
        if (csvFileName != null && csvFileName.length() > 0) {
            csvFile = new File(tct.getCsvDir(false), csvFileName);
        }else{
            csvFile = new File(tct.getCsvDir(true), name+".csv");
        }
        return csvFile;
    }


    protected DataDriver getDataDriver(){
        csv = new CsvDataDriver();
        return csv;
    }

    /**
     * Gets the relative path to the name of the csv file. This does
     * not use any logic in considering testEnvironment nor organization.
     * @return the relative path and name of the csv file to read in.
     */
    public String getCsvFileName() {
        return this.csvFileName;
    }

    /**
     * Sets the relative path to the name of the csv file. This does
     * not use any logic in considering testEnvironment nor organization.
     * @param csvFileName - the relative path and name of the csv file to read in.
     * @jameleon.attribute
     */
    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    /**
     * @return the name of the csv file to read in.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the csv file to read in.
     * @param name - The name of the csv file.
     * @jameleon.attribute
     */ 
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The directory of the csv file (relative to baseDir).
     */ 
    public File getCsvDir() {
        return getDataDir();
    }

    /**
     * Sets the directory of the csv file to read in (relative to baseDir).
     * @param csvDir - The directory of the csv file.
     * @jameleon.attribute
     */ 
    public void setCsvDir(File csvDir) {
        setDataDir(csvDir);
    }

    /**
     * @return the separator used to separate CSV files.
     */
    public char getCsvValueSeparator(){
        return csv.getDelimiter();
    }

    /**
     * Sets the separator used to separate CSV files.
     * @param valueSeparator - The separator used to separate CSV files.
     * @jameleon.attribute
     */
    public void setCsvValueSeparator(char valueSeparator){
        csv.setDelimiter(valueSeparator);
    }

    /**
     * Sets the character set to use when reading in the CSV file
     * @param charset - the character set to use when reading in the CSV file (Defaults to UTF-8)
     * @jameleon.attribute
     */
    public void setCsvCharset(String charset){
        setCharset(charset);
    }

    public String getCsvCharset() {
        return charset;
    }

}
