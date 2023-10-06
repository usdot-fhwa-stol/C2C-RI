package org.fhwa.c2cri.centermodel;

import java.util.ArrayList;
import org.fhwa.c2cri.applayer.MessageContentGenerator;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.messagemanager.Message;

/**
 * When the RI is running a Test, and Owner Center is the Center Mode specified
 * in the Test Configuration, this class will oversee the performance and setup
 * of RI Entity Emulation actions specified in the test scripts for the entities
 * specified in the currently adopted version of the TMDD.
 *
 * This class acts as the main interface for a service which provides updated
 * (and sometimes simulated) entity information.
 *
 * This class provides methods which allow for external actions on entity
 * information. All entities support all of the actions.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 * @version 1.0
 * @created 26-Jan-2016 1:28:34 PM
 */
public class RIEmulation implements RINRTMSelectionProvider {

    /**
     * The entity data update service associated with Entity Emulation.
     */
    private EntityDataUpdateService updateService;

    /* 
        * Specifies whether the emulation parameters will be re-initialized before each test case. 
     */
    private Boolean reInitBeforeTestCase;

    private boolean emulationEnabled;
    
    private RIEmulator emulatorImplementation;

    private static RIEmulation riEmulationInstance;
    
    private RIEmulationParameters emulationParameters;
    
    private ArrayList<RINRTMSelection> nrtmSelections = new ArrayList();

    private MessageContentGenerator emulationContentGenerator;
    
    private RIEmulation() {

    }

    public static RIEmulation getInstance() {
        if (riEmulationInstance == null) {
            riEmulationInstance = new RIEmulation();
        }
        return riEmulationInstance;
    }

        /**
     * Method used to trigger the initialization of RI Emulation using the
     * emulation data provided.
     *
     * @param emulationParameters
     */
    public void initialize(String standardName, RIEmulationParameters emulationParameters) throws EntityEmulationException {
            if (emulatorImplementation != null)
                emulatorImplementation= null;
        
            RIEmulatorService theService = RIEmulatorService.getInstance();
            emulatorImplementation = theService.getEmulator(standardName);            
            
            if (emulatorImplementation != null) {
                emulatorImplementation.initialize(emulationParameters);
            }
            this.emulationParameters = emulationParameters;

    }

