package com.dggs.dggsprebuildservice.server;

import com.dggs.dggsprebuildservice.model.BuildParam;
import com.dggs.dggsprebuildservice.model.SpatialData;

import java.io.IOException;
import java.math.BigInteger;

public interface DggsService {

    /**
     * 空间数据多分辨率格网数据预构建
     *
     * @param buildParam 构建参数
     */
    public void preBuild(BuildParam buildParam) throws IOException;


    /**
     * 空间数据多分辨率格网数据预构建
     *
     * @param buildParam 构建参数
     */
    public void preBuildPyh(BuildParam buildParam) throws IOException;


    SpatialData getRgb(Long code);

}
