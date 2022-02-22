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

package org.fhwa.c2cri.testcases;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.wsdl.BindingFault;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.fhwa.c2cri.messagemanager.XmlGeneratorUtility;
import org.fhwa.c2cri.messagemanager.XmlGeneratorUtility;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.Constants;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.SoapVersion;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.WsdlUtils;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.WsdlUtils.SoapHeader;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.XmlUtils;
import tmddv3verification.utilities.TMDDWSDL;

/**
 * Builds SOAP requests according to WSDL/XSD definitions
 * 
 * @author Ole.Matzura
 */

public class OperationMessageBuilder
{

	private Map<QName, String[]> multiValues = null;
        private TMDDWSDL riWSDLContext;

	public OperationMessageBuilder(TMDDWSDL riWSDL)
	{
            this.riWSDLContext = riWSDL;
	}

	public void setMultiValues( Map<QName, String[]> multiValues )
	{
		this.multiValues = multiValues;
	}


	public static String buildFault( String faultcode, String faultstring )
	{
		XmlGeneratorUtility generator = new XmlGeneratorUtility( false );
//		generator.setTypeComment( false );
//		generator.setIgnoreOptional( true );

		String emptyResponse = buildEmptyFault(generator);

		emptyResponse = XmlUtils.setXPathContent( emptyResponse, "//faultcode", faultcode );
		emptyResponse = XmlUtils.setXPathContent( emptyResponse, "//faultstring", faultstring );

		return emptyResponse;
	}


	public static String buildEmptyFault( )
	{
		XmlGeneratorUtility generator = new XmlGeneratorUtility( false );

		String emptyResponse = buildEmptyFault( generator);

		return emptyResponse;
	}

	private static String buildEmptyFault( XmlGeneratorUtility generator)
	{
                SoapVersion soapVersion;
                soapVersion = SoapVersion.Soap11;
//                String emptyResponse = buildEmptyMessage();
                String emptyResponse = buildEmptyMessageForFault();
		try
		{
		XmlObject xmlObject = XmlObject.Factory.newInstance();
		XmlCursor cursor = xmlObject.newCursor();
//		cursor.toNextToken();
//			XmlObject xmlObject = XmlObject.Factory.parse( emptyResponse );
//			XmlCursor cursor = xmlObject.newCursor();

	//		if( cursor.toChild( soapVersion.getEnvelopeQName() ) && cursor.toChild( soapVersion.getBodyQName() ) )
	//		{
				SchemaType faultType = soapVersion.getFaultType();
				Node bodyNode = cursor.getDomNode();
				Document dom = XmlUtils.parseXml( generator.createSampleForType( faultType ) );
				bodyNode.appendChild( bodyNode.getOwnerDocument().importNode( dom.getDocumentElement(), true ) );
	//		}

			cursor.dispose();
			emptyResponse = xmlObject.toString();
		}
		catch( Exception e )
		{
		}
		return emptyResponse;
	}


	public static String buildEmptyMessage()
	{
		XmlGeneratorUtility generator = new XmlGeneratorUtility( false );
//		generator.setTypeComment( false );
//		generator.setIgnoreOptional( true );
                SoapVersion soapVersion;
                soapVersion = SoapVersion.Soap11;
		return generator.createSampleForType( soapVersion.getEnvelopeType() );
	}

	public static String buildEmptyMessageForFault( )
	{
		boolean inputSoapEncoded = true;
		XmlGeneratorUtility xmlGenerator = new XmlGeneratorUtility( inputSoapEncoded );

		XmlObject object = XmlObject.Factory.newInstance();
		XmlCursor cursor = object.newCursor();
		cursor.toNextToken();
                SoapVersion soapVersion = SoapVersion.Soap11;
		cursor.beginElement( soapVersion.getEnvelopeQName() );

		if( inputSoapEncoded )
		{
			cursor.insertNamespace( "xsi", Constants.XSI_NS );
			cursor.insertNamespace( "xsd", Constants.XSD_NS );
		}

		cursor.toFirstChild();

		cursor.beginElement( soapVersion.getBodyQName() );
		cursor.toFirstChild();


		cursor.dispose();

		try
		{
			StringWriter writer = new StringWriter();
			XmlUtils.serializePretty( object, writer );
			return writer.toString();
		}
		catch( Exception e )
		{
			return object.xmlText();
		}
	}

