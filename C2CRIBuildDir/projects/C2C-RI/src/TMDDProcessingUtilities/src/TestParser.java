
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestParser {
    private static Pattern PDefRegExp; //Parameter name and value

    public static void main(String args[]) throws Exception {

//        PDefRegExp = Pattern.compile("^ *([^#\n\r]*\\S) *= *(.*)", Pattern.MULTILINE);
        PDefRegExp = Pattern.compile("^ *([^#\n\r=]*\\S) *= *(.*)", Pattern.MULTILINE);
        String line1 = "#PARAMETER TYPE = String\n"+
"#EDITABLE = true\n"+
"#DOCUMENTATION = Enter the XML message to be sent as a request during this test. \n"+
"ResponseMessage = <soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><mes:dMSInventoryMsg xmlns:mes=\"http://www.tmdd.org/3/messages\"><dms-inventory-item><restrictions><organization-information-forwarding-restrictions>4</organization-information-forwarding-restrictions></restrictions><device-inventory-header><organization-information><organization-id>string</organization-id><organization-name>string</organization-name><organization-location>stringstri</organization-location><organization-function>string</organization-function><organization-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></organization-contact-details><center-contact-list><center-contact-details><center-id>string</center-id><center-name>string</center-name><center-location><latitude>3</latitude><longitude>3</longitude><horizontalDatum>3</horizontalDatum><height><altitude><kmDec>1000.00</kmDec></altitude><verticalDatum>1</verticalDatum></height></center-location><center-description>string</center-description><center-type>fixed</center-type><center-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></center-contact-details></center-contact-details></center-contact-list><last-update-time><date>stringst</date><time>string</time><offset>strin</offset></last-update-time></organization-information><device-id>string</device-id><device-location><latitude>3</latitude><longitude>3</longitude><horizontalDatum>nad27</horizontalDatum><height><verticalLevel>2</verticalLevel></height></device-location><device-name>string</device-name><device-description>string</device-description><device-control-type>status only</device-control-type><controller-description>string</controller-description><network-id>string</network-id><node-id>string</node-id><node-name>string</node-name><link-id>string</link-id><link-name>string</link-name><link-direction>s</link-direction><linear-reference>string</linear-reference><linear-reference-version>6</linear-reference-version><route-designator>string</route-designator><device-url>string</device-url><last-update-time><date>stringst</date><time>string</time><offset>strin</offset></last-update-time></device-inventory-header><dms-sign-type>5</dms-sign-type><signTechnology>2</signTechnology><signHeightPixels>2</signHeightPixels><signWidthPixels>2</signWidthPixels><signHeight>2</signHeight><signWidth>2</signWidth><charHeightPixels>2</charHeightPixels><charWidthPixels>2</charWidthPixels><dms-beacon-type>2</dms-beacon-type><dms-vertical-border>2</dms-vertical-border><dms-horizontal-border>2</dms-horizontal-border><dms-vertical-pixel-pitch>2</dms-vertical-pixel-pitch><dms-horizontal-pixel-pitch>2</dms-horizontal-pixel-pitch><dms-max-pages>2</dms-max-pages><dms-max-message-length>2</dms-max-message-length><dms-color-scheme>2</dms-color-scheme><dms-multi-tag-support>2</dms-multi-tag-support></dms-inventory-item></mes:dMSInventoryMsg></soapenv:Body></soapenv:Envelope>\n";

        Matcher matcher = PDefRegExp.matcher(line1);

        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        } else {
            System.out.println("No Matches Found!");
        }

        System.out.println();
        String line2 =
"#PARAMETER TYPE = String\n" +
"#MINIMUM = 2000\n" +
"#MAXIMUM = 4000\n" +
"#EDITABLE = true\n" +
"#DOCUMENTATION = The Organization associated with this C2C RI Installation\n" +
"ResponseMessage = Message 2 Agency X = String\n";

        matcher = PDefRegExp.matcher(line2);

        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        } else {
            System.out.println("No Matches Found!");
        }

    }
}
