@echo off

REM SET _JAVA_HOME=C:/jdk1.4.1
REM SET _JAVA_HOME=C:\j2sdk1.4.1_02
SET _JAVA_HOME=C:\Program Files\java\jdk1.6.0_17
SET _COMPILER=modern
SET JAMELEON_HOME=../jameleon-core

:error_checking
REM IF THE AMPERSAND IS SPACED AFTER THE SET, THE SPACE WILL BE INCLUDED IN THE VARIABLE VALUE.
IF NOT DEFINED JAVA_HOME ( SET JAVA_HOME=%_JAVA_HOME%)
IF NOT EXIST "%JAVA_HOME%\bin" ( SET ERROR=1& echo ERROR: %JAVA_HOME% You either need to install java or set JAVA_HOME)
IF NOT DEFINED COMPILER ( SET COMPILER=%_COMPILER% )
IF DEFINED ERROR ( ECHO CAN'T CONTINUE UNTIL YOU FIX THE ABOVE ERRORS & goto end )

:run_command
set LIBS=%JAMELEON_HOME%/lib/ant
set CP=.;%JAMELEON_HOME%/lib/junit.jar;%LIBS%/ant-launcher.jar;"%JAVA_HOME%/lib/tools.jar"
set PATH=%PATH%;lib

"%JAVA_HOME%/bin/java" -Xmx192m -Xms64m -classpath %CP% org.apache.tools.ant.launch.Launcher -Dbuild.compiler=%COMPILER% %*


:end