	public String buildMessageFromInput( BindingOperation bindingOperation, boolean buildOptional,
			boolean alwaysBuildHeaders ) throws Exception
	{
//		boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded( bindingOperation );
		boolean inputSoapEncoded = true;
		XmlGeneratorUtility xmlGenerator = new XmlGeneratorUtility( inputSoapEncoded );
//		xmlGenerator.setMultiValues( multiValues );
//		xmlGenerator.setIgnoreOptional( !buildOptional );

		XmlObject object = XmlObject.Factory.newInstance();
		XmlCursor cursor = object.newCursor();
		cursor.toNextToken();
                SoapVersion soapVersion = SoapVersion.Soap11;
//		cursor.beginElement( soapVersion.getEnvelopeQName() );

/**
                if( inputSoapEncoded )
		{
			cursor.insertNamespace( "xsi", Constants.XSI_NS );
			cursor.insertNamespace( "xsd", Constants.XSD_NS );
		}
*/
//		cursor.toFirstChild();

//		cursor.beginElement( soapVersion.getBodyQName() );
		cursor.toFirstChild();

		String combined = buildDocumentRequest( bindingOperation, cursor, xmlGenerator );
/**
		if( alwaysBuildHeaders )
		{
			BindingInput bindingInput = bindingOperation.getBindingInput();
			if( bindingInput != null )
			{
				List<?> extensibilityElements = bindingInput.getExtensibilityElements();
				List<SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders( extensibilityElements );
				addHeaders( soapHeaders, cursor, xmlGenerator );
			}
		}
 */
		cursor.dispose();

		try
		{
			StringWriter writer = new StringWriter();
			XmlUtils.serializePretty( object, writer );
			return writer.toString();
		}
		catch( Exception e )
		{
			return object.xmlText();
		}
	}

	public String buildSoapMessageFromInput( BindingOperation bindingOperation, boolean buildOptional ) throws Exception
	{
		return buildSoapMessageFromInput( bindingOperation, buildOptional, true );
	}

	public String buildSoapMessageFromInput( BindingOperation bindingOperation, boolean buildOptional,
			boolean alwaysBuildHeaders ) throws Exception
	{
//		boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded( bindingOperation );
		boolean inputSoapEncoded = true;
		XmlGeneratorUtility xmlGenerator = new XmlGeneratorUtility( inputSoapEncoded );
//		xmlGenerator.setMultiValues( multiValues );
//		xmlGenerator.setIgnoreOptional( !buildOptional );

		XmlObject object = XmlObject.Factory.newInstance();
		XmlCursor cursor = object.newCursor();
		cursor.toNextToken();
                SoapVersion soapVersion = SoapVersion.Soap11;
		cursor.beginElement( soapVersion.getEnvelopeQName() );

		if( inputSoapEncoded )
		{
			cursor.insertNamespace( "xsi", Constants.XSI_NS );
			cursor.insertNamespace( "xsd", Constants.XSD_NS );
		}

		cursor.toFirstChild();

		cursor.beginElement( soapVersion.getBodyQName() );
		cursor.toFirstChild();

		buildDocumentRequest( bindingOperation, cursor, xmlGenerator );

		if( alwaysBuildHeaders )
		{
			BindingInput bindingInput = bindingOperation.getBindingInput();
			if( bindingInput != null )
			{
				List<?> extensibilityElements = bindingInput.getExtensibilityElements();
				List<SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders( extensibilityElements );
				addHeaders( soapHeaders, cursor, xmlGenerator );
			}
		}
		cursor.dispose();

		try
		{
			StringWriter writer = new StringWriter();
			XmlUtils.serializePretty( object, writer );
			return writer.toString();
		}
		catch( Exception e )
		{
			return object.xmlText();
		}
	}

	private void addHeaders( List<SoapHeader> headers, XmlCursor cursor, XmlGeneratorUtility xmlGenerator ) throws Exception
	{
		// reposition
		cursor.toStartDoc();
                SoapVersion soapVersion = SoapVersion.Soap11;
		cursor.toChild( soapVersion.getEnvelopeQName() );
		cursor.toFirstChild();

		cursor.beginElement( soapVersion.getHeaderQName() );
		cursor.toFirstChild();

		for( int i = 0; i < headers.size(); i++ )
		{
			SoapHeader header = headers.get( i );

			Message message = riWSDLContext.getMessage( header.getMessage() );
			if( message == null )
			{
				System.err.println( "Missing message for header: " + header.getMessage() );
				continue;
			}

			Part part = message.getPart( header.getPart() );

			if( part != null )
				createElementForPart( part, cursor, xmlGenerator );
			else
				System.err.println( "Missing part for header; " + header.getPart() );
		}
	}

