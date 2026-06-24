package com.park;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 智慧公园管理平台 - 主启动类
 *
 * @author park-team
 */
@SpringBootApplication
@MapperScan("com.park.*.mapper")
public class ParkServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(ParkServerApplication.class, args);
        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        System.out.println("\n----------------------------------------------------------");
        System.out.println("\t智慧公园管理平台启动成功!");
        System.out.println("\t接口文档地址: http://localhost:" + port + contextPath + "/swagger-ui.html");
        System.out.println("----------------------------------------------------------\n");
    }
}
