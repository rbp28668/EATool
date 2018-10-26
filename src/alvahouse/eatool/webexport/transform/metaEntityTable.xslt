<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes" xmlns:ex="http://alvagem.co.uk/eatool/export">
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="ex:Export">
	<html>
		<head>
 		    <link type="text/css" rel="stylesheet" href="../ea.css"/>
			<title>Entities of type <xsl:value-of select="ex:EntityList/ex:Name"/> as Table</title>
		</head>
		<body>
			<h1 id="repTitle"><xsl:value-of select="ex:Title"/></h1>
			<xsl:apply-templates select="ex:EntityList"/>
		</body>
	</html>
	</xsl:template>

	<xsl:template match="ex:EntityList">
		<h2><xsl:value-of select="ex:Name"/></h2>
		<hr/>
			<a href="../index.html" class="mainNav">Home</a>
		<hr/>
		<table class="metaEnityTable">
			<tbody>
				<xsl:apply-templates select="ex:MetaProperties"/>
				<xsl:apply-templates select="ex:Entity"/>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="ex:MetaProperties">
		<tr>
			<th>Entity</th>
			<xsl:for-each select="ex:MetaProperty">
				<th><xsl:value-of select="ex:Name"/></th>
			</xsl:for-each>
		</tr>
	</xsl:template>
	
	<xsl:template match="ex:Entity">
		<xsl:element name="tr">
		<xsl:choose>
			<xsl:when test="position() mod 2 = 0 "><xsl:attribute name="class">evenER</xsl:attribute></xsl:when>
			<xsl:otherwise><xsl:attribute name="class">oddER</xsl:attribute></xsl:otherwise>
		</xsl:choose>
			<td><a href="../entity/{ex:UUID}.html"><xsl:value-of select="ex:Name"/></a></td>
			<xsl:for-each select="ex:Properties/ex:Property">
				<td>
				<xsl:choose>
					<xsl:when test="ex:Type='url'">
						<a href="{ex:Value}"><xsl:value-of select="ex:Value"/></a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="ex:Value"/>
					</xsl:otherwise>
				</xsl:choose>
				</td>
			</xsl:for-each>
		</xsl:element> <!-- end tr -->
	</xsl:template>
	
</xsl:stylesheet>
