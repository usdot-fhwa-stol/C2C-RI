/**
 * 
 */
package org.fhwa.c2cri.testmodel;

import java.io.Serializable;

/**
 * This class maintains the various options available for a test.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestOptions implements Serializable{

	/** flag indicating whether range testing tests will be performed *. */
	private boolean rangeTesting;
	
	/** flag indicating whether negative testing tests will be performed *. */
	private boolean negativeTesting;
	
	/** flag indicating whether other optional tests will be performed *. */
	private boolean otherTestingOptions;

        /**
         * Instantiates a new test options.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        public TestOptions(){
            this(true, true, false);
        }

        /**
         * Instantiates a new test options.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param rangeTesting the range testing
         * @param negativeTesting the negative testing
         * @param otherTesting the other testing
         */
        public TestOptions(boolean rangeTesting, boolean negativeTesting, boolean otherTesting){
            this.rangeTesting = rangeTesting;
            this.negativeTesting = negativeTesting;
            this.otherTestingOptions = otherTesting;
        }

        /**
         * Checks if is negative testing.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return the negativeTesting
         */
	public boolean isNegativeTesting() {
		return negativeTesting;
	}
	
	/**
	 * Sets the negative testing.
	 *
	 * @param negativeTesting the negativeTesting to set
	 */
	public void setNegativeTesting(boolean negativeTesting) {
		this.negativeTesting = negativeTesting;
	}
	
	/**
	 * Checks if is other testing options.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the otherTestingOptions
	 */
	public boolean isOtherTestingOptions() {
		return otherTestingOptions;
	}
	
	/**
	 * Sets the other testing options.
	 *
	 * @param otherTestingOptions the otherTestingOptions to set
	 */
	public void setOtherTestingOptions(boolean otherTestingOptions) {
		this.otherTestingOptions = otherTestingOptions;
	}
	
	/**
	 * Checks if is range testing.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return the rangeTesting
	 */
	public boolean isRangeTesting() {
		return rangeTesting;
	}
	
	/**
	 * Sets the range testing.
	 *
	 * @param rangeTesting the rangeTesting to set
	 */
	public void setRangeTesting(boolean rangeTesting) {
		this.rangeTesting = rangeTesting;
	}
	
	
}
