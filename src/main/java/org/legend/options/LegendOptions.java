package org.legend.options;

import lombok.Builder;
import lombok.Data;
import org.legend.utils.LegendUtils;

@Data
@Builder
public class LegendOptions {
    private Integer width;
    private Integer height;
    private boolean transparent;
    private String bgColor;
    private Integer verticalRuleMargin;
    private Integer horizontalRuleMargin;
    @Builder.Default
    private LegendUtils.LegendLayout layout = LegendUtils.LegendLayout.VERTICAL;

    @Builder.Default
    private String fontName = "Sans-Serif";

    @Builder.Default
    private String fontStyle = "bold";
    private String fontColor;

    @Builder.Default
    private Integer fontSize = 12;
    private Integer labelXposition;
    private Integer labelXOffset;

    @Builder.Default
    private Integer maxHeight = 0;

    private String title;
}
