/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.subpub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageSubscription;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;



/**
 * The Class Subscription.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Subscription {

    /** The subscriptionHeaderContents. */
    private C2CMessageSubscription subscriptionHeaderContents;

    /**
     * The Enum SUBSCRIPTIONSTATE.
     */
    public static enum SUBSCRIPTIONSTATE {

        /** The initializing. */
        INITIALIZING, /** The active. */
 ACTIVE, /** The updating. */
 UPDATING, /** The completed. */
 COMPLETED, /** The error. */
 ERROR
    };

    /**
     * The Enum SUBSCRIPTIONACTION.
     */
    public static enum SUBSCRIPTIONACTION {

        /** The reserved. */
        RESERVED, /** The newsubscription. */
 NEWSUBSCRIPTION, /** The replacesubscription. */
 REPLACESUBSCRIPTION, /** The cancelsubscription. */
 CANCELSUBSCRIPTION, /** The cancellallpriorsubscriptions. */
 CANCELLALLPRIORSUBSCRIPTIONS
    };

    /**
     * The Enum SUBSCRIPTIONTYPE.
     */
    public static enum SUBSCRIPTIONTYPE {

        /** The reserved. */
        RESERVED, /** The onetime. */
 ONETIME, /** The periodic. */
 PERIODIC, /** The onchange. */
 ONCHANGE
    };
    
    /** The state. */
    private SUBSCRIPTIONSTATE state = SUBSCRIPTIONSTATE.INITIALIZING;
    
    /** The center mode. */
    private Center.RICENTERMODE centerMode;
    
    /** The op spec. */
    private OperationSpecification opSpec;
    
    /** The related publication. */
    private Publication relatedPublication;

    /**
     * Instantiates a new subscription.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public Subscription(C2CMessageSubscription message) {
        this.subscriptionHeaderContents = message;
    }

    /**
     * Replace.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public void replace(C2CMessageSubscription message) {
        setState(SUBSCRIPTIONSTATE.UPDATING);
        synchronized (subscriptionHeaderContents) {
            subscriptionHeaderContents = message;
        }

    }

    /**
     * Cancel.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void cancel() {

        setState(SUBSCRIPTIONSTATE.COMPLETED);

    }

    /**
     * Publication complete.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void publicationComplete() {
        if (getState().equals(SUBSCRIPTIONSTATE.ACTIVE)
                || getState().equals(SUBSCRIPTIONSTATE.UPDATING)) {
            setState(SUBSCRIPTIONSTATE.COMPLETED);
        } else {
            setState(SUBSCRIPTIONSTATE.ERROR);
        }
    }

    /**
     * Publication active.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void publicationActive() {
        if (getState().equals(SUBSCRIPTIONSTATE.INITIALIZING)
                || getState().equals(SUBSCRIPTIONSTATE.UPDATING)) {
            setState(SUBSCRIPTIONSTATE.ACTIVE);
        } else {
            setState(SUBSCRIPTIONSTATE.ERROR);
        }
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public SUBSCRIPTIONSTATE getState() {
        synchronized (this.state){
            return state;
        }
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    protected void setState(SUBSCRIPTIONSTATE state) {
        synchronized (this.state) {
            this.state = state;
        }
    }

    /**
     * Gets the subscription id.
     *
     * @return the subscription id
     */
    public String getSubscriptionID() {
        return subscriptionHeaderContents.getSubscriptionID();
    }

    /**
     * Gets the subscription action.
     *
     * @return the subscription action
     */
    public SUBSCRIPTIONACTION getSubscriptionAction() {
        SUBSCRIPTIONACTION returnAction = SUBSCRIPTIONACTION.RESERVED;

        for (String subscriptionAction : subscriptionHeaderContents.getSubscriptionAction().getSubscriptionActionItem()) {
            if ((subscriptionAction.equals("replaceSubscription"))
                    || subscriptionAction.equals("2")) {
                returnAction = SUBSCRIPTIONACTION.REPLACESUBSCRIPTION;
                break;
            } else if ((subscriptionAction.equals("newSubscription"))
                    || subscriptionAction.equals("1")) {
                returnAction = SUBSCRIPTIONACTION.NEWSUBSCRIPTION;
                break;

            } else if ((subscriptionAction.equals("cancelSubscription"))
                    || subscriptionAction.equals("3")) {
                returnAction = SUBSCRIPTIONACTION.CANCELSUBSCRIPTION;
                break;

            } else if ((subscriptionAction.equals("cancelAllPriorSubscriptions"))
                    || subscriptionAction.equals("4")) {
                returnAction = SUBSCRIPTIONACTION.CANCELLALLPRIORSUBSCRIPTIONS;
                break;

            } else if ((subscriptionAction.equals("reserved"))
                    || subscriptionAction.equals("0")) {
                returnAction = SUBSCRIPTIONACTION.RESERVED;
                break;

            }
        }
        return returnAction;
    }

    /**
     * Gets the subscription action.
     *
     * @param message the message
     * @return the subscription action
     */
    public static SUBSCRIPTIONACTION getSubscriptionAction(C2CMessageSubscription message) {
        SUBSCRIPTIONACTION returnAction = SUBSCRIPTIONACTION.RESERVED;

        for (String subscriptionAction : message.getSubscriptionAction().getSubscriptionActionItem()) {
            if ((subscriptionAction.equals("replaceSubscription"))
                    || subscriptionAction.equals("2")) {
                returnAction = SUBSCRIPTIONACTION.REPLACESUBSCRIPTION;
                break;
            } else if ((subscriptionAction.equals("newSubscription"))
                    || subscriptionAction.equals("1")) {
                returnAction = SUBSCRIPTIONACTION.NEWSUBSCRIPTION;
                break;

            } else if ((subscriptionAction.equals("cancelSubscription"))
                    || subscriptionAction.equals("3")) {
                returnAction = SUBSCRIPTIONACTION.CANCELSUBSCRIPTION;
                break;

            } else if ((subscriptionAction.equals("cancelAllPriorSubscriptions"))
                    || subscriptionAction.equals("4")) {
                returnAction = SUBSCRIPTIONACTION.CANCELLALLPRIORSUBSCRIPTIONS;
                break;

            } else if ((subscriptionAction.equals("reserved"))
                    || subscriptionAction.equals("0")) {
                returnAction = SUBSCRIPTIONACTION.RESERVED;
                break;

            }
        }
        return returnAction;
    }

    /**
     * Gets the subscription frequency.
     *
     * @return the subscription frequency
     */
    public long getSubscriptionFrequency() {
        long frequency = subscriptionHeaderContents.getSubscriptionFrequency();
        return frequency;
    }

    /**
     * Gets the subscription frequency.
     *
     * @param message the message
     * @return the subscription frequency
     */
    public static long getSubscriptionFrequency(C2CMessageSubscription message) {
        long frequency = message.getSubscriptionFrequency();
        return frequency;
    }

    /**
     * Gets the subscription name.
     *
     * @return the subscription name
     */
    public String getSubscriptionName() {
        String name = subscriptionHeaderContents.getSubscriptionName();
        return name;
    }

    /**
     * Gets the subscription name.
     *
     * @param message the message
     * @return the subscription name
     */
    public static String getSubscriptionName(C2CMessageSubscription message) {
        String name = message.getSubscriptionName();
        return name;
    }

    /**
     * Gets the return address.
     *
     * @return the return address
     */
    public String getReturnAddress() {
        String name = subscriptionHeaderContents.getReturnAddress();
        return name;
    }

    /**
     * Gets the return address.
     *
     * @param message the message
     * @return the return address
     */
    public static String getReturnAddress(C2CMessageSubscription message) {
        String name = message.getReturnAddress();
        return name;
    }
    
    
    /**
     * Gets the subscription type.
     *
     * @return the subscription type
     */
    public SUBSCRIPTIONTYPE getSubscriptionType() {
        SUBSCRIPTIONTYPE returnType = SUBSCRIPTIONTYPE.RESERVED;
        String subscriptionType = subscriptionHeaderContents.getSubscriptionType().getSubscriptionTypeItem();
        if ((subscriptionType.equals("reserved"))
                || subscriptionType.equals("0")) {
            returnType = SUBSCRIPTIONTYPE.RESERVED;
        } else if ((subscriptionType.equals("oneTime"))
                || subscriptionType.equals("1")) {
            returnType = SUBSCRIPTIONTYPE.ONETIME;
        } else if ((subscriptionType.equals("periodic"))
                || subscriptionType.equals("2")) {
            returnType = SUBSCRIPTIONTYPE.PERIODIC;
        } else if ((subscriptionType.equals("onChange"))
                || subscriptionType.equals("3")) {
            returnType = SUBSCRIPTIONTYPE.ONCHANGE;
        }
        return returnType;
    }

    /**
     * Gets the subscription type.
     *
     * @param message the message
     * @return the subscription type
     */
    public static SUBSCRIPTIONTYPE getSubscriptionType(C2CMessageSubscription message) {
        SUBSCRIPTIONTYPE returnType = SUBSCRIPTIONTYPE.RESERVED;
        String subscriptionType = message.getSubscriptionType().getSubscriptionTypeItem();
        if ((subscriptionType.equals("reserved"))
                || subscriptionType.equals("0")) {
            returnType = SUBSCRIPTIONTYPE.RESERVED;
        } else if ((subscriptionType.equals("oneTime"))
                || subscriptionType.equals("1")) {
            returnType = SUBSCRIPTIONTYPE.ONETIME;
        } else if ((subscriptionType.equals("periodic"))
                || subscriptionType.equals("2")) {
            returnType = SUBSCRIPTIONTYPE.PERIODIC;
        } else if ((subscriptionType.equals("onChange"))
                || subscriptionType.equals("3")) {
            returnType = SUBSCRIPTIONTYPE.ONCHANGE;
        }
        return returnType;
    }

    /**
     * Gets the subscription start time.
     *
     * @return the subscription start time
     */
    public Date getSubscriptionStartTime() {
        if (subscriptionHeaderContents.getSubscriptionTimeFrame() != null) {
            XMLGregorianCalendar startTime = subscriptionHeaderContents.getSubscriptionTimeFrame().getStart();
            return startTime.toGregorianCalendar().getTime();
        } else {
            return new Date();
        }
    }

    /**
     * Gets the subscription start time.
     *
     * @param message the message
     * @return the subscription start time
     */
    public static Date getSubscriptionStartTime(C2CMessageSubscription message) {
        if (message.getSubscriptionTimeFrame() != null) {
            XMLGregorianCalendar startTime = message.getSubscriptionTimeFrame().getStart();
            return startTime.toGregorianCalendar().getTime();
        } else {
            return new Date();
        }
    }

    /**
     * Gets the subscription end time.
     *
     * @return the subscription end time
     */
    public Date getSubscriptionEndTime() {
        try{
            XMLGregorianCalendar endTime = subscriptionHeaderContents.getSubscriptionTimeFrame().getEnd();
            return endTime.toGregorianCalendar().getTime();
        } catch (Exception ex){
          ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the subscription end time.
     *
     * @param message the message
     * @return the subscription end time
     */
    public static Date getSubscriptionEndTime(C2CMessageSubscription message) {
        try{
        XMLGregorianCalendar endTime = message.getSubscriptionTimeFrame().getEnd();
        return endTime.toGregorianCalendar().getTime();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Produce c2c message subscription xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] produceC2cMessageSubscriptionXML(C2CMessageSubscription message) throws Exception {
        byte[] subscription = null;

        try {
            JAXBContext jc = JAXBContext.newInstance(C2CMessageSubscription.class.getPackage().getName());
            Marshaller mm = jc.createMarshaller();
            mm.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mm.marshal(message, buffer);
            subscription = buffer.toByteArray();
            buffer.close();

        } catch (IOException ioex) {
            ioex.printStackTrace();
            throw new Exception("Subscription:produceC2CMessageSubscriptionXML Error: ", ioex);


        } catch (JAXBException jaxbex) {
            jaxbex.printStackTrace();
            throw new Exception("Subscription:produceC2CMessageSubscriptionXML Error: ", jaxbex);

        }
        return subscription;

    }
}
