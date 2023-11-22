package com.dggs.dggsprebuildservice.config;

import com.dggs.dggsprebuildservice.model.Hexagon.VecterModel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;
import io.swagger.models.auth.In;
import org.gdal.gdal.gdal;
import org.gdal.ogr.*;

import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheLoader {

    public CacheLoader() throws IOException {
    }

    private static H3Core h3;

    private static Cache<Long, VecterModel> cache = Caffeine.newBuilder()
            .maximumSize(50000000) // 设置缓存的最大大小
            .build();


    @PreDestroy
    public void destroy() throws Exception {
        cache.invalidateAll();
    }

    /**
     * 预缓存前0-8级别数据
     */
    public static void preloadTilesToCache(String cacheShpTilePath, int maxRes) {
        for (int i = 0; i <= maxRes; i++) {
            List<Long> nowList = getShpMulitRes(cacheShpTilePath, i);
            System.out.println("第" + i + "层级构建完成");
            System.out.println("第" + i + "层级存储中...");
            for (int j = 0; j < nowList.size(); j++) {
                VecterModel vecterModel = new VecterModel();
                cache.put(nowList.get(j), vecterModel);
            }
            System.out.println("第" + i + "层级存储完成！");
        }
    }

    /**
     * 将shpfile转换成pointlist
     */
    public static List<Long> getShpMulitRes(String path, int res) {
        List<List<LatLng>> holes = new ArrayList<>();
        List<LatLng> points = new ArrayList<>();

        gdal.AllRegister();
        ogr.RegisterAll();

        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
//        属性表字段支持中文
        gdal.SetConfigOption("SHAPE_ENCODING", "");
//        读取数据
        String strDriverName = "ESRI Shapefile";
//        创建文件，根据strDriverName扩展名自动判断驱动类型
        org.gdal.ogr.Driver oDriver = ogr.GetDriverByName(strDriverName);
        if (oDriver == null) {
            System.out.println(strDriverName + "驱动不可用！\n");
            return null;
        }


        DataSource source = oDriver.Open(path);
        if (source == null) {
            System.out.println("无法打开文件");
            return null;
        }
        Layer layer = source.GetLayerByIndex(0);
        System.out.println(source.GetLayerCount());
        Feature feature;
        while ((feature = layer.GetNextFeature()) != null) {
            Geometry geometry = feature.GetGeometryRef();
            System.out.println(geometry.GetGeometryCount());
            //便利每个面
            for (int geoNum = 0; geoNum < geometry.GetGeometryCount(); geoNum++) {
                Geometry polygon = geometry.GetGeometryRef(geoNum);
                System.out.println(polygon.ExportToJson());
                String geometryJson = polygon.ExportToJson();
                double[][] boundaryPoints = polygon.GetBoundary().GetPoints();


                for (int i = 0; i < polygon.GetPointCount(); i++) {
                    double[] point = geometry.GetPoint(i);
                    if (polygon.IsRing()) {
                        //如果封闭，不是孔洞
                        points.add(new LatLng(point[1], point[0]));
                    }
                }
            }
            feature.delete();
        }
        source.delete();
        layer.delete();
        System.out.println("第" + res + "层计算中...");
        List<Long> result = h3.polygonToCells(points, holes, res);
        System.out.println("第" + res + "层计算完成！");
        return result;

    }

    /**
     * 根据key来获取已经缓存的瓦片
     *
     * @param key 层级+列号+行号，字符串拼接
     * @return
     */
    public static VecterModel tryGetCacheTile(String key) {
        return cache.getIfPresent(key);
    }


}
