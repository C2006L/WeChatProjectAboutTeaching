package com.coursepack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 项目启动测试类
 * 确保 Spring 容器能正常加载所有 Bean
 */
@SpringBootTest
class CoursePackApplicationTests {

    @Test
    void contextLoads() {
        // 如果容器加载失败，此测试会报错
    }

}
