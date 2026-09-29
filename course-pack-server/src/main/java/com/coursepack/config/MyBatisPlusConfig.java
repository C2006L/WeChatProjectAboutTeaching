package com.coursepack.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 *
 * MyBatis-Plus 是 MyBatis 的增强工具，主要特性：
 *   - 内置 CRUD 方法，不用写 XML
 *   - 强大的条件构造器 (QueryWrapper)
 *   - 分页插件 (PaginationInnerInterceptor)
 *   - 逻辑删除支持
 *
 * @MapperScan 扫描 mapper 包下的所有 Mapper 接口，自动生成代理实现类
 */
@Configuration
@MapperScan("com.coursepack.mapper")
public class MyBatisPlusConfig {

    /**
     * 注册分页插件
     *
     * 没有这个配置，MyBatis-Plus 的 Page 对象不会自动追加 LIMIT 语句，
     * 会查出全部数据，导致内存溢出。
     *
     * @return MybatisPlusInterceptor 拦截器链
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
