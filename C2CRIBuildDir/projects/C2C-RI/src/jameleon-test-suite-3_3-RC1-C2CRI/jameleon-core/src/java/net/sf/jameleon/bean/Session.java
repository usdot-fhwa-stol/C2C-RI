/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.bean;
import net.sf.jameleon.XMLable;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
/**
 * This class represents a Session in a TestCase. This is currently used only for test case
 * documentation generation
 * A Session consists of:
 * <ul>
 *  <li>application - The application this session is opened for. REQUIRED.</li>
 *  <li>organization - The organization this session will test for if any - OPTIONAL.</li> 
 *  <li>functional points - A list of <code>FunctionalPoint</code>s executed in this Session - REQUIRED</li>
 * </ul>
 */
public class Session implements XMLable{
    private static final long serialVersionUID = 1L;
   /**
     * The application this session is opened for
     */
    protected String application;
    /**
     * The organization this session will test for if any
     */
    protected String organization;
    /**
     * A list of <code>FunctionalPoint</code>s
     */
    protected List functionalPoints;
    /**
     * Default constructor only used to initialize variables
     */
    public Session() {
        functionalPoints = new LinkedList();
    }
    /**
     * Constructor to set the following parameters
     * @param application - The application this session is opened for
     * @param organization - The organization this session will test for if any
     */
    public Session(String application, String organization) {
        this();
        this.application = application;
        this.organization = organization;
    }
    /**
     * @return a list of <code>FunctionalPoint</code>s contained in this Session
     */
    public List getFunctionalPoints(){
        return this.functionalPoints;
    }
    /**
     * Adds a <code>FunctionalPoint</code> to the list of <code>FunctionalPoint</code>s under this Session
     * @param fp - A <code>FunctionalPoint</code> under this Session
     */
    public void addFunctionalPoint(FunctionalPoint fp){
        functionalPoints.add(fp);
    }
    /**
     * @return The application this session is opened for
     */
    public String getApplication(){
        return this.application;
    }
    /**
     * Sets the application this session is opened for
     * @param application - the application this session is opened for
     */
    public void setApplication(String application){
        this.application = application;
    }
    /**
     * @return The organization this session will test for if any
     */
    public String getOrganization(){
        return this.organization;
    }
    /**
     * Sets the organization this session will test for if any
     * @param organization - the organization this session will test for if any
     */
    public void setOrganization(String organization){
        this.organization = organization;
    }
    public String toXML(){
        StringBuffer str = new StringBuffer();
        str.append("\t<session>\n");
        if (application != null && application.length() > 0) {
            str.append("\t\t<application>").append(application).append("</application>\n");
        }
        if (organization != null && organization.length() > 0) {
            str.append("\t\t<organization>").append(organization).append("</organization>\n");
        }
        Iterator it = functionalPoints.iterator();
        while (it.hasNext()) {
            str.append(((XMLable)it.next()).toXML());
        }
        str.append("\t</session>\n");
        return str.toString();
    }
}