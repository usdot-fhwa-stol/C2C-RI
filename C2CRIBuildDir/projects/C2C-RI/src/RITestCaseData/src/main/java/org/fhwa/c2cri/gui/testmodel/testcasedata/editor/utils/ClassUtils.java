package org.fhwa.c2cri.gui.testmodel.testcasedata.editor.utils;

import java.sql.Driver;

  public class ClassUtils
  {

  	
  	 /**
     * Returns an array of all JDBC drivers in the classpath
     *
     * 
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
			Class sunJdbcOdbcDriverClass = Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		    Driver sunJdbcOdbcDriver = (Driver) sunJdbcOdbcDriverClass.newInstance();
			listOfDrivers[0] = sunJdbcOdbcDriver;
		 }
		 catch (Exception e)
	     {

	 	 }
		
		 return listOfDrivers;

      }

  }
