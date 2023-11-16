package com.dggs.dggsprebuildservice;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@MapperScan("com.dggs.dggsprebuildservice.mapper")
public class DggsprebuildserviceApplication {

    private final static Logger logger = LoggerFactory.getLogger(DggsprebuildserviceApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application =  SpringApplication.run(DggsprebuildserviceApplication.class, args);
        Environment env = application.getEnvironment();
        String contextPath = env.getProperty("server.servlet.context-path");
        if(StringUtils.isNoneEmpty(contextPath) && !"/".equals(contextPath)){
            logger.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Login: \thttp://{}:{}{}/login\n\t" +
                            "----------------------------------------------------------",
                    DggsprebuildserviceApplication.class.getSimpleName(),
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path"));
        }else {
            logger.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Login: \thttp://{}:{}/doc.html\n\t" +
                            "----------------------------------------------------------",
                    DggsprebuildserviceApplication.class.getSimpleName(),
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("server.port"));
        }
    }

}
