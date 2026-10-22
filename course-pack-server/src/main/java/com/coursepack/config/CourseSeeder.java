package com.coursepack.config;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.dto.course.CourseStep;
import com.coursepack.entity.Course;
import com.coursepack.entity.CourseMaterial;
import com.coursepack.mapper.CourseMapper;
import com.coursepack.mapper.CourseMaterialMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程域种子（批次H，幂等：courses 表为空时插入）。
 * 8 门生命教育课程与前端 mock/course.ts 完全同构（同 id 同标题 同主题 学段 版本），
 * 使 mock 模式与真实模式页面表现一致；materials 生成对应数量的记录行。
 */
@Slf4j
@Component
@Order(110)
public class CourseSeeder implements CommandLineRunner {

    private final CourseMapper courseMapper;
    private final CourseMaterialMapper materialMapper;

    public CourseSeeder(CourseMapper courseMapper, CourseMaterialMapper materialMapper) {
        this.courseMapper = courseMapper;
        this.materialMapper = materialMapper;
    }

    @Override
    public void run(String... args) {
        Long any = courseMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }
        List<Course> courses = new ArrayList<>();
courses.add(course(1L, "我的身体，我做主", "body", "#4F8F83", "小学4-6年级", 40, "精简版",
                "认识自己的身体，学会表达和保护自己的界限，尊重自己也尊重他人。",
                List.of("认识身体隐私部位与身体界限", "学会用“我愿意/我不愿意”表达感受", "知道遇到不舒服接触时可以告诉信任的成人"),
                List.of("每个孩子都有权利保护自己的身体", "尊重他人从尊重身体边界开始"),
                List.of(1L, 2L, 3L),
                List.of(step(1, "热身活动", "认识身体的不同部位", 10),
                        step(2, "情景讨论", "哪些行为让人不舒服？", 10),
                        step(3, "小组活动", "我的身体界限小卡片", 10),
                        step(4, "分享交流", "我们可以怎么做？", 5),
                        step(5, "价值总结与升华", "每个人都有身体界限，我们要学会尊重", 5))));
courses.add(course(2L, "我们的家庭，不一样也很棒", "family", "#D99A62", "小学1-3年级", 30, "精简版",
                "了解家庭的多样性，学习欣赏不同家庭结构中的爱与支持。",
                List.of("认识不同家庭结构的普遍性", "学会尊重每一种家庭形式", "感受并表达对自己家庭的爱"),
                List.of("家庭有爱最重要", "不同家庭都值得尊重"),
                List.of(6L),
                List.of(step(1, "热身活动", "画出我的家", 8),
                        step(2, "故事导入", "不同形状的家庭", 7),
                        step(3, "小组讨论", "我们家的特别之处", 8),
                        step(4, "分享交流", "爱让我们在一起", 5),
                        step(5, "总结延伸", "给家人的一句话", 2))));
courses.add(course(3L, "说话有力量，尊重每个人", "respect", "#7FA89B", "小学4-6年级", 40, "精简版",
                "学习有效沟通与倾听，理解尊重他人边界的重要性。",
                List.of("学会用“我句式”表达感受", "识别语言中的不尊重信号", "练习积极倾听与回应"),
                List.of("尊重从好好说话开始", "每个人都有被听见的权利"),
                List.of(7L),
                List.of(step(1, "热身活动", "传话游戏感受语言力量", 8),
                        step(2, "情景体验", "“我句式”练习", 10),
                        step(3, "小组活动", "给好朋友的一封信", 10),
                        step(4, "角色扮演", "遇到不尊重怎么办", 8),
                        step(5, "总结升华", "班级尊重公约", 4))));
courses.add(course(4L, "成长的我，很特别", "growth", "#5D9B78", "小学1-3年级", 30, "精简版",
                "认识成长中的变化，建立积极的自我认同。",
                List.of("发现自己在成长中的变化", "欣赏自己的独特之处", "建立积极自我形象"),
                List.of("每个人都是独一无二的", "成长中的变化很正常"),
                List.of(2L, 9L),
                List.of(step(1, "热身活动", "小时候的我 vs 现在的我", 6),
                        step(2, "故事导入", "特别的成长故事", 6),
                        step(3, "小组活动", "我的优点树", 8),
                        step(4, "分享交流", "我为成长骄傲", 6),
                        step(5, "总结延伸", "写给成长后的自己", 4))));
courses.add(course(5L, "我会表达拒绝", "body", "#4F8F83", "小学4-6年级", 45, "标准版",
                "学会在不舒服的情境中清晰说“不”，并寻求可信赖成人的帮助。",
                List.of("识别让人不舒服的情境", "练习坚定地说“不”", "掌握求助渠道与方法"),
                List.of("说“不”是保护自己的权利", "遇到危险要马上求助"),
                List.of(3L, 4L, 8L, 10L),
                List.of(step(1, "热身活动", "身体界限红绿灯", 8),
                        step(2, "情景讨论", "哪些情境需要拒绝", 10),
                        step(3, "角色扮演", "坚定说“不”演练", 12),
                        step(4, "小组活动", "我的求助电话链", 8),
                        step(5, "总结延伸", "班级安全求助墙", 7))));
courses.add(course(6L, "我们的朋友，我们的约定", "respect", "#7FA89B", "小学1-3年级", 35, "标准版",
                "学习友谊中的规则与边界，共建安全的同伴关系。",
                List.of("理解友谊中的相互尊重", "学会共同制定约定", "识别并拒绝朋友间的不当要求"),
                List.of("好朋友互相尊重", "约定让关系更安全"),
                List.of(7L),
                List.of(step(1, "热身活动", "友谊之树", 6),
                        step(2, "故事导入", "小兔子与小狐狸的约定", 7),
                        step(3, "小组活动", "制定我们的友谊约定", 10),
                        step(4, "角色扮演", "约定被打破了怎么办", 8),
                        step(5, "总结延伸", "班级友谊公约签名", 4))));
courses.add(course(7L, "变化中的我与我", "growth", "#5D9B78", "小学4-6年级", 40, "标准版",
                "理解青春期前的身心变化，以科学态度迎接成长。",
                List.of("了解青春期前身体与情绪变化", "以科学态度看待发育差异", "学会自我照顾与情绪调节"),
                List.of("成长变化是自然的旅程", "科学认知帮助我们安心"),
                List.of(2L, 5L, 9L),
                List.of(step(1, "热身活动", "成长变化接龙", 8),
                        step(2, "知识讲解", "身体发育科学小知识", 10),
                        step(3, "小组讨论", "变化带来的感受", 8),
                        step(4, "情境应对", "如何处理尴尬与好奇", 8),
                        step(5, "总结延伸", "我的成长呵护计划", 6))));
courses.add(course(8L, "安全小卫士", "general", "#A8B5A0", "小学1-3年级", 30, "标准版",
                "识别日常安全风险，掌握基本自我保护方法。",
                List.of("识别校园与社区中的安全隐患", "掌握求助与避险基本方法", "强化安全自我保护意识"),
                List.of("安全意识是自我保护的第一步", "遇到危险要冷静求助"),
                List.of(3L, 8L, 10L),
                List.of(step(1, "热身活动", "安全标志大搜索", 5),
                        step(2, "情景判断", "哪些是危险情境", 8),
                        step(3, "小组活动", "设计安全小提示", 8),
                        step(4, "角色扮演", "遇到危险怎么做", 5),
                        step(5, "总结延伸", "我的安全小卫士守则", 4))));

