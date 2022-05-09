<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor xmlns="http://www.opengis.net/se" version="1.1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/se http://schemas.opengis.net/se/1.1.0/StyledLayerDescriptor.xsd" xmlns:ogc="http://www.opengis.net/ogc" xmlns:se="http://www.opengis.net/se">
  <NamedLayer>
    <se:Name>zones</se:Name>
    <UserStyle>
      <se:Name>zones</se:Name>
      <se:FeatureTypeStyle>
        <se:Rule>
          <se:Name>Single symbol</se:Name>
          <se:PointSymbolizer>
            <se:Graphic>
              <!--Parametric SVG-->
              <se:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="../img/map_marker.svg" />
                <se:Format>image/png</se:Format>
              </se:ExternalGraphic>
              <!--Well known marker fallback-->
              <se:Mark>
                <se:WellKnownName>square</se:WellKnownName>
                <se:Fill>
                  <se:CssParameter name="fill">#ff0000</se:CssParameter>
                </se:Fill>
                <se:Stroke>
                  <se:CssParameter name="stroke">#232323</se:CssParameter>
                  <se:CssParameter name="stroke-width">0.5</se:CssParameter>
                </se:Stroke>
              </se:Mark>
              <se:Size>18</se:Size>
            </se:Graphic>
          </se:PointSymbolizer>
        </se:Rule>
        <se:Rule>
          <se:TextSymbolizer>
            <se:Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </se:Label>
            <se:Font>
              <se:CssParameter name="font-family">Liberation Sans</se:CssParameter>
              <se:CssParameter name="font-size">15</se:CssParameter>
            </se:Font>
            <se:LabelPlacement>
              <se:PointPlacement>
                <se:AnchorPoint>
                  <se:AnchorPointX>0</se:AnchorPointX>
                  <se:AnchorPointY>1</se:AnchorPointY>
                </se:AnchorPoint>
              </se:PointPlacement>
            </se:LabelPlacement>
            <se:Halo>
              <se:Radius>2</se:Radius>
              <se:Fill>
                <se:CssParameter name="fill">#fafafa</se:CssParameter>
                <se:CssParameter name="fill-opacity">0.8</se:CssParameter>
              </se:Fill>
            </se:Halo>
            <se:Fill>
              <se:CssParameter name="fill">#323232</se:CssParameter>
            </se:Fill>
          </se:TextSymbolizer>
        </se:Rule>
      </se:FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>
