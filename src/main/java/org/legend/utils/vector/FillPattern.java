package org.legend.utils.vector;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FillPattern {
    private String propertyName;
    private String propertyValue;
    private String spriteId;
    private String defaultSpriteId;
}
