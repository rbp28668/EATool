<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes" xmlns:ex="http://alvagem.co.uk/eatool/export">
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="ex:Export">
	<html>
		<head>
 		   <link type="text/css" rel="stylesheet" href="ea.css"/>
			<title>Entity Types</title>
		</head>
		<body>
			<h1 id="repTitle"><xsl:value-of select="ex:Title"/></h1>
			<h2>Entity Types</h2>
			<dl>
			<xsl:apply-templates select="ex:MetaEntity"/>
			</dl>
			<xsl:if test="ex:DiagramTypes/ex:DiagramType">
				<hr/>
				<h2>Diagrams</h2>
				<xsl:apply-templates select="ex:DiagramTypes/ex:DiagramType"/>
			</xsl:if>
			<xsl:if test="ex:HTMLPageList/ex:HTMLPage">
				<hr/>
				<h2>Report Pages</h2>
				<xsl:apply-templates select="ex:HTMLPageList/ex:HTMLPage"/>
			</xsl:if>
		</body>
	</html>
	</xsl:template>

	<xsl:template match="ex:MetaEntity">
		<dt>
		<a href="metaEntity/{ex:UUID}.html"><xsl:value-of select="ex:Name"/></a>
		<a href="metaEntityTable/{ex:UUID}.html">( as table )</a>
		</dt>
		<xsl:if test="ex:Description">
			<dd>
				<xsl:value-of select="ex:Description"/>
			</dd>
		</xsl:if>		
	</xsl:template>
	
	<xsl:template match="ex:DiagramType">
		<h3 class="diagramType"><xsl:value-of select="ex:Name"/></h3>
		<dl>
		<xsl:apply-templates select="ex:Diagrams/ex:Diagram"/>
		</dl>
	</xsl:template>
	
	<xsl:template match="ex:Diagram">
	    <dt><a href="diagrams/{ex:UUID}.html"><xsl:value-of select="ex:Name"/></a></dt>
		<dd><xsl:value-of select="ex:Description"/></dd>
	</xsl:template>

	<xsl:template match="ex:HTMLPage">
	    <dt><a href="pages/{ex:UUID}.html"><xsl:value-of select="ex:Name"/></a></dt>
		<dd><xsl:value-of select="ex:Description"/></dd>
	</xsl:template>

</xsl:stylesheet>
