package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.entity.TimelineLog;
import com.coursepack.mapper.TimelineLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 最新动态时间线（审核/教师状态操作写入）
 */
@Service
public class TimelineService {

    private final TimelineLogMapper timelineLogMapper;

    public TimelineService(TimelineLogMapper timelineLogMapper) {
        this.timelineLogMapper = timelineLogMapper;
    }

    public void log(String action, String content, Long operatorId) {
        TimelineLog t = new TimelineLog();
        t.setAction(action);
        t.setContent(content);
        t.setOperatorId(operatorId);
        t.setCreatedAt(LocalDateTime.now());
        timelineLogMapper.insert(t);
    }

    public List<TimelineLog> latest(int limit) {
        return timelineLogMapper.selectList(
                Wrappers.<TimelineLog>lambdaQuery()
                        .orderByDesc(TimelineLog::getCreatedAt)
                        .last("LIMIT " + limit));
    }
}