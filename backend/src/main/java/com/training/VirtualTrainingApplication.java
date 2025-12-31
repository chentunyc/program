package com.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 虚拟仿真实训教学管理平台 - 启动类
 *
 * @author Training Team
 * @since 2024-01-01
 */
@SpringBootApplication
public class VirtualTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualTrainingApplication.class, args);
        System.out.println("========================================");
        System.out.println("虚拟仿真实训教学管理平台启动成功!");
        System.out.println("接口文档地址: http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}
