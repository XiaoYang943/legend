<?xml version="1.0" encoding="UTF-8"?><se:UserStyle xmlns="http://www.opengis.net/se" xmlns:se="http://www.opengis.net/se" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc">
  <se:Name>Stroke size</se:Name>
  <se:FeatureTypeStyle>
    <se:Name>name</se:Name>
    <se:Rule>
      <se:LineSymbolizer>
        <se:Stroke>
          <se:CssParameter name="stroke">#FF0000</se:CssParameter>
          <se:CssParameter name="stroke-width">
            <ogc:Function name="if_then_else">
              <ogc:Function name="greaterThan">
                <ogc:PropertyName>ID_RSU</ogc:PropertyName>
                <ogc:Literal>1000</ogc:Literal>
              </ogc:Function>
              <ogc:Literal>5</ogc:Literal>
              <ogc:Literal>20</ogc:Literal>
            </ogc:Function>
          </se:CssParameter>
        </se:Stroke>
      </se:LineSymbolizer>
    </se:Rule>
  </se:FeatureTypeStyle>
</se:UserStyle>