        for (Course c : courses) {
            courseMapper.insert(c);
            int videos = (c.getId() == 5 || c.getId() == 7) ? 2 : 1;
            addMaterials(c.getId(), c.getTitle(), 1, 1, 1, videos);
        }
        log.info("[seed] 创建课程种子 {} 门", courses.size());
    }

    private Course course(Long id, String title, String theme, String cover, String grade, int duration,
                          String version, String summary, List<String> objectives, List<String> values,
                          List<Long> sensitiveQa, List<CourseStep> steps) {
        Course c = new Course();
        c.setId(id);
        c.setTitle(title);
        c.setTheme(theme);
        c.setCover(cover);
        c.setGrade(grade);
        c.setDuration(duration);
        c.setVersion(version);
        c.setSummary(summary);
        c.setObjectivesJson(JSONUtil.toJsonStr(objectives));
        c.setValuesJson(JSONUtil.toJsonStr(values));
        c.setSensitiveQaJson(JSONUtil.toJsonStr(sensitiveQa));
        c.setStepsJson(JSONUtil.toJsonStr(steps));
        c.setStatus(1);
        return c;
    }

    private CourseStep step(int index, String title, String desc, int duration) {
        CourseStep s = new CourseStep();
        s.setIndex(index);
        s.setTitle(title);
        s.setDesc(desc);
        s.setDuration(duration);
        return s;
    }

    private void addMaterials(Long courseId, String title, int plans, int slides, int toolkits, int videos) {
        addRow(courseId, "plan", "《" + title + "》教案", plans);
        addRow(courseId, "slides", "《" + title + "》课件", slides);
        addRow(courseId, "toolkit", "《" + title + "》教具集", toolkits);
        addRow(courseId, "video", "《" + title + "》视频", videos);
    }

    private void addRow(Long courseId, String type, String name, int count) {
        for (int i = 1; i <= count; i++) {
            CourseMaterial m = new CourseMaterial();
            m.setCourseId(courseId);
            m.setType(type);
            m.setName(count == 1 ? name : name + " · 第 " + i + " 集");
            if ("video".equals(type)) {
                m.setSize("≈ 8 分钟");
            }
            m.setCreatedAt(LocalDateTime.now());
            materialMapper.insert(m);
        }
    }
}
