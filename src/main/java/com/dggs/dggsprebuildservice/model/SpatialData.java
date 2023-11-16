package com.dggs.dggsprebuildservice.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("h3_chengnanjiedao")
public class SpatialData {

    /**
     * 网格编码
     */
    private Long id = 0L;

    /**
     * geojson空间数据
     */
    private String geom;

    /**
     * 红波段
     */
    private Integer red;

    /**
     * 绿波段
     */
    private Integer green;

    /**
     * 蓝波段
     */
    private Integer blue;

    private Integer alph = 1;

    private String resampleFile;


}
