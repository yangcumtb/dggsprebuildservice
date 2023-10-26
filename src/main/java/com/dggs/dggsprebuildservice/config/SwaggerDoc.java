package com.dggs.dggsprebuildservice.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerDoc {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .host("localhost")
                .enable(true)//为true可以访问 false不能访问
                .select()//通过.select()方法，去配置扫描接口
                .apis(RequestHandlerSelectors.basePackage("com.dggs.dggsprebuildservice.controller"))//RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档")
                .description("全球离散格网（DGGS）于构建多分辨率服务接口文档")
                .contact(new Contact("杨文杰", "/dggs", "yang216099@163.com"))
                .licenseUrl("http://39.101.184.20:8001")
                .version("v1.0")
                .build();
    }
}
