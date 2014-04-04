<?xml version="1.0" encoding="ASCII" standalone="no"?>
<xsl:stylesheet version="1.0" xmlns:ns0="urn:hl7-org:v3"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xsl ns0 xsi xd date"
    xmlns:date="http://exslt.org/dates-and-times" extension-element-prefixes="date">

    <xsl:output method="xml" encoding="ASCII" indent="yes"/>

    <!-- Define input parameters -->
    <xsl:param name="port" select="'8081'"/>

    <!-- Edit the config.xml file for Apache Tomcat -->
    <xsl:template match="/">
        <xsl:apply-templates select="node()|@*"/>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:choose>
            <xsl:when
                test="name()='Connector' and ./@port = '8080'">
                <xsl:element name="Connector">
                	<xsl:attribute name="port">
                		<xsl:value-of select="$port" />
                	</xsl:attribute>
                	<xsl:attribute name="protocol">
                		<xsl:value-of select="'HTTP/1.1'" />
                	</xsl:attribute>
                	<xsl:attribute name="connectionTimeout">
                		<xsl:value-of select="'20000'" />
                	</xsl:attribute>
                	<xsl:attribute name="redirectPort">
                		<xsl:value-of select="'8443'" />
                	</xsl:attribute>
                </xsl:element>
            </xsl:when>
               
            <!-- Otherwise just copy things -->
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
