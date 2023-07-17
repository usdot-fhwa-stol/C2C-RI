# Summary:

This repository contains the source code of the Center-To-Center Reference Implementation (C2C RI) Tool.  The C2C RI supports efficient deployment of systems using C2C communications by providing a standardized way to verify conformance to C2C standards. The C2C interface may be between two traffic management centers or any two centers that need to coordinate (regionally or locally) the management of a corridor, arterial, incident, event, or more. Currently the C2C RI supports conformance testing for the Institute of Transportation Engineers (ITE) Traffic Management Data Dictionary (TMDD) v3.03c, TMDD v3.03d and TMDD v3.1 standards, and National Transportation Communications for ITS Protocol (NTCIP) 2306 v1.69 Web Services Description Language (WSDL)/Simple Object Access Protocol (SOAP) protocol.

The source code generates jar files associated with each of the following source projects:
* RI_Properties - Includes the classes related to the C2C RI configuration settings.
* RITestCaseData - Includes the classes necessary to provide Test Case data file parameters as content for test cases executed by the C2C RI.
* RITestCaseDefinitions - Includes classes that are only used by various developer testing programs that are included in various other projects.
* RI_Utilities - Includes various utility classes that are used by other C2C RI projects.
* RI_Connections - Includes java extension classes necessary for logging over the wire data.
* RI_Logging - Includes classes responsible for creating, reading and signing test log files.
* C2CRIReportsDBLibrary - Includes classes necessary for creating and populating a reports database which is used in the generation of C2C RI Reports.
* RICenterServices - Contains classes which are used to define the Application Layer Standards, Information Layer Standards and the data that is passed between these layers.
* RI_Jameleon - Contains the data driven test scripting engine used by the C2C RI along with some additional custom tags.
* NTCIP2306v01_69 - Contains the NTCIP 2306 test specifications, WSDL processing code, HTTP and FTP Client and Server code, along with custom NTCIP 2306 related test script tags.
* TMDDCommon - Contains the base TMDD standard dialog, message and emulation handling code.  It also includes TMDD related test script tags.
* TMDDv303 - Extends the base TMDD standard to meet TMDD v3.03c definitions and includes TMDD v3.03c test specifications.
* TMDDv303d - Extends the base TMDD standard to meet TMDD v3.03d definitions and includes TMDD v3.03d test specifications.
* TMDDv31 - Extends the base TMDD standard to meet TMDD v3.1 definitions and includes TMDD v3.1 test specifications.
* RIGUI - Includes the main C2C RI GUI and Wizard related classes.  Additionally it contains the classes responsible for the Test Configuration and Test Results.

The source code was programmed in Apache Netbeans with Java.  

**** Test Suites and Test Log files created by the Open Source version of the C2C RI will not be recognized by the official release version of the C2C RI which is available at https://www.standards.its.dot.gov/DeploymentResources/Tools.  ****

# Organizational Outline:
* Project Title
* Release Notes
* Getting Started
* Prerequisites
* Installing
* Testing
* License
* Contact Information

# Project Title

*Center-to-Center Reference Implementation (C2C RI)*

The C2C RI, is intended to fill existing gaps in ITS standards consistency, interoperability, and conformance. The USDOT developed this tool in response to existing gaps as identified through surveys of public and private entities that implement ITS standards.

## Release Notes

#### Release 1.0.0 (February 21, 2022)
- Initial release

## Getting Started

*Download the source code files. The source code is ready to build if the build environment is ready (see Prerequisites).*


### Prerequisites

### Setup Build Environment
Requires:
- Windows 8 (or higher)

Download Apache Ant 1.10.5 (https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.5-bin.zip).<br/>
Extract to a folder on the build computer (e.g. C:\Libraries\ant) and add to the Path System Environment Variable.

