<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes" xmlns:ex="http://alvagem.co.uk/eatool/export">
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="ex:Export">
	<html>
		<head>
 		   <link type="text/css" rel="stylesheet" href="../ea.css"/>
			<title>Entity Types</title>
		</head>
		<body>
			<h1 id="repTitle"><xsl:value-of select="ex:Title"/></h1>
			<h2><xsl:value-of select="ex:StandardDiagram/ex:Name"/></h2>
			<hr/>
			<xsl:apply-templates select="ex:StandardDiagram"/>
			<h2><xsl:value-of select="ex:TimeDiagram/ex:Name"/></h2>
			<hr/>
			<xsl:apply-templates select="ex:TimeDiagram"/>
		</body>
	</html>
	</xsl:template>

	<xsl:template match="ex:StandardDiagram">
		<map name="diagram">
			<xsl:apply-templates select="ex:EntitySymbol"/>
		</map>
		<img src="{ex:UUID}.png" alt="Diagram {ex:Name}" usemap="#diagram"/>
	</xsl:template>

	<xsl:template match="ex:TimeDiagram">
		<map name="diagram">
			<xsl:apply-templates select="ex:EntitySymbol"/>
		</map>
		<img src="{ex:UUID}.png" alt="Diagram {ex:Name}" usemap="#diagram"/>
	</xsl:template>
	
	<xsl:template match="ex:EntitySymbol">
		<area alt="{ex:Name}" href="../entity/{ex:UUID}.html" shape="rect" coords="{ex:Bounds/@x1}, {ex:Bounds/@y1}, {ex:Bounds/@x2}, {ex:Bounds/@y2}"/>
	</xsl:template>
	
</xsl:stylesheet>
