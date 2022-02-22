/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.utilities;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Whole combination is means, 1 to 35 characters with any lower or upper case
 * character, digit or special symbol " ", "_" and "-" only.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class FilenameValidator {

    /** The pattern. */
    private Pattern pattern;
    
    /** The matcher. */
    private Matcher matcher;
    
    /** The error list. */
    private ArrayList<String> errorList = new ArrayList<String>();
    
    /** The max file length. */
    private static Integer MAX_FILE_LENGTH = 35;
    
    /** The Constant FILENAME_PATTERN. */
    private static final String FILENAME_PATTERN = "^[a-zA-Z0-9 _-]{1,35}$";

    /**
     * Instantiates a new filename validator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public FilenameValidator() {
        pattern = Pattern.compile(FILENAME_PATTERN);
    }

    /**
     * Validate filename with regular expression.
     *
     * @param filename filename for validation
     * @return true valid filename, false invalid filename
     */
    public boolean validate(final String filename) {
        boolean matches = false;
        if (filename.length() > MAX_FILE_LENGTH) {
            errorList.add("The file name length is " + filename.length() + " which exceeds the maximum " + MAX_FILE_LENGTH + " characters.");
        }
        if (filename.trim().isEmpty()) {
            matches = false;
            errorList.add("No file name was entered.");

        } else {
            matcher = pattern.matcher(filename.trim());

            try {
                matches = matcher.matches();
                if (!matches) {
                    matcher.lookingAt();
                    errorList.add("Invalid \"" + filename.substring(matcher.end(), matcher.end()) + "\" character encountered in file name.");
                }
            } catch (Exception ex) {
                matches = false;
                errorList.add("Invalid character(s) encountered in file name.  \nOnly lower or upper case characters, digits or special symbols ' ', _ and -  are valid.");
            }
        }
        return matches;
    }

    /**
     * Gets the errors encountered.
     *
     * @return the errors encountered
     */
    public String getErrorsEncountered() {
        String tempString = "";
        for (String thisError : errorList) {
            tempString = tempString.concat(thisError + "\n");
        }
        return tempString;
    }
}
