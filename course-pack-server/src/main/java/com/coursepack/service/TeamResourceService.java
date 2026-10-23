package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.dto.admin.ResourceVO;
import com.coursepack.dto.admin.SeedHonorVO;
import com.coursepack.dto.admin.TeamVO;
import com.coursepack.entity.Resource;
import com.coursepack.entity.SeedHonor;
import com.coursepack.entity.TeacherTraining;
import com.coursepack.entity.TeachingCase;
import com.coursepack.entity.Team;
import com.coursepack.entity.TeamMember;
import com.coursepack.entity.Training;
import com.coursepack.entity.User;
import com.coursepack.mapper.ResourceMapper;
import com.coursepack.mapper.SeedHonorMapper;
import com.coursepack.mapper.TeacherTrainingMapper;
import com.coursepack.mapper.TeachingCaseMapper;
import com.coursepack.mapper.TeamMapper;
import com.coursepack.mapper.TeamMemberMapper;
import com.coursepack.mapper.TrainingMapper;
import com.coursepack.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 队伍 / 资源 / 种子教师荣誉服务（批次J4）。
 * 成员必修进度：user 关联行走 teacher_training 实时聚合；种子演示行用 seed_done。
 */
@Service
public class TeamResourceService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TeamMapper teamMapper;
    private final TeamMemberMapper memberMapper;
    private final ResourceMapper resourceMapper;
    private final SeedHonorMapper honorMapper;
    private final TrainingMapper trainingMapper;
    private final TeacherTrainingMapper progressMapper;
    private final TeachingCaseMapper caseMapper;
    private final UserMapper userMapper;
    private final TimelineService timelineService;

    public TeamResourceService(TeamMapper teamMapper, TeamMemberMapper memberMapper,
                               ResourceMapper resourceMapper, SeedHonorMapper honorMapper,
                               TrainingMapper trainingMapper, TeacherTrainingMapper progressMapper,
                               TeachingCaseMapper caseMapper, UserMapper userMapper,
                               TimelineService timelineService) {
        this.teamMapper = teamMapper;
        this.memberMapper = memberMapper;
        this.resourceMapper = resourceMapper;
        this.honorMapper = honorMapper;
        this.trainingMapper = trainingMapper;
        this.progressMapper = progressMapper;
        this.caseMapper = caseMapper;
        this.userMapper = userMapper;
        this.timelineService = timelineService;
    }

    /* ================= 队伍 ================= */

    /** 队伍全量（成员必修进度实时聚合 / 种子行取 seed_done） */
    public List<TeamVO> adminTeams() {
        List<Team> teams = teamMapper.selectList(Wrappers.<Team>lambdaQuery().orderByAsc(Team::getId));
        List<Long> requiredIds = requiredTrainingIds();
        Map<Long, Long> requiredDoneByUser = progressMapper.selectList(
                        Wrappers.<TeacherTraining>lambdaQuery().eq(TeacherTraining::getProgress, 100))
                .stream()
                .filter(p -> requiredIds.contains(p.getTrainingId()))
                .collect(Collectors.groupingBy(TeacherTraining::getUserId, Collectors.counting()));
        int requiredTotal = requiredIds.size();
        Map<Long, List<TeamMember>> byTeam = memberMapper.selectList(
                        Wrappers.<TeamMember>lambdaQuery().orderByAsc(TeamMember::getSort))
                .stream()
                .collect(Collectors.groupingBy(TeamMember::getTeamId));

        return teams.stream().map(t -> {
            TeamVO vo = new TeamVO();
            vo.setId(t.getId());
            vo.setName(t.getName());
            vo.setOrg(t.getOrg());
            vo.setArea(t.getArea());
            vo.setUsage(t.getUsageCount());
            vo.setMembers(byTeam.getOrDefault(t.getId(), List.of()).stream()
                    .map(m -> toMemberRow(m, requiredDoneByUser, requiredTotal))
                    .collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
    }

    private TeamVO.MemberRow toMemberRow(TeamMember m, Map<Long, Long> requiredDoneByUser,
                                         int requiredTotal) {
        TeamVO.MemberRow r = new TeamVO.MemberRow();
        r.setName(m.getName());
        r.setRole(m.getRole() == null ? "成员" : m.getRole());
        r.setRequiredTotal(requiredTotal);
        if (m.getUserId() != null) {
            r.setRequiredDone(requiredDoneByUser.getOrDefault(m.getUserId(), 0L).intValue());
        } else {
            r.setRequiredDone(m.getSeedDone() == null ? 0 : m.getSeedDone());
        }
        return r;
    }

    private List<Long> requiredTrainingIds() {
        return trainingMapper.selectList(Wrappers.<Training>lambdaQuery()
                        .eq(Training::getKind, "required"))
                .stream().map(Training::getId).collect(Collectors.toList());
    }

    /* ================= 资源 ================= */

    public List<ResourceVO> adminResources() {
        return resourceMapper.selectList(Wrappers.<Resource>lambdaQuery()
                        .orderByAsc(Resource::getId)).stream()
                .map(this::toResourceVO)
                .collect(Collectors.toList());
    }

    /** 下载资源（次数 +1，写时间线） */
    @Transactional
    public ResourceVO download(Long id, Long operatorId) {
        Resource r = resourceMapper.selectById(id);
        if (r == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        r.setDownloads(r.getDownloads() + 1);
        r.setUpdatedAt(LocalDateTime.now());
        resourceMapper.updateById(r);
        timelineService.log("RESOURCE_DOWNLOADED", "已下载资源「" + r.getTitle() + "」", operatorId);
        return toResourceVO(r);
    }

    private ResourceVO toResourceVO(Resource r) {
        ResourceVO vo = new ResourceVO();
        vo.setId(r.getId());
        vo.setTitle(r.getTitle());
        vo.setKind(r.getKind());
        vo.setDownloads(r.getDownloads());
        vo.setSize(r.getSize());
        vo.setUpdatedAt(r.getUpdatedAt() == null ? "" : DATE.format(r.getUpdatedAt()));
        return vo;
    }

    /* ================= 种子教师荣誉 ================= */

    public List<SeedHonorVO> adminSeedHonors() {
        Map<Long, String> teamNameById = teamMapper.selectList(Wrappers.emptyWrapper()).stream()
                .collect(Collectors.toMap(Team::getId, Team::getName));

        List<SeedHonor> honors = honorMapper.selectList(Wrappers.<SeedHonor>lambdaQuery()
                .orderByDesc(SeedHonor::getCertifiedAt));
        List<TeachingCase> allCases = caseMapper.selectList(Wrappers.emptyWrapper());
        Map<Long, List<TeachingCase>> byUploader = allCases.stream()
                .collect(Collectors.groupingBy(TeachingCase::getUploaderId));
        Map<Long, String> teamByUser = memberMapper.selectList(
                        Wrappers.<TeamMember>lambdaQuery().isNotNull(TeamMember::getUserId))
                .stream()
                .collect(Collectors.toMap(TeamMember::getUserId,
                        m -> teamNameById.getOrDefault(m.getTeamId(), "未分配"),
                        (a, b) -> a));

        return honors.stream().map(h -> {
            SeedHonorVO vo = new SeedHonorVO();
            vo.setId(h.getId());
            User u = userMapper.selectById(h.getUserId());
            vo.setName(u == null || u.getName() == null ? "教师" : u.getName());
            vo.setTeam(teamByUser.getOrDefault(h.getUserId(), "未分配"));
            vo.setCertifiedAt(h.getCertifiedAt() == null ? "" : DATE.format(h.getCertifiedAt()));
            List<TeachingCase> mine = byUploader.getOrDefault(h.getUserId(), List.of());
            vo.setCaseCount(mine.size());
            vo.setApprovedCount((int) mine.stream()
                    .filter(c -> "APPROVED".equals(c.getStatus())).count());
            vo.setFeaturedCount((int) mine.stream()
                    .filter(c -> c.getFeatured() != null && c.getFeatured() > 0).count());
            return vo;
        }).collect(Collectors.toList());
    }
}
