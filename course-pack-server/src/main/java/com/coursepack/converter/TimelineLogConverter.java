package com.coursepack.converter;

import com.coursepack.dto.admin.TimelineLogVO;
import com.coursepack.entity.TimelineLog;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * TimelineLog 实体 → TimelineLogVO（MapStruct）
 */
@Mapper(componentModel = "spring")
public interface TimelineLogConverter {

    TimelineLogVO toVO(TimelineLog log);

    List<TimelineLogVO> toVOList(List<TimelineLog> logs);
}
