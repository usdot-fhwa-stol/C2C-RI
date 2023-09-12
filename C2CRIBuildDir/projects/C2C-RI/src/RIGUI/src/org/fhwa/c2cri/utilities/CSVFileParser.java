/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class CSVFileParser processes CSV files.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class CSVFileParser {
    
    /** The header list. */
    public  ArrayList<String> headerList = new ArrayList<String>();
    
    /** The csv data. */
    public  ArrayList<ArrayList<String>> csvData = new ArrayList<ArrayList<String>>();

    /**
     * Gets the csv data.
     *
     * @return the csv data
     */
    public ArrayList<ArrayList<String>> getCsvData() {
        return csvData;
    }

    /**
     * Gets the header list.
     *
     * @return the header list
     */
    public ArrayList<String> getHeaderList() {
        return headerList;
    }

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public void main(String[] args) {
        File newFile = new File("c:/temp/TMDDNeeds.csv");
        try{
            parse(newFile.toURI().toURL());            
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    /**
     * Parses the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param csvFile the csv file
     */
    public void parse(URL csvFile) {
//create BufferedReader to read csv file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.openStream())))
		{
            //csv file containing data
//            String strFile = "C:/FileIO/example.csv";
            
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line

//String pattern = ",(?!([^\"]*\"[^\"]*\")*[^\"]*\"[^\"]*$)";
String pattern = "\"([^\"]*)\"|(?<=,|^)([^,]*)(?=,|$)";
Pattern splitter = Pattern.compile(pattern);
Matcher matcher = null;

            while ((strLine = br.readLine()) != null) {
//break comma separated line using ","

                if ((strLine != null)&&(!strLine.isEmpty())){
                lineNumber++;
//                String[] stringElements = splitter.split(strLine);
                matcher = splitter.matcher(strLine);
//                String[] stringElements = splitter.split(strLine);
//                String[] stringElements = strLine.split(",", 512);
                ArrayList<String> tempArray = new ArrayList<String>();

                String match;
                while (matcher.find()){
                    match = matcher.group(1);
                    if (match != null){
                    } else {
                        match = matcher.group(2);
                    }

                        //The first line should contain header info
                        if (lineNumber == 1) {
                            headerList.add(match.replace("\"", ""));

                        } else {
                            if (match.startsWith("\"")){
                                if (match.endsWith("\"")){
                                    match = match.replaceFirst("\"", "");
                                    match = match.substring(0, match.lastIndexOf("\"")-1);
                                }
                            }
                            tempArray.add(match);
                        }

                }
                if (lineNumber > 1) csvData.add(tempArray);

//                for (int ii=0; ii <  stringElements.length; ii++){
//                        //The first line should contain header info
//                        if (lineNumber == 1) {
//                            headerList.add(stringElements[ii].replace("\"", ""));
//
//                        } else {
//                            if (stringElements[ii].startsWith("\"")){
//                                if (stringElements[ii].endsWith("\"")){
//                                    stringElements[ii].replaceFirst("\"", "");
//                                    stringElements[ii] = stringElements[ii].substring(0, stringElements[ii].lastIndexOf("\"")-1);
//                                }
//                            }
//                            tempArray.add(stringElements[ii]);
//                        }
//
//                }
//                if (lineNumber > 1) csvData.add(tempArray);

                }
            }
        } catch (Exception e) {
            System.out.println("Exception while reading csv file: " + e);
            e.printStackTrace();
        }
//    System.out.println("Header Array \n" + headerList.toString());
//    System.out.println("\n\nData Array \n" + csvData.toString());

    }
    
    /**
     * Parse2.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param csvFile the csv file
     */
    public void parse2(URL csvFile) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.openStream())))
		{
            //csv file containing data
//            String strFile = "C:/FileIO/example.csv";
            //create BufferedReader to read csv file
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line

            while ((strLine = br.readLine()) != null) {
                lineNumber++;
//break comma separated line using ","
                st = new StringTokenizer(strLine, ",", true);
                String lastToken = "";
                ArrayList<String> tempArray = new ArrayList<String>();
                while (st.hasMoreTokens()) {
//display csv values
                    String tokenValue = st.nextToken();
                    if (!tokenValue.equals(",")) {
                        tokenNumber++;

                        //The first line should contain header info
                        if (lineNumber == 1) {
                            headerList.add(tokenValue);

                        } else {
                            tempArray.add(tokenValue);
                        }

//                        System.out.println("Line # " + lineNumber
//                                + ", Token # " + tokenNumber
//                                + ", Token : " + tokenValue);


                    } else {
                        if (tokenValue.equals(lastToken)) {
                            tokenNumber++;
                            tokenValue = "";
                            //The first line should contain header info
                            if (lineNumber == 1) {
                                headerList.add(tokenValue);

                            } else {
                                tempArray.add(tokenValue);
                            }

//                            System.out.println("Line # " + lineNumber
//                                    + ", Token # " + tokenNumber
//                                    + ", Token : " + tokenValue);

                        }
                    }
                    lastToken = tokenValue;
                }
                //reset token number
                tokenNumber = 0;
                if (lineNumber > 1) csvData.add(tempArray);
            }
        } catch (Exception e) {
            System.out.println("Exception while reading csv file: " + e);
            e.printStackTrace();
        }
//    System.out.println("Header Array \n" + headerList.toString());
//    System.out.println("\n\nData Array \n" + csvData.toString());

    }
}
