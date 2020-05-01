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
			<h2><xsl:value-of select="ex:Entity/ex:Name"/> (<xsl:value-of select="ex:Entity/ex:MetaName"/>)</h2>
			<xsl:apply-templates select="ex:Entity"/>
		</body>
	</html>
	</xsl:template>

	<xsl:template match="ex:Entity">
		<hr/>
		<a href="../index.html" class="mainNav">Home</a> <a href="../metaEntity/{ex:MetaUUID}.html" class="mainNav">Up to <xsl:value-of select="ex:MetaName"/></a>
		<hr/>
		
		<xsl:apply-templates select="ex:Properties"/>
		<xsl:apply-templates select="ex:Relationships"/>
		
	</xsl:template>

	<xsl:template match="ex:Properties">
	<table class="entityTable">
		<tbody>
			<xsl:apply-templates select="ex:Property"/>
		</tbody>
	</table>
	</xsl:template>
	
	<xsl:template match="ex:Property">
		<tr>
		<td><xsl:value-of select="@name"/></td>
		<xsl:choose>
			<xsl:when test="@type='url'">
				<td><a href="{.}"><xsl:value-of select="."/></a></td>
			</xsl:when>	
			<xsl:otherwise>
				<td><xsl:value-of select="."/></td>
			</xsl:otherwise>
		</xsl:choose>
		</tr>
	</xsl:template>


	<xsl:template match="ex:Relationships">
		<xsl:for-each select="ex:RelationshipType">
			<h3 class="relType"><xsl:value-of select="ex:Name"/></h3>
		<table>
			<tbody>
			<xsl:apply-templates select="ex:Relationship"/>
			</tbody>
		</table>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="ex:Relationship">
		<xsl:apply-templates select="ex:Role"/>
	</xsl:template>
	
	<xsl:template match="ex:Role">
		<tr>
			<td><xsl:value-of select="ex:Name"/>:</td>
			<td> <a href="../entity/{ex:ConnectedEntity/ex:ConnectedUUID}.html"><xsl:value-of select="ex:ConnectedEntity/ex:Name"/></a> </td>
			<xsl:apply-templates select="ex:ConnectedEntity/ex:SummaryProperty"/>
			<xsl:apply-templates select="../ex:RelationshipProperty"/>
		</tr>
	</xsl:template>
	
	<xsl:template match="ex:SummaryProperty">
		<td><xsl:value-of select="."/></td>
	</xsl:template>

	<xsl:template match="ex:RelationshipProperty">
	    <!--  Note can use contents of @type attribute to produce type specific output (e.g boolean) -->
	    <xsl:choose>
	    	<xsl:when test="@type='boolean'">
	    		<xsl:choose>
	    			<xsl:when test="current() = 'true'">
						<td><xsl:value-of select="@name"/></td>
	    			</xsl:when> 
		    		<xsl:otherwise>
		    			<td> </td>
		    		</xsl:otherwise>
	    		</xsl:choose>
	    	</xsl:when>
	    	<xsl:otherwise>
				<td><xsl:value-of select="@name"/>: <xsl:value-of select="."/></td>
	    	</xsl:otherwise>
	    </xsl:choose>
	</xsl:template>

</xsl:stylesheet>
