/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.operations;


/**
 * The Class OperationIdentifier identifies an operation by service, port, operation and the type of handler that made the request for the operation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OperationIdentifier {

    /**
     * The Enum SOURCETYPE.
     */
    public enum SOURCETYPE {

        /** The handler. */
        HANDLER, /** The listener. */
 LISTENER
    };
    
    /** The operation name. */
    private String operationName;
    
    /** The service name. */
    private String serviceName;
    
    /** The port name. */
    private String portName;
    
    /** The source. */
    private SOURCETYPE source;
    
    /** The hash code. */
    private int fHashCode;

    /**
     * Instantiates a new operation identifier.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceName the service name
     * @param portName the port name
     * @param operationName the operation name
     * @param source the source
     */
    public OperationIdentifier(String serviceName, 
            String portName, String operationName, SOURCETYPE source) {
        this.serviceName = serviceName;
        this.portName = portName;
        this.operationName = operationName;
        this.source = source;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {

        if (this == that) {
            return true;
        }
        if (!(that instanceof OperationIdentifier)) {
            return false;
        }
        OperationIdentifier tmp = (OperationIdentifier) that;
        return (serviceName.equals(tmp.serviceName)
                && portName.equals(tmp.portName)
                && operationName.equals(tmp.operationName)
                && source.equals(tmp.source));
    }

    /**
     * Gets the operation name.
     *
     * @return the operation name
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Gets the port name.
     *
     * @return the port name
     */
    public String getPortName() {
        return portName;
    }

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public SOURCETYPE getSource() {
        return source;
    }




    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
//        int hash = 7;
//        hash = 31 * hash + fHashCode;
//        hash = 31 * hash + (null == data ? 0 : data.hashCode());
//        return hash;

        //this style of lazy initialization is
        //suitable only if the object is immutable
        if (fHashCode == 0) {
            int result = HashCodeUtil.SEED;
            result = HashCodeUtil.hash(result, serviceName);
            result = HashCodeUtil.hash(result, portName);
            result = HashCodeUtil.hash(result, operationName);
            result = HashCodeUtil.hash(result, source);
            fHashCode = result;
        }
        return fHashCode;

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return("Service: "+serviceName+"  PortName: "+portName+"  OperationName: "+operationName+"  Source: "+source);
    }
}
