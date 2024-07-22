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
package net.sf.jameleon.util;

import net.sf.jameleon.Storable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;

/**
 * A bean that keeps track of a list of <code>Storables</code> and calls the <code>store</code> on each at the appropriate time.
 * The appropriate times are:
 * <ol>
 *     <li><b>ON_ERROR_EVENT</b> - When some error occurs.</li>
 *     <li><b>ON_STATE_CHANGE_EVENT</b> - Everytime the object changes state.</li>
 *     <li><b>ON_NO_EVENT</b> - Never store the state.</li>
 * </ol>
 * The default event is <b>ON_ERROR_EVENT</b>.
 * When one of these event occurs, first the <code>storableEvent</code> event is checked to see if the event
 * is to be recorded. If the event is not to be recorded, then nothing happens. If the event is to be recorded,
 * then the {@link net.sf.jameleon.Storable#store(java.lang.String,int)} is called for each 
 * {@link net.sf.jameleon.Storable} in the list of registered Storables.
 */
public class StateStorer {


    protected static StateStorer stateStorer;
    /**
     * When to store the state of the <code>Storables</code>.
     */
    protected int storableEvent;
    /**
     * A list of storables whose state will be stored at the appropriate time.
     */
    protected Set storables;
    /**
     * The default event at which to store the state of the object.
     */
    public final static String DEFAULT_STORE_DIR         = JameleonDefaultValues.RESULTS_DIR;
    /**
     * The directory to store the <code>Storables</code>' state to.
     */
    protected File storeDir;
    /**
     * Only store the state of the object on an unexpected error ( not an assertion error ).
     */
    public final static int ON_ERROR_EVENT                = 1;
    /**
     * Store the state of the object everytime the state itself changes.
     */
    public final static int ON_STATE_CHANGE_EVENT         = 2;
    /**
     * Don't store the state of the object.
     */
    public final static int ON_NO_EVENT                   = 3;
    /**
     * Don't store the state of the object.
     */
    public final static int ON_COMMAND_EVENT              = 4;
    /**
     * The default event at which to store the state of the object.
     */
    public final static int DEFAULT_EVENT         = ON_ERROR_EVENT;
    
    protected static Logger log = LogManager.getLogger(StateStorer.class.getName());
    protected boolean available = true;
    /**
     * Since this is a singleton, a private constructor is in order.
     * Initializes all values to their defaults.
     */
    private StateStorer(){
        storables = Collections.synchronizedSet(new HashSet());
        storableEvent = DEFAULT_EVENT;
        storeDir = new File(DEFAULT_STORE_DIR);
    }

    public static StateStorer getInstance(){
        if (stateStorer == null) {
            stateStorer = new StateStorer();
        }
        return stateStorer;
    }

    public void reset(){
        getStorables().clear();
        storableEvent = DEFAULT_EVENT;
        storeDir = new File(DEFAULT_STORE_DIR);
    }

    /**
     * Tests whether the state of a storable is to be stored or not.
     * @param event - use <code>ON_ERROR</code> for an error or <code>ON_STATE_CHANGE</code> for a simple state change.
     * @return true if the state 
     */
    protected boolean isStateStorable(int event){
	return ( (event == ON_COMMAND_EVENT) || (storableEvent != ON_NO_EVENT && storableEvent >= event) );
    }

    /**
     * Records that an <code>event</code> occured. If the event is set as a storable event at runtime,
     * then thee state of all Storables will be recorded.
     * @param event - The event type that occured.
     */
    public void eventOccured(int event){
        if (isStateStorable(event)) {
            storeState(event);
        }
    }

    /**
     * @return the <code>event</code> at which the <code>Storables</code>' state will be stored.
     */
    public int getStorableEvent(){
        return storableEvent;
    }

    /**
     * Sets the event at which the state of the storables will be saved.
     * @param event - the event at which the <code>Storables</code>' state will be stored. 
     * If an invalid <code>event</code> is passed, the <code>storableEvent will not be changed.
     */
    public void setStorableEvent(int event){
        if (ON_ERROR_EVENT == event || ON_STATE_CHANGE_EVENT == event || ON_NO_EVENT == event ) {
            this.storableEvent = event;
        }
    }

    /**
     * Gets the list of storables to be stored when the appropriate event occurs.
     * @return The list of storables to be stored when the appropriate event occurs.
     */
    public Set getStorables(){
        return storables;
    }

    /**
     * Adds a <code>Storable</code> for which the state will be stored when the appropriate event occurs..
     * @param s - a <code>Storable</code> object..
     */
    public synchronized void addStorable(Storable s){
        while (!available) {
            try{
                wait();
            } catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			}
        }
        available = false;
        storables.add(s);
        available = true;
        notifyAll();
    }

    /**
     * Removes a <code>Storable</code> for which the state will be stored when the appropriate event occurs..
     * @param s - the <code>Storable</code> object to removed from the list of objects
     */
    public synchronized void removeStorable(Storable s){
        while (!available) {
            try{
                wait();
            } catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			}
        }
        available = false;
        storables.remove(s);
        available = true;
        notifyAll();
    }

    protected synchronized void storeState(int event){
        while (!available) {
            try{
                wait();
            } catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			}
        }
        available = false;
        Iterator it = storables.iterator();
        while (it.hasNext()) {
            Storable s = (Storable)it.next();
            String storeFileS = s.getStoreToFileName(event);
            if (storeFileS != null) {
                File storeFile = new File(storeDir, storeFileS);
                try{
                    s.store(storeFile.getPath(), event);
                }catch(IOException ioe){
                    log.debug("Unable to write state of storable to file " + JameleonUtility.getStack(ioe));
                }
            }
        }
        available = true;
        notifyAll();
    }

    public File getStoreDir(){
        return this.storeDir;
    }

    public void setStoreDir(File dir){
        this.storeDir = dir;
    }
}
