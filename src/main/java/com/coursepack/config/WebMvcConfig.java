package com.coursepack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * 【学习要点】
 *
 * 1. 什么是 CORS 跨域？
 *    - 浏览器的同源策略：协议、域名、端口三者必须完全一致
 *    - 前端 http://localhost:5173 访问后端 http://localhost:8080 → 端口不同 → 跨域
 *    - 浏览器会拦截跨域请求，需要后端在响应头中声明允许
 *
 * 2. CORS 解决方案原理：
 *    - 后端在响应头中添加 Access-Control-Allow-Origin 等字段
 *    - 浏览器看到这些字段后，允许前端读取响应
 *    - 对于复杂请求（POST + JSON），浏览器会先发 OPTIONS 预检请求
 *
 * 3. 为什么用 allowedOriginPatterns 而不是 allowedOrigins？
 *    - Spring 2.7+ 中，allowedOrigins("*") + allowCredentials(true) 会报错
 *    - 因为浏览器不允许 Origin: * 和 credentials 同时为 true
 *    - allowedOriginPatterns("*") 可以绕过这个限制
 *
 * 4. WebMvcConfigurer 接口：
 *    - Spring MVC 的配置扩展点
 *    - 实现这个接口可以自定义 CORS、拦截器、消息转换器等
 *    - 不需要 @EnableWebMvc（会覆盖 Spring Boot 自动配置）
 *
 * 【面试可能问】
 * Q: 什么是跨域？为什么浏览器要限制跨域？
 * A: 跨域是指浏览器的同源策略限制。协议、域名、端口任一不同即为跨域。
 *    限制跨域是为了防止 CSRF（跨站请求伪造）攻击，保护用户数据安全。
 *
 * Q: CORS 和 JSONP 有什么区别？
 * A: JSONP 只支持 GET 请求，利用 <script> 标签绕过跨域限制；
 *    CORS 是标准方案，支持所有 HTTP 方法，通过响应头声明跨域策略。
 *    现代项目都用 CORS。
 *
 * Q: 简单请求和复杂请求的区别？
 * A: 简单请求：GET/HEAD/POST + 特定 Content-Type，浏览器直接发送。
 *    复杂请求：PUT/DELETE 或 application/json，浏览器先发 OPTIONS 预检请求。
 *    预检通过后才发送真实请求。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * CORS 跨域配置
     *
     * 允许小程序开发工具、本地前端开发服务器访问后端 API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 允许所有来源（开发阶段用，生产环境应指定具体域名）
                .allowedOriginPatterns("*")
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带 Cookie（Session 方案需要）
                .allowCredentials(true)
                // 预检请求缓存时间（秒），减少 OPTIONS 请求
                .maxAge(3600);
    }

    // ==================== 后续阶段会在这里添加拦截器注册 ====================
    // Phase 2 会添加：
    //   registry.addInterceptor(wxJwtInterceptor)
    //           .addPathPatterns("/api/wx/**")
    //           .excludePathPatterns("/api/wx/login", "/api/wx/refresh");
    //
    //   registry.addInterceptor(adminAuthInterceptor)
    //           .addPathPatterns("/api/admin/**");

}
