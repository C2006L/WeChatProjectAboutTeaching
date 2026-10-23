package com.coursepack.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.common.UserRole;
import com.coursepack.dto.training.TrainingCourseVO;
import com.coursepack.dto.training.TrainingOverviewVO;
import com.coursepack.dto.training.TrainingVO;
import com.coursepack.entity.TeacherTraining;
import com.coursepack.entity.Training;
import com.coursepack.entity.User;
import com.coursepack.mapper.TeacherTrainingMapper;
import com.coursepack.mapper.TrainingMapper;
import com.coursepack.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 培训域服务（批次J）：必修/选修课程 + 教师学习进度。
 * 教师端状态推导：progress>=100 done；0-99 current；未开始：
 * 必修 → current（应学），选修 → lock（必修全部完成后再学）。
 */
@Service
public class TrainingService {

    private final TrainingMapper trainingMapper;
    private final TeacherTrainingMapper recordMapper;
    private final UserMapper userMapper;

    public TrainingService(TrainingMapper trainingMapper, TeacherTrainingMapper recordMapper,
                           UserMapper userMapper) {
        this.trainingMapper = trainingMapper;
        this.recordMapper = recordMapper;
        this.userMapper = userMapper;
    }

    /** 教师端课程列表（含个人进度与状态） */
    public List<TrainingVO> listForUser(Long userId) {
        List<Training> trainings = trainingMapper.selectList(
                Wrappers.<Training>lambdaQuery().orderByAsc(Training::getId));
        Map<Long, Integer> progress = recordByUser(userId);
        boolean requiredDone = allRequiredDone(trainings, progress);
        return trainings.stream()
                .map(t -> toVO(t, progress.getOrDefault(t.getId(), 0), requiredDone))
                .collect(Collectors.toList());
    }

    public TrainingVO getForUser(Long userId, Long trainingId) {
        Training t = trainingMapper.selectById(trainingId);
        if (t == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        Map<Long, Integer> progress = recordByUser(userId);
        boolean requiredDone = allRequiredDone(
                trainingMapper.selectList(Wrappers.emptyWrapper()), progress);
        return toVO(t, progress.getOrDefault(t.getId(), 0), requiredDone);
    }

    /** 播放/学习后上报进度（0-100），upsert */
    @Transactional
    public void updateProgress(Long userId, Long trainingId, Integer progress) {
        if (progress == null || progress < 0 || progress > 100) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "进度需在 0-100 之间");
        }
        Training t = trainingMapper.selectById(trainingId);
        if (t == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        TeacherTraining row = recordMapper.selectOne(Wrappers.<TeacherTraining>lambdaQuery()
                .eq(TeacherTraining::getUserId, userId)
                .eq(TeacherTraining::getTrainingId, trainingId)
                .last("LIMIT 1"));
        if (row == null) {
            row = new TeacherTraining();
            row.setUserId(userId);
            row.setTrainingId(trainingId);
            row.setProgress(progress);
            recordMapper.insert(row);
        } else if (progress > row.getProgress()) {
            row.setProgress(progress);
            recordMapper.updateById(row);
        }
    }

    private TrainingVO toVO(Training t, int progress, boolean requiredDone) {
        TrainingVO vo = new TrainingVO();
        vo.setId(t.getId());
        vo.setTitle(t.getTitle());
        vo.setType(t.getKind());
        vo.setDuration(t.getDuration());
        vo.setProgress(progress);
        vo.setStatus(statusOf(progress, t.getKind(), requiredDone));
        vo.setVideoUrl(t.getVideoUrl() == null ? "" : t.getVideoUrl());
        vo.setIntroduction(t.getIntroduction());
        vo.setOutline(t.getOutlineJson() == null ? List.of()
                : JSONUtil.toList(t.getOutlineJson(), String.class));
        vo.setMaterials(t.getMaterialsJson() == null ? List.of()
                : JSONUtil.toList(t.getMaterialsJson(), TrainingVO.MaterialItem.class));
        return vo;
    }

    private String statusOf(int progress, String kind, boolean requiredDone) {
        if (progress >= 100) return "done";
        if (progress > 0) return "current";
        // 未开始：必修为进行中（应学习）；选修在必修全部完成前锁定
        return "elective".equals(kind) && !requiredDone ? "lock" : "current";
    }

    private Map<Long, Integer> recordByUser(Long userId) {
        return recordMapper.selectList(Wrappers.<TeacherTraining>lambdaQuery()
                        .eq(TeacherTraining::getUserId, userId))
                .stream()
                .collect(Collectors.toMap(TeacherTraining::getTrainingId, TeacherTraining::getProgress));
    }

