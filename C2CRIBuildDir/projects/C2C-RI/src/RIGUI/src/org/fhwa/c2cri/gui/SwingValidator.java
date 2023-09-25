/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.math.BigDecimal;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a generic set of methods which can be used
 * to validate swing fields on the various user displays.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SwingValidator {

    /** Tests whether text is included within a text component
     * Present a warning to the user if the field is not valid.
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isPresent(JTextComponent c, String title) {
        if (c.getText().length() == 0) {
            showMessage(c, title + " is a required field.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /** Tests whether a valid integer value is provided within a text component
     * Present a warning to the user if the field is not valid.
     * @param c the component to be checked
     * @param title the title of the component
     * @return flag indicating whether the integer value is valid
     */
    public static boolean isInteger(JTextComponent c, String title) {
        try {
            int i = Integer.parseInt(c.getText());
            return true;
        } catch (NumberFormatException e) {
            showMessage(c, title + " must be an integer.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
    }

    /** Tests whether a valid double value is provided within a text component
     * Present a warning to the user if the field is not valid.
     * @param c the component to be checked
     * @param title the title of the component for error messages
     * @return flag indicating whether the value is valid
     */
    public static boolean isDouble(JTextComponent c, String title) {
        try {
            double d = Double.parseDouble(c.getText());
            return true;
        } catch (NumberFormatException e) {
            showMessage(c, title + " must be a valid number.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
    }

    /** Tests whether a valid decimal value is provided within a text component
     * Present a warning to the user if the field is not valid.
     * @param c the component to be checked
     * @param title the title of the component for error messages
     * @return flag indicating whether the value is valid
     */
    public static boolean isDecimal(JTextComponent c, String title) {
        try {
            BigDecimal bd = new BigDecimal(c.getText());
            return true;
        } catch (NumberFormatException e) {
            showMessage(c, title + " must be a valid number.\n"
                    + "Please re-enter.");
            c.requestFocus();
            return false;
        }
    }

    /** Tests whether a provided value is within a given range.
     * Present a warning to the user if the field is not valid.
     * @param c the component to be checked
     * @param min the minimum value in the range
     * @param max the maximum value in the range
     * @param title the title of the component for error messages
     * @return flag indicating whether the value is valid
     */
    public static boolean isWithinRange(JTextComponent c, double min, double max, String title) {
        double d = Double.parseDouble(c.getText());
        if (d < min || d > max) {
            showMessage(c, title + " must be between " + min + " and " + max + ".\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /** Tests whether text included within a text component is in the proper IP Address form
     * Present a warning to the user if the field is not valid.
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isIPAddress(JTextComponent c, String title) {

        String[] parts = c.getText().split("\\.");
        if (parts.length != 4) {
            showMessage(c, title + " is not valid.  Must have format: [0-255].[0-255].[0-255].[0-255] \n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
        for (String s : parts) {
            int i = Integer.parseInt(s);
            if ((i < 0) || (i > 255)) {
                showMessage(c, title + " is not valid.  Must have format: [0-255].[0-255].[0-255].[0-255] \n"
                        + "Please re-enter.");
                c.requestFocusInWindow();
                return false;
            }
        }

        return true;
    }

    /** Tests whether text included within a text component is in the proper URL form
     * Present a warning to the user if the field is not valid.
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isURL(JTextComponent c, String title) {
        try {
            URL url = new URL(c.getText());
        } catch (Exception ex) {
            showMessage(c, title + " is not a properly formed URL.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /** Tests whether text included within a text component is in the proper port number form.
     *
     * A valid port value is between 0 and 65535.
     *
     * Present a warning to the user if the field is not valid.
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isPort(JTextComponent c, String title) {

        try {
            Integer portNumber = Integer.parseInt(c.getText());

            if (!((portNumber >= 0) && (portNumber <= 65535))) {
                showMessage(c, title + " is not a proper Port number. (From 0 to 65535) \n"
                        + "Please re-enter.");
                c.requestFocusInWindow();
                return false;

            }
        } catch (Exception ex) {
            showMessage(c, title + " is not a properly Port number.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;

        }

        return true;
    }

    /** Tests whether text included within a text component is in the proper Host Name form
     *
     * 1). It can contain only dots, dash and alphanumeric characters.
     * 2). It cannot be more than 63 characters in length.
     * 3). The first character must be an alphanumeric.
     * 4). The last character cannot be a dot or dash.
     * 5). There should at least be one alphabet (This is for Linux).
     *
     * Present a warning to the user if the field is not valid.
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isHostName(JTextComponent c, String title) {
        String domainIdentifier = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
        String domainNameRule = "(" + domainIdentifier + ")((\\.)(" + domainIdentifier + "))*";
        String oneAlpha = "(.)*((\\p{Alpha})|[-])(.)*";

        if ((c.getText() == null) || (c.getText().length() > 63)) {
            showMessage(c, title + " is not a properly formed host name.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }

        if (!(c.getText().matches(domainNameRule) && c.getText().matches(oneAlpha))) {
            showMessage(c, title + " is not a properly formed host name.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /** Tests whether text included within a text component is in the proper URL form
     * Present a warning to the user if the field is not valid.
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isAlphanumeric(JTextComponent c, String title) {

        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(c.getText());
        boolean matchFound = m.matches();
        if (!matchFound) {
            showMessage(c, title + " is not a properly formed alphanumeric string.\n"
                    + "Please re-enter.");
            c.requestFocusInWindow();
            return false;
        }

        return true;
    }

    /**
     * Displays the error message.
     *
     * @param c the c
     * @param message the message
     */
    private static void showMessage(JTextComponent c, String message) {
        JOptionPane.showMessageDialog(c, message, "Invalid Entry",
                JOptionPane.ERROR_MESSAGE);
    }
}
