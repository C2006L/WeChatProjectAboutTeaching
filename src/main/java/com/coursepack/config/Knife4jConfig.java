package com.coursepack.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Knife4j API 文档配置
 *
 * 访问地址：http://localhost:8080/doc.html
 *
 * Knife4j 是 Swagger 的增强 UI，提供：
 *   - 中文档界面
 *   - 接口搜索和排序
 *   - 在线调试
 *   - 离线文档导出
 *
 * 为什么用 Knife4j 而非原生 Swagger？
 *   - UI 更友好，中文支持好
 *   - 调试体验更佳
 *   - 支持 JWT Token 全局认证
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    /**
     * API 文档分组配置
     * Docket 是 Swagger 的核心配置类
     *
     * @return Docket 实例
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.coursepack.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(singletonList(apiKey()))
                .securityContexts(singletonList(securityContext()));
    }

    /**
     * API 文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("课程包资源站 API 文档")
                .description("支教老师培训平台后端接口 — 支持小程序端和管理后台")
                .version("v1.0.0")
                .contact(new springfox.documentation.service.Contact(
                        "C2006L",
                        "https://gitee.com/C2006L",
                        ""))
                .build();
    }

    /**
     * JWT Token 认证配置
     * 在 Knife4j 页面右上角可以输入 Token，所有接口自动携带
     */
    private SecurityScheme apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    /**
     * 安全上下文：定义哪些路径需要认证
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api/(wx|admin)/.*"))
                .build();
    }

    /**
     * 默认认证引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        return singletonList(new SecurityReference("Authorization", new AuthorizationScope[]{authorizationScope}));
    }

}
