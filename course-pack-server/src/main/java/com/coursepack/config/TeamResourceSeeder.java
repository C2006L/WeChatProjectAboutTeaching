package com.coursepack.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.entity.Resource;
import com.coursepack.entity.SeedHonor;
import com.coursepack.entity.Team;
import com.coursepack.entity.TeamMember;
import com.coursepack.entity.User;
import com.coursepack.mapper.ResourceMapper;
import com.coursepack.mapper.SeedHonorMapper;
import com.coursepack.mapper.TeamMapper;
import com.coursepack.mapper.TeamMemberMapper;
import com.coursepack.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 队伍 / 资源 / 种子荣誉种子（批次J4，幂等：各表有数据即跳过）。
 * 数据与管理端 mock seed.ts 同构：6 队伍 / 20 演示成员 / 12 资源 / 4 荣誉。
 * 另将 teacher01–04 以成员身份挂入队伍 1/2/3/5：荣誉的案例数与成员必修进度
 * 走服务端实时聚合（进度来自 teacher_training，必修 5/5、2/5…）。
 */
@Slf4j
@Component
@Order(150)
public class TeamResourceSeeder implements CommandLineRunner {

    private final TeamMapper teamMapper;
    private final TeamMemberMapper memberMapper;
    private final ResourceMapper resourceMapper;
    private final SeedHonorMapper honorMapper;
    private final UserMapper userMapper;

    public TeamResourceSeeder(TeamMapper teamMapper, TeamMemberMapper memberMapper,
                              ResourceMapper resourceMapper, SeedHonorMapper honorMapper,
                              UserMapper userMapper) {
        this.teamMapper = teamMapper;
        this.memberMapper = memberMapper;
        this.resourceMapper = resourceMapper;
        this.honorMapper = honorMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        seedTeams();
        seedResources();
        seedHonors();
    }

    private void seedTeams() {
        Long any = teamMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }
        List<Team> teams = Arrays.asList(
                team("山科大希望之光队", "山东科技大学", "临沂市 沂水县", 36),
                team("青岛大学麦田队", "青岛大学", "菏泽市 单县", 48),
                team("曲师大知行队", "曲阜师范大学", "济宁市 金乡县", 31),
                team("山师大蒲公英队", "山东师范大学", "德州市 平原县", 42),
                team("聊城大学星火队", "聊城大学", "聊城市 东阿县", 28),
                team("临沂大学沂蒙队", "临沂大学", "临沂市 蒙阴县", 19));
        teams.forEach(teamMapper::insert);

