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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Am implementation of @{link DataDriver} for CSV Files.
 * This implementation can accept different delimiters. The
 * default delimiter is ','.
 */
public class CsvDataDriver implements DataDriver{
    protected File csvFile;
    protected BufferedReader in;
    protected final static char DEFAULT_DELIMETER = ',';
    protected char delimiter = DEFAULT_DELIMETER;
    protected String encoding = JameleonDefaultValues.FILE_CHARSET;
    protected List keys;
    protected String line;

    /**
     * Default construtor. After calling this constructor,
     * setFile() will need to be set.
     */
    public CsvDataDriver(){
        csvFile = null;
        in = null;
        keys = new ArrayList();
    }

    /**
     * Sets the file to be read in to <code>csvFile</code>
     * @param csvFile - the file to be used for parsing
     */
    public CsvDataDriver(File csvFile){
        this();
        this.csvFile = csvFile;
    }

    /**
     * Sets the file and the delimiter of the file to be read in
     * @param csvFile - the file to be used for parsing
     * @param delimiter -  the field delimiter of the csv file.
     */
    public CsvDataDriver(File csvFile, char delimiter){
        this(csvFile);
        this.delimiter = delimiter;
    }

    /**
     * Closes the handle to the data source
     */
    public void close() {
        keys.clear();
        if (in != null) {
            try{
                in.close();
            }catch(IOException ioe){}
        }
    }

    /**
     * Opens the handle to the data source
     * @throws IOException when the data source can not be found.
     */
    public void open() throws IOException {
        if (csvFile != null) {
            try{
                in = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), encoding));
            }catch(UnsupportedEncodingException uee){
                throw new IOException(encoding + " is an unsupported encoding type");
            }
        }else{
            throw new IOException("No file set to open");
        }
    }

    /**
     * Gets the encoding of the csv file. Defaults to UTF-8
     * @return the encoding of the csv file
     */
    public String getEncoding(){
        return encoding;
    }

    /**
     * Sets the encoding of the csv file.
     * @param encoding - the encoding of the csv file
     */
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }

    /**
     * Gets the CSV file used as a datasource.
     * @return the CSV file used as a datasource.
     */
    public File getFile(){
        return csvFile;
    }

    /**
     * Sets the CSV file used as a datasource.
     * @param csvFile - the CSV file used as a datasource.
     */
    public void setFile(File csvFile){
        this.csvFile = csvFile;
    }

    /**
     * Gets the next row from the data source
     * @return a key-value HashMap representing the data row or null if
     * no data row is available
     * @throws IllegalStateException if there are more values than keys
     */
    public Map getNextRow() {
        Map vars = null;
        try{
            if (line == null) {
                readLine();
            }
            if (line != null) {
                vars = new HashMap();
                List values = parseLine(line, false);
                if (values.size() > keys.size()) {
                    throw new IllegalStateException(csvFile.getPath() + " has more values than keys!");
                }
                String tmp = null;
                for (int index = 0; index < values.size(); index++) {
                    tmp = (String)values.get(index);
                    if (tmp == null || tmp.length() == 0) {
                        tmp = null;
                    }
                    vars.put(keys.get(index),tmp);
                }
            }
        }finally{
            line = null;
        }
        return vars;
    }

    /**
     * Gets the field delimiter for the csv file
     * @return the field delimiter for the csv file
     */
    public char getDelimiter(){
        return delimiter;
    }

    /**
     * Sets the field delimiter for the csv file
     * @param delimiter - the field delimiter for the csv file
     */
    public void setDelimiter(char delimiter){
        this.delimiter = delimiter;
    }

    /**
     * Sets the key columns from the csv file
     */
    protected void setKeys(){
        if (keys.size() == 0) {
            getNextUcommentedLine();
            if (line != null) {
                keys = parseLine(line, true);
                line = null;
            }
        }
    }

    /**
     * Tells whether the data source has another row
     * @return true if the data source still has more rows
     */
    public boolean hasMoreRows() {
        boolean moreRows = false;
        if (line == null) {
            readLine();
        }
        if (line != null) {
            moreRows = true;
        }
        return moreRows;
    }

    protected List parseLine(String line, boolean keysLine){

        List list = new ArrayList();
        if (isLineCommented(line)) { return list; }

        String[] values = line.split( "\\"+String.valueOf(delimiter), -1 );
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            String trimmedValue = value.trim();
            if ( trimmedValue.startsWith("\"") && trimmedValue.endsWith("\"") ) {
                list.add( trimmedValue.subSequence(1, trimmedValue.length()-1) );
            } else {
                if (keysLine) {
                    list.add(trimmedValue);
                } else {
                    list.add(value);
                }
            }
        }

        return list;
    }

    protected boolean isLineCommented(String line){
        boolean commented = false;
        if (line != null && line.startsWith("#")) {
            commented = true;
        }
        return commented;
    }

    protected void readLine(){
        line = null;
        setKeys();
        getNextUcommentedLine();
    }

    protected void getNextUcommentedLine(){
        try{
            String tempLine = null;
            while (in != null && 
                   in.ready() && 
                   line == null &&
                   (tempLine = new String(in.readLine().getBytes(encoding),encoding)) != null) {
                if (!isLineCommented(tempLine)) {
                    line = tempLine;
                }
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

}
