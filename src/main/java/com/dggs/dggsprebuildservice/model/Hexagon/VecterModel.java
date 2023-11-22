package com.dggs.dggsprebuildservice.model.Hexagon;

import lombok.Data;

@Data
public class VecterModel {
    /**
     * 网格编码
     */
    private Long id = 0L;

    /**
     * 是否是陆地
     */
    private Boolean island;
}
