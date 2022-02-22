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
<testcase xmlns="jelly:jameleon" name="MapVariableFromVariableToVariableCsv">
  <test-case-summary>
      Tests that the when a map-variable tag is embeded in a csv tag and type is set to list,
      that a list is created with the same number of values in it as in the CSV file.</test-case-summary>
  <test-case-author>Christian Hargraves</test-case-author>
  <test-case-level>REGRESSION</test-case-level>
  <test-case-bug>905961</test-case-bug>
  <functional-point-tested>map-variable</functional-point-tested>
  <application-tested>framework</application-tested>
    <junit-session application="junit">
        <csv name="MapVariable5Rows">
            <map-variable toVariable="listOfValues" fromVariable="singleValue" variableType="list"/>
        </csv>
        <test-list-size functionId="Test size of list after reading in values from csv file"
            expectedSize="5"/>
    </junit-session>
</testcase>