        List<Team> saved = teamMapper.selectList(
                Wrappers.<Team>lambdaQuery().orderByAsc(Team::getId));
        List<TeamMember> members = Arrays.asList(
                // 每队成员与 mock 一致（队长在前），sort 递增
                member(saved.get(0).getId(), "王春霞", "队长", 1, 5),
                member(saved.get(0).getId(), "赵子豪", "成员", 2, 5),
                member(saved.get(0).getId(), "林晓彤", "成员", 3, 4),
                member(saved.get(1).getId(), "周雨桐", "队长", 1, 5),
                member(saved.get(1).getId(), "李有田", "成员", 2, 2),
                member(saved.get(1).getId(), "徐海生", "成员", 3, 5),
                member(saved.get(1).getId(), "马兰", "成员", 4, 3),
                member(saved.get(2).getId(), "刘志强", "队长", 1, 5),
                member(saved.get(2).getId(), "张远航", "成员", 2, 2),
                member(saved.get(2).getId(), "杜文静", "成员", 3, 4),
                member(saved.get(3).getId(), "孙晓晴", "队长", 1, 5),
                member(saved.get(3).getId(), "陈雪梅", "成员", 2, 2),
                member(saved.get(3).getId(), "高峰", "成员", 3, 5),
                member(saved.get(3).getId(), "白鹭", "成员", 4, 4),
                member(saved.get(4).getId(), "孙晓梅", "队长", 1, 5),
                member(saved.get(4).getId(), "郭栋", "成员", 2, 3),
                member(saved.get(4).getId(), "韩冰", "成员", 3, 5),
                member(saved.get(5).getId(), "孟祥云", "队长", 1, 4),
                member(saved.get(5).getId(), "尹航", "成员", 2, 2),
                member(saved.get(5).getId(), "彭飞", "成员", 3, 1),
                // teacher01–04 以 user 行挂入队伍：必修进度实时聚合（sort 10 排尾）
                memberWithUser(saved.get(0).getId(), "teacher01", "成员", 10),
                memberWithUser(saved.get(1).getId(), "teacher02", "成员", 10),
                memberWithUser(saved.get(2).getId(), "teacher03", "成员", 10),
                memberWithUser(saved.get(4).getId(), "teacher04", "成员", 10));
        members.forEach(memberMapper::insert);
        log.info("[seed] 创建队伍 {} 支、成员 {} 名", teams.size(), members.size());
    }

    private void seedResources() {
        Long any = resourceMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }
        List<Resource> resources = Arrays.asList(
                resource("生命教育课程教案模板（标准版）", "教案模板", 342, "86 KB", "2026-08-20"),
                resource("班会课教案骨架（40 分钟版）", "教案模板", 218, "52 KB", "2026-08-12"),
                resource("家访记录与沟通要点模板", "教案模板", 156, "48 KB", "2026-07-30"),
                resource("课程封面与目录 PPT 模板", "PPT模板", 289, "3.2 MB", "2026-08-18"),
                resource("互动问答页 PPT 模板", "PPT模板", 173, "1.8 MB", "2026-08-08"),
                resource("结课展示 PPT 模板", "PPT模板", 98, "2.4 MB", "2026-07-22"),
                resource("课堂参与度观察量表", "评估量表", 201, "64 KB", "2026-08-25"),
                resource("学生成长追踪量表（学期版）", "评估量表", 187, "72 KB", "2026-08-15"),
                resource("教师授课自评量表", "评估量表", 143, "45 KB", "2026-07-28"),
                resource("低龄段绘本清单（1-3 年级）", "绘本清单", 265, "38 KB", "2026-08-30"),
                resource("情绪主题绘本书单（附导读）", "绘本清单", 154, "42 KB", "2026-08-22"),
                resource("家庭主题绘本书单", "绘本清单", 132, "36 KB", "2026-08-05"));
        resources.forEach(resourceMapper::insert);
        log.info("[seed] 创建资源 {} 条", resources.size());
    }

    private void seedHonors() {
        Long any = honorMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }
        List<SeedHonor> honors = Arrays.asList(
                honor("teacher01", LocalDate.of(2026, 7, 2)),
                honor("teacher02", LocalDate.of(2026, 6, 18)),
                honor("teacher03", LocalDate.of(2026, 5, 30)),
                honor("teacher04", LocalDate.of(2026, 4, 12)));
        honors.forEach(honorMapper::insert);
        log.info("[seed] 创建种子教师荣誉 {} 条", honors.size());
    }

    private Team team(String name, String org, String area, int usage) {
        Team t = new Team();
        t.setName(name);
        t.setOrg(org);
        t.setArea(area);
        t.setUsageCount(usage);
        return t;
    }

    private TeamMember member(Long teamId, String name, String role, int sort, Integer seedDone) {
        TeamMember m = new TeamMember();
        m.setTeamId(teamId);
        m.setName(name);
        m.setRole(role);
        m.setSort(sort);
        m.setSeedDone(seedDone);
        return m;
    }

    private TeamMember memberWithUser(Long teamId, String username, String role, int sort) {
        User u = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username).last("LIMIT 1"));
        if (u == null) {
            throw new IllegalStateException("[seed] 未找到演示账号 " + username);
        }
        TeamMember m = member(teamId, u.getName(), role, sort, null);
        m.setUserId(u.getId());
        return m;
    }

    private Resource resource(String title, String kind, int downloads, String size, String date) {
        Resource r = new Resource();
        r.setTitle(title);
        r.setKind(kind);
        r.setDownloads(downloads);
        r.setSize(size);
        LocalDateTime at = LocalDate.parse(date).atTime(10, 0);
        r.setCreatedAt(at);
        r.setUpdatedAt(at);
        return r;
    }

    private SeedHonor honor(String username, LocalDate certifiedAt) {
        User u = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username).last("LIMIT 1"));
        if (u == null) {
            throw new IllegalStateException("[seed] 未找到演示账号 " + username);
        }
        SeedHonor h = new SeedHonor();
        h.setUserId(u.getId());
        h.setCertifiedAt(certifiedAt);
        return h;
    }
}
