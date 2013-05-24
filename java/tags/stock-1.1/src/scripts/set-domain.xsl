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
            <!-- Edit the postmaster -->
            <xsl:when
                test="name(parent::node()/parent::node())='config' and name(parent::node())='James' and name()='postmaster'">
                <xsl:element name="postmaster">
                    <xsl:text>Postmaster@</xsl:text>
                    <xsl:value-of select="$domain"/>
                </xsl:element>
            </xsl:when>

            <!-- Edit the servername -->
            <xsl:when
                test="name(parent::node()/parent::node()/parent::node())='config' and name(parent::node()/parent::node())='James' and name(parent::node())='servernames' and name()='servername'">
                <xsl:element name="servername">
                    <xsl:value-of select="$domain"/>
                </xsl:element>
            </xsl:when>

            <!-- Edit the RecipAndSenderIsNotLocal=mydomain.com attribute of the mailet configuration  -->
            <xsl:when test="name()='mailet' and starts-with(./@match,'RecipAndSenderIsNotLocal=')">
                <xsl:element name="mailet">
                    <xsl:attribute name="match">
                        <xsl:text>RecipAndSenderIsNotLocal=</xsl:text>
                        <xsl:value-of select="$domain"/>
                    </xsl:attribute>
                    <xsl:attribute name="class">NHINDSecurityAndTrustMailet</xsl:attribute>
                    <xsl:for-each select="ConfigURL">
                        <xsl:copy>
                            <xsl:apply-templates select="node()|@*"/>
                        </xsl:copy>
                    </xsl:for-each>
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
