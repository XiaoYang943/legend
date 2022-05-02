<?xml version="1.0" encoding="UTF-8"?><se:UserStyle xmlns="http://www.opengis.net/se" xmlns:se="http://www.opengis.net/se" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc">
  <se:Name>Default Styler</se:Name>
  <se:FeatureTypeStyle>
    <se:Name>name</se:Name>
    <se:Rule>
      <se:LineSymbolizer>
        <se:Stroke>
          <se:CssParameter name="stroke">
            <ogc:Function name="if_then_else">
              <ogc:Function name="greaterThan">
                <ogc:PropertyName>ID_RSU</ogc:PropertyName>
                <ogc:Literal>1000</ogc:Literal>
              </ogc:Function>
              <ogc:Literal>#000000</ogc:Literal>
              <ogc:Literal>#FF0000</ogc:Literal>
            </ogc:Function>
          </se:CssParameter>
        </se:Stroke>
      </se:LineSymbolizer>
    </se:Rule>
  </se:FeatureTypeStyle>
</se:UserStyle>
