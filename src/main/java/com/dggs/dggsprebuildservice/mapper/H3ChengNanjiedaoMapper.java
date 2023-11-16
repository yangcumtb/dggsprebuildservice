package com.dggs.dggsprebuildservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dggs.dggsprebuildservice.model.SpatialData;
import org.apache.ibatis.annotations.Param;

public interface H3ChengNanjiedaoMapper extends BaseMapper<SpatialData> {

    SpatialData getRgb(@Param("code") Long code);
}
