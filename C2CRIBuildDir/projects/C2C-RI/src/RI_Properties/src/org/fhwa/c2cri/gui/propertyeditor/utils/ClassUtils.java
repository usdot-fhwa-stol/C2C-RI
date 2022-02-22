package org.fhwa.c2cri.gui.propertyeditor.utils;

import java.sql.Driver;

  /**
   * The Class ClassUtils.
   *
   * @author Unknown
   * Last Updated:  1/8/2014
   */
  public class ClassUtils
  {

  	
  	 /**
 	   * Returns an array of all JDBC drivers in the classpath.
 	   *
 	   * @param baseClassName the base class name
 	   * @return the driver[]
 	   */
  	 
  	  public static Driver[] findAllDriversInClasspath(String baseClassName)
      {

  	  	
  	  	// Following is a sample implementation for the returning the list of 
  	  	// JDBC drivers in the classpath.
  	  	// Refer http://glaforge.free.fr/weblog/index.php?itemid=28 for a more complete example
  	  	// of fetching the list of all JDBC Drivers in the classpath.
  	  	
  	  	Driver[] listOfDrivers = new Driver[1];
      	 try
		 {
			Class sunJdbcOdbcDriverClass = Class.forName(baseClassName);
		    Driver sunJdbcOdbcDriver = (Driver) sunJdbcOdbcDriverClass.newInstance();
			listOfDrivers[0] = sunJdbcOdbcDriver;
		 }
		 catch (Exception e)
	     {

	 	 }
		
		 return listOfDrivers;

      }

  }
