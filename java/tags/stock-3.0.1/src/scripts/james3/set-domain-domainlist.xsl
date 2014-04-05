<?xml version="1.0" encoding="ASCII" standalone="no"?>
<xsl:stylesheet version="1.0" xmlns:ns0="urn:hl7-org:v3"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xsl ns0 xsi xd date"
    xmlns:date="http://exslt.org/dates-and-times" extension-element-prefixes="date">

    <xsl:output method="xml" encoding="ASCII" indent="yes"/>

    <!-- Define input parameters -->
    <xsl:param name="domain" select="'xxx'"/>

    <!-- Edit the config.xml file for Apache James -->
    <xsl:template match="/">
        <xsl:apply-templates select="node()|@*"/>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:choose>


            <!-- Edit the domain name -->
            <xsl:when
                test="name()='domainname'">
                <xsl:element name="domainname">
                    <xsl:value-of select="$domain"/>
                </xsl:element>
            </xsl:when>

           <!-- Edit the default domain name -->
            <xsl:when
                test="name()='defaultDomain'">
                <xsl:element name="defaultDomain">
                    <xsl:value-of select="$domain"/>
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
