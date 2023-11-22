package com.dggs.dggsprebuildservice.controller;

//import com.dggs.dggsprebuildservice.config.CustomDeSerializer;

import com.dggs.dggsprebuildservice.config.CacheLoader;
import com.dggs.dggsprebuildservice.model.*;
import com.dggs.dggsprebuildservice.model.Hexagon.H3Request;
import com.dggs.dggsprebuildservice.model.Hexagon.Hexagon;
import com.dggs.dggsprebuildservice.model.Hexagon.VecterModel;
import com.dggs.dggsprebuildservice.server.DggsService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.uber.h3core.H3Core;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@Api("控制器")
@CrossOrigin("*")
@RequestMapping("/Dggs")
public class DggsController {

    @Resource
    DggsService dggsService;

    @GetMapping("/hello")
    @ApiOperation("Hello")
    public String getWorld() {

        return "Hello,this is a dggs prebuild grid services";
    }

    /**
     * 根据经纬度获取对应的h3编码
     *
     * @param lat 纬度
     * @param lng 经度
     * @param res 分辨率
     * @return
     * @throws IOException
     */
    @GetMapping("/getCode")
    @ApiOperation("获取h3编码")
    public ResponseData getCode(@Param("lat") double lat, @Param("lng") double lng, @Param("res") int res) throws IOException {
        H3Core h3 = H3Core.newInstance();
        return ResponseData.success(dggsService.getRgb(599852508923297791L));
    }

    /**
     * 栅格瓦片数据的构建
     *
     * @param buildParam 构建参数
     * @return
     * @throws IOException
     */
    @PostMapping("/prebuild")
    @ApiOperation("预构建多分辨率网格")
    public ResponseData preBuild(@RequestBody BuildParam buildParam) throws IOException {
        dggsService.preBuild(buildParam);
        return ResponseData.success();
    }

    @PostMapping("/prebuildPyh")
    @ApiOperation("预构建多分辨率网格")
    public ResponseData prebuildPyh(@RequestBody BuildParam buildParam) throws IOException {
        dggsService.preBuildPyh(buildParam);
        return ResponseData.success();
    }

    @PostMapping("/getRenderData")
    @ApiOperation("获取渲染数据")
    public List<Hexagon> getRenderData(@RequestBody List<Hexagon> hexagonList) throws IOException {
        H3Core h3 = H3Core.newInstance();

        hexagonList.forEach(hexagon -> {
//            SpatialData cor = dggsService.getRgb(h3.stringToH3(hexagon.getCode()));
//            if (cor != null) {
//                List<Integer> colors = new ArrayList<>();
//                colors.add(cor.getRed());
//                colors.add(cor.getGreen());
//                colors.add(cor.getBlue());
//                colors.add(255);
//                hexagon.setColor(colors);
//            }
            System.out.println(h3.stringToH3(hexagon.getCode()));

        });
        return hexagonList;
    }

    @PostMapping("/getShpArea")
    @ApiOperation("获取渲染数据")
    public List<Hexagon> getShpData(@RequestBody List<Hexagon> hexagonList) throws IOException {
        H3Core h3 = H3Core.newInstance();

        hexagonList.forEach(hexagon -> {
            VecterModel vecterModel = CacheLoader.tryGetCacheTile(hexagon.getCode());
            if (vecterModel != null) {
                List<Integer> colors = new ArrayList<>();
                colors.add(0);
                colors.add(0);
                colors.add(0);
                colors.add(255);
                hexagon.setColor(colors);
            }
        });
        return hexagonList;
    }


}
