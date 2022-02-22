
package org.fhwa.c2cri.plugin.c2cri.event;

import java.util.EventObject;

/**
 * Represents a verification event.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class VerificationEvent extends EventObject{

    /**
     * Instantiates a new verification event.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param source the source
     */
    public VerificationEvent(Object source){
        super(source);
    }

}
