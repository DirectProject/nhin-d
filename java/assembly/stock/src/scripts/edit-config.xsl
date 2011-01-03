<?xml version="1.0" encoding="ASCII" standalone="no"?>
<xsl:stylesheet version="1.0" xmlns:ns0="urn:hl7-org:v3"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xsl ns0 xsi xd date"
    xmlns:date="http://exslt.org/dates-and-times" extension-element-prefixes="date">

    <xsl:output method="xml" encoding="ASCII" indent="yes"/>

    <!-- Define input paramaters -->
    <xsl:param name="postmaster" select="'nhin-d+postmaster@nologs.org'"/>
    <xsl:param name="servername" select="'james.nologs.org'"/>
    <xsl:param name="mailetpackage" select="'org.nhindirect.gateway.smtp.james.mailet'"/>
    <xsl:param name="matcherpackage" select="'org.nhindirect.gateway.smtp.james.matcher'"/>
    <xsl:param name="mailet-config-scope" select="'All'"/>
    <xsl:param name="mailet-config-url"
        select="'file:///home/mbamberg/james-2.3.2/apps/james/SAR-INF/STConfig.xml'"/>
    <xsl:param name="mailet-config-class" select="'NHINDSecurityAndTrustMailet'"/>
    <xsl:param name="mailet-match-remove" select="'RemoteAddrNotInNetwork=127.0.0.1'"/>

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
                    <xsl:value-of select="$postmaster"/>
                </xsl:element>
            </xsl:when>

            <!-- Add an additional servername -->
            <xsl:when
                test="name(parent::node()/parent::node()/parent::node())='config' and name(parent::node()/parent::node())='James' and name(parent::node())='servernames' and name()='servername'">
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*"/>
                </xsl:copy>
                <xsl:element name="servername">
                    <xsl:value-of select="$servername"/>
                </xsl:element>
            </xsl:when>

            <!-- Copy fetchmailConfig reference -->
            
            <!-- For now, just embed fethmailConfig -->            
            <!--
            <xsl:when test="name(parent::node())='config' and name()='fetchmail'">
                <xsl:text disable-output-escaping="yes">&amp;fetchmailConfig;</xsl:text>
            </xsl:when>
            -->
            
            <!-- Add mailetpackage -->
            <xsl:when test="name(parent::node())='config' and name()='mailetpackages'">
                <xsl:element name="mailetpackages">
                    <xsl:apply-templates select="./mailetpackage"/>
                    <xsl:element name="mailetpackage">
                        <xsl:value-of select="$mailetpackage"/>
                    </xsl:element>
                </xsl:element>
            </xsl:when>

            <!-- Add matcherpackage -->
            <xsl:when test="name(parent::node())='config' and name()='matcherpackages'">
                <xsl:element name="matcherpackages">
                    <xsl:apply-templates select="./matcherpackage"/>
                    <xsl:element name="matcherpackage">
                        <xsl:value-of select="$matcherpackage"/>
                    </xsl:element>
                </xsl:element>
            </xsl:when>

            <!-- Replace RemoteAddrNotInNetwork with new mailet scope, class and url -->
            <xsl:when
                test="name(parent::node()/parent::node())='spoolmanager' and name(parent::node())='processor' and name()='mailet'">
                <xsl:choose>
                    <xsl:when
                        test="@match = 'RemoteAddrNotInNetwork=127.0.0.1' and @class = 'ToProcessor'">
                        <xsl:element name="mailet">
                            <xsl:attribute name="match">
                                <xsl:value-of select="$mailet-config-scope"/>
                            </xsl:attribute>
                            <xsl:attribute name="class">
                                <xsl:value-of select="$mailet-config-class"/>
                            </xsl:attribute>
                            <xsl:element name="ConfigURL">
                                <xsl:value-of select="$mailet-config-url"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy>
                            <xsl:apply-templates select="node() | @*" />
                        </xsl:copy>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            
            <!-- Replace authorized addresses -->
            <xsl:when
                test="name(parent::node()/parent::node())='smtpserver' and name(parent::node())='handler' and name()='authorizedAddresses'">
                <xsl:element name="authRequired">
                    <xsl:value-of select="'announce'"/>
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
