<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor xmlns="http://www.opengis.net/se" xmlns:se="http://www.opengis.net/se" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.1.0" xmlns:ogc="http://www.opengis.net/ogc" xsi:schemaLocation="http://www.opengis.net/se http://schemas.opengis.net/se/1.1.0/StyledLayerDescriptor.xsd" xmlns:xlink="http://www.w3.org/1999/xlink">
  <NamedLayer>
    <se:Name>Average building compactness</se:Name>
    <UserStyle>
      <se:Name>Average building compactness</se:Name>
      <se:FeatureTypeStyle>
        <se:Rule>
          <se:Name>0.0 - 0.0</se:Name>
          <se:Description>
            <se:Title>0.0 - 0.0</se:Title>
          </se:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
              <ogc:Literal>0</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:MaxScaleDenominator>500000</se:MaxScaleDenominator>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#feebe0</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#232323</se:CssParameter>
              <se:CssParameter name="stroke-width">1</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>0.0 - 4.6</se:Name>
          <se:Description>
            <se:Title>0.0 - 4.6</se:Title>
          </se:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:And>
              <ogc:PropertyIsGreaterThan>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>0</ogc:Literal>
              </ogc:PropertyIsGreaterThan>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>4.59999999999999964</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
          <se:MaxScaleDenominator>500000</se:MaxScaleDenominator>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#ffaab6</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#232323</se:CssParameter>
              <se:CssParameter name="stroke-width">1</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>4.6 - 5.0</se:Name>
          <se:Description>
            <se:Title>4.6 - 5.0</se:Title>
          </se:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:And>
              <ogc:PropertyIsGreaterThan>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>4.59999999999999964</ogc:Literal>
              </ogc:PropertyIsGreaterThan>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>5</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
          <se:MaxScaleDenominator>500000</se:MaxScaleDenominator>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#ff29a0</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#232323</se:CssParameter>
              <se:CssParameter name="stroke-width">1</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>5.0 - 5.5</se:Name>
          <se:Description>
            <se:Title>5.0 - 5.5</se:Title>
          </se:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:And>
              <ogc:PropertyIsGreaterThan>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>5</ogc:Literal>
              </ogc:PropertyIsGreaterThan>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>5.5</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
          <se:MaxScaleDenominator>500000</se:MaxScaleDenominator>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#d1008b</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#232323</se:CssParameter>
              <se:CssParameter name="stroke-width">1</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>5.5 - 103</se:Name>
          <se:Description>
            <se:Title>5.5 - 103</se:Title>
          </se:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:And>
              <ogc:PropertyIsGreaterThan>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>5.5</ogc:Literal>
              </ogc:PropertyIsGreaterThan>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>NET_COMPACTNESS</ogc:PropertyName>
                <ogc:Literal>103</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
          <se:MaxScaleDenominator>500000</se:MaxScaleDenominator>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#86007a</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#232323</se:CssParameter>
              <se:CssParameter name="stroke-width">1</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
      </se:FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>
