package com.coursepack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 课程包资源站后端启动类
 *
 * @SpringBootApplication 包含三个注解：
 *   @SpringBootConfiguration  — 标记为配置类
 *   @EnableAutoConfiguration  — 自动配置 Spring Boot
 *   @ComponentScan            — 扫描 com.coursepack 包下的所有组件
 *
 * @EnableAsync — 开启异步支持（Phase 6 AI 对话会用到）
 */
@SpringBootApplication
@EnableAsync
public class CoursePackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoursePackApplication.class, args);
    }

}
