package org.legend.options;

import lombok.Builder;
import lombok.Data;
import org.legend.utils.legend.LegendUtils;

@Data
@Builder
public class LegendOptions {
    /**
     * 图例项的宽度(image+padding)
     */
    @Builder.Default
    private Integer width = 32;

    /**
     * 图例项的高度(image+padding)
     */
    @Builder.Default
    private Integer height = 32;

    /**
     *
     */
    private boolean transparent;

    /**
     * 背景色
     */
    private String bgColor;

    /**
     * 图例的布局方式
     */
    @Builder.Default
    private LegendUtils.LegendLayout layout = LegendUtils.LegendLayout.VERTICAL;

    /**
     * 字体名称
     */
    @Builder.Default
    private String fontName = "Sans-Serif";

    /**
     * 字体风格
     */
    @Builder.Default
    private String fontStyle = "bold";

    /**
     * 字体颜色
     */
    private String fontColor;

    /**
     * 字体大小
     */
    @Builder.Default
    private Integer fontSize = 12;

    /**
     * 图例标题的水平偏移量
     */
    private Integer titleOffsetX;

    /**
     * 图例标签相对于符号的水平偏移量
     */
    private Integer labelOffsetX;

    /**
     * 整个图例项的最大高度
     */
    @Builder.Default
    private Integer maxHeight = 0;

    /**
     * 图例项垂直方向上的偏移量
     */
    @Builder.Default
    private Integer ruleOffsetY = 0;

    /**
     * 图例标题内容
     */
    private String title;

    /**
     * 不显示全量图例，只保留属性表中有的图例
     */
    @Builder.Default
    private boolean isShowAllRules = false;
}
