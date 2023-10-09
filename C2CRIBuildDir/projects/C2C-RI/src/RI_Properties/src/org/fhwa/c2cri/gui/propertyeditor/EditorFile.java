package org.fhwa.c2cri.gui.propertyeditor;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


/**
 * The Class EditorFile.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorFile extends File {
    
    /** The group array. */
    Vector groupArray = new Vector();
    
    /** The is modified. */
    transient EditorModified isModified = new EditorModified();
    
    /** The errors. */
    String errors;
    
    /**
     * Instantiates a new editor file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param path the path
     */
    public EditorFile(String path) {
        super(path);
    }
    
    /**
     * Checks if is modified.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is modified
     */
    public boolean isModified() {
        return isModified.getState();
    }
    
    /**
     * Adds the group.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param g the g
     */
    public void addGroup(Group g){
        groupArray.add(g);
    }
    
    /**
     * Inits the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void init(){
        groupArray = new Vector();
        isModified.setState(false);
        errors = "";
    }
    
    /**
     * Group at.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param i the i
     * @return the group
     */
    public Group groupAt(int i){
        return (Group)groupArray.elementAt(i);
    }
    
    /**
     * Num group.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the int
     */
    public int numGroup(){
        return groupArray.size();
    }
    
    /**
     * Sets the modified.
     *
     * @param b the new modified
     */
    public void setModified(boolean b){
        isModified.setState(b);      
    }
    
    /**
     * Sets the error parsing.
     *
     * @param e the new error parsing
     */
    public void setErrorParsing(String e){
        errors = e;
    }
    
    /**
     * Gets the error parsing.
     *
     * @return the error parsing
     */
    public String getErrorParsing(){
        return errors;
    }
    
    /**
     * Adds the observer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param o the o
     */
    public void addObserver(Observer o){
        isModified.addObserver(o);
    }
    
}

class EditorModified extends Observable {
    boolean isModified = false;
    
    public void setState(boolean b){
        isModified = b;
        setChanged();
        notifyObservers();
    }
    
    public boolean getState(){
        return isModified;
    }
}
