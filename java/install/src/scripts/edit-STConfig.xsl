<?xml version="1.0" encoding="ASCII" standalone="no"?>
<xsl:stylesheet version="1.0" xmlns:ns0="urn:hl7-org:v3"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xsl ns0 xsi xd date"
    xmlns:date="http://exslt.org/dates-and-times" extension-element-prefixes="date">

    <xsl:output method="xml" encoding="ASCII" indent="yes"/>

    <!-- Define input paramaters -->
    <xsl:param name="keystorepath" select="'keystore-path-edited'"/>

    <!-- Edit the config.xml file for Apache James -->
    <xsl:template match="/">
        <xsl:apply-templates select="node()|@*"/>
    </xsl:template>

    <xsl:template match="@file">
        <xsl:attribute name="file">
            <xsl:value-of select="$keystorepath"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
