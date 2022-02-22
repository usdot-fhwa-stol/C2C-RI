package net.sf.jameleon.taglet;

import java.util.Map;

import com.sun.javadoc.Tag;

public class JameleonFunctionTaglet extends AbstractJameleonTaglet {

    public JameleonFunctionTaglet(){
        name = "jameleon.function";
        inType = true;
    }

    public static void register(Map tagletMap){
        JameleonFunctionTaglet tag = new JameleonFunctionTaglet();
        doRegister(tagletMap,tag);
    }

    public String toString(Tag tag){
        StringBuffer buff = new StringBuffer("<b>Tag Name</b>: ");
        String tagName = getValueFromAttribute(tag.text(), "name");
        buff.append("<i>").append("&lt;");
        buff.append(tagName).append("&gt;").append("</i>");
        String tagType = getValueFromAttribute(tag.text(), "type");
        if (tagType != null) {
            buff.append(" <b>Type</b>: ");
            buff.append("<i>").append(tagType).append("</i>");
        }
        buff.append("<br/>\n");
        return buff.toString();

    }

    public String toString(Tag[] tags){
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < tags.length; i++) {
            buff.append(toString(tags[i]));
        }
        return buff.toString();
    }

}

