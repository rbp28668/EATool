<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes" xmlns:ea="http://rbp28668.co.uk/java/test/repository">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/ea:Repository">
		<html>
			<head>
				<title>Meta Model Export</title>
			</head>
			<body>
				<h1>Meta Model</h1>
				<h2>Entities</h2>
				<xsl:apply-templates select="ea:MetaModel/ea:MetaEntity"/>
				<h2>Relationships</h2>
				<xsl:apply-templates select="ea:MetaModel/ea:MetaRelationship"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="ea:MetaEntity">
		<xsl:call-template name="NamedItem"/>
		<xsl:if test="ea:MetaProperty">
		<table>
			<xsl:for-each select="ea:MetaProperty">
			<tr>
				<td><xsl:value-of select="@name"/></td>
				<td><xsl:value-of select="@type"/></td>
				<td><xsl:value-of select="@uuid"/></td>
			</tr>
			</xsl:for-each>
		</table>
		</xsl:if>
	</xsl:template>	

	<xsl:template match="ea:MetaRelationship">
		<xsl:call-template name="NamedItem"/>
		<xsl:apply-templates select="ea:MetaRole"/>
	</xsl:template>	

	<xsl:template match="ea:MetaRole">
		<p>Role <xsl:value-of select="@name"/>
		(<i><xsl:value-of select="@uuid"/></i>)<br/>
		Connects to <xsl:value-of select="@connects"/> with multiplicity <xsl:value-of select="@multiplicity"/>
		<xsl:if test="@description"><br/><xsl:value-of select="@description"/></xsl:if>
		</p>
		
	</xsl:template>

	<xsl:template name="NamedItem">
		<h3><xsl:value-of select="@name"/></h3>
		<i><xsl:value-of select="@uuid"/></i>
		<xsl:if test="@description"><br/><xsl:value-of select="@description"/></xsl:if>
	</xsl:template>
</xsl:stylesheet>
