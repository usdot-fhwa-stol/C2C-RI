/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.wsdl;


/**
 * The Class ServiceSpecification provides the details of all bindings, operations and messages related to a service.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ServiceSpecification {
    
    /**
     * The Enum SERVICETYPE.
     */
    public static enum SERVICETYPE {
/** The server. */
SERVER, 
 /** The client. */
 CLIENT, 
 /** The listener. */
 LISTENER, 
 /** The unknown. */
 UNKNOWN};

    /** The name. */
    private String name="";
    
    /** The location. */
    private String location="";
    
    /** The operations. */
    private OperationSpecCollection operations = new OperationSpecCollection();
    
    /** The service type. */
    private SERVICETYPE serviceType = SERVICETYPE.UNKNOWN;

    /**
     * Instantiates a new service specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param location the location
     */
    public ServiceSpecification(String name, String location){
        this.location = location;
        this.name = name;
    }

    /**
     * Instantiates a new service specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param location the location
     * @param serviceType the service type
     */
    public ServiceSpecification(String name, String location, SERVICETYPE serviceType){
        this.name = name;
        this.location = location;
        this.serviceType = serviceType;
    }

    /**
     * Instantiates a new service specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param location the location
     * @param serviceType the service type
     * @param osList the os list
     */
    public ServiceSpecification(String name, String location, SERVICETYPE serviceType, OperationSpecCollection osList){
        this.name = name;
        this.location = location;
        this.serviceType = serviceType;
        this.operations = osList;
    }


    /**
     * Gets the location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param location the new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Adds the operation specification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param theOperationSpec the the operation spec
     */
    public void addOperationSpecification(OperationSpecification theOperationSpec){
        this.operations.add(theOperationSpec);
    }


    /**
     * Gets the operations.
     *
     * @return the operations
     */
    public OperationSpecCollection getOperations() {
        return operations;
    }


    /**
     * Gets the service type.
     *
     * @return the service type
     */
    public SERVICETYPE getServiceType() {
        return serviceType;
    }

    /**
     * Sets the service type.
     *
     * @param serviceType the new service type
     */
    public void setServiceType(SERVICETYPE serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }



}
