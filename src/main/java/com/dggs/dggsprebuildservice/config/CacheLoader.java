package com.dggs.dggsprebuildservice.config;

import com.dggs.dggsprebuildservice.model.Hexagon.VecterModel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;
import io.swagger.models.auth.In;
import org.gdal.gdal.gdal;
import org.gdal.ogr.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 本地缓存
 *
 */
public class CacheLoader {

    public CacheLoader() throws IOException {
    }


    private static Cache<Long, VecterModel> cache = Caffeine.newBuilder()
            .maximumSize(10000000000L) // 设置缓存的最大大小
            .build();


    @PreDestroy
    public void destroy() throws Exception {
        cache.invalidateAll();
    }

    /**
     * 预缓存前0-8级别数据
     */
    public static void preloadTilesToCache(String cacheShpTilePath, int maxRes) throws IOException {
        for (int i = 0; i <= maxRes; i++) {
            long buildstart = new Date().getTime();
            List<Long> nowList = getShpMulitRes(cacheShpTilePath, i);
            if (nowList == null) {
                return;
            }
            long buildend = new Date().getTime();

            System.out.println("第" + i + "层级构建完成!用时" + (buildend - buildstart) + "毫秒");
            long savaStart = new Date().getTime();
            System.out.println("第" + i + "层级存储中...（共计" + nowList.size() + "个网格)");
            for (int j = 0; j < nowList.size(); j++) {
                VecterModel vecterModel = new VecterModel();
                cache.put(nowList.get(j), vecterModel);
            }
            long saveEnd = new Date().getTime();

            System.out.println("第" + i + "层级存储完成！用时" + (saveEnd - savaStart) + "毫秒");
        }
    }

    /**
     * 将shpfile转换成pointlist
     */
    public static List<Long> getShpMulitRes(String path, int res) throws IOException {
        List<List<LatLng>> holes = new ArrayList<>();
        List<LatLng> points = new ArrayList<>();

        ShapefileDataStore dataStore = new ShapefileDataStore(new File(path).toURI().toURL());
        dataStore.setCharset(Charset.forName("UTF-8"));

        String typeName = dataStore.getTypeNames()[0];
        SimpleFeatureType schema = dataStore.getSchema(typeName);

        FeatureIterator<SimpleFeature> iterator = dataStore.getFeatureSource(typeName).getFeatures().features();

        try {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();

                // 获取几何对象
                org.locationtech.jts.geom.Geometry geometry = (org.locationtech.jts.geom.Geometry) feature.getDefaultGeometry();

                // 检查几何类型，确保是多边形
                if (geometry != null && geometry.getGeometryType().equalsIgnoreCase("MultiPolygon")) {
                    // 处理 MultiPolygon
                    for (int i = 0; i < geometry.getNumGeometries(); i++) {
                        org.locationtech.jts.geom.Polygon polygon = (org.locationtech.jts.geom.Polygon) geometry.getGeometryN(i);
                        // 获取 Polygon 的坐标点
                        org.locationtech.jts.geom.Coordinate[] coordinates = polygon.getCoordinates();
                        for (int j = 0; j < coordinates.length; j++) {
                            double x = coordinates[j].getX();
                            double y = coordinates[j].getY();
                            points.add(new LatLng(y, x));
                        }
                    }
                }
            }
        } finally {
            iterator.close();
            dataStore.dispose();
        }
//        points.add(new LatLng(0.1, 0.5));
//        points.add(new LatLng(0.2, 0.5));
//        points.add(new LatLng(0.2, 0.6));
//        points.add(new LatLng(0.1, 0.6));
//        points.add(new LatLng(0.1, 0.5));

        if (points.size() != 0) {
            H3Core h3 = H3Core.newInstance();
            List<Long> result = h3.polygonToCells(points, null, res);
            return result;
        }
        return null;

    }

    /**
     * 根据key来获取已经缓存的瓦片
     *
     * @param key 层级+列号+行号，字符串拼接
     * @return
     */
    public static VecterModel tryGetCacheTile(Long key) {
        return cache.getIfPresent(key);
    }


}
