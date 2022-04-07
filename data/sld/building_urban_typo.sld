<StyledLayerDescriptor xmlns="http://www.opengis.net/se" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.1.0" xmlns:xlink="http://www.w3.org/1999/xlink" units="mm" xsi:schemaLocation="http://www.opengis.net/se http://schemas.opengis.net/se/1.1.0/StyledLayerDescriptor.xsd" xmlns:se="http://www.opengis.net/se">
  <NamedLayer>
    <se:Name>typo_usr_geom</se:Name>
    <UserStyle>
      <se:Name>typo_usr_geom</se:Name>
      <se:FeatureTypeStyle>
        <se:Rule>
          <se:Name>Industrial building</se:Name>
          <se:Description>
            <se:Title>Industrial building</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>ba</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#8f8f8f</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>High-rise building</se:Name>
          <se:Description>
            <se:Title>High-rise building</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>bgh</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#000d00</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Block of buildings on closed urban islet</se:Name>
          <se:Description>
            <se:Title>Block of buildings on closed urban islet</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>icif</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#d52623</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Block of buildings on open urban islet</se:Name>
          <se:Description>
            <se:Title>Block of buildings on open urban islet</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>icio</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#f07923</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Detached building</se:Name>
          <se:Description>
            <se:Title>Detached building</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>id</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#eccb27</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Informal building</se:Name>
          <se:Description>
            <se:Title>Informal building</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>local</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#d728ac</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Residential on closed islet</se:Name>
          <se:Description>
            <se:Title>Residential on closed islet</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>pcif</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#2b6724</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Residential on open islet</se:Name>
          <se:Description>
            <se:Title>Residential on open islet</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>pcio</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#36884a</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Detached house</se:Name>
          <se:Description>
            <se:Title>Detached house</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>pd</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#22be2f</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Semi-detached house</se:Name>
          <se:Description>
            <se:Title>Semi-detached house</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
              <ogc:Literal>psc</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#05ff58</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:Name>Undefined</se:Name>
          <se:Description>
            <se:Title>Undefined</se:Title>
          </se:Description>
          <MaxScaleDenominator>500000</MaxScaleDenominator>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsNull>
              <ogc:PropertyName>I_TYPO</ogc:PropertyName>
            </ogc:PropertyIsNull>
          </ogc:Filter>
          <se:PolygonSymbolizer>
            <se:Fill>
              <se:CssParameter name="fill">#ffffff</se:CssParameter>
            </se:Fill>
            <se:Stroke>
              <se:CssParameter name="stroke">#000000</se:CssParameter>
              <se:CssParameter name="stroke-width">0.26</se:CssParameter>
              <se:CssParameter name="stroke-linejoin">bevel</se:CssParameter>
            </se:Stroke>
          </se:PolygonSymbolizer>
        </se:Rule>
      </se:FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>