/*
 * Created on Nov 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import java.sql.Driver;

import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.utils.ClassUtils;


 


/**
 * @author swaminathan_n01
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ParameterJDBCDriver extends ParameterSymbol {

	String strBaseClassName ;
	
	/**
	 * @param name
	 * @param value
	 * @param editable
	 * @param doc
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

	 public String toText(){
        //System.out.println("\t\tadding parameter " + name);
		String newLine = System.getProperty("line.separator");
        return toTextType() + newLine + toTextValue();
    }
	
}
