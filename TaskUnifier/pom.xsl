<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://maven.apache.org/POM/4.0.0"
                xmlns:m="http://maven.apache.org/POM/4.0.0"
                exclude-result-prefixes="m">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:template match="node()|@*">
        <xsl:param name="projectVersion"/>
        <xsl:param name="parentVersion"/>
        <xsl:copy>
            <xsl:apply-templates select="node()|@*">
                <xsl:with-param name="projectVersion">
                    <xsl:value-of select="$projectVersion"/>
                </xsl:with-param>
                <xsl:with-param name="parentVersion">
                    <xsl:value-of select="$parentVersion"/>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="//m:project/m:version">
        <xsl:param name="projectVersion"/>
        <xsl:param name="parentVersion"/>
        <version>
            <xsl:value-of select="$projectVersion"/>
        </version>
    </xsl:template>
    <xsl:template match="//m:project/m:parent/m:version">
        <xsl:param name="projectVersion"/>
        <xsl:param name="parentVersion"/>
        <version>
            <xsl:value-of select="$parentVersion"/>
        </version>
    </xsl:template>
</xsl:stylesheet>