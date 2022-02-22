package net.sf.jameleon.taglet;

import com.sun.source.doctree.DocTree;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;

import jdk.javadoc.doclet.Taglet;

public abstract class AbstractJameleonTaglet implements Taglet{

    protected String name;

    protected boolean inField;
    protected boolean inConstructor;
    protected boolean inMethod;
    protected boolean inOverview;
    protected boolean inPackage;
    protected boolean inType;
    protected boolean isInlineTag;
    protected DocletEnvironment env;
    protected Doclet doclet;
    
    public String getName(){
        return name;
    }

    public boolean inField(){
        return inField;
    }

    public boolean inConstructor(){
        return inConstructor;
    }

    public boolean inMethod(){
        return inMethod;
    }

    public boolean inOverview(){
        return inOverview;
    }

    public boolean inPackage(){
        return inPackage;
    }

    public boolean inType(){
        return inType;
    }

    public boolean isInlineTag(){
        return isInlineTag;
    }

    protected static void doRegister(Map tagletMap, Taglet tag){
        Taglet t = (Taglet) tagletMap.get(tag.getName());
        if (t != null) {
            tagletMap.remove(t.getName());
        }
        tagletMap.put(tag.getName(), tag);
    }

    protected String getValueFromAttribute(String tagText, String attrName){
        String tagName = null;
        final String TAG_NAME_ATTR = attrName+"=\"";
        int index = tagText.indexOf(TAG_NAME_ATTR);
        int start = index+TAG_NAME_ATTR.length();
        int end = tagText.indexOf("\"",index+TAG_NAME_ATTR.length());
        if (index > -1) {
            tagName = tagText.substring(start, end);
        }
        return tagName;
    }
        @Override
    public Set<Location> getAllowedLocations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(DocletEnvironment env, Doclet doclet) {
        this.env = env;
        this.doclet = doclet;
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

