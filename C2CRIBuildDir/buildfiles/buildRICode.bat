REM This batch file builds all of the C2C RI application projects


ECHO OFF
CLS
Set CURRENTDIR=%~dp0

Set PATH=%PATH%;c:\program files (x86)\launch4j\;c:\program files (x86)\nsis\
ECHO.

Echo Path is set to %PATH%
Echo Java Home is set to %JAVA_HOME%
Echo Current Directory is %CURRENTDIR%

:DOBATCH
	call java -version


        ECHO 1. Building RI_Properties ...

        chdir /d ..\projects\c2c-ri\src\RI_Properties
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%


        ECHO 2. Building RITestCaseData ...

        chdir /d ..\projects\c2c-ri\src\RITestCaseData
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%


        ECHO 3. Building RITestCaseDefinitions ...

        chdir /d ..\projects\c2c-ri\src\RITestCaseDefinitions
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%

		
        ECHO 4. Building RI_Utilities ...

        chdir /d ..\projects\c2c-ri\src\RI_Utilities
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%


		
        ECHO 5. Building RI_Connections ...

        chdir /d ..\projects\c2c-ri\src\RI_Connections
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%
		

        ECHO 6. Building RI_Logging ...
        chdir /d ..\projects\c2c-ri\src\RI_Logging
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%
		

        ECHO 7. Building C2CRIReportsDBLibrary ...

        chdir /d ..\projects\c2c-ri\src\C2CRIReportsDBLibrary
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%
		
		
        ECHO 8. Building RICenterServices ...
        chdir /d ..\projects\c2c-ri\src\RICenterServices
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%

		
        ECHO 9. Building RI_Jameleon ...
        chdir /d ..\projects\c2c-ri\src\jameleon-test-suite-3_3-RC1-C2CRI\jameleon-core
        call ant cleanDist
        call ant cleanBuild
        call ant -Dnb.internal.action.name=build main
		call ant -Dnb.internal.action.name=build build.ripoc	
        chdir /d %CURRENTDIR%


        ECHO 10. Building NTCIP2306v01_69 ...
        chdir /d ..\projects\c2c-ri\src\NTCIP2306v01_69
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%


        ECHO 11. Building TMDDProcessingUtilities ...
        chdir /d ..\projects\c2c-ri\src\tmddprocessingutilities
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%

        ECHO 12. Building TMDDCommon ...
        chdir /d ..\projects\c2c-ri\src\TMDDCommon
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%
		
        ECHO 13. Building TMDDv303 ...
        chdir /d ..\projects\c2c-ri\src\TMDDv303
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%

        ECHO 14. Building TMDDv303d ...
        chdir /d ..\projects\c2c-ri\src\TMDDv303d
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%

        ECHO 15. Building TMDDv31 ...
        chdir /d ..\projects\c2c-ri\src\TMDDv31
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%

        ECHO 16. Building RIGUI ...
        chdir /d ..\projects\c2c-ri\src\RIGUI
        call ant clean
        call ant -Dnb.internal.action.name=build jar
        chdir /d %CURRENTDIR%		
		

		ECHO 17. Signing the NTCIP2306v01_69 Jar File
        chdir /d ..\projects\c2c-ri\src\NTCIP2306v01_69\Dist
		set NTCIPDIR=%cd%
		echo NTCIP Jar Directory is %NTCIPDIR%
		chdir /d %CURRENTDIR%
		
		chdir /d ..\projects\c2c-ri\src\RIGUI\TestSuites
		set KEYDIR=%cd%
		echo Key Directory is %KEYDIR%		
		copy %NTCIPDIR%\NTCIP2306v01_69.jar %KEYDIR%\NTCIP2306v01_69OpenSource.jar		
        chdir /d %CURRENTDIR%

		
		ECHO 18. Signing the TMDDv303 Jar File
        chdir /d ..\projects\c2c-ri\src\TMDDv303\Dist
		set TMDDv303DIR=%cd%
		echo TMDD Jar Directory is %TMDDv303DIR%
		chdir /d %CURRENTDIR%
		
		chdir /d ..\projects\c2c-ri\src\RIGUI\TestSuites
		set KEYDIR=%cd%
		echo Key Directory is %KEYDIR%
		copy %TMDDv303DIR%\TMDDv303.jar %KEYDIR%\TMDDv303-OpenSource.jar		
        chdir /d %CURRENTDIR%

		ECHO 19. Signing the TMDDv303d Jar File
        chdir /d ..\projects\c2c-ri\src\TMDDv303d\Dist
		set TMDDv303dDIR=%cd%
		echo TMDD 303d Jar Directory is %TMDDv303dDIR%
		chdir /d %CURRENTDIR%
		
		chdir /d ..\projects\c2c-ri\src\RIGUI\TestSuites
		set KEYDIR=%cd%
		echo Key Directory is %KEYDIR%
		copy %TMDDv303dDIR%\TMDDv303d.jar %KEYDIR%\TMDDv303d-OpenSource.jar				
        chdir /d %CURRENTDIR%

		ECHO 20. Signing the TMDDv31 Jar File
        chdir /d ..\projects\c2c-ri\src\TMDDv31\Dist
		set TMDDv31DIR=%cd%
		echo TMDD 304 Jar Directory is %TMDDv31DIR%
		chdir /d %CURRENTDIR%
		
		chdir /d ..\projects\c2c-ri\src\RIGUI\TestSuites
		set KEYDIR=%cd%
		echo Key Directory is %KEYDIR%
		copy %TMDDv31DIR%\TMDDv31.jar %KEYDIR%\TMDDv31-OpenSource.jar		

        chdir /d %CURRENTDIR%
		
        GOTO DONE


:OOPS
        ECHO Exiting batch file due to error %errorlevel%!!


:DONE
        chdir /d %CURRENTDIR%
        ECHO Done!

