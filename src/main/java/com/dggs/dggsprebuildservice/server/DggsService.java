package com.dggs.dggsprebuildservice.server;

import com.dggs.dggsprebuildservice.model.BuildParam;
import com.dggs.dggsprebuildservice.model.Camera;
import com.dggs.dggsprebuildservice.model.Hexagon.Hexagon;
import com.dggs.dggsprebuildservice.model.Hexagon.VecterModel;
import com.dggs.dggsprebuildservice.model.SpatialData;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

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

    /**
     * 将摄像头网格化
     *
     * @param camera 摄像头
     * @return 返回六边形网格
     */
    public List<Hexagon> cameraGrids(Camera camera);

    VecterModel tryGetCacheTile(Long code);

    void clearCache(String imageId);

}
