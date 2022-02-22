/*
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
*/
package net.sf.jameleon;

import java.util.Iterator;
import java.util.Map;

import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.exception.JameleonTagException;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.SupportedTags;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.xml.sax.Attributes;

/**
 * Registers the tag libraries based on what is set up in SupportedTags.
 * SupportedTags by default reads its configuration from jameleon.conf' plugins
 * directive.
 * Even if no plug-ins are configured, then the following files should be read in:
 * <ol>
 *  <li>jameleon-core.properties</li>
 *  <li>TestCaseTagDefs.properties</li>
 * </ol>
 * The format of those files is:<br>
 * tagname=package.and.classname.of.function.point
 */
public class TestCaseTagLibrary extends TagLibrary {

    protected static SupportedTags st = new SupportedTags();
    protected static boolean warnOnNoPluginsFound;

    public TestCaseTagLibrary() {
        warnOnNoPluginsFound = Boolean.valueOf(Configurator.getInstance().getValue("warnOnNoPluginsFound", "true")).booleanValue();
        st.setWarnOnNoPluginsFile(warnOnNoPluginsFound);
        registerTags(st.getSupportedTags());
    }

    /**
     * Loops through all keys in a file named by the <code>name</code parameter.
     * @param tags - a tag name - class name map of tags to register
     */
    protected void registerTags(Map tags){
        Iterator keys = tags.keySet().iterator();
        String key = null;
        while (keys.hasNext()) {
            key = (String)keys.next();
            String className = (String)tags.get(key);
            registerTag(key,getClassLoader(className));
        }
    }


    private Class getClassLoader(String className){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class clzz = null;
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        } else {
            if (findClass(className,classLoader) == null) {
                classLoader = this.getClass().getClassLoader();
            }
        }
        clzz = findClass(className, classLoader);
        if (clzz == null) {
            throw new JameleonException("Couldn't find "+className);
        }
        return clzz;
    }

    private Class findClass(String className, ClassLoader cl){
        Class clzz = null;
        try{
           clzz = cl.loadClass(className);
        }catch(ClassNotFoundException cnfe){
            //
        }
        return clzz;
    }

    public static void resetTags(){
        st = new SupportedTags();
        st.setWarnOnNoPluginsFile(warnOnNoPluginsFound);
    }

    public static void setWarnOnNoPluginsFound(boolean warnOnNoPlugins){
       warnOnNoPluginsFound = warnOnNoPlugins;
       if (st != null) {
           st.setWarnOnNoPluginsFile(warnOnNoPlugins);
       }
    }

    //TagLibrary Methods

    /** Creates a new Tag for the given tag name and attributes */
    public Tag createTag(String name, Attributes attributes)
        throws JellyException {
        Tag tag = super.createTag(name, attributes);
        if (tag == null) {
            throw new JameleonTagException(name, "is not a recognized Jameleon tag. Please check the spelling and try again.");
        }
        return tag;
    }

    /** Creates a new script to execute the given tag name and attributes */
    //uncommenting this will cause a parse failure before the script
    //is even executed if an unknown tag is encountered.
/*
    public TagScript createTagScript(String name, Attributes attributes)
        throws JellyException {

        TagScript tagScript = super.createTagScript(name, attributes);
        if (tagScript == null) {
            throw new JameleonTagException(name, "is not a recognized Jameleon tag. Please check the spelling and try again.");
        }
        return tagScript;
    }
*/
}