	public void createElementForPart( Part part, XmlCursor cursor, XmlGeneratorUtility xmlGenerator ) throws Exception
	{
		QName elementName = part.getElementName();
		QName typeName = part.getTypeName();

		if( elementName != null )
		{
			cursor.beginElement( elementName );

			if( riWSDLContext.hasSchemaTypes)
			{
				SchemaGlobalElement elm = riWSDLContext.getSchemaTypeSystem().findElement( elementName );
				if( elm != null )
				{
					cursor.toFirstChild();
					xmlGenerator.createSampleForType( elm.getType(), cursor );
				}
				else
					System.err.println( "Could not find element [" + elementName + "] specified in part [" + part.getName() + "]" );
			}

			cursor.toParent();
		}
		else
		{
			// cursor.beginElement( new QName(
			// wsdlContext.getWsdlDefinition().getTargetNamespace(), part.getName()
			// ));
			cursor.beginElement( part.getName() );
			if( typeName != null && riWSDLContext.hasSchemaTypes )
			{
				SchemaType type = riWSDLContext.getSchemaTypeSystem().findType( typeName );

				if( type != null )
				{
					cursor.toFirstChild();
					xmlGenerator.createSampleForType( type, cursor );
				}
				else
					System.err.println( "Could not find type [" + typeName + "] specified in part [" + part.getName() + "]" );
			}

			cursor.toParent();
		}
	}

	private String buildDocumentRequest( BindingOperation bindingOperation, XmlCursor cursor, XmlGeneratorUtility xmlGenerator )
			throws Exception
	{
            String finalMessage="";
            Part[] parts = WsdlUtils.getInputParts( bindingOperation );

		for( int i = 0; i < parts.length; i++ )
		{
			Part part = parts[i];
			if( !WsdlUtils.isAttachmentInputPart( part, bindingOperation )
					&& ( part.getElementName() != null || part.getTypeName() != null ) )
			{
				XmlCursor c = cursor.newCursor();
                                c.toEndToken();
				createElementForPart( part, c, xmlGenerator );

		try
		{

                    XmlObject nextobject = XmlObject.Factory.newInstance();
                    XmlCursor nextcursor = nextobject.newCursor();
                    nextcursor.toFirstChild();
                    createElementForPart(part, nextcursor, xmlGenerator);
//                    cursor.copyXml(nextcursor);
                    StringWriter tmpwriter = new StringWriter();
		    XmlUtils.serializePretty( nextobject, tmpwriter );
                    finalMessage = finalMessage + finalMessage.concat(tmpwriter.toString());

		}
		catch( Exception e )
		{
			finalMessage = c.getObject().xmlText();
		}


                                c.dispose();
			}
		}
            return finalMessage;
	}

	private void buildDocumentResponse( BindingOperation bindingOperation, XmlCursor cursor, XmlGeneratorUtility xmlGenerator )
			throws Exception
	{
		Part[] parts = WsdlUtils.getOutputParts( bindingOperation );

		for( int i = 0; i < parts.length; i++ )
		{
			Part part = parts[i];

			if( !WsdlUtils.isAttachmentOutputPart( part, bindingOperation )
					&& ( part.getElementName() != null || part.getTypeName() != null ) )
			{
				XmlCursor c = cursor.newCursor();
				c.toLastChild();
				createElementForPart( part, c, xmlGenerator );
				c.dispose();
			}
		}
	}



	public String buildMessageFromOutput( BindingOperation bindingOperation, boolean buildOptional,
			boolean alwaysBuildHeaders ) throws Exception
	{
		boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded( bindingOperation );
		XmlGeneratorUtility xmlGenerator = new XmlGeneratorUtility( inputSoapEncoded );
//		xmlGenerator.setIgnoreOptional( !buildOptional );
//		xmlGenerator.setMultiValues( multiValues );

		XmlObject object = XmlObject.Factory.newInstance();
		XmlCursor cursor = object.newCursor();
		cursor.toNextToken();
//		cursor.beginElement( SoapVersion.Soap11.getEnvelopeQName());

//		if( inputSoapEncoded )
//		{
//			cursor.insertNamespace( "xsi", Constants.XSI_NS );
//			cursor.insertNamespace( "xsd", Constants.XSD_NS );
//		}

//		cursor.toFirstChild();

//		cursor.beginElement( SoapVersion.Soap11.getBodyQName() );
		cursor.toFirstChild();

			buildDocumentResponse( bindingOperation, cursor, xmlGenerator );

//		if( alwaysBuildHeaders )
//		{
			// bindingOutput will be null for one way operations,
			// but then we shouldn't be here in the first place???
//			BindingOutput bindingOutput = bindingOperation.getBindingOutput();
//			if( bindingOutput != null )
//			{
//				List<?> extensibilityElements = bindingOutput.getExtensibilityElements();
//				List<SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders( extensibilityElements );
//				addHeaders( soapHeaders, cursor, xmlGenerator );
//			}
//		}
		cursor.dispose();

		try
		{
			StringWriter writer = new StringWriter();
			XmlUtils.serializePretty( object, writer );
			return writer.toString();
		}
		catch( Exception e )
		{
			return object.xmlText();
		}
	}


