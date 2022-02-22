<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : C2CRIValueConversion.xsl
    Created on : May 31, 2013, 3:21 PM
    Author     : TransCore ITS
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	>
<!--	xmlns:mes="http://www.tmdd.org/301/messages" 
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:c2c="http://www.ntcip.org/c2c-message-administration" -->
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-public="yes"/>

<xsl:param name="centerid" select="'tcore_test'"/>
<xsl:param name="organizationid" select="'transcore.com'"/>
<xsl:param name="latitude" select="'33964380'"/>
<xsl:param name="longitude" select="'-84217945'"/>
<xsl:param name="date" select="'20130531'"/>
<xsl:param name="time" select="'105805'"/>
<xsl:param name="offset" select="'01:00'"/>
<xsl:param name="subscriptionActionitem" select="'newSubscription'"/>
<xsl:param name="subscriptionType" select="'periodic'"/>
<xsl:param name="subscriptionStart" select="'2013-06-18T19:18:33'"/>
<xsl:param name="subscriptionEnd" select="'2014-08-19T13:27:14-04:00'"/>
<xsl:param name="dialog"/>
<xsl:param name="networkInformationType"/>
<xsl:param name="deviceType"/>
<xsl:param name="deviceInformationType"/>
    
<xsl:template match="node()|@*">
	<xsl:copy>
		<xsl:apply-templates select="node()|@*"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="center-id">
	<center-id><xsl:value-of select="$centerid"/></center-id>
</xsl:template>

<xsl:template match="organization-id">
	<organization-id><xsl:value-of select="$organizationid"/></organization-id>
</xsl:template>

<xsl:template match="latitude">
	<latitude><xsl:value-of select="$latitude"/></latitude>
</xsl:template>

<xsl:template match="longitude">
	<longitude><xsl:value-of select="$longitude"/></longitude>
</xsl:template>

<xsl:template match="subscriptionAction-item">
	<subscriptionAction-item><xsl:value-of select="$subscriptionActionitem"/></subscriptionAction-item>
</xsl:template>

<xsl:template match="subscriptionType">
	<subscriptionType><subscriptionType-item><xsl:value-of select="$subscriptionType"/></subscriptionType-item></subscriptionType>
</xsl:template>

<xsl:template match="last-update-time/date">
	<date><xsl:value-of select="$date"/></date>
</xsl:template>

<xsl:template match="last-update-time/time">
	<time><xsl:value-of select="$time"/></time>
</xsl:template>

<xsl:template match="last-update-time/offset">
	<offset><xsl:value-of select="$offset"/></offset>
</xsl:template>

<xsl:template match="returnAddress">
	<returnAddress>http://C2CRIEC:8086/TMDDWS/EC/tmddECSoapHttpService/tmddECSoapHttpServicePort/<xsl:value-of select="$dialog"/></returnAddress>
</xsl:template>

<xsl:template match="network-information-type">
    <network-information-type><xsl:value-of select="$networkInformationType"/></network-information-type>
</xsl:template>

<xsl:template match="device-type">
	<device-type><xsl:value-of select="$deviceType"/></device-type>
</xsl:template>

<xsl:template match="device-information-type">
	<device-information-type><xsl:value-of select="$deviceInformationType"/></device-information-type>
</xsl:template>

<xsl:template match="subscriptionTimeFrame/start">
	<start><xsl:value-of select="$subscriptionStart"/></start>
</xsl:template>

<xsl:template match="subscriptionTimeFrame/end">
	<end><xsl:value-of select="$subscriptionEnd"/></end>
</xsl:template>

</xsl:stylesheet>
