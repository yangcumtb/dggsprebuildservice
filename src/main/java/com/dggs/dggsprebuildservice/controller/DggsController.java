package com.dggs.dggsprebuildservice.controller;

//import com.dggs.dggsprebuildservice.config.CustomDeSerializer;

import com.dggs.dggsprebuildservice.config.CacheLoader;
import com.dggs.dggsprebuildservice.model.*;
import com.dggs.dggsprebuildservice.model.Hexagon.Hexagon;
import com.dggs.dggsprebuildservice.model.Hexagon.VecterModel;
import com.dggs.dggsprebuildservice.server.DggsService;
import com.uber.h3core.H3Core;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        return ResponseData.success(h3.getResolution(635881305580465599L));
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
            SpatialData cor = dggsService.getRgb(h3.stringToH3(hexagon.getCode()));
            if (cor != null) {
                List<Integer> colors = new ArrayList<>();
                colors.add(cor.getRed());
                colors.add(cor.getGreen());
                colors.add(cor.getBlue());
                colors.add(255);
                hexagon.setColor(colors);
            }
            System.out.println(h3.stringToH3(hexagon.getCode()));

        });
        return hexagonList;
    }

//    @PostMapping("/getShpArea")
//    @ApiOperation("获取渲染数据")
//    public List<Hexagon> getShpData(@RequestBody List<Hexagon> hexagonList) throws IOException {
//        H3Core h3 = H3Core.newInstance();
//
//        hexagonList.forEach(hexagon -> {
//            VecterModel vecterModel = CacheLoader.tryGetCacheTile(h3.stringToH3(hexagon.getCode()));
//            if (vecterModel != null) {
//                List<Integer> colors = new ArrayList<>();
//                colors.add(159);
//                colors.add(187);
//                colors.add(115);
//                colors.add(180);
//                hexagon.setColor(colors);
//            }
//        });
//        return hexagonList;
//    }

    @PostMapping("/getShpArea")
    @ApiOperation("获取渲染数据")
    public List<String> getShpData(@RequestBody List<String> requestdata) throws IOException {
        H3Core h3 = H3Core.newInstance();
        List<String> responseData = new ArrayList<>();

        requestdata.forEach(hexagon -> {
//            VecterModel vecterModel = CacheLoader.tryGetCacheTile(h3.stringToH3(hexagon));
            VecterModel vecterModel = dggsService.tryGetCacheTile(h3.stringToH3(hexagon));

            if (vecterModel != null) {
                responseData.add(hexagon);
            }
        });
        return responseData;
    }


    @Delete("/delete")
    @ApiOperation("清除图层缓存")
    public ResponseData clearCache(String layerId) {
        return ResponseData.success();
    }


}
