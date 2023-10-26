package com.dggs.dggsprebuildservice.server;

import com.dggs.dggsprebuildservice.model.BuildParam;
import com.dggs.dggsprebuildservice.model.SpatialData;

public interface DggsService {

    /**
     * 空间数据多分辨率格网数据预构建
     *
     * @param spatialData 空间数据
     * @param buildParam  构建参数
     */
    public void preBuild(SpatialData spatialData, BuildParam buildParam);

}
