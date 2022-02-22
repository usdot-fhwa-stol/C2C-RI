/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005 Christian W. Hargraves (engrean@hotmail.com)
    
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

package net.sf.jameleon;

import org.apache.commons.jelly.LocationAware;
import org.apache.commons.jelly.TagSupport;

/** 
 * An abstract tag that is location aware. 
 * In other words, it is a tag that is capable of recording where in a Jelly script a tag 
 * is used which can aid debugging and tracing.</p>
 */
public abstract class LocationAwareTagSupport extends TagSupport implements LocationAware {

    protected int lineNumber = -1;
    protected int columnNumber = -1;
    protected String scriptFileName;
    protected String elementTagName;

    /** 
     * @return the line number of the tag 
     */
    public int getLineNumber(){
        return lineNumber;
    }
    
    /** 
     * Sets the line number of the tag 
     */
    public void setLineNumber(int lineNumber){
        this.lineNumber = lineNumber;
    }

    /** 
     * @return the column number of the tag 
     */
    public int getColumnNumber(){
        return columnNumber;
    }
    
    /** 
     * Sets the column number of the tag 
     */
    public void setColumnNumber(int columnNumber){
        this.columnNumber = columnNumber;
    }

    /** 
     * @return the Jelly file which caused the problem 
     */
    public String getFileName(){
        return scriptFileName;
    }
    
    /** 
     * Sets the Jelly file which caused the problem 
     */
    public void setFileName(String fileName){
        this.scriptFileName = fileName;
    }
    
    /** 
     * @return the element name which caused the problem
     */
    public String getElementName(){
        return elementTagName;
    }

    /** 
     * Sets the element name which caused the problem
     */
    public void setElementName(String elementName){
        this.elementTagName = elementName;
    }
}
