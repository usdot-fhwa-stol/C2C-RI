package net.sf.jameleon.taglet;

import com.sun.javadoc.Tag;

import java.util.Map;

public class MockJameleonTaglet extends AbstractJameleonTaglet{

    public static void register(Map tagletMap){
        MockJameleonTaglet tag = new MockJameleonTaglet();
        doRegister(tagletMap,tag);
    }

    public String toString(Tag tag){
        return new String();
    }

    public String toString(Tag[] tags){
        return new String();
    }

}
