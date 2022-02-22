/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.util;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.JameleonUtility;

public class GenSyntaxReference {

    protected SupportedTags supportedTags;
    protected Map tags;

    public GenSyntaxReference(){
        supportedTags = new SupportedTags();
    }

    public Map getTagsForPlugin(String plugin){
        Map supportedTagsMap = Collections.synchronizedMap(new TreeMap());
        tags = Collections.synchronizedMap(new TreeMap());
        supportedTags.setTagsFor(supportedTagsMap, plugin);
        Iterator it = supportedTagsMap.keySet().iterator();
        String className, key;
        FunctionalPoint fp;
        while (it.hasNext()) {
            key = (String)it.next();
            className = (String)supportedTagsMap.get(key);
            fp = JameleonUtility.loadFunctionalPoint(className, this);
            tags.put(fp.getDefaultTagName(), fp);
        }
        return tags;
    }

    public void genReferenceForPlugin(String plugin, String templateName, File toFile, Map templateParams){
        getTagsForPlugin(plugin);
        Map params = new HashMap();
        params.put("tags", tags);
        params.putAll(templateParams);
        TemplateProcessor tcdf = new TemplateProcessor(templateName);
        tcdf.transform(toFile,params);
    }
}
