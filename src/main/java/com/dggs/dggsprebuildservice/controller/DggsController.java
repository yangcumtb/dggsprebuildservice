package com.dggs.dggsprebuildservice.controller;

import com.dggs.dggsprebuildservice.model.BuildParam;
import com.dggs.dggsprebuildservice.model.ResponseData;
import com.dggs.dggsprebuildservice.model.SpatialData;
import com.dggs.dggsprebuildservice.server.DggsService;
import com.uber.h3core.H3Core;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@Api("控制器")
@CrossOrigin("*")
@RequestMapping("/Dggs")
public class HelloWord {

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
        return ResponseData.success(h3.latLngToCellAddress(lat, lng, res));
    }

    @PostMapping("/prebuild")
    @ApiOperation("预构建多分辨率网格")
    public ResponseData preBuild() {
        dggsService.preBuild(new SpatialData(), new BuildParam());
        return ResponseData.success();
    }


}
