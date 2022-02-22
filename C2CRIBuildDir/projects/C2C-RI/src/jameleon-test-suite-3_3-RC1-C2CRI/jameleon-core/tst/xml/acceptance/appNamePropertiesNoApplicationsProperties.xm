<?xml version="1.0"?>
<!--
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
-->
<testcase xmlns="jelly:jameleon">

    <test-case-summary>Tests that a file matching the application name followed by '.properties' is loadded if the file exists</test-case-summary>
    <test-case-author>Christian Hargraves</test-case-author>
    <test-case-level>ACCEPTANCE</test-case-level>
    <functional-point-tested>properties</functional-point-tested>

    <junit-session application="appName">
            <junit-simple-compare-2-vars 
                functionId="compare two variables w/o organization"
                var1="${var1}"
                var2="${var2}"/>
    </junit-session>

    <junit-session application="appName" organization="sf">
            <junit-simple-compare-2-vars 
                functionId="compare two variables with organization"
                var1="${var3}"
                var2="${var4}"/>
    </junit-session>

</testcase>
