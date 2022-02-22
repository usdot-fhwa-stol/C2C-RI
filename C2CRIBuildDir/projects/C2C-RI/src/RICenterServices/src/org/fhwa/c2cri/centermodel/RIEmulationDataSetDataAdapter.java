/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.centermodel;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author TransCore ITS, LLC
 */
public class RIEmulationDataSetDataAdapter extends XmlAdapter<String, byte[]>{

    @Override
    public byte[] unmarshal(String arg0) throws Exception {
        return StringEscapeUtils.unescapeXml(arg0).getBytes();
    }

    @Override
    public String marshal(byte[] arg0) throws Exception {
        return StringEscapeUtils.escapeXml10(new String(arg0));
    }
    
}
