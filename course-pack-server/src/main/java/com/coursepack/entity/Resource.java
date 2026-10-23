package com.coursepack.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源库条目（模板/量表，管理端维护）
 */
@Data
@TableName("resources")
public class Resource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /** 教案模板|PPT模板|评估量表|绘本清单 */
    private String kind;

    private Integer downloads;

    private String size;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
