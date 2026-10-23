package com.coursepack.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.entity.TeachingCase;
import com.coursepack.entity.TeachingCaseReview;
import com.coursepack.mapper.TeachingCaseMapper;
import com.coursepack.mapper.TeachingCaseReviewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 教学案例种子（批次J，幂等）：8 条与 Web 端 mock/seed.ts 同构
 * （6 APPROVED + 2 PENDING 带已完成环节的审核记录，供管理端详情展示）。
 */
@Slf4j
@Component
@Order(140)
public class CaseSeeder implements CommandLineRunner {

    private static final DateTimeFormatter D = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter T = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TeachingCaseMapper caseMapper;
    private final TeachingCaseReviewMapper reviewMapper;

    public CaseSeeder(TeachingCaseMapper caseMapper, TeachingCaseReviewMapper reviewMapper) {
        this.caseMapper = caseMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public void run(String... args) {
        Long any = caseMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }

        // 1 王春霞 teacher01
        long c1 = seedCase(1L, "《我的身体我做主》课堂实录与反思", "小学4-6年级", "身体与安全",
                "《我的身体，我做主》", "2026-08-28", "APPROVED", true,
                "四年级 40 分钟完整课堂实录，含「身体红绿灯」游戏的分步组织方法与三种突发情况应对。");
        review(c1, "EDUCATION", "教学目标清晰，环节完整，适合直接复用。", "李文静（教育顾问）", "2026-08-29 10:20");
        review(c1, "PSYCHOLOGY", "身体保护表述科学，无恐吓性语言。", "王雅雯（心理督导）", "2026-08-29 16:45");
        review(c1, "SOCIOLOGY", "内容符合乡村课堂实际，材料成本低。", "郑海燕（社工督导）", "2026-08-30 09:10");
        review(c1, "PRINCIPAL", "通过，建议作为示范案例推广。", "陈明德（校长）", "2026-08-30 15:00");

        // 2 周雨桐 teacher02
        long c2 = seedCase(2L, "《我们的家庭》绘本共读教案改编", "小学1-3年级", "家庭与合作",
                "《我们的家庭，不一样也很棒》", "2026-08-25", "APPROVED", true,
                "把原绘本共读改编为三代家庭访谈任务单，学生带回家庭作业后再回课堂分享，附访谈提纲。");
        review(c2, "EDUCATION", "家庭访谈设计巧妙，家校联动自然。", "李文静（教育顾问）", "2026-08-26 11:00");
        review(c2, "PSYCHOLOGY", "注意了单亲/留守家庭的表述敏感性，处理得当。", "王雅雯（心理督导）", "2026-08-26 17:30");
        review(c2, "SOCIOLOGY", "任务单工作量适中，符合农村家庭实际。", "郑海燕（社工督导）", "2026-08-27 09:40");
        review(c2, "PRINCIPAL", "通过。", "陈明德（校长）", "2026-08-27 14:20");

        // 3 刘志强 teacher03
        long c3 = seedCase(3L, "反霸凌情景剧：三幕课堂设计", "小学4-6年级", "人际与尊重",
                "《我们的朋友，我们的约定》", "2026-08-22", "APPROVED", false,
                "旁观者视角的三幕情景剧剧本，含角色卡、舞台提示与演后引导话术，60 分钟完整流程。");
        review(c3, "EDUCATION", "剧本冲突设计合理，引导话术专业。", "李文静（教育顾问）", "2026-08-23 10:05");
        review(c3, "PSYCHOLOGY", "旁观者介入策略符合校园心理干预规范。", "王雅雯（心理督导）", "2026-08-23 15:15");
        review(c3, "SOCIOLOGY", "通过。", "郑海燕（社工督导）", "2026-08-24 09:30");
        review(c3, "PRINCIPAL", "通过，建议配一段排练花絮。", "陈明德（校长）", "2026-08-24 16:50");

        // 4 孙晓梅 teacher04
        long c4 = seedCase(4L, "《我会表达拒绝》低龄化改编实践", "小学1-3年级", "身体与安全",
                "《我会表达拒绝》", "2026-08-20", "APPROVED", false,
                "面向 1-2 年级把角色扮演简化为「点头/摇头」游戏，附方言版引导词，适合低龄段课堂。");
        review(c4, "EDUCATION", "低龄化改编到位，游戏规则简单清晰。", "李文静（教育顾问）", "2026-08-21 10:40");
        review(c4, "PSYCHOLOGY", "通过。", "王雅雯（心理督导）", "2026-08-21 14:25");
        review(c4, "SOCIOLOGY", "方言版引导词很接地气，值得推广。", "郑海燕（社工督导）", "2026-08-22 09:15");
        review(c4, "PRINCIPAL", "通过。", "陈明德（校长）", "2026-08-22 15:35");

        // 5 王春霞 teacher01
        long c5 = seedCase(1L, "家校沟通手册：沂水家访 12 例", "小学1-3年级", "家庭与合作",
                "《我们的家庭，不一样也很棒》", "2026-08-15", "APPROVED", false,
                "12 个真实家访案例的沟通要点复盘，覆盖留守、隔代抚养、单亲三类家庭场景。");
        review(c5, "EDUCATION", "案例真实，复盘有方法沉淀。", "李文静（教育顾问）", "2026-08-16 10:10");
        review(c5, "PSYCHOLOGY", "家庭信息已充分脱敏。", "王雅雯（心理督导）", "2026-08-16 15:20");
        review(c5, "SOCIOLOGY", "通过。", "郑海燕（社工督导）", "2026-08-17 09:00");
        review(c5, "PRINCIPAL", "通过，建议纳入新教师培训材料。", "陈明德（校长）", "2026-08-17 14:45");

        // 6 王春霞 teacher01
        long c6 = seedCase(1L, "课堂破冰游戏 20 式（附物料清单）", "小学1-3年级", "综合主题",
                "《安全小卫士》", "2026-08-10", "APPROVED", false,
                "按「场地/人数/物料」三维分类的 20 个破冰游戏速查表，每个附 3 分钟讲解脚本。");
        review(c6, "EDUCATION", "速查表实用，分类维度清晰。", "李文静（教育顾问）", "2026-08-11 10:30");
        review(c6, "PSYCHOLOGY", "通过。", "王雅雯（心理督导）", "2026-08-11 14:10");
        review(c6, "SOCIOLOGY", "物料均为乡村常见用品，可落地。", "郑海燕（社工督导）", "2026-08-12 09:20");
        review(c6, "PRINCIPAL", "通过。", "陈明德（校长）", "2026-08-12 15:00");

        // 7 刘志强 teacher03（待审，2 环节）
        long c7 = seedCase(3L, "《生命的四季》主题班会全流程", "小学4-6年级", "成长与变化",
                "《变化中的我与我》", "2026-09-03", "PENDING", false,
                "以四季轮转类比生命周期的主题班会设计，含 4 个观察任务与生命教育绘本延伸书单。");
        review(c7, "EDUCATION", "结构完整，观察任务设计有层次。", "李文静（教育顾问）", "2026-09-04 10:15");
        review(c7, "PSYCHOLOGY", "生命周期表述科学，建议补充哀伤情绪的引导预案。", "王雅雯（心理督导）", "2026-09-04 16:30");

        // 8 周雨桐 teacher02（待审，1 环节）
        long c8 = seedCase(2L, "乡村小学性教育问答实录", "小学4-6年级", "身体与安全",
                "《我会表达拒绝》", "2026-09-05", "PENDING", false,
                "五年级课堂 23 个真实提问与应答实录，附「可回应/需转介」分类判断表。");
        review(c8, "EDUCATION", "问答实录价值高，分类判断表实用，建议补充转介渠道清单。", "李文静（教育顾问）", "2026-09-05 17:20");

        log.info("[seed] 创建教学案例种子 8 条");
    }

    private long seedCase(Long uploaderId, String title, String grade, String theme,
                          String courseTitle, String createdAt, String status, boolean featured,
                          String summary) {
        TeachingCase c = new TeachingCase();
        c.setUploaderId(uploaderId);
        c.setTitle(title);
        c.setGrade(grade);
        c.setTheme(theme);
        c.setCourseTitle(courseTitle);
        c.setSummary(summary);
        c.setStatus(status);
        c.setFeatured(featured ? 1 : 0);
        c.setCreatedAt(LocalDate.parse(createdAt, D).atTime(10, 0));
        c.setUpdatedAt(LocalDate.parse(createdAt, D).atTime(10, 0));
        caseMapper.insert(c);
        return c.getId();
    }

    private void review(long caseId, String stage, String comment, String reviewer, String time) {
        TeachingCaseReview r = new TeachingCaseReview();
        r.setCaseId(caseId);
        r.setStage(stage);
        r.setComment(comment);
        r.setReviewer(reviewer);
        r.setReviewedAt(LocalDateTime.parse(time, T));
        reviewMapper.insert(r);
    }
}
