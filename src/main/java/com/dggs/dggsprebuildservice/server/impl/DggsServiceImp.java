package com.dggs.dggsprebuildservice.server.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dggs.dggsprebuildservice.config.CacheLoader;
import com.dggs.dggsprebuildservice.config.GDALInitializer;
import com.dggs.dggsprebuildservice.mapper.H3ChengNanjiedaoMapper;
import com.dggs.dggsprebuildservice.model.BuildParam;
import com.dggs.dggsprebuildservice.model.SpatialData;
import com.dggs.dggsprebuildservice.server.DggsService;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;
import org.gdal.gdal.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Service
public class DggsServiceImp extends ServiceImpl<H3ChengNanjiedaoMapper, SpatialData> implements DggsService {

//    @Resource
//    private H3ChengNanjiedaoMapper h3ChengNanjiedao;

    private String cellResampleFile = "/Users/yang/Documents/javaproject/tifdata/cellResamplefile";

    /**
     * 预构建方法
     *
     * @param buildParam 构建参数
     * @throws IOException
     */
    @Override
    public void preBuild(BuildParam buildParam) throws IOException {
        GDALInitializer.initialize();
        H3Core h3 = H3Core.newInstance();
        Dataset dataset = gdal.Open(buildParam.getFilePath());
        double[] geoParam = dataset.GetGeoTransform();

        List<LatLng> outBox = new ArrayList<>();
        // 左上角
        LatLng topLeft = new LatLng(geoParam[3], geoParam[0]);
        outBox.add(topLeft);

        // 右上角
        LatLng topRight = new LatLng(geoParam[3], geoParam[0] + geoParam[1] * dataset.getRasterXSize());
        outBox.add(topRight);

        // 右下角
        LatLng bottomRight = new LatLng(geoParam[3] + geoParam[5] * dataset.getRasterYSize(), geoParam[0] + geoParam[1] * dataset.getRasterXSize());
        outBox.add(bottomRight);

        // 左下角
        LatLng bottomLeft = new LatLng(geoParam[3] + geoParam[5] * dataset.getRasterYSize(), geoParam[0]);
        outBox.add(bottomLeft);

        outBox.add(topLeft);
        List<Long> res = h3.polygonToCells(outBox, null, 0);

        for (int i = 1; i < buildParam.getResolution(); i++) {
            List<Long> newlist = h3.polygonToCells(outBox, null, i);
            res.addAll(newlist);
        }

        dataset.delete();
        for (Long code : res) {
            //根据编码获取经纬度范围
            Collection<Long> longCollection = new LinkedList<>();
            longCollection.add(code);
            List<List<List<LatLng>>> cellarea = h3.cellsToMultiPolygon(longCollection, true);

            double[] cellAreaLonlat = this.getCellArea(cellarea);

            SpatialData spatialData = reSample(buildParam.getFilePath(), cellAreaLonlat, "near", cellResampleFile + File.separator + code + ".tif");

            spatialData.setId(code);

            Dataset cellPoint = gdal.Open(spatialData.getResampleFile());

            //获取red
            Band red = cellPoint.GetRasterBand(1);
            int[] pixelValue = new int[1];
            red.ReadRaster(0, 0, 1, 1, pixelValue);
            spatialData.setRed(pixelValue[0]);
            //第二波段
            Band green = cellPoint.GetRasterBand(2);
            green.ReadRaster(0, 0, 1, 1, pixelValue);
            spatialData.setGreen(pixelValue[0]);
            //第三波段
            Band blue = cellPoint.GetRasterBand(3);
            blue.ReadRaster(0, 0, 1, 1, pixelValue);
            spatialData.setBlue(pixelValue[0]);
            cellPoint.delete();
            this.getBaseMapper().insert(spatialData);
            new File(spatialData.getResampleFile()).delete();
        }

    }


    /**
     * 根据网格经纬度坐标，获取四至范围，用于重采样
     *
     * @param cellArea 格网范围
     * @return
     */
    public double[] getCellArea(List<List<List<LatLng>>> cellArea) {
        double minX = 180.0;
        double maxX = -180.0;
        double minY = 90.0;
        double maxY = -90.0;
        for (List<List<LatLng>> firstLevel : cellArea) {
            for (List<LatLng> secondLevel : firstLevel) {
                for (LatLng latLng : secondLevel) {
                    if (latLng.lat > maxY) {
                        maxY = latLng.lat;
                    }
                    if (latLng.lat < minY) {
                        minY = latLng.lat;
                    }
                    if (latLng.lng > maxX) {
                        maxX = latLng.lng;
                    }
                    if (latLng.lng < minX) {
                        minX = latLng.lng;
                    }
                }
            }
        }

        return new double[]{minX, maxX, maxY, minY};
    }

    /**
     * 格网单元的重采样
     *
     * @param inputFile 数据集
     * @param cellArea  网格区域
     * @return
     */
    public SpatialData reSample(String inputFile, double[] cellArea, String method, String outputFile) {
        try {
            // 设置.exe文件路径
            //linux系统路径
            String exePath = "gdalwarp";
            // 设置命令参数
            String[] command = {
                    exePath,
                    "-r",
                    method,
                    "-te",
                    String.valueOf(cellArea[0]),
                    String.valueOf(cellArea[3]),
                    String.valueOf(cellArea[1]),
                    String.valueOf(cellArea[2]),
                    "-ts",
                    String.valueOf(1),
                    String.valueOf(1),
                    inputFile,
                    outputFile
            };
            // 创建进程并执行命令
            System.out.println("正在执行重采样，采样方式：" + method);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            // 获取进程的标准输出流
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // 读取并打印进程的实时日志
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        SpatialData spatialData = new SpatialData();
        spatialData.setResampleFile(outputFile);
        return spatialData;
    }

    @Override
    public SpatialData getRgb(Long code) {
        return this.getBaseMapper().selectById(code);
    }

    /**
     * 构建矢量
     *
     * @param buildParam 构建参数
     * @throws IOException
     */
    @Override
    public void preBuildPyh(BuildParam buildParam) {
        CacheLoader.preloadTilesToCache(buildParam.getFilePath(), buildParam.getResolution());
    }


}
