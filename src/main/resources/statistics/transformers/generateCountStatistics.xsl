<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:strip-space elements="*"/>
    <xsl:output omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="Statistics">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="Batch">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="Film">
                <xsl:sort select="@name"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="*[name() != 'Film']"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="Film">
          <xsl:copy>
              <xsl:apply-templates select="@*"/>
              <xsl:apply-templates select="Edition">
                  <xsl:sort select="@name"/>
              </xsl:apply-templates>
              <xsl:apply-templates select="*[name() != 'Edition']"/>
          </xsl:copy>
      </xsl:template>

    <xsl:template match="Edition-dates"/>

</xsl:stylesheet>