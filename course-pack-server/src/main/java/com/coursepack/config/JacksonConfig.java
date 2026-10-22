package com.coursepack.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * Jackson java.time 序列化格式：LocalDateTime 统一输出 yyyy-MM-dd HH:mm:ss。
 * （spring.jackson.date-format 只作用于 java.util.Date，LocalDateTime 需单独注册）
 * 前端各端 slice/显示逻辑按该格式实现。
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer javaTimeFormat() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return builder -> builder.serializers(new LocalDateTimeSerializer(fmt));
    }
}