	public String buildSoapMessageFromOutput( BindingOperation bindingOperation, boolean buildOptional )
			throws Exception
	{
		return buildSoapMessageFromOutput( bindingOperation, buildOptional, true );
	}

	public String buildSoapMessageFromOutput( BindingOperation bindingOperation, boolean buildOptional,
			boolean alwaysBuildHeaders ) throws Exception
	{
		boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded( bindingOperation );
		XmlGeneratorUtility xmlGenerator = new XmlGeneratorUtility( inputSoapEncoded );
//		xmlGenerator.setIgnoreOptional( !buildOptional );
//		xmlGenerator.setMultiValues( multiValues );

		XmlObject object = XmlObject.Factory.newInstance();
		XmlCursor cursor = object.newCursor();
		cursor.toNextToken();
		cursor.beginElement( SoapVersion.Soap11.getEnvelopeQName());

		if( inputSoapEncoded )
		{
			cursor.insertNamespace( "xsi", Constants.XSI_NS );
			cursor.insertNamespace( "xsd", Constants.XSD_NS );
		}

		cursor.toFirstChild();

		cursor.beginElement( SoapVersion.Soap11.getBodyQName() );
		cursor.toFirstChild();

			buildDocumentResponse( bindingOperation, cursor, xmlGenerator );

		if( alwaysBuildHeaders )
		{
			// bindingOutput will be null for one way operations,
			// but then we shouldn't be here in the first place???
			BindingOutput bindingOutput = bindingOperation.getBindingOutput();
			if( bindingOutput != null )
			{
				List<?> extensibilityElements = bindingOutput.getExtensibilityElements();
				List<SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders( extensibilityElements );
				addHeaders( soapHeaders, cursor, xmlGenerator );
			}
		}
		cursor.dispose();

		try
		{
			StringWriter writer = new StringWriter();
			XmlUtils.serializePretty( object, writer );
			return writer.toString();
		}
		catch( Exception e )
		{
			return object.xmlText();
		}
	}

	public String buildFault( BindingOperation bindingOperation, String faultName )
	{

		XmlGeneratorUtility generator = new XmlGeneratorUtility( false );
//		generator.setExampleContent( false );
//		generator.setTypeComment( false );
//		generator.setMultiValues( multiValues );
		String faultResponse = buildEmptyFault();

		XmlCursor cursor = null;
		try
		{
			XmlObject xmlObject = XmlObject.Factory.parse( faultResponse );
			XmlObject[] detail = xmlObject.selectPath( "//detail" );
			if( detail.length > 0 )
			{
				cursor = detail[0].newCursor();

				cursor.toFirstContentToken();

//				generator.setTypeComment( true );
//				generator.setIgnoreOptional( iface.getSettings().getBoolean(
//						WsdlSettings.XML_GENERATION_ALWAYS_INCLUDE_OPTIONAL_ELEMENTS ) );

				for( Part part : getFaultParts(bindingOperation, faultName) )
					createElementForPart( part, cursor, generator );
			}

			faultResponse = xmlObject.xmlText( new XmlOptions().setSaveAggressiveNamespaces().setSavePrettyPrint() );
		}
		catch( Exception e1 )
		{
		}
		finally
		{
			if( cursor != null )
				cursor.dispose();
		}

		return faultResponse;
	}

	public static Part[] getFaultParts( BindingOperation bindingOperation, String faultName ) throws Exception
	{
		List<Part> result = new ArrayList<Part>();

		BindingFault bindingFault = bindingOperation.getBindingFault( faultName );
		SOAPFault soapFault = WsdlUtils.getExtensiblityElement( bindingFault.getExtensibilityElements(), SOAPFault.class );

		Operation operation = bindingOperation.getOperation();
//		if( soapFault != null && soapFault.getName() != null )
		if( bindingFault != null && bindingFault.getName() != null )
		{
//			Fault fault = operation.getFault( soapFault.getName() );
			Fault fault = operation.getFault( bindingFault.getName() );
			if( fault == null )
				throw new Exception( "Missing Fault [" + soapFault.getName() + "] in operation [" + operation.getName()
						+ "]" );
			result.addAll( fault.getMessage().getOrderedParts( null ) );
		}
		return result.toArray( new Part[result.size()] );
	}

}
