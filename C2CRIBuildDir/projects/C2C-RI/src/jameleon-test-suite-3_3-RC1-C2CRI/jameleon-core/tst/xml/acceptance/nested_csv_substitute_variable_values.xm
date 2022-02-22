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
    <test-case-summary>Tests that variable substitution occurs in nested csv tags.</test-case-summary>
    <test-case-author>Christian Hargraves</test-case-author>
    <test-case-level>ACCEPTANCE</test-case-level>
    <test-case-level>REGRESSION</test-case-level>
    <test-case-bug>1038088</test-case-bug>
    <functional-point-tested>csv</functional-point-tested>
    <application-tested>framework</application-tested>

    <junit-session application="sub">
        <csv name="nested_csv_substitute_variable_values">
            <junit-simple-compare-2-vars
                functionId="Check that variable substitution occurs inside the csv tag that is inside a session tag"
                var1="${variable1}"
                var2="${variable2}"/>
        </csv>
    </junit-session>

    <csv name="nested_csv_substitute_variable_values">
        <junit-session application="sub">
            <junit-simple-compare-2-vars
                functionId="Check that variable substitution occurs inside the csv tag"
                var1="${variable1}"
                var2="${variable2}"/>
        </junit-session>
    </csv>
</testcase>
