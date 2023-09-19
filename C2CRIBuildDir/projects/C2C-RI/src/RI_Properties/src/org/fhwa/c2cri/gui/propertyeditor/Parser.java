package org.fhwa.c2cri.gui.propertyeditor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
 * The Class Parser.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class Parser {
    
    /** The Group reg exp. */
    private static Pattern GroupRegExp;
    
    /** The P type reg exp. */
    private static Pattern PTypeRegExp;
    
    /** The PA values reg exp. */
    private static Pattern PAValuesRegExp; //Pattern for parameter allowed values
    
    /** The P edit reg exp. */
    private static Pattern PEditRegExp; //Pattern to know whether the P. is editable or not
    
    /** The P def reg exp. */
    private static Pattern PDefRegExp; //Parameter name and value
    
    /** The P doc reg exp. */
    private static Pattern PDocRegExp; //Pattern to get the documentation for a parameter
    
    /** The P base class reg exp. */
    private static Pattern PBaseClassRegExp;
    
    /** The Empty line. */
    private static Pattern EmptyLine;
    
    /** The g. */
    private static Group g; //current group when parsing property file
    
    /** The errors. */
    private static String errors = "";
    
    /** The line number. */
    private static int lineNumber = 0;
    
    /** The driver names. */
    private static String[] driverNames;
    
    /**
     * Inits the values.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void initValues() {
        
        GroupRegExp = Pattern.compile("(?i)^# *group *name *= *(.*\\S)");
        PTypeRegExp = Pattern.compile("(?i)^# *parameter *type *= *(.*\\S)", Pattern.MULTILINE);
        PAValuesRegExp = Pattern.compile("(?i)^# *allowed *values *= *(.*\\S)", Pattern.MULTILINE);
        PEditRegExp = Pattern.compile("(?i)^# *editable *= *(.*\\S)", Pattern.MULTILINE);
		PDocRegExp = Pattern.compile("(?i)^# *documentation *= *(.*\\S)", Pattern.MULTILINE);
        PDefRegExp = Pattern.compile("^ *([^#\n\r]*\\S) *= *(.*)", Pattern.MULTILINE);
        EmptyLine = Pattern.compile("^\\s*\n$", Pattern.MULTILINE);
        PBaseClassRegExp = Pattern.compile("(?i)^# *BASECLASS *= *(.*\\S)", Pattern.MULTILINE);
        
		
        errors = "";
        lineNumber = 0;
        
    }
    
    /**
     * Group name.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return the string
     */
    public static String groupName(String line) {
        
        Matcher matcher = GroupRegExp.matcher(line);
        
        if (matcher.find()) return matcher.group(1);
        else return "";
    }
    
    /**
     * Gets the comments.
     *
     * @param data the data
     * @return the comments
     */
    public static String getComments(String data) {
		Matcher matcher = PTypeRegExp.matcher(data);
		data = matcher.replaceAll("");
		 
		matcher = PAValuesRegExp.matcher(data);
		data = matcher.replaceAll("");
		matcher = PEditRegExp.matcher(data);
		data = matcher.replaceAll("");
		matcher = PDocRegExp.matcher(data);
		data = matcher.replaceAll("");		
		matcher = PDefRegExp.matcher(data);
		data = matcher.replaceAll("");
		
		matcher = EmptyLine.matcher(data);
		data = matcher.replaceAll("");
		
		return data;

    }
    
    /**
     * P type.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return the string
     */
    public static String pType(String line) {
        
        Matcher matcher = PTypeRegExp.matcher(line);
        
        if (matcher.find()) return matcher.group(1); 
        else return "";
    }
    
    /**
     * P a values.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return the string[]
     */
    public static String[] pAValues(String line) {
        
        Matcher matcher = PAValuesRegExp.matcher(line);
        
        if (matcher.find()) return matcher.group(1).split(", *");
        else return null;
    }
    
    /**
     * P a base class.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return the string
     */
    public static String pABaseClass(String line) {
        
        Matcher matcher = PBaseClassRegExp.matcher(line);
        
        if (matcher.find()) return matcher.group(1);
        else return null;
    }
    
    /**
     * P edit.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return true, if successful
     */
    public static boolean pEdit(String line) {
        
        Matcher matcher = PEditRegExp.matcher(line);
        
        if (matcher.find() && matcher.group(1).compareToIgnoreCase("false") == 0) return false;
        
        else return true;
    }

	/**
	 * P doc.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param line the line
	 * @return the string
	 */
	public static String pDoc(String line) {
        
		Matcher matcher = PDocRegExp.matcher(line);
        
		if (matcher.find()) return matcher.group(1); 
		else return "";
	}
    
    /**
     * P name.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return the string
     */
    public static String pName(String line) {
        
        Matcher matcher = PDefRegExp.matcher(line);
        
        if (matcher.find()) return matcher.group(1);
        else return "";
    }
    
    /**
     * P value.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param line the line
     * @return the string
     */
    public static String pValue(String line) {
        
        Matcher matcher = PDefRegExp.matcher(line);
        
        if (matcher.find()) return matcher.group(2);
        else return "";
    }
    
    /**
     * Gets the groups.
     *
     * @param eFile the e file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void getGroups(EditorFile eFile) throws IOException {
        
        try (BufferedReader br = new BufferedReader(new FileReader(eFile)))
		{
        
			String groupData = ""; //current data for current group

			while(br.ready()) {

				String line = br.readLine();
				//lineNumber++;

				if(!groupName(line).equals("")) {

					if(!groupData.equals("")) {
						getParameters(groupData);
					}

					g = new Group(groupName(line));
					eFile.addGroup(g);
					groupData = "";
				}
				else {
					groupData += line + "\n";
				}
			}

			if(!groupData.equals("")) {
				getParameters(groupData);
			}

		}
    }
    
    /**
     * Gets the parameters.
     *
     * @param data the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void getParameters(String data) throws IOException{
        
        //System.out.println("data for group " + g.name + ":\n" + data);
        
        BufferedReader br = new BufferedReader(new StringReader(data));
        
        String cpType = ""; //type of current parameter
        
        String pData = ""; //current data for current parameter
        
        String pDoc = "";
        
        String line = "";
        
//        Driver[] driversInClasspath = ClassUtils.findAllDriversInClasspath();
//        int len = driversInClasspath.length;
//		driverNames = new String[len];
//		
//		for (int idx = 0; idx <len ; idx++)
//		{
//			Driver drv = driversInClasspath[idx];
//			driverNames[idx] = drv.getClass().getName();
//			System.out.println(drv.getClass().getName());
//		}
        
        
        while(br.ready() && line != null) {
            if(!pName(line).equals("")) {
                createParameter(pData + line + "\n");
                pData = "";
            }
            else pData += line + "\n";
            line = br.readLine();
            lineNumber++;
        }
        
        //createParameter(pData);
        
        br.close();
    }
    
    /**
     * Creates the parameter.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param data the data
     */
    public static void createParameter(String data){
        
        
        String pType      = pType(data);
        String pName      = pName(data);
        String pValue     = pValue(data);
        String[] pAValues = pAValues(data);
        boolean pEdit     = pEdit(data);
        String pDoc       = pDoc(data);
        String pBaseClass = pABaseClass(data);
        
        
        Parameter p = null; //current Parameter
        
        
        
        if(pName.equals(""))
            errors += "Error:No name for parameter of group '"
            + g.getName() + "' around line " + lineNumber + '\n'; //error
        
        else {
            if(pType.equals("")) pType = "string";
            if(pType.compareToIgnoreCase("symbol") == 0) {
                
                if(pAValues == null) {
                    errors += "Error:No allowed values for Symbol parameter '"
                    + pName + "' of group '" + g.getName() + "' around line "
                    + lineNumber + '\n';//error
                    
                    pEdit = false;
                }
                
                p = new ParameterSymbol(pName, pValue, pAValues, pEdit, pDoc);
            }
            
            else if(pType.compareToIgnoreCase("boolean") == 0) {
                
                if(pValue.compareToIgnoreCase("true") != 0 &&  pValue.compareToIgnoreCase("false") != 0) {
                    errors += "Error:Wrong value '" + pValue + "' for boolean Parameter '"
                    + pName + "' of group '" + g.getName() + "' around line "
                    + lineNumber + '\n';//error
                    
                    pEdit = false;
                }
                
                p = new ParameterBoolean(pName, pValue, pEdit, pDoc);
            }
            
            else if(pType.compareToIgnoreCase("file") == 0) {
            	File tempFile = null;
				try {
					tempFile = new File(new URI(pValue));
				} catch (URISyntaxException e) {
					System.out.println("Invalid URI:" + pValue);
					e.printStackTrace();
					// Try if the path is normal path and not a URI
					tempFile = new File(pValue);
				} catch (IllegalArgumentException e) {
					System.out.println("Invalid URI:" + pValue);
					e.printStackTrace();
					// Try if the path is normal path and not a URI
					tempFile = new File(pValue);
				}				
            	if(tempFile != null) {
            		pValue = tempFile.getAbsolutePath();
            	}
                p = new ParameterFile(pName, pValue, ParameterFile.FILE, pEdit, pDoc);
            }
            
            else if(pType.compareToIgnoreCase("directory") == 0) {
				File tempFile = null;
				try {
					tempFile = new File(new URI(pValue));
				} catch (URISyntaxException e) {
					System.out.println("Invalid URI:" + pValue);
					e.printStackTrace();
					// Try if the path is normal path and not a URI
					tempFile = new File(pValue);
				} catch (IllegalArgumentException e) {
					System.out.println("Invalid URI:" + pValue);
					e.printStackTrace();
					// Try if the path is normal path and not a URI
					tempFile = new File(pValue);
				}				
				if(tempFile != null) {
					pValue = tempFile.getAbsolutePath();
				}
                p = new ParameterFile(pName, pValue, ParameterFile.DIR, pEdit, pDoc);
            }
            
            else if(pType.compareToIgnoreCase("string") == 0) {
                p = new ParameterString(pName, pValue, pEdit, pDoc);
            }
            // Handle the LongInteger Type.  For now, we'll treat it as a string.
            else if(pType.compareToIgnoreCase("LongInteger") == 0) {
                p = new ParameterString(pName, pValue, pEdit, pDoc);
            }
            else if(pType.compareToIgnoreCase("JDBCDRIVER") == 0) {
            	p = new ParameterJDBCDriver(pName, pValue, pBaseClass,pEdit, pDoc);
            }
            else {
                errors += "Error:Wrong type '" + pType +"' for parameter '"
                + pName + "' of group '" + g.getName() + "' around line " + 
                lineNumber + '\n'; //error; //error type
                
                pEdit = false;
                
                p = new Parameter(pName, pValue, pEdit, pDoc, pType);
            }
            
            if(p != null) {
            	p.setComments(getComments(data));
                g.addParameter(p);
            }
        }
        
    }
    
    /**
     * Parses the property file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param eFile the e file
     * @return the int
     */
    public static int parsePropertyFile(EditorFile eFile) {
        
        initValues();
        
        try
		{
            getGroups(eFile);
        } catch(IOException ioe){}
        
        eFile.setErrorParsing(errors);
        
        System.out.println(errors);
        
        return eFile.numGroup();
    }
    
}