        /**
     * Method used to trigger the initialization of RI Emulation using the
     * emulation data provided.
     *
     * @param emulationParameters
     */
    public void initialize() throws EntityEmulationException {            
            if (emulatorImplementation != null && emulationParameters != null && isEmulationEnabled()){
                emulatorImplementation.initialize(emulationParameters);
            } else if (!isEmulationEnabled()){
                    throw new EntityEmulationException("RI Emulation is not enabled for this Test.");                
            } else if (emulationParameters == null){
                    throw new EntityEmulationException("No initial RI Parameters have been defined for this Test.");                                
            } else if (emulatorImplementation == null){
                    throw new EntityEmulationException("No RIEmulator has been selected for this Test.");                                                
            }
    }
    
    
    /**
     * Method provides a means for external addition of entities.
     *
     * @param entityDataType
     * @param entityId
     */
    public void addEntity(String entityDataType, String entityId) throws EntityEmulationException {
           if (isEmulationEnabled() && emulatorImplementation != null) {
               emulatorImplementation.addEntity(entityDataType, entityId);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");

    }

    /**
     * Method provides a means for external addition of elements of an entity.
     *
     * @param entityDataType
     * @param entityId
     * @param entityName
     * @param entityValue
     */
    public void addEntityElement(String entityDataType, String entityId, String entityName, String entityValue) throws EntityEmulationException{
           if (isEmulationEnabled() && emulatorImplementation != null) {
               emulatorImplementation.addEntityElement(entityDataType, entityId, entityName, entityValue);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");
    }

    /**
     * Method provides a means for external deletion of an entity.
     *
     * @param entityDataType
     * @param entityId
     * @param entityName
     */
    public void deleteEntity(String entityDataType, String entityId) throws EntityEmulationException{
           if (isEmulationEnabled() && emulatorImplementation != null) {
               emulatorImplementation.deleteEntity(entityDataType, entityId);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");

    }
    /**
     * Method provides a means for external deletion of an entity element.
     *
     * @param entityDataType
     * @param entityId
     * @param entityName
     * @param entityValue
     */
    public void deleteEntityElement(String entityDataType, String entityId, String entityElementName) throws EntityEmulationException{
           if (isEmulationEnabled() && emulatorImplementation != null) {
               emulatorImplementation.deleteEntityElement(entityDataType, entityId, entityElementName);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");

    }

    /**
     * Method provides a means for external view of an entity element.
     *
     * @param entityDataType
     * @param entityId
     * @param entityName
     */
    public String getEntityElementValue(String entityDataType, String entityId, String entityName) throws EntityEmulationException{
           if (isEmulationEnabled() && emulatorImplementation != null) {
               return emulatorImplementation.getEntityElementValue(entityDataType, entityId, entityName);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");
        
    }

    /**
     * This method returns the publication message associated with the
     * subscription message that was received and the dialog name provided. If
     * the request could not be properly satisfied by the entity emulation, an
     * exception is thrown.
     *
     * @param dialog The name of the publication dialog that is associated with
     * the message provided.
     * @param subscriptionMessage The subscription message that was sent for
     * this publication.
     */
    public Message getPubMessage(String dialog, Message subscriptionMessage) throws EntityEmulationException{
           if (isEmulationEnabled() && emulatorImplementation != null) {
               return emulatorImplementation.getPubResponse(dialog, subscriptionMessage);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");

    }

    /**
     * This method returns the response message associated with the request
     * message that was received. If the request could not be properly satisfied
     * by the entity emulation, an error response message is provided.
     *
     * @param dialog The name of the RequestResponse dialog that is associated
     * with the message provided.
     * @param requestMessage The request message that was received from the EC.
     */
    public Message getRRResponse(String dialog, Message requestMessage) throws EntityEmulationException {
           if (isEmulationEnabled() && emulatorImplementation != null) {
               return emulatorImplementation.getRRResponse(dialog, requestMessage);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");
    }

    /**
     * This method returns the response message associated with the subscription
     * message that was received. If the request could not be properly satisfied
     * by the entity emulation, an error response message is provided.
     *
     * @param dialog The name of the subscription dialog that is associated with
     * the message provided.
     * @param requestMessage The subscription message that was received from the
     * EC.
     */
    public Message getSubResponse(String dialog, Message requestMessage) throws EntityEmulationException {
           if (isEmulationEnabled() && emulatorImplementation != null) {
               return emulatorImplementation.getSubResponse(dialog, requestMessage);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");
    }


    /**
     * Registers the provided entity listener for any updates to data related to
     * the given dialog.
     *
     * @param dialog
     * @param entityListener
     */
    public EntityDataUpdateService registerForUpdates(String dialog, EntityDataListener entityListener) {
        return null;
    }

    /**
     * Untegisters the provided entity listener for updates to data related to
     * the given dialog.
     *
     * @param dialog
     * @param entityListener
     */
    public void unRegisterForUpdates(String dialog, EntityDataListener entityListener) {
		// original implementation was empty
    }

    /**
     * Method provides a means for external change of an entity element value.
     *
     * @param entityDataType
     * @param entityId
     * @param entityName
     * @param entityValue
     */
    public void updateEntityElement(String entityDataType, String entityId, String entityName, String entityValue) throws EntityEmulationException {
           if (isEmulationEnabled() && emulatorImplementation != null) {
               emulatorImplementation.updateEntityElement(entityDataType, entityId, entityName, entityValue);
           } else throw new EntityEmulationException("No valid Emulator has been initialized.");

    }

    public boolean isEmulationEnabled() {
        return emulationEnabled;
    }

    public void setEmulationEnabled(boolean emulationEnabled) {
        this.emulationEnabled = emulationEnabled;
    }

    public Boolean getReInitBeforeTestCase() {
        return reInitBeforeTestCase;
    }

    public void setReInitBeforeTestCase(Boolean reInitBeforeTestCase) {
        this.reInitBeforeTestCase = reInitBeforeTestCase;
    }

    public void setNrtmSelections(ArrayList nrtmSelections) {
        this.nrtmSelections = nrtmSelections;
        System.out.println("RIEmulation::setNRTMSelections NRTM Selections Map Size = "+this.nrtmSelections.size());
        
    }

    @Override
    public ArrayList<RINRTMSelection> getNRTMSelections() {
        System.out.println("RIEmulation::getNRTMSelections NRTM Selections Map Size = "+this.nrtmSelections.size());
        return nrtmSelections;
    }

    public void operationRelatedDataUpdate(String operationName) {
        if (emulationContentGenerator!= null) emulationContentGenerator.operationRelatedDataUpdate(operationName);
    }

    public void setEmulationContentGenerator(MessageContentGenerator emulationContentGenerator) {
        this.emulationContentGenerator = emulationContentGenerator;
    }

    
}
