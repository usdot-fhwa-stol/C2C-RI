/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.fhwa.c2cri.testmodel.TestCaseMatrix;

/**
 * The Class testTestCaseMatrix.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class testTestCaseMatrix implements Serializable{

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        String testCaseMatrixURL = "file:/c:/inout/tmddv3TCMatrix1.csv";
        testAFile(testCaseMatrixURL);
        System.out.println("\n\n");
        String testCaseMatrixURL2 = "file:/c:/inout/tmddv3TCMatrix2.csv";
        testTwoFiles(testCaseMatrixURL, testCaseMatrixURL2);
        }

    /**
     * Test a file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCaseMatrixURLString the test case matrix url string
     */
    public static void testAFile(String testCaseMatrixURLString){
                    try {
                        URL newFile = new URL(testCaseMatrixURLString);
                        System.out.println("suiteSpecString External = "+ newFile.toExternalForm());
                        System.out.println(" First Trial Effort!!!");
                        TestCaseMatrix newMatrix = new TestCaseMatrix(newFile);
                        System.out.println(newMatrix.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
      //              } catch (URISyntaxException e) {
       //                 e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println(testCaseMatrixURLString + " not found. Using default settings.");
                    } catch (NullPointerException npe) {
                        System.err.println(npe.getMessage());
                    } catch (Exception ex){
                        System.err.println(ex.getMessage());
                    }

    }

    /**
     * Test two files.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCaseMatrixURLString the test case matrix url string
     * @param testCaseMatrixURLExtString the test case matrix url ext string
     */
    public static void testTwoFiles(String testCaseMatrixURLString, String testCaseMatrixURLExtString){
                    try {
                        URL newFile = new URL(testCaseMatrixURLString);
                        URL newFile2 = new URL(testCaseMatrixURLExtString);
                        System.out.println("suiteSpecString External = "+ newFile.toExternalForm());
                        System.out.println(" Two File Trial Effort!!!");
                        TestCaseMatrix newMatrix = new TestCaseMatrix(newFile, newFile2);
                        System.out.println(newMatrix.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
      //              } catch (URISyntaxException e) {
       //                 e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println(testCaseMatrixURLString + " not found. Using default settings.");
                    } catch (NullPointerException npe) {
                        System.err.println(npe.getMessage());
                    } catch (Exception ex){
                        System.err.println(ex.getMessage());
                    }

    }

    /**
     * Test with suites.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void testWithSuites(){
                File testSuiteFolder = new File(".\\TestSuites");
        System.out.println("Looking at " + testSuiteFolder.getPath());
        FilenameFilter jarFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
//		    	return true;
            }
        };

        File[] listOfTestSuites = testSuiteFolder.listFiles(jarFilter);
        List<URL> jarList = new ArrayList();

        if (listOfTestSuites == null) {
            // Either dir does not exist or is not a directory
            System.out.println("No Pre-defined Test Suite files found.");
        } else {
            for (int i = 0; i < listOfTestSuites.length; i++) {
                // Get filename of file or directory
                System.out.println(listOfTestSuites[i].getName());

                try {
                    jarList.add(listOfTestSuites[i].toURI().toURL());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            for (int i = 0; i < jarList.size(); i++) {
                System.out.println("Jar File => " + jarList.get(i).toExternalForm());
            }

            URL[] jars = (URL[]) jarList.toArray(new URL[0]);

            URL tryIt = jars[1];
            try{
               URL[] smallJar = new URL[1];
               smallJar[0] = jars[0];
     //          URLClassLoader newloader = new URLClassLoader(smallJar);
               String suiteSpecString = "jar:file:"+smallJar[0].getFile() + "!/SuiteSpec.properties";
               System.out.println( "  Suite Spec String = "+ suiteSpecString);
    //           URL withSpec = loader.findResource("SuiteSpec.properties");
    //            System.out.println(" the Trial WithSpec = " + withSpec.getFile() + " -- External = "+ withSpec.toExternalForm());
                    try {
                        URL newFile = new URL(suiteSpecString);
                        System.out.println("suiteSpecString External = "+ newFile.toExternalForm());
                        System.out.println(" First Trial Effort!!!");
                        TestCaseMatrix newParser = new TestCaseMatrix(newFile);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
      //              } catch (URISyntaxException e) {
       //                 e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println(suiteSpecString + " not found. Using default settings.");
                    } catch (NullPointerException npe) {
                        System.err.println(npe.getMessage());
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

            URLClassLoader loader = new URLClassLoader(jars);
            Thread.currentThread().setContextClassLoader(loader);
            try {
                Enumeration suiteSpecs = loader.findResources("SuiteSpec.properties");

                while (suiteSpecs.hasMoreElements()) {
                    URL thisOne = (URL) suiteSpecs.nextElement();
                    System.out.println("thisOne : " + thisOne.getFile() + " @ " + thisOne.getPath());





//        File newFile = new File("c:/temp/TMDDNeeds.csv");
                    try {
                        URL newFile = new URL("file:/C:/projects/C2C-RI/src/RIGUI/./TestSuites/TMDDV3_NTCIP2304.jar!/SuiteSpec.properties");
                        TestCaseMatrix newParser = new TestCaseMatrix(newFile);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
      //              } catch (URISyntaxException e) {
       //                 e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println(thisOne.getFile() + " not found. Using default settings.");
                    } catch (NullPointerException npe) {
                        System.err.println(npe.getMessage());
                    }
                }
                 }
                catch (Exception e) {
                    System.err.println("Error Loading CSV from Test Suites");
                }

            }


    }
    }
