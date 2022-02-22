package net.sf.jameleon.taglet;

import java.util.Map;

import com.sun.javadoc.Tag;

public class JameleonStepTaglet extends AbstractJameleonTaglet {

    public JameleonStepTaglet(){
        name = "jameleon.step";
        inType = true;
    }

    public static void register(Map tagletMap){
        JameleonStepTaglet tag = new JameleonStepTaglet();
        doRegister(tagletMap,tag);
    }

    public String toString(Tag tag){
        StringBuffer buff = new StringBuffer("<b>Steps:</b><ol>\n");
        
        buff.append("<li>").append(tag.text()).append("</li>\n");
        buff.append("</ol>\n");
        return buff.toString();

    }

    public String toString(Tag[] tags){
        StringBuffer buff = new StringBuffer();
        if (tags.length >  0) {
            buff = new StringBuffer("<b>Steps:</b><ol>\n");
            for (int i = 0; i < tags.length; i++) {
                buff.append("<li>").append(tags[i].text()).append("</li>\n");
            }
            buff.append("</ol>\n");
        }
        return buff.toString();
    }

}

