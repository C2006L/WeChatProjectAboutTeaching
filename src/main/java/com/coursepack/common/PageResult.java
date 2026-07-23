package com.coursepack.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应体
 *
 * 用于封装分页查询结果，与 Result<T> 配合使用：
 *   Result<PageResult<CourseVO>>
 *
 * @param <T> 列表元素类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 快速构建分页结果
     *
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页大小
     * @param pages   总页数
     * @param records 当前页数据
     * @return PageResult 实例
     */
    public static <T> PageResult<T> of(Long total, Long current, Long size, Long pages, List<T> records) {
        return new PageResult<>(total, current, size, pages, records);
    }

}
