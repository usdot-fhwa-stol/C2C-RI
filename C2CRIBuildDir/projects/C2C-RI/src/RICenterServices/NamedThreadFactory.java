/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.applayer;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

/**
 * A factory for creating Thread objects that can be identified by a specified name.
 */
public class NamedThreadFactory implements ThreadFactory{

    /** The Constant poolNumber. */
    static final AtomicInteger poolNumber = new AtomicInteger(1);
     
    /** The thread number. */
    final AtomicInteger threadNumber = new AtomicInteger(1);
    
    /** The name prefix. */
    final String namePrefix;
    
    /**
     * Instantiates a new named thread factory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     */
    public NamedThreadFactory(String name){
        namePrefix = name+"-" + poolNumber.getAndIncrement() + "-thread-";
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    public Thread newThread(Runnable r){
        Thread t = new Thread(null, r, namePrefix + threadNumber.getAndIncrement(),
                0);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e){
                    Logger.getLogger(t.getName()).error(e.getMessage(), e);
                }
        });
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

}
