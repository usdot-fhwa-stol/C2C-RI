/*
 * Created on Nov 17, 2004
 *
 */
package org.fhwa.c2cri.gui.propertyeditor;

import java.sql.Driver;

import org.fhwa.c2cri.gui.propertyeditor.utils.ClassUtils;


 


/**
 * The Class ParameterJDBCDriver.
 *
 * @author swaminathan_n01
 */
public class ParameterJDBCDriver extends ParameterSymbol {

	/** The str base class name. */
	String strBaseClassName ;
	
	/**
	 * Instantiates a new parameter jdbc driver.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param name the name
	 * @param value the value
	 * @param baseClassName the base class name
	 * @param editable the editable
	 * @param doc the doc
	 */
	public ParameterJDBCDriver(String name, String value, String baseClassName,  boolean editable, String doc) {
		
		super(name, value, null, editable, doc);
		Driver[] driversInClasspath = ClassUtils.findAllDriversInClasspath(baseClassName);
		int len = driversInClasspath.length;
		String[] driverNames = new String[len];
		
		for (int idx = 0; idx <len ; idx++)
		{
			Driver drv = driversInClasspath[idx];
			driverNames[idx] = drv.getClass().getName();
			System.out.println(drv.getClass().getName());
		}
		setAllowedValues(driverNames);
		
		type = "JDBCDRIVER";
		
	}

	 /**
 	 * To text.
 	 * 
 	 * Pre-Conditions: N/A
 	 * Post-Conditions: N/A
 	 *
 	 * @return the string
 	 */
 	public String toText(){
        //System.out.println("\t\tadding parameter " + name);
		String newLine = System.getProperty("line.separator");
        return toTextType() + newLine + toTextValue();
    }
	
}
