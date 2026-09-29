package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.entity.LessonDraft;
import com.coursepack.mapper.LessonDraftMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 备课草稿（支撑"电脑备课、手机续写"：两端读写同一张表）
 */
@Service
public class LessonDraftService {

    private final LessonDraftMapper draftMapper;

    public LessonDraftService(LessonDraftMapper draftMapper) {
        this.draftMapper = draftMapper;
    }

    /** 取某教师最新草稿（按更新倒序取第一条），无则返回 null */
    public LessonDraft getLatest(Long userId) {
        return draftMapper.selectOne(
                Wrappers.<LessonDraft>lambdaQuery()
                        .eq(LessonDraft::getUserId, userId)
                        .orderByDesc(LessonDraft::getUpdatedAt)
                        .last("LIMIT 1"));
    }

    /** 保存草稿：存在则更新，不存在则新建 */
    public LessonDraft save(LessonDraft draft) {
        LessonDraft existing = getLatest(draft.getUserId());
        if (existing != null) {
            existing.setCourseId(draft.getCourseId());
            existing.setTitle(draft.getTitle());
            existing.setOutline(draft.getOutline());
            existing.setContent(draft.getContent());
            existing.setUpdatedAt(LocalDateTime.now());
            draftMapper.updateById(existing);
            return existing;
        }
        draft.setId(null);
        draft.setCreatedAt(LocalDateTime.now());
        draft.setUpdatedAt(LocalDateTime.now());
        draftMapper.insert(draft);
        return draft;
    }

    public LessonDraft getById(Long id) {
        LessonDraft d = draftMapper.selectById(id);
        if (d == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return d;
    }
}