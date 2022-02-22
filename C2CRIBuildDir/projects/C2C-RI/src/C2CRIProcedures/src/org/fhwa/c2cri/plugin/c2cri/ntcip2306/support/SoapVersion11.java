/*
 *  soapUI, copyright (C) 2004-2010 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;



/**
 * SoapVersion for SOAP 1.1
 * 
 * @author ole.matzura
 */

public class SoapVersion11 extends AbstractSoapVersion
{
	private final static QName envelopeQName = new QName( Constants.SOAP11_ENVELOPE_NS, "Envelope" );
	private final static QName bodyQName = new QName( Constants.SOAP11_ENVELOPE_NS, "Body" );
	private final static QName faultQName = new QName( Constants.SOAP11_ENVELOPE_NS, "Fault" );
	private final static QName headerQName = new QName( Constants.SOAP11_ENVELOPE_NS, "Header" );

	SchemaTypeLoader soapSchema;
	SchemaType soapEnvelopeType;
	private XmlObject soapSchemaXml;
	private XmlObject soapEncodingXml;
	private SchemaType soapFaultType;

	public final static SoapVersion11 instance = new SoapVersion11();

	private SoapVersion11()
	{

		try
		{
			XmlOptions options = new XmlOptions();
			options.setCompileNoValidation();
			options.setCompileNoPvrRule();
			options.setCompileDownloadUrls();
			options.setCompileNoUpaRule();
			options.setValidateTreatLaxAsSkip();
			soapSchemaXml = XmlObject.Factory.parse(SoapVersion11.class.getResource("/org/fhwa/c2cri/plugin/c2cri/ntcip2306/support/verification/v01_69/Soap-Envelope.xsd"), options );
			soapSchema = XmlBeans.loadXsd( new XmlObject[] { soapSchemaXml } );

			soapEnvelopeType = soapSchema.findDocumentType( envelopeQName );
			soapFaultType = soapSchema.findDocumentType( faultQName );

			soapEncodingXml = XmlObject.Factory.parse(SoapVersion11.class.getResource("/org/fhwa/c2cri/plugin/c2cri/ntcip2306/support/verification/v01_69/soapEncoding.xsd"), options );
		}
		catch( Exception e )
		{
//			SoapUI.logError( e );
		}
		finally
		{
//			Thread.currentThread().setContextClassLoader( contextClassLoader );
		}
	}

	public SchemaType getEnvelopeType()
	{
//		return EnvelopeDocument.type;
                return soapEnvelopeType;
	}

	public String getEnvelopeNamespace()
	{
		return Constants.SOAP11_ENVELOPE_NS;
	}

	public String getEncodingNamespace()
	{
		return Constants.SOAP_ENCODING_NS;
	}

	public XmlObject getSoapEncodingSchema() throws XmlException, IOException
	{
		return soapEncodingXml;
	}

	public XmlObject getSoapEnvelopeSchema() throws XmlException, IOException
	{
		return soapSchemaXml;
	}

	public String toString()
	{
		return "SOAP 1.1";
	}

	public String getContentTypeHttpHeader( String encoding, String soapAction )
	{
		if( encoding == null || encoding.trim().length() == 0 )
			return getContentType();
		else
			return getContentType() + ";charset=" + encoding;
	}

	public String getSoapActionHeader( String soapAction )
	{
		if( soapAction == null || soapAction.length() == 0 )
		{
			soapAction = "\"\"";
		}
		else
		{
			soapAction = "\"" + soapAction + "\"";
		}

		return soapAction;
	}

	public String getContentType()
	{
		return "text/xml";
	}

	public QName getBodyQName()
	{
		return bodyQName;
	}

	public QName getEnvelopeQName()
	{
		return envelopeQName;
	}

	public QName getHeaderQName()
	{
		return headerQName;
	}

	public SchemaTypeLoader getSoapEnvelopeSchemaLoader()
	{
		return soapSchema;
	}

	public SchemaType getFaultType()
	{
		return soapFaultType;
	}

	public String getName()
	{
		return "SOAP 1.1";
	}

	public String getFaultDetailNamespace()
	{
		return "";
	}
}
