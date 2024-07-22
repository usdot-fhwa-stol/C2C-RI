REM This batch files creates a build instance of the C2C RI application
REM in a directory selected by the user.


ECHO OFF
CLS
Set CURRENTDIR=%~dp0
Set PACKAGEDIR=%~f1
Set PATH=%PATH%;c:\program files (x86)\launch4j\;c:\program files (x86)\nsis\
ECHO.

Echo Current Directory is %CURRENTDIR%
IF [%1]==[] GOTO BLANK
IF exist %~f1 GOTO EXISTS
GOTO NOTEXIST
ECHO This slipped through the crack!! %~f1

:BLANK
        ECHO No destination path was entered for the build.  %1
        GOTO DONE

:NOTEXIST     
        SET/P ANSWER=The destination path %1 does not exist.  Would you like to create it (Y/N)?
        echo You chose %ANSWER%

        IF /I {%ANSWER%}=={N} GOTO DONE

        MKDIR %~f1
        IF [%errorlevel%] GTR [1] GOTO OOPS 
        echo Completed creation of directory %~f1.
        GOTO EXISTS


:EXISTS
        ECHO The destination path %1 exists.  Starting the build process ...

:DOBATCH
        ECHO 1. Copying the RIGUI Distribution folder to %~f1
        mkdir %~f1\dist
        xcopy  /Q ..\projects\C2C-RI\src\RIGUI\dist\*.jar %~f1\dist
        ECHO 2. Copying the Lib folder to %~f1\dist\lib
        mkdir %~f1\dist\lib
        xcopy /E /Q ..\lib %~f1\dist\lib
        ECHO 3. Copying the C2CRIReportsDBLibrary Distribution jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\C2CRIReportsDBLibrary\dist\*.jar %~f1\dist\lib
        ECHO 4. Copying the Jameleon jars to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\jameleon-test-suite-3_3-RC1-C2CRI\jameleon-core\dist\*.jar %~f1\dist\lib
        ECHO 5. Copying the RI Plugin jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\jameleon-test-suite-3_3-RC1-C2CRI\ri-plugin\dist\*.jar %~f1\dist\lib
        ECHO 6. Copying the NTCIP2306v01_69 Distribution jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\NTCIP2306v01_69\dist\*.jar %~f1\dist\lib
        ECHO 7. Copying the RI Logging Distribution jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\RI_Logging\dist\*.jar %~f1\dist\lib
        ECHO 8. Copying the RI Properties Distribution jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\RI_Properties\dist\*.jar %~f1\dist\lib
        ECHO 9. Copying the RI_Utilties Distribution jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\RI_Utilities\dist\*.jar %~f1\dist\lib
        ECHO 10. Copying the RI_CenterServices Distribution jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\RICenterServices\dist\*.jar %~f1\dist\lib
        ECHO 11. Copying the RITestCaseData jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\RITestCaseData\dist\*.jar %~f1\dist\lib
        ECHO 12. Copying the RI_Connections jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\RI_Connections\dist\*.jar %~f1\dist\lib
        ECHO 13. Copying the TMDDCommon jar to %~f1\dist\lib
        xcopy  /Q ..\projects\C2C-RI\src\TMDDCommon\dist\*.jar %~f1\dist\lib
        ECHO 14. Copying the CustomTestSuites folder to %~f1
        mkdir %~f1\CustomTestSuites
        ECHO 15. Copying the TestSuites folder to %~f1
        mkdir %~f1\TestSuites
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\TestSuites %~f1\TestSuites
        ECHO 16. Copying the Keystore folder to %~f1
        mkdir %~f1\keystore
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\keystore %~f1\keystore
        ECHO 17. Copying the Reports folder to %~f1
        mkdir %~f1\Reports
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\Reports %~f1\Reports
        ECHO 18. Copying the Resources folder to %~f1
        mkdir %~f1\Resources
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\Resources %~f1\Resources
        ECHO 19. Copying the Test Files folder to %~f1
        mkdir %~f1\TestFiles
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\TestFiles %~f1\TestFiles
        ECHO 20. Copying the Properties Files to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\*.properties %~f1
        ECHO 21. Copying the .db3 Files to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\*.db3 %~f1
        ECHO 22. Copying the .bmp Files to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\*.bmp %~f1
        ECHO 23. Copying the .jpg Files to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\*.jpg %~f1
        ECHO 24. Copying the Config Files to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\*.conf %~f1
        ECHO 25. Copying the README.txt File to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\README.txt %~f1
        ECHO 26. Copying the UsersManual.pdf File to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\UsersManual.pdf %~f1
        ECHO 27. Copying the Test RI Config Files and Log Files to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\Release3ConfigFiles\*.* %~f1
        ECHO 28. Copying the JRE Folder to %~f1
        mkdir %~f1\jre
        xcopy /E /Q ..\jre %~f1\jre
        ECHO 29. Copying the Emulation Data Files folder to %~f1
        mkdir %~f1\emulationDataFiles
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\emulationDataFiles %~f1\emulationDataFiles
        ECHO 30. Copying the Emulation TestConfigurationFiles folder to %~f1
        mkdir %~f1\TestConfigurationFiles
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\TestConfigurationFiles %~f1\TestConfigurationFiles
        ECHO 31. Copying the NTCIP 2306 SUT Test Files folder to %~f1
        mkdir %~f1\NTCIP2306SUTTestFiles
        xcopy /E /Q ..\projects\C2C-RI\src\RIGUI\NTCIP2306SUTTestFiles %~f1\NTCIP2306SUTTestFiles
        ECHO 32. Copying the log4j2.xml File to %~f1
        xcopy /Q ..\projects\C2C-RI\src\RIGUI\log4j2.xml %~f1

        chdir /d %~f1
        launch4jc.exe %CURRENTDIR%\C2CRILaunch4jConfig.xml
        copy %CURRENTDIR%\c2cri.exe %~f1
        erase %CURRENTDIR%\c2cri.exe
		
        makensis.exe %CURRENTDIR%\"c2cri with Start Menu.nsi"
        copy %CURRENTDIR%\c2cri_installer.exe %~f1
        erase %CURRENTDIR%\c2cri_installer.exe

        launch4jc.exe %CURRENTDIR%\C2CRILaunch4jConfigDebug64.xml
        copy %CURRENTDIR%\c2criDebug.exe %~f1
        erase %CURRENTDIR%\c2criDebug.exe
		
        GOTO DONE


:OOPS
        ECHO Exiting batch file due to error %errorlevel%!!


:DONE
        chdir /d %CURRENTDIR%
        ECHO Done!