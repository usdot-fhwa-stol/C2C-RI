package net.sf.jameleon.taglet;

import java.util.Map;

import com.sun.javadoc.Tag;

public class JameleonAttributeTaglet extends AbstractJameleonTaglet {

    public JameleonAttributeTaglet(){
        name = "jameleon.attribute";
        inField = true;
        inMethod = true;
        inOverview = true;
        inPackage = true;
        inType = true;
    }

    public static void register(Map tagletMap){
        JameleonAttributeTaglet tag = new JameleonAttributeTaglet();
        doRegister(tagletMap,tag);
    }

    public String toString(Tag tag){
        StringBuffer buff = new StringBuffer();
        String required = getValueFromAttribute(tag.text(), "required");
        String contextName = getValueFromAttribute(tag.text(), "contextName");
        String defaultValue = getValueFromAttribute(tag.text(), "default");
        buff.append("<b>This attribute is recognized by Jameleon</b><br/>");
        if (required != null) {
            buff.append("<b>Required: </b> <i>").append(required).append("</i><br/>\n");
        }
        if (defaultValue != null) {
            buff.append("<b>Default Value: </b> <i>").append(defaultValue).append("</i><br/>\n");
        }
        if (contextName != null) {
            buff.append("<b>Context Name: </b> <i>").append(contextName).append("</i><br/>\n");
        }
        return buff.toString();

    }

    public String toString(Tag[] tags){
        String str = null;
        if (tags.length > 0) {
            str = toString(tags[0]);
        }
        return str;
    }

}

