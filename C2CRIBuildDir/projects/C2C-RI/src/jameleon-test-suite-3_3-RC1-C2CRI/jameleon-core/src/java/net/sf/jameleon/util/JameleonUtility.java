/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.util;

import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.exception.JameleonScriptException;

import java.io.*;
import org.apache.log4j.LogManager;

public class JameleonUtility {

    /**
     * This class isn't meant to be instantiated.
     */
    private JameleonUtility(){
    }

    public static FunctionalPoint loadFunctionalPoint(String className, Object callingClass){
        FunctionalPoint fpTemp = null;
        String classNameOrig = className;
        String className1 = classNameOrig.replace('.',File.separatorChar);
        className1 = className1+InstanceSerializer.SERIALIZED_EXT;
        String className2 = classNameOrig.replace('.','/');
        className2 = className2+InstanceSerializer.SERIALIZED_EXT;
        InputStream is = null;
        try{
        	is = getInputStream(className1, callingClass);
            fpTemp = (FunctionalPoint)InstanceSerializer.deserialize(is);
            closeInputStream(is);
            classNameOrig = className1;
            if (fpTemp == null) {
            	is = getInputStream(className2, callingClass);
                fpTemp = (FunctionalPoint)InstanceSerializer.deserialize(is);
                closeInputStream(is);
                classNameOrig = className2;
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }finally{
        	closeInputStream(is);
        }
        if (fpTemp == null) {
            throw new JameleonScriptException("\nCould not find '"+classNameOrig+"'!\nPlease be sure to execute the Jameleon-provided"+
                                       " Ant task for registering custom\ntags and that the files (.dat) generated from "+
                                       "this are placed in the CLASSPATH!");
        }
        return fpTemp;
    }
    
    protected static void closeInputStream(InputStream is){
    	try{
	    	if (is != null){
	    		is.close();
	    	}
    	}catch(IOException e){
    		//So what, we couldn't close the connection
    		//Maybe it was already closed?
    	}
    }

    public static InputStream getInputStream(String className, Object callingClass){
        InputStream input = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = callingClass.getClass().getClassLoader();
            input = classLoader.getResourceAsStream( className );
        } else {
            input = classLoader.getResourceAsStream( className );
            if (input == null) {
                classLoader = callingClass.getClass().getClassLoader();
                input = classLoader.getResourceAsStream( className );
            }
        }
        return input;
    }


    /**
     * A helper method to get the stack track of a throwable as a String
     * @param t The exception to get the stack trace from.
     * @return The stack trace as a String.
     */
    public static String getStack(Throwable t){
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Formats the error message.
     * @param msg - The message to format.
     * @return The formatted <code>msg</code>.
     */
    public static String createErrMsg(String msg) {
    	return msg;
    }

    /**
     * Write some text to a file who's directory may or may not exist.
     * @param f - The file to write to.
     * @param content - The content to write.
     * @throws IOException - If the directory structure could not be created or if the file could not be written to.
     */
    public static void recordResultsToFile(File f, String content) throws IOException{
        createDirStructure(f.getParentFile());
        FileWriter fw = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fw);
        try{
            out.write(content);
        }finally{
            out.close();
        	fw.close();
        }
    }

    /**
     * Write some text to a file who's directory may or may not exist.
     * @param f - The file to write to.
     * @param content - The content to write.
     * @param encoding - The encoding type to write to.
     * @throws IOException - If the directory structure could not be created or if the file could not be written to.
     */
    public static void recordResultsToFile(File f, String content, String encoding) throws IOException{
        createDirStructure(f.getParentFile());
        BufferedWriter out = null;
        FileOutputStream fos = new FileOutputStream(f);
        try{
            out = new BufferedWriter(new OutputStreamWriter(fos, encoding));
        }catch(UnsupportedEncodingException ue){
            ue.printStackTrace();
            //throw new IOException(getStack(ue));
        }
        try{
            if (out != null){
                out.write(content);
            }
        }finally{
            if (out != null){
                out.close();
            }
            fos.close();
        }
    }

    /**
     * Ensures that a directory structure is created.
     * @param f The directory to create the structure.
     */
    public static void createDirStructure(File f){
        if (!f.exists() && !f.mkdir()) {
            createDirStructure(f.getParentFile());
            f.mkdir();
        }
    }

