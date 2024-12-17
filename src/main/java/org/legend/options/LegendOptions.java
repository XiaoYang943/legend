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

    @Builder.Default
    private boolean isShowAllRules = false; // 不显示全量图例，只保留属性表中有的图例(多数情况下，样式可以复用，当样式时，图例项过多，导致图例太长)
}
