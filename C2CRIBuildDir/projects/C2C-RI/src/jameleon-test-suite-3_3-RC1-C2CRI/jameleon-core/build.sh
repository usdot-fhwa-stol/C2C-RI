#!/bin/bash
_JAVA_HOME=/usr/local/java
_COMPILER=modern
export JAMELEON_HOME=../jameleon-core

if [ -z "$JAVA_HOME" ]; then
	JAVA_HOME=$_JAVA_HOME
fi

if [ -z "$COMPILER" ]; then
	COMPILER=$_COMPILER
fi

if [ ! -d "$JAVA_HOME" ]; then
	error=true 
	echo "You either need to install java or set JAVA_HOME"
fi

if [ "$error" = "true" ]; then
	echo "CAN'T CONTINUE UNTIL THE ABOVE ERRORS ARE FIXED"
else
    LIBS=$JAMELEON_HOME/lib/ant
    
    if [ $OSTYPE = cygwin ]; then
        CP='$JAVA_HOME/lib/tools.jar'
        CP="$CP;.;$JAMELEON_HOME/lib/junit.jar;$LIBS/ant-launcher.jar"
        export PATH="$PATH;lib"
    else
        CP=.:$JAMELEON_HOME/lib/junit.jar:$LIBS/ant-launcher.jar:$JAVA_HOME/lib/tools.jar
        export PATH=$PATH:lib
    fi
    "$JAVA_HOME/bin/java" -Xmx126m -Xms32m -classpath $CP org.apache.tools.ant.launch.Launcher -Dbuild.compiler=$COMPILER $*
fi

