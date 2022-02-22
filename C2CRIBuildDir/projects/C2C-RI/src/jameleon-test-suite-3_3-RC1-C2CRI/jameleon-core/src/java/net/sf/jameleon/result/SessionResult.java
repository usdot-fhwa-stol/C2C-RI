/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.result;

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;

/**
 * An implementation of @see TestResult that represents the results of a session
 * Since a session contains function points, <code>SessionResult</code> is the results
 * of all of the function points in the single session
 */
public class SessionResult extends TestResultWithChildren {
	private static final long serialVersionUID = 1L;

    /**
     * Default Constructor.
     */
    public SessionResult(FunctionalPoint tag){
        super(tag);
    }

    /**
     * Default Constructor.
     */
    public SessionResult(FunctionalPoint tag, HasChildResults parentResults){
        super(tag, parentResults);
    }

    public boolean isDataDriven() {
        return false;
    }

    /**
     * @return a XML String representation of the results
     */
    public String toXML() {
        StringBuffer str = new StringBuffer("\n");
        str.append("\t<session-result>\n");
        Attribute application = tag.getAttribute("application");
        if (application != null && application.getValue() instanceof String) {
            str.append("\t\t<application>").append(escapeXML(application.getValue().toString())).append("</application>\n");
        }
        str.append(super.toXML());
        str.append("\t</session-result>\n");
        return str.toString();
    }

    /**
     * @return A String representation of the results
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        Attribute application = tag.getAttribute("application");
        if (application != null && application.getValue() instanceof String) {
            str.append("Application: ").append(escapeXML(application.getValue().toString())).append("\n");
        }
        str.append(super.toString());
        return str.toString();
    }

}