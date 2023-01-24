/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * The Class ServiceSpecCollection contains a collection of related services.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ServiceSpecCollection {

    /** The service collection. */
    private ArrayList<ServiceSpecification> serviceCollection = new ArrayList<ServiceSpecification>();

    /**
     * Adds the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param ss the ss
     * @return true, if successful
     */
    public boolean add(ServiceSpecification ss) {
        return serviceCollection.add(ss);
    }

    /**
     * Contains.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceName the service name
     * @param serviceLocation the service location
     * @param serviceType the service type
     * @return true, if successful
     */
    public boolean contains(String serviceName, String serviceLocation, ServiceSpecification.SERVICETYPE serviceType) {
        boolean result = false;
        for (ServiceSpecification thisSpec : serviceCollection) {
            if (thisSpec.getServiceType().equals(serviceType)
                    && thisSpec.getLocation().equals(serviceLocation)
                    && thisSpec.getName().equals(serviceName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceName the service name
     * @param serviceLocation the service location
     * @param serviceType the service type
     * @return the service specification
     */
    public ServiceSpecification get(String serviceName, String serviceLocation, ServiceSpecification.SERVICETYPE serviceType) {
        ServiceSpecification result = null;
        for (ServiceSpecification thisSpec : serviceCollection) {
            if (thisSpec.getServiceType().equals(serviceType)
                    && thisSpec.getLocation().equals(serviceLocation)
                    && thisSpec.getName().equals(serviceName)) {
                result = thisSpec;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param index the index
     * @return the service specification
     */
    public ServiceSpecification get(int index) {
        ServiceSpecification result = null;
        try {
            result = serviceCollection.get(index);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Gets the client specs by location.
     *
     * @return the client specs by location
     */
    public HashMap<String, OperationSpecCollection> getClientSpecsByLocation() {
        HashMap<String, OperationSpecCollection> returnMap = new HashMap<String, OperationSpecCollection>();
        for (ServiceSpecification thisSpec : serviceCollection) {
            if (thisSpec.getServiceType().equals(ServiceSpecification.SERVICETYPE.CLIENT)) {
                if (returnMap.containsKey(thisSpec.getLocation())) {
                    for (OperationSpecification thisOperation : thisSpec.getOperations().getCopyAsList()) {
                        if (!returnMap.get(thisSpec.getLocation()).contains(thisOperation.getRelatedToService(), thisOperation.getRelatedToPort(), thisOperation.getOperationName())) {
                            returnMap.get(thisSpec.getLocation()).add(thisOperation);
                        }
                    }
                } else {
                    OperationSpecCollection thisCollection = new OperationSpecCollection();
                    for (OperationSpecification thisOperation : thisSpec.getOperations().getCopyAsList()) {
                        thisCollection.add(thisOperation);
                    }
                    returnMap.put(thisSpec.getLocation(), thisCollection);
                }
            }
        }
        return returnMap;
    }

    /**
     * Gets the server specs by location.
     *
     * @return the server specs by location
     */
    public HashMap<String, OperationSpecCollection> getServerSpecsByLocation() {
        HashMap<String, OperationSpecCollection> returnMap = new HashMap<String, OperationSpecCollection>();
        for (ServiceSpecification thisSpec : serviceCollection) {
            if (thisSpec.getServiceType().equals(ServiceSpecification.SERVICETYPE.LISTENER)
                    || thisSpec.getServiceType().equals(ServiceSpecification.SERVICETYPE.SERVER)) {
                if (returnMap.containsKey(thisSpec.getLocation())) {
                    System.out.println("ServiceSpecCollection::getServerSpecsByLocation  Return Map Already contains "+thisSpec.getLocation());
                    for (OperationSpecification thisOperation : thisSpec.getOperations().getCopyAsList()) {
                        if (!returnMap.get(thisSpec.getLocation()).contains(thisOperation.getRelatedToService(), thisOperation.getRelatedToPort(), thisOperation.getOperationName())) {
                            System.out.println("ServiceSpecCollection::getServerSpecsByLocation  Before add size = "+returnMap.get(thisSpec.getLocation()).size());
                            returnMap.get(thisSpec.getLocation()).add(thisOperation);
                            System.out.println("ServiceSpecCollection:getServerSpecsByLocation  Added operation "+thisOperation.getOperationName()+ " new size = "+ +returnMap.get(thisSpec.getLocation()).size());
                        }
                    }
                } else {
                    System.out.println("ServiceSpecCollection::getServerSpecsByLocation  Location "+thisSpec.getLocation() + " added to the map.");
                    OperationSpecCollection thisCollection = new OperationSpecCollection();
                    for (OperationSpecification thisOperation : thisSpec.getOperations().getCopyAsList()) {
                        thisCollection.add(thisOperation);
                            System.out.println("ServiceSpecCollection:getServerSpecsByLocation  Added operation "+thisOperation.getOperationName());
                    }
                    returnMap.put(thisSpec.getLocation(), thisCollection);
                }
            }
        }
        return returnMap;

    }

    /**
     * Gets the copy as list.
     *
     * @return the copy as list
     */
    public ArrayList<ServiceSpecification> getCopyAsList() {
        return (ArrayList) serviceCollection.clone();
    }

    /**
     * Checks if is empty.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return serviceCollection.isEmpty();
    }

    /**
     * Size.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the int
     */
    public int size() {
        return serviceCollection.size();
    }
}