    /**
     * Deletes the given directory/file and all child files.
     * @param f The directory/file to delete.
     */
    public static void deleteDirStructure(File f){
        if (f.isFile() || f.list().length == 0){
            if (!f.delete())
				LogManager.getLogger(JameleonUtility.class).error("Failed to delete " + f.getAbsolutePath());
        }else if (f.isDirectory()){
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++){
                deleteDirStructure(files[i]);
            }
            deleteDirStructure(f);
        }
    }

    public static String decodeXMLToText(String str){
        str = str.replace("\\&lt;","<");
        str = str.replace("\\&gt;",">");
        str = str.replace("\\&amp;","&");
        return str;
    }

    public static String decodeTextToXML(String str){
        str = str.replace("&","\\&amp;");
        str = str.replace("<","\\&lt;");
        str = str.replace(">","\\&gt;");
        return str;
    }

    public static String executionTimeToString(long time){
        long hours, mins, secs, tempTime;
        hours = time / 3600000;
        tempTime = time %  3600000;
        mins = tempTime / 60000;
        tempTime = tempTime % 60000;
        secs = tempTime / 1000;
        tempTime = tempTime % 1000;
        String msS = null;
        if (tempTime < 10) {
            msS = "00"+tempTime;
        }else if (tempTime < 100) {
            msS = "0"+time;
        }else{
            msS = tempTime+"";
        }
        String formattedTime = concatNum(hours, "h ");
        formattedTime += concatNum(mins, "m ");
        if( time == 0 || secs > 0 || tempTime > 0){
            formattedTime += secs + "."+msS+"s";
        }

        return formattedTime.trim();
//        return hours+"h "+mins+"m "+secs+"."+msS+"s";
    }

    private static String concatNum(long num, String postfix){
        String concat = "";
        if (num > 0){
            concat = num + postfix;
        }
        return concat;
    }


    public static String stripHtmlComments(String text){
        return text.replaceAll("(?s)<\\!--.*?-->","");
        //return java.util.regex.Pattern.compile("<\\!--.*?-->","");
    }

    public static String convertNullToString(String str){
        String rtrStr = ""; 
        if (str != null && str.length() > 0) {
            rtrStr = str;
        }
        return rtrStr;
    }

    public static String getEndingPath(String path, String file){
        String endingPath = stripCurrentDirFromPath(fixFileSeparators(file));
        path = stripCurrentDirFromPath(fixFileSeparators(path));
        int index = endingPath.indexOf(path);
        if (index != -1){
            endingPath = endingPath.substring(index + path.length());
            if (endingPath.startsWith(File.separator)){
                endingPath = endingPath.substring(1);
            }
        }
        return endingPath;
    }

    private static String stripCurrentDirFromPath(String path){
        String newPath = path;
        if (newPath.startsWith("."+File.separator)){
            newPath = newPath.substring(2);
        }
        return newPath;
    }

    public static String getEndingPath(String path, File file){
        return getEndingPath(path, file.getPath());
    }

    public static String getEndingPath(File path, String file){
        return getEndingPath(path.getPath(), new File(file));
    }

    public static String getFileNameFromPath(String path){
        String fileName = new File(path).getPath();
        int index = fileName.lastIndexOf(File.separator);
        if (index != -1) {
            fileName = fileName.substring(index+1);
        }
        index = fileName.lastIndexOf(".");
        if (index != -1 ) {
            fileName = fileName.substring(0,index);
        }
        return fileName;
    }

    public static String getFileNameFromScriptPath(String path){
        String fileName = path;
        if (fileName != null){
            fileName = fixFileSeparators(fileName, "/");
            int index = fileName.lastIndexOf("/");
            if (index != -1) {
                fileName = fileName.substring(index+1);
            }
        }
        return fileName;
    }

    /**
     * @param path a path using forward slashes
     * @return the original path with all forward slashes replaced by unix slash
     */
    public static String fixFileSeparators(String path) {
        return fixFileSeparators(path, File.separator);
    }

    /**
     * @param path a path using forward slashes
     * @param fileSeparator the character to replace the forward slashes
     * @return the original path with all forward slashes replaced by <code>fileSeparator</code>
     */
    public static String fixFileSeparators(String path, String fileSeparator) {
        String fixedPath = "";
        if (path != null){
            String separator = File.separator;
            //Switch the separator to split on if the OS-specific separator isn't found
            if (path.indexOf(separator) == -1){
                if (separator.equals("/")){
                    separator = "\\";
                }else if (separator.equals("\\")){
                    separator = "/";
                }
            }
            if (separator.equals("\\")){
                separator = "\\\\";
            }
            String[] pathParts = path.split(separator);
            for (int i = 0; i < pathParts.length; i++) {
                fixedPath += pathParts[i];
                if (i != pathParts.length - 1) {
                    fixedPath += fileSeparator;
                }
            }
        }
        return fixedPath;
    }
}
