/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.logger;


import java.io.Serializable;
import org.apache.logging.log4j.Level;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;





/**
 *
 * @author TransCore ITS, LLC
 */
public class ActionLogAppender extends AbstractAppender {

    /**
     * The listener for append events
     */
    protected ActionLogAppenderListener listener = null;
    
    /**
     * Create new instance.
     */
    ActionLogAppender(String sName, Filter oFilter, Layout<? extends Serializable> oLayout, boolean bIgnoreExceptions, Property[] oProperties)
	{
		super(sName, oFilter, oLayout, bIgnoreExceptions, oProperties);
	}


    /**
     * Add a new listener to this appender.
     * @param actionListener 
     */
    public void addListener(ActionLogAppenderListener actionListener){
        listener = actionListener;
    }
    
    /**
     * Remove the listener from the appender.
     * @param actionListener 
     */
    public void removeListener(ActionLogAppenderListener actionListener){
        listener = null;
    }    


	@Override
    public void append(LogEvent event){
        if (this.listener != null)
		{
			Filter oF = getFilter();
            if (event.getLevel().isLessSpecificThan(Level.INFO) && oF == null || oF.filter(event) == Filter.Result.ACCEPT)
                this.listener.appenderUpdate(event.getInstant().getEpochMillisecond(), event.getMessage().getFormattedMessage());
        }
    }


    public void close() {
        listener = null;
    }
}