    private boolean allRequiredDone(List<Training> trainings, Map<Long, Integer> progress) {
        return trainings.stream()
                .filter(t -> "required".equals(t.getKind()))
                .allMatch(t -> progress.getOrDefault(t.getId(), 0) >= 100);
    }

    /* ================= 管理端聚合 ================= */

    /** 培训课程管理行（completedCount 按进度记录实时汇总） */
    public List<TrainingCourseVO> adminCourses() {
        List<Training> trainings = trainingMapper.selectList(
                Wrappers.<Training>lambdaQuery().orderByAsc(Training::getId));
        long teacherCount = countTeachers();
        Map<Long, List<TeacherTraining>> byTraining = allRecords().stream()
                .collect(Collectors.groupingBy(TeacherTraining::getTrainingId));
        return trainings.stream().map(t -> {
            TrainingCourseVO vo = new TrainingCourseVO();
            vo.setId(t.getId());
            vo.setTitle(t.getTitle());
            vo.setType("required".equals(t.getKind()) ? "必修" : "选修");
            vo.setDuration(t.getDuration());
            List<TeacherTraining> recs = byTraining.getOrDefault(t.getId(), List.of());
            if ("required".equals(t.getKind())) {
                vo.setAssignedCount((int) teacherCount);
                vo.setCompletedCount((int) recs.stream().filter(r -> r.getProgress() >= 100).count());
            } else {
                // 选修：有学习记录的教师为应学，完成数为其中 progress>=100
                vo.setAssignedCount(recs.size());
                vo.setCompletedCount((int) recs.stream().filter(r -> r.getProgress() >= 100).count());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /** 培训 overview + 未完成必修教师名单（未完成教师与教师管理口径一致：仅 ACTIVE） */
    public TrainingOverviewVO adminOverview() {
        List<Training> trainings = trainingMapper.selectList(
                Wrappers.<Training>lambdaQuery().orderByAsc(Training::getId));
        List<Training> required = trainings.stream()
                .filter(t -> "required".equals(t.getKind())).collect(Collectors.toList());

        List<User> teachers = userMapper.selectList(Wrappers.<User>lambdaQuery()
                .eq(User::getRole, UserRole.TEACHER.name()));
        Map<Long, List<TeacherTraining>> byUser = allRecords().stream()
                .collect(Collectors.groupingBy(TeacherTraining::getUserId));
        int teacherCount = teachers.size();

        int done = 0, inprogress = 0, requiredTotal = required.size() * teacherCount;
        int doneRequired = 0;
        TrainingOverviewVO vo = new TrainingOverviewVO();
        List<TrainingOverviewVO.UnfinishedRow> unfinished = new java.util.ArrayList<>();
        for (User u : teachers) {
            Map<Long, Integer> progress = byUser
                    .getOrDefault(u.getId(), List.<TeacherTraining>of()).stream()
                    .collect(Collectors.toMap(TeacherTraining::getTrainingId, TeacherTraining::getProgress));
            boolean allDone = required.stream()
                    .allMatch(t -> progress.getOrDefault(t.getId(), 0) >= 100);
            List<String> missing = required.stream()
                    .filter(t -> progress.getOrDefault(t.getId(), 0) < 100)
                    .map(Training::getTitle).collect(Collectors.toList());
            doneRequired += required.size() - missing.size();
            if (allDone) {
                done += 1;
            } else {
                // 未完成名单覆盖全部账号状态；"进行中"统计仅计 ACTIVE
                if ("ACTIVE".equals(u.getStatus()) && missing.size() != required.size()) {
                    inprogress += 1;
                }
                unfinished.add(toUnfinished(u, required.size() - missing.size(), required.size(), missing));
            }
        }
        vo.setRequiredRate(requiredTotal == 0 ? 0
                : Math.round((doneRequired * 100f) / requiredTotal));
        vo.setDoneTeachers(done);
        vo.setInprogressTeachers(inprogress);
        vo.setTeamCount(0); // 批次J4：队伍域建立后按 users 关联 team 聚合
        vo.setUnfinished(unfinished);
        return vo;
    }

    private TrainingOverviewVO.UnfinishedRow toUnfinished(User u, int completed, int requiredCount,
                                                          List<String> missing) {
        TrainingOverviewVO.UnfinishedRow row = new TrainingOverviewVO.UnfinishedRow();
        row.setTeacherId(u.getId());
        row.setName(u.getName());
        row.setTeam("未分配"); // 批次J4：未建立队伍关联前缺省
        row.setStatus(u.getStatus());
        row.setCompletedCount(completed);
        row.setRequiredCount(requiredCount);
        row.setMissing(missing);
        return row;
    }

    private List<TeacherTraining> allRecords() {
        return recordMapper.selectList(Wrappers.emptyWrapper());
    }

    private long countTeachers() {
        return userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getRole, UserRole.TEACHER.name()));
    }
}
