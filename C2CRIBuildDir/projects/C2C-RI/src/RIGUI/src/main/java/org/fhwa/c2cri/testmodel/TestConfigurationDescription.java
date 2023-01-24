/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.util.Date;

/**
 * The Class TestConfigurationDescription captures the description details of each Test Configuration file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestConfigurationDescription {

    /** The id. */
    private int id;
    
    /** The valid. */
    private boolean valid;
    
    /** The filename. */
    private String filename;
    
    /** The file description. */
    private String fileDescription;
    
    /** The file date. */
    private Date fileDate;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the filename.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename.
     *
     * @param filename the new filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the file description.
     *
     * @return the file description
     */
    public String getFileDescription() {
        return fileDescription;
    }

    /**
     * Sets the file description.
     *
     * @param fileDescriptions the new file description
     */
    public void setFileDescription(String fileDescriptions) {
        this.fileDescription = fileDescriptions;
    }

    /**
     * Gets the file date.
     *
     * @return the file date
     */
    public Date getFileDate() {
        return fileDate;
    }

    /**
     * Sets the file date.
     *
     * @param fileDate the new file date
     */
    public void setFileDate(Date fileDate) {
        this.fileDate = fileDate;
    }

    /**
     * Checks if is valid.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the valid.
     *
     * @param valid the new valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {

        if (this == that) {
            return true;
        }
        if (!(that instanceof TestConfigurationDescription)) {
            return false;
        }
        TestConfigurationDescription tmp = (TestConfigurationDescription) that;
        return ((valid==tmp.valid)
                && filename.equals(tmp.filename)
                && fileDescription.equals(tmp.fileDescription)
                && fileDate.equals(tmp.fileDate));
                
    }





    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (valid == true ? 1:0);
        hash = 31 * hash + (null == filename ? 0 : filename.hashCode());
        hash = 31 * hash + (null == fileDescription ? 0 : fileDescription.hashCode());
        hash = 31 * hash + (null == fileDate ? 0 : fileDate.hashCode());
        return hash;


    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return("ID: "+id+"  Name: "+filename+"  Description: "+fileDescription+"  Date: "+fileDate);
    }
    
}
