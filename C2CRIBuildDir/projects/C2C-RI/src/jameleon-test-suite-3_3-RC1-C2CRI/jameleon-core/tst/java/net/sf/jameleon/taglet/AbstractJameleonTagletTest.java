package net.sf.jameleon.taglet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AbstractJameleonTagletTest extends TestCase {
    private MockJameleonTaglet taglet;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(AbstractJameleonTagletTest.class);
    }

    public AbstractJameleonTagletTest(String name) {
        super(name);
    }

    public void setUp(){
        taglet = new MockJameleonTaglet();
    }

    public void tearDown(){
        taglet = null;
    }

    public void testRegister(){
    }

    public void testGetValueFromAttribute(){
        String text = "name=\"someName\"";
        String name = "name";
        String value = taglet.getValueFromAttribute(text,name);
        assertEquals("Name attribute", "someName", value);
    }

}
