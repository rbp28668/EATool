<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes" xmlns:ex="http://alvagem.co.uk/eatool/export">
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="ex:Export">
	<html>
		<head>
		    <link type="text/css" rel="stylesheet" href="../ea.css"/>
			<title>Entities of type <xsl:value-of select="ex:EntityList/ex:Name"/></title>
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
		<table>
			<tbody>
				<xsl:apply-templates select="ex:Entity"/>
			</tbody>
		</table>
		
	</xsl:template>

	<xsl:template match="ex:Entity">
		<tr>
		<td><a href="../entity/{ex:UUID}.html"><xsl:value-of select="ex:Name"/></a></td>
		<xsl:apply-templates select="ex:Properties/ex:Property[@summary='true']" />
		</tr>
	</xsl:template>
	
	<xsl:template match="ex:Property">
		<td><xsl:value-of select="ex:Value"/></td>
	</xsl:template>
</xsl:stylesheet>
