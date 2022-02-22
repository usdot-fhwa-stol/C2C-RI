/**
 * 
 */
package org.fhwa.c2cri.testmodel;

import java.io.Serializable;

/**
 * This class stores and maintains the TestMode parameters.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestMode implements Serializable{
	
	/** flag indicating whether the RI will perform tests acting as an external center. */
	protected boolean externalCenterOperation;
	
	/** flag indicating whether the RI will perform tests acting as an owner center. */
	protected boolean ownerCenterOperation;

        /**
         * Constructor for the TestMode Class.
         */
        public TestMode(){
            this(true, false);
        }

        /**
         * Constructor for the TestMode Class.
         *
         * @param externalCenterMode the external center mode
         * @param ownerCenterMode the owner center mode
         */
        public TestMode(boolean externalCenterMode, boolean ownerCenterMode){
            this.externalCenterOperation = externalCenterMode;
            this.ownerCenterOperation = ownerCenterMode;
        }
	
	/**
	 * Checks if is external center operation.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the externalCenterOperation
	 */
	public boolean isExternalCenterOperation() {
		return externalCenterOperation;
	}
	
	/**
	 * Sets the external center operation.
	 *
	 * @param externalCenterOperation the externalCenterOperation to set
	 */
	public void setExternalCenterOperation(boolean externalCenterOperation) {
		this.externalCenterOperation = externalCenterOperation;
	}
	
	/**
	 * Checks if is owner center operation.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the ownerCenterOperation
	 */
	public boolean isOwnerCenterOperation() {
		return ownerCenterOperation;
	}
	
	/**
	 * Sets the owner center operation.
	 *
	 * @param ownerCenterOperation the ownerCenterOperation to set
	 */
	public void setOwnerCenterOperation(boolean ownerCenterOperation) {
		this.ownerCenterOperation = ownerCenterOperation;
	}

}
