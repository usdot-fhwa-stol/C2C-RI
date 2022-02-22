/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
*/
package net.sf.jameleon.plugin.junit;

import java.io.File;
import java.util.List;

import net.sf.jameleon.result.FunctionResult;

/**
 * @jameleon.function name="validate-results-file" type="action"
 */

public class ValidateResultsFileTag extends JUnitFunctionTag {

	/**
	 * @jameleon.attribute required="true"
	 */
	private File expectedFile;
	
	public void testBlock(){
		List results = getFunctionResults().getParentResults().getChildrenResults();
		//Get the results from the previous tag
		FunctionResult result = (FunctionResult)results.get(results.size()-2);
		assertTrue("The file was not saved", result.getErrorFile().exists());
		assertEquals("file path", expectedFile.getAbsolutePath(), result.getErrorFile().getAbsolutePath());
	}
}