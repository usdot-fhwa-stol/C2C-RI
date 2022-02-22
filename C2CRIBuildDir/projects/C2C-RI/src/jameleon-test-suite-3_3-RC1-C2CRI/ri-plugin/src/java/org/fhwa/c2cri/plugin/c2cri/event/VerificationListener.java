package org.fhwa.c2cri.plugin.c2cri.event;

import java.util.EventListener;

/**
 * The listener interface for receiving verification events.
 * The class that is interested in processing a verification
 * event implements this interface. When
 * the verification event occurs, that object's appropriate
 * method is invoked.
 *
 * @see VerificationEvent
 */
public interface VerificationListener extends EventListener{

    /**
     * Gets called when a verification result is being reported.
     *
     * @param event - a VerificationEvent Object
     */
    public void verificationUpdate(VerificationEvent event);


}
