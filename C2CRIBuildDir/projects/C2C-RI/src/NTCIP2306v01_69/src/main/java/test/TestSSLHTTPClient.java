/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.fhwa.c2cri.ntcip2306v109.http.NTCIP2306ClientConnectionManger;
import org.fhwa.c2cri.ntcip2306v109.http.NTCIP2306SSLSocketFactory;

/**
 * The Class TestSSLHTTPClient.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestSSLHTTPClient {

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params,
                HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpConnectionParams.setTcpNoDelay(params,
                true);
        HttpConnectionParams.setSocketBufferSize(params,
                8192);
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);
        HttpProtocolParams.setUseExpectContinue(params,
                true);

// load the keystore containing the client certificate - keystore type is probably jks or pkcs12
        final KeyStore keystore = KeyStore.getInstance("jks");
        InputStream keystoreInput = null;
        File keystoreFile = new File("c:\\c2cri\\keystore\\keystore");
        keystoreInput = new FileInputStream(keystoreFile);

        keystore.load(keystoreInput, "c2cri1".toCharArray());

// load the trustore, leave it null to rely on cacerts distributed with the JVM - truststore type is probably jks or pkcs12
        KeyStore truststore = KeyStore.getInstance("jks");
        InputStream truststoreInput = null;
        truststoreInput = new FileInputStream(keystoreFile);
        truststore.load(truststoreInput, "c2cri1".toCharArray());

        final SchemeRegistry schemeRegistry = new SchemeRegistry();

        NTCIP2306SSLSocketFactory sslsf = new NTCIP2306SSLSocketFactory(keystore, "c2cri1", truststore);
        sslsf.setSecureMode(true);
        schemeRegistry.register(new Scheme("https", sslsf, 443));
        DefaultHttpClient httpClient = new DefaultHttpClient(new NTCIP2306ClientConnectionManger(params, schemeRegistry), params);

        HttpPost httppost = new HttpPost("https://test.its.gov:443/tmddws/TmddWS.svc/OC");

        String msg = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\"><wsa:Action>http://www.tmdd.org/301/dialogs/ItmddOCSoapHttpServicePortType/dlCenterActiveVerificationRequest</wsa:Action><wsa:To>https://testcolondexsrv.its.nv.gov/tmddws/TmddWS.svc/OC</wsa:To></SOAP-ENV:Header><SOAP-ENV:Body><mes:centerActiveVerificationRequestMsg xmlns:mes=\"http://www.tmdd.org/301/messages\" xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"mes:CenterActiveVerificationRequest\">\n"
                + "         <authentication xsi:type=\"mes:Authentication\">\n"
                + "            <user-id>string</user-id>\n"
                + "            <password>string</password>\n"
                + "            <!--Optional:-->\n"
                + "            <operator-id>string</operator-id>\n"
                + "            <!--You may enter ANY elements at this point-->\n"
                + "         </authentication>\n"
                + "         <organization-requesting xsi:type=\"mes:OrganizationInformation\">\n"
                + "            <organization-id>transcore.com</organization-id>\n"
                + "            <!--Optional:-->\n"
                + "            <organization-name>string</organization-name>\n"
                + "            <!--Optional:-->\n"
                + "            <organization-location>stringstri</organization-location>\n"
                + "            <!--Optional:-->\n"
                + "            <organization-function>string</organization-function>\n"
                + "            <!--Optional:-->\n"
                + "            <organization-contact-details xsi:type=\"mes:ContactDetails\">\n"
                + "               <contact-id>string</contact-id>\n"
                + "               <!--Optional:-->\n"
                + "               <person-name>string</person-name>\n"
                + "               <!--Optional:-->\n"
                + "               <person-title>string</person-title>\n"
                + "               <!--Optional:-->\n"
                + "               <phone-number>string</phone-number>\n"
                + "               <!--Optional:-->\n"
                + "               <phone-alternate>string</phone-alternate>\n"
                + "               <!--Optional:-->\n"
                + "               <mobile-phone-number>string</mobile-phone-number>\n"
                + "               <!--Optional:-->\n"
                + "               <mobile-phone-id>string</mobile-phone-id>\n"
                + "               <!--Optional:-->\n"
                + "               <fax-number>string</fax-number>\n"
                + "               <!--Optional:-->\n"
                + "               <pager-number>string</pager-number>\n"
                + "               <!--Optional:-->\n"
                + "               <pager-id>string</pager-id>\n"
                + "               <!--Optional:-->\n"
                + "               <email-address>string</email-address>\n"
                + "               <!--Optional:-->\n"
                + "               <radio-unit>string</radio-unit>\n"
                + "               <!--Optional:-->\n"
                + "               <address-line1>string</address-line1>\n"
                + "               <!--Optional:-->\n"
                + "               <address-line2>string</address-line2>\n"
                + "               <!--Optional:-->\n"
                + "               <city>string</city>\n"
                + "               <!--Optional:-->\n"
                + "               <state>st</state>\n"
                + "               <!--Optional:-->\n"
                + "               <zip-code>string</zip-code>\n"
                + "               <!--Optional:-->\n"
                + "               <country>string</country>\n"
                + "               <!--You may enter ANY elements at this point-->\n"
                + "            </organization-contact-details>\n"
                + "            <!--Optional:-->\n"
                + "            <center-contact-list>\n"
                + "               <center-contact-details xsi:type=\"mes:OrganizationCenterInformation\">\n"
                + "                  <center-id>tcore_test</center-id>\n"
                + "                  <!--Optional:-->\n"
                + "                  <center-name>string</center-name>\n"
                + "                  <!--Optional:-->\n"
                + "                  <center-location xmlns:lrms=\"http://www.LRMS-Adopted-02-00-00\" xsi:type=\"lrms:GeoLocation\">\n"
                + "                     <latitude>33964380</latitude>\n"
                + "                     <longitude>-84217945</longitude>\n"
                + "                     <!--Optional:-->\n"
                + "                     <horizontalDatum>wgs-84</horizontalDatum>\n"
                + "                     <!--Optional:-->\n"
                + "                     <height xsi:type=\"lrms:Height\">\n"
                + "                        <!--You have a CHOICE of the next 2 items at this level.  Item 0 was chosen.-->\n"
                + "                        <altitude xsi:type=\"lrms:Distance\">\n"
                + "                           <!--You have a CHOICE of the next 18 items at this level.  Item 15 was chosen.-->\n"
                + "                           <kmDec>1000.00</kmDec>\n"
                + "                        </altitude>\n"
                + "                        <!--Optional:-->\n"
                + "                        <verticalDatum>1</verticalDatum>\n"
                + "                     </height>\n"
                + "                  </center-location>\n"
                + "                  <!--Optional:-->\n"
                + "                  <center-description>string</center-description>\n"
                + "                  <!--Optional:-->\n"
                + "                  <center-type>fixed</center-type>\n"
                + "                  <!--Optional:-->\n"
                + "                  <center-contact-details xsi:type=\"mes:ContactDetails\">\n"
                + "                     <contact-id>string</contact-id>\n"
                + "                     <!--Optional:-->\n"
                + "                     <person-name>string</person-name>\n"
                + "                     <!--Optional:-->\n"
                + "                     <person-title>string</person-title>\n"
                + "                     <!--Optional:-->\n"
                + "                     <phone-number>string</phone-number>\n"
                + "                     <!--Optional:-->\n"
                + "                     <phone-alternate>string</phone-alternate>\n"
                + "                     <!--Optional:-->\n"
                + "                     <mobile-phone-number>string</mobile-phone-number>\n"
                + "                     <!--Optional:-->\n"
                + "                     <mobile-phone-id>string</mobile-phone-id>\n"
                + "                     <!--Optional:-->\n"
                + "                     <fax-number>string</fax-number>\n"
                + "                     <!--Optional:-->\n"
                + "                     <pager-number>string</pager-number>\n"
                + "                     <!--Optional:-->\n"
                + "                     <pager-id>string</pager-id>\n"
                + "                     <!--Optional:-->\n"
                + "                     <email-address>string</email-address>\n"
                + "                     <!--Optional:-->\n"
                + "                     <radio-unit>string</radio-unit>\n"
                + "                     <!--Optional:-->\n"
                + "                     <address-line1>string</address-line1>\n"
                + "                     <!--Optional:-->\n"
                + "                     <address-line2>string</address-line2>\n"
                + "                     <!--Optional:-->\n"
                + "                     <city>string</city>\n"
                + "                     <!--Optional:-->\n"
                + "                     <state>st</state>\n"
                + "                     <!--Optional:-->\n"
                + "                     <zip-code>string</zip-code>\n"
                + "                     <!--Optional:-->\n"
                + "                     <country>string</country>\n"
                + "                     <!--You may enter ANY elements at this point-->\n"
                + "                  </center-contact-details>\n"
                + "                  <!--You may enter ANY elements at this point-->\n"
                + "               </center-contact-details>\n"
                + "            </center-contact-list>\n"
                + "            <!--Optional:-->\n"
                + "            <last-update-time xsi:type=\"mes:DateTimeZone\">\n"
                + "               <date>20130531</date>\n"
                + "               <time>105805</time>\n"
                + "               <!--Optional:-->\n"
                + "               <offset>01:00</offset>\n"
                + "               <!--You may enter ANY elements at this point-->\n"
                + "            </last-update-time>\n"
                + "            <!--You may enter ANY elements at this point-->\n"
                + "         </organization-requesting>\n"
                + "         <!--You may enter ANY elements at this point-->\n"
                + "      </mes:centerActiveVerificationRequestMsg></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        try {
            MakeECTestCases ecTC = new MakeECTestCases();
            TestCaseSpec tc = ecTC.getTestCase(0);
            msg = new String(tc.getRequestMessage(), "UTF-8");
            String soapHeader = "<SOAP-ENV:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\"><wsa:Action>http://www.tmdd.org/301/dialogs/ItmddOCSoapHttpServicePortType/" + tc.getOperationName() + "</wsa:Action><wsa:To>https://testcolondexsrv.its.nv.gov/tmddws/TmddWS.svc/OC</wsa:To></SOAP-ENV:Header>";
            httppost.addHeader("SOAPAction", "http://www.tmdd.org/301/dialogs/ItmddOCSoapHttpServicePortType/" + tc.getOperationName());

            ByteArrayEntity baEnt = new ByteArrayEntity(msg.getBytes("UTF-8"));
            baEnt.setContentType("text/xml; charset=utf-8");
            httppost.setHeader("SOAPAction", "http://www.tmdd.org/301/dialogs/ItmddOCSoapHttpServicePortType/" + tc.getOperationName());
            httppost.setEntity(baEnt);

            HttpResponse response = httpClient.execute(httppost);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }

    }
}
