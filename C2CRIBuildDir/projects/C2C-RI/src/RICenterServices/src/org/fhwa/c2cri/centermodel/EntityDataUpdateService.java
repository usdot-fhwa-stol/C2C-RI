package org.fhwa.c2cri.centermodel;


/**
 * This class provides the capability to notify subscribers about updates to
 * entity data that would relate to OnChange publication updates.  Notifications
 * will be sent to subscribers that are currently registered for updates for each
 * dialog.
 * @author TransCore ITS,LLC
 * @version 1.0
 * @created 26-Jan-2016 1:30:09 PM
 */
public class EntityDataUpdateService {

	public EntityDataUpdateService(){

	}

	/**
	 * 
	 * @param dialog
	 */
	protected void notifySubscribers(String dialog){

	}

	/**
	 * This method provides a way for objects to subscribe to notifications of updates
	 * to entity data related to the publication update dialog provided.
	 * 
	 * @param dialog
	 * @param subscriber
	 */
	protected void registerForUpdates(String dialog, EntityDataListener subscriber){

	}

	/**
	 * This method provides a way for objects to unsubscribe for notifications of
	 * updates to entity data related to the publication update dialog provided.
	 * 
	 * @param entityDataListener
	 */
	protected void unregisterForUpdates(EntityDataListener entityDataListener){

	}

}