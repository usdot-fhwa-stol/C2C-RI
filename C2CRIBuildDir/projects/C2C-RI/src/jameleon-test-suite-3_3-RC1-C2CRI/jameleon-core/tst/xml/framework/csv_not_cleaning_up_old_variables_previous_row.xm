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
<testcase xmlns="jelly:jameleon"
          name="SimpleFunction2Vars2Rows"
          useCSV="true">
  <test-case-summary>
      Tests that old variable values aren't pulled from the previous row
      if there is no value for that particular column
  </test-case-summary>
  <test-case-author>Christian Hargraves</test-case-author>
  <test-case-level>ACCEPTANCE</test-case-level>
  <functional-point-tested>csv</functional-point-tested>
  <application-tested>framework</application-tested>
  <test-case-bug>1105543</test-case-bug>

  <ju-session>
      <junit-simple-compare-2-vars 
          functionId="Compare two values. The first time should pass."
          var1="${simpleFunctionVar1}" 
          var2="${simpleFunctionVar2}"/>
  </ju-session>
</testcase>