Download Temurin jdk-11.0.19+7 (https://adoptium.net/temurin/releases/?version=11).<br/>
Select the correct .msi installer based on the Operating System and Architecture of the build computer. Run the installer and enable the "JavaSoft (Oracle) registry keys" and "Set JAVA_HOME variable".<br/>

Download the launch4j 3.13 Win32 installation program (https://sourceforge.net/projects/launch4j/files/launch4j-3/3.13/launch4j-3.13-win32.exe/download).<br/>
Run the installer keeping the default settings.

Download the NSIS 3.04  installation program (https://sourceforge.net/projects/nsis/files/NSIS%203/3.04/nsis-3.04-setup.exe/download).<br/>
Run the installer keeping the default settings.

Download the C2C RI source code repository to the build computer.

#### Build the source code
Run Notepad to open \<path where source code was installed\>C2CRIBuildDir\buildfiles\buildRICode.bat. 
On line 12 change %JAVAHOME% to %JAVA_HOME%. 
Erase line 7 (Set JAVA_HOME=C:\Libraries\jdk-11)<br/>

Open a new windows command prompt.<br/>
Change the directory path to the C2CRIBuildDir\buildfiles path of the source code repository.

Type **buildRICode** and press Enter


#### Create the installer

Open a windows command prompt.<br/>
Change the directory path to the C2CRIBuildDir\buildfiles path of the source code repository.

Type **build** "*path where installation files should be placed"*<br/>
Note: If the directory path does not currently exist the user will be asked whether the directory should be created by the script.


### Installing
Step 1. Uninstall any earlier versions of the C2C RI application.

Step 2. Copy the c2cri_Release 2 _Installer.exe to your computer.  Double click on the file to launch the install program.

Step 3. The Main Installation screen displays.

Step 4. You are prompted to choose a directory in which to install the application. You may accept the default directory or create your own directory path (it is recommended to use the default directory). Click Next when done.

Step 5. The Choose Start Menu Folder window displays a dialog.  This window allows you to choose where to install the shortcut in your Start Menu. Choose a folder and click the Install button to install the software.

Step 6. An installation window displays, showing the status of the installation.

Step 7. Once the installation is complete the Completing the C2C RI Setup Wizard window displays. Click the Finish button to complete the installation.

### Testing
Step 1. Navigate to the directory where the C2C RI Application was installed.

Step 2. Double-click the C2CRI.exe file to launch the C2C RI Application.

Step 3. Select the ‘**File->New**’ command from the C2C RI MENU BAR.  Verify that the New Test Configuration Dialog is displayed.  The Test Suite drop down menu for the Information Layer Standard should include standards TMDD v303c, TMDD v303d and TMDD v3.1 at a minimum.  The test suite drop down menu for the Application Layer Standard should include NTCIP 2306 v1.69 at a minimum.

Step 4. Click the **Cancel** button and the New Test Configuration Dialog should close.

Step 5. Select the ‘**File->Reports**’ command from the C2C RI MENU BAR.  Verify that the application window displays the Configuration and Log Report Tabs.  

Step 6. Enter "**SampleReport**" in the Name field at the top of the Configuration Tab in the Report File Selection Frame.  The Configuration tab should include a list of test configuration files (with a .ricfg extension).  Select one of the configuration files.  Click the Configuration File Details Radio button in the Report Details Tab.  Click the **Create** Button at the bottom of the window.

Step 7. A dialog will initially appear indicating the the the report is being created.  Once complete, a Report Status dialog will be presented indicating that the Report Creation has completed.  Click the **OK** button.

Step 8. Click the **View** button at the bottom of the window.  The C2C RI Report Viewer should open and present the created report.  

Step 9. Select '**File -> Exit**' from the C2C RI Report Viewer.  The PDF Viewer should close.

Step 10. Click the **Cancel** button at the bottom of the C2C RI Main Window.  The main C2C RI User Interface screen should be showing.

Step 11. Select '**File->Exit**' command from the C2C RI MENU BAR.  The C2C RI Application should close.

### License

This project is licensed under the apache-2.0 License.

### Contact Information
To initiate technical support, contact c2crisupport@transcore.com. Provide contact information and the C2C RI technical support team will contact you so they can understand the issue(s) you are facing. 
